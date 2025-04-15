package com.pedestriamc.namecolor.commands;

import com.pedestriamc.common.message.Messenger;
import com.pedestriamc.namecolor.Message;
import com.pedestriamc.namecolor.NameColor;
import com.pedestriamc.namecolor.NameUtilities;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.regex.Pattern;

public class NicknameCommand implements CommandExecutor {

    private static final Pattern PATTERN = Pattern.compile("&#[a-fA-F0-9]{6}", Pattern.CASE_INSENSITIVE);

    private final NameColor namecolor;
    private final boolean notifyChange;
    private final NameUtilities nameUtilities;
    private final Messenger<Message> messenger;

    public NicknameCommand(NameColor nameColor) {
        this.namecolor = nameColor;
        notifyChange = nameColor.notifyChange();
        nameUtilities = nameColor.getNameUtilities();
        messenger = nameColor.getMessenger();
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        Player selectedPlayer;
        String nick;

        //Check for enough args
        if(args.length == 0) {
            messenger.sendMessage(sender, Message.INSUFFICIENT_ARGS);
            return true;
        }
        //Check for too many/invalid args
        if(args.length > 2) {
            messenger.sendMessage(sender, Message.INVALID_ARGS_NICK);
            return true;
        }
        //Help message
        if (args[0].equalsIgnoreCase("help")) {
            messenger.sendMessage(sender, Message.NICKNAME_HELP);
            return true;
        }

        if(args.length == 1) {
            if(!sender.hasPermission("namecolor.nick") && !sender.hasPermission("namecolor.*") && !sender.hasPermission("namecolor.nick.*")) {
                messenger.sendMessage(sender, Message.NO_PERMS);
                return true;
            }
            if(sender instanceof Player p) {
                if (args[0].equalsIgnoreCase("reset")) {
                    nameUtilities.setDisplayName(sender.getName(), p,true);
                    if(notifyChange) {
                        messenger.sendMessage(sender, Message.NAME_SET, getPlaceholders(p));
                    }
                    return true;
                }

                nick = args[0];
                if(isNickTooLong(nick)) {
                    messenger.sendMessage(sender, Message.NICK_TOO_LONG);
                    return true;
                }

                if(!sender.hasPermission("namecolor.filter.bypass")) {
                    if(isDuplicateNickname(nick, p)) {
                        messenger.sendMessage(sender, Message.USERNAME_NICK_PROHIBITED);
                        return true;
                    }
                    if(isBlacklistedNickname(nick)) {
                        messenger.sendMessage(sender, Message.NICK_BLACKLIST);
                        return true;
                    }
                }
                if(!sender.hasPermission("namecolor.nick.color") && !nick.equals(stripColor(nick))) {
                    nameUtilities.setDisplayName(stripColor(args[0]), p, true);
                    if(notifyChange) {
                        messenger.sendMessage(sender, Message.NO_NICK_COLOR, getPlaceholders(p));
                    }
                    return true;
                }
                nameUtilities.setDisplayName(nick, p,true);
                if(notifyChange) {
                    messenger.sendMessage(p, Message.NAME_SET, getPlaceholders(p));
                }
            } else {
                messenger.sendMessage(sender, Message.INSUFFICIENT_ARGS);
            }
            return true;
        }
        //At this point, command has 2 args, meaning that it is setting another player's nickname
        if(!sender.hasPermission("namecolor.nick.others")) {
            messenger.sendMessage(sender, Message.NO_PERMS);
            return true;
        }
        selectedPlayer = Bukkit.getPlayer(args[1]);
        if(selectedPlayer != null) {
            if (args[0].equalsIgnoreCase("RESET")) {
                nameUtilities.setDisplayName(selectedPlayer.getName(), selectedPlayer,true);
                if(notifyChange) {
                    messenger.sendMessage(sender, Message.NAME_SET, getPlaceholders(selectedPlayer));
                }
                messenger.sendMessage(sender, Message.NAME_SET_OTHER, getPlaceholders(selectedPlayer));
                return true;
            }
            if(isNickTooLong(args[0])) {
                messenger.sendMessage(sender, Message.NICK_TOO_LONG);
                return true;
            }
            if(isDuplicateNickname(args[0], selectedPlayer)) {
                messenger.sendMessage(sender, Message.USERNAME_NICK_PROHIBITED);
                return true;
            }

            if(!sender.hasPermission("namecolor.nick.color") && !args[0].equals(stripColor(args[0]))) {
                nameUtilities.setDisplayName(stripColor(args[0]), Bukkit.getPlayer(args[1]), true);
                if(!sender.equals(selectedPlayer)) {
                    messenger.sendMessage(sender, Message.NO_NICK_COLOR_OTHER, getPlaceholders(selectedPlayer));
                    if(notifyChange) {
                        messenger.sendMessage(sender, Message.NAME_SET, getPlaceholders(selectedPlayer));
                    }
                    return true;
                }
                if(notifyChange) {
                    messenger.sendMessage(selectedPlayer, Message.NO_NICK_COLOR, getPlaceholders(selectedPlayer));
                }
                return true;
            }
            nameUtilities.setDisplayName(args[0], Bukkit.getPlayer(args[1]), true);
            if(!sender.equals(selectedPlayer)) {
                messenger.sendMessage(selectedPlayer, Message.NAME_SET_OTHER, getPlaceholders(selectedPlayer));
            }
            if(notifyChange) {
                messenger.sendMessage(selectedPlayer, Message.NAME_SET, getPlaceholders(selectedPlayer));
            }
        } else {
            messenger.sendMessage(sender, Message.INVALID_PLAYER);
        }
        return true;
    }

    public boolean isDuplicateNickname(@NotNull String name, @NotNull Player player) {
        String strippedNick = stripColor(name);
        if(strippedNick.equals(player.getName()) || namecolor.allowUsernameNicknames()) {
            return false;
        }

        for(OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
            if (offlinePlayer.getName() != null && (offlinePlayer.getName().equalsIgnoreCase(name))) {
                return true;
            }
        }

        for(Player p : Bukkit.getOnlinePlayers()) {
            if (p.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }

        return false;
    }

    public boolean isBlacklistedNickname(@NotNull String name) {
        name = PATTERN.matcher(name).replaceAll("");
        name = ChatColor.translateAlternateColorCodes('&',name);
        name = ChatColor.stripColor(name);
        name = name.toLowerCase();
        return nameUtilities.getBlacklist().contains(name);
    }

    public boolean isNickTooLong(@NotNull String name) {
        name = PATTERN.matcher(name).replaceAll("");
        name = ChatColor.translateAlternateColorCodes('&',name);
        name = ChatColor.stripColor(name);
        return name.length() > namecolor.nickLengthLimit();

    }

    public String stripColor(String str) {
        String nickWithNoColor = PATTERN.matcher(str).replaceAll("");
        nickWithNoColor = ChatColor.translateAlternateColorCodes('&', nickWithNoColor);
        nickWithNoColor = ChatColor.stripColor(nickWithNoColor);
        return nickWithNoColor;
    }

    private Map<String, String> getPlaceholders(Player player) {
        return Map.of("%display-name%&", player.getDisplayName());
    }
}
