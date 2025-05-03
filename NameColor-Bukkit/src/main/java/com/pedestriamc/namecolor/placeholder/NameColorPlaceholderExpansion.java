package com.pedestriamc.namecolor.placeholder;

import com.pedestriamc.namecolor.NameColor;
import com.pedestriamc.namecolor.user.UserUtil;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NameColorPlaceholderExpansion extends PlaceholderExpansion {

    private final UserUtil userUtil;

    public NameColorPlaceholderExpansion(NameColor nameColor) {
        userUtil = nameColor.getUserUtil();
    }

    @Override
    public @NotNull String getIdentifier() {
        return "namecolor";
    }

    @Override
    public @NotNull String getAuthor() {
        return "wiicart";
    }

    @Override
    public @NotNull String getVersion() {
        return NameColor.PLUGIN_VERSION;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @Nullable String onPlaceholderRequest(final Player player, @NotNull final String params) {
        if(params.equalsIgnoreCase("display_name")) {
            return userUtil.getUser(player.getUniqueId()).getDisplayName();
        }
        return null;
    }


}
