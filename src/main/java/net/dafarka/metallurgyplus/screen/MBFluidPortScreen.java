package net.dafarka.metallurgyplus.screen;

import net.dafarka.metallurgyplus.MetallurgyPlus;
import net.dafarka.metallurgyplus.screen.menu.MBFluidPortMenu;
import net.dafarka.metallurgyplus.util.FluidGaugeRenderer;
import net.dafarka.metallurgyplus.util.Utility;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class MBFluidPortScreen extends BaseScreen<MBFluidPortMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(
        MetallurgyPlus.MODID,
        "textures/gui/fluid_port_gui.png"
    );

    public MBFluidPortScreen(MBFluidPortMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, TEXTURE);
        imageWidth = 176;
        imageHeight = 166;
    }

    @Override
    protected void init() {
        super.init();
        this.inventoryLabelY = 10000;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        super.renderBg(graphics, partialTick, mouseX, mouseY);

        int x = leftPos;
        int y = topPos;

        renderFluidBar(graphics, x, y);
        renderFluidText(graphics, x, y);
    }

    private void renderFluidBar(GuiGraphics guiGraphics, int x, int y) {
        FluidGaugeRenderer.render(guiGraphics, menu.getFluid(), menu.getFluidCapacity(), x + 152, y + 7, 16, 72);
    }

    private void renderFluidText(GuiGraphics guiGraphics, int x, int y) {
        int stored = menu.getFluidStored();
        int capacity = menu.getFluidCapacity();

        if (stored != 0) {
            String fluidNameText = menu.getFluid().getDisplayName().getString();
            guiGraphics.drawString(font, fluidNameText, x + 10, y + 25, 0x000000, false);
        }

        String fluidStoredText = Utility.formatWithSeparator(stored, ',') + " / " + Utility.formatWithSeparator(capacity, ',') + " mB";
        guiGraphics.drawString(font, fluidStoredText, x + 10, y + 40, 0x000000, false);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
    }
}
