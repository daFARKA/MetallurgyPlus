package net.dafarka.metallurgyplus.block.entity.multiblock;

import net.dafarka.metallurgyplus.block.ModBlocks;
import net.dafarka.metallurgyplus.energy.BigEnergyStorage;
import net.dafarka.metallurgyplus.util.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.math.BigInteger;
import java.util.function.Supplier;

public class MBBatteryController extends MBControllerBlockEntity {
    private static final String CELL_BLOCK_PREFIX = "battery/cell";
    private static final BigInteger CELL_BASE_CAPACITY = BigInteger.valueOf(2000000000);
    private static final BigInteger CELL_BASE_TRANSFER = BigInteger.valueOf(2000000000);

    public MBBatteryController(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        this(
            type,
            pos,
            state,
            () -> new BigEnergyStorage(BigInteger.ZERO, BigInteger.ZERO, BigInteger.ZERO)
        );
    }

    public MBBatteryController(BlockEntityType<?> type, BlockPos pos, BlockState state, Supplier<BigEnergyStorage> storageSupplier) {
        super(type, pos, state, storageSupplier);
    }

    @Override
    protected MBStructureDefinition getStructureDefinition() {
        return BatteryStructureDefinition.DEFINITION;
    }

    @Override
    protected void onStructureFormed(MBStructure structure) {
        BigInteger capacity = BigInteger.ZERO;
        BigInteger transfer = BigInteger.ZERO;

        if (level == null) return;

        for (BlockPos cellPos : structure.requiredInteriorPositions()) {
            int tier = getCellTier(level.getBlockState(cellPos));
            if (tier <= 0) {
                continue;
            }

            capacity = capacity.add(CELL_BASE_CAPACITY.multiply(BigInteger.TEN.pow(tier - 1)));
            transfer = transfer.add(CELL_BASE_TRANSFER.multiply(BigInteger.TEN.pow(tier - 1)));
        }

        if (energyStorage.setLimits(capacity, transfer, transfer)) {
            setChanged();
        }
    }

    @Override
    protected void onStructureInvalidated() {
        if (energyStorage.setLimits(BigInteger.ZERO, BigInteger.ZERO, BigInteger.ZERO)) {
            setChanged();
        }
    }

    private static int getCellTier(BlockState state) {
        String path = BuiltInRegistries.BLOCK.getKey(state.getBlock()).getPath();
        if (!path.startsWith(CELL_BLOCK_PREFIX)) {
            return 0;
        }

        try {
            return Integer.parseInt(path.substring(CELL_BLOCK_PREFIX.length()));
        } catch (NumberFormatException ignored) {
            return 0;
        }
    }

    private static final class BatteryStructureDefinition {
        private static final MBStructureDefinition DEFINITION = MBStructureDefinition.rectangularPrism(3, 16)
            .edges(state -> state.is(ModBlocks.MULTIBLOCKS_MAP.get("battery/casing").get()))
            .walls(state -> state.is(Blocks.GLASS)
                || state.is(ModBlocks.MULTIBLOCKS_MAP.get("battery/casing").get())
                || state.is(ModBlocks.MULTIBLOCKS_MAP.get("battery/input").get())
                || state.is(ModBlocks.MULTIBLOCKS_MAP.get("battery/output").get())
                || state.is(ModBlocks.MULTIBLOCKS_MAP.get("battery/controller").get()))
            .controller(state -> state.is(ModBlocks.MULTIBLOCKS_MAP.get("battery/controller").get()))
            .requireInterior(state -> state.is(ModTags.Blocks.BATTERY_CELLS), 1)
            .build();
    }
}
