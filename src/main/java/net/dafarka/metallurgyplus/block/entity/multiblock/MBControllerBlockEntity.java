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

    private MBStructure formedStructure;

    protected MBControllerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, Supplier<BigEnergyStorage> storageSupplier) {
        super(type, pos, state, storageSupplier);
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

    /**
     * The registered multiblock block name used for this controller's energy input ports.
     */
    protected String getEnergyInputPortName() {
        return null;
    }

    /**
     * The registered multiblock block name used for this controller's energy output ports.
     */
    protected String getEnergyOutputPortName() {
        return null;
    }

    /**
     * The registered multiblock block name used for this controller's item input ports.
     */
    protected String getItemInputPortName() {
        return null;
    }

    /**
     * The registered multiblock block name used for this controller's item output ports.
     */
    protected String getItemOutputPortName() {
        return null;
    }

    /**
     * The registered multiblock block name used for this controller's fluid input ports.
     */
    protected String getFluidInputPortName() {
        return null;
    }

    /**
     * The registered multiblock block name used for this controller's fluid output ports.
     */
    protected String getFluidOutputPortName() {
        return null;
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
        boolean transferredFromInputs = transferFromInputs(structure);
        boolean transferredToOutputs = transferToOutputs(structure);
        if (transferredFromInputs || transferredToOutputs) {
            setChanged();
        }
    }

    private boolean transferFromInputs(MBStructure structure) {
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

        List<EnergyEndpoint> endpoints = getEnergyEndpoints(structure, inputPortName, true);
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

        List<EnergyEndpoint> endpoints = getEnergyEndpoints(structure, outputPortName, false);
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

    private List<EnergyEndpoint> getEnergyEndpoints(MBStructure structure, String portName, boolean controllerReceives) {
        List<EnergyEndpoint> endpoints = new ArrayList<>();
        Set<IEnergyStorage> seen = Collections.newSetFromMap(new IdentityHashMap<>());

        for (PortConnection connection : getPortConnections(structure, portName)) {
            IEnergyStorage storage = connection.blockEntity()
                .getCapability(ForgeCapabilities.ENERGY, connection.neighborSide())
                .orElse(null);
            if ((controllerReceives ? !storage.canExtract() : !storage.canReceive()) || !seen.add(storage)) {
                continue;
            }

            endpoints.add(new EnergyEndpoint(storage));
        }

        return endpoints;
    }

    /**
     * Finds adjacent block entities for matching wall ports. The side is the face on the neighbor
     * that points back toward the multiblock, suitable for capability lookups.
     */
    protected final List<PortConnection> getPortConnections(MBStructure structure, String portName) {
        if (level == null || structure == null || portName == null) {
            return List.of();
        }

        var portBlockEntry = ModBlocks.MULTIBLOCKS_MAP.get(portName);
        if (portBlockEntry == null) {
            return List.of();
        }

        Block portBlock = portBlockEntry.get();
        List<PortConnection> connections = new ArrayList<>();
        for (BlockPos portPos : structure.wallPositions()) {
            if (!level.getBlockState(portPos).is(portBlock)) {
                continue;
            }

            Direction outward = getOutwardDirection(structure, portPos);
            BlockEntity neighbor = level.getBlockEntity(portPos.relative(outward));
            if (neighbor != null) {
                connections.add(new PortConnection(neighbor, outward.getOpposite()));
            }
        }

        return connections;
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

    protected record PortConnection(BlockEntity blockEntity, Direction neighborSide) { }

    private static final class EnergyEndpoint {
        private final IEnergyStorage storage;
        private BigInteger available = BigInteger.ZERO;

        private EnergyEndpoint(IEnergyStorage storage) {
            this.storage = storage;
        }
    }
}
