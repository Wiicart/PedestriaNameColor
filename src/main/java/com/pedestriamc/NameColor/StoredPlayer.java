package com.pedestriamc.NameColor;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class StoredPlayer {

    public enum Type{
        RGB_COLOR, NICKNAME, CHAT_COLOR;
    }

    private UUID uuid;
    private ChatColor chatColor;
    private String color;
    private String nickname;
    private Type type;

    public StoredPlayer(Player player, ChatColor c){//ChatColor constructor
        uuid = player.getUniqueId();
        chatColor = c;
        color = null;
        nickname = null;
        type = Type.CHAT_COLOR;
    }
    public StoredPlayer(Player player, String name, boolean isNickname){//Nickname & RGB constructor
        if(isNickname){
            uuid = player.getUniqueId();
            chatColor = null;
            color = null;
            nickname = name;
            type = Type.NICKNAME;
        }else{
            uuid = player.getUniqueId();
            chatColor = null;
            color = name;
            type = Type.RGB_COLOR;
        }
    }
    public UUID getUuid(){
        return uuid;
    }
    public String getColor(){
        return color;
    }
    public ChatColor getChatColor(){
        return chatColor;
    }
    public String getNickname(){
        return nickname;
    }
    public Type getType(){
        return type;
    }
}
