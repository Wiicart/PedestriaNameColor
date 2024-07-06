package com.pedestriamc.namecolor;

import com.earth2me.essentials.Essentials;
import com.pedestriamc.namecolor.commands.NameColorCommand;
import com.pedestriamc.namecolor.commands.NicknameCommand;
import com.pedestriamc.namecolor.commands.WhoIsCommand;
import com.pedestriamc.namecolor.gui.GUIListener;
import com.pedestriamc.namecolor.gui.GUIManager;
import com.pedestriamc.namecolor.listeners.JoinListener;
import com.pedestriamc.namecolor.listeners.LeaveListener;
import com.pedestriamc.namecolor.nms.PlayerNameTagManager;
import com.pedestriamc.namecolor.nms.Version_1_20_6;
import com.pedestriamc.namecolor.nms.Version_1_21;
import com.pedestriamc.namecolor.tabcompleters.NameColorCommandTabCompleter;
import com.pedestriamc.namecolor.tabcompleters.NicknameTabCompleter;
import com.pedestriamc.namecolor.tabcompleters.WhoIsTabCompleter;
import com.tchristofferson.configupdater.ConfigUpdater;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public final class NameColor extends JavaPlugin {

    private final FileConfiguration config = getConfig();
    private static NameColor instance;
    private File playersFile;
    private FileConfiguration playersConfig;
    private String mode;
    private boolean notify;
    private String defaultColor;
    private PlayerNameTagManager overHeadNameTagManager;
    private boolean setOverHeadNames = true;
    private boolean allowUserNick = false;
    private int maxNicknameLength = 0;
    private final String pluginVersion = "1.5";
    private final String distributor = "spigot";
    /*
    !! UPDATE VERSION NUMBER WITH EACH UPDATE !!
     */
    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        saveDefaultConfig();
        setupPlayersFile();
        getModeFromConfig();
        configSetup();
        //setOverHeadName();
        NameUtilities.initialize();
        int pluginId = 22112;
        Metrics metrics = new Metrics(this, pluginId);
        metrics.addCustomChart(new SimplePie("mode", this::getMode));
        metrics.addCustomChart(new SimplePie("distributor", this::getDistributor));
        registerClasses();
        Messenger.initialize();
        Bukkit.getLogger().info("[NameColor] NameColor version " + pluginVersion + " enabled.");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        this.savePlayersConfig();
        Bukkit.getLogger().info("[NameColor] Disabled.");
    }
    /*
    Private Methods
     */
    //Determines server version, and assigns appropriate class for version
    private void setOverHeadName(){
        switch (Bukkit.getVersion()) {
            case "1.21" -> overHeadNameTagManager = new Version_1_21();
            case "1.20.6" -> overHeadNameTagManager = new Version_1_20_6();
            default -> {
                Bukkit.getLogger().info("[NameColor] NameColor overhead-names are not supported on this Minecraft version!");
                Bukkit.getLogger().info("[NameColor] Check for updates that support your Minecraft version!");
                Bukkit.getLogger().info("[NameColor] Enabling without overhead-name support.");
                setOverHeadNames = false;
            }
        }
    }
    //Registers commands and listeners
    private void registerClasses(){
        GUIManager guiManager = new GUIManager();
        GUIListener listener = new GUIListener(guiManager);
        Bukkit.getPluginManager().registerEvents(listener,this);

        try{
            this.getCommand("namecolor").setExecutor(new NameColorCommand());
            this.getCommand("nick").setExecutor(new NicknameCommand());
            this.getCommand("nickname").setExecutor(new NicknameCommand());
            this.getCommand("whois").setExecutor(new WhoIsCommand());
            this.getCommand("namecolor").setTabCompleter(new NameColorCommandTabCompleter());
            this.getCommand("nick").setTabCompleter(new NicknameTabCompleter());
            this.getCommand("nickname").setTabCompleter(new NicknameTabCompleter());
            this.getCommand("whois").setTabCompleter(new WhoIsTabCompleter());
        }catch(NullPointerException a){
            Bukkit.getLogger().info("[NameColor] Unable to register commands!");
        }
        getServer().getPluginManager().registerEvents(new JoinListener(), this);
        getServer().getPluginManager().registerEvents(new LeaveListener(), this);
    }
    //Sets up players.yml file, which is used to store name colors and nicknames
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
    //Gets mode from config file, consider if option set in config is viable
    private void getModeFromConfig(){
        Essentials essentials;
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
    // Updates configuration, and gets any necessary config values
    private void configSetup(){
        //Config updater using https://github.com/tchristofferson/Config-Updater
        File configFile = new File(getDataFolder(), "config.yml");
        if(configFile.exists()){
            Bukkit.getLogger().info("[NameColor] Configuration found.");
            try{
                ConfigUpdater.update(this,"config.yml", configFile);
            }catch(IOException e){
                Bukkit.getLogger().info("[NameColor] Updating config failed.");
                e.printStackTrace();
            }
        }
        //Default color
        if(config.getString("default-color") != null){
            defaultColor = config.getString("default-color");
        }else {
            defaultColor = "&f";
        }
        //Nickname length limit, defaults to 16 if null
        maxNicknameLength = config.getInt("max-nickname-length");
        if(maxNicknameLength == 0){
            maxNicknameLength = 16;
        }
        //Allow nicknames that are the same as another player's username
        allowUserNick = config.getBoolean("allow-username-nicknames");
        notify = config.getBoolean("notify-players", true);
    }
    /*
    Object getter methods
     */
    //returns plugin instance
    public static NameColor getInstance(){
        return instance;
    }
    //returns PlayerNameTag class for version
    public PlayerNameTagManager getOverheadName() { return overHeadNameTagManager; }
    //Returns config file
    public FileConfiguration getConfigFile(){
        return config;
    }
    // returns player config file
    public FileConfiguration getPlayersConfig() { return playersConfig; }
    /*
    Variable getter methods
     */
    //returns plugin prefix from config, color codes are not processed
    public String getPrefix(){
        return config.getString("prefix");
    }
    //returns display name setting mode, will be either "server" or "essentials"
    public String getMode(){
        return mode;
    }
    //returns plugin distributor, used for bStats
    public String getDistributor() { return distributor; }
    //returns if nicknames that are the same as another player's username are allowed
    public boolean allowUsernameNicknames(){ return allowUserNick; }
    //returns if players should be notified that their display name has changed
    public boolean notifyChange(){
        return notify;
    }
    //returns String default color, color codes not processed.
    public String getDefaultColor(){
        return defaultColor;
    }
    //returns if class is assigned to set player name tags
    public boolean isSetOverHeadNames(){
        return setOverHeadNames;
    }
    //returns int nickname length limit
    public int nickLengthLimit(){
        return maxNicknameLength;
    }
    //returns plugin version
    public String getPluginVersion(){ return pluginVersion; }
    /*
    Public config methods
     */
    //saves players.yml file
    public void savePlayersConfig() {
        try {
            playersConfig.save(playersFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
