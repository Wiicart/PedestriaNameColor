package com.pedestriamc.namecolor.commands;

import com.pedestriamc.namecolor.NameColor;
import com.pedestriamc.namecolor.SetName;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class NicknameCommand implements CommandExecutor {
    FileConfiguration config = NameColor.getInstance().getConfigFile();
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, String label, String[] args) {
        Player selectedPlayer;
        String nick;
        if(args.length == 0){
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', NameColor.getInstance().getPrefix()) + ChatColor.translateAlternateColorCodes('&', config.getString("insufficient-args")));
            return true;
        }
        if(args.length > 2){
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', NameColor.getInstance().getPrefix()) + ChatColor.translateAlternateColorCodes('&', config.getString("invalid-args-nick")));
            return true;
        }
        if (args[0].equalsIgnoreCase("HELP")) {
            for(String msgs : NameColor.getInstance().getConfigFile().getStringList("nickname-help")){
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',msgs));
            }
            return true;
        }
        if(args.length == 1){
            if(!sender.hasPermission("namecolor.nick") && !sender.hasPermission("namecolor.*") && !sender.hasPermission("namecolor.nick.*")){
                //SENDS MESSAGE NO PERMS FROM LANG
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', NameColor.getInstance().getPrefix()) + ChatColor.translateAlternateColorCodes('&', config.getString("no-perms")));
                return true;
            }
            if(sender instanceof Player){
                selectedPlayer = (Player) sender;
                nick = args[0];
                SetName.setNick(nick,selectedPlayer,true);
                if(!sender.equals(selectedPlayer)){
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', NameColor.getInstance().getPrefix()) + NameColor.getInstance().processSenderPlaceholders(selectedPlayer));
                }
                selectedPlayer.sendMessage(ChatColor.translateAlternateColorCodes('&', NameColor.getInstance().getPrefix()) + NameColor.getInstance().processPlaceholders(selectedPlayer));
                //ADD MESSAGE TO PLAYER THAT THEIR NICK HAS BEEN SET
                return true;
            }else{
                //THIS MEANS SERVER SENT COMMAND WITH ONE ARG, CONFIG W/ LANG
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', NameColor.getInstance().getPrefix()) + ChatColor.translateAlternateColorCodes('&', config.getString("insufficient-args")));
                return true;
            }
        }
        if(args.length == 2){
            if(!sender.hasPermission("namecolor.nick.others")){
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', NameColor.getInstance().getPrefix()) + ChatColor.translateAlternateColorCodes('&', config.getString("no-perms")));
                return true;
            }
            selectedPlayer = Bukkit.getPlayer(args[1]);
            if(selectedPlayer != null){
                args[0] += "&r";
                SetName.setNick(args[0], Bukkit.getPlayer(args[1]), true);
                if(!sender.equals(selectedPlayer)){
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', NameColor.getInstance().getPrefix()) + NameColor.getInstance().processSenderPlaceholders(selectedPlayer));
                }
                selectedPlayer.sendMessage(ChatColor.translateAlternateColorCodes('&', NameColor.getInstance().getPrefix()) + NameColor.getInstance().processPlaceholders(selectedPlayer));
                return true;
            }else{
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', NameColor.getInstance().getPrefix()) + ChatColor.translateAlternateColorCodes('&', config.getString("invalid-player")));
                return true;
            }
        }
        return true;
    }
}
