package com.Wcash;

import com.Wcash.DiscordWebhook;
import com.Wcash.MCDBridge;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.MessageDecoration;
import org.javacord.api.entity.message.embed.EmbedBuilder;

import java.awt.*;
import java.io.IOException;

/**
 * Chat Listener to listen for any player logout on the server, then sends it to the Discord Channel
 * using the WebHook URL provided.
 *
 * @author Wcash
 */
public class LogoutListener implements Listener {

    private ServerTextChannel channel;

    public LogoutListener(ServerTextChannel channel) {
        this.channel = channel;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onLogin(PlayerQuitEvent event) {
        new MessageBuilder()
                .append(event.getPlayer().getDisplayName())
                .append(" » ")
                .setEmbed(new EmbedBuilder()
                        .setTitle(MessageDecoration.BOLD + event.getPlayer().toString() + " left the server")
                        .setColor(Color.RED))
                .send(channel);
    }


}
