package net.dafarka.metallurgyplus.datagen;

import net.dafarka.metallurgyplus.MetallurgyPlus;
import net.dafarka.metallurgyplus.block.ModBlocks;
import net.dafarka.metallurgyplus.item.ModItems;
import net.dafarka.metallurgyplus.util.Utility;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.ModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, MetallurgyPlus.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        generateMaps(ModItems.MATERIAL_MAP, ModBlocks.MATERIAL_BLOCKS_MAP, "material");
        generateMaps(ModItems.ORE_MAP, ModBlocks.ORE_BLOCKS_MAP, "ore", "cutout");
        generateMaps(ModItems.ALLOY_MAP, ModBlocks.ALLOY_BLOCKS_MAP, "alloy");
        generateItemMap(ModItems.CUSTOM_ITEM_MAP, "custom");
        generateItemMapBase(ModItems.VANILLA_MAP);
        generateMaps(ModItems.GEM_MAP, ModBlocks.GEM_BLOCKS_MAP, "gem");

        simpleBlockItemModel("machine_frame", "machine_frame", "cutout");

        groupItem(ModItems.LLAMKANA, "custom");

        simpleBlockItemModel("alloy_smelter");
        simpleBlockItemModel("ore_processing_unit");
        simpleBlockItemModel("grinder");
        simpleBlockItemModel("press");
        simpleBlockItemModel("extractor");
        simpleBlockItemModel("gemstone_cutter");
        simpleBlockItemModel("quarry");
        simpleBlockItemModel("power_source");
        simpleBlockItemModel("sack_station", "sack_station", "cutout");

        simpleBlockItemModels(ModBlocks.CABLE_BLOCKS_MAP.values());
        simpleBlockItemModels(ModBlocks.SOLAR_PANEL_BLOCK_MAP.values(), "cutout");
        simpleBlockItemModels(ModBlocks.BATTERY_BLOCK_MAP.values());

        createCoilItems();
        createSackItems();

        multiBlockItemModels();
    }

    private ItemModelBuilder groupItem(RegistryObject<? extends Item> item, String group) {
        return withExistingParent(item.getId().getPath(),
            new ResourceLocation("item/generated")).texture("layer0",
            new ResourceLocation(MetallurgyPlus.MODID, "item/" + group + "/" + item.getId().getPath()));
    }

    private ItemModelBuilder baseItem(RegistryObject<? extends Item> item) {
        String currentName = item.getId().getPath();
        String componentName = currentName.split("_")[1];

        return withExistingParent(item.getId().getPath(),
            new ResourceLocation("item/generated")).texture("layer0",
            new ResourceLocation(MetallurgyPlus.MODID, "item/base/" + componentName));
    }

    private ItemModelBuilder dynamicItem(RegistryObject<? extends Item> item, @Nullable String group) {
        if (group == null) return baseItem(item);

        String texturePath = "item/" + (group.isEmpty() ? "" : group + "/") + item.getId().getPath();
        ResourceLocation customTexture = new ResourceLocation(MetallurgyPlus.MODID, texturePath);

        if (existingFileHelper.exists(customTexture, ModelProvider.TEXTURE)) {
            return groupItem(item, group);
        } else {
            return baseItem(item);
        }
    }

    public void simpleBlockItemModel(String modelName) {
        simpleBlockItemModel(modelName, modelName, null);
    }

    public void simpleBlockItemModel(String modelName, String parentBlockPath) {
        simpleBlockItemModel(modelName, parentBlockPath, null);
    }

    public void simpleBlockItemModel(String modelName, String parentBlockPath, @Nullable String renderType) {
        var builder = getBuilder(modelName)
            .parent(new ModelFile.UncheckedModelFile(modLoc("block/" + parentBlockPath)));

        if (renderType != null) {
            builder.renderType(renderType);
        }

        builder.transforms()
            .transform(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND)
            .rotation(10, -45, 170)
            .translation(0, 1.5f, -2.75f)
            .scale(0.375f, 0.375f, 0.375f)
            .end();
    }


    private void generateMaps(Map<String, RegistryObject<Item>> itemMap, Map<String, RegistryObject<Block>> blockMap) {
        for (RegistryObject<Item> item : itemMap.values()) {
            baseItem(item);
        }

        for (RegistryObject<Block> block : blockMap.values()) {
            String blockName = block.get().getDescriptionId().split("\\.")[2];
            simpleBlockItemModel(blockName);
        }
    }

    private void generateMaps(Map<String, RegistryObject<Item>> itemMap, Map<String, RegistryObject<Block>> blockMap, String group) {
        generateMaps(itemMap, blockMap, group, null);
    }

    private void generateMaps(Map<String, RegistryObject<Item>> itemMap, Map<String, RegistryObject<Block>> blockMap, String group, @Nullable String renderType) {
        for (RegistryObject<Item> item : itemMap.values()) {
            dynamicItem(item, group);
        }

        for (RegistryObject<Block> block : blockMap.values()) {
            String blockName = block.get().getDescriptionId().split("\\.")[2];
            simpleBlockItemModel(blockName, blockName, renderType);
        }
    }

    private void generateItemMap(Map<String, RegistryObject<? extends Item>> itemMap, String group) {
        for (RegistryObject<? extends Item> item : itemMap.values()) {
            dynamicItem(item, group);
        }
    }

    private void generateItemMapBase(Map<String, RegistryObject<Item>> itemMap) {
        for (RegistryObject<Item> item : itemMap.values()) {
            baseItem(item);
        }
    }

    private void simpleBlockItemModels(Collection<? extends RegistryObject<? extends Block>> blocks) {
        simpleBlockItemModels(blocks, null);
    }

    private void simpleBlockItemModels(Collection<? extends RegistryObject<? extends Block>> blocks, @Nullable String renderType) {
        for (RegistryObject<? extends Block> block : blocks) {
            String blockName = block.getId().getPath();
            simpleBlockItemModel(blockName, blockName, renderType);
        }
    }

    private void multiBlockItemModels() {
        for (RegistryObject<? extends Block> block : ModBlocks.MULTIBLOCKS_MAP.values()) {
            String[] parts = Utility.getMultiBlockNames(block);

            String multiblock = parts[0];
            String blockName = parts[1];

            String parentBlockPath = "multiblock/" + multiblock + "/" + blockName;
            String modelName = "item/" + multiblock + "/" + blockName;

            simpleBlockItemModel(modelName, parentBlockPath);
        }
    }

    public void createCoilItems() {
        for (RegistryObject<Item> item : ModItems.COIL_MAP.values()) {
            getBuilder(item.getId().getPath())
                .parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", modLoc("item/base/coil"))
                .texture("layer1", modLoc("item/base/coil_spindle"));
        }
    }

    public void createSackItems() {
        for (RegistryObject<Item> item : ModItems.SACK_MAP.values()) {
            getBuilder(item.getId().getPath())
                .parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", modLoc("item/base/sack"))
                .texture("layer1", modLoc("item/base/sack_hole"));
        }
    }
}
