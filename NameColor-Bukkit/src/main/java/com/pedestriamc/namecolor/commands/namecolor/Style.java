package com.pedestriamc.namecolor.commands.namecolor;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.permissions.Permission;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public enum Style {
    BOLD(new Permission("namecolor.set.style.bold"), ChatColor.BOLD, "BOLD"),
    ITALIC(new Permission("namecolor.set.style.italic"), ChatColor.ITALIC, "ITALIC"),
    ITALICS(new Permission("namecolor.set.style.italic"), ChatColor.ITALIC, "ITALICS"),
    UNDERLINE(new Permission("namecolor.set.style.underline"), ChatColor.UNDERLINE, "UNDERLINE"),
    MAGIC(new Permission("namecolor.set.style.magic"), ChatColor.MAGIC, "MAGIC"),
    STRIKE(new Permission("namecolor.set.style.strike"), ChatColor.STRIKETHROUGH, "STRIKE");

    private static final Map<String, Style> MAP;
    static {
        HashMap<String, Style> temp = new HashMap<>();
        for (Style style : values()) {
            temp.put(style.getName(), style);
        }
        MAP = Collections.unmodifiableMap(temp);
    }

    private final Permission permission;
    private final ChatColor color;
    private final String name;

    Style(Permission permission, ChatColor color, String name) {
        this.permission = permission;
        this.color = color;
        this.name = name;
    }

    public Permission getPermission() {
        return permission;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return color.toString();
    }

    public static Style getStyle(String name) {
        return MAP.get(name.toUpperCase(Locale.ROOT));
    }
}
