package net.dafarka.metallurgyplus.block.entity.multiblock;

import net.dafarka.metallurgyplus.block.ModBlocks;
import net.dafarka.metallurgyplus.block.custom.BatteryCellBlock;
import net.dafarka.metallurgyplus.energy.BigEnergyStorage;
import net.dafarka.metallurgyplus.energy.IBigEnergyStorage;
import net.dafarka.metallurgyplus.screen.menu.MBBatteryMenu;
import net.dafarka.metallurgyplus.util.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.math.BigInteger;
import java.util.*;
import java.util.function.Supplier;

public class MBBatteryController extends MBControllerBlockEntity implements MenuProvider {
    private static final String CELL_BLOCK_PREFIX = BatteryCellBlock.BLOCK_ID_PREFIX;
    private static final int ENERGY_WORDS = 6;
    private static final int ENERGY_WORD_BITS = 15;
    private static final BigInteger ENERGY_WORD_MASK = BigInteger.ONE.shiftLeft(ENERGY_WORD_BITS).subtract(BigInteger.ONE);
    private static final BigInteger MAX_STANDARD_TRANSFER = BigInteger.valueOf(Integer.MAX_VALUE);

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
            return ENERGY_WORDS * 2 + 1;
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

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction side) {
        if (capability == ForgeCapabilities.ENERGY) {
            return LazyOptional.empty();
        }
        return super.getCapability(capability, side);
    }

    @Override
    protected MBStructureDefinition getStructureDefinition() {
        return BatteryStructureDefinition.DEFINITION;
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

    @Override
    protected void onServerTick() {
        if (level == null || !isFormed()) {
            return;
        }

        getFormedStructure().ifPresent(structure -> {
            boolean transferredFromInputs = transferFromInputs(structure);
            boolean transferredToOutputs = transferToOutputs(structure);
            if (transferredFromInputs || transferredToOutputs) {
                setChanged();
            }
        });
    }

    private boolean transferFromInputs(MBStructure structure) {
        BigInteger maxReceive = energyStorage.getMaxReceiveBig();
        if (maxReceive.signum() <= 0) {
            return false;
        }

        BigInteger remainingBudget = energyStorage.receiveEnergyBig(maxReceive, true);
        if (remainingBudget.signum() <= 0) {
            return false;
        }

        List<EnergyEndpoint> endpoints = getConnectedEndpoints(structure, "battery/input", true);
        for (EnergyEndpoint endpoint : endpoints) {
            endpoint.available = getExtractableEnergy(endpoint.storage, remainingBudget);
        }
        endpoints.removeIf(endpoint -> endpoint.available.signum() <= 0);

        BigInteger receivedTotal = BigInteger.ZERO;
        int remainingEndpoints = endpoints.size();
        for (EnergyEndpoint endpoint : endpoints) {
            if (remainingBudget.signum() <= 0) {
                break;
            }

            BigInteger share = divideRoundUp(remainingBudget, remainingEndpoints);
            BigInteger requested = endpoint.available.min(share);
            BigInteger extracted = extractFrom(endpoint.storage, requested);
            BigInteger received = energyStorage.receiveEnergyBig(extracted, false).min(extracted);
            receivedTotal = receivedTotal.add(received);
            remainingBudget = remainingBudget.subtract(received);
            remainingEndpoints--;
        }

        return receivedTotal.signum() > 0;
    }

    private boolean transferToOutputs(MBStructure structure) {
        BigInteger maxExtract = energyStorage.getMaxExtractBig();
        if (maxExtract.signum() <= 0) {
            return false;
        }

        BigInteger remainingBudget = energyStorage.extractEnergyBig(maxExtract, true);
        if (remainingBudget.signum() <= 0) {
            return false;
        }

        List<EnergyEndpoint> endpoints = getConnectedEndpoints(structure, "battery/output", false);
        for (EnergyEndpoint endpoint : endpoints) {
            endpoint.available = getReceivableEnergy(endpoint.storage, remainingBudget);
        }
        endpoints.removeIf(endpoint -> endpoint.available.signum() <= 0);

        BigInteger extractedTotal = BigInteger.ZERO;
        int remainingEndpoints = endpoints.size();
        for (EnergyEndpoint endpoint : endpoints) {
            if (remainingBudget.signum() <= 0) {
                break;
            }

            BigInteger share = divideRoundUp(remainingBudget, remainingEndpoints);
            BigInteger offered = endpoint.available.min(share);
            BigInteger accepted = receiveAt(endpoint.storage, offered);
            BigInteger extracted = energyStorage.extractEnergyBig(accepted, false).min(accepted);
            extractedTotal = extractedTotal.add(extracted);
            remainingBudget = remainingBudget.subtract(extracted);
            remainingEndpoints--;
        }

        return extractedTotal.signum() > 0;
    }

    private List<EnergyEndpoint> getConnectedEndpoints(MBStructure structure, String portName, boolean batteryReceives) {
        List<EnergyEndpoint> endpoints = new ArrayList<>();
        Set<IEnergyStorage> seen = Collections.newSetFromMap(new IdentityHashMap<>());
        Block portBlock = ModBlocks.MULTIBLOCKS_MAP.get(portName).get();

        for (BlockPos portPos : structure.wallPositions()) {
            if (!level.getBlockState(portPos).is(portBlock)) {
                continue;
            }

            Direction outward = getOutwardDirection(structure, portPos);
            BlockEntity neighbor = level.getBlockEntity(portPos.relative(outward));
            if (neighbor == null) {
                continue;
            }

            IEnergyStorage storage = neighbor
                .getCapability(ForgeCapabilities.ENERGY, outward.getOpposite())
                .orElse(null);
            if ((batteryReceives ? !storage.canExtract() : !storage.canReceive()) || !seen.add(storage)) {
                continue;
            }

            endpoints.add(new EnergyEndpoint(storage));
        }

        return endpoints;
    }

    private static Direction getOutwardDirection(MBStructure structure, BlockPos pos) {
        if (pos.getX() == structure.minimum().getX()) return Direction.WEST;
        if (pos.getX() == structure.maximum().getX()) return Direction.EAST;
        if (pos.getY() == structure.minimum().getY()) return Direction.DOWN;
        if (pos.getY() == structure.maximum().getY()) return Direction.UP;
        if (pos.getZ() == structure.minimum().getZ()) return Direction.NORTH;
        return Direction.SOUTH;
    }

    private static BigInteger getExtractableEnergy(IEnergyStorage storage, BigInteger limit) {
        BigInteger extracted;
        if (storage instanceof IBigEnergyStorage bigStorage) {
            extracted = bigStorage.extractEnergyBig(limit, true);
        } else {
            extracted = BigInteger.valueOf(storage.extractEnergy(limit.min(MAX_STANDARD_TRANSFER).intValue(), true));
        }
        return extracted.max(BigInteger.ZERO).min(limit);
    }

    private static BigInteger getReceivableEnergy(IEnergyStorage storage, BigInteger limit) {
        BigInteger received;
        if (storage instanceof IBigEnergyStorage bigStorage) {
            received = bigStorage.receiveEnergyBig(limit, true);
        } else {
            received = BigInteger.valueOf(storage.receiveEnergy(limit.min(MAX_STANDARD_TRANSFER).intValue(), true));
        }
        return received.max(BigInteger.ZERO).min(limit);
    }

    private static BigInteger extractFrom(IEnergyStorage storage, BigInteger requested) {
        if (requested.signum() <= 0) {
            return BigInteger.ZERO;
        }

        BigInteger extracted;
        if (storage instanceof IBigEnergyStorage bigStorage) {
            extracted = bigStorage.extractEnergyBig(requested, false);
        } else {
            extracted = BigInteger.valueOf(storage.extractEnergy(requested.min(MAX_STANDARD_TRANSFER).intValue(), false));
        }
        return extracted.max(BigInteger.ZERO).min(requested);
    }

    private static BigInteger receiveAt(IEnergyStorage storage, BigInteger offered) {
        if (offered.signum() <= 0) {
            return BigInteger.ZERO;
        }

        BigInteger received;
        if (storage instanceof IBigEnergyStorage bigStorage) {
            received = bigStorage.receiveEnergyBig(offered, false);
        } else {
            received = BigInteger.valueOf(storage.receiveEnergy(offered.min(MAX_STANDARD_TRANSFER).intValue(), false));
        }
        return received.max(BigInteger.ZERO).min(offered);
    }

    private static BigInteger divideRoundUp(BigInteger value, int divisor) {
        BigInteger[] division = value.divideAndRemainder(BigInteger.valueOf(divisor));
        return division[1].signum() == 0 ? division[0] : division[0].add(BigInteger.ONE);
    }

    private static final class EnergyEndpoint {
        private final IEnergyStorage storage;
        private BigInteger available = BigInteger.ZERO;

        private EnergyEndpoint(IEnergyStorage storage) {
            this.storage = storage;
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
