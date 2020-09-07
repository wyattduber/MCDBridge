package com.Wcash;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.Plugin;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

/**
 * A message listener to forward any message sent to the discord channel provided in config.yml to the minecraft server chat
 *
 * @author Wcash
 */
public class DiscordListener implements MessageCreateListener {

    private Plugin plugin;

    public DiscordListener(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onMessageCreate(MessageCreateEvent event) {
        if (event.getMessageAuthor().isBotUser()) {
            return;
        }
        plugin.getServer().broadcastMessage("§f[§9Discord§f]" + event.getMessageAuthor().getName() + "§f: " + event.getMessageContent());

    }

}
