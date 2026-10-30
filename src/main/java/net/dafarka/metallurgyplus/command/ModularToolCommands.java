package net.dafarka.metallurgyplus.command;

import net.dafarka.metallurgyplus.MetallurgyPlus;
import net.dafarka.metallurgyplus.item.tool.ModularTool;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MetallurgyPlus.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ModularToolCommands {
    private ModularToolCommands() {
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(
            Commands.literal(MetallurgyPlus.MODID)
                .then(Commands.literal("upgrade_tool")
                    .requires(source -> source.hasPermission(2))
                    .executes(context -> {
                        var source = context.getSource();
                        var player = source.getPlayerOrException();
                        ItemStack heldStack = player.getMainHandItem();

                        if (!(heldStack.getItem() instanceof ModularTool tool)) {
                            source.sendFailure(Component.literal("Hold a modular tool in your main hand."));
                            return 0;
                        }

                        int newLevel = tool.upgradeLevel(heldStack);
                        source.sendSuccess(
                            () -> Component.literal("Upgraded modular tool to level " + newLevel + "."),
                            false
                        );
                        return 1;
                    })
                )
        );
    }
}
