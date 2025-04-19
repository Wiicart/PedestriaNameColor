package com.pedestriamc.namecolor.api.color.painter;

import org.jetbrains.annotations.NotNull;

import java.awt.Color;

/**
 * Represents ways that the Gradient class can apply a Gradient to a String.
 */
@SuppressWarnings("unused")
public interface Painter {

    /**
     * Uses Colors from net.md_5.bungee.api.ChatColor.
     * Does NOT strip any colors, Strings must manually be stripped.
     */
    Painter BUNGEE = new BungeePainter();

    /**
     * Uses Colors from net.md_5.bungee.api.ChatColor.
     * Strips all colors and styles before applying.
     */
    Painter BUNGEE_STRIP = new BungeeStripPainter();

    /**
     * Uses Colors from net.md_5.bungee.api.ChatColor.
     * Automatically strips colors, except for styles.
     */
    Painter BUNGEE_STRIP_IGNORE_STYLES = new BungeeStripIgnoreStylesPainter();


    @NotNull
    String apply(@NotNull Color color1, @NotNull Color color2, @NotNull String s);

}
