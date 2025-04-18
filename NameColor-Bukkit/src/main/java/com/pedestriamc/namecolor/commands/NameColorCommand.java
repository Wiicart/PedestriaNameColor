package com.pedestriamc.namecolor.commands;

import com.google.common.collect.ImmutableMap;
import com.pedestriamc.common.message.Messenger;
import com.pedestriamc.namecolor.user.User;
import com.pedestriamc.namecolor.Message;
import com.pedestriamc.namecolor.NameColor;
import com.pedestriamc.namecolor.user.UserUtil;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

public class NameColorCommand implements CommandExecutor {

    // Traditional HEX format, with "&" at the start
    private static final Pattern SPIGOT_HEX = Pattern.compile("&#[a-fA-F0-9]{6}", Pattern.CASE_INSENSITIVE);

    // Traditional HEX format
    private static final Pattern STANDARD_HEX = Pattern.compile("^#[a-fA-F0-9]{6}$", Pattern.CASE_INSENSITIVE);

    private static final Map<String, ChatColor> colorMap;
    private static final Map<String, ChatColor> styleMap;

    static {
        Map<String, ChatColor> tempColorMap = new HashMap<>();
        tempColorMap.put("BLACK", ChatColor.BLACK);
        tempColorMap.put("DARKBLUE", ChatColor.DARK_BLUE);
        tempColorMap.put("DARKGREEN", ChatColor.DARK_GREEN);
        tempColorMap.put("DARKAQUA", ChatColor.DARK_AQUA);
        tempColorMap.put("DARKRED", ChatColor.DARK_RED);
        tempColorMap.put("DARKPURPLE", ChatColor.DARK_PURPLE);
        tempColorMap.put("GOLD", ChatColor.GOLD);
        tempColorMap.put("GRAY", ChatColor.GRAY);
        tempColorMap.put("DARKGRAY", ChatColor.DARK_GRAY);
        tempColorMap.put("BLUE", ChatColor.BLUE);
        tempColorMap.put("GREEN", ChatColor.GREEN);
        tempColorMap.put("AQUA", ChatColor.AQUA);
        tempColorMap.put("RED", ChatColor.RED);
        tempColorMap.put("LIGHTPURPLE", ChatColor.LIGHT_PURPLE);
        tempColorMap.put("PINK", ChatColor.LIGHT_PURPLE);
        tempColorMap.put("YELLOW", ChatColor.YELLOW);
        tempColorMap.put("WHITE", ChatColor.WHITE);
        colorMap = ImmutableMap.copyOf(tempColorMap);

        Map<String, ChatColor> tempStyleMap = new HashMap<>();
        tempStyleMap.put("BOLD", ChatColor.BOLD);
        tempStyleMap.put("ITALIC", ChatColor.ITALIC);
        tempStyleMap.put("ITALICS", ChatColor.ITALIC);
        tempStyleMap.put("UNDERLINE", ChatColor.UNDERLINE);
        tempStyleMap.put("MAGIC", ChatColor.MAGIC);
        tempStyleMap.put("STRIKE", ChatColor.STRIKETHROUGH);
        styleMap = ImmutableMap.copyOf(tempStyleMap);
    }

    private final boolean notify;
    private final UserUtil userUtil;
    private final Messenger<Message> messenger;

    public NameColorCommand(NameColor nameColor) {
        FileConfiguration config = nameColor.getConfig();
        notify = config.getBoolean("notify-players", true);
        userUtil = nameColor.getUserUtil();
        this.messenger = nameColor.getMessenger();
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if(doesNotHavePermission(sender)) {
            messenger.sendMessage(sender, Message.NO_PERMS);
            return true;
        }

        if (args.length == 1 && !(sender instanceof Player)) {
            messenger.sendMessage(sender, Message.CONSOLE_MUST_DEFINE_PLAYER);
            return true;
        }

        if(args.length > 7) {
            messenger.sendMessage(sender, Message.INVALID_ARGS_COLOR);
            return true;
        }

        String finalArg = args[args.length - 1];
        Player target = Bukkit.getPlayer(finalArg);
        if(target == null) {
            if(!(sender instanceof Player)) {
                messenger.sendMessage(sender, Message.INVALID_PLAYER);
                return true;
            }

            if(styleMap.get(finalArg.toUpperCase(Locale.ROOT)) == null) {
                messenger.sendMessage(sender, Message.INVALID_PLAYER);
                return true;
            }

            target = (Player) sender;
        } else {
            args[args.length - 1] = "";
        }

        StringBuilder builder = new StringBuilder();

        String color = args[0].toUpperCase(Locale.ROOT);
        if(isNotColor(color)) {
            messenger.sendMessage(sender, Message.INVALID_COLOR);
            return true;
        }
        builder.append(processColor(color));

        if(hasStylePermission(sender)) {
            appendStyles(builder, args);
        }
        builder.append(target.getName());

        User user = userUtil.getUser(target.getUniqueId());
        user.setDisplayName(builder.toString());
        userUtil.saveUser(user);

        if(!sender.equals(target)) {
            messenger.sendMessage(sender, Message.NAME_SET_OTHER, getPlaceholders(target));
        }

        if(notify) {
            messenger.sendMessage(target, Message.NAME_SET, getPlaceholders(target));
        }

        return true;
    }

    // Makes HEX codes without & have &, ignores other color codes bc NameUtilities will handle those.
    private String processColor(@NotNull String color) {
        if(STANDARD_HEX.matcher(color).matches()) {
            color = "&" + color;
        }

        if(colorMap.containsKey(color)) {
            color = String.valueOf(colorMap.get(color));
        }

        return color;
    }

    private boolean isNotColor(String color) {
        return !SPIGOT_HEX.matcher(color).matches() && !STANDARD_HEX.matcher(color).matches() && !colorMap.containsKey(color);
    }

    private void appendStyles(@NotNull StringBuilder builder, String @NotNull [] args) {
        for(String arg : args) {
            ChatColor style = styleMap.get(arg.toUpperCase(Locale.ROOT));
            if(style != null) {
                builder.append(style);
            }
        }
    }

    private boolean doesNotHavePermission(CommandSender sender) {
        return !(sender.isOp() ||
                sender.hasPermission("*") ||
                sender.hasPermission("namecolor.*") ||
                sender.hasPermission("namecolor.set") ||
                sender.hasPermission("namecolor.set.*"));
    }

    private boolean hasStylePermission(CommandSender sender) {
        return sender.isOp() ||
                sender.hasPermission("namecolor.*") ||
                sender.hasPermission("namecolor.set.*") ||
                sender.hasPermission("namecolor.set.style");
    }

    @Contract("_ -> new")
    private @NotNull @Unmodifiable Map<String, String> getPlaceholders(@NotNull Player player) {
        return Map.of(
                "%display-name%", player.getDisplayName(),
                "%username%", player.getName()
        );
    }
}
