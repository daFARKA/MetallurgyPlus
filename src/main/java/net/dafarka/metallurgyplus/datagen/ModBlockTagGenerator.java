package net.dafarka.metallurgyplus.datagen;

import net.dafarka.metallurgyplus.MetallurgyPlus;
import net.dafarka.metallurgyplus.block.ModBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ModBlockTagGenerator extends BlockTagsProvider {

    public ModBlockTagGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                                @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, MetallurgyPlus.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        mapBlocksAddTags(ModBlocks.MATERIAL_BLOCKS_MAP);
        oreBlocksAddTags();
        mapBlocksAddTags(ModBlocks.MATERIAL_BLOCKS_MAP);

        this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .add(ModBlocks.ORE_PROCESSING_UNIT.get(),
                ModBlocks.ALLOY_SMELTER.get());

        this.tag(BlockTags.MINEABLE_WITH_SHOVEL)
            .add(ModBlocks.CLAY_MINERAL.get());

        this.tag(BlockTags.NEEDS_IRON_TOOL)
            .add(ModBlocks.CLAY_MINERAL.get());

        this.tag(Tags.Blocks.ORES)
            .add(ModBlocks.CLAY_MINERAL.get());
    }

    private void mapBlocksAddTags(Map<String, RegistryObject<Block>> blockMap) {
        for (RegistryObject<Block> block : blockMap.values()) {
            this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(block.get());
            this.tag(BlockTags.NEEDS_IRON_TOOL).add(block.get());
        }
    }

    private void oreBlocksAddTags() {
        for (RegistryObject<Block> ore : ModBlocks.ORE_BLOCKS_MAP.values()) {
            this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ore.get());
            this.tag(BlockTags.NEEDS_IRON_TOOL).add(ore.get());
            this.tag(Tags.Blocks.ORES).add(ore.get());
        }
    }
}
