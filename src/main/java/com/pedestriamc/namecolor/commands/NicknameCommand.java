ckage com.pedestriamc.namecolor.commands;

import com.pedestriamc.namecolor.Message;
import com.pedestriamc.namecolor.Messenger;
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

import java.util.regex.Pattern;

public class NicknameCommand implements CommandExecutor {

    private final boolean notifyChange = NameColor.getInstance().notifyChange();
    private final Pattern pattern = Pattern.compile("&#[a-fA-F0-9]{6}", Pattern.CASE_INSENSITIVE);

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        Player selectedPlayer;
        String nick;
        //Check for enough args
        if(args.length == 0){
            Messenger.sendMessage(sender, Message.INSUFFICIENT_ARGS);
            return true;
        }
        //Check for too many/invalid args
        if(args.length > 2){
            Messenger.sendMessage(sender, Message.INVALID_ARGS_NICK);
            return true;
        }
        //Help message
        if (args[0].equalsIgnoreCase("help")) {
            Messenger.sendMessage(sender, Message.NICKNAME_HELP);
            return true;
        }
        if(args.length == 1){
            if(!sender.hasPermission("namecolor.nick") && !sender.hasPermission("namecolor.*") && !sender.hasPermission("namecolor.nick.*")){
                Messenger.sendMessage(sender, Message.NO_PERMS);
                return true;
            }
            if(sender instanceof Player){
                if (args[0].equalsIgnoreCase("reset")) {
                    NameUtilities.setNick(sender.getName(),(Player) sender,true, false);
                    if(notifyChange){
                        Messenger.processPlaceholders(sender, Message.NAME_SET, (Player) sender);
                    }
                    return true;
                }
                selectedPlayer = (Player) sender;
                nick = args[0];
                if(isNickTooLong(nick)){
                    Messenger.sendMessage(sender, Message.NICK_TOO_LONG);
                    return true;
                }
                if(!sender.hasPermission("namecolor.filter.bypass")){
                    if(isDuplicateNickname(nick, (Player) sender)){
                        Messenger.sendMessage(sender, Message.USERNAME_NICK_PROHIBITED);
                        return true;
                    }
                    if(isBlacklistedNickname(nick)){
                        Messenger.sendMessage(sender, Message.NICK_BLACKLIST);
                        return true;
                    }
                }
                NameUtilities.setNick(nick, selectedPlayer,true, false);
                if(notifyChange){
                    Messenger.processPlaceholders(selectedPlayer, Message.NAME_SET, selectedPlayer);
                }
            }else{
                //THIS MEANS SERVER SENT COMMAND WITH ONE ARG, CONFIG W/ LANG
                Messenger.sendMessage(sender, Message.INSUFFICIENT_ARGS);
            }
            return true;
        }
        //At this point, command has 2 args, meaning that it is setting another player's nickname
        if(!sender.hasPermission("namecolor.nick.others")){
            Messenger.sendMessage(sender, Message.NO_PERMS);
            return true;
        }
        selectedPlayer = Bukkit.getPlayer(args[1]);
        if(selectedPlayer != null){
            if (args[0].equalsIgnoreCase("RESET")) {
                NameUtilities.setNick(selectedPlayer.getName(), selectedPlayer,true, false);
                if(notifyChange){
                    Messenger.processPlaceholders(selectedPlayer, Message.NAME_SET, (Player) sender);
                }
                Messenger.processPlaceholders(sender, Message.NAME_SET_OTHER, (Player) sender);
                return true;
            }
            if(isNickTooLong(args[0])){
                Messenger.sendMessage(sender, Message.NICK_TOO_LONG);
                return true;
            }
            if(isDuplicateNickname(args[0], selectedPlayer)){
                Messenger.sendMessage(sender, Message.USERNAME_NICK_PROHIBITED);
                return true;
            }
            NameUtilities.setNick(args[0], Bukkit.getPlayer(args[1]), true, false);
            if(!sender.equals(selectedPlayer)){
                Messenger.processPlaceholders(sender, Message.NAME_SET_OTHER, selectedPlayer);
            }
            if(notifyChange){
                Messenger.processPlaceholders(selectedPlayer, Message.NAME_SET, selectedPlayer);
            }
        }else{
            Messenger.sendMessage(sender, Message.INVALID_PLAYER);
        }
        return true;
    }
    //Method to determine if nicknames that are the same as another player's username is allowed, and if so, determine
    //if the nickname is offending
    public boolean isDuplicateNickname(@NotNull String name, @NotNull Player player){
        String nickWithNoColor = pattern.matcher(name).replaceAll("");
        nickWithNoColor = ChatColor.translateAlternateColorCodes('&', nickWithNoColor);
        nickWithNoColor = ChatColor.stripColor(nickWithNoColor);
        if(nickWithNoColor.equals(player.getName())){
            return false;
        }
        if(NameColor.getInstance().allowUsernameNicknames()){
            return false;
        }
        for(OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()){
            if (offlinePlayer.getName().equalsIgnoreCase(name)){
                return true;
            }
        }
        for(Player p : Bukkit.getOnlinePlayers()){
            if (p.getName().equalsIgnoreCase(name)){
                return true;
            }
        }
        return false;
    }

    public boolean isBlacklistedNickname(@NotNull String name){
        name = pattern.matcher(name).replaceAll("");
        name = ChatColor.translateAlternateColorCodes('&',name);
        name = ChatColor.stripColor(name);
        name = name.toLowerCase();
        return NameUtilities.getBlacklist().contains(name);
    }

    public boolean isNickTooLong(@NotNull String name){
        name = pattern.matcher(name).replaceAll("");
        name = ChatColor.translateAlternateColorCodes('&',name);
        name = ChatColor.stripColor(name);
        return name.length() > NameColor.getInstance().nickLengthLimit();

    }
}

