package com.pedestriamc.namecolor;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SetNickname {
    private static boolean useEssentials = false;
    private static Essentials essentials;
    private static Pattern pattern;
    public static void initialize(){
        if(NameColor.getInstance().getMode().equals("essentials")){
            useEssentials = true;
            essentials = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
        }
        pattern = Pattern.compile("^&#[a-fA-F0-9]{6}$");
    }
    public static void setNick(String nick, Player player, boolean save){
        Bukkit.getLogger().info("Pre processing: " + nick);
        int i = 1;
        Matcher matcher = pattern.matcher(nick);
        while(matcher.find()){ //https://stackoverflow.com/questions/15130309/how-to-use-regex-in-string-contains-method-in-java
            nick = nick.replace(matcher.group(i), ChatColor.of(matcher.group(i)).toString()); //https://stackoverflow.com/questions/237061/using-regular-expressions-to-extract-a-value-in-java
            i++;
        }
        Bukkit.getLogger().info("Post processing: " + nick);
        if(useEssentials){
            nick = ChatColor.translateAlternateColorCodes('&', nick);
            User user = essentials.getUser(player.getUniqueId());
            user.setNickname(nick);
        }else{
            player.setDisplayName(ChatColor.translateAlternateColorCodes('§', nick));
        }
        if(save){
            StoredPlayers.saveStoredPlayer(new StoredPlayer(player, nick,true));
        }
    }
}