package com.pedestriamc.namecolor;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public final class User {

    public enum Type{
        RGB_COLOR, NICKNAME, CHAT_COLOR
    }

    private final UUID uuid;
    private final ChatColor chatColor;
    private final String color;
    private final Type type;
    private String nickname;

    public User(@NotNull Player player, ChatColor c){
        uuid = player.getUniqueId();
        chatColor = c;
        color = null;
        nickname = null;
        type = Type.CHAT_COLOR;
    }
    public User(@NotNull Player player, String name, boolean isNickname){
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
            nickname = null;
        }
    }

    public UUID getUuid(){
        return uuid;
    }
    public Type getType(){
        return type;
    }
    @Nullable public String getColor(){
        return color;
    }
    @Nullable public ChatColor getChatColor(){
        return chatColor;
    }
    @Nullable public String getNickname(){
        return nickname;
    }
    public void setNickname(String nickname){ this.nickname = nickname; }
}
