package com.pedestriamc.namecolor.commands;

import com.pedestriamc.common.message.Messenger;
import com.pedestriamc.namecolor.Message;
import com.pedestriamc.namecolor.NameColor;
import com.pedestriamc.namecolor.NameUtilities;
import com.pedestriamc.namecolor.user.User;
import com.pedestriamc.namecolor.user.UserUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Map;

public class WhoIsCommand implements CommandExecutor {

    private final UserUtil userUtil;
    private final Messenger<Message> messenger;

    public WhoIsCommand(@NotNull NameColor nameColor) {
        userUtil = nameColor.getUserUtil();
        messenger = nameColor.getMessenger();
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if(doesNotHavePermission(sender)) {
            messenger.sendMessage(sender, Message.NO_PERMS);
            return true;
        }

        if(args.length == 0) {
            messenger.sendMessage(sender, Message.INSUFFICIENT_ARGS);
            return true;
        } else if(args.length > 1) {
            messenger.sendMessage(sender, Message.INVALID_ARGS_WHOIS);
            return true;
        }

        String stripped = NameUtilities.stripColor(args[0]);
        Player player = null;
        for(User user : userUtil.getUsers()) {
            if(NameUtilities.stripColor(user.getDisplayName()).equalsIgnoreCase(stripped)) {
                player = user.getPlayer();
                break;
            }
        }

        if(player == null) {
            messenger.sendMessage(sender, Message.NICK_UNKNOWN, Map.of("%display-name%", stripped));
        } else {
            messenger.sendMessage(sender, Message.WHOIS_MESSAGE, generatePlaceholders(player));
        }

        return true;
    }

    private boolean doesNotHavePermission(@NotNull CommandSender sender) {
        return !(sender.isOp() ||
                sender.hasPermission("*") ||
                sender.hasPermission("namecolor.*") ||
                sender.hasPermission("namecolor.whois"));
    }

    @Contract("_ -> new")
    private @NotNull @Unmodifiable Map<String, String> generatePlaceholders(@NotNull Player player) {
        return Map.of(
                "%display-name%", player.getDisplayName(),
                "%username%", player.getName()
        );
    }
}
