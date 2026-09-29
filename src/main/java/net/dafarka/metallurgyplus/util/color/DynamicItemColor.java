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

public class DynamicItemColor implements ItemColor {


    public DynamicItemColor() { }

    @Override
    public int getColor(@NotNull ItemStack pStack, int pTintIndex) {
        if (pTintIndex != 0) {
            return -1;
        }

        boolean isBlock = pStack.getItem() instanceof BlockItem;

        ResourceLocation id = ForgeRegistries.ITEMS.getKey(pStack.getItem());
        if (id == null) return -1;
        String name = id.getPath();

        if (!isBlock) {
            IForgeBakedModel model = Minecraft.getInstance().getItemRenderer().getModel(pStack, null, null, 0);
            TextureAtlasSprite sprite = model.getParticleIcon(ModelData.EMPTY);
            ResourceLocation spriteLocation = sprite.contents().name();

            if (!spriteLocation.getPath().startsWith("item/base/")) return -1;

            if (ModItems.MATERIAL_COLOR_MAP.get(name) != null) {
                return ModItems.MATERIAL_COLOR_MAP.get(name);
            } else if (ModItems.ORE_COLOR_MAP.get(name) != null) {
                return ModItems.ORE_COLOR_MAP.get(name);
            } else if (ModItems.ALLOY_COLOR_MAP.get(name) != null) {
                return ModItems.ALLOY_COLOR_MAP.get(name);
            } else if (ModItems.VANILLA_MAP.get(name) != null) {
                return ModItems.VANILLA_COLOR_MAP.get(name);
            } else if (ModItems.GEM_MAP.get(name) != null) {
                return ModItems.GEM_COLOR_MAP.get(name);
            }
        } else {
            if (ModBlocks.MATERIAL_COLOR_MAP.get(name) != null) {
                return ModBlocks.MATERIAL_COLOR_MAP.get(name);
            } else if (ModBlocks.ORE_COLOR_MAP.get(name) != null) {
                return ModBlocks.ORE_COLOR_MAP.get(name);
            } else if (ModBlocks.ALLOY_COLOR_MAP.get(name) != null) {
                return ModBlocks.ALLOY_COLOR_MAP.get(name);
            } else if (ModBlocks.GEM_COLOR_MAP.get(name) != null) {
                return ModBlocks.GEM_COLOR_MAP.get(name);
            }
        }

        return -1;
    }
}