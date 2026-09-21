package net.dafarka.metallurgyplus.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.dafarka.metallurgyplus.block.custom.MachineBlock;
import net.dafarka.metallurgyplus.screen.menu.MachineMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class MachineScreen extends AbstractContainerScreen<MachineMenu> {

    private final ResourceLocation texture;

    private final int[] progressPosition;
    private final int[] energyPosition;

    public MachineScreen(
        MachineMenu pMenu,
        Inventory pPlayerInventory,
        Component pTitle
    ) {
        super(pMenu, pPlayerInventory, pTitle);

        this.texture = pMenu.getTexture();

        MachineBlock machineBlock = menu.getMachineBlock();

        this.progressPosition = machineBlock.getProgressPosition();
        this.energyPosition = machineBlock.getEnergyPosition();
    }

    @Override
    protected void init() {
        super.init();
        this.inventoryLabelY = 10000;
        //this.titleLabelY = 10000;
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, texture);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        pGuiGraphics.blit(texture, x, y, 0, 0, imageWidth, imageHeight);

        renderProgressArrow(pGuiGraphics, x, y);
        renderEnergyBar(pGuiGraphics, x, y);
    }

    private void renderProgressArrow(GuiGraphics guiGraphics, int x, int y) {
        if (menu.isCrafting()) {
            guiGraphics.blit(texture, x + progressPosition[0], y + progressPosition[1], 176, 0, menu.utilityMenu.getScaledProgress(), 8);
        }
    }

    private void renderEnergyBar(GuiGraphics guiGraphics, int x, int y) {
        guiGraphics.blit(texture, x + energyPosition[0], y + energyPosition[1], 176, 16, menu.utilityMenu.getScaledEnergy(), 13);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        renderBg(guiGraphics, delta, mouseX, mouseY);
        super.render(guiGraphics, mouseX, mouseY, delta);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }
}
