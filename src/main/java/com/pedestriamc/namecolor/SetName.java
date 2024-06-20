package com.pedestriamc.namecolor;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class SetName {
    /*
    Class to actual update names, and optionally save them.
    both setColor methods are not used anymore by NameColorCommand.java and NicknameCommand.java, though
    JoinListener.java may still use the methods.
     */
    private static boolean useEssentials = false;
    private static Essentials essentials;
    private static Pattern pattern;
    private static BidiMap<String, String> playerDisplayNames; //https://stackoverflow.com/questions/5415056/bidimap-synchronization
    public static void initialize(){
        if(NameColor.getInstance().getMode().equals("essentials")){
            useEssentials = true;
            essentials = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
        }
        pattern = Pattern.compile("&#[a-fA-F0-9]{6}", Pattern.CASE_INSENSITIVE);
        playerDisplayNames = new DualHashBidiMap<String, String>();
    }
    //ChatColor mode (old)
    public static void setColor(Player player, ChatColor color, boolean save){
        if(useEssentials){
            try{
                User user = essentials.getUser(player.getUniqueId());
                StoredPlayers.saveStoredPlayer(new StoredPlayer(player, color));
                player.setDisplayName(color + player.getName());
                user.setNickname(color + player.getName());
            }catch(NullPointerException a){
                Bukkit.getLogger().info("[NameColor] Essentials unable to find user.");
            }
        }else{
            player.setDisplayName(color + player.getName());
        }
        if(save){
            StoredPlayers.saveStoredPlayer(new StoredPlayer(player, color));
        }
        addPlayer(player);
    }
    //RGB mode (old)
    public static void setColor(Player player, String color, boolean save){
        if(color.charAt(0) == '#'){
            if(useEssentials){
                try{
                    User user = essentials.getUser(player.getUniqueId());
                    player.setDisplayName(ChatColor.of(color) + player.getName());
                    user.setNickname(ChatColor.of(color) + player.getName());
                }catch(NullPointerException a){
                    Bukkit.getLogger().info("[NameColor] Essentials unable to find user.");
                }
            }else{
                player.setDisplayName(ChatColor.of(color) + player.getName());
                player.setPlayerListName(ChatColor.of(color) + player.getName());
            }
            if(save){
                StoredPlayers.saveStoredPlayer(new StoredPlayer(player, color, false));
            }
        }else{
            if(useEssentials){
                try{
                    User user = essentials.getUser(player.getUniqueId());
                    user.setNickname(ChatColor.translateAlternateColorCodes('&',color) + player.getName());
                }catch(NullPointerException a){
                    Bukkit.getLogger().info("[NameColor] Essentials unable to find user.");
                }
            }else{
                player.setDisplayName(ChatColor.translateAlternateColorCodes('&',color) + player.getName());
            }
        }
        addPlayer(player);
    }
    //SetNick method used by both NameColorCommand.java and NicknameCommand.java
    public static void setNick(String nick, Player player, boolean save){
        Matcher matcher = pattern.matcher(nick);
        while(matcher.find()){ //https://stackoverflow.com/questions/15130309/how-to-use-regex-in-string-contains-method-in-java
            //https://stackoverflow.com/questions/237061/using-regular-expressions-to-extract-a-value-in-java
            String hexColor = matcher.group().substring(1).toUpperCase();
            ChatColor color = ChatColor.of(new Color(Integer.parseInt(hexColor.substring(1), 16)));
            nick = nick.replace(matcher.group(), color.toString());
        }
        nick += "&f&r";
        nick = ChatColor.translateAlternateColorCodes('&', nick);
        if(useEssentials){
            //nick = ChatColor.translateAlternateColorCodes('&', nick);
            User user = essentials.getUser(player.getUniqueId());
            player.setDisplayName(ChatColor.translateAlternateColorCodes('&', nick));
            user.setNickname(nick);

        }else{
            player.setDisplayName(ChatColor.translateAlternateColorCodes('&', nick));
            //player.setPlayerListName();
        }
        if(save){
            StoredPlayers.saveStoredPlayer(new StoredPlayer(player, nick,true));
            addPlayer(player);
        }
    }
    //Display name hashmap getter, setter methods
    /*
    fix
     */
    @Nullable
    public static String getPlayer(String displayName){
        return playerDisplayNames.getKey(displayName.toUpperCase());
    }
    public static void addPlayer(Player player){
        removePlayer(player);
        playerDisplayNames.put(player.getName().toUpperCase(), ChatColor.stripColor(player.getDisplayName()).toUpperCase());
    }
    public static void removePlayer(Player player){
        playerDisplayNames.remove(player.getName().toUpperCase());
    }
}


