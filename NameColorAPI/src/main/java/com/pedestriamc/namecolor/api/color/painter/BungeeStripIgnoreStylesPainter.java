package com.pedestriamc.namecolor.api.color.painter;

import com.pedestriamc.namecolor.api.color.Gradient;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

final class BungeeStripIgnoreStylesPainter implements Painter {

    static final Pattern STRIP_ONLY_COLORS = Pattern.compile("(?i)" + ChatColor.COLOR_CHAR + "(x(?:" + ChatColor.COLOR_CHAR + "[0-9A-F]){6}|[0-9A-F])");

    static final List<String> STYLES = List.of(
            String.valueOf(ChatColor.UNDERLINE),
            String.valueOf(ChatColor.STRIKETHROUGH),
            String.valueOf(ChatColor.MAGIC),
            String.valueOf(ChatColor.ITALIC),
            String.valueOf(ChatColor.BOLD),
            String.valueOf(ChatColor.RESET)
    );

    static final String RESET = ChatColor.RESET.toString();

    static final int STYLE_LENGTH = String.valueOf(ChatColor.BOLD).length();

    @Override
    public @NotNull String apply(@NotNull Color color1, @NotNull Color color2, @NotNull String s) {
        List<String> elements = stripExceptStylesToList(s);
        List<Color> colors = Gradient.interpolate(color1, color2, elements.size());

        StringBuilder currentStyle = new StringBuilder();
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < elements.size(); i++) {
            String val = elements.get(i);
            if(val.isEmpty()) {
                continue;
            }

            if(val.length() > 1) {
                if(val.contains(RESET)) {
                    currentStyle = new StringBuilder();
                    val = val.substring(val.indexOf(RESET) + RESET.length());
                }
                if(!val.isEmpty()) {
                    currentStyle.append(extractCodes(val));
                    val = val.substring(val.length() - 1);
                }
            }

            builder.append(net.md_5.bungee.api.ChatColor.of(colors.get(i)));
            builder.append(currentStyle);
            builder.append(val);
        }

        return builder.toString();
    }

    // Breaks down a String into groupings. If styles are present before a char,
    // they will be grouped together with the following char. Otherwise, each char is one group.
    private List<String> stripExceptStylesToList(@NotNull String input) {
        ArrayList<String> list = new ArrayList<>();
        input = STRIP_ONLY_COLORS.matcher(input).replaceAll("");
        char[] chars = input.toCharArray();

        int i = 0;
        while (i < chars.length) {
            if (i + STYLE_LENGTH <= chars.length) {
                String remainder = new String(chars, i, chars.length - i);
                String codes = extractCodes(remainder);
                if(!codes.isEmpty()) {
                    int length = codes.length();
                    if(chars.length - i > length) {
                        list.add(codes + chars[i + length]);
                        i += length + 1;
                    } else {
                        list.add(codes);
                        i += length;
                    }
                    continue;
                }
            }

            list.add(String.valueOf(chars[i]));
            i++;
        }

        return list;
    }

    // Extracts style codes from a String, and returns the extracted codes in String form.
    private String extractCodes(@NotNull String input) {
        char[] chars = input.toCharArray();
        StringBuilder codes = new StringBuilder();

        int i = 0;
        while (i + STYLE_LENGTH <= chars.length) {
            String next = new String(chars, i, STYLE_LENGTH);
            if (STYLES.contains(next)) {
                codes.append(next);
                i += STYLE_LENGTH;
            } else {
                break;
            }
        }

        return codes.toString();
    }
}