package com.pedestriamc.namecolor.tabcompleters;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class WhoIsTabCompleter extends AbstractTabCompleter {

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String @NotNull [] args) {
        if(args.length != 1) {
            return EMPTY;
        }

        List<String> nicknames = new ArrayList<>();
        for(Player player : Bukkit.getOnlinePlayers()) {
            nicknames.add(player.getDisplayName());
        }

        return filter(nicknames, args[0]);
    }
}
