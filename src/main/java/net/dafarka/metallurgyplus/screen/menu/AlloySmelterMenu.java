package net.dafarka.metallurgyplus.screen.menu;

import net.dafarka.metallurgyplus.MetallurgyPlus;
import net.dafarka.metallurgyplus.block.ModBlocks;
import net.dafarka.metallurgyplus.block.entity.AlloySmelterBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.SlotItemHandler;

public class AlloySmelterMenu extends AbstractContainerMenu {
    public static final int[][] INPUT_POSITIONS = {{8, 26}, {26, 26}, {8, 44}, {26, 44}, {50, 26}, {68, 26}, {50, 44}, {68, 44}};
    public static final int[][] OUTPUT_POSITIONS = {{116, 18}, {134, 18}, {152, 18}, {116, 36}, {134, 36}, {152, 36}, {116, 54}, {134, 54}, {152, 54}};
    public static final int ALLOY_SMELTER_SLOTS_COUNT = INPUT_POSITIONS.length + OUTPUT_POSITIONS.length;

    public UtilityMenu utilityMenu;

    public final AlloySmelterBlockEntity blockEntity;
    private final Level level;
    private final ContainerData data;
    private ItemStack lastMovedStack = ItemStack.EMPTY;
    private int lastSlotBundleTouched = 1;

    public AlloySmelterMenu(int pContainerId, Inventory inv, FriendlyByteBuf friendlyByteBuf) {
        this(pContainerId, inv, inv.player.level().getBlockEntity(friendlyByteBuf.readBlockPos()), new SimpleContainerData(19));
    }

    public AlloySmelterMenu(int pContainerId, Inventory inv, BlockEntity entity, ContainerData data) {
        super(ModMenuTypes.ALLOY_SMELTER_MENU.get(), pContainerId);
        checkContainerSize(inv, ALLOY_SMELTER_SLOTS_COUNT);
        blockEntity = ((AlloySmelterBlockEntity) entity);
        this.level = inv.player.level();
        this.data = data;
        this.utilityMenu = new UtilityMenu(this.data);

        addPlayerInventory(inv);
        addPlayerHotbar(inv);

        this.blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(iItemHandler -> {
            for (int i = 0; i < INPUT_POSITIONS.length; i++) {
                this.addSlot(new SlotItemHandler(iItemHandler, i, INPUT_POSITIONS[i][0], INPUT_POSITIONS[i][1]));
            }

            for (int i = 0; i < OUTPUT_POSITIONS.length; i++) {
                this.addSlot(new SlotItemHandler(iItemHandler, i + INPUT_POSITIONS.length, OUTPUT_POSITIONS[i][0], OUTPUT_POSITIONS[i][1]));
            }
        });

        addDataSlots(data);
    }

    public boolean isCrafting() {
        return data.get(0) > 0;
    }

    // CREDIT GOES TO: diesieben07 | https://github.com/diesieben07/SevenCommons
    // Adapted by dafarka to fit the Alloy Smelter of MetallurgyPlus.
    // must assign a slot number to each of the slots used by the GUI.
    // For this container, we can see both the tile inventory's slots as well as the player inventory slots and the hotbar.
    // Each time we add a Slot to the container, it automatically increases the slotIndex, which means
    //  0 - 8 = hotbar slots (which will map to the InventoryPlayer slot numbers 0 - 8)
    //  9 - 35 = player inventory slots (which map to the InventoryPlayer slot numbers 9 - 35)
    //  36 - 44 = TileInventory slots, which map to our TileEntity slot numbers 0 - 8)
    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
    private static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
    private static final int VANILLA_FIRST_SLOT_INDEX = 0;
    private static final int TE_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;

    // THIS YOU HAVE TO DEFINE!
    private static final int TE_INVENTORY_SLOT_COUNT = ALLOY_SMELTER_SLOTS_COUNT;  // must be the number of slots you have!
    @Override
    public ItemStack quickMoveStack(Player playerIn, int pIndex) {
        Slot sourceSlot = slots.get(pIndex);
        if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;  //EMPTY_ITEM
        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copySourceStack = sourceStack.copy(); // Make a copy for later comparison

        // Check if the slot clicked is one of the vanilla container slots
        if (pIndex < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT) {
            // This is a vanilla container slot so merge the stack into the tile inventory
            if (ItemStack.isSameItemSameTags(sourceStack, lastMovedStack)) {
                // If items are equal insert into the same slotbundle
                if (lastSlotBundleTouched == 1) {
                    if (moveItemStackTo(sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX + 4,
                        TE_INVENTORY_FIRST_SLOT_INDEX + 8, false)) {
                        lastSlotBundleTouched = 1;
                    } else {
                        return ItemStack.EMPTY;
                    }
                } else {
                    if (moveItemStackTo(sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX,
                        TE_INVENTORY_FIRST_SLOT_INDEX + 4, false)) {
                        lastSlotBundleTouched = 2;
                    } else {
                        return ItemStack.EMPTY;
                    }
                }
            } else {
                // else insert into the other slotbundle
                if (lastSlotBundleTouched == 1) {
                    if (moveItemStackTo(sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX,
                        TE_INVENTORY_FIRST_SLOT_INDEX + 4, false)) {
                        lastSlotBundleTouched = 2;
                    } else {
                        return ItemStack.EMPTY;
                    }
                } else {
                    if (moveItemStackTo(sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX + 4,
                        TE_INVENTORY_FIRST_SLOT_INDEX + 8, false)) {
                        lastSlotBundleTouched = 1;
                    } else {
                        return ItemStack.EMPTY;
                    }
                }
            }


        } else if (pIndex < TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT) {
            // This is a TE slot so merge the stack into the players inventory
            if (!moveItemStackTo(sourceStack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            System.out.println("Invalid slotIndex:" + pIndex);
            return ItemStack.EMPTY;
        }
        // If stack size == 0 (the entire stack was moved) set slot contents to null
        if (sourceStack.isEmpty()) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }
        sourceSlot.onTake(playerIn, sourceStack);
        this.lastMovedStack = copySourceStack;
        return lastMovedStack;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
            pPlayer, ModBlocks.ALLOY_SMELTER.get());
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 84 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }
}
