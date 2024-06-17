package com.pedestriamc.namecolor;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import com.pedestriamc.namecolor.commands.NameColorCommand;
import com.pedestriamc.namecolor.commands.NicknameCommand;
import com.pedestriamc.namecolor.commands.WhoIsCommand;
import com.pedestriamc.namecolor.listeners.JoinListener;
import com.pedestriamc.namecolor.listeners.LeaveListener;
import com.pedestriamc.namecolor.tabcompleters.NameColorCommandTabCompleter;
import com.pedestriamc.namecolor.tabcompleters.NicknameTabCompleter;
import com.pedestriamc.namecolor.tabcompleters.WhoIsTabCompleter;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;

public final class NameColor extends JavaPlugin {

    private FileConfiguration config = getConfig();
    private Essentials essentials;
    private static NameColor instance;
    private File playersFile;
    private FileConfiguration playersConfig;
    private String mode;
    private boolean notify;
    private String defaultColor;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        saveDefaultConfig();
        setupPlayersFile();
        if(config.getString("default-color") != null){
            defaultColor = config.getString("default-color");
        }else {
            defaultColor = "&f";
        }
        this.getModeFromConfig();
        SetName.initialize();
        int pluginId = 22112;
        Metrics metrics = new Metrics(this, pluginId);
        metrics.addCustomChart(new SimplePie("mode", () -> {
            return getMode();
        }));
        this.getCommand("namecolor").setExecutor(new NameColorCommand());
        this.getCommand("nick").setExecutor(new NicknameCommand());
        this.getCommand("nickname").setExecutor(new NicknameCommand());
        this.getCommand("whois").setExecutor(new WhoIsCommand());
        this.getCommand("namecolor").setTabCompleter(new NameColorCommandTabCompleter());
        this.getCommand("nick").setTabCompleter(new NicknameTabCompleter());
        this.getCommand("nickname").setTabCompleter(new NicknameTabCompleter());
        this.getCommand("whois").setTabCompleter(new WhoIsTabCompleter());
        getServer().getPluginManager().registerEvents(new JoinListener(), this);
        getServer().getPluginManager().registerEvents(new LeaveListener(), this);
        Bukkit.getLogger().info("[NameColor] Enabled");

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        this.savePlayersConfig();
        Bukkit.getLogger().info("NameColor disabled");
    }
    public static NameColor getInstance(){
        return instance;
    }
    public FileConfiguration getConfigFile(){
        return config;
    }
    public String getPrefix(){
        return config.getString("prefix");
    }
    public String getMode(){
        return mode;
    }
    private void setupPlayersFile() {
        playersFile = new File(getDataFolder(), "players.yml");
        if (!playersFile.exists()) {
            playersFile.getParentFile().mkdirs();
            try {
                playersFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        playersConfig = YamlConfiguration.loadConfiguration(playersFile);
    }

    public FileConfiguration getPlayersConfig() {
        return playersConfig;
    }
    public void savePlayersConfig() {
        try {
            playersConfig.save(playersFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void getModeFromConfig(){ //get mode from config file, consider if option set in config is viable
        if(config.getString("mode").equalsIgnoreCase("auto")){
            if(getServer().getPluginManager().getPlugin("Essentials") != null){
                Bukkit.getLogger().info("[NameColor] Using Essentials mode");
                mode = "essentials";
                essentials = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
            }else{
                Bukkit.getLogger().info("[NameColor] Essentials plugin not found, using Server mode");
                mode = "server";
            }
        }else if(config.getString("mode").equalsIgnoreCase("essentials")){
            if(getServer().getPluginManager().getPlugin("Essentials") != null){
                Bukkit.getLogger().info("[NameColor] Using Essentials mode");
                mode = "essentials";
                essentials = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
            }else{
                Bukkit.getLogger().info("[NameColor] Essentials plugin not found, defaulting to Server mode");
                mode = "server";
            }
        }else if(config.getString("mode").equalsIgnoreCase("server")){
            Bukkit.getLogger().info("[NameColor] Using Server mode");
            mode = "server";
        }else{
            Bukkit.getLogger().info("[NameColor] Invalid mode in config.yml, defaulting to Server mode");
            mode = "server";
        }
    }
    @Nullable
    public String processPlaceholders(Player player) { //processes display name placeholder for message sent to player that had display name changed
        String msg = config.getString("name-set");
        if (msg != null) {
            if (msg.contains("%display-name%")) {
                msg = msg.replace("%display-name%", player.getDisplayName());
            }
            return ChatColor.translateAlternateColorCodes('&', msg);
        }
        return null;
    }
    @Nullable
    public String processSenderPlaceholders(Player player){ //processes placeholders for sender
        String msg = config.getString("name-set-other");
        if(msg != null){
            if(msg.contains("%display-name%")){
                if(this.getMode().equals("essentials")){
                    User user = essentials.getUser(player.getUniqueId());
                    msg = msg.replace("%display-name%", user.getDisplayName());
                }else{
                    msg = msg.replace("%display-name%", player.getDisplayName());
                }
            }
            if(msg.contains("%username%")){
                if(this.getMode().equals("essentials")){
                    User user = essentials.getUser(player.getUniqueId());
                    msg = msg.replace("%username%", user.getDisplayName());
                }else{
                    msg = msg.replace("%username%", player.getName());
                }
            }
            return ChatColor.translateAlternateColorCodes('&', msg);
        }
        return null;
    }
    public boolean notifyChange(){
        return notify;
    }
    public String getDefaultColor(){
        return defaultColor;
    }
}
