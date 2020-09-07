package com.Wcash;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;

import java.awt.*;

/**
 * Chat Listener to listen for any player chat message sent on the server, then sends it to the Discord Channel
 * using the WebHook URL provided.
 *
 * @author Wcash
 */
public class ChatListener implements Listener {

    private TextChannel channel;
    private static TextChannel staticChannel;
    private LoginListener login;
    private LogoutListener logout;
    private Plugin plugin;

    public ChatListener(TextChannel channel, Plugin plugin) {
        this.channel = channel;
        this.plugin = plugin;
        staticChannel = channel;
        login = new LoginListener(channel);
        logout = new LogoutListener(channel);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChat(AsyncPlayerChatEvent event) {
        CharSequence sequence1 = "§f[§9Discord§f]";
        CharSequence sequence2 = "[Discord]";
        CharSequence joinSequence = ":heavy_plus_sign:";
        CharSequence leaveSequence = ":heavy_minus_sign:";
        System.out.println(event.getPlayer().getDisplayName());
        if (!event.getMessage().contains(sequence1) && !event.getMessage().contains(sequence2) && !event.getMessage().contains(joinSequence) && !event.getMessage().contains(leaveSequence)) {
            new MessageBuilder()
                    .append(event.getPlayer().getName())
                    .append(" » ")
                    .append(event.getMessage())
                    .send(channel);
        }
    }

    public static void sendServerStartMessage() {
        new MessageBuilder()
                .setEmbed(new EmbedBuilder()
                        .setTitle(":white_check_mark: Server has Started")
                        .setColor(Color.GREEN))
                .send(staticChannel);
    }

    public static void sendServerCloseMessage() {
        new MessageBuilder()
                .setEmbed(new EmbedBuilder()
                        .setTitle(":octagonal_sign: Server has Closed")
                        .setColor(Color.RED))
                .send(staticChannel);
    }

}
