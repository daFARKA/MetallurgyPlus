package net.dafarka.metallurgyplus.screen.menu;

import net.dafarka.metallurgyplus.block.ModBlocks;
import net.dafarka.metallurgyplus.block.entity.QuarryBlockEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.SlotItemHandler;

public class QuarryMenu extends AbstractContainerMenu {
    public static final int[][] OUTPUT_POSITIONS = {{116, 18}, {134, 18}, {152, 18}, {116, 36}, {134, 36}, {152, 36}, {116, 54}, {134, 54}, {152, 54}};
    public static final int QUARRY_SLOTS_COUNT = OUTPUT_POSITIONS.length;

    public UtilityMenu utilityMenu;

    private final QuarryBlockEntity blockEntity;
    private final Level level;
    private final ContainerData data;

    public QuarryMenu(int id, Inventory inv, FriendlyByteBuf buf) {
        this(id, inv, inv.player.level().getBlockEntity(buf.readBlockPos()), new SimpleContainerData(11));
    }

    public QuarryMenu(int id, Inventory inv, BlockEntity entity, ContainerData data) {
        super(ModMenuTypes.QUARRY_MENU.get(), id);
        checkContainerSize(inv, QUARRY_SLOTS_COUNT);
        this.blockEntity = (QuarryBlockEntity) entity;
        this.data = data;
        this.level = inv.player.level();
        this.utilityMenu = new UtilityMenu(this.data);

        addPlayerInventory(inv);
        addPlayerHotbar(inv);

        this.blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(iItemHandler -> {
            for (int i = 0; i < OUTPUT_POSITIONS.length; i++) {
                this.addSlot(new SlotItemHandler(iItemHandler, i, OUTPUT_POSITIONS[i][0], OUTPUT_POSITIONS[i][1]));
            }
        });

        addDataSlots(data);
    }

    public ContainerData getData() {
        return this.data;
    }

    public QuarryBlockEntity getBlockEntity() {
        return blockEntity;
    }

    public BlockPos getBlockEntityPos() {
        return blockEntity.getBlockPos();
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), pPlayer, ModBlocks.QUARRY.get());
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
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
