package com.Wcash;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.Plugin;
import org.javacord.api.DiscordApi;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

/**
 * A message listener to forward any message sent to the discord channel provided in config.yml to the minecraft server chat
 *
 * @author Wcash
 */
public class DiscordListener implements MessageCreateListener {

    private Plugin plugin;
    private DiscordApi api;

    public DiscordListener(Plugin plugin, DiscordApi api) {
        this.plugin = plugin;
        this.api = api;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onMessageCreate(MessageCreateEvent event) {
        FileConfiguration config = plugin.getConfig();
        config.getString("Channel");
        if (event.getMessageAuthor().isBotUser()) {
            return;
        }
        if (api.getChannelById(config.getString("Channel")).isPresent()) {
            if (!(api.getTextChannelById(config.getString("Channel")).get() == event.getChannel())) {
                System.out.println("§c[MCDBridge] Discord Channel not found! Make sure the ID in the config.yml is correct!");
                return;
            }
        }
        plugin.getServer().broadcastMessage("§f[§9Discord§f]" + event.getMessageAuthor().getName() + "§f: " + event.getMessageContent());

    }

}
