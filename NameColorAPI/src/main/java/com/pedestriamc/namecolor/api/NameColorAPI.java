package com.pedestriamc.namecolor.api;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public interface NameColorAPI {

    /**
     * Provides the display name of any user, if NameColor has one saved.
     * Returns null if NameColor has no data stored on the player.
     * @param player The player to get the display name of.
     * @return A display name, if NameColor has one saved.
     */
    String getDisplayName(OfflinePlayer player);

    /**
     * Sets a OfflinePlayer's display name, and saves it on the server.
     * @param player The player to set the display name of.
     */
    void setDisplayName(OfflinePlayer player, String displayName);

    /**
     * Sets a Player's display name, and saves it on the server.
     * @param player The player to set the display name of.
     * @param displayName The new display name.
     */
    void setDisplayName(Player player, String displayName);

    /**
     * Provides the Player with the matching display name.
     * Ignores color codes.
     * The Player with the nickname must be online
     * @param string The display name.
     */
    Player whoIs(String string);
}
