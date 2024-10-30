package net.dafarka.metallurgyplus.datagen;

import net.dafarka.metallurgyplus.MetallurgyPlus;
import net.dafarka.metallurgyplus.block.ModBlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockStateProvider extends BlockStateProvider {

    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, MetallurgyPlus.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        materialBlocksWithItem();

        blockWithItem(ModBlocks.CLAY_MINERAL);
        blockWithItem(ModBlocks.BAUXITE_ORE);
        blockWithItem(ModBlocks.BAUXITE_ORE_DEEPSLATE);
    }

    private void blockWithItem(RegistryObject<Block> blockRegistryObject) {
        simpleBlockWithItem(blockRegistryObject.get(), cubeAll(blockRegistryObject.get()));
    }

    private void materialBlocksWithItem() {
        for (RegistryObject<Block> block : ModBlocks.MATERIAL_BLOCKS_MAP.values()) {
            //String blockName = block.get().getDescriptionId().split("\\.")[2];
            //generateCustomBlockModelWithItem(ForgeRegistries.BLOCKS.getValue(new ResourceLocation(MetallurgyPlus.MODID, blockName)), blockName);
            simpleBlockState(block.get());
        }
    }

    private void generateCustomBlockModelWithItem(Block block, String modelName) {
        // Create a custom model with "cube_all" parent and specified texture path
        ModelFile model = models().withExistingParent(modelName, "minecraft:block/cube_all")
            .texture("all", MetallurgyPlus.MODID + ":block/base_block");

        // Link the block state to the custom model
        simpleBlockWithItem(block, model);
    }

    public void simpleBlockState(Block block) {
        String blockName = block.getDescriptionId().split("\\.")[2];
        // Use the mod ID and the model name to construct the model path
        ResourceLocation model = new ResourceLocation(MetallurgyPlus.MODID, "block/" + blockName);

        // Generate a simple blockstate with a single variant
        getVariantBuilder(block).forAllStates(state ->
            ConfiguredModel.builder()
                .modelFile(models().getExistingFile(model))
                .build()
        );
    }


}
