package net.dafarka.metallurgyplus.util;

import net.dafarka.metallurgyplus.MetallurgyPlus;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utility {

    private static final Pattern DIGIT_PATTERN = Pattern.compile("\\d+");

    /**
     * Gets an item from ForgeRegistries using the name.
     * <br>
     * The respecting item is given by a priority system. First ModItems are returned,
     * if no ModItem exists with that name return a vanilla minecraft item with that name
     * if no vanilla item exists with that name return air.
     *
     * @param name the name of the item to return
     *
     * @return an Item with the given name, air otherwise.
     */
    public static Item getItem(String name) {
        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(MetallurgyPlus.MODID, name));
        if (item == ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft", "air"))) {
            item = ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft", name));
        }
        return item;
    }

    /**
     * Formats a given name from "type.id.resource" to "id:resource"
     * <br>
     * name is not checked for proper formatting!
     *
     * @param name The input name which should always be of the form "type.id.resource"
     *
     * @return The formatted name of the form "id:resource"
     */
    public static String formatResourceName(String name) {
        String[] parts = name.split("\\.");
        return parts[parts.length - 2] + ":" + parts[parts.length - 1];
    }

    /**
     * Formats a given Integer to have digit grouping {@code separator}.
     *
     * @param number    the Integer number to format
     * @param separator the separator symbol between the digit groups.
     *
     * @return The formatted string with the corresponding digit grouping
     */
    public static String formatWithSeparator(@NotNull Integer number, char separator) {
        String numStr = number.toString();
        StringBuilder sb = new StringBuilder();

        int len = numStr.length();
        int count = 0;

        // Handle negative numbers
        int start = 0;
        if (numStr.charAt(0) == '-') {
            sb.append('-');
            start = 1;
        }

        // Insert separator every 3 digits from the end
        for (int i = len - 1; i >= start; i--) {
            sb.insert(start, numStr.charAt(i));
            count++;
            if (count % 3 == 0 && i > start) {
                sb.insert(start, separator);
            }
        }

        return sb.toString();
    }

    /**
     * Formats a BigInteger into a compact, human-readable string with SI/engineering suffixes
     * (e.g. 1.25K, 45.60M, 1.00G, 2.50T).
     *
     * @param number the BigInteger to format
     *
     * @return The formatted compact string
     */
    public static String formatCompact(@NotNull BigInteger number) {
        BigInteger abs = number.abs();
        if (abs.compareTo(BigInteger.valueOf(1000)) < 0) {
            return number.toString();
        }

        BigDecimal dec = new BigDecimal(number);
        int unitIndex = 0;

        while (dec.abs().compareTo(BigDecimal.valueOf(1000)) >= 0 && unitIndex < NumberSuffixes.SUFFIXES.length - 1) {
            dec = dec.divide(BigDecimal.valueOf(1000), 2, RoundingMode.HALF_UP);
            unitIndex++;
        }

        String formattedNum = dec.stripTrailingZeros().toPlainString();

        return formattedNum + NumberSuffixes.SUFFIXES[unitIndex];
    }

    /**
     * Gets the multiblock and block name of a specific multiblock block.
     * <p>
     * Example: <br>
     * "battery/casing" -> multiblock="battery", blockName="casing"
     *
     * @param block The block to get the name parts of.
     *
     * @return A string array of the 3 parts. The return array is guaranteed to have 2 parts.
     */
    public static String[] getMultiBlockNames(RegistryObject<? extends Block> block) {
        String path = block.getId().getPath();
        String[] parts = path.split("/");

        String multiblock = parts[0];
        String blockName = parts[parts.length - 1];

        return new String[]{multiblock, blockName};
    }

    /**
     * Gets the tier of a given name.
     * <p>
     * Examples: <br>
     * "name1"  -> 1 <br>
     * "name22" -> 22
     *
     * @param name The name with an integer in it (trailing).
     *
     * @return the tier
     */
    public static int getTier(String name) {
        if (name == null || name.isEmpty()) {
            return 0;
        }

        Matcher matcher = DIGIT_PATTERN.matcher(name);
        if (matcher.find()) {
            try {
                return Integer.parseInt(matcher.group());
            } catch (NumberFormatException ignored) {
                return 0;
            }
        }

        return 0;
    }

    /**
     * Gets the base name of a given string by removing trailing digits.
     * <p>
     * Examples: <br>
     * "name1"          -> "name" <br>
     * "solar_panel_22" -> "solar_panel" <br>
     * "cable/cell_3"   -> "cable/cell"
     *
     * @param name The name with trailing tier.
     *
     * @return The base name without the tier.
     */
    public static String removeTrailingDigits(String name) {
        if (name == null) {
            return "";
        }
        return name.replaceAll("[_/]?\\d+$", "");
    }

    /**
     * Replaces in a given string a string with a different string.
     *
     * @param input       The given string.
     * @param toReplace   The string to replace.
     * @param replaceWith The string to replace with.
     *
     * @return The replaced string.
     */
    public static String replaceInString(String input, String toReplace, String replaceWith) {
        if (input == null || toReplace == null || replaceWith == null || toReplace.isEmpty()) {
            return input;
        }

        return input.replace(toReplace, replaceWith);
    }

    /**
     * Removes in a given string a string.
     *
     * @param input    The given string.
     * @param toRemove The string to remove.
     *
     * @return The replaced string.
     */
    public static String removeInString(String input, String toRemove) {
        return replaceInString(input, toRemove, "");
    }
}
