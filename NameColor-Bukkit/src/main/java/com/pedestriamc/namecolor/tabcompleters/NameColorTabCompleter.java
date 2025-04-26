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
            "red", "pink", "yellow", "white"
    );

    private static final List<String> STYLES = List.of(
            "bold", "underline", "italic", "magic", "strike"
    );

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if(!sender.isOp() && !sender.hasPermission("namecolor.*") && !sender.hasPermission("namecolor.set")) {
            return EMPTY;
        }

        switch (args.length) {
            case 0, 1 -> {
                List<String> list = new ArrayList<>(COLORS);
                list.add("help");
                return filter(list, args[0]);
            }
            case 2, 3, 4, 5, 6, 7 -> {
                if(sender.hasPermission("namecolor.set.style")) {
                    List<String> list = new ArrayList<>(STYLES);
                    list.addAll(getPlayerNames());
                    return filter(list, args[args.length - 1]);
                }
            }
            default -> { return EMPTY; }
        }
        return EMPTY;
    }
}
