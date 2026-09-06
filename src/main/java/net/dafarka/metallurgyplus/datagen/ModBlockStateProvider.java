package net.dafarka.metallurgyplus.datagen;

import net.dafarka.metallurgyplus.MetallurgyPlus;
import net.dafarka.metallurgyplus.block.ModBlocks;
import net.dafarka.metallurgyplus.block.custom.BatteryBlock;
import net.dafarka.metallurgyplus.block.custom.CableBlock;
import net.dafarka.metallurgyplus.block.custom.SackStationBlock;
import net.dafarka.metallurgyplus.block.custom.SolarPanelBlock;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockStateProvider extends BlockStateProvider {

    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, MetallurgyPlus.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        simpleBlocksWithItem();
        customBlocksWithItem();

        blockWithItem(ModBlocks.CLAY_MINERAL);

        horizontalFacingBlock("alloy_smelter", ModBlocks.ALLOY_SMELTER.get());
        horizontalFacingBlock("ore_processing_unit", ModBlocks.ORE_PROCESSING_UNIT.get());
        horizontalFacingBlock("grinder", ModBlocks.GRINDER.get());
        horizontalFacingBlock("press", ModBlocks.PRESS.get());
        horizontalFacingBlock("extractor", ModBlocks.EXTRACTOR.get());
        horizontalFacingBlock("quarry", ModBlocks.QUARRY.get());
        horizontalFacingBlock("power_source", ModBlocks.POWER_SOURCE.get());
        sackStationBlock();

        cableBlocks();
        solarPanelBlocks();
        batteryBlocks();
    }

    private void blockWithItem(RegistryObject<Block> blockRegistryObject) {
        simpleBlockWithItem(blockRegistryObject.get(), cubeAll(blockRegistryObject.get()));
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
    }

    private void customBlocksWithItem() {
        for (RegistryObject<Block> block : ModBlocks.CUSTOM_BLOCKS_MAP.values()) {
            String blockName = block.get().getDescriptionId().split("\\.")[2];
            if (blockName.equals("machine_frame")) {
                simpleBlockWithItem(block.get(), models().getExistingFile(modLoc("block/machine_frame")));
            } else {
                blockWithItem(block);
            }
        }
    }

    public void simpleBlockState(Block block) {
        String blockName = block.getDescriptionId().split("\\.")[2];
        ResourceLocation model = new ResourceLocation(MetallurgyPlus.MODID, "block/" + blockName);

        getVariantBuilder(block).forAllStates(state ->
            ConfiguredModel.builder()
                .modelFile(models().getExistingFile(model))
                .build()
        );
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

    private void cableBlocks() {
        for (RegistryObject<CableBlock> block : ModBlocks.CABLE_BLOCKS_MAP.values()) {
            horizontalFacingBlock(block.get().getDescriptionId().split("\\.")[2], block.get());
        }
    }

    private void solarPanelBlocks() {
        for (RegistryObject<SolarPanelBlock> block : ModBlocks.SOLAR_PANEL_BLOCK_MAP.values()) {
            horizontalFacingBlock(block.get().getDescriptionId().split("\\.")[2], block.get());
        }
    }

    private void batteryBlocks() {
        for (RegistryObject<BatteryBlock> block : ModBlocks.BATTERY_BLOCK_MAP.values()) {
            horizontalFacingBlock(block.get().getDescriptionId().split("\\.")[2], block.get());
        }
    }
}
