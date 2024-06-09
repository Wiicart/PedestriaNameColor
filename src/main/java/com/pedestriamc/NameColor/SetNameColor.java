package com.pedestriamc.NameColor;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class SetNameColor {
    private static boolean essentials = false;
    public static void initialize(){
        if(NameColor.getInstance().getMode().equals("essentials")){
            essentials = true;
        }
    }
    public static void setColor(Player player, ChatColor color, boolean save){ //ChatColor mode
        if(essentials){
            Essentials essentials = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
            User user = essentials.getUser(player.getUniqueId());
            user.setNickname(color + player.getName());
        }else{
            player.setDisplayName(color + player.getName());
        }
        if(save){
            StoredPlayers.saveStoredPlayer(new StoredPlayer(player, color));
        }
    }
    public static void setColor(Player player, String color, boolean save){ //RGB mode
        if(essentials){
            Essentials essentials = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
            User user = essentials.getUser(player.getUniqueId());
            user.setNickname(ChatColor.of(color) + player.getName());
        }else{
            player.setDisplayName(ChatColor.of(color) + player.getName());
        }
        if(save){
            StoredPlayers.saveStoredPlayer(new StoredPlayer(player, color, false));
        }

    }
}
