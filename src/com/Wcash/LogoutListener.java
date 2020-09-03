package com.Wcash;

import com.Wcash.DiscordWebhook;
import com.Wcash.MCDBridge;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.IOException;

/**
 * Chat Listener to listen for any player logout on the server, then sends it to the Discord Channel
 * using the WebHook URL provided.
 *
 * @author Wcash
 */
public class LogoutListener implements Listener {

    private String webhookURL = "";

    public LogoutListener(String webhookURL) {
        this.webhookURL = webhookURL;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onLogout(PlayerQuitEvent event) {
        DiscordWebhook logout = new DiscordWebhook(webhookURL);
        logout.setUsername("Minecraft Server Chat");
        logout.setContent(":heavy_minus_sign: **" + event.getPlayer().getName() + " left the server**");
        try {
            logout.execute();
        } catch (IOException e) {
            System.out.println("Error Sending Logout Message!");
            e.printStackTrace();
        }
    }


}
