package net.dafarka.metallurgyplus.worldgen.placement;

import net.dafarka.metallurgyplus.MetallurgyPlus;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModPlacementModifiers {

    public static final DeferredRegister<PlacementModifierType<?>> PLACEMENT_MODIFIERS =
        DeferredRegister.create(
            Registries.PLACEMENT_MODIFIER_TYPE,
            MetallurgyPlus.MODID
        );

    public static final RegistryObject<PlacementModifierType<ClayMineralPlacement>> CLAY_MINERAL =
        PLACEMENT_MODIFIERS.register(
            "clay_mineral",
            () -> () -> ClayMineralPlacement.CODEC
        );
}