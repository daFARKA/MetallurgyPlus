package net.dafarka.metallurgyplus.item.custom;

import net.dafarka.metallurgyplus.util.OreRarity;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class OreBlockItem extends BlockItem {
    private final OreRarity oreRarity;

    public OreBlockItem(Block block, Properties properties, OreRarity oreRarity) {
        super(block, properties);
        this.oreRarity = oreRarity;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable net.minecraft.world.level.Level level,
                                List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        ChatFormatting color;
        String key;

        switch (oreRarity) {
            case COMMON -> {
                color = ChatFormatting.GRAY;
                key = "tooltip.metallurgyplus.common";
            }
            case UNCOMMON -> {
                color = ChatFormatting.GREEN;
                key = "tooltip.metallurgyplus.uncommon";
            }
            case RARE -> {
                color = ChatFormatting.BLUE;
                key = "tooltip.metallurgyplus.rare";
            }
            case VERY_RARE -> {
                color = ChatFormatting.DARK_PURPLE;
                key = "tooltip.metallurgyplus.very_rare";
            }
            case EXTREMELY_RARE -> {
                color = ChatFormatting.GOLD;
                key = "tooltip.metallurgyplus.extremely_rare";
            }
            default -> {
                color = ChatFormatting.WHITE;
                key = "tooltip.metallurgyplus.unknown";
            }
        }

        tooltip.add(Component.translatable(key).withStyle(color));
    }

    public OreRarity getOreRarity() {
        return oreRarity;
    }
}
