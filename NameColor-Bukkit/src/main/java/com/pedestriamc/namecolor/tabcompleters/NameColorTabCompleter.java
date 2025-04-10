package com.pedestriamc.namecolor.tabcompleters;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class NameColorTabCompleter extends AbstractTabCompleter {

    private static final List<String> COLORS = List.of(
            "black", "darkblue", "darkgreen", "darkaqua", "darkred",
            "darkpurple", "gold", "gray", "darkgray", "blue", "green", "aqua",
            "red", "lightpurple", "yellow", "white"
    );

    private static final List<String> STYLES = List.of(
            "bold", "underline", "italic", "magic", "strike"
    );

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        switch (args.length) {
            case 0, 1 -> {
                List<String> list = new ArrayList<>(COLORS);
                list.add("help");
                return filter(list, args[0]);
            }
            default -> {
                if(sender.hasPermission("namecolor.set.style")) {
                    return filter(STYLES, args[args.length - 1]);
                }
                return EMPTY;
            }
        }
    }
}
