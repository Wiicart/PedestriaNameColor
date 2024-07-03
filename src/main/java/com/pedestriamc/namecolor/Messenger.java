package com.pedestriamc.namecolor;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;

public final class Messenger {
    //Class to send messages based on config, w/prefix
    public enum Message{
        NAMECOLOR_HELP, NICKNAME_HELP, WHOIS_HELP, INSUFFICIENT_ARGS, INVALID_PLAYER, NO_PERMS, NAME_SET, NAME_SET_OTHER, WHOIS_MESSAGE, INVALID_ARGS_COLOR, INVALID_ARGS_NICK, INVALID_ARGS_WHOIS, INVALID_CMD_COLOR, INVALID_CMD_NICK, INVALID_COLOR, NICK_TOO_LONG, USERNAME_NICK_PROHIBITED, COLORS
    }
    private static final HashMap<Message, Object> messageStringHashMap = new HashMap<>();
    private static String prefix;
    private static final HashMap<Message, Object> defaults;
    static{
        defaults = new HashMap<>();
        defaults.put(Message.NAMECOLOR_HELP, new String[]{
                "&8+----[&dNameColor&8]----+",
                "&fUsage: &7/namecolor <color> <option1> <option2>&f.",
                "&fAny combinations of the following options can be used:",
                "&fbold, underline, italics, magic, strike.",
                "&fIf changing another player's name, add their username to the end.",
                "&fRGB colors supported, formatted #<RGB>."
        });
        defaults.put(Message.NICKNAME_HELP, new String[]{
                "&8+----[&dNameColor&8]----+",
                "&fUsage: &7/nick <nick> <username>&f.",
                "&f<username> may be blank, RGB colors supported.",
                "&fNote: Enter RGB codes as &#<RGB>."
        });
        defaults.put(Message.COLORS, new String[]{
                "&8+--------------[&dNameColor&8]--------------+",
                "" + ChatColor.WHITE + "&0" + ChatColor.BLACK + " Black" + ChatColor.WHITE + "&1" + ChatColor.DARK_BLUE + " Dark Blue",
                "" + ChatColor.WHITE + "&2" + ChatColor.DARK_GREEN + " Dark Green" + ChatColor.WHITE + "&3" + ChatColor.DARK_AQUA + " Dark Aqua",
                "" + ChatColor.WHITE + "&4" + ChatColor.DARK_RED + " Dark Red" + ChatColor.WHITE + "&5" + ChatColor.DARK_PURPLE + " Dark Purple",
                "" + ChatColor.WHITE + "&6" + ChatColor.GOLD + " Gold" + ChatColor.WHITE + "&7" + ChatColor.GRAY + " Gray",
                "" + ChatColor.WHITE + "&8" + ChatColor.DARK_GRAY + " Dark Gray" + ChatColor.WHITE + "&9" + ChatColor.BLUE + " Blue",
                "" + ChatColor.WHITE + "&a" + ChatColor.GREEN + " Green" + ChatColor.WHITE + "&b" + ChatColor.AQUA + " Aqua",
                "" + ChatColor.WHITE +  "&c" + ChatColor.RED + " Red" + ChatColor.WHITE + "&d" + ChatColor.LIGHT_PURPLE + " Light Purple",
                "" +  ChatColor.WHITE + "&e" + ChatColor.YELLOW + " Yellow" + ChatColor.WHITE + "&f" + ChatColor.WHITE + " White",
                "" + ChatColor.WHITE + "Styles:",
                "" + ChatColor.WHITE + "&l" + ChatColor.BOLD + " Bold" + ChatColor.RESET + ChatColor.WHITE + "&k" + ChatColor.MAGIC + " Magic",
                "" + ChatColor.WHITE + "&n" + ChatColor.UNDERLINE + " Underline" + ChatColor.RESET + ChatColor.WHITE + "&m" + ChatColor.STRIKETHROUGH + " Strikethrough",
                "" + ChatColor.WHITE + "&o" + ChatColor.ITALIC + " Italic" + ChatColor.RESET + ChatColor.WHITE + "&r" + ChatColor.WHITE + " Reset"
        });
        defaults.put(Message.WHOIS_HELP, "&fUsage: &7/whois <display name>");
        defaults.put(Message.INSUFFICIENT_ARGS, "&fInsufficient arguments.");
        defaults.put(Message.INVALID_PLAYER, "&fCannot find that player!");
        defaults.put(Message.NO_PERMS, "&fYou don't have permission!");
        defaults.put(Message.NAME_SET, "&fYour display name has been set to &f%display-name%&f.");
        defaults.put(Message.NAME_SET_OTHER, "&f%username%'s display name has been set to %display-name%&f.");
        defaults.put(Message.WHOIS_MESSAGE, "&7%display-name%''s &fusername is &7%username%&f.");
        defaults.put(Message.INVALID_ARGS_COLOR, "&fInvalid arguments. Type &7/namecolor help &ffor usage.");
        defaults.put(Message.INVALID_ARGS_NICK, "&fInvalid arguments. Type &7/nick help &ffor usage.");
        defaults.put(Message.INVALID_ARGS_WHOIS, "&fInvalid arguments. Type &7/whois help &ffor usage.");
        defaults.put(Message.INVALID_CMD_COLOR, "&fInvalid command usage! Type &7/namecolor help &ffor usage.");
        defaults.put(Message.INVALID_CMD_NICK, "&fInvalid command usage! Type &7/nick help &ffor usage.");
        defaults.put(Message.INVALID_COLOR, "&fInvalid color!");
        defaults.put(Message.NICK_TOO_LONG, "&fThat nickname is too long!");
        defaults.put(Message.USERNAME_NICK_PROHIBITED, "&fYour nickname cannot be the username of another player.");
    }
    //Initialize HashMap
    public static void initialize(){
        FileConfiguration config = NameColor.getInstance().getConfig();
        for(Message msg : Message.values()){ //https://www.baeldung.com/java-enum-iteration
            String configValue = msg.toString().replace("_", "-").toLowerCase();
            try{
                if (config.isList(configValue)) {
                    messageStringHashMap.put(msg, config.getStringList(configValue).toArray(new String[0]));
                } else {
                    messageStringHashMap.put(msg, config.getString(configValue));
                }
            }catch(NullPointerException a){
                Bukkit.getLogger().info("[NameColor] Unable to find value " + configValue + " in config.yml, resorting to default message.");
                messageStringHashMap.put(msg, defaults.get(msg));
            }
        }
        prefix = config.getString("prefix");
        if(prefix == null){
            prefix = "&8[&dNameColor&8] &f";
        }
        messageStringHashMap.put(Message.COLORS, defaults.get(Message.COLORS));
    }
    public static void sendMessage(CommandSender sender, Message message){
        if(messageStringHashMap.get(message) instanceof String[] msg){
            for(String str : msg){
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', str));
            }
        }else if(messageStringHashMap.get(message) instanceof String){
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + messageStringHashMap.get(message)));
        }else{
            Bukkit.getLogger().info("[NameColor] Unknown object type for message " + message.toString());
        }
    }
    public static void processPlaceholders(CommandSender sender, Message message, Player player){
        String finalMessage = (String) messageStringHashMap.get(message);
        while(finalMessage.contains("%display-name%")){
            finalMessage = finalMessage.replace("%display-name%", player.getDisplayName());
        }
        while(finalMessage.contains("%username%")){
            finalMessage = finalMessage.replace("%username%", player.getName());
        }
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + finalMessage));
    }
}
