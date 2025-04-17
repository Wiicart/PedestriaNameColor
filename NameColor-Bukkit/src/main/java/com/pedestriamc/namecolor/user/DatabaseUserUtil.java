package com.pedestriamc.namecolor.user;

import com.pedestriamc.namecolor.NameColor;
import com.pedestriamc.namecolor.NameUtilities;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import javax.sql.DataSource;
import java.sql.*;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Database User storage class.
 * Early NameColor versions that rely on NameUtilities#setColor() are not supported.
 */
public class DatabaseUserUtil implements UserUtil {

    private final DataSource dataSource;
    private final NameColor nameColor;
    private final NameUtilities nameUtilities;
    private final Map<UUID, User> map;

    /**
     * Constructor for DB user Util
     * @param nameColor NameColor instance
     * @throws SQLException thrown when an issue occurs with the connection
     */
    public DatabaseUserUtil(@NotNull NameColor nameColor, @NotNull String mode) throws SQLException {
        super();
        this.nameColor = nameColor;
        nameUtilities = nameColor.getNameUtilities();
        map = new ConcurrentHashMap<>();
        FileConfiguration configFile = nameColor.getConfig();
        String address = configFile.getString("database.host");
        String port = configFile.getString("database.port");
        String user = configFile.getString("database.user");
        String password = configFile.getString("database.password");
        String database = configFile.getString("database.database");

        if(address == null || user == null || password == null || port == null || database == null) {
            throw new SQLException("Invalid database information");
        }

        String jdbcUrl;
        String driverClassName;

        switch(mode) {
            case "mysql", "mariadb" -> {
                jdbcUrl = "jdbc:mariadb://" + address + ":" + port + "/" + database;
                driverClassName = "org.mariadb.jdbc.Driver";

            }
            case "postgresql" -> {
                jdbcUrl = "jdbc:postgresql://" + address + ":" + port + "/" + database;
                driverClassName = "org.postgresql.Driver";
            }
            default -> throw new SQLException("Invalid database type defined.");
        }

        HikariDataSource hikariDataSource = new HikariDataSource();
        hikariDataSource.setDriverClassName(driverClassName);
        hikariDataSource.setJdbcUrl(jdbcUrl);
        hikariDataSource.setUsername(user);
        hikariDataSource.setPassword(password);
        dataSource = hikariDataSource;

        try(Connection connection = dataSource.getConnection(); Statement statement = connection.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS namecolor_users(uuid varChar(36) primary key, nick varchar(50))";
            statement.executeUpdate(sql);
        } catch(SQLException e) {
            throw new SQLException("An error occurred with the database.");
        }

    }


    @Override
    public void saveUser(@NotNull User user) {
        nameColor.async(() -> {
            String sql = "INSERT INTO namecolor_users(uuid, nick) VALUES (?, ?) ON DUPLICATE KEY UPDATE nick = VALUES(nick)";
            try(Connection connection = dataSource.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, String.valueOf(user.getUuid()));
                statement.setString(2, user.getDisplayName());

                statement.executeUpdate();
            } catch (SQLException e) {
                nameColor.warn("[NameColor] ERROR: An error occurred when trying to update the database.");
            }
        });
    }

    @Override
    public User loadUser(@NotNull UUID uuid) {
        String sql = "SELECT * FROM namecolor_users WHERE uuid = ?";

        try(Connection connection = dataSource.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, String.valueOf(uuid));
            ResultSet results = statement.executeQuery();
            if(results.next()) {
                String color = results.getString("nick");
                if (color != null) {
                    User user = new User(nameUtilities, uuid, color);
                    addUser(user);
                    return user;
                }

            }
        } catch (SQLException throwables) {
            nameColor.warn("[NameColor] ERROR: An error occurred when attempting to load player " + uuid);
        }
        return null;
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
    public User getUser(UUID uuid) {
        return map.get(uuid);
    }

    @Override
    public void addUser(User user) {
        map.put(user.getUuid(), user);
    }

    @Override
    public void removeUser(UUID uuid) {
        map.remove(uuid);
    }

    @Override
    public Set<User> getUsers() {
        return new HashSet<>(map.values());
    }

    @Override
    public void disable() {
        try {
            dataSource.getConnection().close();
        } catch(Exception ignored) {
        }
    }
}
