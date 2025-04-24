package com.pedestriamc.namecolor.api.color.painter;

import org.jetbrains.annotations.NotNull;

import java.awt.Color;
import java.util.regex.Pattern;

public class BungeeStripPainter extends BungeePainter {

    public static final Pattern SPIGOT_HEX = Pattern.compile("&#[a-fA-F0-9]{6}", Pattern.CASE_INSENSITIVE);

    @Override
    @NotNull
    public String apply(@NotNull Color color1, @NotNull Color color2, @NotNull String s) {
        s = stripColor(s);
        return super.apply(color1, color2, s);
    }

    public static String stripColor(String str) {
        String stripped = SPIGOT_HEX.matcher(str).replaceAll("");
        stripped = org.bukkit.ChatColor.translateAlternateColorCodes('&', stripped);
        stripped = org.bukkit.ChatColor.stripColor(stripped);
        return stripped;
    }
}
