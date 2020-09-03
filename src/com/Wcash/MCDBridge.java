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
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.bukkit.plugin.java.JavaPlugin;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.server.Server;

import java.io.IOException;
import java.util.Optional;

/**
 * Main Plugin File. This is where the onEnable and onDisable functions run
 * as well as most other initialization involved.
 *
 * @author Wcash
 */
public final class MCDBridge extends JavaPlugin implements Listener {

    private DiscordApi api;

    @Override
    public void onEnable() {

        /* Config Stuff */
        saveDefaultConfig();
        FileConfiguration config = this.getConfig();
        this.getConfig().options().copyDefaults(false);

        DiscordApi api = new DiscordApiBuilder().setToken(config.getString("auth")).login().join();
        System.out.println(api.createBotInvite());

        Optional<Server> serverList = api.getServerById(config.getString("server"));
        if (serverList.isPresent()) {
            Server server = serverList.get();
            Optional<ServerTextChannel> channelList = api.getServerTextChannelById(config.getString("channel"));
            if (channelList.isPresent()) {
                ServerTextChannel channel = channelList.get();

                getServer().getPluginManager().registerEvents(this, this); //Necessary for listener events in this class
                getServer().getPluginManager().registerEvents(new ChatListener(channel), this); // Initializes Chat Listener
                getServer().getPluginManager().registerEvents(new LoginListener(channel), this); //Initializes Login Listener
                getServer().getPluginManager().registerEvents(new LogoutListener(channel), this); // Initializes Logout Listener
                api.addListener(new DiscordListener(this));
            }
        }

    }

    @Override
    public void onDisable(){
        if (api != null) {
            api.disconnect();
            api = null;
        }
    }

}
