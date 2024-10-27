package net.dafarka.metallurgyplus.datagen;

import net.dafarka.metallurgyplus.MetallurgyPlus;
import net.dafarka.metallurgyplus.item.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, MetallurgyPlus.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        simpleItem(ModItems.STEEL_INGOT);
        simpleItem(ModItems.ALUMINUM_INGOT);
        simpleItem(ModItems.BARIUM_INGOT);
        simpleItem(ModItems.BERYLLIUM_INGOT);
        simpleItem(ModItems.CADMIUM_INGOT);
        simpleItem(ModItems.CHROMIUM_INGOT);
        simpleItem(ModItems.COBALT_INGOT);
        simpleItem(ModItems.GALLIUM_INGOT);
        simpleItem(ModItems.HAFNIUM_INGOT);
        simpleItem(ModItems.INDIUM_INGOT);
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
        simpleItem(ModItems.ZIRCONIUM_INGOT);

        simpleItem(ModItems.CLAY_MINERAL_RAW);
        simpleItem(ModItems.ALUMINUM_RAW);
        simpleItem(ModItems.BAUXITE);
        simpleItem(ModItems.KAOLINITE);

        simpleItem(ModItems.SILICON);
    }

    private ItemModelBuilder simpleItem(RegistryObject<Item> item) {
        return withExistingParent(item.getId().getPath(),
            new ResourceLocation("item/generated")).texture("layer0",
            new ResourceLocation(MetallurgyPlus.MODID,"item/" + item.getId().getPath()));
    }
}
