package net.dafarka.metallurgyplus.block.entity.multiblock;

import net.dafarka.metallurgyplus.energy.BigEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.math.BigInteger;
import java.util.function.Supplier;

public class MBReactorController extends MBControllerBlockEntity {
    public MBReactorController(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        this(
            type,
            pos,
            state,
            () -> new BigEnergyStorage(BigInteger.ZERO, BigInteger.ZERO, BigInteger.ZERO)
        );
    }

    public MBReactorController(BlockEntityType<?> type, BlockPos pos, BlockState state, Supplier<BigEnergyStorage> storageSupplier) {
        super(type, pos, state, storageSupplier);
    }

    @Override
    protected MBStructureDefinition getStructureDefinition() {
        // Keep this placeholder controller inactive until the reactor structure is implemented.
        return null;
    }
}
