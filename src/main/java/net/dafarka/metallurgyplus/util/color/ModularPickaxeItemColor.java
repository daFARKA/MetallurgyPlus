package net.dafarka.metallurgyplus.util.color;

import net.dafarka.metallurgyplus.item.ModItems;
import net.dafarka.metallurgyplus.item.tool.ModularTool;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

/**
 * Tints the handle from the rod and the head from the head item saved on the stack.
 */
public class ModularPickaxeItemColor implements ItemColor {
    private static final List<Map<String, Integer>> COMPONENT_COLORS = List.of(
        ModItems.MATERIAL_COLOR_MAP,
        ModItems.ALLOY_COLOR_MAP,
        ModItems.VANILLA_COLOR_MAP,
        ModItems.GEM_COLOR_MAP
    );

    @Override
    public int getColor(@NotNull ItemStack stack, int tintIndex) {
        if (!(stack.getItem() instanceof ModularTool tool)) return -1;

        ResourceLocation componentId = switch (tintIndex) {
            case 0 -> tool.getRodId(stack);
            case 1 -> tool.getHeadId(stack);
            default -> null;
        };
        if (componentId == null) return -1;

        for (Map<String, Integer> colorMap : COMPONENT_COLORS) {
            Integer color = colorMap.get(componentId.getPath());
            if (color != null) return color;
        }
        return -1;
    }
}
