package net.dafarka.metallurgyplus.datagen;

import net.dafarka.metallurgyplus.MetallurgyPlus;
import net.dafarka.metallurgyplus.block.ModBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagGenerator extends BlockTagsProvider {

    public ModBlockTagGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                                @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, MetallurgyPlus.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .add(ModBlocks.STEEL_BLOCK.get(),
                ModBlocks.BAUXITE_ORE.get());

        this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .add(ModBlocks.ORE_PROCESSING_UNIT.get());

        this.tag(BlockTags.MINEABLE_WITH_SHOVEL)
            .add(ModBlocks.CLAY_MINERAL.get());

        this.tag(BlockTags.NEEDS_STONE_TOOL)
            .add(ModBlocks.BAUXITE_ORE.get());

        this.tag(BlockTags.NEEDS_IRON_TOOL)
            .add(ModBlocks.STEEL_BLOCK.get(),
                ModBlocks.CLAY_MINERAL.get());

        this.tag(Tags.Blocks.ORES)
            .add(ModBlocks.CLAY_MINERAL.get(),
                ModBlocks.BAUXITE_ORE.get());

    }
}
