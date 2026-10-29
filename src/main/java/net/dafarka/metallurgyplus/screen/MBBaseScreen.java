package net.dafarka.metallurgyplus.screen;

import net.dafarka.metallurgyplus.screen.menu.MBBaseMenu;
import net.dafarka.metallurgyplus.util.Utility;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public abstract class MBBaseScreen<T extends MBBaseMenu> extends BaseScreen<T> {

    private final ResourceLocation texture;

    public MBBaseScreen(T menu, Inventory pPlayerInventory, Component pTitle, ResourceLocation texture) {
        super(menu, pPlayerInventory, pTitle, texture);

        this.texture = texture;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float pPartialTick, int pMouseX, int pMouseY) {
        super.renderBg(graphics, pPartialTick, pMouseX, pMouseY);

        int x = leftPos;
        int y = topPos;

    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);

        int x = leftPos;
        int y = topPos;

        renderFormedSymbol(graphics, x, y);

        if (Utility.isAreaHovered(mouseX, mouseY, leftPos, topPos, 160, 3, 13)) {
            boolean formed = menu.isFormed();
            String text = formed ? "Structure formed" : "Structure incomplete";
            graphics.renderTooltip(
                font,
                Component.literal(text),
                mouseX,
                mouseY
            );
        }
    }

    private void renderFormedSymbol(GuiGraphics graphics, int x, int y) {
        boolean formed = menu.isFormed();
        int u = !formed ? 176 : 192;
        graphics.blit(texture, x + 160, y + 3, u, 0, 13, 13);
    }
}
