package com.pedestriamc.namecolor;

import com.earth2me.essentials.Essentials;
import com.pedestriamc.namecolor.user.User;
import com.pedestriamc.namecolor.user.UserUtil;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NameUtilities {
    private boolean useEssentials = false;
    private Essentials essentials;
    private final Pattern pattern;
    private final ArrayList<String> blacklist;
    private final BidiMap<Player, String> playerDisplayNames;
    private final UserUtil userUtil;

    public NameUtilities(NameColor nameColor){
        if(nameColor.getMode().equals("essentials")){
            useEssentials = true;
            essentials = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
        }
        pattern = Pattern.compile("&#[a-fA-F0-9]{6}", Pattern.CASE_INSENSITIVE);
        playerDisplayNames = new DualHashBidiMap<>();
        FileConfiguration blacklistConfig = nameColor.getBlacklistFileConfig();
        blacklist = new ArrayList<>();
        userUtil = nameColor.getUserUtil();
        List<?> tempList = blacklistConfig.getList("blacklist");
        if(tempList == null){
            return;
        }
        for(Object obj : tempList){
            if(obj instanceof String){
                blacklist.add(((String) obj).toLowerCase());
            }
        }
    }

    /**
     * Legacy setColor method.  Now uses the setNick method.
     * @param player The player to set the name color of.
     * @param color The color to set it to.
     * @param save If the color should be saved.
     */
    public void setColor(Player player, ChatColor color, boolean save){
        setDisplayName(color + player.getName(), player, save);
    }

    /**
     * Legacy setColor method.  Now redirects to the setNick method.
     * @param player The player to set the name color of.
     * @param color The color to set it to.
     * @param save If the color should be saved.
     */
    public void setColor(Player player, String color, boolean save){
        if(color.charAt(0) == '#'){
            setDisplayName(ChatColor.of(color) + player.getName(), player, save);
        }else{
            setDisplayName(ChatColor.translateAlternateColorCodes('&', color) + player.getName(), player, save);
        }
    }

    /**
     * Primary method of changing display names in NameColor.
     * https://stackoverflow.com/questions/237061/using-regular-expressions-to-extract-a-value-in-java
     * https://stackoverflow.com/questions/15130309/how-to-use-regex-in-string-contains-method-in-java
     * @param displayName The new display name of the player.
     * @param player The player to set the display name of.
     * @param save If this new display name should be saved.
     */
    public void setDisplayName(String displayName, Player player, boolean save){
        Matcher matcher = pattern.matcher(displayName);
        while(matcher.find()){
            String hexColor = matcher.group().substring(1).toUpperCase();
            ChatColor color = ChatColor.of(new Color(Integer.parseInt(hexColor.substring(1), 16)));
            displayName = displayName.replace(matcher.group(), color.toString());
        }
        displayName += "&r";
        displayName = ChatColor.translateAlternateColorCodes('&', displayName);
        player.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
        if(useEssentials){
            essentials.getUser(player.getUniqueId()).setNickname(displayName);
        }
        if(save){
            userUtil.saveUser(new User(player, displayName,true));
        }
        addPlayer(player);
    }

    /**
     * Provides an array of the contents of blacklists.yml
     * @return An ArrayList containing blacklisted nicknames.
     */
    public ArrayList<String> getBlacklist(){
        return new ArrayList<>(blacklist);
    }


    @Nullable
    public String getPlayer(String displayName){
        if(!playerDisplayNames.containsValue(displayName.toUpperCase())){
            return null;
        }
        return playerDisplayNames.getKey(displayName.toUpperCase()).getName();
    }
    //Adds a username and display name to the hash map
    public void addPlayer(Player player){
        removePlayer(player);
        playerDisplayNames.put(player, ChatColor.stripColor(player.getDisplayName()).toUpperCase());
    }
    //Removes player and username from the hash map
    public void removePlayer(Player player){
        playerDisplayNames.remove(player);
    }
}
