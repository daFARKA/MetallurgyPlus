package net.dafarka.metallurgyplus.datagen;

import net.dafarka.metallurgyplus.MetallurgyPlus;
import net.dafarka.metallurgyplus.block.ModBlocks;
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
        registerMaterialModels();
    }

    private void registerMaterialModels() {
        for (RegistryObject<Block> block : ModBlocks.MATERIAL_BLOCKS_MAP.values()) {
            String blockName = block.get().getDescriptionId().split("\\.")[2];
            registerMaterialModel(blockName);
        }
    }

    private void registerMaterialModel(String blockName) {
        // Replace with your block registry name and texture location
        ResourceLocation texture = new ResourceLocation(MetallurgyPlus.MODID, "block/base_block");

        // Generate a cube model with tintindex for each face
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

}
