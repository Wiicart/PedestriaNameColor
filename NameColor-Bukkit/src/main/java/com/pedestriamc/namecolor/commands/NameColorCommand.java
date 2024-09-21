package com.pedestriamc.namecolor.commands;

import com.pedestriamc.namecolor.Message;
import com.pedestriamc.namecolor.NameColor;
import com.pedestriamc.namecolor.NameUtilities;
import com.pedestriamc.namecolor.Messenger;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class NameColorCommand implements CommandExecutor {

    private final String[] colors;
    private final ChatColor[] chatColors;
    private final boolean notifyPlayer;
    private final NameUtilities nameUtilities;

    public NameColorCommand(NameColor nameColor){
        colors = new String[]{"BLACK", "DARKBLUE", "DARKGREEN", "DARKAQUA", "DARKRED", "DARKPURPLE", "GOLD", "GRAY", "DARKGRAY", "BLUE", "GREEN", "AQUA", "RED", "LIGHTPURPLE", "YELLOW", "WHITE"};
        chatColors = new ChatColor[]{ChatColor.BLACK, ChatColor.DARK_BLUE, ChatColor.DARK_GREEN, ChatColor.DARK_AQUA, ChatColor.DARK_RED, ChatColor.DARK_PURPLE, ChatColor.GOLD, ChatColor.GRAY, ChatColor.DARK_GRAY, ChatColor.BLUE, ChatColor.GREEN, ChatColor.AQUA, ChatColor.RED, ChatColor.LIGHT_PURPLE, ChatColor.YELLOW, ChatColor.WHITE};
        notifyPlayer = nameColor.notifyChange();
        nameUtilities = nameColor.getNameUtilities();
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        Player selectedPlayer;
        StringBuilder color = new StringBuilder();
        //Check if command is invalid or player does not have permission
        if(sender instanceof Player){
            //Default selectedPlayer is sender
            selectedPlayer = (Player) sender;
            //Check for permissions
            if(!sender.hasPermission("namecolor.set") && !sender.hasPermission("namecolor.*")){
                Messenger.sendMessage(sender, Message.NO_PERMS);
                return true;
            }
            //Check if args == 0
            if(args.length == 0){
                Messenger.sendMessage(sender, Message.INSUFFICIENT_ARGS);
                return true;
            }
            if (args[0].equalsIgnoreCase("HELP")) {
                Messenger.sendMessage(sender, Message.NAMECOLOR_HELP);
                return true;
            }
            //Check if command is setting a different player's nickname
            if(args.length >= 2 && Bukkit.getPlayer(args[args.length - 1]) != null){
                if (!sender.hasPermission("namecolor.set.others") && !sender.hasPermission("namecolor.*")){
                    Messenger.sendMessage(sender, Message.NO_PERMS);
                    return true;
                }else{
                    selectedPlayer = Bukkit.getPlayer(args[args.length - 1]);
                }
            }
        }else{ //Sender is server
            if(args.length < 2){
                Messenger.sendMessage(sender, Message.INSUFFICIENT_ARGS);
                return true;
            }
            if(Bukkit.getPlayer(args[args.length - 1]) != null){
                selectedPlayer = Bukkit.getPlayer(args[args.length - 1]);
            }else{
                Messenger.sendMessage(sender, Message.INVALID_PLAYER);
                return true;
            }
        }
        //Checks done, time to process command
        //Determine type of color code, and begin building string
        if(Arrays.asList(colors).contains(args[0].toUpperCase())){ //https://stackoverflow.com/questions/1128723/how-do-i-determine-whether-an-array-contains-a-particular-value-in-java
            color.append(chatColors[Arrays.asList(colors).indexOf(args[0].toUpperCase())]);
        }else if(args[0].toUpperCase().matches(("^#[a-fA-F0-9]{6}$"))){
            color.append("&");
            color.append(args[0].toUpperCase());
        }else{
            //Invalid color as first arg
            Messenger.sendMessage(sender, Message.INVALID_ARGS_COLOR);
            return true;
        }
        //process additional options such as bold, italics, etc.
        List<String> styleList = Arrays.asList("BOLD", "UNDERLINE", "ITALICS", "ITALIC", "MAGIC", "STRIKE");
        for(int i=0; i<args.length; i++){
            args[i] = args[i].toUpperCase();
        }
        if(Arrays.stream(args).anyMatch(styleList::contains) && (!sender.hasPermission("namecolor.set.style") && !sender.hasPermission("namecolor.*") && !sender.hasPermission("namecolor.set.*"))){
            Messenger.sendMessage(sender, Message.NO_PERMS_STYLE);
            return true;
        }
        for(int i = 1; i<args.length; i++){
            switch(args[i].toUpperCase()){
                case "BOLD":
                    color.append("&l");
                    break;
                case "UNDERLINE":
                    color.append("&n");
                    break;
                case "ITALICS":
                case "ITALIC":
                    color.append("&o");
                    break;
                case "MAGIC":
                    color.append("&k");
                    break;
                case "STRIKE":
                    color.append("&m");
                    break;
                default:
                    break;
                }
            }
        color.append(selectedPlayer.getName());
        nameUtilities.setDisplayName(color.toString(), selectedPlayer, true);
        if(!sender.equals(selectedPlayer)){
            Messenger.processPlaceholders(sender, Message.NAME_SET_OTHER, selectedPlayer);
        }
        if(notifyPlayer){
            Messenger.processPlaceholders(selectedPlayer, Message.NAME_SET, selectedPlayer);
        }
        return true;
    }
}
