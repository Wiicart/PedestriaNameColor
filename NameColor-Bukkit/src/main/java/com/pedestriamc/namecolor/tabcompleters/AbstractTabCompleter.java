package com.pedestriamc.namecolor.tabcompleters;

import org.bukkit.Bukkit;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public abstract class AbstractTabCompleter implements TabCompleter {

    protected static final List<String> EMPTY = List.of();

    protected List<String> getPlayerNames() {
        ArrayList<String> list = new ArrayList<>();
        for(Player p : Bukkit.getOnlinePlayers()) {
            list.add(p.getName());
        }
        return list;
    }

    protected List<String> filter(List<String> list, String input) {
        return list.stream()
                .filter(arg -> arg.toLowerCase().startsWith(input.toLowerCase()))
                .toList();
    }
}

