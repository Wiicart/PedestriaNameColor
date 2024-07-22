package com.pedestriamc.namecolor;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class NameUtilities {
    /*
    Class to actual update names, and optionally save them.
    both setColor methods are not used any more by NameColorCommand.java and NicknameCommand.java, though
    JoinListener.java may still use the methods.
    Includes a hashmap of all online players and their display names
     */
    private static boolean useEssentials = false;
    private static Essentials essentials;
    private static Pattern pattern;
    private static ArrayList<String> blacklist;
    private static HashMap<String, Team> teamMap;
    private static BidiMap<Player, String> playerDisplayNames; //https://stackoverflow.com/questions/5415056/bidimap-synchronization
    public static void initialize(){
        if(NameColor.getInstance().getMode().equals("essentials")){
            useEssentials = true;
            essentials = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
        }
        pattern = Pattern.compile("&#[a-fA-F0-9]{6}", Pattern.CASE_INSENSITIVE);
        playerDisplayNames = new DualHashBidiMap<>();
        teamMap = new HashMap<>();
        FileConfiguration blacklistConfig = NameColor.getInstance().getBlacklistFileConfig();
        blacklist = new ArrayList<>();
        List<?> tempList = blacklistConfig.getList("blacklist");
        if(tempList != null){
            for(Object obj : tempList){
                if(obj instanceof String){
                    blacklist.add(((String) obj).toLowerCase());
                }
            }
        }
    }
    //ChatColor mode (only used by GUI)
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
    //RGB mode, old
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
    public static void setNick(String nick, Player player, boolean save, boolean isNameColor){
        Matcher matcher = pattern.matcher(nick);//processing RGB code
        while(matcher.find()){ //https://stackoverflow.com/questions/15130309/how-to-use-regex-in-string-contains-method-in-java
            //https://stackoverflow.com/questions/237061/using-regular-expressions-to-extract-a-value-in-java
            String hexColor = matcher.group().substring(1).toUpperCase();
            ChatColor color = ChatColor.of(new Color(Integer.parseInt(hexColor.substring(1), 16)));
            //replace occurrence of RGB code to ChatColor
            nick = nick.replace(matcher.group(), color.toString());
        }
        //Ensure that name color codes don't extend beyond name
        nick += "&r";
        nick = ChatColor.translateAlternateColorCodes('&', nick);

        //Setting the proper display names
        player.setDisplayName(ChatColor.translateAlternateColorCodes('&', nick));
        //Additional step if using Essentials API
        if(useEssentials){
            essentials.getUser(player.getUniqueId()).setNickname(nick);
        }
        if(isNameColor){
            nick = nick.replace(player.getName(),"");
            updateNameTag(player, nick);
        }
        //Saves player
        if(save){
            //Saving as new StoredPlayer
            StoredPlayers.saveStoredPlayer(new StoredPlayer(player, nick,true));
        }
        addPlayer(player);
    }

    public static void updateNameTag(Player player, String color) {
        Bukkit.getLogger().info("updateNameTag called for player: " + player.getName() + " with color: " + color);
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        if (scoreboard == null) {
            Bukkit.getLogger().severe("Failed to get the main scoreboard.");
            return;
        }
        Team team = scoreboard.getTeam("colorBoard_" + player.getName());
        if (team == null) {
            team = scoreboard.registerNewTeam("colorBoard_" + player.getName());
            Bukkit.getLogger().info("Created new team: " + team.getName());
        } else {
            team.removeEntry(player.getName());
            Bukkit.getLogger().info("Removed previous entry from team: " + team.getName());
        }

        team.addEntry(player.getName());
        team.setPrefix(color);
        Bukkit.getLogger().info("Added player to team with prefix: " + team.getPrefix());
        teamMap.put(team.getName(), team);
        Bukkit.getLogger().info("updateNameTag completed for player: " + player.getName());
    }



    public static ArrayList<String> getBlacklist(){
        return new ArrayList<>(blacklist);
    }

    //Display name hashmap getter, setter methods
    //Data stored with Player object first, then displayName String

    @Nullable
    //Gets a player's username from the hashmap
    public static String getPlayer(String displayName){
        if(!playerDisplayNames.containsValue(displayName.toUpperCase())){
            return null;
        }
        return playerDisplayNames.getKey(displayName.toUpperCase()).getName();
    }
    //Adds a username and display name to the hash map
    public static void addPlayer(Player player){
        removePlayer(player);
        playerDisplayNames.put(player, ChatColor.stripColor(player.getDisplayName()).toUpperCase());
    }
    //Removes player and username from the hash map
    public static void removePlayer(Player player){
        playerDisplayNames.remove(player);
    }
}


