package net.dafarka.metallurgyplus;

import com.mojang.logging.LogUtils;
import net.dafarka.metallurgyplus.block.ModBlocks;
import net.dafarka.metallurgyplus.block.custom.BatteryBlock;
import net.dafarka.metallurgyplus.block.custom.CableBlock;
import net.dafarka.metallurgyplus.block.custom.SolarPanelBlock;
import net.dafarka.metallurgyplus.block.entity.ModBlockEntities;
import net.dafarka.metallurgyplus.block.entity.renderer.SackStationBlockEntityRenderer;
import net.dafarka.metallurgyplus.item.ModCreativeTabs;
import net.dafarka.metallurgyplus.item.ModItems;
import net.dafarka.metallurgyplus.network.ModMessages;
import net.dafarka.metallurgyplus.recipe.ModRecipeSerializers;
import net.dafarka.metallurgyplus.screen.*;
import net.dafarka.metallurgyplus.screen.menu.ModMenuTypes;
import net.dafarka.metallurgyplus.util.color.DynamicItemColor;
import net.dafarka.metallurgyplus.util.color.DynamicKeyColor;
import net.dafarka.metallurgyplus.worldgen.placement.ModPlacementModifiers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

import java.util.HashSet;
import java.util.Set;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(MetallurgyPlus.MODID)
public class MetallurgyPlus {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "metallurgyplus";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public MetallurgyPlus() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModCreativeTabs.register(modEventBus);

        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModMenuTypes.register(modEventBus);
        ModRecipeSerializers.register(modEventBus);

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);

        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        // Register our mod's PlacementModifiers
        ModPlacementModifiers.PLACEMENT_MODIFIERS.register(modEventBus);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(ModMessages::register);
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {

    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("HELLO from server starting");
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void registerBlockColors(RegisterColorHandlersEvent.Block event) {
            DynamicKeyColor<Integer> cableColor = DynamicKeyColor.byTier(ModBlocks.CABLE_COLOR_MAP);
            DynamicKeyColor<Integer> solarColor = DynamicKeyColor.byTier(ModBlocks.SOLAR_PANEL_COLOR_MAP);
            DynamicKeyColor<Integer> batteryColor = DynamicKeyColor.byTier(ModBlocks.BATTERY_COLOR_MAP);

            DynamicKeyColor<String> alloyColor = DynamicKeyColor.byName(ModBlocks.ALLOY_COLOR_MAP);
            DynamicKeyColor<String> materialColor = DynamicKeyColor.byName(ModBlocks.MATERIAL_COLOR_MAP);
            DynamicKeyColor<String> oreColor = DynamicKeyColor.byName(ModBlocks.ORE_COLOR_MAP);
            DynamicKeyColor<String> gemColor = DynamicKeyColor.byName(ModBlocks.GEM_COLOR_MAP);
            DynamicKeyColor<String> multiblocksColor = DynamicKeyColor.byName(ModBlocks.MULTIBLOCK_COLOR_MAP);

            // Blocks
            for (RegistryObject<Block> b : ModBlocks.MATERIAL_BLOCKS_MAP.values())
                event.register(materialColor, b.get());
            for (RegistryObject<Block> b : ModBlocks.ORE_BLOCKS_MAP.values())
                event.register(oreColor, b.get());
            for (RegistryObject<Block> b : ModBlocks.ALLOY_BLOCKS_MAP.values())
                event.register(alloyColor, b.get());
            for (RegistryObject<Block> b : ModBlocks.GEM_BLOCKS_MAP.values())
                event.register(gemColor, b.get());

            for (RegistryObject<CableBlock> b : ModBlocks.CABLE_BLOCKS_MAP.values())
                event.register(cableColor, b.get());
            for (RegistryObject<SolarPanelBlock> b : ModBlocks.SOLAR_PANEL_BLOCK_MAP.values())
                event.register(solarColor, b.get());
            for (RegistryObject<BatteryBlock> b : ModBlocks.BATTERY_BLOCK_MAP.values())
                event.register(batteryColor, b.get());
            for (RegistryObject<? extends Block> b : ModBlocks.MULTIBLOCKS_MAP.values())
                event.register(multiblocksColor, b.get());
        }

        @SubscribeEvent
        public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
            DynamicKeyColor<Integer> cableColor = DynamicKeyColor.byTier(ModBlocks.CABLE_COLOR_MAP);
            DynamicKeyColor<Integer> solarColor = DynamicKeyColor.byTier(ModBlocks.SOLAR_PANEL_COLOR_MAP);
            DynamicKeyColor<Integer> batteryColor = DynamicKeyColor.byTier(ModBlocks.BATTERY_COLOR_MAP);
            DynamicKeyColor<Integer> coilColor = DynamicKeyColor.byTier(ModItems.COIL_COLOR_MAP);
            DynamicKeyColor<Integer> sackColor = DynamicKeyColor.byTier(ModItems.SACK_COLOR_MAP);

            DynamicKeyColor<String> multiblocksColor = DynamicKeyColor.byName(ModBlocks.MULTIBLOCK_COLOR_MAP);

            Set<Item> handledItems = new HashSet<>();

            for (RegistryObject<CableBlock> b : ModBlocks.CABLE_BLOCKS_MAP.values()) {
                Item item = b.get().asItem();
                event.register(cableColor, item);
                handledItems.add(item);
            }
            for (RegistryObject<SolarPanelBlock> b : ModBlocks.SOLAR_PANEL_BLOCK_MAP.values()) {
                Item item = b.get().asItem();
                event.register(solarColor, item);
                handledItems.add(item);
            }
            for (RegistryObject<BatteryBlock> b : ModBlocks.BATTERY_BLOCK_MAP.values()) {
                Item item = b.get().asItem();
                event.register(batteryColor, item);
                handledItems.add(item);
            }
            for (RegistryObject<? extends Block> b : ModBlocks.MULTIBLOCKS_MAP.values()) {
                Item item = b.get().asItem();
                event.register(multiblocksColor, item);
                handledItems.add(item);
            }

            for (RegistryObject<Item> item : ModItems.COIL_MAP.values()) {
                event.register(coilColor, item.get());
                handledItems.add(item.get());
            }
            for (RegistryObject<Item> item : ModItems.SACK_MAP.values()) {
                event.register(sackColor, item.get());
                handledItems.add(item.get());
            }

            DynamicItemColor dynamicItemColor = new DynamicItemColor();
            for (RegistryObject<Item> entry : ModItems.ITEMS.getEntries()) {
                Item item = entry.get();

                // Skip ANY item that already received a custom handler
                if (handledItems.contains(item)) {
                    continue;
                }

                event.register(dynamicItemColor, item);
            }
        }

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            LOGGER.info("Hello from MetallurgyPlus!");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());

            MenuScreens.register(ModMenuTypes.MACHINE_MENU.get(), MachineScreen::new);
            MenuScreens.register(ModMenuTypes.QUARRY_MENU.get(), QuarryScreen::new);
            MenuScreens.register(ModMenuTypes.BATTERY_MENU.get(), BatteryScreen::new);
            MenuScreens.register(ModMenuTypes.SACK_MENU.get(), SackScreen::new);

            MenuScreens.register(ModMenuTypes.MULTIBLOCK_BATTERY_MENU.get(), MBBatteryScreen::new);

            for (RegistryObject<Block> ore : ModBlocks.ORE_BLOCKS_MAP.values()) {
                ItemBlockRenderTypes.setRenderLayer(ore.get(), RenderType.cutout());
            }

            ItemBlockRenderTypes.setRenderLayer(ModBlocks.SACK_STATION.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.MACHINE_FRAME.get(), RenderType.cutout());
        }

        @SubscribeEvent
        public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerBlockEntityRenderer(ModBlockEntities.SACK_STATION_BE.get(), SackStationBlockEntityRenderer::new);
        }
    }
}
