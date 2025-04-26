package com.pedestriamc.namecolor.commands.namecolor;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.permissions.Permission;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public enum Color {
    BLACK(new Permission("namecolor.set.color.black"), ChatColor.BLACK, "BLACK"),
    DARKBLUE(new Permission("namecolor.set.color.darkblue"), ChatColor.DARK_BLUE, "DARKBLUE"),
    DARKGREEN(new Permission("namecolor.set.color.darkgreen"), ChatColor.DARK_GREEN, "DARKGREEN"),
    DARKAQUA(new Permission("namecolor.set.color.darkaqua"), ChatColor.DARK_AQUA, "DARKAQUA"),
    DARKRED(new Permission("namecolor.set.color.darkred"), ChatColor.DARK_RED, "DARKRED"),
    DARKPURPLE(new Permission("namecolor.set.color.darkpurple"), ChatColor.DARK_PURPLE, "DARKPURPLE"),
    GOLD(new Permission("namecolor.set.color.gold"), ChatColor.GOLD, "GOLD"),
    GRAY(new Permission("namecolor.set.color.gray"), ChatColor.GRAY, "GRAY"),
    DARKGRAY(new Permission("namecolor.set.color.darkgray"), ChatColor.DARK_GRAY, "DARKGRAY"),
    BLUE(new Permission("namecolor.set.color.blue"), ChatColor.BLUE, "BLUE"),
    GREEN(new Permission("namecolor.set.color.green"), ChatColor.GREEN, "GREEN"),
    AQUA(new Permission("namecolor.set.color.aqua"), ChatColor.AQUA, "AQUA"),
    RED(new Permission("namecolor.set.color.red"), ChatColor.RED, "RED"),
    PINK(new Permission("namecolor.set.color.pink"), ChatColor.LIGHT_PURPLE, "PINK"),
    YELLOW(new Permission("namecolor.set.color.yellow"), ChatColor.YELLOW, "YELLOW"),
    WHITE(new Permission("namecolor.set.color.white"), ChatColor.WHITE, "WHITE"),
    HEX(new Permission("namecolor.set.color.hex"), null, "HEX");

    private static final Map<String, Color> MAP;
    static {
        HashMap<String, Color> temp = new HashMap<>();
        for (Color color : values()) {
            temp.put(color.getName(), color);
        }
        MAP = Collections.unmodifiableMap(temp);
    }

    private final Permission permission;
    private final ChatColor chatColor;
    private final String name;

    Color(Permission permission, ChatColor color, String name) {
        this.permission = permission;
        this.chatColor = color;
        this.name = name;
    }

    public Permission getPermission() {
        return permission;
    }

    public String getName() {
        return name;
    }

    public static Color getColor(String name) {
        return MAP.get(name.toUpperCase(Locale.ROOT));
    }

    @Override
    public String toString() {
        if(chatColor != null) {
            return chatColor.toString();
        }
        return "";
    }
}
