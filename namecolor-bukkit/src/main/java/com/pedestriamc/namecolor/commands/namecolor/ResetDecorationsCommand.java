package com.pedestriamc.namecolor.commands.namecolor;

import com.pedestriamc.common.message.Messenger;
import com.pedestriamc.namecolor.Message;
import com.pedestriamc.namecolor.NameColor;
import com.pedestriamc.namecolor.user.User;
import net.md_5.bungee.api.ChatColor;
import net.wiicart.commands.command.CartCommandExecutor;
import net.wiicart.commands.command.CommandData;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;

class ResetDecorationsCommand implements CartCommandExecutor {

    private final NameColor nameColor;
    private final Messenger<Message> messenger;

    ResetDecorationsCommand(@NotNull NameColor nameColor) {
        this.nameColor = nameColor;
        messenger = nameColor.getMessenger();
    }

    @Override
    public void onCommand(@NotNull CommandData data) {
        String[] args = data.args();
        CommandSender sender = data.sender();

        User target = getTarget(sender, args);
        if (target == null) {
            return;
        }

    }

    @NotNull
    private String strip(@NotNull String input) {
        return ChatColor.stripColor(input);
    }

    @Nullable
    private User getTarget(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length == 1) {
            String name = args[0];
            Player target = Bukkit.getPlayer(name);
            if (target != null) {
                return getUser(target.getUniqueId());
            } else {
                messenger.sendMessage(sender, Message.INVALID_PLAYER);
                return null;
            }
        } else {
            if (sender instanceof Player player) {
                return getUser(player.getUniqueId());
            } else {
                messenger.sendMessage(sender, Message.CONSOLE_MUST_DEFINE_PLAYER);
                return null;
            }
        }
    }

    private User getUser(@NotNull UUID uuid) {
        return nameColor.getUserUtil().getUser(uuid);
    }

}
