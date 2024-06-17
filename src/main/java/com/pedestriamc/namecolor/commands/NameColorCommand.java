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

import java.util.Arrays;

public class NameColorCommand implements CommandExecutor {

    private final String[] colors = new String[]{"BLACK", "DARKBLUE", "DARKGREEN", "DARKAQUA", "DARKRED", "DARKPURPLE", "GOLD", "GRAY", "DARKGRAY", "BLUE", "GREEN", "AQUA", "RED", "LIGHTPURPLE", "YELLOW", "WHITE"};
    private final ChatColor[] chatColors = new ChatColor[]{ChatColor.BLACK, ChatColor.DARK_BLUE, ChatColor.DARK_GREEN, ChatColor.DARK_AQUA, ChatColor.DARK_RED, ChatColor.DARK_PURPLE, ChatColor.GOLD, ChatColor.GRAY, ChatColor.DARK_GRAY, ChatColor.BLUE, ChatColor.GREEN, ChatColor.AQUA, ChatColor.RED, ChatColor.LIGHT_PURPLE, ChatColor.YELLOW, ChatColor.WHITE};
    private FileConfiguration config = NameColor.getInstance().getConfigFile();

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player selectedPlayer;
        boolean setOther = false;
        StringBuilder color = new StringBuilder();
        //Check if command is invalid or player does not have permission
        if(sender instanceof Player){
            //Default selectedPlayer is sender
            selectedPlayer = (Player) sender;
            //Check for permissions
            if(!sender.hasPermission("namecolor.set") && !sender.hasPermission("namecolor.*")){
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', NameColor.getInstance().getPrefix()) + ChatColor.translateAlternateColorCodes('&', config.getString("no-perms")));
                return true;
            }
            //Check if args == 0
            if(args.length == 0){
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', NameColor.getInstance().getPrefix()) + ChatColor.translateAlternateColorCodes('&', config.getString("insufficient-args")));
                return true;
            }
            if (args[0].equals("HELP")) {
                for(String msgs : NameColor.getInstance().getConfigFile().getStringList("namecolor-help")){
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',msgs));
                }
                return true;
            }
            //Check if command is setting a different player's nickname
            if(args.length >= 2 && Bukkit.getPlayer(args[args.length - 1]) != null){
                if (!sender.hasPermission("namecolor.set.others") && !sender.hasPermission("namecolor.*")){
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', NameColor.getInstance().getPrefix()) + ChatColor.translateAlternateColorCodes('&', config.getString("no-perms")));
                    return true;
                }else{
                    selectedPlayer = Bukkit.getPlayer(args[args.length - 1]);
                    setOther = true;
                }
            }
        }else{ //Sender is server
            if(args.length < 2){
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', NameColor.getInstance().getPrefix()) + ChatColor.translateAlternateColorCodes('&', config.getString("insufficient-args")));
                return true;
            }
            if(Bukkit.getPlayer(args[args.length - 1]) != null){
                selectedPlayer = Bukkit.getPlayer(args[args.length - 1]);
            }else{
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', NameColor.getInstance().getPrefix()) + ChatColor.translateAlternateColorCodes('&', config.getString("invalid-player")));
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
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', NameColor.getInstance().getPrefix()) + ChatColor.translateAlternateColorCodes('&', config.getString("invalid-color")));
        }
        //process additional options such as bold, italics, etc.
        for(int i = 1; i<args.length; i++){
            switch(args[i].toUpperCase()){
                case "BOLD":
                    color.append("&l");
                    break;
                case "UNDERLINE":
                    color.append("&n");
                    break;
                case "ITALICS":
                    color.append("&o");
                    break;
                case "MAGIC":
                    color.append("&k");
                    break;
                case "STRIKE":
                    color.append("&m");
                    break;
                case "ITALIC":
                    color.append("&o");
                    break;
                default:
                    break;
                }
            }
        color.append(selectedPlayer.getName());
        SetName.setNick(color.toString(), selectedPlayer, true);
        if(!sender.equals(selectedPlayer)){
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', NameColor.getInstance().getPrefix()) + NameColor.getInstance().processSenderPlaceholders(selectedPlayer));
        }
        selectedPlayer.sendMessage(ChatColor.translateAlternateColorCodes('&', NameColor.getInstance().getPrefix()) + NameColor.getInstance().processPlaceholders(selectedPlayer));
        return true;
    }
}
