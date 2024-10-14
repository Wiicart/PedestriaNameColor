package com.pedestriamc.namecolor.user;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public abstract class UserUtil {

    private final UserMap userMap;

    public UserUtil(){
        userMap = new UserMap();
    }

    public abstract void saveUser(User user);

    public abstract User loadUser(Player player);

    public abstract void disable();

    public UserMap userMap(){
        return userMap;
    }

    public class UserMap{

        private final HashMap<UUID, User> users = new HashMap<>();

        public void addUser(User user) {
            users.put(user.getUuid(), user);

        }

        public void removeUser(UUID uuid) {
            users.remove(uuid);
        }

        public User getUser(UUID uuid) {
            return users.get(uuid);
        }

    }

}
