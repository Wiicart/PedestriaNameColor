package com.pedestriamc.namecolor.user;

import com.pedestriamc.namecolor.NameColor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class YamlUserUtil extends UserUtil{

    private final NameColor nameColor;

    public YamlUserUtil(NameColor nameColor){
        super();
        this.nameColor = nameColor;
    }

    public void saveUser(User user){
        FileConfiguration config = nameColor.getPlayersConfig();
        if(user != null){
            config.set("players." + user.getUuid() + ".color", null);
            config.set("players." + user.getUuid() + ".nick", null);
            switch (user.getType()) {
                case RGB_COLOR -> config.set("players." + user.getUuid() + ".color", user.getColor());
                case CHAT_COLOR -> config.set("players." + user.getUuid() + ".color", user.getChatColor().toString());
                case NICKNAME -> config.set("players." + user.getUuid() + ".nick", user.getNickname());
            }
            nameColor.savePlayersConfig();
        }
    }

    @Nullable
    public User loadUser(Player player){
        FileConfiguration config = nameColor.getPlayersConfig();
        String playerUUID = player.getUniqueId().toString();
        String colorPath = "players." + playerUUID + ".color";
        String nickPath = "players." + playerUUID + ".nick";
        if (config.contains(colorPath)) {
            String color = config.getString(colorPath);
            if (color != null) {
                if (color.matches("^#[a-fA-F0-9]{6}$")) {
                    return new User(player, color, false);
                } else if (color.length() == 2) {
                    ChatColor chatColor = ChatColor.getByChar(color.charAt(1));
                    if (chatColor != null) {
                        return new User(player, chatColor);
                    } else {
                        Bukkit.getLogger().info("[NameColor] Invalid ChatColor code: " + color);
                    }
                } else {
                    Bukkit.getLogger().info("[NameColor] Invalid color format for player " + player.getName() + ": " + color);
                }
            }
        }
        if(config.contains(nickPath)){
            return new User(player, config.getString(nickPath), true);
        }
        return null;
    }

    @Override
    public void disable() {

    }

}