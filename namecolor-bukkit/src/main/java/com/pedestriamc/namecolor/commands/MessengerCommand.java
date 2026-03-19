package com.pedestriamc.namecolor.commands;

import com.pedestriamc.common.message.Messenger;
import com.pedestriamc.namecolor.Message;
import com.pedestriamc.namecolor.NameColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class MessengerCommand implements CommandExecutor {

    private final Messenger<Message> messenger;
    private final Message message;

    public MessengerCommand(@NotNull NameColor nameColor, @NotNull Message message) {
        this.messenger = nameColor.getMessenger();
        this.message = message;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        messenger.sendMessage(sender, message);
        return true;
    }
}
