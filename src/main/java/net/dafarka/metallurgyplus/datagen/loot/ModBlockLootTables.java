package net.dafarka.metallurgyplus.datagen.loot;

import net.dafarka.metallurgyplus.block.ModBlocks;
import net.dafarka.metallurgyplus.block.custom.BatteryBlock;
import net.dafarka.metallurgyplus.block.custom.CableBlock;
import net.dafarka.metallurgyplus.block.custom.SolarPanelBlock;
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

import java.util.Map;
import java.util.Set;

public class ModBlockLootTables extends BlockLootSubProvider {
    public ModBlockLootTables() {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags());
    }


    @Override
    protected void generate() {
        mapBlocksDropSelf(ModBlocks.MATERIAL_BLOCKS_MAP);
        oreBlocksRaw();
        mapBlocksDropSelf(ModBlocks.ALLOY_BLOCKS_MAP);
        cableBlocksDropSelf();
        solarPanelBlocksDropSelf();
        batteryBlocksDropSelf();
        customBlocksDropSelf();

        this.dropSelf(ModBlocks.ORE_PROCESSING_UNIT.get());
        this.dropSelf(ModBlocks.ALLOY_SMELTER.get());
        this.dropSelf(ModBlocks.GRINDER.get());
        this.dropSelf(ModBlocks.PRESS.get());
        this.dropSelf(ModBlocks.EXTRACTOR.get());
        this.dropSelf(ModBlocks.QUARRY.get());
        this.dropSelf(ModBlocks.POWER_SOURCE.get());
        this.dropSelf(ModBlocks.SACK_STATION.get());

        this.add(ModBlocks.CLAY_MINERAL.get(),
            block -> createCopperLikeOreDrops(ModBlocks.CLAY_MINERAL.get(), ModItems.CUSTOM_ITEM_MAP.get("clay_mineral_raw").get()));
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

    private void cableBlocksDropSelf() {
        for (RegistryObject<CableBlock> cable : ModBlocks.CABLE_BLOCKS_MAP.values()) {
            this.dropSelf(cable.get());
        }
    }

    private void solarPanelBlocksDropSelf() {
        for (RegistryObject<SolarPanelBlock> panel : ModBlocks.SOLAR_PANEL_BLOCK_MAP.values()) {
            this.dropSelf(panel.get());
        }
    }

    private void batteryBlocksDropSelf() {
        for (RegistryObject<BatteryBlock> battery : ModBlocks.BATTERY_BLOCK_MAP.values()) {
            this.dropSelf(battery.get());
        }
    }

    private void customBlocksDropSelf() {
        for (RegistryObject<Block> block : ModBlocks.CUSTOM_BLOCKS_MAP.values()) {
            this.dropSelf(block.get());
        }
    }
}
