package net.dafarka.metallurgyplus.block.entity.multiblock;

import net.dafarka.metallurgyplus.block.ModBlocks;
import net.dafarka.metallurgyplus.energy.BigEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.math.BigInteger;
import java.util.function.Supplier;

public class MBReactorController extends MBControllerBlockEntity {

    private final MBStructureDefinition structureDefinition;

    public MBReactorController(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        this(
            type,
            pos,
            state,
            () -> new BigEnergyStorage(BigInteger.ZERO, BigInteger.ZERO, BigInteger.ZERO)
        );
    }

    public MBReactorController(BlockEntityType<?> type, BlockPos pos, BlockState state, Supplier<BigEnergyStorage> storageSupplier) {
        super(type, pos, state, storageSupplier, "reactor");

        structureDefinition = ReactorStructureDefinition.create(getControllerName(), getEnergyOutputPortName(),
            getItemInputPortName(), getItemOutputPortName(), getFluidInputPortName(), getFluidOutputPortName());
    }

    @Override
    protected MBStructureDefinition getStructureDefinition() {
        return structureDefinition;
    }

    private static final class ReactorStructureDefinition {
        private static MBStructureDefinition create(String controllerName, String energyOutputPortName,
                                                    String itemInputPortName, String itemOutputPortName,
                                                    String fluidInputPortName, String fluidOutputPortName) {
            return MBStructureDefinition.rectangularPrism(5, 32, 3, 64)
                .edges(state -> state.is(ModBlocks.MULTIBLOCKS_MAP.get("reactor/casing").get()))
                .walls(state -> state.is(Blocks.GLASS)
                    || state.is(ModBlocks.MULTIBLOCKS_MAP.get("reactor/casing").get())
                    || state.is(ModBlocks.MULTIBLOCKS_MAP.get("reactor/glass").get())
                    || state.is(ModBlocks.MULTIBLOCKS_MAP.get(energyOutputPortName).get())
                    || state.is(ModBlocks.MULTIBLOCKS_MAP.get(itemInputPortName).get())
                    || state.is(ModBlocks.MULTIBLOCKS_MAP.get(itemOutputPortName).get())
                    || state.is(ModBlocks.MULTIBLOCKS_MAP.get(fluidInputPortName).get())
                    || state.is(ModBlocks.MULTIBLOCKS_MAP.get(fluidOutputPortName).get())
                    || state.is(ModBlocks.MULTIBLOCKS_MAP.get(controllerName).get()))
                .controller(state -> state.is(ModBlocks.MULTIBLOCKS_MAP.get(controllerName).get()))
                .requireInterior(
                    state -> state.is(ModBlocks.MULTIBLOCKS_MAP.get("reactor/fuel_cell").get()),
                    1
                )
                .build();
        }
    }
}
