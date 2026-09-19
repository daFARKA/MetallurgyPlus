package net.dafarka.metallurgyplus.item;

import net.dafarka.metallurgyplus.MetallurgyPlus;
import net.dafarka.metallurgyplus.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.Comparator;
import java.util.Map;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
        DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MetallurgyPlus.MODID);

    public static final RegistryObject<CreativeModeTab> METALLURGY_PLUS_TAB = CREATIVE_TABS.register("metallurgyplus_tab",
        () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.ALLOY_MAP.get("steel_ingot").get()))
            .title(Component.translatable("creativetab.metallurgyplus_tab"))
            .displayItems((pParameters, pOutput) -> {
                for (RegistryObject<Item> item : ModItems.ITEMS.getEntries()) {
                    pOutput.accept(item.get());
                }
                for (RegistryObject<Block> block : ModBlocks.BLOCKS.getEntries()) {
                    pOutput.accept(block.get());
                }
            })
            .build());

    public static final RegistryObject<CreativeModeTab> ITEMS = CREATIVE_TABS.register("metallurgyplus_items",
        () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.CUSTOM_ITEM_MAP.get("silicon").get()))
            .title(Component.translatable("creativetab.metallurgyplus_items"))
            .displayItems((pParameters, pOutput) -> {
                pOutput.accept(ModItems.LLAMKANA.get());
                addSortedItems(ModItems.CUSTOM_ITEM_MAP, pOutput);
                addSortedItems(ModItems.COIL_MAP, pOutput);
                addSortedItems(ModItems.SACK_MAP, pOutput);
            })
            .build());

    public static final RegistryObject<CreativeModeTab> MATERIALS = CREATIVE_TABS.register("metallurgyplus_materials",
        () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.MATERIAL_MAP.get("tantalum_ingot").get()))
            .title(Component.translatable("creativetab.metallurgyplus_materials"))
            .displayItems((pParameters, pOutput) -> {
                addSortedItems(ModItems.MATERIAL_MAP, ModBlocks.MATERIAL_BLOCKS_MAP, pOutput);
            })
            .build());

    public static final RegistryObject<CreativeModeTab> ORES = CREATIVE_TABS.register("metallurgyplus_ores",
        () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModBlocks.ORE_BLOCKS_MAP.get("ilmenite_stone_block").get()))
            .title(Component.translatable("creativetab.metallurgyplus_ores"))
            .displayItems((pParameters, pOutput) -> {
                pOutput.accept(ModBlocks.CLAY_MINERAL.get());

                addSortedItems(ModItems.ORE_MAP, ModBlocks.ORE_BLOCKS_MAP, pOutput);
            })
            .build());

    public static final RegistryObject<CreativeModeTab> ALLOYS = CREATIVE_TABS.register("metallurgyplus_alloys",
        () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.ALLOY_MAP.get("titanium-6al-2sn-4zr-2mo_ingot").get()))
            .title(Component.translatable("creativetab.metallurgyplus_alloys"))
            .displayItems((pParameters, pOutput) -> {
                addSortedItems(ModItems.ALLOY_MAP, ModBlocks.ALLOY_BLOCKS_MAP, pOutput);
            })
            .build());

    public static final RegistryObject<CreativeModeTab> VANILLA_ITEMS = CREATIVE_TABS.register("metallurgyplus_vanilla_items",
        () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.VANILLA_MAP.get("iron_gear").get()))
            .title(Component.translatable("creativetab.metallurgyplus_vanilla_items"))
            .displayItems((pParameters, pOutput) -> {
                addSortedItems(ModItems.VANILLA_MAP, pOutput);
            })
            .build());

    public static final RegistryObject<CreativeModeTab> GEMS = CREATIVE_TABS.register("metallurgyplus_gems",
        () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.GEM_MAP.get("ruby_gem").get()))
            .title(Component.translatable("creativetab.metallurgyplus_gems"))
            .displayItems((pParameters, pOutput) -> {
                addSortedItems(ModItems.GEM_MAP, ModBlocks.GEM_BLOCKS_MAP, pOutput);
            })
            .build());

    public static final RegistryObject<CreativeModeTab> MACHINES = CREATIVE_TABS.register("metallurgyplus_machines",
        () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModBlocks.ALLOY_SMELTER.get()))
            .title(Component.translatable("creativetab.metallurgyplus_machines"))
            .displayItems((pParameters, pOutput) -> {
                pOutput.accept(ModBlocks.ALLOY_SMELTER.get());
                pOutput.accept(ModBlocks.ORE_PROCESSING_UNIT.get());
                pOutput.accept(ModBlocks.GRINDER.get());
                pOutput.accept(ModBlocks.PRESS.get());
                pOutput.accept(ModBlocks.EXTRACTOR.get());
                pOutput.accept(ModBlocks.QUARRY.get());
                pOutput.accept(ModBlocks.POWER_SOURCE.get());
                pOutput.accept(ModBlocks.SACK_STATION.get());

                addSortedBlocks(ModBlocks.CUSTOM_BLOCKS_MAP, pOutput);

                addSortedBlocks(ModBlocks.CABLE_BLOCKS_MAP, pOutput);
                addSortedBlocks(ModBlocks.BATTERY_BLOCK_MAP, pOutput);
                addSortedBlocks(ModBlocks.SOLAR_PANEL_BLOCK_MAP, pOutput);
            })
            .build());


    public static void register(IEventBus eventBus) {
        CREATIVE_TABS.register(eventBus);
    }

    private static void addSortedItems(Map<?, RegistryObject<Item>> itemMap, CreativeModeTab.Output output) {
        itemMap.values().stream()
            .sorted(Comparator.comparing(item -> item.getId().getPath()))
            .forEach(item -> output.accept(item.get()));
    }

    private static void addSortedItems(Map<?, RegistryObject<Item>> itemMap, Map<String, RegistryObject<Block>> blockMap, CreativeModeTab.Output output) {
        itemMap.values().stream()
            .sorted(Comparator.comparing(item -> item.getId().getPath()))
            .forEach(item -> {
                output.accept(item.get());

                String itemName = item.getId().getPath();

                String baseName = itemName
                    .replace("_ingot", "")
                    .replace("_raw", "")
                    .replace("_gem", "");

                blockMap.entrySet().stream()
                    .filter(entry -> {
                        String blockName = entry.getKey();

                        return blockName.equals(baseName + "_block")
                            || blockName.startsWith(baseName + "_")
                            && blockName.endsWith("_block");
                    })
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(entry -> output.accept(entry.getValue().get()));
            });
    }

    private static void addSortedBlocks(Map<?, ? extends RegistryObject<? extends Block>> blockMap, CreativeModeTab.Output output) {
        blockMap.values().stream()
            .sorted(Comparator.comparingInt(block -> {
                String name = block.getId().getPath();
                return Integer.parseInt(name.replaceAll("\\D+", ""));
            }))
            .forEach(block -> output.accept(new ItemStack(block.get())));
    }
}
