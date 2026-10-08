package net.dafarka.metallurgyplus.util.color;

import net.dafarka.metallurgyplus.util.Utility;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Function;

public class DynamicKeyColor<K> implements BlockColor, ItemColor {

    private final Map<K, Integer> colorMap;
    private final Function<String, K> keyExtractor;
    private final int targetTintIndex;

    public DynamicKeyColor(Map<K, Integer> colorMap, Function<String, K> keyExtractor) {
        this(colorMap, keyExtractor, 0);
    }

    public DynamicKeyColor(
        Map<K, Integer> colorMap,
        Function<String, K> keyExtractor,
        int targetTintIndex
    ) {
        this.colorMap = colorMap;
        this.keyExtractor = keyExtractor;
        this.targetTintIndex = targetTintIndex;
    }

    /**
     * Creates a color handler that extracts the tier from the registry name.
     * <p>
     * Examples:
     * cable1_block   -> 1
     * cable2_block   -> 2
     * battery1_block -> 1
     */
    public static DynamicKeyColor<Integer> byTier(Map<Integer, Integer> map) {
        return new DynamicKeyColor<>(map, Utility::getTier);
    }

    /**
     * Creates a color handler that uses the complete registry path
     * as the lookup key.
     * <p>
     * Example:
     * bronze_block -> "bronze_block"
     */
    public static DynamicKeyColor<String> byName(Map<String, Integer> map) {
        return new DynamicKeyColor<>(map, Function.identity());
    }

    @Override
    public int getColor(
        BlockState state,
        @Nullable BlockAndTintGetter level,
        @Nullable BlockPos pos,
        int tintIndex
    ) {
        if (tintIndex != this.targetTintIndex) {
            return -1;
        }

        ResourceLocation id = ForgeRegistries.BLOCKS.getKey(state.getBlock());

        if (id == null) {
            return -1;
        }

        return resolveColor(id.getPath());
    }

    @Override
    public int getColor(ItemStack stack, int tintIndex) {
        if (tintIndex != this.targetTintIndex) {
            return -1;
        }

        ResourceLocation id = ForgeRegistries.ITEMS.getKey(stack.getItem());

        if (id == null) {
            return -1;
        }

        return resolveColor(id.getPath());
    }

    private int resolveColor(String name) {
        try {
            K key = this.keyExtractor.apply(name);

            if (key == null) {
                return -1;
            }

            return this.colorMap.getOrDefault(key, -1);
        } catch (Exception e) {
            return -1;
        }
    }
}