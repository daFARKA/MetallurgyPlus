package net.dafarka.metallurgyplus.block.entity;

import net.dafarka.metallurgyplus.MetallurgyPlus;
import net.dafarka.metallurgyplus.block.ModBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MetallurgyPlus.MODID);

    public static final RegistryObject<BlockEntityType<OreProcessingUnitBlockEntity>> ORE_PROCESSING_BE =
        BLOCK_ENTITIES.register("ore_processing_be", () -> BlockEntityType.Builder.of(OreProcessingUnitBlockEntity::new,
            ModBlocks.ORE_PROCESSING_UNIT.get()).build(null));

    public static final RegistryObject<BlockEntityType<AlloySmelterBlockEntity>> ALLOY_SMELTER_BE =
        BLOCK_ENTITIES.register("alloy_smelter_be", () -> BlockEntityType.Builder.of(AlloySmelterBlockEntity::new,
            ModBlocks.ALLOY_SMELTER.get()).build(null));

    public static final RegistryObject<BlockEntityType<PowerSourceBlockEntity>> POWER_SOURCE_BE =
        BLOCK_ENTITIES.register("power_source_be", () -> BlockEntityType.Builder.of(PowerSourceBlockEntity::new,
            ModBlocks.POWER_SOURCE.get()).build(null));

    public static final RegistryObject<BlockEntityType<BatteryBlockEntity>> BATTERY_BE =
        BLOCK_ENTITIES.register("battery_be", () -> BlockEntityType.Builder.of(BatteryBlockEntity::new,
            ModBlocks.BATTERY.get()).build(null));


    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
