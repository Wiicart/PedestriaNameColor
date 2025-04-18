package com.pedestriamc.namecolor.listeners;

import com.pedestriamc.namecolor.NameColor;
import com.pedestriamc.namecolor.user.UserUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

public class LeaveListener implements Listener {

    private final UserUtil userUtil;

    public LeaveListener(@NotNull NameColor nameColor) {
        userUtil = nameColor.getUserUtil();
    }

    @EventHandler
    public void onPlayerLeave(@NotNull PlayerQuitEvent event) {
        userUtil.removeUser(event.getPlayer().getUniqueId());
    }
}
