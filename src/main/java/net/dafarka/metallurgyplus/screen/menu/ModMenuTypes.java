package net.dafarka.metallurgyplus.screen.menu;

import net.dafarka.metallurgyplus.MetallurgyPlus;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS =
        DeferredRegister.create(ForgeRegistries.MENU_TYPES, MetallurgyPlus.MODID);

    public static final RegistryObject<MenuType<MachineMenu>> MACHINE_MENU =
        registerMenuType("machine_menu", MachineMenu::new);

    public static final RegistryObject<MenuType<QuarryMenu>> QUARRY_MENU =
        registerMenuType("quarry_menu", QuarryMenu::new);

    public static final RegistryObject<MenuType<BatteryMenu>> BATTERY_MENU =
        registerMenuType("battery_menu", BatteryMenu::new);

    public static final RegistryObject<MenuType<SackMenu>> SACK_MENU =
        registerMenuType("sack_menu", SackMenu::new);

    private static <T extends AbstractContainerMenu> RegistryObject<MenuType<T>> registerMenuType(String name, IContainerFactory<T> factory) {
        return MENUS.register(name, () -> IForgeMenuType.create(factory));
    }

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}
