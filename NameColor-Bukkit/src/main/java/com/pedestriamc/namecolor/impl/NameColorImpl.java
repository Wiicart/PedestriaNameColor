package com.pedestriamc.namecolor.impl;

import com.pedestriamc.namecolor.api.NameColorAPI;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class NameColorImpl implements NameColorAPI {

    @Override
    public String getDisplayName(OfflinePlayer player) {
        return null;
    }

    @Override
    public void setDisplayName(OfflinePlayer player, String displayName) {

    }

    @Override
    public void setDisplayName(Player player, String displayName) {

    }

    @Override
    public Player whoIs(String string) {
        return null;
    }

}