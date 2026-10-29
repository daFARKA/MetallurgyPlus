package net.dafarka.metallurgyplus.screen;

import net.dafarka.metallurgyplus.MetallurgyPlus;
import net.dafarka.metallurgyplus.screen.menu.BatteryMenu;
import net.dafarka.metallurgyplus.util.Utility;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class BatteryScreen extends BaseScreen<BatteryMenu> {
    private static final ResourceLocation TEXTURE =
        new ResourceLocation(MetallurgyPlus.MODID, "textures/gui/battery_gui.png");

    public BatteryScreen(BatteryMenu pMenu, Inventory pPlayerInventory,
                         Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle, TEXTURE);
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        super.renderBg(pGuiGraphics, pPartialTick, pMouseX, pMouseY);

        int x = leftPos;
        int y = topPos;

        renderEnergyBar(pGuiGraphics, x, y);
        renderEnergyText(pGuiGraphics, x, y);
    }

    private void renderEnergyBar(GuiGraphics guiGraphics, int x, int y) {
        guiGraphics.blit(TEXTURE, x + 8, y + 61, 0, 176, menu.getScaledEnergy(), 13);
    }

    private void renderEnergyText(GuiGraphics guiGraphics, int x, int y) {
        int energy = menu.getEnergyStored();
        int maxEnergy = menu.getMaxEnergy();

        String energyText = Utility.formatWithSeparator(energy, ',') + " / " + Utility.formatWithSeparator(maxEnergy, ',') + " FE";

        guiGraphics.drawString(font, energyText, x + 10, y + 20, 0x000000, false);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        super.render(guiGraphics, mouseX, mouseY, delta);
    }
}
