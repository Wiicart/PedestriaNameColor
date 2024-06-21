package com.pedestriamc.namecolor;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;

public final class Messenger {
    //Class to send messages based on config, w/prefix
    public enum Message{
        NAMECOLOR_HELP, NICKNAME_HELP, WHOIS_HELP, INSUFFICIENT_ARGS, INVALID_PLAYER, NO_PERMS, NAME_SET, NAME_SET_OTHER, WHOIS_MESSAGE, INVALID_ARGS_COLOR, INVALID_ARGS_NICK, INVALID_ARGS_WHOIS, INVALID_CMD_COLOR, INVALID_CMD_NICK, INVALID_COLOR
    }
    private static HashMap<Message, String> messageStringHashMap = new HashMap<>();
    private static String prefix;
    //Initialize HashMap
    public static void initialize(){
        FileConfiguration config = NameColor.getInstance().getConfig();
        for(Message msg : Message.values()){ //https://www.baeldung.com/java-enum-iteration
            String configValue = msg.toString().replace("_", "-").toLowerCase();
            try{
                messageStringHashMap.put(msg, config.getString(configValue));
            }catch(NullPointerException a){
                Bukkit.getLogger().info("[NameColor] Unable to find value " + configValue + " in config.yml, resorting to default message.");
            }
        }

    }
    public static void sendMessage(CommandSender sender, Message message, String[] args){
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + messageStringHashMap.get(message)));
    }
    public static void processPlaceholders(Message message, Player player, String displayName){

    }
}
