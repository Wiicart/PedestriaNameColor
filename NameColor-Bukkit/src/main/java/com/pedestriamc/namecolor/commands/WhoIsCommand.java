package com.pedestriamc.namecolor.commands;

import com.pedestriamc.common.message.Messenger;
import com.pedestriamc.namecolor.Message;
import com.pedestriamc.namecolor.NameColor;
import com.pedestriamc.namecolor.NameUtilities;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class WhoIsCommand implements CommandExecutor {

    private final NameUtilities nameUtilities;
    private final Messenger<Message> messenger;

    public WhoIsCommand(NameColor nameColor) {
        nameUtilities = nameColor.getNameUtilities();
        messenger = nameColor.getMessenger();
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if(sender.hasPermission("namecolor.whois") || sender.hasPermission("namecolor.*")) {
            if(args.length == 0) {
                messenger.sendMessage(sender, Message.INSUFFICIENT_ARGS);
                return true;
            }
            if(args.length > 1) {
                messenger.sendMessage(sender, Message.INVALID_ARGS_WHOIS);
                return true;
            }

            String p = nameUtilities.getPlayerName(args[0]);
            if(p == null) {
                messenger.sendMessage(sender, Message.INVALID_PLAYER);
            } else {
                HashMap<String, String> placeholders = new HashMap<>();
                placeholders.put("%display-name%", Bukkit.getPlayer(p).getDisplayName());
                placeholders.put("%username%", p);
                messenger.sendMessage(sender, Message.WHOIS_MESSAGE, placeholders);
            }

        } else {
            messenger.sendMessage(sender, Message.NO_PERMS);
        }
        return true;
    }
}
