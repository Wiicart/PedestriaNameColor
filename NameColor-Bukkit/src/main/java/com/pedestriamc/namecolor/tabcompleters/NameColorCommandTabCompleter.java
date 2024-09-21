package com.pedestriamc.namecolor.tabcompleters;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class NameColorCommandTabCompleter implements TabCompleter {

    private final List<String> colorList = Arrays.asList("black", "darkblue", "darkgreen", "darkaqua", "darkred", "darkpurple", "gold", "gray", "darkgray", "blue", "green", "aqua", "red", "lightpurple", "yellow", "white");
    private final List<String> styleList = Arrays.asList("bold", "underline", "italic", "magic", "strike");
    private final List<String> helpList = List.of("help");

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args){
        if(sender instanceof Player){
            if(args.length == 1 && args[0].length() > 0 && args[0].charAt(0) == 'h'){
                return helpList;
            }
            if(args.length == 1 && !args[0].equals("#")){
                return colorList;
            }
            if(args.length > 2 && sender.hasPermission("namecolor.set.style")){
                return styleList;
            }
        }
        return null;
    }

}
