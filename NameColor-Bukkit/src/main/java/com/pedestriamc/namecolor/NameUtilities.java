package com.pedestriamc.namecolor;

import com.earth2me.essentials.Essentials;
import com.pedestriamc.namecolor.api.Mode;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NameUtilities {

    public static final Pattern SPIGOT_HEX = Pattern.compile("&#[a-fA-F0-9]{6}", Pattern.CASE_INSENSITIVE);

    private final NameColor nameColor;
    private boolean usingEssentials;
    private Essentials essentials;
    private final List<String> blacklist;

    public NameUtilities(NameColor nameColor) {
        this.nameColor = nameColor;
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
            usingEssentials = essentials != null;
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
     * Legacy setColor method. Redirects to setDisplayName
     * @param player The player to set the name color of.
     * @param color The color to set it to.
     */
    @SuppressWarnings("unused")
    public void setColor(Player player, ChatColor color) {
        setDisplayName(color + player.getName(), player);
    }

    /**
     * Legacy setColor method.  Now redirects to the setNick method.
     * @param player The player to set the name color of.
     * @param color The color to set it to.
     * @param save If the color should be saved.
     */
    @SuppressWarnings("unused")
    public void setColor(Player player, String color, boolean save) {
        if(color.charAt(0) == '#') {
            setDisplayName(ChatColor.of(color) + player.getName(), player);
        } else {
            setDisplayName(ChatColor.translateAlternateColorCodes('&', color) + player.getName(), player);
        }
    }

    /**
     * Primary method of changing display names in NameColor.
     * <a href="https://stackoverflow.com/questions/237061/using-regular-expressions-to-extract-a-value-in-java">...</a>
     * <a href="https://stackoverflow.com/questions/15130309/how-to-use-regex-in-string-contains-method-in-java">...</a>
     * @param displayName The new display name of the player.
     * @param player The player to set the display name of.
     */
    public void setDisplayName(String displayName, Player player) {
        Matcher matcher = SPIGOT_HEX.matcher(displayName);
        while(matcher.find()) {
            String hexColor = matcher.group().substring(1).toUpperCase();
            ChatColor color = ChatColor.of(new Color(Integer.parseInt(hexColor.substring(1), 16)));
            displayName = displayName.replace(matcher.group(), color.toString());
        }

        displayName += "&r";
        displayName = ChatColor.translateAlternateColorCodes('&', displayName);
        player.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
        if(usingEssentials) {
            essentials.getUser(player.getUniqueId()).setNickname(displayName);
        }
    }

    public boolean isBlacklisted(String nick) {
        return blacklist.contains(nick.toLowerCase());
    }

    public static String stripColor(String str) {
        String stripped = SPIGOT_HEX.matcher(str).replaceAll("");
        stripped = org.bukkit.ChatColor.translateAlternateColorCodes('&', stripped);
        stripped = org.bukkit.ChatColor.stripColor(stripped);
        return stripped;
    }

}
