package net.dafarka.metallurgyplus.block.entity;

import com.google.common.collect.Maps;
import net.dafarka.metallurgyplus.MetallurgyPlus;
import net.dafarka.metallurgyplus.block.ModBlocks;
import net.dafarka.metallurgyplus.block.custom.BatteryBlock;
import net.dafarka.metallurgyplus.block.custom.CableBlock;
import net.dafarka.metallurgyplus.block.custom.QuarryBlock;
import net.dafarka.metallurgyplus.block.custom.SolarPanelBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.Map;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MetallurgyPlus.MODID);

    public static final Map<Integer, RegistryObject<BlockEntityType<CableBlockEntity>>> CABLE_BLOCK_ENTITIES = new HashMap<>();
    public static final Map<Integer, RegistryObject<BlockEntityType<SolarPanelBlockEntity>>> SOLAR_BLOCK_ENTITIES = new HashMap<>();
    public static final Map<Integer, RegistryObject<BlockEntityType<BatteryBlockEntity>>> BATTERY_BLOCK_ENTITIES = new HashMap<>();

    public static final RegistryObject<BlockEntityType<OreProcessingUnitBlockEntity>> ORE_PROCESSING_BE =
        BLOCK_ENTITIES.register("ore_processing_be", () -> BlockEntityType.Builder.of(OreProcessingUnitBlockEntity::new,
            ModBlocks.ORE_PROCESSING_UNIT.get()).build(null));

    public static final RegistryObject<BlockEntityType<AlloySmelterBlockEntity>> ALLOY_SMELTER_BE =
        BLOCK_ENTITIES.register("alloy_smelter_be", () -> BlockEntityType.Builder.of(AlloySmelterBlockEntity::new,
            ModBlocks.ALLOY_SMELTER.get()).build(null));

    public static final RegistryObject<BlockEntityType<GrinderBlockEntity>> GRINDER_BE =
        BLOCK_ENTITIES.register("grinder_be", () -> BlockEntityType.Builder.of(GrinderBlockEntity::new,
            ModBlocks.GRINDER.get()).build(null));

    public static final RegistryObject<BlockEntityType<QuarryBlockEntity>> QUARRY_BE =
        BLOCK_ENTITIES.register("quarry_be", () -> BlockEntityType.Builder.of(QuarryBlockEntity::new,
            ModBlocks.QUARRY.get()).build(null));

    public static final RegistryObject<BlockEntityType<PowerSourceBlockEntity>> POWER_SOURCE_BE =
        BLOCK_ENTITIES.register("power_source_be", () -> BlockEntityType.Builder.of(PowerSourceBlockEntity::new,
            ModBlocks.POWER_SOURCE.get()).build(null));


    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
        registerCableBlocks();
        registerSolarBlocks();
        registerBatteryBlocks();
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