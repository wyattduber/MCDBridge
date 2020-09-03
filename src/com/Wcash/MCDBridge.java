package com.Wcash;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.IOException;

/**
 * Main Plugin File. This is where the onEnable and onDisable functions run
 * as well as most other initialization involved.
 *
 * @author Wcash
 */
public final class MCDBridge extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {

        /* Config Stuff */
        saveDefaultConfig();
        FileConfiguration config = this.getConfig();
        this.getConfig().options().copyDefaults(false);

        getServer().getPluginManager().registerEvents(this, this); //Necessary for listener events

        /* Initializes the Chat Listener */

        getServer().getPluginManager().registerEvents(new Listener() {

            @EventHandler(priority = EventPriority.MONITOR)
            public void onChat(AsyncPlayerChatEvent event) {
                DiscordWebhook chat = new DiscordWebhook(config.getString("url"));
                chat.setUsername("Minecraft Server Chat");
                chat.setContent(event.getPlayer().getDisplayName() + " » " + event.getMessage());
                try {
                    chat.execute();
                } catch (IOException e) {
                    System.out.println("Error Sending Chat Message!");
                    e.printStackTrace();
                }
            }

        }, this);

        /* Initializes the Login Listener */

        getServer().getPluginManager().registerEvents(new Listener() {

            @EventHandler(priority = EventPriority.MONITOR)
            public void onLogin(PlayerLoginEvent event) {
                DiscordWebhook login = new DiscordWebhook(config.getString("url"));
                login.setUsername("Minecraft Server Chat");
                login.setContent(":heavy_plus_sign: **" + event.getPlayer().getName() + " joined the server**");
                try {
                    login.execute();
                } catch (IOException e) {
                    System.out.println("Error Sending Login Message!");
                    e.printStackTrace();
                }
            }

        }, this);

        /* Initializes the Logout Listener */

        getServer().getPluginManager().registerEvents(new Listener() {

            @EventHandler(priority = EventPriority.MONITOR)
            public void onLogout(PlayerQuitEvent event) {
                DiscordWebhook logout = new DiscordWebhook(config.getString("url"));
                logout.setUsername("Minecraft Server Chat");
                logout.setContent(":heavy_minus_sign: **" + event.getPlayer().getName() + " left the server**");
                try {
                    logout.execute();
                } catch (IOException e) {
                    System.out.println("Error Sending Logout Message!");
                    e.printStackTrace();
                }
            }

        }, this);

    }

    @Override
    public void onDisable(){

    }

}
