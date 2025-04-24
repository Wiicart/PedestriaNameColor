package com.pedestriamc.namecolor.api;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@SuppressWarnings("unused")
public interface NameColorAPI {

    NameColorUser getUser(@NotNull UUID uuid);


    /**
     * Provides the Player with the matching display name.
     * Ignores color codes.
     * The Player with the nickname must be online
     * @param string The display name.
     */
    Player whoIs(String string);
}
