package net.dafarka.metallurgyplus;

import com.mojang.logging.LogUtils;
import net.dafarka.metallurgyplus.block.ModBlocks;
import net.dafarka.metallurgyplus.block.entity.ModBlockEntities;
import net.dafarka.metallurgyplus.item.ModCreativeTabs;
import net.dafarka.metallurgyplus.item.ModItems;
import net.dafarka.metallurgyplus.recipe.ModRecipes;
import net.dafarka.metallurgyplus.screen.AlloySmelterScreen;
import net.dafarka.metallurgyplus.screen.ModMenuTypes;
import net.dafarka.metallurgyplus.screen.OreProcessingUnitScreen;
import net.dafarka.metallurgyplus.util.color.ModDynamicAlloyBlockColor;
import net.dafarka.metallurgyplus.util.color.ModDynamicMaterialBlockColor;
import net.dafarka.metallurgyplus.util.color.ModDynamicItemColor;
import net.dafarka.metallurgyplus.util.color.ModDynamicOreBlockColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
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

// The value here should match an entry in the META-INF/mods.toml file
@Mod(MetallurgyPlus.MODID)
public class MetallurgyPlus
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "metallurgyplus";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public MetallurgyPlus()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModCreativeTabs.register(modEventBus);

        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModMenuTypes.register(modEventBus);
        ModRecipes.register(modEventBus);

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);

        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {

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
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            LOGGER.info("Hello from MetallurgyPlus!");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());

            MenuScreens.register(ModMenuTypes.ORE_PROCESSING_MENU.get(), OreProcessingUnitScreen::new);
            MenuScreens.register(ModMenuTypes.ALLOY_SMELTER_MENU.get(), AlloySmelterScreen::new);

            for (RegistryObject<Block> ore : ModBlocks.ORE_BLOCKS_MAP.values()) {
                ItemBlockRenderTypes.setRenderLayer(ore.get(), RenderType.translucent());
            }

            ItemColors itemColors = Minecraft.getInstance().getItemColors();
            for (RegistryObject<Item> item : ModItems.ITEMS.getEntries()) {
                itemColors.register(new ModDynamicItemColor(), item.get());
            }

            BlockColors blockColors = Minecraft.getInstance().getBlockColors();
            for (RegistryObject<Block> block : ModBlocks.MATERIAL_BLOCKS_MAP.values()) {
                blockColors.register(new ModDynamicMaterialBlockColor(), block.get());
            }
            for (RegistryObject<Block> block : ModBlocks.ORE_BLOCKS_MAP.values()) {
                blockColors.register(new ModDynamicOreBlockColor(), block.get());
            }
            for (RegistryObject<Block> block : ModBlocks.ALLOY_BLOCKS_MAP.values()) {
                blockColors.register(new ModDynamicAlloyBlockColor(), block.get());
            }
        }
    }
}
