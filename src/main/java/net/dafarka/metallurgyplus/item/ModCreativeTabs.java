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

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
        DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MetallurgyPlus.MODID);

    public static final RegistryObject<CreativeModeTab> METALLURGY_PLUS_TAB = CREATIVE_TABS.register("metallurgyplus_tab",
        () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.MATERIAL_MAP.get("steel_ingot").get()))
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


    public static void register(IEventBus eventBus) {
        CREATIVE_TABS.register(eventBus);
    }
}
