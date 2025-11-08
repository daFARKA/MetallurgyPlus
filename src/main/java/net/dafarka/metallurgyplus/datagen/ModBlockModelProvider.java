package net.dafarka.metallurgyplus.datagen;

import net.dafarka.metallurgyplus.MetallurgyPlus;
import net.dafarka.metallurgyplus.block.ModBlocks;
import net.dafarka.metallurgyplus.block.custom.CableBlock;
import net.dafarka.metallurgyplus.item.ModItems;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockModelProvider extends BlockModelProvider {

    public ModBlockModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, MetallurgyPlus.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        registerDefaultModels();
    }

    private void registerDefaultModels() {
        for (RegistryObject<Block> block : ModBlocks.MATERIAL_BLOCKS_MAP.values()) {
            String blockName = block.get().getDescriptionId().split("\\.")[2];
            registerMaterialModel(blockName);
        }

        for (RegistryObject<Block> block : ModBlocks.ORE_BLOCKS_MAP.values()) {
            String blockName = block.get().getDescriptionId().split("\\.")[2];
            registerOreModel(blockName);
        }

        for (RegistryObject<Block> block : ModBlocks.ALLOY_BLOCKS_MAP.values()) {
            String blockName = block.get().getDescriptionId().split("\\.")[2];
            registerMaterialModel(blockName);
        }

        for (RegistryObject<CableBlock> block : ModBlocks.CABLE_BLOCKS_MAP.values()) {
            String blockName = block.get().getDescriptionId().split("\\.")[2];
            registerCable(blockName);
        }

        registerOrientables();
    }

    private void registerMaterialModel(String blockName) {
        ResourceLocation texture = new ResourceLocation(MetallurgyPlus.MODID, "block/base_block");

        getBuilder(blockName)
            .parent(getExistingFile(mcLoc("block/cube_all")))
            .texture("all", texture)
            .element()
            .from(0, 0, 0)
            .to(16, 16, 16)
            .face(Direction.NORTH).tintindex(0).texture("#all").end()
            .face(Direction.SOUTH).tintindex(0).texture("#all").end()
            .face(Direction.EAST).tintindex(0).texture("#all").end()
            .face(Direction.WEST).tintindex(0).texture("#all").end()
            .face(Direction.UP).tintindex(0).texture("#all").end()
            .face(Direction.DOWN).tintindex(0).texture("#all").end()
            .end();
    }

    private void registerOreModel(String blockName) {
        ResourceLocation textureBase = new ResourceLocation(MetallurgyPlus.MODID, "block/stone");
        for (int i = 1; i < ModItems.ORE_BASE_NAME.length; i++) {
            if (blockName.split("_")[1].equals(ModItems.ORE_BASE_NAME[i])) {
                textureBase = new ResourceLocation(MetallurgyPlus.MODID, "block/" + ModItems.ORE_BASE_NAME[i]);
            }
        }
        ResourceLocation textureOreLayer = new ResourceLocation(MetallurgyPlus.MODID, "block/ore_layer");

        getBuilder(blockName)
            .parent(getExistingFile(mcLoc("block/cube_all")))
            .texture("particle", textureBase)
            .texture("layer0", textureBase)
            .texture("layer1", textureOreLayer)
            .element()
            .from(0, 0, 0)
            .to(16, 16, 16)
            .face(Direction.NORTH).tintindex(1).texture("#layer0").end()
            .face(Direction.SOUTH).tintindex(1).texture("#layer0").end()
            .face(Direction.EAST).tintindex(1).texture("#layer0").end()
            .face(Direction.WEST).tintindex(1).texture("#layer0").end()
            .face(Direction.UP).tintindex(1).texture("#layer0").end()
            .face(Direction.DOWN).tintindex(1).texture("#layer0").end()
            .end()
            .element()
            .from(0, 0, 0)
            .to(16, 16, 16)
            .face(Direction.NORTH).tintindex(0).texture("#layer1").end()
            .face(Direction.SOUTH).tintindex(0).texture("#layer1").end()
            .face(Direction.EAST).tintindex(0).texture("#layer1").end()
            .face(Direction.WEST).tintindex(0).texture("#layer1").end()
            .face(Direction.UP).tintindex(0).texture("#layer1").end()
            .face(Direction.DOWN).tintindex(0).texture("#layer1").end()
            .end();
    }

    private void registerOrientables() {
        registerOrientable("alloy_smelter", false);
        registerOrientable("ore_processing_unit", false);
        registerOrientable("power_source", true);
        registerOrientable("battery", true);
    }

    private void registerOrientable(String name, boolean allSidesSame) {
        if (allSidesSame) {
            getBuilder(name)
                .parent(getExistingFile(modLoc("block_entity_orientable")))
                .texture("top", modLoc("block/" + name))
                .texture("front", modLoc("block/" + name))
                .texture("side", modLoc("block/" + name));
        } else {
            getBuilder(name)
                .parent(getExistingFile(modLoc("block_entity_orientable")))
                .texture("top", modLoc("block/" + name + "_top"))
                .texture("front", modLoc("block/" + name + "_front"))
                .texture("side", modLoc("block/" + name + "_side"));
        }
    }

    private void registerCable(String name) {
        getBuilder(name)
            .parent(getExistingFile(modLoc("block_entity_orientable")))
            .texture("top", modLoc("block/base_cable"))
            .texture("front", modLoc("block/base_cable"))
            .texture("side", modLoc("block/base_cable"))
            .element()
            .from(0, 0, 0)
            .to(16, 16, 16)
            .face(Direction.NORTH).texture("#front").tintindex(0).end()
            .face(Direction.SOUTH).texture("#front").tintindex(0).end()
            .face(Direction.EAST).texture("#side").tintindex(0).end()
            .face(Direction.WEST).texture("#side").tintindex(0).end()
            .face(Direction.UP).texture("#top").tintindex(0).end()
            .face(Direction.DOWN).texture("#top").tintindex(0).end()
            .end();
    }

}
