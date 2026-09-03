package net.dafarka.metallurgyplus.screen.menu;

import net.dafarka.metallurgyplus.item.sack.SackItem;
import net.dafarka.metallurgyplus.item.sack.SackStorage;
import net.dafarka.metallurgyplus.util.ModTags;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class SackMenu extends AbstractContainerMenu {

    private final Inventory playerInventory;
    private final Player player;
    private final int sackSlot;
    private final boolean offhand;


    public SackMenu(int containerId, Inventory playerInventory, int sackSlot, boolean offhand) {
        super(ModMenuTypes.SACK_MENU.get(), containerId);

        this.playerInventory = playerInventory;
        this.player = playerInventory.player;
        this.sackSlot = sackSlot;
        this.offhand = offhand;

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);
    }

    /**
     * Client-side constructor.
     * <p>
     * The client doesn't need the actual sack to be authoritative.
     * It only needs enough information to display it.
     */
    public SackMenu(int containerId, Inventory playerInventory, FriendlyByteBuf data) {
        this(
            containerId,
            playerInventory,
            data.readInt(),
            data.readBoolean()
        );
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

    /**
     * Returns the actual sack ItemStack from the player's inventory.
     * <p>
     * This is deliberately resolved every time instead of keeping a
     * separate ItemStack reference, so the server works with the
     * authoritative inventory.
     */
    public ItemStack getSack() {
        if (offhand) {
            return playerInventory.offhand.get(0);
        }

        if (sackSlot < 0 || sackSlot >= playerInventory.getContainerSize()) {
            return ItemStack.EMPTY;
        }

        return playerInventory.getItem(sackSlot);
    }

    public boolean isValidSack() {
        return SackStorage.isSack(getSack());
    }

    public List<Item> getSackItems() {
        List<Item> items = new ArrayList<>();

        var tag = BuiltInRegistries.ITEM.getTag(ModTags.Items.MATERIAL_RAW);

        if (tag.isEmpty()) {
            return items;
        }

        for (Holder<Item> holder : tag.get()) {
            items.add(holder.value());
        }

        return items;
    }

    public int getAmount(Item item) {
        return SackStorage.getAmount(getSack(), item);
    }

    public int getCapacity() {
        return SackStorage.getCapacity(getSack());
    }

    /**
     * Handles buttons sent from the client.
     */
    @Override
    public boolean clickMenuButton(Player player, int buttonId) {
        if (!isValidSack()) {
            return false;
        }

        List<Item> items = getSackItems();

        int itemIndex = buttonId / 4;
        int action = buttonId % 4;

        if (itemIndex < 0 || itemIndex >= items.size()) {
            return false;
        }

        Item item = items.get(itemIndex);

        /*
         * 0 = left
         * 1 = right
         * 2 = shift-left
         * 3 = shift-right
         */
        boolean shift = action >= 2;
        boolean takeOne = action == 1 || action == 3;

        int amount;

        if (takeOne) {
            amount = 1;
        } else {
            amount = SackStorage.getAmount(getSack(), item);
        }

        if (shift) {
            return takeItemToInventory(player, item, amount);
        } else {
            return handleItemCursor(player, item, amount);
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        if (index < 0 || index >= slots.size()) {
            return ItemStack.EMPTY;
        }

        Slot slot = slots.get(index);

        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack sourceStack = slot.getItem();

        if (!SackStorage.canStore(sourceStack)) {
            return ItemStack.EMPTY;
        }

        ItemStack originalStack = sourceStack.copy();

        ItemStack sack = getSack();

        int added = SackStorage.add(sack, sourceStack);

        if (added <= 0) {
            return ItemStack.EMPTY;
        }

        sourceStack.shrink(added);

        if (sourceStack.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.set(sourceStack);
        }

        slot.setChanged();

        return originalStack;
    }

    @Override
    public boolean stillValid(Player player) {
        if (!isValidSack()) {
            return false;
        }

        if (offhand) {
            return player.getOffhandItem() == getSack();
        }

        return player.getInventory()
            .getItem(sackSlot)
            .getItem()
            instanceof SackItem;
    }

    private boolean handleItemCursor(Player player, Item item, int amount) {
        if (player == null) {
            return false;
        }

        if (amount > 64) {
            amount = 64;
        }

        ItemStack sack = getSack();

        ItemStack cursor = getCarried();

        if (cursor.isEmpty() || amount == 1) {
            int removedAmount = SackStorage.remove(sack, item, amount);

            int space = cursor.getMaxStackSize() - cursor.getCount();
            int amountToRemove = removedAmount + cursor.getCount();
            int actualAmount = Math.min(space, amountToRemove);

            ItemStack result = new ItemStack(item, actualAmount);

            setCarried(result);

            return true;
        } else {
            if (!ItemStack.isSameItemSameTags(cursor, item.getDefaultInstance())) {
                return false;
            }

            int added = SackStorage.add(sack, cursor);

            cursor.shrink(added);
            setCarried(cursor);

            return true;
        }
    }

    private boolean takeItemToInventory(Player player, Item item, int amount) {
        ItemStack sack = getSack();

        if (!SackStorage.isSack(sack)) {
            return false;
        }

        int available = SackStorage.getAmount(sack, item);

        amount = Math.min(amount, available);

        if (amount <= 0) {
            return false;
        }

        ItemStack extracted = new ItemStack(item, amount);

        boolean moved = moveItemStackTo(extracted, 0, 36, false);

        if (!moved) {
            return false;
        }

        int inserted = amount - extracted.getCount();

        if (inserted <= 0) {
            return false;
        }

        SackStorage.remove(sack, item, inserted);

        return true;
    }
}
