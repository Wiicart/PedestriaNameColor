package com.pedestriamc.namecolor.tabcompleters;

import org.bukkit.Bukkit;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
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

    protected List<String> collect(Collection<String> collection1, Collection<String> collection2, Collection<String> collection3) {
        List<String> list = new ArrayList<>();
        if(collection1 != null) {
            list.addAll(collection1);
        }
        if(collection2 != null) {
            list.addAll(collection2);
        }
        if(collection3 != null) {
            list.addAll(collection3);
        }
        return list;
    }

    protected List<String> collect(Collection<String> collection1, Collection<String> collection2) {
        return collect(collection1, collection2, EMPTY);
    }
}

