package net.dafarka.metallurgyplus.screen;

import net.dafarka.metallurgyplus.block.base.MachineBlock;
import net.dafarka.metallurgyplus.network.ModMessages;
import net.dafarka.metallurgyplus.network.packets.SwitchMachineMenuPacket;
import net.dafarka.metallurgyplus.screen.menu.MachineMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;

public class MachineScreen extends BaseScreen<MachineMenu> {

    private final ResourceLocation texture;

    private final int[] progressPosition;

    public MachineScreen(
        MachineMenu pMenu,
        Inventory pPlayerInventory,
        Component pTitle
    ) {
        super(pMenu, pPlayerInventory, pTitle, pMenu.getTexture());

        this.texture = pMenu.getTexture();

        MachineBlock machineBlock = menu.getMachineBlock();

        this.progressPosition = machineBlock.getProgressPosition();
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        super.renderBg(pGuiGraphics, pPartialTick, pMouseX, pMouseY);

        int x = leftPos;
        int y = topPos;

        renderProgressArrow(pGuiGraphics, x, y);
        renderEnergyBar(pGuiGraphics, x, y);
        renderIOButton(pGuiGraphics, x, y);
    }

    private void renderProgressArrow(GuiGraphics guiGraphics, int x, int y) {
        if (menu.isCrafting()) {
            guiGraphics.blit(texture, x + progressPosition[0], y + progressPosition[1], 176, 0, menu.utilityMenu.getScaledProgress(), 8);
        }
    }

    private void renderEnergyBar(GuiGraphics guiGraphics, int x, int y) {
        guiGraphics.blit(texture, x + 8, y + 60, 176, 16, menu.utilityMenu.getScaledEnergy(), 13);
    }

    private void renderIOButton(GuiGraphics guiGraphics, int x, int y) {
        guiGraphics.blit(texture, x + 161, y + 3, 176, 32, 16, 16);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        super.render(guiGraphics, mouseX, mouseY, delta);

        if (isIoButtonHovered(mouseX, mouseY)) {
            guiGraphics.renderTooltip(font, Component.literal("Configure IO"), mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && isIoButtonHovered(mouseX, mouseY)) {
            ModMessages.sendToServer(new SwitchMachineMenuPacket(menu.blockEntity.getBlockPos(), true));
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private boolean isIoButtonHovered(double mouseX, double mouseY) {
        int buttonX = leftPos + 160;
        int buttonY = topPos;
        return mouseX >= buttonX && mouseX < buttonX + 16
            && mouseY >= buttonY && mouseY < buttonY + 16;
    }
}
