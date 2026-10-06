package net.dafarka.metallurgyplus.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.dafarka.metallurgyplus.MetallurgyPlus;
import net.dafarka.metallurgyplus.screen.menu.MBBatteryMenu;
import net.dafarka.metallurgyplus.util.Utility;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class MBBatteryScreen extends AbstractContainerScreen<MBBatteryMenu> {
    private static final ResourceLocation TEXTURE =
        new ResourceLocation(MetallurgyPlus.MODID, "textures/gui/battery_gui.png");

    public MBBatteryScreen(MBBatteryMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void init() {
        super.init();
        this.inventoryLabelY = 10000;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        graphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);
        graphics.blit(TEXTURE, x + 8, y + 61, 0, 176, menu.getScaledEnergy(), 13);

        graphics.drawString(font, "Stored: " + Utility.formatCompact(menu.getEnergyStored()) + " FE", x + 8, y + 21, 0x000000, false);
        graphics.drawString(font, "Capacity: " + Utility.formatCompact(menu.getMaxEnergy()) + " FE", x + 8, y + 34, 0x000000, false);

        boolean formed = menu.isFormed();
        graphics.drawString(
            font,
            formed ? "Structure formed" : "Structure incomplete",
            x + 8,
            y + 47,
            formed ? 0x007000 : 0x900000,
            false
        );
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
    }
}
