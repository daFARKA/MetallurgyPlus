package net.dafarka.metallurgyplus.item.sack;

import net.dafarka.metallurgyplus.MetallurgyPlus;
import net.dafarka.metallurgyplus.item.custom.SackItem;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MetallurgyPlus.MODID)
public class SackPickupHandler {

    @SubscribeEvent
    public static void onItemPickup(EntityItemPickupEvent event) {
        Player player = event.getEntity();
        ItemEntity itemEntity = event.getItem();

        ItemStack droppedStack = itemEntity.getItem();

        if (droppedStack.isEmpty()) {
            return;
        }

        if (!SackStorage.canStore(droppedStack)) {
            return;
        }

        for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {

            ItemStack sack = player.getInventory().getItem(slot);

            if (!(sack.getItem() instanceof SackItem)) {
                continue;
            }

            int inserted = SackStorage.add(sack, droppedStack);

            if (inserted > 0) {
                droppedStack.shrink(inserted);
            }

            if (droppedStack.isEmpty()) {
                itemEntity.discard();
                event.setCanceled(true);
                return;
            }
        }
    }
}
