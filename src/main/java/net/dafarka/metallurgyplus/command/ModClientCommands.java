package net.dafarka.metallurgyplus.command;

import com.mojang.brigadier.CommandDispatcher;
import net.dafarka.metallurgyplus.MetallurgyPlus;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MetallurgyPlus.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = net.minecraftforge.api.distmarker.Dist.CLIENT)
public class ModClientCommands {

    public static final String EXPORT_PATH = MetallurgyPlus.MODID + "/export";

    @SubscribeEvent
    public static void onRegisterClientCommands(RegisterClientCommandsEvent event) {

        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(
            Commands.literal(MetallurgyPlus.MODID)
                .then(Commands.literal("export_texture")
                    .executes(ctx -> {
                        ExportTintedTextureCommand.execute();
                        return 1;
                    })
                )
        );

        dispatcher.register(
            Commands.literal(MetallurgyPlus.MODID)
                .then(Commands.literal("export_all")
                    .executes(ctx -> {
                        ExportAllCommand.execute();
                        return 1;
                    })
                )
        );

    }
}