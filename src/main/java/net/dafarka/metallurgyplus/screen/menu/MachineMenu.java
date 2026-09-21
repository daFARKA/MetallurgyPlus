package net.dafarka.metallurgyplus.screen.menu;

import net.dafarka.metallurgyplus.MetallurgyPlus;
import net.dafarka.metallurgyplus.block.custom.MachineBlock;
import net.dafarka.metallurgyplus.block.entity.MachineBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.SlotItemHandler;

public class MachineMenu extends AbstractContainerMenu {

    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
    private static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
    private static final int VANILLA_FIRST_SLOT_INDEX = 0;

    public final MachineBlockEntity blockEntity;
    private final Level level;
    private final ContainerData data;

    public UtilityMenu utilityMenu;

    public MachineMenu(int containerId, Inventory inventory, FriendlyByteBuf friendlyByteBuf) {
        this(containerId, inventory, inventory.player.level().getBlockEntity(friendlyByteBuf.readBlockPos()), new SimpleContainerData(19));
    }

    public MachineMenu(int containerId, Inventory inventory, BlockEntity entity, ContainerData data) {
        super(ModMenuTypes.MACHINE_MENU.get(), containerId);

        blockEntity = (MachineBlockEntity) entity;
        level = inventory.player.level();
        this.data = data;
        utilityMenu = new UtilityMenu(data);

        MachineBlock machineBlock = getMachineBlock();

        int machineSlotCount = machineBlock.getInputSlots() + machineBlock.getOutputSlots();

        checkContainerSize(inventory, machineSlotCount);

        addPlayerInventory(inventory);
        addPlayerHotbar(inventory);

        blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {

            int[][] inputPositions = machineBlock.getInputPositions();

            for (int i = 0; i < inputPositions.length; i++) {
                addSlot(new SlotItemHandler(
                    handler,
                    i,
                    inputPositions[i][0],
                    inputPositions[i][1]
                ));
            }

            int[][] outputPositions = machineBlock.getOutputPositions();

            for (int i = 0; i < outputPositions.length; i++) {
                addSlot(new SlotItemHandler(
                    handler,
                    inputPositions.length + i,
                    outputPositions[i][0],
                    outputPositions[i][1]
                ));
            }
        });

        addDataSlots(data);
    }

    public MachineBlock getMachineBlock() {
        return (MachineBlock) blockEntity.getBlockState().getBlock();
    }

    public ResourceLocation getTexture() {
        return new ResourceLocation(
            MetallurgyPlus.MODID,
            "textures/gui/" + blockEntity.getBlockState().getBlock()
                .builtInRegistryHolder()
                .key()
                .location()
                .getPath() + "_gui.png"
        );
    }

    public boolean isCrafting() {
        return data.get(0) > 0;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot sourceSlot = slots.get(index);

        if (sourceSlot == null || !sourceSlot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();

        int machineFirstSlot =
            VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;

        int machineSlotCount =
            getMachineBlock().getInputSlots()
                + getMachineBlock().getOutputSlots();

        if (index < machineFirstSlot) {
            if (!moveItemStackTo(
                sourceStack,
                machineFirstSlot,
                machineFirstSlot + machineSlotCount,
                false
            )) {
                return ItemStack.EMPTY;
            }
        } else if (index < machineFirstSlot + machineSlotCount) {
            if (!moveItemStackTo(
                sourceStack,
                VANILLA_FIRST_SLOT_INDEX,
                VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT,
                false
            )) {
                return ItemStack.EMPTY;
            }
        } else {
            return ItemStack.EMPTY;
        }

        if (sourceStack.isEmpty()) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }

        sourceSlot.onTake(player, sourceStack);

        return copyOfSourceStack;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(
            ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
            player,
            blockEntity.getBlockState().getBlock()
        );
    }

    private void addPlayerInventory(Inventory inventory) {
        for (int row = 0; row < 3; ++row) {
            for (int column = 0; column < 9; ++column) {
                addSlot(new Slot(inventory, column + row * 9 + 9, 8 + column * 18, 84 + row * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory inventory) {
        for (int i = 0; i < 9; ++i) {
            addSlot(new Slot(inventory, i, 8 + i * 18, 142));
        }
    }
}