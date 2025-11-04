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

import java.util.Arrays;
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
        add("block.metallurgyplus.power_source", "Power Source");

        add("creativetab.metallurgyplus_tab", "MetallurgyPlus");

        addMapsTranslations(ModItems.MATERIAL_MAP, ModBlocks.MATERIAL_BLOCKS_MAP);
        addOreTranslations();
        addMapsTranslations(ModItems.ALLOY_MAP, ModBlocks.ALLOY_BLOCKS_MAP);
    }

    private void addMapsTranslations(Map<String, RegistryObject<Item>> itemMap, Map<String, RegistryObject<Block>> blockMap) {
        for (RegistryObject<Item> item : itemMap.values()) {
            String fullName = item.getId().getPath();
            String name = getName(fullName);
            add("item." + MetallurgyPlus.MODID + "." + fullName, capitalizeFirstLetterEach(name));
        }

        for (RegistryObject<Block> block : blockMap.values()) {
            String fullName = block.getId().getPath();
            String name = getName(fullName);
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

    private String getName(String fullName) {
        String[] parts = fullName.split("_");

        String name = parts[0] + " " + parts[1];
        if (parts[0].split("-").length > 1) {
            String[] _parts = parts[0].split("-");
            if (_parts[0].equals("titanium") && _parts.length > 2) {
                return getTitaniumAlloyName(_parts) + " " + parts[1];
            }

            StringBuilder stringBuilder = new StringBuilder();
            for (String text : _parts) {
                stringBuilder.append(text);
                stringBuilder.append(" ");
            }
            stringBuilder.append(parts[1]);
            name = stringBuilder.toString();
        }
        return name;
    }

    /**
     * Titanium Alloys have very industrialized names and always look like this: Titanium-xEl1-xEl1-...
     * <br>
     * x stands for a number here and El1 for the first element (that is not Titanium) and so on.
     *
     * @param parts The parts of the name that is formatted like this: titanium-xel1-xel2-....
     *
     * @return A well formatted name typically looking like this: Titanium-xEl1-xEl2-...
     *
     * */
    private String getTitaniumAlloyName(String[] parts) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Titanium");
        String[] components = Arrays.copyOfRange(parts, 1, parts.length);
        for (String component : components) {
            stringBuilder.append("-");
            int i = 0;
            while (i < component.length() && Character.isDigit(component.charAt(i))) {
                i++;
            }

            if (i < component.length()) {
                stringBuilder.append(component, 0, i); // Add the numeric part
                stringBuilder.append(Character.toUpperCase(component.charAt(i))); // Capitalize the first letter of element
                stringBuilder.append(component.substring(i + 1)); // Append the rest of the element symbol
            } else {
                stringBuilder.append(component);
            }

        }
        return stringBuilder.toString();
    }
}