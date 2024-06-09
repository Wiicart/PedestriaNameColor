package com.pedestriamc.namecolor;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.checkerframework.checker.units.qual.N;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class StoredPlayer {

    public enum Type{
        RGB_COLOR, NICKNAME, CHAT_COLOR
    }

    private final UUID uuid;
    private final ChatColor chatColor;
    private final String color;
    private final Type type;
    private String nickname;

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
