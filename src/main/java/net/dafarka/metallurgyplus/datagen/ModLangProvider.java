package net.dafarka.metallurgyplus.datagen;

import net.dafarka.metallurgyplus.MetallurgyPlus;
import net.dafarka.metallurgyplus.block.ModBlocks;
import net.dafarka.metallurgyplus.item.ModItems;
import net.dafarka.metallurgyplus.util.Utility;
import net.minecraft.data.PackOutput;
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

        add("block.metallurgyplus.machine_frame", "Machine Frame");

        add("block.metallurgyplus.ore_processing_unit", "Ore Processing Unit");
        add("block.metallurgyplus.alloy_smelter", "Alloy Smelter");
        add("block.metallurgyplus.grinder", "Grinder");
        add("block.metallurgyplus.press", "Metal Press");
        add("block.metallurgyplus.extractor", "Extractor");
        add("block.metallurgyplus.gemstone_cutter", "Gemstone Cutter");
        add("block.metallurgyplus.quarry", "Quarry");
        add("block.metallurgyplus.power_source", "Creative Power Source");
        add("block.metallurgyplus.sack_station", "Sack Docking Station");

        add("message.metallurgyplus.multiblock.formed", "Multiblock formed");

        addCreativeTabsTranslations();

        add("tooltip.metallurgyplus.common", "Y-Level: 80 to -64");
        add("tooltip.metallurgyplus.uncommon", "Y-Level: 50 to -64");
        add("tooltip.metallurgyplus.rare", "Y-Level: 20 to -64");
        add("tooltip.metallurgyplus.very_rare", "Y-Level: -10 to -64");
        add("tooltip.metallurgyplus.extremely_rare", "Y-Level: -30 to -64");

        add("tooltip.metallurgyplus.battery_cell_capacity", "Adds %s FE to battery capacity");

        add("block." + MetallurgyPlus.MODID + ".battery", "Battery");
        addTieredBlockTranslations(ModBlocks.BATTERY_BLOCK_MAP, "Battery");
        addTieredBlockTranslations(ModBlocks.CABLE_BLOCKS_MAP);
        addTieredBlockTranslations(ModBlocks.SOLAR_PANEL_BLOCK_MAP, "Solar Panel");


        addMapsTranslations(ModItems.MATERIAL_MAP, ModBlocks.MATERIAL_BLOCKS_MAP);
        addOreTranslations();
        addMapsTranslations(ModItems.ALLOY_MAP, ModBlocks.ALLOY_BLOCKS_MAP);
        addBlockMapTranslations(ModBlocks.CUSTOM_BLOCKS_MAP);
        addCustomItemMapTranslations(ModItems.CUSTOM_ITEM_MAP);
        addCustomItemMapTranslations(ModItems.VANILLA_MAP);
        addBaseItemMapTranslations(ModItems.GEM_MAP);
        addBlockMapTranslations(ModBlocks.GEM_BLOCKS_MAP);
        addTieredItemTranslations(ModItems.COIL_MAP);
        addTieredItemTranslations(ModItems.SACK_MAP);
        addMultiBlockTranslations();
    }

    private void addCreativeTabsTranslations() {
        add("creativetab.metallurgyplus_tab", "MetallurgyPlus");
        add("creativetab.metallurgyplus_items", "MetallurgyPlus Items");
        add("creativetab.metallurgyplus_materials", "MetallurgyPlus Materials");
        add("creativetab.metallurgyplus_ores", "MetallurgyPlus Ores");
        add("creativetab.metallurgyplus_alloys", "MetallurgyPlus Alloys");
        add("creativetab.metallurgyplus_vanilla_items", "MetallurgyPlus Vanilla Items");
        add("creativetab.metallurgyplus_gems", "MetallurgyPlus Gemstones");
        add("creativetab.metallurgyplus_machines", "MetallurgyPlus Machines");
        add("creativetab.metallurgyplus_multiblocks", "MetallurgyPlus Multiblock Parts");
    }

    private void addMapsTranslations(Map<String, RegistryObject<Item>> itemMap, Map<String, RegistryObject<Block>> blockMap) {
        addItemMapTranslations(itemMap);
        addBlockMapTranslations(blockMap);
    }

    /**
     * name_component -> Name Component
     */
    private void addBlockMapTranslations(Map<String, RegistryObject<Block>> blockMap) {
        for (RegistryObject<Block> block : blockMap.values()) {
            String fullName = block.getId().getPath();
            String name = getName(fullName);
            add("block." + MetallurgyPlus.MODID + "." + fullName, capitalizeFirstLetterEach(name));
        }
    }

    /**
     * name_component -> Name Component
     */
    private void addItemMapTranslations(Map<String, RegistryObject<Item>> itemMap) {
        for (RegistryObject<Item> item : itemMap.values()) {
            String fullName = item.getId().getPath();
            String name = getName(fullName);
            add("item." + MetallurgyPlus.MODID + "." + fullName, capitalizeFirstLetterEach(name));
        }
    }

    /**
     * name_component -> Name Component
     */
    private void addCustomItemMapTranslations(Map<String, ? extends RegistryObject<? extends Item>> itemMap) {
        for (RegistryObject<? extends Item> item : itemMap.values()) {
            String fullName = item.getId().getPath();
            String name = fullName.replace('_', ' ');
            add("item." + MetallurgyPlus.MODID + "." + fullName, capitalizeFirstLetterEach(name));
        }
    }

    /**
     * first-last_component -> First Last
     */
    private void addBaseItemMapTranslations(Map<String, RegistryObject<Item>> itemMap) {
        for (RegistryObject<Item> item : itemMap.values()) {
            String fullName = item.getId().getPath();
            String name = fullName.split("_")[0].replace("-", " ");
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

    private <T extends Block> void addTieredBlockTranslations(Map<?, ? extends RegistryObject<T>> blockMap, String displayNamePrefix) {
        for (RegistryObject<T> entry : blockMap.values()) {
            int tier = Utility.getTier(entry.getId().getPath());
            add(entry.get(), displayNamePrefix + " Tier " + tier);
        }
    }

    private <T extends Block> void addTieredBlockTranslations(Map<?, ? extends RegistryObject<T>> blockMap) {
        for (RegistryObject<T> entry : blockMap.values()) {
            String path = entry.getId().getPath();
            int tier = Utility.getTier(path);
            add(entry.get(), capitalizeFirstLetterEach(getName(Utility.removeTrailingDigits(Utility.removeInString(path, "_block")))) + " Tier " + tier);
        }
    }

    private void addMultiBlockTranslations() {
        for (RegistryObject<? extends Block> block : ModBlocks.MULTIBLOCKS_MAP.values()) {
            String[] parts = Utility.getMultiBlockNames(block);

            String multiblock = parts[0];
            String rawBlockName = parts[1];

            Integer tier = Utility.getTier(rawBlockName);

            String cleanedBlockName = rawBlockName
                .replaceAll("\\d+", "")
                .replace("_block", "")
                .replace("_", " ")
                .trim();

            String cleanedMultiblock = multiblock.replace("_", " ").trim();

            String name = capitalizeFirstLetterEach(cleanedMultiblock + " " + getName(cleanedBlockName));

            if (tier > 0) {
                name += " Tier " + tier;
            }

            add(block.get(), name);
        }
    }

    /**
     * name_component -> Name Component Tier x
     */
    private void addTieredItemTranslations(Map<Integer, RegistryObject<Item>> itemMap) {
        for (RegistryObject<Item> item : itemMap.values()) {
            String fullName = item.getId().getPath();
            String name = item.get().getDescriptionId().split("\\.")[2];
            int tier = Utility.getTier(name);

            add("item." + MetallurgyPlus.MODID + "." + fullName, capitalizeFirstLetterEach(name.split("_")[0].replaceAll("\\d+$", "")) + " Tier " + tier);
        }
    }

    /**
     * Capitalizes the first letter of every word in the provided string.
     * The strings need to be separated by a space.
     *
     * @param input string whose words should be capitalized
     *
     * @return the input string with the first letter of each word capitalized
     */
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

    /**
     * Formats the name of an entity.
     *
     * @param fullName The full name of the entity, of the following form:
     *                 name<-tag><_component>, where -tag and _component are optional.
     *                 There can even be more tags, e.g: name<-tag1-tag2-tag3><_component>
     *
     * @return a nicely formatted name, such as: Name Tag Component
     */
    private String getName(String fullName) {
        String[] parts = {fullName};

        if (fullName.contains("_")) {
            parts = fullName.split("_");
        }

        String name = parts[0];
        String component = "";
        if (parts.length == 2) {
            component = parts[1];
            name = parts[0] + " " + component;
        }
        if (parts[0].split("-").length > 1) {
            String[] _parts = parts[0].split("-");
            if (_parts[0].equals("titanium") && _parts.length > 2) {
                return getTitaniumAlloyName(_parts) + " " + component;
            }

            StringBuilder stringBuilder = new StringBuilder();
            for (String text : _parts) {
                stringBuilder.append(text);
                stringBuilder.append(" ");
            }
            stringBuilder.append(component);
            name = stringBuilder.toString();
        }
        return name;
    }

    /**
     * Titanium Alloys have very industrialized names and always look like this: Titanium-xEl1-xEl1-...
     * <br>
     * x is a number and El1 the first element (that is not Titanium) and so on.
     *
     * @param parts The parts of the name that is formatted like this: titanium-xel1-xel2-....
     *
     * @return A well formatted name typically looking like this: Titanium-xEl1-xEl2-...
     */
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
