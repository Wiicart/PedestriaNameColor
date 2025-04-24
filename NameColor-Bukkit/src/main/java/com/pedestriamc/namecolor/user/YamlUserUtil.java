package com.pedestriamc.namecolor.user;

import com.pedestriamc.namecolor.NameColor;
import com.pedestriamc.namecolor.NameUtilities;
import com.pedestriamc.namecolor.manager.FileManager;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class YamlUserUtil implements UserUtil {

    static final String NICK_KEY = "players.{UUID}.nick";
    static final String LEGACY_KEY = "players.{UUID}.color";

    private final NameColor nameColor;
    private final NameUtilities nameUtilities;
    private final FileManager fileManager;
    private final FileConfiguration config;
    private final Map<UUID, User> map;

    public YamlUserUtil(NameColor nameColor) {
        super();
        this.nameColor = nameColor;
        this.nameUtilities = nameColor.getNameUtilities();
        fileManager = nameColor.files();
        config = fileManager.getPlayersConfig();
        map = new ConcurrentHashMap<>();
    }

    @Override
    public void saveUser(@NotNull User user) {
        Objects.requireNonNull(user);
        nameColor.async(() -> {
            synchronized(config) {
                config.set(getNickPath(user.getUniqueID()), user.getDisplayName());
            }
            fileManager.savePlayersFile();
        });
    }

    @Nullable
    @Override
    public User loadUser(@NotNull UUID uuid) {
        synchronized(config) {
            Player player = Objects.requireNonNull(Bukkit.getPlayer(uuid));
            String color = config.getString(getLegacyPath(uuid));
            String nick = config.getString(getNickPath(uuid));

            String displayName;
            if(nick != null) {
                displayName = nick;
            } else if (color != null) {
                displayName = parseLegacy(player, color);
            } else {
                displayName = player.getName();
            }

            User user = new User(nameUtilities, uuid, displayName);
            addUser(user);
            return user;
        }
    }

    // Parses legacy "color" key, converts to nickname.
    @NotNull
    private String parseLegacy(Player player, String color) {
        if(color.matches("^#[a-fA-F0-9]{6}$")) {
            return ChatColor.of(color) + player.getName();
        }

        if(color.length() == 2) {
            return ChatColor.getByChar(color.charAt(1)) + player.getName();
        }

        return player.getName();
    }

    private String getNickPath(@NotNull UUID uuid) {
        return NICK_KEY.replace("{UUID}", uuid.toString());
    }

    private String getLegacyPath(@NotNull UUID uuid) {
        return LEGACY_KEY.replace("{UUID}", uuid.toString());
    }

    @Override
    public CompletableFuture<User> loadUserAsync(@NotNull UUID uuid) {
        CompletableFuture<User> future = new CompletableFuture<>();
        nameColor.async(() -> {
            try {
                User user = loadUser(uuid);
                future.complete(user);
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        });
        return future;
    }

    @Override
    @NotNull
    public User getUser(UUID uuid) {
        User user = map.get(uuid);
        if(user == null) {
            user = new User(nameUtilities, uuid);
            addUser(user);
        }

        return user;
    }

    @Override
    public void addUser(User user) {
        map.put(user.getUniqueID(), user);
    }

    @Override
    public void removeUser(UUID uuid) {
        map.remove(uuid);
    }

    @Override
    public void disable() {
        // No implementation required for YAML storage
    }

    @Override
    public Set<User> getUsers() {
        return new HashSet<>(map.values());
    }

}