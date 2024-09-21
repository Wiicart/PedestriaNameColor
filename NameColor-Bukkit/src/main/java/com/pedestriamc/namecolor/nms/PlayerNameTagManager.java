package com.pedestriamc.namecolor.nms;

import org.bukkit.entity.Player;

public interface PlayerNameTagManager {
    //Sets over-head name to given String, using ChatColor color codes
    void setOverHeadName(Player player, String name);

    //Tells plugin to no longer modify over-head name
    void removeOverHeadName(Player player);
}
