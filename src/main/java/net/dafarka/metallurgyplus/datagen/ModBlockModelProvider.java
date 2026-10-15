package net.dafarka.metallurgyplus.datagen;

import net.dafarka.metallurgyplus.MetallurgyPlus;
import net.dafarka.metallurgyplus.block.ModBlocks;
import net.dafarka.metallurgyplus.block.custom.BatteryBlock;
import net.dafarka.metallurgyplus.block.custom.CableBlock;
import net.dafarka.metallurgyplus.block.custom.SolarPanelBlock;
import net.dafarka.metallurgyplus.item.ModItems;
import net.dafarka.metallurgyplus.util.Utility;
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
            registerCubeAllModel(blockName, "metal_block", "base");
        }

        for (RegistryObject<Block> block : ModBlocks.ORE_BLOCKS_MAP.values()) {
            String blockName = block.get().getDescriptionId().split("\\.")[2];
            registerOreModel(blockName);
        }

        for (RegistryObject<Block> block : ModBlocks.ALLOY_BLOCKS_MAP.values()) {
            String blockName = block.get().getDescriptionId().split("\\.")[2];
            registerCubeAllModel(blockName, "metal_block", "base");
        }

        for (RegistryObject<Block> block : ModBlocks.GEM_BLOCKS_MAP.values()) {
            String blockName = block.get().getDescriptionId().split("\\.")[2];
            registerCubeAllModel(blockName, "gem_block", "base");
        }

        for (RegistryObject<CableBlock> block : ModBlocks.CABLE_BLOCKS_MAP.values()) {
            String blockName = block.getId().getPath();
            String textureName = Utility.removeTrailingDigits(Utility.removeInString(blockName, "_block"));
            registerCableModels(blockName, modLoc("block/energy/" + textureName));
        }

        for (RegistryObject<SolarPanelBlock> block : ModBlocks.SOLAR_PANEL_BLOCK_MAP.values()) {
            String blockName = block.get().getDescriptionId().split("\\.")[2];
            registerOrientable(blockName, "energy", "solar_panel", "_", "side", "side", "top", "bottom", defaultTintIndices);
        }

        for (RegistryObject<BatteryBlock> block : ModBlocks.BATTERY_BLOCK_MAP.values()) {
            String blockName = block.get().getDescriptionId().split("\\.")[2];
            registerOrientable(blockName, "energy", "battery", "", "", "", "", "", defaultTintIndices);
        }

        for (RegistryObject<? extends Block> block : ModBlocks.MULTIBLOCKS_MAP.values()) {
            String[] parts = Utility.getMultiBlockNames(block);

            String multiblock = parts[0];
            String blockName = parts[1];

            if (multiblock.equals("battery")) {
                registerBatteryPart(blockName);
            }
        }

        registerOrientable("alloy_smelter", "machine", "alloy_smelter", "_", null);
        registerOrientable("ore_processing_unit", "machine", "ore_processing_unit", "_", null);
        registerOrientable("grinder", "machine", "grinder", "_", null);
        registerOrientable("press", "machine", "press", "_", null);
        registerOrientable("extractor", "machine", "extractor", "_", null);
        registerOrientable("gemstone_cutter", "machine", "gemstone_cutter", "_", null);
        registerOrientable("enricher", "machine", "enricher", "_", null);
        registerOrientable("quarry", "machine", "quarry", "_", null);
        registerOrientable("power_source", "energy", "power_source", "", "", "", "", "", null);
        registerSackStation();
    }

    private void registerCableModels(String name, ResourceLocation texture) {
        getBuilder(name + "_core")
            .parent(getExistingFile(mcLoc("block/block")))
            .texture("particle", texture)
            .texture("cable", texture)
            .element()
            .from(6, 6, 6)
            .to(10, 10, 10)
            .allFaces((direction, face) -> face
                .uvs(6, 6, 10, 10)
                .texture("#cable")
                .tintindex(0))
            .end();

        getBuilder(name + "_arm")
            .parent(getExistingFile(mcLoc("block/block")))
            .texture("particle", texture)
            .texture("cable", texture)
            .element()
            .from(6, 6, 0)
            .to(10, 10, 6)
            .face(Direction.DOWN).uvs(6, 0, 10, 6).texture("#cable").tintindex(0).end()
            .face(Direction.UP).uvs(6, 10, 10, 16).texture("#cable").tintindex(0).end()
            .face(Direction.NORTH).uvs(6, 6, 10, 10).texture("#cable").tintindex(0)
            .cullface(Direction.NORTH).end()
            .face(Direction.WEST).uvs(0, 6, 6, 10).texture("#cable").tintindex(0).end()
            .face(Direction.EAST).uvs(10, 6, 16, 10).texture("#cable").tintindex(0).end()
            // The south face meets the core, so it is intentionally omitted.
            .end();

        // A straight cable segment for the inventory icon.
        getBuilder(name + "_item")
            .parent(getExistingFile(mcLoc("block/block")))
            .texture("particle", texture)
            .texture("cable", texture)
            .element()
            .from(6, 6, 0)
            .to(10, 10, 16)
            .face(Direction.DOWN).uvs(6, 0, 10, 16).texture("#cable").tintindex(0).end()
            .face(Direction.UP).uvs(6, 0, 10, 16).texture("#cable").tintindex(0).end()
            .face(Direction.NORTH).uvs(6, 6, 10, 10).texture("#cable").tintindex(0).end()
            .face(Direction.SOUTH).uvs(6, 6, 10, 10).texture("#cable").tintindex(0).end()
            .face(Direction.WEST).uvs(0, 6, 6, 10).texture("#cable").tintindex(0).end()
            .face(Direction.EAST).uvs(10, 6, 16, 10).texture("#cable").tintindex(0).end()
            .end();
    }

    private void registerCubeAllModel(String blockName, String textureName, String group) {
        ResourceLocation texture = new ResourceLocation(MetallurgyPlus.MODID, "block/" + group + "/" + textureName);
        if (group.isEmpty()) {
            texture = new ResourceLocation(MetallurgyPlus.MODID, "block/" + blockName);
        }

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
        ResourceLocation textureBase = new ResourceLocation(MetallurgyPlus.MODID, "block/ore/stone");
        for (int i = 1; i < ModItems.ORE_BASE_NAME.length; i++) {
            if (blockName.split("_")[1].equals(ModItems.ORE_BASE_NAME[i])) {
                textureBase = new ResourceLocation(MetallurgyPlus.MODID, "block/ore/" + ModItems.ORE_BASE_NAME[i]);
            }
        }
        ResourceLocation textureOreLayer = new ResourceLocation(MetallurgyPlus.MODID, "block/ore/ore_layer");

        getBuilder(blockName)
            .parent(getExistingFile(mcLoc("block/cube_all")))
            .renderType(mcLoc("cutout"))
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

    private void registerOrientable(String name, String group, String textureName, String connector, String front, String side, String top, String bottom, @Nullable int[] tintIndices) {
        registerOrientable(
            name,
            group,
            textureName + connector + front,
            textureName + connector + side,
            textureName + connector + top,
            textureName + connector + bottom,
            tintIndices
        );
    }

    private void registerOrientable(String name, String group, String textureName, String connector, @Nullable int[] tintIndices) {
        registerOrientable(
            name,
            group,
            textureName,
            connector,
            "front",
            "side",
            "top",
            "bottom",
            tintIndices
        );
    }

    private void registerOrientable(String name, String group, String front, String side, String top, String bottom, @Nullable int[] tintIndices) {
        ResourceLocation frontTexture = modLoc("block/" + group + "/" + front);
        ResourceLocation sideTexture = modLoc("block/" + group + "/" + side);
        ResourceLocation topTexture = modLoc("block/" + group + "/" + top);
        ResourceLocation bottomTexture = modLoc("block/" + group + "/" + bottom);

        if (tintIndices == null || tintIndices.length != 6) {
            getBuilder(name)
                .parent(getExistingFile(modLoc("block_entity_orientable")))
                .texture("front", frontTexture)
                .texture("side", sideTexture)
                .texture("top", topTexture)
                .texture("bottom", bottomTexture);
        } else {
            getBuilder(name)
                .parent(getExistingFile(modLoc("block_entity_orientable")))
                .texture("front", frontTexture)
                .texture("side", sideTexture)
                .texture("top", topTexture)
                .texture("bottom", bottomTexture)
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
            .texture("front", modLoc("block/machine/sack_station"))
            .texture("side", modLoc("block/machine/sack_station"))
            .texture("top", modLoc("block/machine/sack_station"))
            .texture("bottom", modLoc("block/machine/sack_station_bottom"))
            .renderType(mcLoc("cutout"));
    }

    private void registerMachineFrame() {
        getBuilder("machine_frame")
            .parent(getExistingFile(mcLoc("block/cube_all")))
            .texture("all", modLoc("block/custom/machine_frame"))
            .renderType(mcLoc("cutout"));
    }

    private void registerBatteryPart(String blockName) {
        String groupName = "multiblock/battery";

        String pathName = "block/" + groupName + "/" + blockName;
        String baseName = Utility.removeTrailingDigits(blockName);
        if (baseName.contains("controller")) {
            registerOrientable(pathName, groupName, "controller", "casing", "casing", "casing", null);
        } else if (baseName.contains("cell")) {
            groupName = groupName + "/base";
            String textureName = Utility.removeTrailingDigits(baseName);
            String sideTextureName = textureName + "_side";
            String topTextureName = "cell_top";
            registerOrientable(pathName, groupName, sideTextureName, sideTextureName, topTextureName, topTextureName, new int[]{0, 0, 0, 0, 1, 1});
        } else {
            registerCubeAllModel(pathName, blockName, groupName);
        }
    }
}
