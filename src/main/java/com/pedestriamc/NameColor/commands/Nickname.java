package com.pedestriamc.NameColor.commands;

import com.pedestriamc.NameColor.NameColor;
import com.pedestriamc.NameColor.SetNickname;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Nickname implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player selectedPlayer;
        String nick;
        if(args.length == 1){
            if(sender instanceof Player){
                selectedPlayer = (Player) sender;
                nick = args[0];
                SetNickname.setNick(nick,selectedPlayer,true);
                //ADD MESSAGE TO PLAYER THAT THEIR NICK HAS BEEN SET
                return true;
            }else{
                //THIS MEANS SERVER SENT COMMAND WITH ONE ARG, CONFIG W/ LANG
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', NameColor.getInstance().getConfig().getString("insufficient-args")));
                return true;
            }
        }
        if(args.length == 2){
            if(Bukkit.getPlayer(args[1]) != null){
                selectedPlayer = Bukkit.getPlayer(args[1]);
                nick = args[0];
                return true;
            }else{
                //INVALID PLAYER MSG FROM CONFIG
                return true;
            }
        }
        return true;
    }
}
