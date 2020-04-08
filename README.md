# NameHide
### Open-Source Bukkit Plugin

### What is NameHide?
  NameHide is another plugin that I was commissioned to make.

  This plugin hides player's name plates when the player is not visible.

### How it works:
  All name plates are hidden by default - by setting every player on the same Scoreboard team -- subsequently disabling nametags.

  Then, for each player that is visible, we create invisible CLIENTSIDE armor stands, with a custom name.

  That armor stand takes the name of the player, and follows them around. Once the player moves out of view, the armor stand despawns.

### Features:

  - No server load since all nameplates are clientside

  - Custom armor stand nameplates
  
  - Lightweight resource usage due to many optimizations


## GIFs of the plugin:
![GIF 1](https://github.com/trevorzucker/NameHide/raw/master/gif/demo1.gif)
