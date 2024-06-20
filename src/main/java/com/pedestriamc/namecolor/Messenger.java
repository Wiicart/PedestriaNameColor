package com.pedestriamc.namecolor;

import org.bukkit.command.CommandSender;

import java.util.HashMap;

public final class Messenger {
    //Class to send messages based on config, w/prefix
    public enum Message{
        NAMECOLOR_HELP, NICKNAME_HELP, WHOIS_HELP, INSUFFICIENT_ARGS, INVALID_PLAYER, NO_PERMS, NAME_SET, NAME_SET_OTHER, WHOIS_MESSAGE, INVALID_ARGS_COLOR, INVALID_ARGS_NICK, INVALID_ARGS_WHOIS, INVALID_CMD_COLOR, INVALID_CMD_NICK, INVALID_COLOR
    }
    HashMap<Message, String> messageStringHashMap = new HashMap<>();
    private static final int messagesLength = Message.values().length;
    public static void initialize(){

    }
    public static void sendMessage(CommandSender sender, Message message, String[] args){


    }
}
