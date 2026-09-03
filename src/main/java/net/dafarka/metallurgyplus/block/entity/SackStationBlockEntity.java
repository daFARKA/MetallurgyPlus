package net.dafarka.metallurgyplus.block.entity;

import net.dafarka.metallurgyplus.item.sack.SackItem;
import net.dafarka.metallurgyplus.item.sack.SackStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Map;

public class SackStationBlockEntity extends BlockEntity {

    protected final ContainerData data;

    private int generation = 0;
    private static final String SACK_TAG = "sack";
    private ItemStack sack = ItemStack.EMPTY;


    public SackStationBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.SACK_STATION_BE.get(), pPos, pBlockState);

        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                return switch (pIndex) {
                    default -> 0;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {
                switch (pIndex) {

                }
            }

            @Override
            public int getCount() {
                return 0;
            }
        };
    }

    public void tick(Level pLevel, BlockPos pPos, BlockState pState) {
        if (!(pLevel instanceof ServerLevel serverLevel) || sack.isEmpty()) return;
        if (!(sack.getItem() instanceof SackItem)) return;

        int tier = ((SackItem) sack.getItem()).getTier();
        int transferAmount = 1 << (tier - 1);

        pushBelowAmount(serverLevel, transferAmount);

        setChanged(pLevel, pPos, pState);
    }

    private void pushBelowAmount(ServerLevel level, int amount) {
        BlockEntity below = level.getBlockEntity(worldPosition.below());
        if (below == null) return;

        below.getCapability(ForgeCapabilities.ITEM_HANDLER, Direction.UP).ifPresent(handler -> {
            for (Map.Entry<Item, Integer> entry : SackStorage.getContents(sack).entrySet()) {
                int available = entry.getValue();
                int toTransfer = Math.min(amount, available);

                if (toTransfer <= 0) continue;

                ItemStack stack = new ItemStack(entry.getKey(), toTransfer);
                ItemStack remainder = ItemHandlerHelper.insertItem(handler, stack, false);

                int inserted = toTransfer - remainder.getCount();

                if (inserted > 0) {
                    SackStorage.remove(sack, entry.getKey(), inserted);
                    setChanged(level, worldPosition, getBlockState());
                    break;
                }
            }
        });
    }

    public void drops() {
        if (!sack.isEmpty()) {
            Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), sack);
        }
    }

    public ItemStack getSack() { return sack.copy(); }

    public void setSack(ItemStack newSack) {
        sack = newSack.isEmpty() ? ItemStack.EMPTY : newSack.copyWithCount(1);
        setChanged();
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {

        return super.getCapability(cap, side);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        if (!sack.isEmpty()) pTag.put(SACK_TAG, sack.save(new CompoundTag()));
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        sack = pTag.contains(SACK_TAG) ? ItemStack.of(pTag.getCompound(SACK_TAG)) : ItemStack.EMPTY;
    }
}
