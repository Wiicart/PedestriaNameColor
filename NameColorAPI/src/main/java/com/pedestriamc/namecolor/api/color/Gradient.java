package com.pedestriamc.namecolor.api.color;

import com.pedestriamc.namecolor.api.color.painter.Painter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a gradient of 2-3 colors.
 */
@SuppressWarnings("unused")
public final class Gradient {

    private Color start;

    private Color middle;

    private Color end;

    private Painter painter;

    /**
     * Constructs a gradient of two colors.
     * @param start The color on the left.
     * @param end The color on the right.
     */
    public Gradient(@NotNull Color start, @NotNull Color end) {
        this(start, end, Painter.BUNGEE);
    }

    public Gradient(@NotNull Color start, @NotNull Color end, @NotNull Painter painter) {
        Objects.requireNonNull(start);
        Objects.requireNonNull(end);
        this.start = start;
        this.end = end;
        this.painter = painter;
    }

    /**
     * Constructs a gradient of three colors.
     * @param start The color on the left.
     * @param middle The color in the middle.
     * @param end The color on the right.
     * @param painter The Painter implementation
     */
    public Gradient(@NotNull Color start, @Nullable Color middle, @NotNull Color end, Painter painter) {
        Objects.requireNonNull(start);
        Objects.requireNonNull(end);
        this.start = start;
        this.middle = middle;
        this.end = end;
        this.painter = painter;
    }

    /**
     * Constructs a gradient of three colors.
     * @param start The color on the left.
     * @param middle The color in the middle.
     * @param end The color on the right.
     */
    public Gradient(@NotNull Color start, @Nullable Color middle, @NotNull Color end) {
        this(start, middle, end, Painter.BUNGEE);
    }

    /**
     * Provides the middle Color of the gradient.
     * @return The middle Color (Nullable)
     */
    @Nullable
    public Color getMiddle() {
        return middle;
    }

    /**
     * Sets the middle Color of the gradient. Null is permitted.
     * @param middle The middle Color
     */
    public void setMiddle(@Nullable Color middle) {
        this.middle = middle;
    }

    /**
     * Provides the end Color of the gradient.
     * @return The end Color
     */
    @NotNull
    public Color getEnd() {
        return end;
    }

    /**
     * Sets the end Color of the gradient. Null is not permitted.
     * @param end The new end Color.
     */
    private void setEnd(@NotNull Color end) {
        Objects.requireNonNull(end);
        this.end = end;
    }

    /**
     * Provides the starting Color of the gradient.
     * @return The starting Color
     */
    @NotNull
    public Color getStart() {
        return start;
    }

    /**
     * Sets the starting Color of the gradient. Null is not permitted.
     * @param start The new starting Color.
     */
    public void setStart(@NotNull Color start) {
        Objects.requireNonNull(start);
        this.start = start;
    }

    /**
     * Provides what Painter implementation is being used
     * @return The Painter implementation
     */
    public Painter getPainter() {
        return painter;
    }

    /**
     * Sets the Painter implementation.
     * @param painter The new Painter
     */
    public void setPainter(@NotNull Painter painter) {
        this.painter = painter;
    }

    /**
     * Applies the Gradient to a String.
     * @param string The String to have the gradient be applied to.
     * @return A colored String.
     */
    @NotNull
    public String apply(@NotNull String string) {
        if(middle == null) {
            return apply(start, end, string, painter);
        }
        return apply(start, middle, end, string, painter);
    }

    public static List<Color> interpolate(@NotNull Color color1, @NotNull Color color2, int length) {
        List<Color> result = new ArrayList<>();
        for(int i=0; i<length; i++) {
            float percent = (float) i / (length - 1);
            int red = (int) (color1.getRed() + percent * (color2.getRed() - color1.getRed()));
            int green = (int) (color1.getGreen() + percent * (color2.getGreen() - color1.getGreen()));
            int blue = (int) (color1.getBlue() + percent * (color2.getBlue() - color1.getBlue()));
            Color color = new Color(red, green, blue);
            result.add(color);
        }

        return result;
    }

    /**
     * Applies a gradient of two colors to a String
     * @param color1 The starting color
     * @param color2 The ending color
     * @param s The original String
     * @param painter The Painter implementation
     * @return A colored String
     */
    public static String apply(@NotNull Color color1, @NotNull Color color2, @NotNull String s, @NotNull Painter painter) {
        return painter.apply(color1, color2, s);
    }

    /**
     * Applies a gradient of two colors to a String
     * @param color1 The starting color
     * @param color2 The ending color
     * @param s The original String
     * @return A colored String
     */
    @NotNull
    public static String apply(@NotNull Color color1, @NotNull Color color2, @NotNull String s) {
        return apply(color1, color2, s, Painter.BUNGEE);
    }

    /**
     * Applies a gradient of three colors to a String
     * @param color1 The first color
     * @param color2 The second color
     * @param color3 The third color
     * @param s The original String
     * @return A colored String
     */
    @NotNull
    public static String apply(@NotNull Color color1, @NotNull Color color2, @NotNull Color color3, @NotNull String s) {
        return apply(color1, color2, color3, s, Painter.BUNGEE);
    }

    /**
     * Applies a gradient of three colors to a String
     * @param color1 The first color
     * @param color2 The second color
     * @param color3 The third color
     * @param s The original String
     * @return A colored String
     */
    @NotNull
    public static String apply(@NotNull Color color1, @NotNull Color color2, @NotNull Color color3, @NotNull String s, Painter painter) {
        int mid = s.length() / 2;
        String first = apply(color1, color2, s.substring(0, mid), painter);
        String second = apply(color2, color3, s.substring(mid), painter);

        return first + second;
    }

}
