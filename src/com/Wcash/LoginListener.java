package com.Wcash;

import java.awt.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.MessageDecoration;
import org.javacord.api.entity.message.embed.EmbedBuilder;

/**
 * Chat Listener to listen for any player login on the server, then sends it to the Discord Channel
 * using the WebHook URL provided.
 *
 * @author Wcash
 */
public class LoginListener implements Listener {

    private TextChannel channel;
    private boolean joined = false;

    public LoginListener(TextChannel channel) {
        this.channel = channel;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onLogin(PlayerLoginEvent event) {
        joined = false;
        new MessageBuilder()
                .setEmbed(new EmbedBuilder()
                        .setTitle(":heavy_plus_sign: " + event.getPlayer().getName() + " joined the server")
                        .setColor(Color.GREEN))
                .send(channel);
        joined = true;
    }

    public boolean hasJoined() {
        return joined;
    }

}
