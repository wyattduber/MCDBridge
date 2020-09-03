package com.Wcash;

import org.bukkit.event.EventHandler;
import org.bukkit.plugin.Plugin;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

public class DiscordListener implements MessageCreateListener {

    private Plugin plugin;
    private MessageCreateEvent event;

    public DiscordListener(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMessageCreate(MessageCreateEvent event) {
        this.event = event;
        String message = "§f[§9Discord§f]" + event.getMessageAuthor().toString() + "§f: " + event.getMessageContent();
        plugin.getServer().broadcastMessage(message);
    }

    public TextChannel getServerChannel() {
        return event.getChannel();
    }

}
