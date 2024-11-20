package com.iafenvoy.mobsbanner.util;

import com.mojang.datafixers.util.Pair;
import net.minecraft.util.DyeColor;

import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class DyeColorUtil {
    public static DyeColor getDyeColorByColor(int rgb) {
        Color color = new Color(rgb);
        Map<DyeColor, Color> dyeColors = new HashMap<>();

        for (DyeColor dyeColor : DyeColor.values()) {
            float[] c = dyeColor.getColorComponents();
            dyeColors.put(dyeColor, new Color(c[0], c[1], c[2], 1));
        }

        Map<Integer, DyeColor> distances = new HashMap<>();

        for (Map.Entry<DyeColor, Color> entry : dyeColors.entrySet()) {
            DyeColor dyeColor = entry.getKey();
            Color dyeColorRgb = entry.getValue();
            int distance = Math.abs(color.getRed() - dyeColorRgb.getRed())
                    + Math.abs(color.getGreen() - dyeColorRgb.getGreen())
                    + Math.abs(color.getBlue() - dyeColorRgb.getBlue());
            distances.put(distance, dyeColor);
        }

        Integer[] sortedDistances = distances.keySet().toArray(new Integer[0]);
        Arrays.sort(sortedDistances);

        return distances.get(sortedDistances[0]) != null ? distances.get(sortedDistances[0]) : DyeColor.WHITE;
    }

    public static Pair<DyeColor, DyeColor> getColorPair(DyeColor dyeColor) {
        return switch (dyeColor) {
            case WHITE, LIGHT_GRAY -> new Pair<>(DyeColor.LIGHT_GRAY, DyeColor.WHITE);
            case ORANGE, BROWN -> new Pair<>(DyeColor.BROWN, DyeColor.ORANGE);
            case MAGENTA, PURPLE -> new Pair<>(DyeColor.PURPLE, DyeColor.MAGENTA);
            case LIGHT_BLUE, BLUE -> new Pair<>(DyeColor.LIGHT_BLUE, DyeColor.BLUE);
            case LIME, GREEN -> new Pair<>(DyeColor.LIME, DyeColor.GREEN);
            case PINK, RED -> new Pair<>(DyeColor.PINK, DyeColor.RED);
            case GRAY, BLACK -> new Pair<>(DyeColor.GRAY, DyeColor.BLACK);
            case YELLOW -> new Pair<>(DyeColor.ORANGE, DyeColor.YELLOW);
            case CYAN -> new Pair<>(DyeColor.LIGHT_BLUE, DyeColor.CYAN);
        };
    }
}
