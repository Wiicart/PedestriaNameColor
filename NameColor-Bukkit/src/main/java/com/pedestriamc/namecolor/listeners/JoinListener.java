package com.pedestriamc.namecolor.listeners;

import com.pedestriamc.namecolor.NameColor;
import com.pedestriamc.namecolor.user.UserUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {

    private final UserUtil userUtil;

    public JoinListener(NameColor nameColor) {
        userUtil = nameColor.getUserUtil();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEvent(PlayerJoinEvent event) {
        userUtil.loadUserAsync(event.getPlayer().getUniqueId());
    }
}
