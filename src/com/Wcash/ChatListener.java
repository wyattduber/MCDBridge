package com.Wcash;

import com.Wcash.DiscordWebhook;
import netscape.javascript.JSObject;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

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

    public ChatListener(String webhookURL) {
        this.webhookURL = webhookURL;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChat(AsyncPlayerChatEvent event) {
        DiscordWebhook chat = new DiscordWebhook(webhookURL);
        chat.setUsername("Minecraft Server Chat");
        chat.setContent(event.getPlayer().getDisplayName() + " » " + event.getMessage());
        try {
            chat.execute();
        } catch (IOException e) {
            System.out.println("Error Sending Chat Message!");
            e.printStackTrace();
        }
    }

}
