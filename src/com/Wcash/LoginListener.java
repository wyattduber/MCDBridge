package com.Wcash;

import com.Wcash.DiscordWebhook;
import de.comroid.eval.model.Embed;

import java.awt.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.MessageDecoration;
import org.javacord.api.entity.message.embed.EmbedBuilder;

import java.io.IOException;

/**
 * Chat Listener to listen for any player login on the server, then sends it to the Discord Channel
 * using the WebHook URL provided.
 *
 * @author Wcash
 */
public class LoginListener implements Listener {

    private ServerTextChannel channel;

    public LoginListener(ServerTextChannel channel) {
        this.channel = channel;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onLogin(PlayerLoginEvent event) {
        new MessageBuilder()
                .append(event.getPlayer().getDisplayName())
                .append(" » ")
                .setEmbed(new EmbedBuilder()
                        .setTitle(MessageDecoration.BOLD + event.getPlayer().toString() + " joined the server")
                        .setColor(Color.GREEN))
                .send(channel);
    }

}
