package com.pedestriamc.namecolor;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        StoredPlayer player = StoredPlayers.loadStoredPlayer(event.getPlayer());
        if(player != null){
            // Determines the way the name is set, and uses the proper method to set displayName
            switch (player.getType()){
                case NICKNAME:
                    SetName.setNick(player.getNickname(), event.getPlayer(), false);
                    break;
                case RGB_COLOR:
                    SetName.setColor(event.getPlayer(), player.getColor(), false);
                    break;
                case CHAT_COLOR:
                    SetName.setColor(event.getPlayer(), player.getChatColor(), false);
                    break;
            }
        }else{
            //Update display name to default color code from config
            SetName.setColor(event.getPlayer(), NameColor.getInstance().getDefaultColor(), false);
        }
    }
}

