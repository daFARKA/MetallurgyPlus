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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ModBlockLootTables extends BlockLootSubProvider {
    public ModBlockLootTables() {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags());
    }

    private Map<String, RegistryObject<Block>> CUSTOM_BLOCKS_MAP_COPY = new HashMap<>();

    @Override
    protected void generate() {
        CUSTOM_BLOCKS_MAP_COPY.clear();
        CUSTOM_BLOCKS_MAP_COPY.putAll(ModBlocks.CUSTOM_BLOCKS_MAP);

        customBlocksCustomBehaviour();

        mapBlocksDropSelf(ModBlocks.MATERIAL_BLOCKS_MAP);
        oreBlocksRaw();
        mapBlocksDropSelf(ModBlocks.ALLOY_BLOCKS_MAP);
        mapBlocksDropSelf(ModBlocks.GEM_BLOCKS_MAP);
        dropSelves(ModBlocks.CABLE_BLOCKS_MAP.values());
        dropSelves(ModBlocks.SOLAR_PANEL_BLOCK_MAP.values());
        dropSelves(ModBlocks.BATTERY_BLOCK_MAP.values());
        dropSelves(ModBlocks.MULTIBLOCKS_MAP.values());
        customBlocksDropSelf();

        this.dropSelf(ModBlocks.MACHINE_FRAME.get());

        this.dropSelf(ModBlocks.ORE_PROCESSING_UNIT.get());
        this.dropSelf(ModBlocks.ALLOY_SMELTER.get());
        this.dropSelf(ModBlocks.GRINDER.get());
        this.dropSelf(ModBlocks.PRESS.get());
        this.dropSelf(ModBlocks.EXTRACTOR.get());
        this.dropSelf(ModBlocks.GEMSTONE_CUTTER.get());
        this.dropSelf(ModBlocks.QUARRY.get());
        this.dropSelf(ModBlocks.POWER_SOURCE.get());
        this.dropSelf(ModBlocks.SACK_STATION.get());
    }

    protected LootTable.Builder createCopperLikeOreDrops(Block pBlock, Item item) {
        return createSilkTouchDispatchTable(pBlock,
            this.applyExplosionDecay(pBlock,
                LootItem.lootTableItem(item)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 5.0F)))
                    .apply(ApplyBonusCount.addOreBonusCount(Enchantments.BLOCK_FORTUNE))));
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries().stream().map(RegistryObject::get)::iterator;
    }

    private void mapBlocksDropSelf(Map<String, RegistryObject<Block>> blockMap) {
        for (RegistryObject<Block> block : blockMap.values()) {
            this.dropSelf(block.get());
        }
    }

    private void oreBlocksRaw() {
        for (RegistryObject<Block> ore : ModBlocks.ORE_BLOCKS_MAP.values()) {
            String currentMaterialName = ore.getId().getPath().split("_")[0];
            this.add(ore.get(), block -> createCopperLikeOreDrops(ore.get(), ModItems.ORE_MAP.get(currentMaterialName + "_raw").get()));
        }
    }

    private void dropSelves(Collection<? extends RegistryObject<? extends Block>> blocks) {
        for (RegistryObject<? extends Block> block : blocks) {
            this.dropSelf(block.get());
        }
    }

    private void customBlocksCustomBehaviour() {
        this.add(ModBlocks.CUSTOM_BLOCKS_MAP.get("clay_mineral").get(),
            block -> createCopperLikeOreDrops(ModBlocks.CUSTOM_BLOCKS_MAP.get("clay_mineral").get(), ModItems.CUSTOM_ITEM_MAP.get("clay_mineral_raw").get()));
        CUSTOM_BLOCKS_MAP_COPY.remove("clay_mineral");
    }

    private void customBlocksDropSelf() {
        for (RegistryObject<Block> block : CUSTOM_BLOCKS_MAP_COPY.values()) {
            this.dropSelf(block.get());
        }
    }
}
