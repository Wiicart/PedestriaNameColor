package com.pedestriamc.NameColor;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

//https://bukkit.org/threads/easy-no-api-setting-up-custom-tab-completion.299956/
public class NameColorCommandTabCompleter implements TabCompleter {
    static final List<String> colorList = Arrays.asList("black", "darkblue", "darkgreen", "darkaqua", "darkred", "darkpurple", "gold", "gray", "darkgray", "blue", "green", "aqua", "red", "lightpurple", "yellow", "white");
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args){
        if(sender instanceof Player){
            if(args.length == 1 && args[0].length() > 0 && args[0].charAt(0) == 'h'){
                return Arrays.asList("help");
            }
            if(args.length == 1 && !args[0].equals("#")){
                return colorList;
            }
            if(args.length == 2){

            }

        }
        return null;
    }

}
