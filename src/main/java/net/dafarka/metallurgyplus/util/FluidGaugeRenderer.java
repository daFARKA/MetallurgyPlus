package net.dafarka.metallurgyplus.util;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;

public class FluidGaugeRenderer {

    public static void render(
        GuiGraphics guiGraphics,
        FluidStack fluid,
        int capacity,
        int x,
        int y,
        int width,
        int height
    ) {
        if (fluid.isEmpty() || capacity <= 0 || width <= 0 || height <= 0) {
            return;
        }

        float fillRatio = Math.min(
            1.0F,
            (float) fluid.getAmount() / capacity
        );

        int fillHeight = Math.round(height * fillRatio);

        if (fillHeight <= 0) {
            return;
        }

        IClientFluidTypeExtensions extensions =
            IClientFluidTypeExtensions.of(fluid.getFluid());

        ResourceLocation texture = extensions.getStillTexture(fluid);

        if (texture == null) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();

        TextureAtlasSprite sprite =
            minecraft.getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                .apply(texture);

        int tint = extensions.getTintColor(fluid);

        float red = ((tint >> 16) & 0xFF) / 255.0F;
        float green = ((tint >> 8) & 0xFF) / 255.0F;
        float blue = (tint & 0xFF) / 255.0F;
        float alpha = ((tint >>> 24) & 0xFF) / 255.0F;

        RenderSystem.setShaderColor(red, green, blue, alpha);

        try {
            guiGraphics.enableScissor(
                x, y + height - fillHeight,
                x + width, y + height
            );

            int tileHeight = Math.max(1, sprite.contents().height());
            for (int offset = 0; offset < height; offset += tileHeight) {
                int tileY = y + height - offset - tileHeight;
                guiGraphics.blit(
                    x, tileY,
                    0,
                    width, tileHeight,
                    sprite
                );
            }
        } finally {
            guiGraphics.disableScissor();
            RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        }
    }
}
