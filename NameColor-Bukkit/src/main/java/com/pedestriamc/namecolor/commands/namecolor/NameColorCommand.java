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
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

public class NameColorCommand implements CommandExecutor {

    // Traditional HEX format, with "&" at the start
    private static final Pattern SPIGOT_HEX = Pattern.compile("&#[a-fA-F0-9]{6}", Pattern.CASE_INSENSITIVE);

    // Traditional HEX format
    private static final Pattern STANDARD_HEX = Pattern.compile("^#[a-fA-F0-9]{6}$", Pattern.CASE_INSENSITIVE);

    private static final int MAX_ARG_LENGTH = Style.values().length /* Style quantity */
            + 1 /* One color */
            + 1 /* Only one player may be defined */;


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
        Player target;
        boolean senderIsNotPlayer = true;

        if(sender instanceof Player p) {
            senderIsNotPlayer = false;
            target = p;
        } else {
            target = null;
        }

        if(handleConditions(args, sender, senderIsNotPlayer)) {
            return true;
        }

        if(args[0].equalsIgnoreCase("help")) {
            messenger.sendMessage(sender, Message.NAMECOLOR_HELP);
            return true;
        }

        if(canUseOnOthers(sender)) {
            String finalArg = args[args.length - 1];
            Player potentialTarget = Bukkit.getPlayer(finalArg);

            if(potentialTarget == null) {
                if(senderIsNotPlayer) {
                    messenger.sendMessage(sender, Message.INVALID_PLAYER);
                    return true;
                }

                if(isNotStyleOrColor(finalArg)) {
                    messenger.sendMessage(sender, Message.INVALID_PLAYER);
                    return true;
                }
            } else {
                args[args.length - 1] = "";
                target = potentialTarget;
            }
        }

        // This shouldn't evaluate to true, here to stop IDE warning
        if(target == null) {
            Bukkit.getLogger().warning("[NameColor] An error occurred while executing the /namecolor command.");
            return true;
        }

        String displayName = generateDisplayName(args, sender, target.getName());
        if(displayName == null) {
            return true;
        }

        updateUser(userUtil.getUser(target.getUniqueId()), displayName);

        if(!sender.equals(target)) {
            messenger.sendMessage(sender, Message.NAME_SET_OTHER, getPlaceholders(target));
        }

        if(notify) {
            messenger.sendMessage(target, Message.NAME_SET, getPlaceholders(target));
        }

        return true;
    }

    private String generateDisplayName(String[] args, CommandSender sender, String name) {
        StringBuilder builder = new StringBuilder();

        String color = args[0].toUpperCase(Locale.ROOT);
        args[0] = "";

        builder = processColor(sender, builder, color);
        if(builder == null) {
            return null;
        }

        builder = appendStyles(sender, builder, args);
        if(builder == null) {
            return null;
        }

        return builder.append(name).toString();
    }

    private boolean handleConditions(String[] args, CommandSender sender, boolean isNotPlayer) {
        if(doesNotHavePermission(sender)) {
            messenger.sendMessage(sender, Message.NO_PERMS);
            return true;
        }

        if(args.length == 0) {
            messenger.sendMessage(sender, Message.INSUFFICIENT_ARGS);
            return true;
        }

        if (args.length == 1 && isNotPlayer) {
            messenger.sendMessage(sender, Message.CONSOLE_MUST_DEFINE_PLAYER);
            return true;
        }

        if(args.length > MAX_ARG_LENGTH) {
            messenger.sendMessage(sender, Message.INVALID_ARGS_COLOR);
            return true;
        }

        return false;
    }

    private void updateUser(User user, String displayName) {
        user.setDisplayName(displayName);
        userUtil.saveUser(user);
    }


    // Makes HEX codes without & have &, ignores other color codes bc NameUtilities will handle those.
    private StringBuilder processColor(@NotNull CommandSender sender, @NotNull StringBuilder builder, @NotNull String color) {
        if(STANDARD_HEX.matcher(color).matches()) {
            color = "&" + color;
        }

        if(SPIGOT_HEX.matcher(color).matches()) {
            if(noColorPermission(sender, Color.HEX)) {
                messenger.sendMessage(sender, Message.NO_PERMS_COLOR_SPECIFIC, Map.of("%color%", "hex"));
                return null;
            }

            return builder.append(color);
        }

        Color c = Color.getColor(color);
        if(c != null) {
            if(noColorPermission(sender, c)) {
                messenger.sendMessage(
                        sender,
                        Message.NO_PERMS_COLOR_SPECIFIC,
                        Map.of("%color%", c.getName().toLowerCase(Locale.ROOT))
                );
                return null;
            }

            return builder.append(c);
        }

        messenger.sendMessage(sender, Message.INVALID_COLOR);
        return null;
    }

    private boolean isNotColor(@NotNull String color) {
        return !SPIGOT_HEX.matcher(color).matches() && !STANDARD_HEX.matcher(color).matches() && Color.getColor(color) == null;
    }

    private boolean isNotStyleOrColor(String s) {
        return isNotColor(s) && Style.getStyle(s) == null;
    }

    private @Nullable StringBuilder appendStyles(@NotNull CommandSender sender, @NotNull StringBuilder builder, String @NotNull [] args) {
        for(String arg : args) {
            Style style = Style.getStyle(arg.toUpperCase(Locale.ROOT));
            if(style != null) {
                if(noStylePermission(sender, style)) {
                    messenger.sendMessage(sender,
                            Message.NO_PERMS_STYLE_SPECIFIC,
                            Map.of("%style%", style.name().toLowerCase(Locale.ROOT))
                    );
                    return null;
                }
                builder.append(style);
            } else {
                if(!arg.isEmpty()) {
                    messenger.sendMessage(sender, Message.UNKNOWN_STYLE);
                    return null;
                }
            }
        }
        return builder;
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
    private @NotNull Map<String, String> getPlaceholders(@NotNull Player player) {
        return Map.of(
                "%display-name%", player.getDisplayName(),
                "%username%", player.getName()
        );
    }

    private boolean canUseOnOthers(@NotNull CommandSender sender) {
        return sender.isOp() || sender.hasPermission("*") || sender.hasPermission("namecolor.*") ||
                sender.hasPermission("namecolor.set.*") || sender.hasPermission("namecolor.set.others");
    }
}
