package com.pedestriamc.namecolor.user;

import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@SuppressWarnings("unused")
public interface UserUtil {

    void saveUser(@NotNull User user);

    @SuppressWarnings("UnusedReturnValue")
    User loadUser(@NotNull UUID uuid);

    @SuppressWarnings("UnusedReturnValue")
    CompletableFuture<User> loadUserAsync(@NotNull UUID uuid);

    User getUser(UUID uuid);

    void addUser(User user);

    void removeUser(UUID uuid);

    void disable();

    Set<User> getUsers();
}
