package com.pedestriamc.namecolor;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public final class StoredPlayers {

    public static void saveStoredPlayer(StoredPlayer storedPlayer){
        FileConfiguration config = NameColor.getInstance().getPlayersConfig();
        if(storedPlayer != null){
            config.set("players." + storedPlayer.getUuid() + ".color", null);
            config.set("players." + storedPlayer.getUuid() + ".nick", null);
            switch(storedPlayer.getType()){
                case RGB_COLOR:
                    config.set("players." + storedPlayer.getUuid() + ".color", storedPlayer.getColor());
                    break;
                case CHAT_COLOR:
                    config.set("players." + storedPlayer.getUuid() + ".color", storedPlayer.getChatColor().toString());
                    break;
                case NICKNAME:
                    config.set("players." + storedPlayer.getUuid() + ".nick", storedPlayer.getNickname());
                    break;
            }
            NameColor.getInstance().savePlayersConfig();
        }
    }

    @Nullable
    public static StoredPlayer loadStoredPlayer(Player player){
        FileConfiguration config = NameColor.getInstance().getPlayersConfig();
        String playerUUID = player.getUniqueId().toString();
        String colorPath = "players." + playerUUID + ".color";
        String nickPath = "players." + playerUUID + ".nick";
        if (config.contains(colorPath)) {
            String color = config.getString(colorPath);
            if (color != null) {
                if (color.matches("^#[a-fA-F0-9]{6}$")) {
                    return new StoredPlayer(player, color, false);
                } else if (color.length() == 2) {
                    ChatColor chatColor = ChatColor.getByChar(color.charAt(1));
                    if (chatColor != null) {
                        return new StoredPlayer(player, chatColor);
                    } else {
                        Bukkit.getLogger().info("[NameColor] Invalid ChatColor code: " + color);
                    }
                } else {
                    Bukkit.getLogger().info("[NameColor] Invalid color format for player " + player.getName() + ": " + color);
                }
            }
        }
        if(config.contains(nickPath)){
            return new StoredPlayer(player,config.getString(nickPath),true);
        }
        return null;
    }
}
