package net.dafarka.metallurgyplus.datagen;

import net.dafarka.metallurgyplus.MetallurgyPlus;
import net.dafarka.metallurgyplus.block.ModBlocks;
import net.dafarka.metallurgyplus.block.custom.BatteryBlock;
import net.dafarka.metallurgyplus.block.custom.CableBlock;
import net.dafarka.metallurgyplus.block.custom.SolarPanelBlock;
import net.dafarka.metallurgyplus.item.ModItems;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;

public class ModBlockModelProvider extends BlockModelProvider {

    public ModBlockModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, MetallurgyPlus.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        int[] defaultTintIndices = {0, 0, 0, 0, 0, 0};

        registerMachineFrame();

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
            registerOrientable(blockName, "base_cable", "", "", "", "", "", defaultTintIndices);
        }

        for (RegistryObject<SolarPanelBlock> block : ModBlocks.SOLAR_PANEL_BLOCK_MAP.values()) {
            String blockName = block.get().getDescriptionId().split("\\.")[2];
            registerOrientable(blockName, "solar_panel", "_", "side", "side", "top", "bottom", new int[]{0, 0, 0, 0, 1, 0});
        }

        for (RegistryObject<BatteryBlock> block : ModBlocks.BATTERY_BLOCK_MAP.values()) {
            String blockName = block.get().getDescriptionId().split("\\.")[2];
            registerOrientable(blockName, "base_battery", "", "", "", "", "", defaultTintIndices);
        }

        registerOrientable("alloy_smelter", "alloy_smelter", "_", "front", "side", "top", "top", null);
        registerOrientable("ore_processing_unit", "ore_processing_unit", "_", "front", "side", "top", "top", null);
        registerOrientable("grinder", "grinder", "_", "front", "side", "top", "top", null);
        registerOrientable("press", "press", "_", "front", "side", "top", "top", null);
        registerOrientable("extractor", "extractor", "_", "front", "side", "top", "top", null);
        registerOrientable("quarry", "quarry", "_", "front", "side", "top", "top", null);
        registerOrientable("power_source", "power_source", "", "", "", "", "", null);
        registerSackStation();
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

    private void registerOrientable(String name, String textureName, String connector, String front, String side, String top, String bottom, @Nullable int[] tintIndices) {
        if (tintIndices == null || tintIndices.length != 6) {
            getBuilder(name)
                .parent(getExistingFile(modLoc("block_entity_orientable")))
                .texture("front", modLoc("block/" + textureName + connector + front))
                .texture("side", modLoc("block/" + textureName + connector + side))
                .texture("top", modLoc("block/" + textureName + connector + top))
                .texture("bottom", modLoc("block/" + textureName + connector + bottom));
        } else {
            getBuilder(name)
                .parent(getExistingFile(modLoc("block_entity_orientable")))
                .texture("front", modLoc("block/" + textureName + connector + front))
                .texture("side", modLoc("block/" + textureName + connector + side))
                .texture("top", modLoc("block/" + textureName + connector + top))
                .texture("bottom", modLoc("block/" + textureName + connector + bottom))
                .element()
                .from(0, 0, 0)
                .to(16, 16, 16)
                .face(Direction.NORTH).texture("#front").tintindex(tintIndices[0]).end()
                .face(Direction.SOUTH).texture("#side").tintindex(tintIndices[1]).end()
                .face(Direction.EAST).texture("#side").tintindex(tintIndices[2]).end()
                .face(Direction.WEST).texture("#side").tintindex(tintIndices[3]).end()
                .face(Direction.UP).texture("#top").tintindex(tintIndices[4]).end()
                .face(Direction.DOWN).texture("#bottom").tintindex(tintIndices[5]).end()
                .end();
        }
    }

    private void registerSackStation() {
        getBuilder("sack_station")
            .parent(getExistingFile(modLoc("block_entity_orientable")))
            .texture("front", modLoc("block/sack_station"))
            .texture("side", modLoc("block/sack_station"))
            .texture("top", modLoc("block/sack_station"))
            .texture("bottom", modLoc("block/sack_station_bottom"))
            .renderType(mcLoc("cutout"));
    }

    private void registerMachineFrame() {
        getBuilder("machine_frame")
            .parent(getExistingFile(mcLoc("block/cube_all")))
            .texture("all", modLoc("block/machine_frame"))
            .renderType(mcLoc("cutout"));
    }
}
