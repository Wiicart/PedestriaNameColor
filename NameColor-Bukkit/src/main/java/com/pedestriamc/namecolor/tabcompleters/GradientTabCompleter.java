package com.pedestriamc.namecolor.tabcompleters;

import com.pedestriamc.namecolor.commands.GradientCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class GradientTabCompleter extends AbstractTabCompleter {

    private static final List<String> COLORS_AND_POUND;

    static {
        List<String> colors = new ArrayList<>();
        for (String color : GradientCommand.COLORS.keySet()) {
            colors.add(color.toLowerCase(Locale.ROOT));
        }
        colors.add("#");
        COLORS_AND_POUND = Collections.unmodifiableList(colors);
    }


    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return switch(args.length) {
            case 0 -> COLORS_AND_POUND;
            case 1 -> filter(COLORS_AND_POUND, args[0]);
            case 2 -> filter(COLORS_AND_POUND, args[1]);
            case 3 -> filter(getPlayerNames(), args[2]);
            default -> EMPTY;
        };
    }
}
