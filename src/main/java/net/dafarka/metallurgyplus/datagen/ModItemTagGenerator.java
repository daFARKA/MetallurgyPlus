package net.dafarka.metallurgyplus.datagen;

import net.dafarka.metallurgyplus.MetallurgyPlus;
import net.dafarka.metallurgyplus.item.ModItems;
import net.dafarka.metallurgyplus.util.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeItemTagsProvider;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ModItemTagGenerator extends ItemTagsProvider {

    public ModItemTagGenerator(PackOutput p_275343_, CompletableFuture<HolderLookup.Provider> p_275729_, CompletableFuture<TagLookup<Block>> p_275322_,
                               @Nullable ExistingFileHelper existingFileHelper) {
        super(p_275343_, p_275729_, p_275322_, MetallurgyPlus.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        this.tag(ItemTags.PICKAXES).add(ModItems.LLAMKANA.get());

        addForgeTags(ModItems.MATERIAL_MAP);
        addForgeTags(ModItems.ORE_MAP);
        addForgeTags(ModItems.ALLOY_MAP);
    }

    private void addForgeTags(Map<String, RegistryObject<Item>> itemMap) {
        for (RegistryObject<Item> item : itemMap.values()) {
            addForgeTag(item);
        }
    }

    private void addForgeTag(RegistryObject<Item> item) {
        String name = item.getId().getPath();
        TagKey<Item> tag = TagKey.create(
            net.minecraft.core.registries.Registries.ITEM,
            new ResourceLocation("forge", name)
        );

        this.tag(tag).add(item.get());
    }
}
