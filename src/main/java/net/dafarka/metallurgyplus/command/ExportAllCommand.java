package net.dafarka.metallurgyplus.command;

import net.dafarka.metallurgyplus.block.ModBlocks;
import net.dafarka.metallurgyplus.block.custom.MachineBlock;
import net.dafarka.metallurgyplus.item.ModItems;
import net.dafarka.metallurgyplus.recipe.MachineRecipe;
import net.dafarka.metallurgyplus.recipe.MachineRecipeWithExtraOutputs;
import net.dafarka.metallurgyplus.util.OreRarity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class ExportAllCommand {

    public static void execute() {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.level == null) {
            return;
        }

        if (minecraft.player == null) {
            return;
        }

        StringBuilder resources = new StringBuilder();
        StringBuilder recipes = new StringBuilder();

        appendResources(resources);
        appendRecipes(recipes, minecraft);


        Path outputDirectory = minecraft.gameDirectory
            .toPath()
            .resolve(ModClientCommands.EXPORT_PATH);

        Path outputFileResources = outputDirectory.resolve("resources.md");
        Path outputFileRecipes = outputDirectory.resolve("recipes.md");


        try {
            Files.createDirectories(outputDirectory);
            Files.writeString(outputFileResources, resources.toString());
            Files.writeString(outputFileRecipes, recipes.toString());

            minecraft.player.sendSystemMessage(
                Component.literal(
                    "Successfully exported all MetallurgyPlus data"
                )
            );
        } catch (IOException e) {
            minecraft.player.sendSystemMessage(
                Component.literal(
                    "Failed to export MetallurgyPlus data: "
                        + e.getMessage()
                )
            );

            e.printStackTrace();
        }
    }

    private static void appendResources(StringBuilder markdown) {
        markdown.append("# Resources\n\n");

        markdown.append(
            "*resourceName - hexColor - (RARITY)*\n\n"
        );

        appendTitle(markdown, "Materials");

        appendResourceSection(
            markdown,
            "Items",
            ModItems.MATERIAL_COLOR_MAP,
            false
        );

        appendResourceSection(
            markdown,
            "Blocks",
            ModBlocks.MATERIAL_COLOR_MAP,
            false
        );

        appendTitle(markdown, "Ores");

        appendResourceSection(
            markdown,
            "Items",
            ModItems.ORE_COLOR_MAP,
            false
        );

        appendResourceSection(
            markdown,
            "Blocks",
            ModBlocks.ORE_COLOR_MAP,
            true
        );

        appendTitle(markdown, "Alloys");

        appendResourceSection(
            markdown,
            "Items",
            ModItems.ALLOY_COLOR_MAP,
            false
        );

        appendResourceSection(
            markdown,
            "Blocks",
            ModBlocks.ALLOY_COLOR_MAP,
            false
        );

        appendTitle(markdown, "Vanilla Materials");

        appendResourceSection(
            markdown,
            "Items",
            ModItems.VANILLA_COLOR_MAP,
            false
        );

        appendTitle(markdown, "Gemstones");

        appendResourceSection(
            markdown,
            "Items",
            ModItems.GEM_COLOR_MAP,
            false
        );

        appendResourceSection(
            markdown,
            "Blocks",
            ModBlocks.GEM_COLOR_MAP,
            false
        );
    }

    private static void appendTitle(StringBuilder markdown, String title) {
        markdown.append("## ").append(title).append("\n\n");
    }

    private static void appendResourceSection(
        StringBuilder markdown,
        String title,
        Map<String, Integer> colors,
        boolean includeRarity
    ) {
        List<String> names = new ArrayList<>(colors.keySet());
        names.sort(String.CASE_INSENSITIVE_ORDER);

        if (title != null) {
            markdown.append("### ").append(title).append("\n\n");
        }

        for (String name : names) {
            markdown.append("* ")
                .append(name)
                .append(" - ")
                .append(formatHexColor(colors.get(name)));

            if (includeRarity) {
                markdown.append(" - ").append(getOreRarity(name));
            }

            markdown.append("\n");
        }

        markdown.append("\n");
    }

    private static String formatHexColor(int color) {
        return String.format("0x%06x", color & 0xFFFFFF);
    }

    private static String getOreRarity(String oreBlockName) {
        RegistryObject<Block> block = ModBlocks.ORE_BLOCKS_MAP.get(oreBlockName);

        if (block == null) {
            return "UNKNOWN";
        }

        OreRarity rarity = ModBlocks.ORE_RARITY_MAP.get(block);

        if (rarity == null) {
            return "UNKNOWN";
        }

        return rarity.name();
    }

    private static void appendRecipes(
        StringBuilder markdown,
        Minecraft minecraft
    ) {
        markdown.append("# Recipes\n\n");

        appendMachineRecipes(
            markdown,
            minecraft,
            "Ore Processing Unit",
            ModBlocks.ORE_PROCESSING_UNIT.get()
        );

        appendMachineRecipes(
            markdown,
            minecraft,
            "Alloy Smelter",
            ModBlocks.ALLOY_SMELTER.get()
        );

        appendMachineRecipes(
            markdown,
            minecraft,
            "Grinder",
            ModBlocks.GRINDER.get()
        );

        appendMachineRecipes(
            markdown,
            minecraft,
            "Press",
            ModBlocks.PRESS.get()
        );

        appendMachineRecipes(
            markdown,
            minecraft,
            "Extractor",
            ModBlocks.EXTRACTOR.get()
        );

        appendMachineRecipes(
            markdown,
            minecraft,
            "Gemstone Cutter",
            ModBlocks.GEMSTONE_CUTTER.get()
        );
    }

    private static void appendMachineRecipes(
        StringBuilder markdown,
        Minecraft minecraft,
        String machineName,
        MachineBlock machineBlock
    ) {
        Collection<? extends MachineRecipe> recipes =
            minecraft.level.getRecipeManager()
                .getAllRecipesFor(machineBlock.getRecipeType());

        markdown.append("## ")
            .append(machineName)
            .append("\n\n");

        if (recipes.isEmpty()) {
            markdown.append("*No recipes.*\n\n");
            return;
        }

        List<MachineRecipe> sortedRecipes = new ArrayList<>(recipes);

        sortedRecipes.sort(
            Comparator.comparing(
                recipe -> getRecipeHeader(recipe, minecraft)
            )
        );

        for (MachineRecipe recipe : sortedRecipes) {
            appendRecipe(markdown, recipe, minecraft);
        }
    }

    private static void appendRecipe(
        StringBuilder markdown,
        MachineRecipe recipe,
        Minecraft minecraft
    ) {
        markdown.append("### ")
            .append(getRecipeHeader(recipe, minecraft))
            .append("\n\n");

        markdown.append("**Input:**\n");

        NonNullList<Ingredient> ingredients = recipe.getIngredients();

        NonNullList<Integer> amounts = recipe.getInputAmounts();

        for (int i = 0; i < ingredients.size(); i++) {
            ItemStack[] items =
                ingredients.get(i).getItems();

            if (items.length == 0) {
                continue;
            }

            markdown.append("- ")
                .append(amounts.get(i))
                .append("x ")
                .append(formatItemName(items[0]))
                .append("\n");
        }

        markdown.append("\n");

        ItemStack output =
            recipe.getResultItem(
                minecraft.level.registryAccess()
            );

        markdown.append("**Output:**\n")
            .append("- ")
            .append(output.getCount())
            .append("x ")
            .append(formatItemName(output))
            .append("\n");

        if (recipe instanceof MachineRecipeWithExtraOutputs extraRecipe) {
            NonNullList<ItemStack> extraOutputs =
                extraRecipe.getExtraOutputs();

            if (extraOutputs != null && !extraOutputs.isEmpty()) {
                markdown.append("\n**Extra Outputs:**\n");

                for (int i = 0; i < extraOutputs.size(); i++) {
                    ItemStack extraOutput =
                        extraOutputs.get(i);

                    double chance =
                        extraRecipe
                            .getExtraOutputChances()
                            .get(i);

                    markdown.append("- ")
                        .append(extraOutput.getCount())
                        .append("x ")
                        .append(formatItemName(extraOutput))
                        .append(" — ")
                        .append(formatChance(chance))
                        .append("\n");
                }
            }
        }

        markdown.append("\n---\n\n");
    }

    private static String getRecipeHeader(
        MachineRecipe recipe,
        Minecraft minecraft
    ) {
        boolean singularInput =
            recipe.getIngredients().size() == 1;

        if (singularInput) {
            ItemStack[] items =
                recipe.getIngredients()
                    .get(0)
                    .getItems();

            if (items.length > 0) {
                return formatItemName(items[0]);
            }
        }

        ItemStack output =
            recipe.getResultItem(
                minecraft.level.registryAccess()
            );

        return formatItemName(output);
    }

    private static String formatItemName(ItemStack stack) {
        return stack.getHoverName().getString();
    }

    private static String formatChance(double chance) {
        double percentage = chance * 100;

        if (percentage == (int) percentage) {
            return (int) percentage + "%";
        }

        return percentage + "%";
    }
}