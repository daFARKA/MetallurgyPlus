package net.dafarka.metallurgyplus.recipe.tool;

import net.dafarka.metallurgyplus.item.tool.ModularTool;
import net.dafarka.metallurgyplus.item.tool.ModularToolPartItem;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public abstract class ModularToolRecipe extends CustomRecipe {
    // Item slots in row-major order, skipping the tool in the center of a 3x3 grid.
    private static final int[] MODIFIER_MATERIAL_SLOTS = {0, 1, 2, 3, 5, 6, 7, 8};
    private static final List<Item> SPEED_UPGRADE_MATERIALS = List.of(
        Items.REDSTONE_BLOCK, Items.REDSTONE_BLOCK, Items.REDSTONE_BLOCK,
        Items.REDSTONE_BLOCK, Items.REDSTONE_BLOCK, Items.REDSTONE_BLOCK,
        Items.REDSTONE_BLOCK, Items.REDSTONE_BLOCK
    );
    private static final List<Item> FORTUNE_UPGRADE_MATERIALS = List.of(
        Items.LAPIS_BLOCK, Items.LAPIS_BLOCK, Items.LAPIS_BLOCK,
        Items.LAPIS_BLOCK, Items.LAPIS_BLOCK, Items.LAPIS_BLOCK,
        Items.LAPIS_BLOCK, Items.LAPIS_BLOCK
    );
    private static final List<Item> SILK_TOUCH_UPGRADE_MATERIALS = List.of(
        Items.AMETHYST_BLOCK, Items.AMETHYST_BLOCK, Items.AMETHYST_BLOCK,
        Items.AMETHYST_BLOCK, Items.AMETHYST_BLOCK, Items.AMETHYST_BLOCK,
        Items.AMETHYST_BLOCK, Items.AMETHYST_BLOCK
    );

    public ModularToolRecipe(ResourceLocation id, CraftingBookCategory category) {
        super(id, category);
    }

    protected abstract boolean isToolHead(ItemStack stack);

    /**
     * Returns whether this recipe handles the supplied modular tool item.
     */
    protected abstract boolean isThisTool(ItemStack stack);

    protected abstract ItemStack createConfiguredTool(ItemStack head, ItemStack rod);

    @Override
    public boolean matches(CraftingContainer container, Level level) {
        return matchesAssembly(container) || matchesRepair(container)
            || matchesSpeedUpgrade(container) || matchesFortuneUpgrade(container)
            || matchesSilkTouchUpgrade(container);
    }

    private boolean matchesAssembly(CraftingContainer container) {
        boolean foundHead = false;
        boolean foundRod = false;

        for (int slot = 0; slot < container.getContainerSize(); slot++) {
            ItemStack stack = container.getItem(slot);
            if (stack.isEmpty()) continue;

            if (isToolHead(stack)) {
                if (foundHead) return false;
                foundHead = true;
            } else if (isToolRod(stack)) {
                if (foundRod) return false;
                foundRod = true;
            } else {
                return false;
            }
        }

        return foundHead && foundRod;
    }

    private boolean matchesRepair(CraftingContainer container) {
        ItemStack toolStack = ItemStack.EMPTY;
        ItemStack materialStack = ItemStack.EMPTY;

        for (int slot = 0; slot < container.getContainerSize(); slot++) {
            ItemStack stack = container.getItem(slot);
            if (stack.isEmpty()) continue;

            if (isThisTool(stack)) {
                if (!toolStack.isEmpty()) return false;
                toolStack = stack;
            } else {
                if (!materialStack.isEmpty()) return false;
                materialStack = stack;
            }
        }

        if (toolStack.isEmpty() || materialStack.isEmpty()) return false;
        ModularTool tool = (ModularTool) toolStack.getItem();
        return tool.getCustomDamage(toolStack) > 0
            && materialStack.is(getRepairMaterial(tool, toolStack));
    }

    private boolean matchesSpeedUpgrade(CraftingContainer container) {
        return matchesModifierUpgrade(container, SPEED_UPGRADE_MATERIALS);
    }

    private boolean matchesFortuneUpgrade(CraftingContainer container) {
        if (!matchesModifierUpgrade(container, FORTUNE_UPGRADE_MATERIALS)) return false;

        ItemStack toolStack = getToolStack(container);
        ModularTool tool = (ModularTool) toolStack.getItem();
        return !tool.hasSilkTouch(toolStack)
            && tool.getFortuneBonus(toolStack) < ModularTool.MAX_FORTUNE_BONUS;
    }

    private boolean matchesSilkTouchUpgrade(CraftingContainer container) {
        if (!matchesModifierUpgrade(container, SILK_TOUCH_UPGRADE_MATERIALS)) return false;

        ItemStack toolStack = getToolStack(container);
        ModularTool tool = (ModularTool) toolStack.getItem();
        return !tool.hasSilkTouch(toolStack) && tool.getFortuneBonus(toolStack) <= 0.0F;
    }

    private boolean matchesModifierUpgrade(CraftingContainer container, List<Item> modifierMaterials) {
        if (modifierMaterials.size() != MODIFIER_MATERIAL_SLOTS.length
            || container.getContainerSize() != 9) return false;

        ItemStack toolStack = container.getItem(4);
        if (toolStack.isEmpty() || !isThisTool(toolStack)) return false;

        for (int i = 0; i < MODIFIER_MATERIAL_SLOTS.length; i++) {
            ItemStack materialStack = container.getItem(MODIFIER_MATERIAL_SLOTS[i]);
            if (!materialStack.is(modifierMaterials.get(i))) return false;
        }

        ModularTool tool = (ModularTool) toolStack.getItem();
        return tool.getModifiers(toolStack) > 0;
    }

    @Override
    public ItemStack assemble(CraftingContainer container, RegistryAccess registryAccess) {
        ItemStack toolStack = getToolStack(container);
        if (!toolStack.isEmpty() && matchesRepair(container)) {
            ModularTool tool = (ModularTool) toolStack.getItem();
            ItemStack repaired = toolStack.copyWithCount(1);
            tool.repair(repaired, Math.max(1, tool.getMaxDurability(repaired) / 4));
            return repaired;
        }
        if (!toolStack.isEmpty() && matchesSpeedUpgrade(container)) {
            ModularTool tool = (ModularTool) toolStack.getItem();
            int modifiers = tool.getModifiers(toolStack);
            if (modifiers > 0) {
                ItemStack upgraded = toolStack.copyWithCount(1);
                tool.addMiningSpeedBonus(upgraded, 0.5F);
                tool.setModifiers(upgraded, modifiers - 1);
                return upgraded;
            }
        }
        if (!toolStack.isEmpty() && matchesFortuneUpgrade(container)) {
            ModularTool tool = (ModularTool) toolStack.getItem();
            ItemStack upgraded = toolStack.copyWithCount(1);
            tool.addFortuneBonus(upgraded, 0.5f);
            tool.setModifiers(upgraded, tool.getModifiers(toolStack) - 1);
            return upgraded;
        }
        if (!toolStack.isEmpty() && matchesSilkTouchUpgrade(container)) {
            ModularTool tool = (ModularTool) toolStack.getItem();
            ItemStack upgraded = toolStack.copyWithCount(1);
            if (tool.addSilkTouch(upgraded)) {
                tool.setModifiers(upgraded, tool.getModifiers(toolStack) - 1);
                return upgraded;
            }
        }

        ItemStack head = ItemStack.EMPTY;
        ItemStack rod = ItemStack.EMPTY;

        for (int slot = 0; slot < container.getContainerSize(); slot++) {
            ItemStack stack = container.getItem(slot);
            if (stack.isEmpty()) continue;
            if (isToolHead(stack)) head = stack;
            else if (isToolRod(stack)) rod = stack;
        }

        if (head.isEmpty() || rod.isEmpty()) return ItemStack.EMPTY;

        return createConfiguredTool(head, rod);
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    private static boolean isToolRod(ItemStack stack) {
        return stack.getItem() instanceof ModularToolPartItem part
            && part.getPartType() == ModularToolPartItem.PartType.ROD;
    }

    private ItemStack getToolStack(CraftingContainer container) {
        for (int slot = 0; slot < container.getContainerSize(); slot++) {
            ItemStack stack = container.getItem(slot);
            if (!stack.isEmpty() && isThisTool(stack)) return stack;
        }
        return ItemStack.EMPTY;
    }

    private static Item getRepairMaterial(ModularTool tool, ItemStack toolStack) {
        Item headItem = ForgeRegistries.ITEMS.getValue(tool.getHeadId(toolStack));
        if (!(headItem instanceof ModularToolPartItem head)
            || head.getPartType() != ModularToolPartItem.PartType.HEAD) return Items.AIR;

        ResourceLocation repairMaterialId = head.getRepairMaterialId();
        if (repairMaterialId == null) return Items.AIR;
        Item repairMaterial = ForgeRegistries.ITEMS.getValue(repairMaterialId);
        return repairMaterial == null ? Items.AIR : repairMaterial;
    }
}
