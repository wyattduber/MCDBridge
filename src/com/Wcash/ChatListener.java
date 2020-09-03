package com.Wcash;

import com.Wcash.DiscordWebhook;
import netscape.javascript.JSObject;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import java.io.IOException;
import java.util.Set;

/**
 * Chat Listener to listen for any player chat message sent on the server, then sends it to the Discord Channel
 * using the WebHook URL provided.
 *
 * @author Wcash
 */
public class ChatListener implements Listener {

    private String webhookURL = "";
    private TextChannel channel = new DiscordListener(null).getServerChannel();

    public ChatListener(ServerTextChannel channel) {
        this.channel = channel;

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChat(AsyncPlayerChatEvent event) {
        new MessageBuilder()
                .append(event.getPlayer().getDisplayName())
                .append(" » ")
                .append(event.getMessage())
                .send(channel);
    }

        /*DiscordWebhook chat = new DiscordWebhook(webhookURL);
        chat.setUsername("Minecraft Server Chat");
        chat.setContent(event.getPlayer().getDisplayName() + " » " + event.getMessage());
        try {
            chat.execute();
        } catch (IOException e) {
            System.out.println("Error Sending Chat Message!");
            e.printStackTrace();
        }
    }

         */

}
