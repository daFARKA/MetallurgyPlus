package net.dafarka.metallurgyplus.network.packets;

import net.dafarka.metallurgyplus.block.entity.QuarryBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SetAreaQuarryPacket {
    private final BlockPos quarryPos;
    private final BlockPos start;
    private final BlockPos end;

    public SetAreaQuarryPacket(BlockPos quarryPos, BlockPos start, BlockPos end) {
        this.quarryPos = quarryPos;
        this.start = start;
        this.end = end;
    }

    // --- Serialization ---
    public SetAreaQuarryPacket(FriendlyByteBuf buf) {
        this.quarryPos = buf.readBlockPos();
        this.start = buf.readBlockPos();
        this.end = buf.readBlockPos();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBlockPos(quarryPos);
        buf.writeBlockPos(start);
        buf.writeBlockPos(end);
    }

    // --- Handle ---
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            var level = player.serverLevel();
            if (!level.isLoaded(quarryPos)) return;

            if (level.getBlockEntity(quarryPos) instanceof QuarryBlockEntity quarry) {
                quarry.setTargetArea(start, end);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
