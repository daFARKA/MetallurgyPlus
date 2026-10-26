package net.dafarka.metallurgyplus.network.packets;

import net.dafarka.metallurgyplus.block.entity.base.MachineBlockEntity;
import net.dafarka.metallurgyplus.screen.menu.MachineIOMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ToggleMachineAutoIOPacket {
    private final boolean isInput;

    public ToggleMachineAutoIOPacket(boolean isInput) {
        this.isInput = isInput;
    }

    public ToggleMachineAutoIOPacket(FriendlyByteBuf buffer) {
        this.isInput = buffer.readBoolean();
    }

    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeBoolean(isInput);
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null
                || !(player.containerMenu instanceof MachineIOMenu menu)
                || !menu.stillValid(player)) {
                return;
            }

            BlockEntity currentBlockEntity = player.serverLevel().getBlockEntity(menu.getBlockEntityPos());
            if (currentBlockEntity != menu.getBlockEntity()
                || !(currentBlockEntity instanceof MachineBlockEntity machine)) {
                return;
            }

            if (isInput) {
                machine.setAutoInputEnabled(!machine.isAutoInputEnabled());
            } else {
                machine.setAutoOutputEnabled(!machine.isAutoOutputEnabled());
            }
        });
        context.setPacketHandled(true);
    }
}
