package com.pedestriamc.namecolor.manager;

import com.pedestriamc.namecolor.NameColor;
import com.tchristofferson.configupdater.ConfigUpdater;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

@SuppressWarnings("unused")
public class FileManager {

    private final NameColor nameColor;

    private File playersFile;
    private File blacklistFile;

    private FileConfiguration playersConfig;
    private FileConfiguration blacklistConfig;

    public FileManager(NameColor nameColor) {
        this.nameColor = nameColor;
        initialize();
    }

    private void initialize() {
        nameColor.saveDefaultConfig();
        loadFiles();
    }

    private void loadFiles() {
        playersFile = loadFile("players.yml");
        blacklistFile = loadFile("blacklist.yml");
        playersConfig = YamlConfiguration.loadConfiguration(playersFile);
        blacklistConfig = YamlConfiguration.loadConfiguration(blacklistFile);
        updateIfPresent("config.yml");
    }

    private File loadFile(String fileName) {
        File file = new File(nameColor.getDataFolder(), fileName);
        if (!file.exists()) {
            try {
                nameColor.saveResource(fileName, false);
            } catch(Exception e) {
                warn("An error occurred while loading file " + fileName + ".");
                warn("Error message: " + e.getMessage());
            }
        }
        return file;
    }

    @SuppressWarnings("SameParameterValue")
    private void updateIfPresent(String resourceName) {
        File file = new File(nameColor.getDataFolder(), resourceName);
        if(file.exists()) {
            try {
                ConfigUpdater.update(nameColor, resourceName, file);
            } catch(IOException e) {
                warn("Failed to update file " + resourceName + ". " + e.getMessage());
            }
        }
    }

    private void warn(String message) {
        nameColor.getLogger().warning(message);
    }

    public FileConfiguration getPlayersConfig() {
        return playersConfig;
    }

    public FileConfiguration getBlacklistConfig() {
        return blacklistConfig;
    }

    public static final Object playersLock = new Object();
    public synchronized void savePlayersFile() {
        synchronized (playersLock) {
            nameColor.async(() -> {
                try {
                    playersConfig.save(playersFile);
                } catch(Exception e) {
                    warn("Failed to save players file: " + e.getMessage());
                }
            });
        }
    }

    public static final Object blacklistLock = new Object();
    public synchronized void saveBlacklistFile() {
        synchronized (playersLock) {
            nameColor.async(() -> {
                try {
                    blacklistConfig.save(blacklistFile);
                } catch(Exception e) {
                    warn("Failed to save blacklist file: " + e.getMessage());
                }
            });
        }
    }
}
