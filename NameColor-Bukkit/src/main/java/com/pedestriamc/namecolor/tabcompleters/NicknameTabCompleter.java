package com.pedestriamc.namecolor.tabcompleters;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.List;

public class NicknameTabCompleter implements TabCompleter {

    private final List<String> list = List.of("reset");

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player){
            if(args.length == 1 && args[0].length() > 0 && args[0].substring(0,1).equalsIgnoreCase("r")){
                return list;
            }
        }
        return null;
    }
}
