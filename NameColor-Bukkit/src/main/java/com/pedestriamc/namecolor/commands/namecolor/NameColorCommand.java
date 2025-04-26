package com.pedestriamc.namecolor.commands.namecolor;

import com.pedestriamc.common.message.Messenger;
import com.pedestriamc.namecolor.user.User;
import com.pedestriamc.namecolor.Message;
import com.pedestriamc.namecolor.NameColor;
import com.pedestriamc.namecolor.user.UserUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

public class NameColorCommand implements CommandExecutor {

    // Traditional HEX format, with "&" at the start
    private static final Pattern SPIGOT_HEX = Pattern.compile("&#[a-fA-F0-9]{6}", Pattern.CASE_INSENSITIVE);

    // Traditional HEX format
    private static final Pattern STANDARD_HEX = Pattern.compile("^#[a-fA-F0-9]{6}$", Pattern.CASE_INSENSITIVE);

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

        if(args[0].equalsIgnoreCase("help")) {
            messenger.sendMessage(sender, Message.NAMECOLOR_HELP);
            return true;
        }

        String finalArg = args[args.length - 1];
        Player target = Bukkit.getPlayer(finalArg);
        if(target == null) {
            if(!(sender instanceof Player)) {
                messenger.sendMessage(sender, Message.INVALID_PLAYER);
                return true;
            }

            if(isNotStyleOrColor(finalArg)) {
                messenger.sendMessage(sender, Message.INVALID_PLAYER);
                return true;
            }

            target = (Player) sender;
        } else {
            args[args.length - 1] = "";
        }

        StringBuilder builder = new StringBuilder();

        String color = args[0].toUpperCase(Locale.ROOT);
        args[0] = "";
        if(processColor(sender, builder, color)) {
            return true;
        }

        if(appendStyles(sender, builder, args)) {
            return true;
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
    private boolean processColor(@NotNull CommandSender sender, @NotNull StringBuilder builder, @NotNull String color) {
        if(STANDARD_HEX.matcher(color).matches()) {
            color = "&" + color;
        }

        if(SPIGOT_HEX.matcher(color).matches()) {
            if(noColorPermission(sender, Color.HEX)) {
                messenger.sendMessage(sender, Message.NO_PERMS_COLOR_SPECIFIC, Map.of("%color%", "hex"));
                return true;
            }
            builder.append(color);
            return false;
        }

        Color c = Color.getColor(color);
        if(c != null) {
            if(noColorPermission(sender, c)) {
                messenger.sendMessage(
                        sender,
                        Message.NO_PERMS_COLOR_SPECIFIC,
                        Map.of("%color%", c.getName().toLowerCase(Locale.ROOT))
                );
                return true;
            }
            builder.append(c);
            return false;
        }

        messenger.sendMessage(sender, Message.INVALID_COLOR);
        return true;
    }

    private boolean isNotColor(@NotNull String color) {
        return !SPIGOT_HEX.matcher(color).matches() && !STANDARD_HEX.matcher(color).matches() && Color.getColor(color) == null;
    }

    private boolean isNotStyleOrColor(String s) {
        return isNotColor(s) && Style.getStyle(s) == null;
    }

    // returns true if the main method should return
    private boolean appendStyles(@NotNull CommandSender sender, @NotNull StringBuilder builder, String @NotNull [] args) {
        for(String arg : args) {
            Style style = Style.getStyle(arg.toUpperCase(Locale.ROOT));
            if(style != null) {
                if(noStylePermission(sender, style)) {
                    messenger.sendMessage(sender,
                            Message.NO_PERMS_STYLE_SPECIFIC,
                            Map.of("%style%", style.name().toLowerCase(Locale.ROOT))
                    );
                    return true;
                }
                builder.append(style);
            } else {
                if(!arg.isEmpty()) {
                    messenger.sendMessage(sender, Message.UNKNOWN_STYLE);
                    return true;
                }
            }
        }
        return false;
    }

    private boolean noStylePermission(@NotNull CommandSender sender, @NotNull Style style) {
        return !sender.isOp() &&
                !sender.hasPermission("*") &&
                !sender.hasPermission("namecolor.*") &&
                !sender.hasPermission("namecolor.set.*") &&
                !sender.hasPermission("namecolor.set.style.*") &&
                !sender.hasPermission(style.getPermission());
    }

    private boolean noColorPermission(@NotNull CommandSender sender, @NotNull Color color) {
        return !sender.isOp() &&
                !sender.hasPermission("*") &&
                !sender.hasPermission("namecolor.*") &&
                !sender.hasPermission("namecolor.set.*") &&
                !sender.hasPermission("namecolor.set.style.*") &&
                !sender.hasPermission(color.getPermission());
    }

    private boolean doesNotHavePermission(@NotNull CommandSender sender) {
        return !(sender.isOp() ||
                sender.hasPermission("*") ||
                sender.hasPermission("namecolor.*") ||
                sender.hasPermission("namecolor.set") ||
                sender.hasPermission("namecolor.set.*"));
    }

    @Contract("_ -> new")
    private @NotNull @Unmodifiable Map<String, String> getPlaceholders(@NotNull Player player) {
        return Map.of(
                "%display-name%", player.getDisplayName(),
                "%username%", player.getName()
        );
    }
}
