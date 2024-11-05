package net.dafarka.metallurgyplus.datagen;

import net.dafarka.metallurgyplus.MetallurgyPlus;
import net.dafarka.metallurgyplus.block.ModBlocks;
import net.dafarka.metallurgyplus.item.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.registries.RegistryObject;

import java.util.Map;

public class ModLangProvider extends LanguageProvider {

    public ModLangProvider(PackOutput output, String locale) {
        super(output, MetallurgyPlus.MODID, locale);
    }

    @Override
    protected void addTranslations() {
        add("item.metallurgyplus.clay_mineral_raw", "Clay Mineral");
        add("item.metallurgyplus.kaolinite", "Kaolinite");
        add("item.metallurgyplus.platinum_like_metals", "Platinum Like Metals");

        add("item.metallurgyplus.silicon", "Silicon");
        add("item.metallurgyplus.sulphur", "Sulphur");
        add("item.metallurgyplus.rare_earth1", "Rare Earth I");
        add("item.metallurgyplus.rare_earth2", "Rare Earth II");
        add("item.metallurgyplus.rare_earth3", "Rare Earth III");
        add("item.metallurgyplus.small_rare_earth", "Small Pile of Rare Earth");
        add("item.metallurgyplus.stone_dust", "Stone Dust");

        add("block.metallurgyplus.clay_mineral", "Clay Mineral");

        add("block.metallurgyplus.ore_processing_unit", "Ore Processing Unit");
        add("block.metallurgyplus.alloy_smelter", "Alloy Smelter");

        add("creativetab.metallurgyplus_tab", "MetallurgyPlus");

        addMapsTranslations(ModItems.MATERIAL_MAP, ModBlocks.MATERIAL_BLOCKS_MAP);
        addOreTranslations();
        addMapsTranslations(ModItems.ALLOY_MAP, ModBlocks.ALLOY_BLOCKS_MAP);
    }

    private void addMapsTranslations(Map<String, RegistryObject<Item>> itemMap, Map<String, RegistryObject<Block>> blockMap) {
        for (RegistryObject<Item> item : itemMap.values()) {
            String fullName = item.getId().getPath();
            String name = fullName.split("_")[0] + " " + fullName.split("_")[1];
            add("item." + MetallurgyPlus.MODID + "." + fullName, capitalizeFirstLetterEach(name));
        }

        for (RegistryObject<Block> block : blockMap.values()) {
            String fullName = block.getId().getPath();
            String name = fullName.split("_")[0] + " " + fullName.split("_")[1];
            add("block." + MetallurgyPlus.MODID + "." + fullName, capitalizeFirstLetterEach(name));
        }
    }

    private void addOreTranslations() {
        for (RegistryObject<Item> item : ModItems.ORE_MAP.values()) {
            String fullName = item.getId().getPath();
            String name = fullName.split("_")[0] + " " + fullName.split("_")[1];
            add("item." + MetallurgyPlus.MODID + "." + fullName, capitalizeFirstLetterEach(name));
        }

        for (RegistryObject<Block> block : ModBlocks.ORE_BLOCKS_MAP.values()) {
            String fullName = block.getId().getPath();
            String name = fullName.split("_")[0] + " Ore";
            for (int i = 1; i < ModItems.ORE_BASE_NAME.length; i++) {
                if (fullName.split("_")[1].equals(ModItems.ORE_BASE_NAME[i])) {
                    name = fullName.split("_")[0] + " " + ModItems.ORE_BASE_NAME[i] + " Ore";
                }
            }
            add("block." + MetallurgyPlus.MODID + "." + fullName, capitalizeFirstLetterEach(name));
        }
    }

    public String capitalizeFirstLetterEach(String input) {
        String[] parts = input.split(" ");
        StringBuilder capitalized = new StringBuilder();

        int i = 0;
        for (String part : parts) {
            capitalized.append(part.substring(0, 1).toUpperCase());
            capitalized.append(part.substring(1));
            if (i < parts.length - 1) {
                capitalized.append(" ");
            }
            i++;
        }

        return capitalized.toString();
    }
}