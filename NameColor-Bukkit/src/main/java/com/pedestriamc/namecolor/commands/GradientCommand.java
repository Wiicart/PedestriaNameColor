package com.pedestriamc.namecolor.commands;

import com.google.common.collect.ImmutableMap;
import com.pedestriamc.common.message.Messenger;
import com.pedestriamc.namecolor.Message;
import com.pedestriamc.namecolor.NameColor;
import com.pedestriamc.namecolor.NameUtilities;
import com.pedestriamc.namecolor.api.color.Gradient;
import com.pedestriamc.namecolor.user.User;
import com.pedestriamc.namecolor.user.UserUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.awt.Color;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.pedestriamc.namecolor.Message.*;

@SuppressWarnings("unused")
public class GradientCommand implements CommandExecutor {

    private static final Map<String, Color> colors;

    static {
        HashMap<String, Color> temp = new HashMap<>();
        temp.put("BLACK", Color.BLACK);
        temp.put("BLUE", Color.BLUE);
        temp.put("GREEN", Color.GREEN);
        temp.put("CYAN", Color.CYAN);
        temp.put("RED", Color.RED);
        temp.put("WHITE", Color.WHITE);
        temp.put("YELLOW", Color.YELLOW);
        temp.put("MAGENTA", Color.MAGENTA);
        temp.put("ORANGE", Color.ORANGE);
        temp.put("LIGHTGRAY", Color.LIGHT_GRAY);
        temp.put("GRAY", Color.GRAY);
        temp.put("DARKGRAY", Color.DARK_GRAY);
        colors = ImmutableMap.copyOf(temp);
    }

    private final Messenger<Message> messenger;
    private final UserUtil userUtil;

    private final boolean notify;

    public GradientCommand(NameColor nameColor) {
        messenger = nameColor.getMessenger();
        userUtil = nameColor.getUserUtil();
        notify = nameColor.getConfig().getBoolean("notify-players", true);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if(doesNotHavePermission(sender)) {
            messenger.sendMessage(sender, NO_PERMS);
            return true;
        }

        if(doesNotHaveAcceptableLength(sender, args.length)) {
            return true;
        }

        Player target = processTarget(sender, args);
        if(target == null) {
            return true;
        }
        User user = userUtil.getUser(target.getUniqueId());

        Color color1 = processColor(args[0]);
        Color color2 = processColor(args[1]);
        if(color1 == null || color2 == null) {
            messenger.sendMessage(sender, GRADIENT_INVALID_COLOR);
            return true;
        }

        String strippedCurrent = NameUtilities.stripColor(target.getDisplayName());
        String nick = Gradient.apply(color1, color2, strippedCurrent);

        user.setDisplayName(nick);
        userUtil.saveUser(user);

        if(!sender.equals(target)) {
            messenger.sendMessage(sender, Message.NAME_SET_OTHER, getPlaceholders(target));
        }

        if(notify) {
            messenger.sendMessage(target, Message.NAME_SET, getPlaceholders(target));
        }

        return true;
    }

    private Color processColor(String string) {
        if(NameUtilities.SPIGOT_HEX.matcher(string).matches()) {
            return Color.decode(string.substring(1));
        }
        return colors.get(string.toLowerCase(Locale.ROOT));
    }


    private Player processTarget(CommandSender sender, String[] args) {
        Player target;
        if(!(sender instanceof Player player)) {
            if(args.length < 3) {
                messenger.sendMessage(sender, CONSOLE_MUST_DEFINE_PLAYER);
                return null;
            }

            target = Bukkit.getPlayer(args[2]);

            if(target == null) {
                messenger.sendMessage(sender, INVALID_PLAYER);
                return null;
            }
        } else if(args.length == 3) {
            if(doesNotHavePermissionForOthers(sender)) {
                messenger.sendMessage(sender, NO_PERMS_OTHER);
                return null;
            }

            target = Bukkit.getPlayer(args[2]);
            if(target == null) {
                messenger.sendMessage(sender, INVALID_PLAYER);
                return null;
            }
        } else {
            target = player;
        }

        return target;
    }

    private boolean doesNotHaveAcceptableLength(CommandSender sender, int length) {
        if(length < 2) {
            messenger.sendMessage(sender, INSUFFICIENT_ARGS);
            return true;
        }

        if(length > 3) {
            messenger.sendMessage(sender, GRADIENT_INVALID_USAGE);
            return true;
        }

        return false;
    }

    private boolean doesNotHavePermission(CommandSender sender) {
        return !(sender.isOp() ||
                sender.hasPermission("*") ||
                sender.hasPermission("namecolor.*") ||
                sender.hasPermission("namecolor.gradient") ||
                sender.hasPermission("namecolor.gradient.*")
        );
    }

    private boolean doesNotHavePermissionForOthers(CommandSender sender) {
        return !(sender.isOp() ||
                sender.hasPermission("*") ||
                sender.hasPermission("namecolor.*") ||
                sender.hasPermission("namecolor.gradient.*") ||
                sender.hasPermission("namecolor.gradient.other"));
    }

    @Contract("_ -> new")
    private @NotNull @Unmodifiable Map<String, String> getPlaceholders(@NotNull Player player) {
        return Map.of(
                "%display-name%", player.getDisplayName(),
                "%username%", player.getName()
        );
    }

}
