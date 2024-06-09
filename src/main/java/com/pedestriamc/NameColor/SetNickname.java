package com.pedestriamc.NameColor;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class SetNickname {
    private static boolean useEssentials = false;
    private static Essentials essentials;
    public static void initialize(){
        if(NameColor.getInstance().getMode().equals("essentials")){
            useEssentials = true;
            essentials = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
        }
    }
    public static void setNick(String nick, Player player, boolean save){
        if(useEssentials){
            User user = essentials.getUser(player.getUniqueId());
            user.setNickname(nick);
        }else{
            player.setDisplayName(nick);
        }
        if(save){
            StoredPlayers.saveStoredPlayer(new StoredPlayer(player, nick,true));
        }
    }
}
