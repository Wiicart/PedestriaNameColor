package com.pedestriamc.namecolor.listeners;

import com.pedestriamc.namecolor.NameColor;
import com.pedestriamc.namecolor.NameUtilities;
import com.pedestriamc.namecolor.StoredPlayer;
import com.pedestriamc.namecolor.StoredPlayers;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        StoredPlayer player = StoredPlayers.loadStoredPlayer(event.getPlayer());
        if(player != null){
            // Determines the way the name is set, and uses the proper method to set displayName
            switch (player.getType()) {
                case NICKNAME -> NameUtilities.setNick(player.getNickname(), event.getPlayer(), false);
                case RGB_COLOR -> NameUtilities.setColor(event.getPlayer(), player.getColor(), false);
                case CHAT_COLOR -> NameUtilities.setColor(event.getPlayer(), player.getChatColor(), false);
            }
        }else{
            //Update display name to default color code from config
            NameUtilities.setColor(event.getPlayer(), NameColor.getInstance().getDefaultColor(), false);
        }
    }
}

