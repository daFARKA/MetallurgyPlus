package net.dafarka.metallurgyplus.network;

import net.dafarka.metallurgyplus.MetallurgyPlus;
import net.dafarka.metallurgyplus.network.packets.SetMachineIOModePacket;
import net.dafarka.metallurgyplus.network.packets.SwitchMachineMenuPacket;
import net.dafarka.metallurgyplus.network.packets.ToggleMachineAutoIOPacket;
import net.dafarka.metallurgyplus.network.packets.SetAreaQuarryPacket;
import net.dafarka.metallurgyplus.network.packets.ToggleQuarryPacket;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraft.resources.ResourceLocation;

public class ModMessages {
    private static SimpleChannel INSTANCE;
    private static int packetId = 0;

    private static int id() { return packetId++; }

    public static void register() {
        SimpleChannel net = NetworkRegistry.ChannelBuilder
            .named(new ResourceLocation(MetallurgyPlus.MODID, "messages"))
            .networkProtocolVersion(() -> "1.0")
            .clientAcceptedVersions(s -> true)
            .serverAcceptedVersions(s -> true)
            .simpleChannel();

        INSTANCE = net;

        net.messageBuilder(SetAreaQuarryPacket.class, id())
            .encoder(SetAreaQuarryPacket::toBytes)
            .decoder(SetAreaQuarryPacket::new)
            .consumerMainThread(SetAreaQuarryPacket::handle)
            .add();

        net.messageBuilder(ToggleQuarryPacket.class, id())
            .encoder(ToggleQuarryPacket::toBytes)
            .decoder(ToggleQuarryPacket::new)
            .consumerMainThread(ToggleQuarryPacket::handle)
            .add();

        net.messageBuilder(SwitchMachineMenuPacket.class, id())
            .encoder(SwitchMachineMenuPacket::toBytes)
            .decoder(SwitchMachineMenuPacket::new)
            .consumerMainThread(SwitchMachineMenuPacket::handle)
            .add();

        net.messageBuilder(SetMachineIOModePacket.class, id())
            .encoder(SetMachineIOModePacket::toBytes)
            .decoder(SetMachineIOModePacket::new)
            .consumerMainThread(SetMachineIOModePacket::handle)
            .add();

        net.messageBuilder(ToggleMachineAutoIOPacket.class, id())
            .encoder(ToggleMachineAutoIOPacket::toBytes)
            .decoder(ToggleMachineAutoIOPacket::new)
            .consumerMainThread(ToggleMachineAutoIOPacket::handle)
            .add();
    }

    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.sendToServer(message);
    }
}
