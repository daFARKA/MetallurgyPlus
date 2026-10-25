package net.dafarka.metallurgyplus.block.entity.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

/**
 * Energy ports resolve the energy capability on the outside of the formed structure. The
 * controller still coordinates transfers against its shared storage.
 */
public class MBEnergyPortBlockEntity extends MBPortBlockEntity {
    public MBEnergyPortBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    /**
     * Gets the energy storage on the outside of this port. {@code outward} points from the
     * multiblock toward the neighboring block.
     */
    @Nullable
    public IEnergyStorage getExternalEnergyStorage(Direction outward) {
        if (level == null || outward == null) {
            return null;
        }

        BlockEntity neighbor = level.getBlockEntity(worldPosition.relative(outward));
        return neighbor == null
            ? null
            : neighbor.getCapability(ForgeCapabilities.ENERGY, outward.getOpposite()).orElse(null);
    }
}
