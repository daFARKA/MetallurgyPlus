package net.dafarka.metallurgyplus.recipe.tool;

import net.dafarka.metallurgyplus.item.ModItems;
import net.dafarka.metallurgyplus.item.tool.ModularPickaxeItem;
import net.dafarka.metallurgyplus.item.tool.ModularToolPartItem;
import net.dafarka.metallurgyplus.recipe.ModRecipeSerializers;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Shapeless recipe that preserves the selected head and rod on the crafted tool.
 */
public class ModularPickaxeRecipe extends ModularToolRecipe {
    public ModularPickaxeRecipe(ResourceLocation id, CraftingBookCategory category) {
        super(id, category);
    }

    @Override
    protected boolean isToolHead(ItemStack stack) {
        if (!(stack.getItem() instanceof ModularToolPartItem part)
            || part.getPartType() != ModularToolPartItem.PartType.HEAD) return false;
        ResourceLocation id = ForgeRegistries.ITEMS.getKey(stack.getItem());
        return id != null && id.getPath().endsWith("_pickaxe-head");
    }

    @Override
    protected ItemStack createConfiguredTool(ItemStack head, ItemStack rod) {
        return ModItems.MODULAR_PICKAXE.get().createConfiguredStack(head, rod);
    }

    @Override
    protected boolean isThisTool(ItemStack stack) {
        return stack.getItem() instanceof ModularPickaxeItem;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return ModItems.MODULAR_PICKAXE.get().createConfiguredStack(
            new ResourceLocation("metallurgyplus", "iron_pickaxe-head"),
            new ResourceLocation("metallurgyplus", "iron_rod")
        );
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.MODULAR_PICKAXE_SERIALIZER.get();
    }

}
