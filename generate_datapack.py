#!/usr/bin/env python3
"""
generate_datapack.py

Usage:
    python generate_datapack.py resources.md datapack-output

Generates a datapack using EXACTLY this structure:

assets/metallurgyplus/lang/en_us.json
assets/metallurgyplus/tinkering/materials<slug>.json

data/metallurgyplus/recipes/<slug>/material/<slug>.json
data/metallurgyplus/tinkering/materials/definition/<slug>.json
data/metallurgyplus/tinkering/materials/stats/<slug>.json
data/metallurgyplus/tinkering/materials/traits/<slug>.json
"""

import os
import shutil
import sys
import re
import json
from pathlib import Path

# ---------------------------------------------------------
# FIXED: Slug uses hyphens ("aluminum scandium" → "aluminum-scandium")
# ---------------------------------------------------------
def slugify(name: str) -> str:
    name = name.lower().strip()
    # convert spaces and "/" to hyphens
    name = re.sub(r"[ \/]+", "-", name)
    # allow a-z 0-9 and hyphens
    name = re.sub(r"[^a-z0-9\-]", "", name)
    return name


def capitalize_display(name: str) -> str:
    return " ".join(w.capitalize() for w in name.split())


def parse_item_line(line: str):
    line = line.strip()
    if not line.startswith("*"):
        return None

    content = line.lstrip("*").strip()
    parts = [p.strip() for p in content.split("-")]

    if len(parts) < 2:
        return None

    name = parts[0]
    color = parts[1]
    rarity = parts[2] if len(parts) > 2 else None

    color = color.lower()
    if color.startswith("0x"):
        color = color[2:]
    if color.startswith("#"):
        color = color[1:]

    color = re.sub(r"[^0-9a-f]", "", color).upper()

    if len(color) < 6:
        color = color.rjust(6, "0")
    if len(color) > 6:
        color = color[-6:]

    return name, color, rarity


# JSON templates
ASSETS_MATERIAL_TEMPLATE = {
  "fallbacks": ["rock"],
  "color": None,
  "luminosity": 6,
  "generator": {
    "transformer": {
      "type": "tconstruct:recolor_sprite",
      "color_mapping": {
        "type": "tconstruct:grey_to_color",
        "palette": [
          { "grey": 0,   "color": "FF000000" },
          { "grey": 63,  "color": "FF240000" },
          { "grey": 102, "color": "FF350000" },
          { "grey": 140, "color": "FF450000" },
          { "grey": 178, "color": "FF5D0000" },
          { "grey": 216, "color": "FF680F00" },
          { "grey": 255, "color": "FF771200" }
        ]
      }
    },
    "supported_stats": [
      "tconstruct:head",
      "tconstruct:handle",
      "tconstruct:binding",
      "tconstruct:repair_kit",
      "tconstruct:armor_plating",
      "tconstruct:plating_boots",
      "tconstruct:plating_leggings",
      "tconstruct:plating_chestplate",
      "tconstruct:plating_helmet",
      "tconstruct:plating_shield",
      "tconstruct:maille",
      "tconstruct:armor_maille"
    ],
    "ignore_material_stats": False
  }
}

def recipe_template(slug):
    return {
      "type": "tconstruct:material",
      "ingredient": {
        "item": f"metallurgyplus:{slug}_ingot"
      },
      "value": 1,
      "material": f"metallurgyplus:{slug}"
    }

DEFINITION_TEMPLATE = {
  "craftable": True,
  "tier": 4,
  "sortOrder": 0,
  "hidden": False
}

STATS_TEMPLATE = {
  "stats": {
    "tconstruct:head": {
      "durability": 100,
      "mining_speed": 1.0,
      "melee_attack": 1.0,
      "mining_tier": "minecraft:netherite"
    },
    "tconstruct:handle": {
      "durability": 0.0,
      "mining_speed": 0.0,
      "melee_damage": 0.0,
      "melee_speed": 0.0
    },
    "tconstruct:binding": {}
  }
}

TRAITS_TEMPLATE = {
  "default": []
}

PACK_MC_META = {
  "pack": {
    "pack_format": 15,
    "description": "tc-materials-metallurgyplus"
  }
}


def generate_from_markdown(md_path: Path, out_path: Path):
    if out_path.exists():
        shutil.rmtree(out_path)

    text = md_path.read_text(encoding="utf-8")
    lines = text.splitlines()

    current_section = None
    items = []

    for line in lines:
        s = line.strip()
        if s.startswith("## "):
            head = s[3:].lower()
            if head.startswith("materials"):
                current_section = "materials"
            elif head.startswith("alloys"):
                current_section = "alloys"
            else:
                current_section = None
            continue

        if current_section in ("materials", "alloys"):
            if s.startswith("*"):
                parsed = parse_item_line(s)
                if parsed:
                    name, color, rarity = parsed
                    slug = slugify(name)  # UPDATED
                    items.append((name, color, slug))

    # Prepare directories
    (out_path).mkdir(parents=True, exist_ok=True)
    (out_path / "pack.mcmeta").write_text(json.dumps(PACK_MC_META, indent=2))

    assets_lang = out_path / "assets" / "metallurgyplus" / "lang"
    assets_mat = out_path / "assets" / "metallurgyplus" / "tinkering" / "materials"
    assets_lang.mkdir(parents=True, exist_ok=True)
    assets_mat.mkdir(parents=True, exist_ok=True)

    data_base = out_path / "data" / "metallurgyplus"
    recipes_base = data_base / "recipes"
    mats_base = data_base / "tinkering" / "materials"
    (mats_base / "definition").mkdir(parents=True, exist_ok=True)
    (mats_base / "stats").mkdir(parents=True, exist_ok=True)
    (mats_base / "traits").mkdir(parents=True, exist_ok=True)

    # lang file
    lang_map = {}

    for name, color, slug in items:
        display = capitalize_display(name)
        lang_map[f"material.metallurgyplus.{slug}"] = display

        # assets materials
        mat_json = json.loads(json.dumps(ASSETS_MATERIAL_TEMPLATE))
        mat_json["color"] = color
        (assets_mat / f"{slug}.json").write_text(json.dumps(mat_json, indent=2))

        # recipes
        recipe_dir = recipes_base / slug / "material"
        recipe_dir.mkdir(parents=True, exist_ok=True)
        (recipe_dir / f"{slug}.json").write_text(
            json.dumps(recipe_template(slug), indent=2)
        )

        # definition
        (mats_base / "definition" / f"{slug}.json").write_text(
            json.dumps(DEFINITION_TEMPLATE, indent=2)
        )

        # stats
        (mats_base / "stats" / f"{slug}.json").write_text(
            json.dumps(STATS_TEMPLATE, indent=2)
        )

        # traits
        (mats_base / "traits" / f"{slug}.json").write_text(
            json.dumps(TRAITS_TEMPLATE, indent=2)
        )

    # write lang file last
    (assets_lang / "en_us.json").write_text(
        json.dumps(lang_map, indent=2, ensure_ascii=False)
    )


def main():
    if len(sys.argv) != 3:
        print("Usage: python generate_datapack.py markdown.md output_folder")
        return

    md = Path(sys.argv[1])
    out = Path(sys.argv[2])

    if not md.exists():
        print("Markdown file not found:", md)
        return

    generate_from_markdown(md, out)
    print("Datapack generated at:", out)


if __name__ == "__main__":
    main()
