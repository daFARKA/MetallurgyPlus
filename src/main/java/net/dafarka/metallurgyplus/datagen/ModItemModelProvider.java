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
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

import java.util.Map;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, MetallurgyPlus.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        generateMaps(ModItems.MATERIAL_MAP, ModBlocks.MATERIAL_BLOCKS_MAP);
        generateMaps(ModItems.ORE_MAP, ModBlocks.ORE_BLOCKS_MAP);
        generateMaps(ModItems.ALLOY_MAP, ModBlocks.ALLOY_BLOCKS_MAP);
        generateItemMap(ModItems.CUSTOM_ITEM_MAP);
        generateItemMapBase(ModItems.VANILLA_MAP);

        simpleItem(ModItems.LLAMKANA);

        simpleBlockItemModel("alloy_smelter");
        simpleBlockItemModel("ore_processing_unit");
        simpleBlockItemModel("grinder");
        simpleBlockItemModel("press");
        simpleBlockItemModel("extractor");
        simpleBlockItemModel("quarry");
        simpleBlockItemModel("power_source");
        simpleBlockItemModel("sack_station");

        createCableBlockItems();
        createSolarPanelBlockItems();
        createBatteryBlockItems();
        createCoilItems();
        createSackItems();
    }

    private ItemModelBuilder simpleItem(RegistryObject<Item> item) {
        return withExistingParent(item.getId().getPath(),
            new ResourceLocation("item/generated")).texture("layer0",
            new ResourceLocation(MetallurgyPlus.MODID, "item/" + item.getId().getPath()));
    }

    private ItemModelBuilder simpleBaseItem(RegistryObject<Item> item, String componentName) {
        return withExistingParent(item.getId().getPath(),
            new ResourceLocation("item/generated")).texture("layer0",
            new ResourceLocation(MetallurgyPlus.MODID, "item/base_" + componentName));
    }

    public void simpleBlockItemModel(String modelName) {
        getBuilder(modelName)
            .parent(new ModelFile.UncheckedModelFile(modLoc("block/" + modelName)))
            .transforms()
            .transform(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND)
            .rotation(10, -45, 170)
            .translation(0, 1.5f, -2.75f)
            .scale(0.375f, 0.375f, 0.375f)
            .end();
    }


    private void generateMaps(Map<String, RegistryObject<Item>> itemMap, Map<String, RegistryObject<Block>> blockMap) {
        for (RegistryObject<Item> item : itemMap.values()) {
            String currentName = item.getId().getPath();
            String currentComponentName = currentName.split("_")[1];
            simpleBaseItem(item, currentComponentName);
        }

        for (RegistryObject<Block> block : blockMap.values()) {
            String blockName = block.get().getDescriptionId().split("\\.")[2];
            simpleBlockItemModel(blockName);
        }
    }

    private void generateItemMap(Map<String, RegistryObject<Item>> itemMap) {
        for (RegistryObject<Item> item : itemMap.values()) {
            simpleItem(item);
        }
    }

    private void generateItemMapBase(Map<String, RegistryObject<Item>> itemMap) {
        for (RegistryObject<Item> item : itemMap.values()) {
            String currentName = item.getId().getPath();
            String currentComponentName = currentName.split("_")[1];
            simpleBaseItem(item, currentComponentName);
        }
    }

    private void createCableBlockItems() {
        for (RegistryObject<CableBlock> block : ModBlocks.CABLE_BLOCKS_MAP.values()) {
            String name = block.get().getDescriptionId().split("\\.")[2];
            simpleBlockItemModel(name);
        }
    }

    private void createSolarPanelBlockItems() {
        for (RegistryObject<SolarPanelBlock> block : ModBlocks.SOLAR_PANEL_BLOCK_MAP.values()) {
            String name = block.get().getDescriptionId().split("\\.")[2];
            simpleBlockItemModel(name);
        }
    }

    private void createBatteryBlockItems() {
        for (RegistryObject<BatteryBlock> block : ModBlocks.BATTERY_BLOCK_MAP.values()) {
            String name = block.get().getDescriptionId().split("\\.")[2];
            simpleBlockItemModel(name);
        }
    }

    public void createCoilItems() {
        for (RegistryObject<Item> item : ModItems.COIL_MAP.values()) {
            getBuilder(item.getId().getPath())
                .parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", modLoc("item/base_coil"))
                .texture("layer1", modLoc("item/base_coil_spindle"));
        }
    }

    public void createSackItems() {
        for (RegistryObject<Item> item : ModItems.SACK_MAP.values()) {
            getBuilder(item.getId().getPath())
                .parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", modLoc("item/base_sack"))
                .texture("layer1", modLoc("item/base_sack_hole"));
        }
    }
}
