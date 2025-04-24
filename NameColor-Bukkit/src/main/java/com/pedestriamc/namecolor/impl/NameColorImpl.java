package com.pedestriamc.namecolor.impl;

import com.pedestriamc.namecolor.NameColor;
import com.pedestriamc.namecolor.api.NameColorAPI;
import com.pedestriamc.namecolor.api.NameColorUser;
import com.pedestriamc.namecolor.user.UserUtil;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class NameColorImpl implements NameColorAPI {

    private final UserUtil userUtil;

    public NameColorImpl(@NotNull NameColor nameColor) {
        userUtil = nameColor.getUserUtil();
    }

    @Override
    public NameColorUser getUser(@NotNull UUID uuid) {
        return userUtil.getUser(uuid);
    }

    @Override
    public Player whoIs(String string) {
        return null;
    }

}
