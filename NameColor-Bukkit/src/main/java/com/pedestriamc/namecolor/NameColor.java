package com.pedestriamc.namecolor;

import com.pedestriamc.common.message.Messenger;
import com.pedestriamc.namecolor.api.Mode;
import com.pedestriamc.namecolor.api.NameColorAPIProvider;
import com.pedestriamc.namecolor.impl.NameColorImpl;
import com.pedestriamc.namecolor.manager.ClassRegistryManager;
import com.pedestriamc.namecolor.manager.FileManager;
import com.pedestriamc.namecolor.placeholder.NameColorPlaceholderExpansion;
import com.pedestriamc.namecolor.user.DatabaseUserUtil;
import com.pedestriamc.namecolor.user.UserUtil;
import com.pedestriamc.namecolor.user.YamlUserUtil;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Collection;
import java.util.Locale;
import java.util.Objects;

public final class NameColor extends JavaPlugin {

    public static final String PLUGIN_VERSION = "1.10.2";
    public static final short PLUGIN_NUMBER = 12;
    public static final String DISTRIBUTOR = "modrinth";

    private FileManager fileManager;
    private NameUtilities nameUtilities;
    private UserUtil userUtil;
    private Messenger<Message> messenger;

    private boolean usingSql;

    private Mode mode;

    @Override
    public void onLoad() {
        fileManager = new FileManager(this);
        determineMode();
        instantiateClasses();
        setupUserUtil();
    }

    @Override
    public void onEnable() {
        ClassRegistryManager.registerClasses(this);
        checkForUpdate();
        registerPlaceholders();
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

        NameColorAPIProvider.setInstance(new NameColorImpl(this), this);
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
        info("Storage Method: yaml");
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

    private void registerPlaceholders() {
        if (getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new NameColorPlaceholderExpansion(this).register();
        }
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

    public @NotNull String getModeString() {
        return String.valueOf(mode).toLowerCase(Locale.ROOT);
    }

    public String getDistributor() { return DISTRIBUTOR; }

    public NameUtilities getNameUtilities() { return nameUtilities; }

    public @NotNull UserUtil getUserUtil() { return userUtil; }

    @Contract(pure = true)
    public @NotNull String isUsingSql() { return String.valueOf(usingSql); }

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