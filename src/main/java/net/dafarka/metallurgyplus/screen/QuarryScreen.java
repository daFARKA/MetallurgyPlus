package net.dafarka.metallurgyplus.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.dafarka.metallurgyplus.MetallurgyPlus;
import net.dafarka.metallurgyplus.network.ModMessages;
import net.dafarka.metallurgyplus.network.packets.SetAreaQuarryPacket;
import net.dafarka.metallurgyplus.network.packets.StartQuarryPacket;
import net.dafarka.metallurgyplus.network.packets.StopQuarryPacket;
import net.dafarka.metallurgyplus.network.packets.ToggleQuarryPacket;
import net.dafarka.metallurgyplus.screen.menu.QuarryMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.Objects;

public class QuarryScreen extends AbstractContainerScreen<QuarryMenu> {
    private static final ResourceLocation TEXTURE =
        new ResourceLocation(MetallurgyPlus.MODID, "textures/gui/quarry_gui.png");

    private EditBox startX, startY, startZ;
    private EditBox endX, endY, endZ;
    private Button setAreaButton;
    private Button startButton;

    public QuarryScreen(QuarryMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void init() {
        super.init();
        int x = leftPos;
        int y = topPos;

        startX = new EditBox(font, x + 10, y + 20, 40, 16, Component.literal("X1"));
        startY = new EditBox(font, x + 55, y + 20, 40, 16, Component.literal("Y1"));
        startZ = new EditBox(font, x + 100, y + 20, 40, 16, Component.literal("Z1"));

        endX = new EditBox(font, x + 10, y + 45, 40, 16, Component.literal("X2"));
        endY = new EditBox(font, x + 55, y + 45, 40, 16, Component.literal("Y2"));
        endZ = new EditBox(font, x + 100, y + 45, 40, 16, Component.literal("Z2"));

        addRenderableWidget(startX);
        addRenderableWidget(startY);
        addRenderableWidget(startZ);
        addRenderableWidget(endX);
        addRenderableWidget(endY);
        addRenderableWidget(endZ);

        setAreaButton = addRenderableWidget(Button.builder(Component.literal("Set Area"), btn -> {
            sendSetAreaPacket();
        }).bounds(x + 10, y + 70, 60, 20).build());

        startButton = addRenderableWidget(
            Button.builder(getStartButtonLabel(), btn -> toggleQuarry())
                .bounds(x + 80, y + 70, 80, 20)
                .build()
        );

        startX.setValue(menu.getBlockEntity().getTempStartX() + "");
        startY.setValue(menu.getBlockEntity().getTempStartY() + "");
        startZ.setValue(menu.getBlockEntity().getTempStartZ() + "");
        endX.setValue(menu.getBlockEntity().getTempEndX() + "");
        endY.setValue(menu.getBlockEntity().getTempEndY() + "");
        endZ.setValue(menu.getBlockEntity().getTempEndZ() + "");
    }

    private void sendSetAreaPacket() {
        try {
            int x1 = Integer.parseInt(startX.getValue());
            int y1 = Integer.parseInt(startY.getValue());
            int z1 = Integer.parseInt(startZ.getValue());
            int x2 = Integer.parseInt(endX.getValue());
            int y2 = Integer.parseInt(endY.getValue());
            int z2 = Integer.parseInt(endZ.getValue());

            ModMessages.sendToServer(new SetAreaQuarryPacket(menu.getBlockEntityPos(),
                new net.minecraft.core.BlockPos(x1, y1, z1),
                new net.minecraft.core.BlockPos(x2, y2, z2)));

        } catch (NumberFormatException e) {
            Objects.requireNonNull(minecraft.player).sendSystemMessage(Component.literal("Invalid coordinates!"));
        }
    }

    private Component getStartButtonLabel() {
        boolean currentlyRunning = menu.getBlockEntity().isRunning();
        MetallurgyPlus.LOGGER.info(String.valueOf(currentlyRunning));
        return Component.literal(currentlyRunning ? "Stop Quarry" : "Start Quarry");
    }

    private void toggleQuarry() {
        ModMessages.sendToServer(new ToggleQuarryPacket(menu.getBlockEntityPos()));
        startButton.setMessage(getStartButtonLabel());
    }

    @Override
    public void containerTick() {
        super.containerTick();

        var data = menu.getData();
        boolean locked = data.get(4) == 1;

        try {
            menu.getBlockEntity().setTempStart(
                Integer.parseInt(startX.getValue()),
                Integer.parseInt(startY.getValue()),
                Integer.parseInt(startZ.getValue())
            );
            menu.getBlockEntity().setTempEnd(
                Integer.parseInt(endX.getValue()),
                Integer.parseInt(endY.getValue()),
                Integer.parseInt(endZ.getValue())
            );
        } catch (NumberFormatException ignored) {}

        startX.setEditable(!locked);
        startY.setEditable(!locked);
        startZ.setEditable(!locked);
        endX.setEditable(!locked);
        endY.setEditable(!locked);
        endZ.setEditable(!locked);
        setAreaButton.active = !locked;

        if (locked) {
            startX.setValue(String.valueOf(data.get(5)));
            startY.setValue(String.valueOf(data.get(6)));
            startZ.setValue(String.valueOf(data.get(7)));
            endX.setValue(String.valueOf(data.get(8)));
            endY.setValue(String.valueOf(data.get(9)));
            endZ.setValue(String.valueOf(data.get(10)));
        }
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        pGuiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        renderEnergyBar(pGuiGraphics, x, y);
    }

    private void renderEnergyBar(GuiGraphics guiGraphics, int x, int y) {
        guiGraphics.blit(TEXTURE, x + 8, y + 65, 176, 16, menu.utilityMenu.getScaledEnergy(), 13);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        renderBg(guiGraphics, delta, mouseX, mouseY);
        super.render(guiGraphics, mouseX, mouseY, delta);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }
}
