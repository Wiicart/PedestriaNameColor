package com.pedestriamc.namecolor.listeners;

import com.pedestriamc.namecolor.NameColor;
import com.pedestriamc.namecolor.NameUtilities;
import com.pedestriamc.namecolor.User;
import com.pedestriamc.namecolor.UserUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {

    private final NameUtilities nameUtilities;
    private final UserUtil userUtil;

    public JoinListener(NameColor nameColor){
        nameUtilities = nameColor.getNameUtilities();
        userUtil = nameColor.getUserUtil();
    }

    @EventHandler
    public void onEvent(PlayerJoinEvent event){
        User player = userUtil.loadUser(event.getPlayer());
        if(player != null){
            userUtil.userMap().addUser(player);
            switch (player.getType()) {
                case NICKNAME -> nameUtilities.setDisplayName(player.getNickname(), event.getPlayer(), false);
                case RGB_COLOR -> nameUtilities.setColor(event.getPlayer(), player.getColor(), false);
                case CHAT_COLOR -> nameUtilities.setColor(event.getPlayer(), player.getChatColor(), false);
            }
        }else{
            nameUtilities.setColor(event.getPlayer(), NameColor.getInstance().getDefaultColor(), false);
        }
    }
}
