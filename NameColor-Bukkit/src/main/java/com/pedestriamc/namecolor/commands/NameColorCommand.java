package com.pedestriamc.namecolor.commands;

import com.pedestriamc.common.message.Messenger;
import com.pedestriamc.namecolor.Message;
import com.pedestriamc.namecolor.NameColor;
import com.pedestriamc.namecolor.NameUtilities;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class NameColorCommand implements CommandExecutor {

    private final List<String> colors = List.of(
            "BLACK",
            "DARKBLUE",
            "DARKGREEN",
            "DARKAQUA",
            "DARKRED",
            "DARKPURPLE",
            "GOLD",
            "GRAY",
            "DARKGRAY",
            "BLUE",
            "GREEN",
            "AQUA",
            "RED",
            "LIGHTPURPLE",
            "YELLOW",
            "WHITE"
    );

    private final ChatColor[] chatColors = new ChatColor[] {
            ChatColor.BLACK,
            ChatColor.DARK_BLUE,
            ChatColor.DARK_GREEN,
            ChatColor.DARK_AQUA,
            ChatColor.DARK_RED,
            ChatColor.DARK_PURPLE,
            ChatColor.GOLD,
            ChatColor.GRAY,
            ChatColor.DARK_GRAY,
            ChatColor.BLUE,
            ChatColor.GREEN,
            ChatColor.AQUA,
            ChatColor.RED,
            ChatColor.LIGHT_PURPLE,
            ChatColor.YELLOW,
            ChatColor.WHITE
    };

    private final List<String> styleList = List.of(
            "BOLD",
            "UNDERLINE",
            "ITALICS",
            "ITALIC",
            "MAGIC",
            "STRIKE"
    );

    private final boolean notifyPlayer;
    private final NameUtilities nameUtilities;
    private final Messenger<Message> messenger;

    public NameColorCommand(NameColor nameColor) {
        FileConfiguration config = nameColor.getConfig();
        notifyPlayer = config.getBoolean("notify-players", true);
        nameUtilities = nameColor.getNameUtilities();
        this.messenger = nameColor.getMessenger();
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {

        Player selectedPlayer;
        StringBuilder color = new StringBuilder();

        if(args.length == 0) {
            messenger.sendMessage(sender, Message.INSUFFICIENT_ARGS);
            return true;
        }

        if (args[0].equalsIgnoreCase("HELP")) {
            messenger.sendMessage(sender, Message.NAMECOLOR_HELP);
            return true;
        }

        if(!(sender instanceof Player)) {
            if(args.length < 2) {
                messenger.sendMessage(sender, Message.INSUFFICIENT_ARGS);
                return true;
            }
            Player p = Bukkit.getPlayer(args[args.length - 1]);
            if(p != null) {
                selectedPlayer = p;
            }else{
                messenger.sendMessage(sender, Message.INVALID_PLAYER);
                return true;
            }
        } else {
            selectedPlayer = (Player) sender;
            if(forbids(sender, "namecolor.set") && forbids(sender, "namecolor.*")) {
                messenger.sendMessage(sender, Message.NO_PERMS);
                return true;
            }
            if(args.length >= 2 && Bukkit.getPlayer(args[args.length - 1]) != null) {
                Player p = Bukkit.getPlayer(args[args.length - 1]);

                if(p != null) {
                    if (forbids(sender, "namecolor.set.others") && forbids(sender, "namecolor.*")) {
                        messenger.sendMessage(sender, Message.NO_PERMS);
                        return true;
                    }
                    selectedPlayer = p;
                } else {
                    messenger.sendMessage(sender, Message.INVALID_PLAYER);
                    return true;
                }
            }
        }


        if(colors.contains(args[0].toUpperCase())) {

            color.append(chatColors[colors.indexOf(args[0].toUpperCase())]);

        } else if(args[0].toUpperCase().matches(("^#[a-fA-F0-9]{6}$"))) {

            color.append("&");
            color.append(args[0].toUpperCase());

        } else {

            messenger.sendMessage(sender, Message.INVALID_ARGS_COLOR);
            return true;

        }

        for(int i=0; i<args.length; i++) {
            args[i] = args[i].toUpperCase();
        }

        if(Arrays.stream(args).anyMatch(styleList::contains) && (
                forbids(sender, "namecolor.set.style") &&
                forbids(sender, "namecolor.*") &&
                forbids(sender, "namecolor.set.*")
        )) {
            messenger.sendMessage(sender, Message.NO_PERMS_STYLE);
            return true;
        }
        for(int i = 1; i<args.length; i++) {
            switch(args[i].toUpperCase()) {
                case "BOLD" -> color.append("&l");
                case "UNDERLINE" -> color.append("&n");
                case "ITALICS", "ITALIC" -> color.append("&o");
                case "MAGIC" -> color.append("&k");
                case "STRIKE" -> color.append("&m");
                default -> {}
            }
        }
        color.append(selectedPlayer.getName());
        nameUtilities.setDisplayName(color.toString(), selectedPlayer, true);

        HashMap<String, String> placeholders = new HashMap<>();
        placeholders.put("%display-name%", selectedPlayer.getDisplayName());
        placeholders.put("%username%", selectedPlayer.getName());

        if(!sender.equals(selectedPlayer)) {
            messenger.sendMessage(sender, Message.NAME_SET_OTHER, placeholders);
        }

        if(notifyPlayer) {
            messenger.sendMessage(selectedPlayer, Message.NAME_SET, placeholders);
        }

        return true;

    }

    public boolean forbids(CommandSender sender, String permission) {
        return !sender.hasPermission(permission);
    }
}
