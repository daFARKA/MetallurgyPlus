package net.dafarka.metallurgyplus.datagen;

import net.dafarka.metallurgyplus.MetallurgyPlus;
import net.dafarka.metallurgyplus.block.ModBlocks;
import net.dafarka.metallurgyplus.block.custom.BatteryBlock;
import net.dafarka.metallurgyplus.block.custom.CableBlock;
import net.dafarka.metallurgyplus.block.custom.SolarPanelBlock;
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
        add("item.metallurgyplus.llamkana", "Llamkana");

        add("block.metallurgyplus.clay_mineral", "Clay Mineral");

        add("block.metallurgyplus.ore_processing_unit", "Ore Processing Unit");
        add("block.metallurgyplus.alloy_smelter", "Alloy Smelter");
        add("block.metallurgyplus.grinder", "Grinder");
        add("block.metallurgyplus.press", "Metal Press");
        add("block.metallurgyplus.quarry", "Quarry");
        add("block.metallurgyplus.power_source", "Creative Power Source");
        add("block.metallurgyplus.battery", "Battery");

        add("creativetab.metallurgyplus_tab", "MetallurgyPlus");

        add("tooltip.metallurgyplus.common", "Y-Level: 80 to -64");
        add("tooltip.metallurgyplus.uncommon", "Y-Level: 50 to -64");
        add("tooltip.metallurgyplus.rare", "Y-Level: 20 to -64");
        add("tooltip.metallurgyplus.very_rare", "Y-Level: -10 to -64");
        add("tooltip.metallurgyplus.extremely_rare", "Y-Level: -30 to -64");

        addMapsTranslations(ModItems.MATERIAL_MAP, ModBlocks.MATERIAL_BLOCKS_MAP);
        addOreTranslations();
        addMapsTranslations(ModItems.ALLOY_MAP, ModBlocks.ALLOY_BLOCKS_MAP);
        addCableTranslations();
        addSolarPanelTranslations();
        addBatteryTranslations();
        addBlockMapTranslations(ModBlocks.CUSTOM_BLOCKS_MAP);
        addCustomItemMapTranslations(ModItems.CUSTOM_ITEM_MAP);
        addCustomItemMapTranslations(ModItems.VANILLA_MAP);
        addTieredItemTranslations(ModItems.COIL_MAP);
    }

    private void addMapsTranslations(Map<String, RegistryObject<Item>> itemMap, Map<String, RegistryObject<Block>> blockMap) {
        addItemMapTranslations(itemMap);
        addBlockMapTranslations(blockMap);
    }

    private void addBlockMapTranslations(Map<String, RegistryObject<Block>> blockMap) {
        for (RegistryObject<Block> block : blockMap.values()) {
            String fullName = block.getId().getPath();
            String name = getName(fullName);
            add("block." + MetallurgyPlus.MODID + "." + fullName, capitalizeFirstLetterEach(name));
        }
    }

    private void addItemMapTranslations(Map<String, RegistryObject<Item>> itemMap) {
        for (RegistryObject<Item> item : itemMap.values()) {
            String fullName = item.getId().getPath();
            String name = getName(fullName);
            add("item." + MetallurgyPlus.MODID + "." + fullName, capitalizeFirstLetterEach(name));
        }
    }

    private void addCustomItemMapTranslations(Map<String, RegistryObject<Item>> itemMap) {
        for (RegistryObject<Item> item : itemMap.values()) {
            String fullName = item.getId().getPath();
            String name = fullName.replace('_', ' ');
            add("item." + MetallurgyPlus.MODID + "." + fullName, capitalizeFirstLetterEach(name));
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

    private void addCableTranslations() {
        for (RegistryObject<CableBlock> cable : ModBlocks.CABLE_BLOCKS_MAP.values()) {
            String fullName = cable.getId().getPath();
            String name = cable.get().getDescriptionId().split("\\.")[2];
            int tier = Integer.parseInt(name.replaceAll("\\D+", ""));

            add("block." + MetallurgyPlus.MODID + "." + fullName, "Cable Tier " + tier);
        }
    }

    private void addSolarPanelTranslations() {
        for (RegistryObject<SolarPanelBlock> panel : ModBlocks.SOLAR_PANEL_BLOCK_MAP.values()) {
            String fullName = panel.getId().getPath();
            String name = panel.get().getDescriptionId().split("\\.")[2];
            int tier = Integer.parseInt(name.replaceAll("\\D+", ""));

            add("block." + MetallurgyPlus.MODID + "." + fullName, "Solar Panel Tier " + tier);
        }
    }

    private void addBatteryTranslations() {
        for (RegistryObject<BatteryBlock> battery : ModBlocks.BATTERY_BLOCK_MAP.values()) {
            String fullName = battery.getId().getPath();
            String name = battery.get().getDescriptionId().split("\\.")[2];
            int tier = Integer.parseInt(name.replaceAll("\\D+", ""));

            add("block." + MetallurgyPlus.MODID + "." + fullName, "Battery Tier " + tier);
        }
    }

    private void addTieredItemTranslations(Map<Integer, RegistryObject<Item>> itemMap) {
        for (RegistryObject<Item> item : itemMap.values()) {
            String fullName = item.getId().getPath();
            String name = item.get().getDescriptionId().split("\\.")[2];
            int tier = Integer.parseInt(name.replaceAll("\\D+", ""));

            add("item." + MetallurgyPlus.MODID + "." + fullName, capitalizeFirstLetterEach(name.split("_")[0].replaceAll("\\d+$", "")) + " Tier " + tier);
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