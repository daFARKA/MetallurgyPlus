package net.dafarka.metallurgyplus.block.entity.multiblock;

import net.dafarka.metallurgyplus.block.ModBlocks;
import net.dafarka.metallurgyplus.block.custom.BatteryCellBlock;
import net.dafarka.metallurgyplus.energy.BigEnergyStorage;
import net.dafarka.metallurgyplus.screen.menu.MBBatteryMenu;
import net.dafarka.metallurgyplus.util.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import java.math.BigInteger;
import java.util.function.Supplier;

public class MBBatteryController extends MBControllerBlockEntity implements MenuProvider {
    public static final int ENERGY_WORDS = 11;
    public static final int ENERGY_WORD_BITS = 15;
    public static final int ENERGY_DATA_COUNT = ENERGY_WORDS * 2 + 1;
    private static final BigInteger ENERGY_WORD_MASK = BigInteger.ONE.shiftLeft(ENERGY_WORD_BITS).subtract(BigInteger.ONE);
    private static final String INPUT_NAME = "battery/energy_input";
    private static final String OUTPUT_NAME = "battery/energy_output";

    private final ContainerData menuData = new ContainerData() {
        @Override
        public int get(int index) {
            if (index >= 0 && index < ENERGY_WORDS) {
                return getEnergyWord(energyStorage.getEnergyStoredBig(), index);
            }
            if (index >= ENERGY_WORDS && index < ENERGY_WORDS * 2) {
                return getEnergyWord(energyStorage.getMaxEnergyStoredBig(), index - ENERGY_WORDS);
            }
            if (index == ENERGY_WORDS * 2) {
                return isFormed() ? 1 : 0;
            }
            return 0;
        }

        @Override
        public void set(int index, int value) {
            // The battery screen only reads controller state.
        }

        @Override
        public int getCount() {
            return ENERGY_DATA_COUNT;
        }
    };

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

    private static int getEnergyWord(BigInteger value, int wordIndex) {
        return value.shiftRight(wordIndex * ENERGY_WORD_BITS).and(ENERGY_WORD_MASK).intValue();
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.metallurgyplus.battery");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new MBBatteryMenu(containerId, playerInventory, this, menuData);
    }

    @Override
    protected MBStructureDefinition getStructureDefinition() {
        return BatteryStructureDefinition.DEFINITION;
    }

    @Override
    protected String getEnergyInputPortName() {
        return INPUT_NAME;
    }

    @Override
    protected String getEnergyOutputPortName() {
        return OUTPUT_NAME;
    }

    @Override
    protected void onStructureFormed(MBStructure structure) {
        BigInteger capacity = BigInteger.ZERO;

        if (level == null) return;

        for (BlockPos cellPos : structure.requiredInteriorPositions()) {
            int tier = getCellTier(level.getBlockState(cellPos));
            if (tier <= 0) {
                continue;
            }

            capacity = capacity.add(BatteryCellBlock.getCapacityForTier(tier));
        }

        if (energyStorage.setLimits(capacity)) {
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
        return state.getBlock() instanceof BatteryCellBlock cell ? cell.getTier() : 0;
    }

    private static final class BatteryStructureDefinition {
        private static final MBStructureDefinition DEFINITION = MBStructureDefinition.rectangularPrism(3, 16)
            .edges(state -> state.is(ModBlocks.MULTIBLOCKS_MAP.get("battery/casing").get()))
            .walls(state -> state.is(Blocks.GLASS)
                || state.is(ModBlocks.MULTIBLOCKS_MAP.get("battery/casing").get())
                || state.is(ModBlocks.MULTIBLOCKS_MAP.get(INPUT_NAME).get())
                || state.is(ModBlocks.MULTIBLOCKS_MAP.get(OUTPUT_NAME).get())
                || state.is(ModBlocks.MULTIBLOCKS_MAP.get("battery/controller").get()))
            .controller(state -> state.is(ModBlocks.MULTIBLOCKS_MAP.get("battery/controller").get()))
            .requireInterior(
                state -> state.is(ModTags.Blocks.BATTERY_CELLS) || state.getBlock() instanceof BatteryCellBlock,
                1
            )
            .build();
    }
}
