package com.pedestriamc.namecolor;

import net.md_5.bungee.api.ChatColor;

import java.awt.Color;

/**
 * Class that holds two Colors and can apply the gradient between the two colors to a String
 */
public class Gradient {

    private final Color start;
    private final Color end;

    public Gradient(Color start, Color end) {
        this.start = start;
        this.end = end;
    }

    public String apply(String string) {

        char[] chars = string.toCharArray();
        StringBuilder sb = new StringBuilder();

        if(chars.length == 0) {
            return ChatColor.of(start) + string;
        }

        for(int i=0; i<chars.length; i++) {

            float percent = (float) i / (chars.length - 1);

            int red = (int) (start.getRed() + percent * (end.getRed() - start.getRed()));
            int green = (int) (start.getGreen() + percent * (end.getGreen() - start.getGreen()));
            int blue = (int) (start.getBlue() + percent * (end.getBlue() - start.getBlue()));

            System.out.println(red);
            System.out.println(green);
            System.out.println(blue);

            Color color = new Color(red, green, blue);

            sb.append(ChatColor.of(color));
            sb.append(chars[i]);

        }

        return sb.toString();
    }

}
