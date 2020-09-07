package com.Wcash.commands;

import com.Wcash.MCDBridge;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.javacord.api.DiscordApi;
import org.jetbrains.annotations.NotNull;

public class AllCommands implements CommandExecutor {

    Player player;
    CommandSender sender;
    Plugin plugin = MCDBridge.getPlugin();
    private DiscordApi api;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        this.sender = sender;
        if (sender instanceof Player) {
            this.player = (Player) sender;
            if (args.length > 0) {
                for (String arg : args) {
                    switch (arg) {
                        case "reload":
                            reload();
                            return true;
                        case "save":
                            player.sendMessage("§f[§9MCDBridge§f] Save Command ran successfully!");
                            return true;
                    }
                }
            }
        }
        return false;
    }

    public void reload() {
        plugin.reloadConfig();
        MCDBridge.reloadListeners(api, plugin, plugin.getConfig(), plugin.getServer());
        player.sendMessage("§f[§9MCDBridge§f] Configuration Reloaded Successfully!");
    }

}
