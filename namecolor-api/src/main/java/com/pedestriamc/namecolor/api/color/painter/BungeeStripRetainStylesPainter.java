package com.pedestriamc.namecolor.api.color.painter;

import com.pedestriamc.namecolor.api.color.Gradient;
import com.pedestriamc.namecolor.api.color.RGBColor;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

final class BungeeStripRetainStylesPainter implements Painter {

    /**
     * The String value of <code>ChatColor.RESET</code>
     */
    static final String RESET_STRING = ChatColor.RESET.toString();

    /**
     * {@link ChatColor#COLOR_CHAR} converted to a String.
     */
    static final String CHAT_COLOR_CHAR_STRING = String.valueOf(ChatColor.COLOR_CHAR);

    /**
     * Represents any traditional {@link ChatColor} color code, with the exceptions of styles and reset.
     */
    static final Pattern COLORS_NOT_STYLES =
            Pattern.compile("(?i)" + ChatColor.COLOR_CHAR + "(x(?:" + ChatColor.COLOR_CHAR + "[0-9A-F]){6}|[0-9A-F])");

    /**
     * Represents hex &#RRGGBB
     */
    static final Pattern HEX_COLOR = Pattern.compile("^&#[0-9A-Fa-f]{6}$");

    /**
     * Represents a hex chat color String &§RRGGBB
     */
    static final Pattern HEX_COLOR_INLINE = Pattern.compile("^&" + ChatColor.COLOR_CHAR +"[0-9A-Fa-f]{6}$");

    /**
     * Represents the long Spigot HEX String in the pattern §x§R§R§G§G§B§B
     */
    static final Pattern LONG_SPIGOT_HEX = Pattern.compile(
            "(?i)" + ChatColor.COLOR_CHAR + "x(" +
                    ChatColor.COLOR_CHAR + "[0-9A-F]){6}"
    );

    /**
     * Represents a BungeeCord-style hex color §#RRGGBB
     */
    static final Pattern BUNGEE_HEX_COLOR = Pattern.compile(
            "(?i)" + ChatColor.COLOR_CHAR + "#[0-9A-F]{6}"
    );

    @Override
    public @NotNull String apply(@NotNull Color color1, @NotNull Color color2, @NotNull String s) {
        String stripped = stripColor(s);
        stripped = stripTrailingResets(stripped);
        stripped = stripRedundantStyles(stripped);
        Tokens tokens = tokenize(stripped);

        List<Color> colors = Gradient.interpolate(color1, color2, tokens.sizeExcludingDecorations());
        int colorPos = 0;

        StringBuilder builder = new StringBuilder();
        String styles = "";
        for (Token current : tokens) {
            if (current.isDecoration) {
                if (current.isReset()) {
                    styles = "";
                    builder.append(RESET_STRING);
                } else if (!styles.contains(current.val)) {
                    styles += current.val;
                }
            } else {
                builder.append(RGBColor.of(colors.get(colorPos)));
                builder.append(styles);
                builder.append(current);
                colorPos++;
            }
        }
        builder.append(RESET_STRING);

        return builder.toString();
    }

    /**
     * Strips any type of color coding from the String, retaining style codes and reset codes.
     * @param input The String to strip
     * @return The stripped String
     */
    private String stripColor(String input) {
        input = BUNGEE_HEX_COLOR.matcher(input).replaceAll("");
        input = COLORS_NOT_STYLES.matcher(input).replaceAll("");
        input = HEX_COLOR.matcher(input).replaceAll("");
        input = HEX_COLOR_INLINE.matcher(input).replaceAll("");
        input = LONG_SPIGOT_HEX.matcher(input).replaceAll("");

        return input;
    }

    private String stripTrailingResets(String input) {
        while (input.endsWith(RESET_STRING)) {
            input = input.substring(0, input.length() - 2);
        }

        return input;
    }

    /**
     * Parses the String and strips redundant style Strings.
     * No colors are expected.
     * @param input The original String.
     * @return The stripped output.
     */
    private String stripRedundantStyles(String input) {
        StringBuilder result = new StringBuilder();
        List<String> activeStyles = new ArrayList<>();
        char[] chars = input.toCharArray();

        int pos = 0;
        while (pos < chars.length) {
            if (chars[pos] == ChatColor.COLOR_CHAR && pos < chars.length - 1) {
                String code = CHAT_COLOR_CHAR_STRING + chars[pos + 1];
                if (code.equals(RESET_STRING) && !activeStyles.isEmpty()) {
                    activeStyles.clear();
                    result.append(RESET_STRING);
                } else if (!activeStyles.contains(code)) {
                    activeStyles.add(code);
                    result.append(code);
                }
                pos += 2;
            } else {
                result.append(chars[pos]);
                pos++;
            }
        }

        return result.toString();
    }

    /**
     * Tokenizes a String into style or reset color codes and individual chars.
     * @param input The String to tokenize
     * @return A list of Tokens.
     */
    private Tokens tokenize(String input) {
        Tokens tokens = new Tokens();
        char[] chars = input.toCharArray();

        int i = 0;
        while (i < chars.length) {
            if (chars[i] == ChatColor.COLOR_CHAR && i + 1 < chars.length) {
                String val = CHAT_COLOR_CHAR_STRING + chars[i + 1];
                tokens.add(new Token(val, true));
                i+= 2;
            } else {
                tokens.add(new Token(String.valueOf(chars[i]), false));
                i++;
            }
        }

        return tokens;
    }

    /**
     * Represents either an individual char from a name, or a style/reset code.
     * @param val The String content. Should be 1-2 characters.
     * @param isDecoration Tells if this is a style or reset.
     */
    private record Token(String val, boolean isDecoration) {

        @Override
        public String toString() {
            return val;
        }

        boolean isReset() {
            return this.val.equals(RESET_STRING);
        }

    }

    /**
     * {@link ArrayList} for Tokens with additional method to check how many tokens are non-decorations.
     */
    private static final class Tokens extends ArrayList<Token> {

        /**
         * Determines how many tokens in this are plain chars.
         * Includes all elements except those where {@link Token#isDecoration} is true.
         * @return A count.
         */
        int sizeExcludingDecorations() {
            int count = 0;
            for (Token token : this) {
                if (!token.isDecoration()) {
                    count++;
                }
            }
            return count;
        }
    }

}
