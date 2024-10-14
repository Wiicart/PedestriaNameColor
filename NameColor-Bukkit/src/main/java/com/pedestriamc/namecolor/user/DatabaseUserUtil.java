package com.pedestriamc.namecolor.user;

import com.pedestriamc.namecolor.NameColor;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Properties;
import java.util.UUID;

/**
 * Database User storage class.
 * Early NameColor versions that rely on NameUtilities#setColor() are not supported.
 */
public class DatabaseUserUtil extends UserUtil {

    private final DataSource dataSource;

    /**
     * Constructor for DB user Util
     * https://www.spigotmc.org/threads/guide-datasource-and-try-with-resources-how-to-connect-to-your-database-properly.480002/
     * @param nameColor NameColor instance
     * @throws Exception thrown when an issue occurs with the connection
     */
    public DatabaseUserUtil(NameColor nameColor) throws Exception {

        super();
        FileConfiguration configFile = nameColor.getConfig();

        String address = configFile.getString("database.host");
        String port = configFile.getString("database.port");
        String user = configFile.getString("database.user");
        String password = configFile.getString("database.password");
        String database = configFile.getString("database.database");

        if(address == null || user == null || password == null || port == null || database == null){
            throw new Exception("Unable to load database");
        }

        Properties properties = new Properties();

        properties.setProperty("dataSourceClassName", "com.mysql.cj.jdbc.MysqlDataSource");
        properties.setProperty("dataSource.serverName", address);
        properties.setProperty("dataSource.password", password);
        properties.setProperty("dataSource.portNumber", port);
        properties.setProperty("dataSource.user", user);
        properties.setProperty("dataSource.databaseName", database);

        HikariConfig config = new HikariConfig(properties);
        config.setMaximumPoolSize(10);

        dataSource = new HikariDataSource(config);

        Connection connection = dataSource.getConnection();
        String sql = "CREATE TABLE IF NOT EXISTS namecolor_users(uuid varChar(36) primary key, nick varchar(50))";
        Statement statement = connection.createStatement();
        statement.executeUpdate(sql);

        statement.close();
        connection.close();

    }


    @Override
    public void saveUser(User user) {

        String sql = "INSERT INTO namecolor_users(uuid, nick) VALUES (?, ?) ON DUPLICATE KEY UPDATE nick = VALUES(nick)";

        try(Connection connection = dataSource.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)){

            statement.setString(1, String.valueOf(user.getUuid()));

            switch(user.getType()){
                case RGB_COLOR -> statement.setString(2, user.getColor());
                case CHAT_COLOR -> statement.setString(2, String.valueOf(user.getChatColor()));
                case NICKNAME -> statement.setString(2, user.getNickname());
            }

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            Bukkit.getLogger().info("[NameColor] ERROR: An error occurred when trying to update the database.");
        }

    }

    @Override
    public User loadUser(Player player) {

        String sql = "SELECT * FROM namecolor_users WHERE uuid = ?";
        UUID uuid = player.getUniqueId();

        try(Connection connection = dataSource.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setString(1, String.valueOf(uuid));
            ResultSet results = statement.executeQuery();
            results.next();
            String color = results.getString("nick");
            if (color != null) {
                return new User(player, color, true);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return null;
    }

    @Override
    public void disable() {
        try {
            dataSource.getConnection().close();
        } catch (Exception ignored) {
        }
    }
}
