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
        simpleBlocksWithItem();

        blockWithItem(ModBlocks.CLAY_MINERAL);
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

    public void simpleBlockState(Block block) {
        String blockName = block.getDescriptionId().split("\\.")[2];
        ResourceLocation model = new ResourceLocation(MetallurgyPlus.MODID, "block/" + blockName);

        getVariantBuilder(block).forAllStates(state ->
            ConfiguredModel.builder()
                .modelFile(models().getExistingFile(model))
                .build()
        );
    }


}
