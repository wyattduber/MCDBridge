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


        /* Initializes All Listeners for Chat */
        getServer().getPluginManager().registerEvents(this, this); //Necessary for listener events in this class
        getServer().getPluginManager().registerEvents(new ChatListener(config.getString("url")), this); // Initializes Chat Listener
        getServer().getPluginManager().registerEvents(new LoginListener(config.getString("url")), this); //Initializes Login Listener
        getServer().getPluginManager().registerEvents(new LogoutListener(config.getString("url")), this); // Initializes Logout Listener

    }

    @Override
    public void onDisable(){

    }

}
