package com.pedestriamc.namecolor.tabcompleters;

import org.bukkit.Bukkit;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

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

    /**
     * Combines a number of collections into a single List<String>
     * @param collections Collections to be added to the final List.
     * @return A populated List<String>
     */
    @SafeVarargs
    final @NotNull List<String> collect(@NotNull Collection<String>... collections) {
        List<String> list = new ArrayList<>();
        for(Collection<String> collection : collections) {
            list.addAll(collection);
        }
        return list;
    }

    /**
     * Combines a String and a number of collections into a single List<String>
     * @param string The individual String
     * @param collections Collections to be added to the final List.
     * @return A populated List<String>
     */
    @SafeVarargs
    final @NotNull List<String> collect(@NotNull String string, @NotNull Collection<String>... collections) {
        List<String> list = collect(collections);
        list.add(string);
        return list;
    }
}

