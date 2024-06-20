package com.pedestriamc.namecolor.commands;

import com.pedestriamc.namecolor.NameColor;
import com.pedestriamc.namecolor.SetName;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

public class WhoIsCommand implements CommandExecutor {

    private final FileConfiguration config = NameColor.getInstance().getConfigFile();
    private final String whoIsMessage = config.getString("whois-message");

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, String label, String[] args){
        if(sender.hasPermission("namecolor.whois") || sender.hasPermission("namecolor.*")){
            String returnMessage = whoIsMessage;
            //Check for insufficient args
            if(args.length == 0){
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', NameColor.getInstance().getPrefix()) + ChatColor.translateAlternateColorCodes('&', config.getString("insufficient-args")));
                return true;
            }
            //Check for too many args
            if(args.length > 1){
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', NameColor.getInstance().getPrefix()) + ChatColor.translateAlternateColorCodes('&', config.getString("invalid-args-whois")));
                return true;
            }
            //Check if can't find display name
            if(SetName.getPlayer(args[0]) == null){
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', NameColor.getInstance().getPrefix()) + ChatColor.translateAlternateColorCodes('&', config.getString("invalid-player")));
                return true;
            }else{
                //All good, sending who is msg
                returnMessage = returnMessage.replace("%display-name%", args[0]);
                returnMessage = returnMessage.replace("%username%", SetName.getPlayer(args[0]));
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', NameColor.getInstance().getPrefix()) + ChatColor.translateAlternateColorCodes('&',returnMessage));
                return true;
            }
        }else{
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', NameColor.getInstance().getPrefix()) + ChatColor.translateAlternateColorCodes('&', config.getString("no-perms")));
            return true;
        }
    }
