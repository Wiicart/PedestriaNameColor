package com.pedestriamc.namecolor.api.color.painter;

import com.pedestriamc.namecolor.api.color.Gradient;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.List;

class BungeePainter implements Painter {

    @Override
    @NotNull
    public String apply(@NotNull Color color1, @NotNull Color color2, @NotNull String s) {
        char[] chars = s.toCharArray();
        List<Color> colors = Gradient.order(color1, color2, chars.length);

        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < chars.length; i++) {
            sb.append(colors.get(i));
            sb.append(chars[i]);
        }

        return sb.toString();
    }
}
