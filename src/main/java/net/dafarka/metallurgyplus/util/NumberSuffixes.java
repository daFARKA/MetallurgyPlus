package net.dafarka.metallurgyplus.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class NumberSuffixes {

    private static final String[] BASE_SI = {
        "", "K", "M", "G", "T", "P", "E", "Z", "Y", "R", "Q"
    };

    /**
     * Suffix list containing:
     * - Base SI units (index 0 to 10: "", K, M, G, T, P, E, Z, Y, R, Q)
     * - Double-letter series (index 11 to 686: "aa" through "zz", representing 26 * 26 = 676 tiers)
     * Total length: 687 elements (up to 10^(686 * 3) = 10^2058).
     */
    public static final String[] SUFFIXES = buildSuffixes();

    private static String[] buildSuffixes() {
        List<String> list = new ArrayList<>(BASE_SI.length + 26 * 26);

        list.addAll(Arrays.asList(BASE_SI));

        for (char first = 'a'; first <= 'z'; first++) {
            for (char second = 'a'; second <= 'z'; second++) {
                list.add("" + first + second);
            }
        }

        return list.toArray(new String[0]);
    }
}
