package net.dafarka.metallurgyplus.datagen.loot;

import net.dafarka.metallurgyplus.block.ModBlocks;
import net.dafarka.metallurgyplus.item.ModItems;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.registries.RegistryObject;

import java.util.Set;

public class ModBlockLootTables extends BlockLootSubProvider {
    public ModBlockLootTables() {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags());
    }



    @Override
    protected void generate() {
        materialBlocksDropSelf();

        this.dropSelf(ModBlocks.ORE_PROCESSING_UNIT.get());
        this.dropSelf(ModBlocks.ALLOY_SMELTER.get());

        this.add(ModBlocks.CLAY_MINERAL.get(),
            block -> createCopperLikeOreDrops(ModBlocks.CLAY_MINERAL.get(), ModItems.CLAY_MINERAL_RAW.get()));
        this.add(ModBlocks.BAUXITE_ORE.get(),
            block -> createCopperLikeOreDrops(ModBlocks.BAUXITE_ORE.get(), ModItems.BAUXITE.get()));
        this.add(ModBlocks.BAUXITE_ORE_DEEPSLATE.get(),
            block -> createCopperLikeOreDrops(ModBlocks.BAUXITE_ORE_DEEPSLATE.get(), ModItems.BAUXITE.get()));
    }

    protected LootTable.Builder createCopperLikeOreDrops(Block pBlock, Item item) {
        return createSilkTouchDispatchTable(pBlock,
            this.applyExplosionDecay(pBlock,
                LootItem.lootTableItem(item)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 5.0F)))
                    .apply(ApplyBonusCount.addOreBonusCount(Enchantments.BLOCK_FORTUNE))));
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries().stream().map(RegistryObject::get)::iterator;
    }

    private void materialBlocksDropSelf() {
        for (RegistryObject<Block> block : ModBlocks.MATERIAL_BLOCKS_MAP.values()) {
            this.dropSelf(block.get());
        }
    }
}
