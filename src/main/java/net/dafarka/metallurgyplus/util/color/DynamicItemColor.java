package net.dafarka.metallurgyplus.util.color;

import net.dafarka.metallurgyplus.block.ModBlocks;
import net.dafarka.metallurgyplus.item.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.IForgeBakedModel;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class DynamicItemColor implements ItemColor {

    // Pre-grouped lookup lists so we don't branch with 10 separate if-else statements
    private static final List<Map<String, Integer>> ITEM_COLOR_MAPS = List.of(
        ModItems.MATERIAL_COLOR_MAP,
        ModItems.ORE_COLOR_MAP,
        ModItems.ALLOY_COLOR_MAP,
        ModItems.VANILLA_COLOR_MAP,
        ModItems.GEM_COLOR_MAP
    );

    private static final List<Map<String, Integer>> BLOCK_ITEM_COLOR_MAPS = List.of(
        ModBlocks.MATERIAL_COLOR_MAP,
        ModBlocks.ORE_COLOR_MAP,
        ModBlocks.ALLOY_COLOR_MAP,
        ModBlocks.GEM_COLOR_MAP
    );

    public DynamicItemColor() { }

    @Override
    public int getColor(@NotNull ItemStack pStack, int pTintIndex) {
        if (pTintIndex != 0) {
            return -1;
        }

        ResourceLocation id = ForgeRegistries.ITEMS.getKey(pStack.getItem());
        if (id == null) {
            return -1;
        }

        String name = id.getPath();

        if (pStack.getItem() instanceof BlockItem) {
            return findColor(name, BLOCK_ITEM_COLOR_MAPS);
        }

        // For regular items: Check sprite path first
        IForgeBakedModel model = Minecraft.getInstance().getItemRenderer().getModel(pStack, null, null, 0);
        TextureAtlasSprite sprite = model.getParticleIcon(ModelData.EMPTY);

        if (!sprite.contents().name().getPath().startsWith("item/base/")) {
            return -1;
        }

        return findColor(name, ITEM_COLOR_MAPS);
    }

    /**
     * Looks through candidate color maps with a single lookup per map.
     */
    private static int findColor(String key, List<Map<String, Integer>> maps) {
        for (Map<String, Integer> map : maps) {
            Integer color = map.get(key);
            if (color != null) {
                return color;
            }
        }
        return -1;
    }
}