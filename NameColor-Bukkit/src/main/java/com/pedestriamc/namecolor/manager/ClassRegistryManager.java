package com.pedestriamc.namecolor.manager;

import com.pedestriamc.namecolor.NameColor;
import com.pedestriamc.namecolor.commands.GradientCommand;
import com.pedestriamc.namecolor.commands.namecolor.NameColorCommand;
import com.pedestriamc.namecolor.commands.NicknameCommand;
import com.pedestriamc.namecolor.commands.WhoIsCommand;
import com.pedestriamc.namecolor.listeners.JoinListener;
import com.pedestriamc.namecolor.listeners.LeaveListener;
import com.pedestriamc.namecolor.tabcompleters.GradientTabCompleter;
import com.pedestriamc.namecolor.tabcompleters.NameColorTabCompleter;
import com.pedestriamc.namecolor.tabcompleters.NicknameTabCompleter;
import com.pedestriamc.namecolor.tabcompleters.WhoIsTabCompleter;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.event.Listener;

public class ClassRegistryManager {

    private final NameColor nameColor;

    public static void registerClasses(NameColor nameColor) {
        new ClassRegistryManager(nameColor).registerClasses();
    }

    private ClassRegistryManager(NameColor nameColor) {
        this.nameColor = nameColor;
    }

    private void registerClasses() {
        registerCommands();
        registerEvents();
    }

    private void registerCommands() {
        registerCommand("namecolor", new NameColorCommand(nameColor), new NameColorTabCompleter());
        registerCommand("whois", new WhoIsCommand(nameColor), new WhoIsTabCompleter());
        registerCommand("gradient", new GradientCommand(nameColor), new GradientTabCompleter());

        NicknameCommand nicknameCommand = new NicknameCommand(nameColor);
        NicknameTabCompleter nicknameTabCompleter = new NicknameTabCompleter();
        registerCommand("nick", nicknameCommand, nicknameTabCompleter);
        registerCommand("nickname", nicknameCommand, nicknameTabCompleter);
    }

    private void registerCommand(String name, CommandExecutor executor, TabCompleter tabCompleter) {
        var command = nameColor.getCommand(name);
        if(command == null) {
            nameColor.getLogger().warning("Failed to register command " + name);
            return;
        }

        if(executor != null) {
            command.setExecutor(executor);
        }

        if(tabCompleter != null) {
            command.setTabCompleter(tabCompleter);
        }
    }

    private void registerEvents() {
        registerEvent(new JoinListener(nameColor));
        registerEvent(new LeaveListener(nameColor));
    }

    private void registerEvent(Listener listener) {
        nameColor.getServer().getPluginManager().registerEvents(listener, nameColor);
    }
}
