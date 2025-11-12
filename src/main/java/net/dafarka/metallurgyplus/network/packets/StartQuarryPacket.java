package net.dafarka.metallurgyplus.network.packets;

import net.dafarka.metallurgyplus.block.entity.QuarryBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class StartQuarryPacket {
    private final BlockPos quarryPos;

    public StartQuarryPacket(BlockPos pos) {
        this.quarryPos = pos;
    }

    public StartQuarryPacket(FriendlyByteBuf buf) {
        this.quarryPos = buf.readBlockPos();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBlockPos(quarryPos);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            var player = ctx.get().getSender();
            if (player == null) return;
            var level = player.serverLevel();
            if (level.getBlockEntity(quarryPos) instanceof QuarryBlockEntity quarry) {
                quarry.startOperation();
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
