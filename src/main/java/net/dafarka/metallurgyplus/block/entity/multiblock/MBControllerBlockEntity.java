package net.dafarka.metallurgyplus.block.entity.multiblock;

import net.dafarka.metallurgyplus.block.ModBlocks;
import net.dafarka.metallurgyplus.block.base.BaseBlock;
import net.dafarka.metallurgyplus.block.entity.base.EnergyBlockEntity;
import net.dafarka.metallurgyplus.energy.BigEnergyStorage;
import net.dafarka.metallurgyplus.energy.IBigEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
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

public abstract class MBControllerBlockEntity extends EnergyBlockEntity<BigEnergyStorage> {
    private static final double NOTIFICATION_RADIUS_SQUARED = 32 * 32;
    private static final BigInteger MAX_STANDARD_TRANSFER = BigInteger.valueOf(Integer.MAX_VALUE);
    private final String multiblockName;

    private MBStructure formedStructure;

    protected MBControllerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, Supplier<BigEnergyStorage> storageSupplier, String multiblockName) {
        super(type, pos, state, storageSupplier);

        this.multiblockName = Objects.requireNonNull(multiblockName, "multiblockName");
    }

    /**
     * Return {@code null} to keep a placeholder controller unformed until its structure is defined.
     */
    @Nullable
    protected abstract MBStructureDefinition getStructureDefinition();

    protected void onStructureFormed(MBStructure structure) {
    }

    protected void onStructureInvalidated() {
    }

    protected void onServerTick() {
    }

    protected String getControllerName() {
        return this.multiblockName + "/controller";
    }

    protected String getEnergyInputPortName() {
        return multiblockName + "/energy_input";
    }

    protected String getEnergyOutputPortName() {
        return multiblockName + "/energy_output";
    }

    protected String getItemInputPortName() {
        return multiblockName + "/item_input";
    }

    protected String getItemOutputPortName() {
        return multiblockName + "/item_output";
    }

    protected String getFluidInputPortName() {
        return multiblockName + "/fluid_input";
    }

    protected String getFluidOutputPortName() {
        return multiblockName + "/fluid_output";
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction side) {
        if (capability == ForgeCapabilities.ENERGY) {
            return LazyOptional.empty();
        }
        return super.getCapability(capability, side);
    }

    /**
     * Validates the structure and updates the formed state. A null definition disables formation.
     */
    protected final boolean form(@Nullable MBStructureDefinition definition) {
        if (level == null || level.isClientSide) {
            return false;
        }

        MBStructure nextStructure = null;
        if (definition != null) {
            Direction facing = getBlockState().getValue(BaseBlock.FACING);
            nextStructure = MBStructureValidator
                .find(level, worldPosition, facing, definition)
                .orElse(null);
        }

        boolean wasFormed = formedStructure != null;
        boolean isNowFormed = nextStructure != null;
        formedStructure = nextStructure;

        if (nextStructure != null) {
            onStructureFormed(nextStructure);
        } else {
            onStructureInvalidated();
        }

        if (!wasFormed && isNowFormed) {
            notifyNearbyPlayers(Component.translatable("message.metallurgyplus.multiblock.formed"));
        }

        return formedStructure != null;
    }

    private void notifyNearbyPlayers(Component message) {
        if (level == null || level.isClientSide) {
            return;
        }

        double x = worldPosition.getX() + 0.5;
        double y = worldPosition.getY() + 0.5;
        double z = worldPosition.getZ() + 0.5;
        for (Player player : level.players()) {
            if (player instanceof ServerPlayer serverPlayer
                && player.distanceToSqr(x, y, z) <= NOTIFICATION_RADIUS_SQUARED) {
                serverPlayer.displayClientMessage(message, true);
            }
        }
    }

    public final boolean isFormed() {
        return formedStructure != null;
    }

    public final Optional<MBStructure> getFormedStructure() {
        return Optional.ofNullable(formedStructure);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, MBControllerBlockEntity controller) {
        if (level.isClientSide) {
            return;
        }

        if (level.getGameTime() % 20L == 0L) {
            controller.form(controller.getStructureDefinition());
        }

        controller.onServerTick();
        controller.transferEnergyThroughPorts();
    }

    private void transferEnergyThroughPorts() {
        if (level == null || !isFormed()) {
            return;
        }

        MBStructure structure = formedStructure;
        boolean transferredFromInputs = transferEnergyFromInputs(structure);
        boolean transferredToOutputs = transferEnergyToOutputs(structure);
        if (transferredFromInputs || transferredToOutputs) {
            setChanged();
        }
    }

    private boolean transferEnergyFromInputs(MBStructure structure) {
        String inputPortName = getEnergyInputPortName();
        if (inputPortName == null) {
            return false;
        }

        BigInteger maxReceive = energyStorage.getMaxReceiveBig();
        if (maxReceive.signum() <= 0) {
            return false;
        }

        BigInteger remainingBudget = energyStorage.receiveEnergyBig(maxReceive, true);
        if (remainingBudget.signum() <= 0) {
            return false;
        }

        List<IEnergyStorage> endpoints = getEnergyPortStorages(structure, inputPortName, true);
        Map<IEnergyStorage, BigInteger> availableByStorage = new IdentityHashMap<>();
        for (IEnergyStorage endpoint : endpoints) {
            BigInteger available = getExtractableEnergy(endpoint, remainingBudget);
            if (available.signum() > 0) {
                availableByStorage.put(endpoint, available);
            }
        }
        endpoints.removeIf(endpoint -> !availableByStorage.containsKey(endpoint));

        BigInteger receivedTotal = BigInteger.ZERO;
        int remainingEndpoints = endpoints.size();
        for (IEnergyStorage endpoint : endpoints) {
            if (remainingBudget.signum() <= 0) {
                break;
            }

            BigInteger share = divideRoundUp(remainingBudget, remainingEndpoints);
            BigInteger requested = availableByStorage.get(endpoint).min(share);
            BigInteger extracted = extractFrom(endpoint, requested);
            BigInteger received = energyStorage.receiveEnergyBig(extracted, false).min(extracted);
            receivedTotal = receivedTotal.add(received);
            remainingBudget = remainingBudget.subtract(received);
            remainingEndpoints--;
        }

        return receivedTotal.signum() > 0;
    }

    private boolean transferEnergyToOutputs(MBStructure structure) {
        String outputPortName = getEnergyOutputPortName();
        if (outputPortName == null) {
            return false;
        }

        BigInteger maxExtract = energyStorage.getMaxExtractBig();
        if (maxExtract.signum() <= 0) {
            return false;
        }

        BigInteger remainingBudget = energyStorage.extractEnergyBig(maxExtract, true);
        if (remainingBudget.signum() <= 0) {
            return false;
        }

        List<IEnergyStorage> endpoints = getEnergyPortStorages(structure, outputPortName, false);
        Map<IEnergyStorage, BigInteger> availableByStorage = new IdentityHashMap<>();
        for (IEnergyStorage endpoint : endpoints) {
            BigInteger available = getReceivableEnergy(endpoint, remainingBudget);
            if (available.signum() > 0) {
                availableByStorage.put(endpoint, available);
            }
        }
        endpoints.removeIf(endpoint -> !availableByStorage.containsKey(endpoint));

        BigInteger extractedTotal = BigInteger.ZERO;
        int remainingEndpoints = endpoints.size();
        for (IEnergyStorage endpoint : endpoints) {
            if (remainingBudget.signum() <= 0) {
                break;
            }

            BigInteger share = divideRoundUp(remainingBudget, remainingEndpoints);
            BigInteger offered = availableByStorage.get(endpoint).min(share);
            BigInteger accepted = receiveAt(endpoint, offered);
            BigInteger extracted = energyStorage.extractEnergyBig(accepted, false).min(accepted);
            extractedTotal = extractedTotal.add(extracted);
            remainingBudget = remainingBudget.subtract(extracted);
            remainingEndpoints--;
        }

        return extractedTotal.signum() > 0;
    }

    private List<IEnergyStorage> getEnergyPortStorages(MBStructure structure, String portName, boolean controllerReceives) {
        List<IEnergyStorage> endpoints = new ArrayList<>();
        Set<IEnergyStorage> seen = Collections.newSetFromMap(new IdentityHashMap<>());

        for (MBEnergyPortBlockEntity port : getPortBlockEntities(structure, portName, MBEnergyPortBlockEntity.class)) {
            Direction outward = getOutwardDirection(structure, port.getBlockPos());
            IEnergyStorage storage = port.getExternalEnergyStorage(outward);
            if (storage == null
                || (controllerReceives ? !storage.canExtract() : !storage.canReceive())
                || !seen.add(storage)) {
                continue;
            }

            endpoints.add(storage);
        }

        return endpoints;
    }

    /**
     * Finds port block entities placed in the formed structure's wall. Unlike
     * resolving their outside neighbors, this returns the port itself.
     */
    protected final <T extends MBPortBlockEntity> List<T> getPortBlockEntities(
        MBStructure structure,
        String portName,
        Class<T> portType
    ) {
        if (level == null || structure == null || portName == null || portType == null) {
            return List.of();
        }

        var portBlockEntry = ModBlocks.MULTIBLOCKS_MAP.get(portName);
        if (portBlockEntry == null) {
            return List.of();
        }

        Block portBlock = portBlockEntry.get();
        List<T> portBlockEntities = new ArrayList<>();
        for (BlockPos portPos : structure.wallPositions()) {
            if (!level.getBlockState(portPos).is(portBlock)) {
                continue;
            }

            BlockEntity blockEntity = level.getBlockEntity(portPos);
            if (portType.isInstance(blockEntity)) {
                portBlockEntities.add(portType.cast(blockEntity));
            }
        }

        return portBlockEntities;
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
        if (requested.signum() <= 0 || !storage.canExtract()) {
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
        if (offered.signum() <= 0 || !storage.canReceive()) {
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

}
