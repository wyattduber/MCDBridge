package com.Wcash;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;

import java.awt.*;

/**
 * Chat Listener to listen for any player logout on the server, then sends it to the Discord Channel
 * using the WebHook URL provided.
 *
 * @author Wcash
 */
public class LogoutListener implements Listener {

    private TextChannel channel;
    private boolean quit = false;

    public LogoutListener(TextChannel channel) {
        this.channel = channel;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onLogout(PlayerQuitEvent event) {
        quit = false;
        new MessageBuilder()
                .append("")
                .setEmbed(new EmbedBuilder()
                        .setTitle(":heavy_minus_sign: " + event.getPlayer().getName() + " left the server")
                        .setColor(Color.RED))
                .send(channel);
        quit = true;
    }

    public boolean hasQuit() {
        return quit;
    }

}
