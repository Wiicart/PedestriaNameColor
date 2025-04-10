package com.pedestriamc.namecolor.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

//Class for /color command, introduced in update 1.5.  Shows all bukkit color codes and text styles,
public class ColorsCommand implements CommandExecutor {
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {

        return true;
    }
}
