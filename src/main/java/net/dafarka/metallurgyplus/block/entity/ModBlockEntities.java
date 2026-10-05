package net.dafarka.metallurgyplus.block.entity;

import net.dafarka.metallurgyplus.MetallurgyPlus;
import net.dafarka.metallurgyplus.block.ModBlocks;
import net.dafarka.metallurgyplus.block.custom.BatteryBlock;
import net.dafarka.metallurgyplus.block.custom.CableBlock;
import net.dafarka.metallurgyplus.block.custom.SolarPanelBlock;
import net.dafarka.metallurgyplus.block.entity.base.MachineBlockEntity;
import net.dafarka.metallurgyplus.block.entity.multiblock.MBBatteryController;
import net.dafarka.metallurgyplus.block.entity.multiblock.MBControllerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MetallurgyPlus.MODID);

    public static final Map<Integer, RegistryObject<BlockEntityType<CableBlockEntity>>> CABLE_BLOCK_ENTITIES = new HashMap<>();
    public static final Map<Integer, RegistryObject<BlockEntityType<SolarPanelBlockEntity>>> SOLAR_BLOCK_ENTITIES = new HashMap<>();
    public static final Map<Integer, RegistryObject<BlockEntityType<BatteryBlockEntity>>> BATTERY_BLOCK_ENTITIES = new HashMap<>();
    public static final Map<String, Supplier<? extends BlockEntityType<? extends MBControllerBlockEntity>>> CONTROLLER_TYPES = new HashMap<>();

    public static final RegistryObject<BlockEntityType<MachineBlockEntity>> MACHINE_BE =
        BLOCK_ENTITIES.register(
            "machine_be",
            () -> BlockEntityType.Builder.of(
                MachineBlockEntity::new,
                ModBlocks.ORE_PROCESSING_UNIT.get(),
                ModBlocks.ALLOY_SMELTER.get(),
                ModBlocks.GRINDER.get(),
                ModBlocks.PRESS.get(),
                ModBlocks.EXTRACTOR.get(),
                ModBlocks.GEMSTONE_CUTTER.get()
            ).build(null)
        );

    public static final RegistryObject<BlockEntityType<QuarryBlockEntity>> QUARRY_BE =
        BLOCK_ENTITIES.register("quarry_be", () -> BlockEntityType.Builder.of(QuarryBlockEntity::new,
            ModBlocks.QUARRY.get()).build(null));

    public static final RegistryObject<BlockEntityType<PowerSourceBlockEntity>> POWER_SOURCE_BE =
        BLOCK_ENTITIES.register("power_source_be", () -> BlockEntityType.Builder.of(PowerSourceBlockEntity::new,
            ModBlocks.POWER_SOURCE.get()).build(null));

    public static final RegistryObject<BlockEntityType<SackStationBlockEntity>> SACK_STATION_BE =
        BLOCK_ENTITIES.register("sack_station_be", () -> BlockEntityType.Builder.of(SackStationBlockEntity::new,
            ModBlocks.SACK_STATION.get()).build(null));

    private static <T extends MBControllerBlockEntity> void registerControllerType(String registryName, String controllerBlockName, ControllerFactory<T> factory) {
        ControllerTypeRegistration<T> registration = new ControllerTypeRegistration<>();
        registration.register(registryName, controllerBlockName, factory);
    }

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
        registerCableBlocks();
        registerSolarBlocks();
        registerBatteryBlocks();
        registerControllerType("battery_controller_be", "battery/controller", MBBatteryController::new);
    }

    @FunctionalInterface
    private interface ControllerFactory<T extends MBControllerBlockEntity> {
        T create(BlockEntityType<T> type, BlockPos pos, BlockState state);
    }

    private static final class ControllerTypeRegistration<T extends MBControllerBlockEntity> {
        private RegistryObject<BlockEntityType<T>> blockEntityType;

        private void register(
            String registryName,
            String controllerBlockName,
            ControllerFactory<T> factory
        ) {
            blockEntityType = BLOCK_ENTITIES.register(registryName, () -> BlockEntityType.Builder.of(
                (pos, state) -> factory.create(blockEntityType.get(), pos, state),
                ModBlocks.MULTIBLOCKS_MAP.get(controllerBlockName).get()
            ).build(null));

            CONTROLLER_TYPES.put(controllerBlockName, blockEntityType);
        }
    }

    private static void registerCableBlocks() {
        for (RegistryObject<CableBlock> cable : ModBlocks.CABLE_BLOCKS_MAP.values()) {
            String path = cable.getId().getPath();
            String digits = path.replaceAll("\\D+", "");
            int tier = digits.isEmpty() ? 0 : Integer.parseInt(digits);

            RegistryObject<BlockEntityType<CableBlockEntity>> cableBE =
                BLOCK_ENTITIES.register("cable" + tier + "_be",
                    () -> BlockEntityType.Builder
                        .of((pos, state) -> new CableBlockEntity(pos, state, tier), cable.get())
                        .build(null)
                );

            CABLE_BLOCK_ENTITIES.put(tier, cableBE);
        }
    }

    private static void registerSolarBlocks() {
        for (RegistryObject<SolarPanelBlock> panel : ModBlocks.SOLAR_PANEL_BLOCK_MAP.values()) {
            String path = panel.getId().getPath();
            String digits = path.replaceAll("\\D+", "");
            int tier = digits.isEmpty() ? 0 : Integer.parseInt(digits);

            RegistryObject<BlockEntityType<SolarPanelBlockEntity>> panelBE =
                BLOCK_ENTITIES.register("solar_panel" + tier + "_be",
                    () -> BlockEntityType.Builder
                        .of((pos, state) -> new SolarPanelBlockEntity(pos, state, tier), panel.get())
                        .build(null)
                );

            SOLAR_BLOCK_ENTITIES.put(tier, panelBE);
        }
    }

    private static void registerBatteryBlocks() {
        for (RegistryObject<BatteryBlock> battery : ModBlocks.BATTERY_BLOCK_MAP.values()) {
            String path = battery.getId().getPath();
            String digits = path.replaceAll("\\D+", "");
            int tier = digits.isEmpty() ? 0 : Integer.parseInt(digits);

            RegistryObject<BlockEntityType<BatteryBlockEntity>> batteryBE =
                BLOCK_ENTITIES.register("battery" + tier + "_be",
                    () -> BlockEntityType.Builder
                        .of((pos, state) -> new BatteryBlockEntity(pos, state, tier), battery.get())
                        .build(null)
                );

            BATTERY_BLOCK_ENTITIES.put(tier, batteryBE);
        }
    }
}
