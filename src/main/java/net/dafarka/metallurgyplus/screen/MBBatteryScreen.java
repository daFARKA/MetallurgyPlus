package net.dafarka.metallurgyplus.screen;

import net.dafarka.metallurgyplus.MetallurgyPlus;
import net.dafarka.metallurgyplus.screen.menu.MBBatteryMenu;
import net.dafarka.metallurgyplus.util.Utility;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class MBBatteryScreen extends BaseScreen<MBBatteryMenu> {
    private static final ResourceLocation TEXTURE =
        new ResourceLocation(MetallurgyPlus.MODID, "textures/gui/battery_gui.png");

    public MBBatteryScreen(MBBatteryMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, TEXTURE);
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
