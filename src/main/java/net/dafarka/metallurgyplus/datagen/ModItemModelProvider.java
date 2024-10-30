package net.dafarka.metallurgyplus.datagen;

import net.dafarka.metallurgyplus.MetallurgyPlus;
import net.dafarka.metallurgyplus.block.ModBlocks;
import net.dafarka.metallurgyplus.item.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, MetallurgyPlus.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        generateMaterials();

        /*simpleItem(ModItems.INDIUM_INGOT);
        simpleItem(ModItems.IRIDIUM_INGOT);
        simpleItem(ModItems.LEAD_INGOT);
        simpleItem(ModItems.MAGNESIUM_INGOT);
        simpleItem(ModItems.MANGANESE_INGOT);
        simpleItem(ModItems.MOLYBDENUM_INGOT);
        simpleItem(ModItems.NICKEL_INGOT);
        simpleItem(ModItems.NIOBIUM_INGOT);
        simpleItem(ModItems.OSMIUM_INGOT);
        simpleItem(ModItems.PALLADIUM_INGOT);
        simpleItem(ModItems.PLATINUM_INGOT);
        simpleItem(ModItems.RHENIUM_INGOT);
        simpleItem(ModItems.RHODIUM_INGOT);
        simpleItem(ModItems.RUTHENIUM_INGOT);
        simpleItem(ModItems.SCANDIUM_INGOT);
        simpleItem(ModItems.SILVER_INGOT);
        simpleItem(ModItems.TANTALUM_INGOT);
        simpleItem(ModItems.TECHNETIUM_INGOT);
        simpleItem(ModItems.THALLIUM_INGOT);
        simpleItem(ModItems.TIN_INGOT);
        simpleItem(ModItems.TITANIUM_INGOT);
        simpleItem(ModItems.TUNGSTEN_INGOT);
        simpleItem(ModItems.VANADIUM_INGOT);
        simpleItem(ModItems.YTTRIUM_INGOT);
        simpleItem(ModItems.ZINC_INGOT);
        simpleItem(ModItems.ZIRCONIUM_INGOT);*/

        simpleItem(ModItems.CLAY_MINERAL_RAW);
        simpleItem(ModItems.BAUXITE);
        simpleItem(ModItems.KAOLINITE);

        simpleItem(ModItems.SILICON);
    }

    private ItemModelBuilder simpleItem(RegistryObject<Item> item) {
        return withExistingParent(item.getId().getPath(),
            new ResourceLocation("item/generated")).texture("layer0",
            new ResourceLocation(MetallurgyPlus.MODID,"item/" + item.getId().getPath()));
    }

    private ItemModelBuilder simpleBaseItem(RegistryObject<Item> item, String compoundName) {
        return withExistingParent(item.getId().getPath(),
            new ResourceLocation("item/generated")).texture("layer0",
            new ResourceLocation(MetallurgyPlus.MODID,"item/base_" + compoundName));
    }

    private void generateMaterials() {
        for (RegistryObject<Item> item : ModItems.MATERIAL_MAP.values()) {
            String currentName = item.getId().getPath();
            String currentComponentName = currentName.split("_")[1];
            simpleBaseItem(item, currentComponentName);
        }

        generateMaterialBlockItems();
    }

    private void generateMaterialBlockItems() {
        for (RegistryObject<Block> block : ModBlocks.MATERIAL_BLOCKS_MAP.values()) {
            String blockName = block.get().getDescriptionId().split("\\.")[2];
            simpleBlockItemModel(blockName);
        }
    }

    public void simpleBlockItemModel(String modelName) {
        // Generate the item model with the specified parent and display properties
        getBuilder(modelName)
            .parent(new ModelFile.UncheckedModelFile(modLoc("block/" + modelName)))
            .transforms()
            .transform(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND)
            .rotation(10, -45, 170)
            .translation(0, 1.5f, -2.75f)
            .scale(0.375f, 0.375f, 0.375f)
            .end();
    }
}
