package com.pedestriamc.namecolor;

import com.earth2me.essentials.Essentials;
import com.pedestriamc.namecolor.commands.WhoIsCommand;
import com.pedestriamc.namecolor.nms.PlayerNameTagManager;
import com.pedestriamc.namecolor.commands.NameColorCommand;
import com.pedestriamc.namecolor.commands.NicknameCommand;
import com.pedestriamc.namecolor.gui.GUIListener;
import com.pedestriamc.namecolor.gui.GUIManager;
import com.pedestriamc.namecolor.listeners.JoinListener;
import com.pedestriamc.namecolor.listeners.LeaveListener;
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

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public final class NameColor extends JavaPlugin {

    private final FileConfiguration config = getConfig();
    private static NameColor instance;
    private File playersFile;
    private FileConfiguration playersConfig;
    File blacklistFile;
    FileConfiguration blacklistFileConfig;
    private String mode;
    private boolean notify;
    private String defaultColor;
    private PlayerNameTagManager overHeadNameTagManager;
    private boolean setOverHeadNames = true;
    private boolean allowUserNick = false;
    private int maxNicknameLength = 0;
    private final String pluginVersion = "1.7";
    private final short pluginNum = 7;
    private final String distributor = "hangar";
    private boolean modifyNameTags;
    private NameUtilities nameUtilities;
    private UserUtil userUtil;
    /*
    !! UPDATE VERSION NUMBER WITH EACH UPDATE !!
     */
    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        saveDefaultConfig();
        setupPlayersFile();
        setupBlacklistFile();
        getModeFromConfig();
        configSetup();
        nameUtilities = new NameUtilities(this);
        userUtil = new UserUtil(this);
        //setOverHeadName();
        int pluginId = 22112;
        Metrics metrics = new Metrics(this, pluginId);
        metrics.addCustomChart(new SimplePie("mode", this::getMode));
        metrics.addCustomChart(new SimplePie("distributor", this::getDistributor));
        registerClasses();
        Messenger.initialize();
        checkUpdate();
        //loadProtocol();
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

    /*private void loadProtocol(){
        if(getServer().getPluginManager().getPlugin("ProtocolLib") == null){
            Bukkit.getLogger().info("[NameColor] ProtocolLib unavailable, player name tags will not be modified.");
            modifyNameTags = false;
            return;
        }
        protocolManager = ProtocolLibrary.getProtocolManager();
        protocolManager.addPacketListener(new PacketAdapter(this, PacketType.Play.Server.PLAYER_INFO) {
            @Override
            public void onPacketSending(PacketEvent e){
                e.setReadOnly(false);
                PacketContainer container = e.getPacket();
                WrappedGameProfile profile = WrappedGameProfile.fromPlayer(e.getPlayer());
                profile = profile.withName(e.getPlayer().getDisplayName() + "NC");
                container.getGameProfiles().writeSafely(0, profile);
                container.getStrings().writeSafely(0,"ng");
            }
        });
    }*/

    //Registers commands and listeners
    private void registerClasses(){
        GUIManager guiManager = new GUIManager();
        GUIListener listener = new GUIListener(guiManager);
        Bukkit.getPluginManager().registerEvents(listener,this);

        try{
            this.getCommand("namecolor").setExecutor(new NameColorCommand(this));
            NicknameCommand nicknameCommand = new NicknameCommand(this);
            this.getCommand("nick").setExecutor(nicknameCommand);
            this.getCommand("nickname").setExecutor(nicknameCommand);
            this.getCommand("whois").setExecutor(new WhoIsCommand(this));
            this.getCommand("namecolor").setTabCompleter(new NameColorCommandTabCompleter());
            this.getCommand("nick").setTabCompleter(new NicknameTabCompleter());
            this.getCommand("nickname").setTabCompleter(new NicknameTabCompleter());
            this.getCommand("whois").setTabCompleter(new WhoIsTabCompleter());
        }catch(NullPointerException a){
            Bukkit.getLogger().info("[NameColor] Unable to register commands!");
        }
        getServer().getPluginManager().registerEvents(new JoinListener(this), this);
        getServer().getPluginManager().registerEvents(new LeaveListener(this), this);
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
    private void setupBlacklistFile(){
        blacklistFile = new File(getDataFolder(), "blacklist.yml");
        if(!blacklistFile.exists()){
            blacklistFile.getParentFile().mkdirs();
            saveResource("blacklist.yml", false);
        }
        blacklistFileConfig = YamlConfiguration.loadConfiguration(blacklistFile);
    }
    //Gets mode from config file, consider if option set in config is viable
    private void getModeFromConfig(){
        Essentials essentials;
        if(config.getString("mode", "auto").equalsIgnoreCase("auto")){
            if(getServer().getPluginManager().getPlugin("Essentials") != null){
                Bukkit.getLogger().info("[NameColor] Using Essentials mode");
                mode = "essentials";
                essentials = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
            }else{
                Bukkit.getLogger().info("[NameColor] Essentials plugin not found, using Server mode");
                mode = "server";
            }
        }else if(config.getString("mode", "auto").equalsIgnoreCase("essentials")){
            if(getServer().getPluginManager().getPlugin("Essentials") != null){
                Bukkit.getLogger().info("[NameColor] Using Essentials mode");
                mode = "essentials";
                essentials = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
            }else{
                Bukkit.getLogger().info("[NameColor] Essentials plugin not found, defaulting to Server mode");
                mode = "server";
            }
        }else if(config.getString("mode", "auto").equalsIgnoreCase("server")){
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
    private void checkUpdate(){
        try{
            HttpsURLConnection connection = (HttpsURLConnection) new URL("https://wiicart.net/namecolor/version.txt").openConnection();
            connection.setRequestMethod("GET");
            String raw = new BufferedReader(new InputStreamReader(connection.getInputStream())).readLine();
            short latest = Short.parseShort(raw);
            if(latest > pluginNum){
                Bukkit.getLogger().info("+-----------[NameColor]-----------+");
                Bukkit.getLogger().info("|    A new update is available!   |");
                Bukkit.getLogger().info("|          Download at:           |");
                Bukkit.getLogger().info("|  https://wiicart.net/namecolor  |");
                Bukkit.getLogger().info("+---------------------------------+");
            }
        } catch(IOException a){
            Bukkit.getLogger().info("[NameColor] Unable to check for updates.");
        }
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
    public NameUtilities getNameUtilities(){ return nameUtilities; }
    public UserUtil getUserUtil(){ return userUtil; }
    /*
    Public config methods
     */
    public FileConfiguration getBlacklistFileConfig(){
        return blacklistFileConfig;
    }
    //saves players.yml file
    public void savePlayersConfig() {
        try {
            playersConfig.save(playersFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}