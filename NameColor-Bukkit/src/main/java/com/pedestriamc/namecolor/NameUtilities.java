package com.pedestriamc.namecolor;

import com.earth2me.essentials.Essentials;
import com.pedestriamc.namecolor.api.Mode;
import com.pedestriamc.namecolor.user.User;
import com.pedestriamc.namecolor.user.UserUtil;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NameUtilities {

    private static final Pattern PATTERN = Pattern.compile("&#[a-fA-F0-9]{6}", Pattern.CASE_INSENSITIVE);

    private final NameColor nameColor;
    private boolean useEssentials;
    private Essentials essentials;
    private final List<String> blacklist;
    private final BidiMap<Player, String> playerDisplayNames;
    private final UserUtil userUtil;

    public NameUtilities(NameColor nameColor) {
        this.nameColor = nameColor;
        userUtil = nameColor.getUserUtil();
        playerDisplayNames = new DualHashBidiMap<>();
        blacklist = new ArrayList<>();
        loadBlacklist();
        determineMode();
    }

    /**
     * Determines if the plugin is using Essentials
     */
    private void determineMode() {
        if(nameColor.getMode() == Mode.ESSENTIALS) {
            try {
                essentials = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
            } catch(ClassCastException e) {
                nameColor.warn("An error occurred while finding Essentials: " + e.getMessage());
            }
            useEssentials = essentials != null;
        }
    }

    /**
     * Loads the blacklist from blacklist.yml
     */
    private void loadBlacklist() {
        FileConfiguration blacklistConfig = nameColor.files().getBlacklistConfig();
        List<?> tempList = blacklistConfig.getList("blacklist");
        if(tempList == null) {
            return;
        }

        for(Object obj : tempList) {
            if(obj instanceof String str) {
                blacklist.add(str.toLowerCase());
            }
        }
    }

    /**
     * Legacy setColor method.  Now uses the setNick method.
     * @param player The player to set the name color of.
     * @param color The color to set it to.
     * @param save If the color should be saved.
     */
    public void setColor(Player player, ChatColor color, boolean save) {
        setDisplayName(color + player.getName(), player, save);
    }

    /**
     * Legacy setColor method.  Now redirects to the setNick method.
     * @param player The player to set the name color of.
     * @param color The color to set it to.
     * @param save If the color should be saved.
     */
    public void setColor(Player player, String color, boolean save) {
        if(color.charAt(0) == '#') {
            setDisplayName(ChatColor.of(color) + player.getName(), player, save);
        } else {
            setDisplayName(ChatColor.translateAlternateColorCodes('&', color) + player.getName(), player, save);
        }
    }

    /**
     * Primary method of changing display names in NameColor.
     * <a href="https://stackoverflow.com/questions/237061/using-regular-expressions-to-extract-a-value-in-java">...</a>
     * <a href="https://stackoverflow.com/questions/15130309/how-to-use-regex-in-string-contains-method-in-java">...</a>
     * @param displayName The new display name of the player.
     * @param player The player to set the display name of.
     * @param save If this new display name should be saved.
     */
    public void setDisplayName(String displayName, Player player, boolean save) {
        Matcher matcher = PATTERN.matcher(displayName);
        while(matcher.find()) {
            String hexColor = matcher.group().substring(1).toUpperCase();
            ChatColor color = ChatColor.of(new Color(Integer.parseInt(hexColor.substring(1), 16)));
            displayName = displayName.replace(matcher.group(), color.toString());
        }
        displayName += "&r";
        displayName = ChatColor.translateAlternateColorCodes('&', displayName);
        player.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
        if(useEssentials) {
            essentials.getUser(player.getUniqueId()).setNickname(displayName);
        }
        if(save) {
            userUtil.saveUser(new User(player, displayName,true));
        }
        addPlayer(player);
    }

    /**
     * Provides an array of the contents of blacklists.yml
     * @return An ArrayList containing blacklisted nicknames.
     */
    public List<String> getBlacklist() {
        return new ArrayList<>(blacklist);
    }

    public boolean isBlacklisted(String nick) {
        return blacklist.contains(nick.toLowerCase());
    }


    @Nullable
    public String getPlayerName(String displayName) {
        if(!playerDisplayNames.containsValue(displayName.toUpperCase())) {
            return null;
        }
        return playerDisplayNames.getKey(displayName.toUpperCase()).getName();
    }

    //Adds a username and display name to the hash map
    public void addPlayer(Player player) {
        removePlayer(player);
        playerDisplayNames.put(player, ChatColor.stripColor(player.getDisplayName()).toUpperCase());
    }

    //Removes player and username from the hash map
    public void removePlayer(Player player) {
        playerDisplayNames.remove(player);
    }
}
