package com.pedestriamc.namecolor.api.color.painter;

import com.pedestriamc.namecolor.api.color.Gradient;
import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.awt.Color;
import java.util.List;

class BungeePainter implements Painter {

    @Override
    @NotNull
    public String apply(@NotNull Color color1, @NotNull Color color2, @NotNull String s) {
        char[] chars = s.toCharArray();
        List<Color> colors = Gradient.interpolate(color1, color2, chars.length);

        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < chars.length; i++) {
            sb.append(ChatColor.of(colors.get(i)));
            sb.append(chars[i]);
        }

        return sb.toString();
    }
}
