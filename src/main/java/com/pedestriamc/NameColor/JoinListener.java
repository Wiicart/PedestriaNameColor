package com.pedestriamc.NameColor;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        StoredPlayer player = StoredPlayers.loadStoredPlayer(event.getPlayer());
        if(player != null){
            if(player.getType() == StoredPlayer.Type.CHAT_COLOR){
                SetNameColor.setColor(event.getPlayer(), player.getChatColor(), false);
            }else if(player.getType() == StoredPlayer.Type.RGB_COLOR){
                SetNameColor.setColor(event.getPlayer(), player.getColor(), false);
            }else if(player.getType() == StoredPlayer.Type.NICKNAME){
                SetNickname.setNick(player.getNickname(), event.getPlayer(), false);
            }
        }
    }
}
