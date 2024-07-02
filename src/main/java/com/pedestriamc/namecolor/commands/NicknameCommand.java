package com.pedestriamc.namecolor.commands;

import com.pedestriamc.namecolor.Messenger;
import com.pedestriamc.namecolor.NameColor;
import com.pedestriamc.namecolor.SetName;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class NicknameCommand implements CommandExecutor {
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        Player selectedPlayer;
        String nick;
        //Check for enough args
        if(args.length == 0){
            Messenger.sendMessage(sender, Messenger.Message.INSUFFICIENT_ARGS);
            return true;
        }
        //Check for too many/invalid args
        if(args.length > 2){
            Messenger.sendMessage(sender, Messenger.Message.INVALID_ARGS_NICK);
            return true;
        }
        //Help message
        if (args[0].equalsIgnoreCase("HELP")) {
            Messenger.sendMessage(sender, Messenger.Message.NICKNAME_HELP);
            return true;
        }
        if(args.length == 1){
            if(!sender.hasPermission("namecolor.nick") && !sender.hasPermission("namecolor.*") && !sender.hasPermission("namecolor.nick.*")){
                Messenger.sendMessage(sender, Messenger.Message.NO_PERMS);
                return true;
            }
            if(sender instanceof Player){
                selectedPlayer = (Player) sender;
                nick = args[0];
                if(ChatColor.stripColor(nick).length() > NameColor.getInstance().nickLengthLimit()){
                    Messenger.sendMessage(sender, Messenger.Message.NICK_TOO_LONG);
                    return true;
                }
                if(isDuplicateNickname(nick, (Player) sender)){
                    Messenger.sendMessage(sender, Messenger.Message.USERNAME_NICK_PROHIBITED);
                }
                SetName.setNick(nick, selectedPlayer,true);
                Messenger.processPlaceholders(selectedPlayer, Messenger.Message.NAME_SET, selectedPlayer);
            }else{
                //THIS MEANS SERVER SENT COMMAND WITH ONE ARG, CONFIG W/ LANG
                Messenger.sendMessage(sender, Messenger.Message.INSUFFICIENT_ARGS);
            }
            return true;
        }
        //At this point, command has 2 args, meaning that it is setting another player's nickname
        if(!sender.hasPermission("namecolor.nick.others")){
            Messenger.sendMessage(sender, Messenger.Message.NO_PERMS);
            return true;
        }
        selectedPlayer = Bukkit.getPlayer(args[1]);
        if(selectedPlayer != null){
            if(ChatColor.stripColor(args[0]).length() > NameColor.getInstance().nickLengthLimit()){
                Messenger.sendMessage(sender, Messenger.Message.NICK_TOO_LONG);
                return true;
            }
            if(isDuplicateNickname(args[0], selectedPlayer)){
                Messenger.sendMessage(sender, Messenger.Message.USERNAME_NICK_PROHIBITED);
            }
            SetName.setNick(args[0], Bukkit.getPlayer(args[1]), true);
            if(!sender.equals(selectedPlayer)){
                Messenger.processPlaceholders(sender, Messenger.Message.NAME_SET_OTHER, selectedPlayer);
            }
            Messenger.processPlaceholders(selectedPlayer, Messenger.Message.NAME_SET, selectedPlayer);
        }else{
            Messenger.sendMessage(sender, Messenger.Message.INVALID_PLAYER);
        }
        return true;
    }
    //Method to determine if nicknames that are the same as another player's username is allowed, and if so, determine
    //if the nickname is offending
    public boolean isDuplicateNickname(@NotNull String name, @NotNull Player player){
        String nickWithNoColor = ChatColor.stripColor(name);
        if(nickWithNoColor.equals(player.getName())){
            return false;
        }
        if(NameColor.getInstance().allowUsernameNicknames()){
            return false;
        }
        for(OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()){
            if (offlinePlayer.getName().equalsIgnoreCase(name)){
                return true;
            }
        }
        for(Player p : Bukkit.getOnlinePlayers()){
            if (p.getName().equalsIgnoreCase(name)){
                return true;
            }
        }
        return false;
    }
}
