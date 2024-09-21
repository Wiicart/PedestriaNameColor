package com.pedestriamc.namecolor.listeners;

import com.pedestriamc.namecolor.NameColor;
import com.pedestriamc.namecolor.NameUtilities;
import com.pedestriamc.namecolor.UserUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

public class LeaveListener implements Listener {

    private final NameUtilities nameUtilities;
    private final UserUtil userUtil;

    public LeaveListener(@NotNull NameColor nameColor){
        nameUtilities = nameColor.getNameUtilities();
        userUtil = nameColor.getUserUtil();
    }

    @EventHandler
    public void onPlayerLeave(@NotNull PlayerQuitEvent event){
        nameUtilities.removePlayer(event.getPlayer());
        userUtil.userMap().removeUser(event.getPlayer().getUniqueId());
    }
}
