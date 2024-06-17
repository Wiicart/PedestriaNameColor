package com.pedestriamc.namecolor.listeners;

import com.pedestriamc.namecolor.SetName;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class LeaveListener implements Listener {
    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event){
        SetName.removePlayer(event.getPlayer());
    }

}
