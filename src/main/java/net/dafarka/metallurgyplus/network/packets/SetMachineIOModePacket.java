package net.dafarka.metallurgyplus.network.packets;

import net.dafarka.metallurgyplus.block.BlockFace;
import net.dafarka.metallurgyplus.block.ItemSideMode;
import net.dafarka.metallurgyplus.block.entity.base.MachineBlockEntity;
import net.dafarka.metallurgyplus.screen.menu.MachineIOMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SetMachineIOModePacket {
    private final BlockFace face;
    private final ItemSideMode mode;

    public SetMachineIOModePacket(BlockFace face, ItemSideMode mode) {
        this.face = face;
        this.mode = mode;
    }

    public SetMachineIOModePacket(FriendlyByteBuf buffer) {
        BlockFace[] faces = BlockFace.values();
        ItemSideMode[] modes = ItemSideMode.values();
        int faceIndex = buffer.readVarInt();
        int modeIndex = buffer.readVarInt();
        this.face = faceIndex >= 0 && faceIndex < faces.length ? faces[faceIndex] : null;
        this.mode = modeIndex >= 0 && modeIndex < modes.length ? modes[modeIndex] : null;
    }

    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeVarInt(face.ordinal());
        buffer.writeVarInt(mode.ordinal());
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null || face == null || mode == null
                || !(player.containerMenu instanceof MachineIOMenu menu)
                || !menu.stillValid(player)) {
                return;
            }

            BlockEntity currentBlockEntity = player.serverLevel().getBlockEntity(menu.getBlockEntityPos());
            if (currentBlockEntity != menu.getBlockEntity()
                || !(currentBlockEntity instanceof MachineBlockEntity machine)) {
                return;
            }

            machine.setItemSideMode(face, mode);
        });
        context.setPacketHandled(true);
    }
}
