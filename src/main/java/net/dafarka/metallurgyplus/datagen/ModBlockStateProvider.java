package net.dafarka.metallurgyplus.datagen;

import net.dafarka.metallurgyplus.MetallurgyPlus;
import net.dafarka.metallurgyplus.block.ModBlocks;
import net.dafarka.metallurgyplus.block.custom.CableBlock;
import net.dafarka.metallurgyplus.block.custom.SackStationBlock;
import net.dafarka.metallurgyplus.util.Utility;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.MultiPartBlockStateBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Collection;

public class ModBlockStateProvider extends BlockStateProvider {

    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, MetallurgyPlus.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        simpleBlocksWithItem();
        customBlocksWithItem();

        simpleBlockWithItem(ModBlocks.MACHINE_FRAME.get(), models().getExistingFile(modLoc("block/machine_frame")));

        horizontalFacingBlock("alloy_smelter", ModBlocks.ALLOY_SMELTER.get());
        horizontalFacingBlock("ore_processing_unit", ModBlocks.ORE_PROCESSING_UNIT.get());
        horizontalFacingBlock("grinder", ModBlocks.GRINDER.get());
        horizontalFacingBlock("press", ModBlocks.PRESS.get());
        horizontalFacingBlock("extractor", ModBlocks.EXTRACTOR.get());
        horizontalFacingBlock("gemstone_cutter", ModBlocks.GEMSTONE_CUTTER.get());
        horizontalFacingBlock("enricher", ModBlocks.ENRICHER.get());
        horizontalFacingBlock("quarry", ModBlocks.QUARRY.get());
        horizontalFacingBlock("power_source", ModBlocks.POWER_SOURCE.get());
        sackStationBlock();

        for (RegistryObject<CableBlock> cable : ModBlocks.CABLE_BLOCKS_MAP.values()) {
            registerCable(cable.get());
        }
        horizontalFacingBlocks(ModBlocks.SOLAR_PANEL_BLOCK_MAP.values());
        horizontalFacingBlocks(ModBlocks.BATTERY_BLOCK_MAP.values());

        multiBlockBlocks();
    }

    private void blockWithItem(RegistryObject<Block> blockRegistryObject, String group) {
        String blockName = blockRegistryObject.get().getDescriptionId().split("\\.")[2];
        ResourceLocation texture = modLoc("block/" + group + "/" + blockName);
        simpleBlockWithItem(blockRegistryObject.get(), models().cubeAll(blockName, texture));
    }

    private void simpleBlocksWithItem() {
        for (RegistryObject<Block> block : ModBlocks.MATERIAL_BLOCKS_MAP.values()) {
            simpleBlockState(block.get());
        }

        for (RegistryObject<Block> block : ModBlocks.ORE_BLOCKS_MAP.values()) {
            simpleBlockState(block.get());
        }

        for (RegistryObject<Block> block : ModBlocks.ALLOY_BLOCKS_MAP.values()) {
            simpleBlockState(block.get());
        }

        for (RegistryObject<Block> block : ModBlocks.GEM_BLOCKS_MAP.values()) {
            simpleBlockState(block.get());
        }
    }

    private void customBlocksWithItem() {
        for (RegistryObject<Block> block : ModBlocks.CUSTOM_BLOCKS_MAP.values()) {
            blockWithItem(block, "custom");
        }
    }

    public void simpleBlockState(Block block) {
        String blockName = block.getDescriptionId().split("\\.")[2];
        ResourceLocation model = new ResourceLocation(MetallurgyPlus.MODID, "block/" + blockName);
        simpleBlockState(block, model);
    }

    public void simpleBlockState(Block block, ResourceLocation texture) {
        getVariantBuilder(block).forAllStates(state ->
            ConfiguredModel.builder()
                .modelFile(models().getExistingFile(texture))
                .build()
        );
    }

    private void horizontalFacingBlocks(Collection<? extends RegistryObject<? extends Block>> blocks) {
        for (RegistryObject<? extends Block> block : blocks) {
            horizontalFacingBlock(block.getId().getPath(), block.get());
        }
    }

    private void horizontalFacingBlock(String name, Block block) {
        ModelFile model = models().getExistingFile(modLoc("block/" + name));

        getVariantBuilder(block)
            .partialState().with(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH)
            .modelForState().modelFile(model).addModel()
            .partialState().with(BlockStateProperties.HORIZONTAL_FACING, Direction.EAST)
            .modelForState().modelFile(model).rotationY(90).addModel()
            .partialState().with(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH)
            .modelForState().modelFile(model).rotationY(180).addModel()
            .partialState().with(BlockStateProperties.HORIZONTAL_FACING, Direction.WEST)
            .modelForState().modelFile(model).rotationY(270).addModel();
    }

    private void sackStationBlock() {
        ModelFile model = models().getExistingFile(modLoc("block/sack_station"));

        getVariantBuilder(ModBlocks.SACK_STATION.get()).forAllStates(state -> ConfiguredModel.builder()
            .modelFile(model)
            .rotationY(((int) state.getValue(SackStationBlock.FACING).toYRot() + 180) % 360)
            .build());
    }

    private void multiBlockBlocks() {
        for (RegistryObject<? extends Block> block : ModBlocks.MULTIBLOCKS_MAP.values()) {
            String[] parts = Utility.getMultiBlockNames(block);

            String multiblock = parts[0];
            String blockName = parts[1];

            if (multiblock.equals("battery")) {
                batteryPart(blockName, block);
            }
        }
    }

    private void batteryPart(String blockName, RegistryObject<? extends Block> registryObject) {
        String groupName = "multiblock/battery";

        String texturePath = "block/" + groupName + "/" + blockName;

        Block block = registryObject.get();
        if (blockName.contains("controller")) {
            horizontalFacingBlock(texturePath.substring(6), block);
        } else {
            simpleBlockState(block, modLoc(texturePath));
        }
    }

    private void registerCable(CableBlock block) {
        String name = ForgeRegistries.BLOCKS.getKey(block).getPath();
        ModelFile coreModel = new ModelFile.UncheckedModelFile(modLoc("block/" + name + "_core"));
        ModelFile armModel = new ModelFile.UncheckedModelFile(modLoc("block/" + name + "_arm"));

        MultiPartBlockStateBuilder builder = getMultipartBuilder(block);

        // Always render the center, then overlay an arm for each true connection property.
        builder.part().modelFile(coreModel).addModel().end();

        addConnectionPart(builder, armModel, PipeBlock.NORTH, 0, 0);
        addConnectionPart(builder, armModel, PipeBlock.SOUTH, 0, 180);
        addConnectionPart(builder, armModel, PipeBlock.WEST, 0, 270);
        addConnectionPart(builder, armModel, PipeBlock.EAST, 0, 90);
        addConnectionPart(builder, armModel, PipeBlock.UP, 270, 0);
        addConnectionPart(builder, armModel, PipeBlock.DOWN, 90, 0);
    }

    private void addConnectionPart(MultiPartBlockStateBuilder builder, ModelFile model,
                                   BooleanProperty property, int rotationX, int rotationY) {
        builder.part()
            .modelFile(model)
            .rotationX(rotationX)
            .rotationY(rotationY)
            .uvLock(true)
            .addModel()
            .condition(property, true)
            .end();
    }
}
