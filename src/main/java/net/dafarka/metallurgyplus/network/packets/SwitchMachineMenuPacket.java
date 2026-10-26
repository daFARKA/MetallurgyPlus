package net.dafarka.metallurgyplus.network.packets;

import net.dafarka.metallurgyplus.block.entity.base.MachineBlockEntity;
import net.dafarka.metallurgyplus.screen.menu.MachineIOMenu;
import net.dafarka.metallurgyplus.screen.menu.MachineMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkHooks;

import java.util.function.Supplier;

public class SwitchMachineMenuPacket {
    private final BlockPos machinePos;
    private final boolean openIOMenu;

    public SwitchMachineMenuPacket(BlockPos machinePos, boolean openIOMenu) {
        this.machinePos = machinePos;
        this.openIOMenu = openIOMenu;
    }

    public SwitchMachineMenuPacket(FriendlyByteBuf buffer) {
        this.machinePos = buffer.readBlockPos();
        this.openIOMenu = buffer.readBoolean();
    }

    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(machinePos);
        buffer.writeBoolean(openIOMenu);
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null || !player.serverLevel().isLoaded(machinePos)
                || !(player.serverLevel().getBlockEntity(machinePos) instanceof MachineBlockEntity machine)) {
                return;
            }

            if (openIOMenu) {
                if (!(player.containerMenu instanceof MachineMenu currentMenu)
                    || !currentMenu.blockEntity.getBlockPos().equals(machinePos)
                    || currentMenu.blockEntity != machine
                    || !currentMenu.stillValid(player)) {
                    return;
                }

                SimpleMenuProvider menuProvider = new SimpleMenuProvider(
                    (containerId, inventory, menuPlayer) -> new MachineIOMenu(
                        containerId,
                        inventory,
                        machine,
                        MachineIOMenu.createMenuData(machine)
                    ),
                    Component.literal("Machine I/O")
                );
                NetworkHooks.openScreen(player, menuProvider, machinePos);
                return;
            }

            if (!(player.containerMenu instanceof MachineIOMenu currentMenu)
                || !currentMenu.getBlockEntityPos().equals(machinePos)
                || currentMenu.getBlockEntity() != machine
                || !currentMenu.stillValid(player)) {
                return;
            }

            NetworkHooks.openScreen(player, machine, machinePos);
        });
        context.setPacketHandled(true);
    }
}
