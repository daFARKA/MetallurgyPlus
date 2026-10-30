package net.dafarka.metallurgyplus.item.tool;

import net.dafarka.metallurgyplus.MetallurgyPlus;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class ModularPickaxeItem extends ModularTool {
    private static final ResourceLocation DEFAULT_HEAD =
        new ResourceLocation(MetallurgyPlus.MODID, "iron_pickaxe-head");
    private static final ResourceLocation DEFAULT_ROD =
        new ResourceLocation(MetallurgyPlus.MODID, "iron_rod");

    public ModularPickaxeItem(Item.Properties properties) {
        // Harvest level is iron for this first version; head-specific speed and
        // durability are read from the selected component items.
        super(1.0F, -2.8F, Tiers.IRON, BlockTags.MINEABLE_WITH_PICKAXE,
            DEFAULT_HEAD, DEFAULT_ROD, properties);
    }

    @NotNull
    @Override
    public Component getName(ItemStack stack) {
        String material = getHeadId(stack).getPath().split("_")[0];
        return Component.translatable(material + "_pickaxe");
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        if (!state.is(BlockTags.MINEABLE_WITH_PICKAXE)) return 1.0F;
        return getMiningSpeed(stack);
    }
}
