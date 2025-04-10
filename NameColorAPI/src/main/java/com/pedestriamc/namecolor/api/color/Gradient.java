package com.pedestriamc.namecolor.api.color;

import net.md_5.bungee.api.ChatColor;

import java.awt.Color;

public final class Gradient {

    /**
     * Applies a gradient of two colors to a String
     * @param color1 The starting color
     * @param color2 The ending color
     * @param s The original String
     * @return A colored String
     */
    public static String apply(Color color1, Color color2, String s) {

        char[] chars = s.toCharArray();
        StringBuilder sb = new StringBuilder();

        if(chars.length == 0) {
            return ChatColor.of(color1) + s;
        }

        for(int i=0; i<chars.length; i++) {

            float percent = (float) i / (chars.length - 1);

            int red = (int) (color1.getRed() + percent * (color2.getRed() - color1.getRed()));
            int green = (int) (color1.getGreen() + percent * (color2.getGreen() - color1.getGreen()));
            int blue = (int) (color1.getBlue() + percent * (color2.getBlue() - color1.getBlue()));

            Color color = new Color(red, green, blue);

            sb.append(ChatColor.of(color));
            sb.append(chars[i]);

        }

        return sb.toString();

    }

    /**
     * Applies a gradient of 3 colors to a String
     * @param color1 The first color
     * @param color2 The second color
     * @param color3 The third color
     * @param s The original String
     * @return A colored String
     */
    public static String apply(Color color1, Color color2, Color color3, String s) {

        int mid = s.length() / 2;

        String first = apply(color1, color2, s.substring(0, mid));
        String second = apply(color2, color3, s.substring(mid));

        return first + second;

    }

}
