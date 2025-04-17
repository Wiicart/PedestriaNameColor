package com.pedestriamc.namecolor;

import com.pedestriamc.common.message.Messenger;
import com.pedestriamc.namecolor.api.Mode;
import com.pedestriamc.namecolor.manager.ClassRegistryManager;
import com.pedestriamc.namecolor.manager.FileManager;
import com.pedestriamc.namecolor.user.DatabaseUserUtil;
import com.pedestriamc.namecolor.user.UserUtil;
import com.pedestriamc.namecolor.user.YamlUserUtil;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Collection;
import java.util.Objects;

public final class NameColor extends JavaPlugin {

    public static final String PLUGIN_VERSION = "1.9.1";
    public static final short PLUGIN_NUMBER = 9;
    public static final String DISTRIBUTOR = "modrinth";

    private FileManager fileManager;
    private NameUtilities nameUtilities;
    private UserUtil userUtil;
    private Messenger<Message> messenger;

    private boolean usingSql;

    private Mode mode;
    private String defaultColor;

    @Override
    public void onLoad() {
        fileManager = new FileManager(this);
        determineMode();
        loadOptions();
        instantiateClasses();
        setupUserUtil();
    }

    @Override
    public void onEnable() {
        ClassRegistryManager.registerClasses(this);
        checkForUpdate();
        initializeMetrics();
        checkIfReload();
        info("NameColor version " + PLUGIN_VERSION + " enabled.");
    }

    @Override
    public void onDisable() {
        userUtil.disable();
        info("Disabled.");
    }

    private void checkIfReload() {
        Collection<? extends Player> players = getServer().getOnlinePlayers();
        if(!players.isEmpty()) {
            for(Player p : players) {
                userUtil.loadUser(p.getUniqueId());
            }
        }
    }

    private void instantiateClasses() {
        nameUtilities = new NameUtilities(this);

        String prefix = Objects.requireNonNullElse(getConfig().getString("prefix"), "&8[&dNameColor&8] &f");
        messenger = new Messenger<>(getConfig(), prefix, Message.class);
    }

    private void initializeMetrics() {
        Metrics metrics = new Metrics(this, 22112);
        metrics.addCustomChart(new SimplePie("mode", this::getModeString));
        metrics.addCustomChart(new SimplePie("distributor", this::getDistributor));
        metrics.addCustomChart(new SimplePie("using_sql", this::isUsingSql));
    }

    /**
     * Determines what UserUtil implementation to use, and constructs it.
     */
    private void setupUserUtil() {
        String storageMode = Objects.requireNonNullElse(getConfig().getString("storage"), "yml");
        if(
                storageMode.equalsIgnoreCase("mysql") ||
                storageMode.equalsIgnoreCase("mariadb") ||
                storageMode.equalsIgnoreCase("postgresql")
        ) {
            info("Storage Method: database");
            try {
                userUtil = new DatabaseUserUtil(this, storageMode);
                usingSql = true;
                return;
            } catch (Exception e) {
                warn("Failed to load database : " + e.getMessage());
                warn("Defaulting to yml storage.");
            }
        }
        userUtil = new YamlUserUtil(this);
    }

    /**
     * Determines what Mode the plugin will run on (Mode.SERVER or Mode.ESSENTIALS)
     * Refers to "mode" in config.yml
     */
    private void determineMode() {
        String pluginMode = Objects.requireNonNullElse(getConfig().getString("mode"), "auto");
        if(pluginMode.equalsIgnoreCase("server")) {
            mode = Mode.SERVER;
            return;
        }

        if(getServer().getPluginManager().getPlugin("Essentials") != null) {
            info("Essentials plugin found, using Essentials mode.");
            mode = Mode.ESSENTIALS;
        } else {
            info("Essentials plugin not found, defaulting to server mode.");
            mode = Mode.SERVER;
        }
    }

    private void loadOptions() {
        FileConfiguration config = getConfig();
        defaultColor = Objects.requireNonNullElse(config.getString("default-color"), "&f");
    }

    private void checkForUpdate() {
        try {
            HttpsURLConnection connection = (HttpsURLConnection) new URL("https://wiicart.net/namecolor/version.txt").openConnection();
            connection.setRequestMethod("GET");
            String raw = new BufferedReader(new InputStreamReader(connection.getInputStream())).readLine();
            short latest = Short.parseShort(raw);
            if(latest > PLUGIN_NUMBER) {
                info("+-----------[NameColor]-----------+");
                info("|    A new update is available!   |");
                info("|          Download at:           |");
                info("|  https://wiicart.net/namecolor  |");
                info("+---------------------------------+");
            }
        } catch(IOException a) {
            info("[NameColor] Unable to check for updates.");
        }
    }

    public void async(Runnable runnable) {
        getServer().getScheduler().runTaskAsynchronously(this, runnable);
    }

    public Mode getMode() {
        return mode;
    }

    public String getModeString() {
        return String.valueOf(mode);
    }

    public String getDistributor() { return DISTRIBUTOR; }

    public String getDefaultColor() {
        return defaultColor;
    }

    public NameUtilities getNameUtilities() { return nameUtilities; }

    public UserUtil getUserUtil() { return userUtil; }

    public String isUsingSql() { return String.valueOf(usingSql); }

    public Messenger<Message> getMessenger() { return messenger; }

    public FileManager files() {
        return fileManager;
    }

    public void info(String message) {
        getLogger().info(message);
    }

    public void warn(String message) {
        getLogger().warning(message);
    }

}