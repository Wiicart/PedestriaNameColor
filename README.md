# NameColor [1.16+]
- RGB Support
- Nicknames
- Essentials Support
- Permissions
- Text styling
- Who is
##   Commands
- /namecolor <color> - sets own name color
- /namecolor <color> <user> - sets other's name color
- /namecolor help - displays help message
- /nick <name> - sets own nickname
- /nick <name> <user> - sets another player's nickname
- /whois <display name> - determines who has a nickname<br/>
  **All commands support RGB**
  ![example](https://cdn.modrinth.com/data/cached_images/474d81c6f84fa527be1297266ba1c59d75cb14f5.png)
##   Permissions
- namecolor.* - all permissions
- namecolor.set - allows players to set their own color
- namecolor.set.others - allows setting other player's color
- namecolor.nick - allows players to set their own nickname
- namecolor.nick.others - allows setting other player's nicknames
- namecolor.whois - allows use of the /whois command
##   Default Config


<details>
<summary>Configuration</summary>


```
#-------------------------#
#        NameColor        #
#     pedestriamc.com     #
#     author: wiicart     #
#-------------------------#
# Sets the plugin's prefix.
# Compatible with '&' color codes.
prefix: '&8[&dNameColor&8] &f'

# Modes of changing nicknames:
#  - auto (default)
#    Automatically chooses the best mode
#    based on server setup.
#
#  - server
#    Uses the Bukkit API to change names.
#    Default fallback option.
#
#  - essentials
#    Uses the Essentials API to change names.
#    Requires EssentialsX.
#    If you have Essentials installed, this plugin
#    may not work properly without this mode.
#
mode: auto

# Default name color:
# Proper format:
# &<color code> for Minecraft codes
# #<rgb> for RGB codes
default-color: '&f'

# +----- Messages -----+
# Note: prefix is used before all messages.
# Compatible with '&' color codes.

# NameColor help message:
namecolor-help:
  - '&8+----[&dNameColor&8]----+'
  - '&fUsage: &7/namecolor <color> <option1> <option2>&f.'
  - '&fAny combinations of the following options can be used:'
  - '&fbold, underline, italics, magic, strike.'
  - '&fIf changing another players name, add their username to the end.'
  - '&fRGB colors supported, formatted #<RGB>.'

# Nickname help message:
nickname-help:
  - '&8+----[&dNameColor&8]----+'
  - '&fUsage: &7/nick <nick> <username>&f.'
  - '&f<username> may be blank, RGB colors supported.'
  - '&fNote: Enter RGB codes as &#<RGB>.'

# Whois help message:
whois-help: '&fUsage: &7/whois <display name>'

# Not enough args message:
insufficient-args: '&fInsufficient arguments.'

# Invalid player message:
invalid-player: '&fCannot find that player!'

# No permission message:
no-perms: '&fYou don''t have permission!'

# Player's name has been set message:
# %display-name% will be replaced with
# the user's new display name.
name-set: '&fYour display name has been set to &f%display-name%&f.'

# Other player's name changed message:
# %display-name% will be replaced with
# the subject's new display name.
# %username% will be replaced with
# the subject's username.
name-set-other: '&f%username%''s display name has been set to %display-name%&f.'

# Whois command message for valid player
# %display-name% will be replaced with the
# display name without colors.
# %username% will be replaced with the proper username.
whois-message: '&7%display-name%''s &fusername is &7%username%&f.'

# Invalid args message for /namecolor:
invalid-args-color: '&fInvalid arguments. Type &7/namecolor help &ffor usage.'

# Invalid args message for /nick:
invalid-args-nick: '&fInvalid arguments. Type &7/nick help &ffor usage.'

# Invalid args message for /whois
invalid-args-whois: '&fInvalid arguments. Type &7/whois help &ffor usage.'

# Invalid command usage message for /namecolor:
invalid-cmd-color: '&fInvalid command usage! Type &7/namecolor help &ffor usage.'

# Invalid command usage message for /nick:
invalid-cmd-nick: '&fInvalid command usage! Type &7/nick help &ffor usage.'

# Invalid color in /namecolor
invalid-color: '&fInvalid color!'
```


</details>


**Need support? Contact @wiicart on Discord or wiicart@pedestriamc.com**
