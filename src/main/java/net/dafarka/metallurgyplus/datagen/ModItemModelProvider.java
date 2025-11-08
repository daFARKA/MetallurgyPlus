package net.dafarka.metallurgyplus.datagen;

import net.dafarka.metallurgyplus.MetallurgyPlus;
import net.dafarka.metallurgyplus.block.ModBlocks;
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

        simpleItem(ModItems.CLAY_MINERAL_RAW);
        simpleItem(ModItems.KAOLINITE);
        simpleItem(ModItems.PLATINUM_LIKE_METALS);

        simpleItem(ModItems.SILICON);
        simpleItem(ModItems.SULPHUR);
        simpleItem(ModItems.RARE_EARTH1);
        simpleItem(ModItems.RARE_EARTH2);
        simpleItem(ModItems.RARE_EARTH3);
        simpleItem(ModItems.SMALL_RARE_EARTH);
        simpleItem(ModItems.STONE_DUST);

        simpleItem(ModItems.LLAMKANA);

        simpleBlockItemModel("alloy_smelter");
        simpleBlockItemModel("ore_processing_unit");
        simpleBlockItemModel("power_source");
        simpleBlockItemModel("battery");

        createCableBlockEntityItems();
        createSolarPanelBlockEntityItems();
    }

    private ItemModelBuilder simpleItem(RegistryObject<Item> item) {
        return withExistingParent(item.getId().getPath(),
            new ResourceLocation("item/generated")).texture("layer0",
            new ResourceLocation(MetallurgyPlus.MODID,"item/" + item.getId().getPath()));
    }

    private ItemModelBuilder simpleBaseItem(RegistryObject<Item> item, String componentName) {
        return withExistingParent(item.getId().getPath(),
            new ResourceLocation("item/generated")).texture("layer0",
            new ResourceLocation(MetallurgyPlus.MODID,"item/base_" + componentName));
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

    private void createCableBlockEntityItems() {
        for (RegistryObject<CableBlock> block : ModBlocks.CABLE_BLOCKS_MAP.values()) {
            String name = block.get().getDescriptionId().split("\\.")[2];
            simpleBlockItemModel(name);
        }
    }

    private void createSolarPanelBlockEntityItems() {
        for (RegistryObject<SolarPanelBlock> block : ModBlocks.SOLAR_PANEL_BLOCK_MAP.values()) {
            String name = block.get().getDescriptionId().split("\\.")[2];
            simpleBlockItemModel(name);
        }
    }
}
