package com.pedestriamc.namecolor.commands;

import com.pedestriamc.namecolor.Message;
import com.pedestriamc.namecolor.Messenger;
import com.pedestriamc.namecolor.NameUtilities;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class WhoIsCommand implements CommandExecutor {

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args){
        if(sender.hasPermission("namecolor.whois") || sender.hasPermission("namecolor.*")){
            //Check for insufficient args
            if(args.length == 0){
                Messenger.sendMessage(sender, Message.INSUFFICIENT_ARGS);
                return true;
            }
            //Check for too many args
            if(args.length > 1){
                Messenger.sendMessage(sender, Message.INVALID_ARGS_WHOIS);
                return true;
            }
            //Check if can't find display name
            if(NameUtilities.getPlayer(args[0]) == null){
                Messenger.sendMessage(sender, Message.INVALID_PLAYER);
                return true;
            }else{
                //All good, sending who is msg
                Messenger.processPlaceholders(sender, Message.WHOIS_MESSAGE, Bukkit.getPlayer(NameUtilities.getPlayer(args[0])));
                return true;
            }
        }else{
            Messenger.sendMessage(sender, Message.NO_PERMS);
            return true;
        }
    }
}
