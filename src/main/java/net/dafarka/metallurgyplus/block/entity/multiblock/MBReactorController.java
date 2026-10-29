package net.dafarka.metallurgyplus.block.entity.multiblock;

import net.dafarka.metallurgyplus.block.ModBlocks;
import net.dafarka.metallurgyplus.energy.BigEnergyStorage;
import net.dafarka.metallurgyplus.screen.menu.ReactorMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.math.BigInteger;
import java.util.function.Supplier;

public class MBReactorController extends MBControllerBlockEntity implements MenuProvider {

    private final MBStructureDefinition structureDefinition;

    private int fuelCellAmount = 1;

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
            getItemInputPortName(), getItemOutputPortName(), getFluidInputPortName());
    }

    @Override
    protected void onStructureFormed(MBStructure structure) {
        if (level == null) return;

        for (BlockPos cellPos : structure.requiredInteriorPositions()) {
            fuelCellAmount++;
        }
    }

    @Override
    protected MBStructureDefinition getStructureDefinition() {
        return structureDefinition;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.metallurgyplus.reactor");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player pPlayer) {
        return new ReactorMenu(containerId, playerInventory, this);
    }

    private static final class ReactorStructureDefinition {
        private static MBStructureDefinition create(String controllerName, String energyOutputPortName,
                                                    String itemInputPortName, String itemOutputPortName,
                                                    String fluidInputPortName) {
            return MBStructureDefinition.rectangularPrism(5, 32, 3, 64)
                .edges(state -> state.is(ModBlocks.MULTIBLOCKS_MAP.get("reactor/casing").get()))
                .walls(state -> state.is(Blocks.GLASS)
                    || state.is(ModBlocks.MULTIBLOCKS_MAP.get("reactor/casing").get())
                    || state.is(ModBlocks.MULTIBLOCKS_MAP.get("reactor/glass").get())
                    || state.is(ModBlocks.MULTIBLOCKS_MAP.get(energyOutputPortName).get())
                    || state.is(ModBlocks.MULTIBLOCKS_MAP.get(itemInputPortName).get())
                    || state.is(ModBlocks.MULTIBLOCKS_MAP.get(itemOutputPortName).get())
                    || state.is(ModBlocks.MULTIBLOCKS_MAP.get(fluidInputPortName).get())
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
