package com.pedestriamc.namecolor.commands;

import com.pedestriamc.common.message.Messenger;
import com.pedestriamc.namecolor.Message;
import com.pedestriamc.namecolor.NameColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static com.pedestriamc.namecolor.Message.*;

@SuppressWarnings("unused")
public class GradientCommand implements CommandExecutor {

    private final Messenger<Message> messenger;

    public GradientCommand(NameColor nameColor) {
        messenger = nameColor.getMessenger();

    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if(args.length == 0) {
            messenger.sendMessage(sender, INSUFFICIENT_ARGS);
        }

        return false;
    }
}
