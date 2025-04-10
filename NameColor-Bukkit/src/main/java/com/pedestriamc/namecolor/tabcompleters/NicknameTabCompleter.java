package com.pedestriamc.namecolor.tabcompleters;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class NicknameTabCompleter extends AbstractTabCompleter {

    private static final List<String> RESET = List.of("reset");

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if(args.length == 1) {
            return filter(RESET, args[0]);
        }
        return EMPTY;
    }
}
