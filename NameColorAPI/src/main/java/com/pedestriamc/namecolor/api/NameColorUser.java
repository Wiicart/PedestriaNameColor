package com.pedestriamc.namecolor.api;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@SuppressWarnings("unused")
public interface NameColorUser {

    String getDisplayName();

    void setDisplayName(@NotNull String displayName);

    UUID getUniqueID();

}
