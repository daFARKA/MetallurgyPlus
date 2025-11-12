package net.dafarka.metallurgyplus.network;

import net.dafarka.metallurgyplus.MetallurgyPlus;
import net.dafarka.metallurgyplus.network.packets.SetAreaQuarryPacket;
import net.dafarka.metallurgyplus.network.packets.StartQuarryPacket;
import net.dafarka.metallurgyplus.network.packets.StopQuarryPacket;
import net.dafarka.metallurgyplus.network.packets.ToggleQuarryPacket;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
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

        net.messageBuilder(StartQuarryPacket.class, id())
            .encoder(StartQuarryPacket::toBytes)
            .decoder(StartQuarryPacket::new)
            .consumerMainThread(StartQuarryPacket::handle)
            .add();

        net.messageBuilder(StopQuarryPacket.class, id())
            .encoder(StopQuarryPacket::toBytes)
            .decoder(StopQuarryPacket::new)
            .consumerMainThread(StopQuarryPacket::handle)
            .add();

        net.messageBuilder(ToggleQuarryPacket.class, id())
            .encoder(ToggleQuarryPacket::toBytes)
            .decoder(ToggleQuarryPacket::new)
            .consumerMainThread(ToggleQuarryPacket::handle)
            .add();
    }

    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.sendToServer(message);
    }
}
