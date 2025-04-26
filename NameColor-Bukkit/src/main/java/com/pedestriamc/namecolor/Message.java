package com.pedestriamc.namecolor;

import com.pedestriamc.common.message.CommonMessage;

public enum Message implements CommonMessage {
    NAMECOLOR_HELP("namecolor-help"),
    NICKNAME_HELP("nickname-help"),
    WHOIS_HELP("whois-help"),
    INSUFFICIENT_ARGS("insufficient-args"),
    INVALID_PLAYER("invalid-player"),
    NO_PERMS("no-perms"),
    NAME_SET("name-set"),
    NAME_SET_OTHER("name-set-other"),
    WHOIS_MESSAGE("whois-message"),
    INVALID_ARGS_COLOR("invalid-args-color"),
    INVALID_ARGS_NICK("invalid-args-nick"),
    INVALID_ARGS_WHOIS("invalid-args-whois"),
    INVALID_CMD_COLOR("invalid-cmd-color"),
    INVALID_CMD_NICK("invalid-cmd-nick"),
    INVALID_COLOR("invalid-color"),
    NICK_TOO_LONG("nick-too-long"),
    USERNAME_NICK_PROHIBITED("username-nick-prohibited"),
    NICK_BLACKLIST("nick-blacklist"),
    NO_PERMS_STYLE("no-perms-style"),
    NO_NICK_COLOR("no-nick-color"),
    NO_NICK_COLOR_OTHER("no-nick-color-other"),
    NO_PERMS_OTHER("no-perms-other"),
    NICK_UNKNOWN("nick-unknown"),
    CONSOLE_MUST_DEFINE_PLAYER("define-player"),
    GRADIENT_INVALID_COLOR("invalid-color-hex"),
    GRADIENT_INVALID_USAGE("invalid-usage-gradient"),
    NO_PERMS_STYLE_SPECIFIC("style-specific"),
    NO_PERMS_COLOR_SPECIFIC("color-specific"),
    UNKNOWN_STYLE("unknown-style"),;

    private final String key;

    Message(String key) {
        this.key = key;
    }

    @Override
    public String getKey() {
        return key;
    }
}
