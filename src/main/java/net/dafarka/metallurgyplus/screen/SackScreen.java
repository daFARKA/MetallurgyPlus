package net.dafarka.metallurgyplus.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.dafarka.metallurgyplus.MetallurgyPlus;
import net.dafarka.metallurgyplus.screen.menu.SackMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class SackScreen extends AbstractContainerScreen<SackMenu> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(MetallurgyPlus.MODID, "textures/gui/sack_gui.png");

    private static final int COLUMNS = 7;
    private static final int ROWS = 4;
    private static final int ITEMS_PER_PAGE = 28;

    private static final int ITEMS_X = 26;
    private static final int ITEMS_Y = 8;

    private int currentPage = 0;

    private static final int ITEM_SPACING = 18;

    private static final int LEFT_ARROW_X = 7;
    private static final int LEFT_ARROW_Y = 39;
    private static final int LEFT_ARROW_WIDTH = 13;
    private static final int LEFT_ARROW_HEIGHT = 7;

    private static final int RIGHT_ARROW_X = 155;
    private static final int RIGHT_ARROW_Y = 39;
    private static final int RIGHT_ARROW_WIDTH = 13;
    private static final int RIGHT_ARROW_HEIGHT = 7;


    public SackScreen(SackMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected void init() {
        super.init();

        currentPage = 0;

        this.inventoryLabelY = 10000;
        this.titleLabelY = 10000;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBg(guiGraphics, partialTick, mouseX, mouseY);
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        renderItems(guiGraphics, mouseX, mouseY);

        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    private void renderItems(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        List<Item> items = menu.getSackItems();

        int startIndex = currentPage * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, items.size());

        for (int index = startIndex; index < endIndex; index++) {
            int localIndex = index - startIndex;

            int column = localIndex % COLUMNS;
            int row = localIndex / COLUMNS;

            int x = leftPos + ITEMS_X + column * ITEM_SPACING;

            int y = topPos + ITEMS_Y + row * ITEM_SPACING;

            Item item = items.get(index);

            ItemStack stack = new ItemStack(item);

            guiGraphics.renderItem(stack, x, y);

            int amount = menu.getAmount(item);
            String amountText = formatNumber(amount);

            guiGraphics.pose().pushPose();

            guiGraphics.pose().translate(0, 0, 200);

            float scale = 0.5F;
            guiGraphics.pose().scale(scale, scale, 1.0F);

            float textX = (x + 16 - font.width(amountText) * scale) / scale;
            float textY = (y + 12) / scale;

            guiGraphics.drawString(font, amountText, textX, textY, 0xFFFFFF, true);

            guiGraphics.pose().popPose();

            if (isMouseOverItem(mouseX, mouseY, x, y)) {
                guiGraphics.renderTooltip(font, stack, mouseX, mouseY);

                hoverEffect(guiGraphics, x, y);
            }
        }
    }

    private boolean isMouseOverItem(double mouseX, double mouseY, int x, int y) {
        return mouseX >= x && mouseX < x + 16 && mouseY >= y && mouseY < y + 16;
    }

    private void hoverEffect(GuiGraphics guiGraphics, int x, int y) {
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0, 0, 300);

        guiGraphics.fill(x, y, x + 16, y + 16, 0x55FFFFFF);

        guiGraphics.pose().popPose();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isInside(mouseX, mouseY, LEFT_ARROW_X, LEFT_ARROW_Y, LEFT_ARROW_WIDTH, LEFT_ARROW_HEIGHT)) {
            if (currentPage > 0) {
                currentPage--;
            }

            return true;
        }

        if (isInside(mouseX, mouseY, RIGHT_ARROW_X, RIGHT_ARROW_Y, RIGHT_ARROW_WIDTH, RIGHT_ARROW_HEIGHT)) {
            List<Item> items = menu.getSackItems();

            int pageCount = getPageCount(items);

            if (currentPage < pageCount - 1) {
                currentPage++;
            }

            return true;
        }

        List<Item> items = menu.getSackItems();

        int startIndex = currentPage * ITEMS_PER_PAGE;
        int endIndex = Math.min(
                startIndex + ITEMS_PER_PAGE,
                items.size()
        );

        for (int index = startIndex; index < endIndex; index++) {

            int localIndex = index - startIndex;

            int column = localIndex % COLUMNS;
            int row = localIndex / COLUMNS;

            int x = leftPos + ITEMS_X + column * ITEM_SPACING;

            int y = topPos + ITEMS_Y + row * ITEM_SPACING;

            if (isMouseOverItem(mouseX, mouseY, x, y)) {

                boolean shift = hasShiftDown();

                int action;

                if (shift) {
                    action = button == 0 ? 2 : 3;
                } else {
                    action = button == 0 ? 0 : 1;
                }

                int buttonId = index * 4 + action;

                Minecraft.getInstance()
                        .gameMode
                        .handleInventoryButtonClick(
                                menu.containerId,
                                buttonId
                        );

                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private boolean isInside(double mouseX, double mouseY, int x, int y, int width, int height) {
        return mouseX >= leftPos + x
                && mouseX < leftPos + x + width
                && mouseY >= topPos + y
                && mouseY < topPos + y + height;
    }

    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        List<Item> items = menu.getSackItems();

        int startIndex = currentPage * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, items.size());

        for (int index = startIndex; index < endIndex; index++) {
            int localIndex = index - startIndex;

            int column = localIndex % COLUMNS;
            int row = localIndex / COLUMNS;

            int x = leftPos
                    + ITEMS_X
                    + column * ITEM_SPACING;

            int y = topPos
                    + ITEMS_Y
                    + row * ITEM_SPACING;

            if (isMouseOverItem(mouseX, mouseY, x, y)) {
                Item item = items.get(index);

                ItemStack stack = new ItemStack(item);

                guiGraphics.renderTooltip(font, stack, mouseX, mouseY);

                return;
            }
        }

        super.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    private int getPageCount(List<Item> items) {
        return Math.max(1, (int) Math.ceil((double) items.size() / ITEMS_PER_PAGE));
    }

    private String formatNumber(int number) {
        if (number >= 1_000_000_000) {
            return String.format("%.1fB", number / 1_000_000_000.0);
        }

        if (number >= 1_000_000) {
            return String.format("%.1fM", number / 1_000_000.0);
        }

        if (number >= 1_000) {
            return String.format("%.1fK", number / 1_000.0);
        }

        return Integer.toString(number);
    }
}