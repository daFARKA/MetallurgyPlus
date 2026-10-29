package net.dafarka.metallurgyplus.screen.menu;

import net.dafarka.metallurgyplus.block.entity.multiblock.MBBatteryController;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.Nullable;

public abstract class MBBaseMenu extends BaseMenu {

    private final ContainerData data;

    protected MBBaseMenu(@Nullable MenuType<?> pMenuType, int pContainerId, int slotCount, ContainerData data) {
        super(pMenuType, pContainerId, slotCount);

        this.data = data;
    }

    public boolean isFormed() {
        return data.get(MBBatteryController.ENERGY_WORDS * 2) == 1;
    }
}
