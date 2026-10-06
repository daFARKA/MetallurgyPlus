package net.dafarka.metallurgyplus.block.entity.multiblock;

import net.dafarka.metallurgyplus.block.base.BaseBlock;
import net.dafarka.metallurgyplus.block.entity.base.EnergyBlockEntity;
import net.dafarka.metallurgyplus.energy.BigEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

public abstract class MBControllerBlockEntity extends EnergyBlockEntity<BigEnergyStorage> {
    private static final double NOTIFICATION_RADIUS_SQUARED = 32 * 32;

    private MBStructure formedStructure;

    protected MBControllerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, Supplier<BigEnergyStorage> storageSupplier) {
        super(type, pos, state, storageSupplier);
    }

    protected abstract MBStructureDefinition getStructureDefinition();

    protected void onStructureFormed(MBStructure structure) {
    }

    protected void onStructureInvalidated() {
    }

    protected void onServerTick() {
    }

    /**
     * Validates the structure and updates this controller's derived formed state.
     */
    protected final boolean form(MBStructureDefinition definition) {
        if (level == null || level.isClientSide) {
            return false;
        }

        Direction facing = getBlockState().getValue(BaseBlock.FACING);
        MBStructure nextStructure = MBStructureValidator
            .find(level, worldPosition, facing, Objects.requireNonNull(definition))
            .orElse(null);

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
    }
}
