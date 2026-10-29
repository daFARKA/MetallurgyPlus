package net.dafarka.metallurgyplus.screen;

import net.dafarka.metallurgyplus.MetallurgyPlus;
import net.dafarka.metallurgyplus.block.BlockFace;
import net.dafarka.metallurgyplus.block.ItemSideMode;
import net.dafarka.metallurgyplus.network.ModMessages;
import net.dafarka.metallurgyplus.network.packets.SetMachineIOModePacket;
import net.dafarka.metallurgyplus.network.packets.SwitchMachineMenuPacket;
import net.dafarka.metallurgyplus.network.packets.ToggleMachineAutoIOPacket;
import net.dafarka.metallurgyplus.screen.menu.MachineIOMenu;
import net.dafarka.metallurgyplus.util.Utility;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;

import java.util.Arrays;
import java.util.EnumMap;

public class MachineIOScreen extends BaseScreen<MachineIOMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(MetallurgyPlus.MODID, "textures/gui/machine_io_gui.png");

    private static final int[][] FACE_POSITIONS = {
        {79, 16}, // Top
        {79, 52}, // Bottom
        {79, 34}, // Front
        {61, 52}, // Back
        {61, 34}, // Left
        {97, 34}  // Right
    };

    private static boolean notColorBlind = true;

    private final EnumMap<BlockFace, ItemSideMode> pendingModes = new EnumMap<>(BlockFace.class);

    public MachineIOScreen(MachineIOMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, TEXTURE);
        imageWidth = 176;
        imageHeight = 166;
        inventoryLabelY = 72;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        super.renderBg(graphics, partialTick, mouseX, mouseY);

        int x = leftPos;
        int y = topPos;

        for (BlockFace face : BlockFace.values()) {
            int[] facePosition = FACE_POSITIONS[face.ordinal()];
            graphics.blit(
                TEXTURE,
                x + facePosition[0] + 1,
                y + facePosition[1] + 1,
                getModeTextureU(getDisplayedMode(face)),
                getModeTextureV(),
                16,
                16
            );
        }

        graphics.blit(
            TEXTURE,
            x + 134,
            y + 53,
            getAutoTextureU(true),
            getAutoTextureV(menu.isAutoInputEnabled()),
            16,
            16
        );

        graphics.blit(
            TEXTURE,
            x + 152,
            y + 53,
            getAutoTextureU(false),
            getAutoTextureV(menu.isAutoOutputEnabled()),
            16,
            16
        );

        graphics.blit(menu.getMachineTexture(), x + 161, y + 3, 176, 32, 16, 16);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);

        BlockFace hoveredFace = getHoveredFace(mouseX, mouseY);
        if (hoveredFace != null) {
            ItemSideMode mode = getDisplayedMode(hoveredFace);
            graphics.renderTooltip(
                font,
                Component.literal(getFaceName(hoveredFace) + ": " + getModeName(mode)),
                mouseX,
                mouseY
            );
        } else {
            if (Utility.isAreaHovered(mouseX, mouseY, leftPos, topPos, 133, 52, 18)) {
                String text = menu.isAutoInputEnabled() ? "Disable" : "Enable";
                graphics.renderTooltip(
                    font,
                    Component.literal(text + " Auto Pull"),
                    mouseX,
                    mouseY
                );
            } else if (Utility.isAreaHovered(mouseX, mouseY, leftPos, topPos, 151, 52, 18)) {
                String text = menu.isAutoOutputEnabled() ? "Disable" : "Enable";
                graphics.renderTooltip(
                    font,
                    Component.literal(text + " Auto Push"),
                    mouseX,
                    mouseY
                );
            } else if (Utility.isAreaHovered(mouseX, mouseY, leftPos, topPos, 161, 3, 12)) {
                graphics.renderTooltip(
                    font,
                    Component.literal("Return"),
                    mouseX,
                    mouseY
                );
            } else if (Utility.isAreaHovered(mouseX, mouseY, leftPos, topPos, 161, 19, 12)) {
                graphics.renderTooltip(
                    font,
                    Component.literal("Set all sides to blocked"),
                    mouseX,
                    mouseY
                );
            } else if (Utility.isAreaHovered(mouseX, mouseY, leftPos, topPos, 161, 35, 12)) {
                graphics.renderTooltip(
                    font,
                    Component.literal("Toggle Colorblind Mode"),
                    mouseX,
                    mouseY
                );
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && Utility.isAreaHovered(mouseX, mouseY, leftPos, topPos, 161, 3, 12)) {
            ModMessages.sendToServer(new SwitchMachineMenuPacket(menu.getBlockEntityPos(), false));
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            return true;
        }

        if (Utility.isAreaHovered(mouseX, mouseY, leftPos, topPos, 161, 19, 12)) {
            if (button == 0) {
                for (BlockFace face : BlockFace.values()) {
                    ModMessages.sendToServer(new SetMachineIOModePacket(face, ItemSideMode.DISABLED));
                }
            } else if (button == 1) {
                for (BlockFace face : Arrays.stream(BlockFace.values()).filter(face -> face != BlockFace.BOTTOM).toList()) {
                    ModMessages.sendToServer(new SetMachineIOModePacket(face, ItemSideMode.INPUT));
                }
                ModMessages.sendToServer(new SetMachineIOModePacket(BlockFace.BOTTOM, ItemSideMode.OUTPUT));
            }

            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            return true;
        }

        if (Utility.isAreaHovered(mouseX, mouseY, leftPos, topPos, 161, 35, 12)) {
            if (button == 0) {
                notColorBlind = !notColorBlind;
            }

            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            return true;
        }

        if (Utility.isAreaHovered(mouseX, mouseY, leftPos, topPos, 133, 52, 18)) {
            if (button == 0) {
                ModMessages.sendToServer(new ToggleMachineAutoIOPacket(true));
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                return true;
            }
        }

        if (Utility.isAreaHovered(mouseX, mouseY, leftPos, topPos, 151, 52, 18)) {
            if (button == 0) {
                ModMessages.sendToServer(new ToggleMachineAutoIOPacket(false));
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                return true;
            }
        }

        if (button == 0) {
            BlockFace face = getHoveredFace(mouseX, mouseY);
            if (face != null) {
                ItemSideMode nextMode = getNextMode(getDisplayedMode(face));
                pendingModes.put(face, nextMode);
                ModMessages.sendToServer(new SetMachineIOModePacket(face, nextMode));
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                return true;
            }
        } else if (button == 1) {
            BlockFace face = getHoveredFace(mouseX, mouseY);
            if (face != null) {
                ModMessages.sendToServer(new SetMachineIOModePacket(face, ItemSideMode.DISABLED));
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private BlockFace getHoveredFace(double mouseX, double mouseY) {
        for (BlockFace face : BlockFace.values()) {
            int[] position = FACE_POSITIONS[face.ordinal()];
            if (Utility.isAreaHovered(mouseX, mouseY, leftPos, topPos, position[0], position[1], 18)) {
                return face;
            }
        }
        return null;
    }

    private static int getModeTextureU(ItemSideMode mode) {
        return switch (mode) {
            case DISABLED -> 176;
            case INPUT -> 192;
            case OUTPUT -> 208;
            case BOTH -> 224;
        };
    }

    private static int getAutoTextureU(boolean isInput) {
        return isInput ? 192 : 208;
    }

    private static int getTextureV(int pos) {
        return notColorBlind ? pos : pos + 16;
    }

    private static int getModeTextureV() {
        return getTextureV(0);
    }

    private static int getAutoTextureV(boolean isEnabled) {
        return !isEnabled ? getTextureV(0) : getTextureV(32);
    }

    private static ItemSideMode getNextMode(ItemSideMode mode) {
        return switch (mode) {
            case DISABLED -> ItemSideMode.INPUT;
            case INPUT -> ItemSideMode.OUTPUT;
            case OUTPUT -> ItemSideMode.BOTH;
            case BOTH -> ItemSideMode.DISABLED;
        };
    }

    private ItemSideMode getDisplayedMode(BlockFace face) {
        ItemSideMode serverMode = menu.getItemSideMode(face);
        ItemSideMode pendingMode = pendingModes.get(face);
        if (pendingMode == null) {
            return serverMode;
        }
        if (pendingMode == serverMode) {
            pendingModes.remove(face);
            return serverMode;
        }
        return pendingMode;
    }

    private void toggleAutoInput() {
        ModMessages.sendToServer(new ToggleMachineAutoIOPacket(true));
    }

    private void toggleAutoOutput() {
        ModMessages.sendToServer(new ToggleMachineAutoIOPacket(false));
    }

    private static String getFaceName(BlockFace face) {
        return switch (face) {
            case TOP -> "Top";
            case BOTTOM -> "Bottom";
            case FRONT -> "Front";
            case BACK -> "Back";
            case LEFT -> "Left";
            case RIGHT -> "Right";
        };
    }

    private static String getModeName(ItemSideMode mode) {
        return switch (mode) {
            case DISABLED -> "Blocked";
            case INPUT -> "In";
            case OUTPUT -> "Out";
            case BOTH -> "In & Out";
        };
    }
}
