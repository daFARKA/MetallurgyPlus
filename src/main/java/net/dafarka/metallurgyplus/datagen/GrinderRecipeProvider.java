package net.dafarka.metallurgyplus.datagen;

import net.dafarka.metallurgyplus.MetallurgyPlus;
import net.dafarka.metallurgyplus.block.ModBlocks;
import net.dafarka.metallurgyplus.datagen.recipe.MachineFinishedRecipeWithExtraOutputs;
import net.dafarka.metallurgyplus.item.ModItems;
import net.dafarka.metallurgyplus.recipe.ModRecipeSerializers;
import net.dafarka.metallurgyplus.util.Utility;
import net.minecraft.core.NonNullList;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class GrinderRecipeProvider extends RecipeProvider {

    public GrinderRecipeProvider(PackOutput pOutput) {
        super(pOutput);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> pWriter) {
        int inputSize;
        NonNullList<Item> inputItems;
        NonNullList<Integer> inputAmounts;
        ItemStack output;
        NonNullList<ItemStack> extraOutputs = null;
        NonNullList<Float> extraChances = null;

        // From this point onwards custom recipes can be added. Use the below example to construct recipes.
        /*
        inputSize = 1;
        inputItems = NonNullList.withSize(inputSize, new Item(new Item.Properties()));
        inputAmounts = NonNullList.withSize(inputSize, 1);
        inputItems.set(0, Utility.getItem("coal"));
        inputAmounts.set(0, 1);
        output = new ItemStack(Utility.getItem("brick"), 1);
        extraOutputs = NonNullList.withSize(1, ItemStack.EMPTY);
        extraOutputs.set(0, new ItemStack(Utility.getItem("redstone"), 1));
        extraChances.set(0, 0.25f);
        addGrinderFinishedRecipe(pWriter, inputItems, inputAmounts, output, extraOutputs, extraChances, new ResourceLocation(MetallurgyPlus.MODID, "test_grinder"));*/

        inputSize = 1;
        inputItems = NonNullList.withSize(inputSize, new Item(new Item.Properties()));
        inputAmounts = NonNullList.withSize(inputSize, 1);
        inputItems.set(0, Utility.getItem("quartz"));
        inputAmounts.set(0, 1);
        output = new ItemStack(ModItems.CUSTOM_ITEM_MAP.get("quartz_dust").get(), 2);
        addGrinderFinishedRecipe(pWriter, inputItems, inputAmounts, output, null, null, new ResourceLocation(MetallurgyPlus.MODID, "quartz_to_quartz_dust_grinder"));

        List<String> oldMaterials = new ArrayList<>();
        for (RegistryObject<Item> item : ModItems.MATERIAL_MAP.values()) {
            String currentName = item.getId().getPath();
            String currentMaterialName = currentName.split("_")[0];
            if (!oldMaterials.contains(currentMaterialName)) {
                oldMaterials.add(currentMaterialName);

                Item ingot = ModItems.MATERIAL_MAP.get(currentMaterialName + "_" + ModItems.MATERIAL_COMPONENT_NAMES[0]).get();
                Item dust = ModItems.MATERIAL_MAP.get(currentMaterialName + "_" + ModItems.MATERIAL_COMPONENT_NAMES[1]).get();
                Item gear = ModItems.MATERIAL_MAP.get(currentMaterialName + "_" + ModItems.MATERIAL_COMPONENT_NAMES[2]).get();
                Item nugget = ModItems.MATERIAL_MAP.get(currentMaterialName + "_" + ModItems.MATERIAL_COMPONENT_NAMES[3]).get();
                Item plate = ModItems.MATERIAL_MAP.get(currentMaterialName + "_" + ModItems.MATERIAL_COMPONENT_NAMES[4]).get();
                Item rod = ModItems.MATERIAL_MAP.get(currentMaterialName + "_" + ModItems.MATERIAL_COMPONENT_NAMES[5]).get();
                Item raw = ModItems.MATERIAL_MAP.get(currentMaterialName + "_" + ModItems.MATERIAL_COMPONENT_NAMES[6]).get();

                inputSize = 1;
                inputItems = NonNullList.withSize(inputSize, new Item(new Item.Properties()));
                inputAmounts = NonNullList.withSize(inputSize, 1);
                inputItems.set(0, ingot);
                inputAmounts.set(0, 1);
                output = new ItemStack(dust, 1);
                addGrinderFinishedRecipe(pWriter, inputItems, inputAmounts, output, extraOutputs, extraChances, new ResourceLocation(MetallurgyPlus.MODID, currentMaterialName + "_ingot_to_dust_grinder"));

                inputSize = 1;
                inputItems = NonNullList.withSize(inputSize, new Item(new Item.Properties()));
                inputAmounts = NonNullList.withSize(inputSize, 1);
                inputItems.set(0, raw);
                inputAmounts.set(0, 1);
                output = new ItemStack(dust, 2);
                addGrinderFinishedRecipe(pWriter, inputItems, inputAmounts, output, extraOutputs, extraChances, new ResourceLocation(MetallurgyPlus.MODID, currentMaterialName + "_raw_to_dust_grinder"));
            }
        }

        for (RegistryObject<Item> item : ModItems.ORE_MAP.values()) {
            String currentName = item.getId().getPath();
            String currentMaterialName = currentName.split("_")[0];
            if (!oldMaterials.contains(currentMaterialName)) {
                oldMaterials.add(currentMaterialName);

                Item raw = ModItems.ORE_MAP.get(currentMaterialName + "_" + ModItems.ORE_COMPONENT_NAMES[0]).get();
                Item dust = ModItems.ORE_MAP.get(currentMaterialName + "_" + ModItems.ORE_COMPONENT_NAMES[1]).get();

                Item ore_stone = ModBlocks.ORE_BLOCKS_MAP.get(currentMaterialName + "_" + ModItems.ORE_BASE_NAME[0] + "_block").get().asItem();
                Item ore_deepslate = ModBlocks.ORE_BLOCKS_MAP.get(currentMaterialName + "_" + ModItems.ORE_BASE_NAME[1] + "_block").get().asItem();

                inputSize = 1;
                inputItems = NonNullList.withSize(inputSize, new Item(new Item.Properties()));
                inputAmounts = NonNullList.withSize(inputSize, 1);
                inputItems.set(0, ore_stone.asItem());
                inputAmounts.set(0, 1);
                output = new ItemStack(raw, 3);
                addGrinderFinishedRecipe(pWriter, inputItems, inputAmounts, output, extraOutputs, extraChances, new ResourceLocation(MetallurgyPlus.MODID, currentMaterialName + "_ore_stone_to_raw_grinder"));

                inputSize = 1;
                inputItems = NonNullList.withSize(inputSize, new Item(new Item.Properties()));
                inputAmounts = NonNullList.withSize(inputSize, 1);
                inputItems.set(0, ore_deepslate.asItem());
                inputAmounts.set(0, 1);
                output = new ItemStack(raw, 3);
                addGrinderFinishedRecipe(pWriter, inputItems, inputAmounts, output, extraOutputs, extraChances, new ResourceLocation(MetallurgyPlus.MODID, currentMaterialName + "_ore_deepslate_to_raw_grinder"));
            }
        }

        for (RegistryObject<Item> item : ModItems.VANILLA_MAP.values()) {
            String currentName = item.getId().getPath();
            String currentMaterialName = currentName.split("_")[0];
            if (!oldMaterials.contains(currentMaterialName)) {
                oldMaterials.add(currentMaterialName);

                Item ingot = Utility.getItem(currentMaterialName + "_ingot");
                if (ingot == Items.AIR) {
                    ingot = Utility.getItem(currentMaterialName);
                }
                Item raw = Utility.getItem("raw_" + currentMaterialName);

                Item dust = ModItems.VANILLA_MAP.get(currentMaterialName + "_" + ModItems.VANILLA_COMPONENT_NAMES[0]).get();
                Item gear = ModItems.VANILLA_MAP.get(currentMaterialName + "_" + ModItems.VANILLA_COMPONENT_NAMES[1]).get();
                Item plate = ModItems.VANILLA_MAP.get(currentMaterialName + "_" + ModItems.VANILLA_COMPONENT_NAMES[2]).get();
                Item rod = ModItems.VANILLA_MAP.get(currentMaterialName + "_" + ModItems.VANILLA_COMPONENT_NAMES[3]).get();

                if (ingot != Items.AIR) {
                    inputSize = 1;
                    inputItems = NonNullList.withSize(inputSize, new Item(new Item.Properties()));
                    inputAmounts = NonNullList.withSize(inputSize, 1);
                    inputItems.set(0, ingot);
                    inputAmounts.set(0, 1);
                    output = new ItemStack(dust, 1);
                    addGrinderFinishedRecipe(pWriter, inputItems, inputAmounts, output, extraOutputs, extraChances, new ResourceLocation(MetallurgyPlus.MODID, currentMaterialName + "_ingot_to_dust_grinder"));
                }

                if (raw != Items.AIR) {
                    inputSize = 1;
                    inputItems = NonNullList.withSize(inputSize, new Item(new Item.Properties()));
                    inputAmounts = NonNullList.withSize(inputSize, 1);
                    inputItems.set(0, raw);
                    inputAmounts.set(0, 1);
                    output = new ItemStack(dust, 2);
                    addGrinderFinishedRecipe(pWriter, inputItems, inputAmounts, output, extraOutputs, extraChances, new ResourceLocation(MetallurgyPlus.MODID, currentMaterialName + "_raw_to_dust_grinder"));
                }
            }
        }
    }

    private void addGrinderFinishedRecipe(Consumer<FinishedRecipe> pWriter, NonNullList<Item> inputItems, NonNullList<Integer> inputAmounts,
                                          ItemStack output, NonNullList<ItemStack> extraOutputs, NonNullList<Float> extraChances, ResourceLocation id) {
        pWriter.accept(new MachineFinishedRecipeWithExtraOutputs(
            inputItems, inputAmounts, output, extraOutputs, extraChances, id, "metallurgyplus:grinder", ModRecipeSerializers.GRINDER_SERIALIZER.get()
        ));
    }
}
