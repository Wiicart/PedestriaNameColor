package com.pedestriamc.namecolor;

import com.pedestriamc.namecolor.commands.NameColorCommand;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

/*



tab color


 */
public final class NameColor extends JavaPlugin {

    FileConfiguration config = getConfig();
    private static NameColor instance;
    private File playersFile;
    private FileConfiguration playersConfig;
    String mode;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        saveDefaultConfig();
        setupPlayersFile();
        if(config.getString("mode").equalsIgnoreCase("auto")){
            if(getServer().getPluginManager().getPlugin("Essentials") != null){
                Bukkit.getLogger().info("[NameColor] Using Essentials mode");
                mode = "essentials";
            }else{
                Bukkit.getLogger().info("[NameColor] Essentials plugin not found, using Server mode");
                mode = "server";
            }
        }else if(config.getString("mode").equalsIgnoreCase("essentials")){
            if(getServer().getPluginManager().getPlugin("Essentials") != null){
                Bukkit.getLogger().info("[NameColor] Using Essentials mode");
                mode = "essentials";
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
        SetNameColor.initialize();
        SetNickname.initialize();
        int pluginId = 22112;
        Metrics metrics = new Metrics(this, pluginId);
        this.getCommand("namecolor").setExecutor(new NameColorCommand());
        getCommand("namecolor").setTabCompleter(new NameColorCommandTabCompleter());
        getServer().getPluginManager().registerEvents(new JoinListener(), this);
        Bukkit.getLogger().info("[NameColor] Enabled");

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
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
            saveResource("players.yml", false);
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
    public static void sendMessage(Player player, String[] message){

    }
}
