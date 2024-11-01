package net.dafarka.metallurgyplus.block;

import net.dafarka.metallurgyplus.MetallurgyPlus;
import net.dafarka.metallurgyplus.block.custom.AlloySmelterBlock;
import net.dafarka.metallurgyplus.block.custom.OreProcessingUnitBlock;
import net.dafarka.metallurgyplus.item.ModItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MetallurgyPlus.MODID);
    public static final Map<String, RegistryObject<Block>> MATERIAL_BLOCKS_MAP = new HashMap<>();
    public static final Map<String, RegistryObject<Block>> ORE_BLOCKS_MAP = new HashMap<>();
    public static final Map<String, RegistryObject<Block>> ALLOY_BLOCKS_MAP = new HashMap<>();
    public static final Map<String, Integer> MATERIAL_COLOR_MAP = new HashMap<>();
    public static final Map<String, Integer> ORE_COLOR_MAP = new HashMap<>();
    public static final Map<String, Integer> ALLOY_COLOR_MAP = new HashMap<>();

    public static final RegistryObject<Block> CLAY_MINERAL = registerBlock("clay_mineral",
        () -> new Block(BlockBehaviour.Properties.copy(Blocks.CLAY).sound(SoundType.GRAVEL)));

    public static final RegistryObject<Block> ORE_PROCESSING_UNIT = registerBlock("ore_processing_unit",
        () -> new OreProcessingUnitBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).sound(SoundType.METAL).noOcclusion()));

    public static final RegistryObject<Block> ALLOY_SMELTER = registerBlock("alloy_smelter",
        () -> new AlloySmelterBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).sound(SoundType.METAL).noOcclusion()));

    public static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block) {
        return ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
