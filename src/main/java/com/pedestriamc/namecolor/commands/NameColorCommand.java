package com.pedestriamc.namecolor.commands;

import com.pedestriamc.namecolor.NameColor;
import com.pedestriamc.namecolor.SetNameColor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NameColorCommand implements CommandExecutor {
    static String prefix = ChatColor.translateAlternateColorCodes('&', NameColor.getInstance().getPrefix()); //https://www.youtube.com/watch?v=qthBRLx7zUA
    static final String[] colors = new String[]{"BLACK", "DARKBLUE", "DARKGREEN", "DARKAQUA", "DARKRED", "DARKPURPLE", "GOLD", "GRAY", "DARKGRAY", "BLUE", "GREEN", "AQUA", "RED", "LIGHTPURPLE", "YELLOW", "WHITE"};
    static final ChatColor[] chatColors = new ChatColor[]{ChatColor.BLACK, ChatColor.DARK_BLUE, ChatColor.DARK_GREEN, ChatColor.DARK_AQUA, ChatColor.DARK_RED, ChatColor.DARK_PURPLE, ChatColor.GOLD, ChatColor.GRAY, ChatColor.DARK_GRAY, ChatColor.BLUE, ChatColor.GREEN, ChatColor.AQUA, ChatColor.RED, ChatColor.LIGHT_PURPLE, ChatColor.YELLOW, ChatColor.WHITE};
    private Player selectedPlayer;

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length > 1 && !sender.hasPermission("namecolor.set.other")){
            sender.sendMessage(prefix + "You don't have permission!");
            return true;
        }
        if (sender.hasPermission("namecolor.set")) {
            int pos = -1;
            args[0] = args[0].toUpperCase();
            if (args[0].equals("HELP")) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', NameColor.getInstance().getConfigFile().getString("help")));
                return true;
            }
            if (sender instanceof Player) {
                if (args.length == 2) {
                    if (Bukkit.getPlayer(args[1]) != null) {
                        selectedPlayer = Bukkit.getPlayer(args[1]);
                    } else {
                        sender.sendMessage(prefix + "Can't find player");
                        return true;
                    }
                } else if (args.length == 1) {
                    selectedPlayer = (Player) sender;
                } else {
                    sender.sendMessage(prefix + "Invalid usage of command");
                    return true;
                }
            }
            if (!(sender instanceof Player)) {
                if (args.length != 2) {
                    sender.sendMessage(prefix + "Invalid usage of command");
                    return true;
                }
                if (Bukkit.getPlayer(args[1]) != null) {
                    selectedPlayer = Bukkit.getPlayer(args[1]);
                } else {
                    sender.sendMessage(prefix + "Can't find player");
                    return true;
                }
            }
            for (int i = 0; i < colors.length; i++) {
                if (args[0].equals(colors[i])) {
                    pos = i;
                }
            }
            if (pos < 0) {

            }
            if (pos > -1) {
                SetNameColor.setColor(selectedPlayer, chatColors[pos], true);
                sender.sendMessage(prefix + ChatColor.WHITE + "Name color for " + ChatColor.GRAY + selectedPlayer.getName() + ChatColor.WHITE + " set to " + chatColors[pos] + colors[pos]);
                return true;
            }
            if (args[0].matches("^#[a-fA-F0-9]{6}$")) {
                SetNameColor.setColor(selectedPlayer, args[0], true);
                sender.sendMessage(prefix + ChatColor.WHITE + "Name color for " + ChatColor.GRAY + selectedPlayer.getName() + ChatColor.WHITE + " set to " + ChatColor.of(args[0]) + args[0]);
                return true;
            }
            sender.sendMessage(prefix + "Invalid usage of command");
            return true;

        }
        sender.sendMessage(prefix + "You don't have permission!");
        return true;
    }

}
