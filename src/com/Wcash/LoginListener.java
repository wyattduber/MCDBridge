package com.Wcash;

import com.Wcash.DiscordWebhook;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import java.io.IOException;

/**
 * Chat Listener to listen for any player login on the server, then sends it to the Discord Channel
 * using the WebHook URL provided.
 *
 * @author Wcash
 */
public class LoginListener implements Listener {

    private String webhookURL;

    public LoginListener(String webhookURL) {
        this.webhookURL = webhookURL;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onLogin(PlayerLoginEvent event) {
        DiscordWebhook login = new DiscordWebhook(webhookURL);
        login.setUsername("Minecraft Server Chat");
        login.setContent(":heavy_plus_sign: **" + event.getPlayer().getName() + " joined the server**");
        try {
            login.execute();
        } catch (IOException e) {
            System.out.println("Error Sending Login Message!");
            e.printStackTrace();
        }
    }

}
