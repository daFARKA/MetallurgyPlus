package net.dafarka.metallurgyplus.command;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ExportedTintedTextureCommands {

    public static void execute() {
        Minecraft mc = Minecraft.getInstance();
        ItemStack stack = mc.player.getMainHandItem();

        if (stack.isEmpty()) {
            mc.player.displayClientMessage(
                Component.literal("Hold an item first!"), true
            );
            return;
        }

        try {
            String fileName = stack.getItem().builtInRegistryHolder().key().location().getPath();
            exportTintedTexture(stack, fileName);

            mc.player.displayClientMessage(
                Component.literal("Exported texture as " + fileName + ".png"), true
            );

        } catch (Exception ex) {
            mc.player.displayClientMessage(
                Component.literal("Failed: " + ex.getMessage()), true
            );
        }
    }

    private static void exportTintedTexture(ItemStack stack, String name) throws IOException {
        Minecraft mc = Minecraft.getInstance();

        var model = mc.getItemRenderer().getModel(stack, null, null, 0);
        TextureAtlasSprite sprite = model.getParticleIcon();

        NativeImage base = sprite.contents().getOriginalImage();
        int w = base.getWidth();
        int h = base.getHeight();

        NativeImage tinted = new NativeImage(w, h, true);

        int tint = mc.getItemColors().getColor(stack, 0);

        int tr = (tint >> 16) & 0xFF;
        int tg = (tint >> 8) & 0xFF;
        int tb = tint & 0xFF;

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {

                int pixel = base.getPixelRGBA(x, y);

                int a = (pixel >> 24) & 0xFF;
                int r = (pixel >> 16) & 0xFF;
                int g = (pixel >> 8) & 0xFF;
                int b = pixel & 0xFF;

                int r2 = (r * tr) / 255;
                int g2 = (g * tg) / 255;
                int b2 = (b * tb) / 255;

                tinted.setPixelRGBA(x, y, (a << 24) | (r2 << 16) | (g2 << 8) | b2);
            }
        }

        Path out = mc.gameDirectory.toPath()
            .resolve("metallurgyplus/exported_textures/" + name + ".png");

        Files.createDirectories(out.getParent());
        tinted.writeToFile(out);
    }

}
