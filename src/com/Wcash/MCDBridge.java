package com.Wcash;

import com.Wcash.commands.*;
import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Main Plugin File. This is where the onEnable and onDisable functions run
 * as well as most other initialization involved.
 *
 * @author Wcash
 */
public final class MCDBridge extends JavaPlugin implements Listener {

    private DiscordApi api;
    private static DiscordListener d;
    private static boolean dInit = false;

    public static MCDBridge getPlugin() {
        return getPlugin(MCDBridge.class);
    }

    public static void reloadListeners(DiscordApi api, Plugin plugin, FileConfiguration config, Server server) {
        AsyncPlayerChatEvent.getHandlerList().unregister(plugin);
        PlayerJoinEvent.getHandlerList().unregister(plugin);
        PlayerQuitEvent.getHandlerList().unregister(plugin);
        ChatListener chat = null;
        LoginListener login = null;
        LogoutListener logout = null;

        if (!Objects.equals(config.getString("BotToken"), "BOTTOKEN")) {
            api = new DiscordApiBuilder().setToken(config.getString("BotToken")).login().join();
            System.out.println(api.createBotInvite());
        } else {
            System.out.println("Please enter a Bot Token in config.yml!");
        }

        /* Gets the channel in which to route the listeners to */
        if (!Objects.equals(config.getString("Channel"), "000000000000000000") || !Objects.equals(config.getString("Channel"), "")) {
            Optional<TextChannel> channels = api.getTextChannelById(config.getString("Channel"));
            if (channels.isPresent()) {
                TextChannel channel = channels.get();
                chat = new ChatListener(channel, plugin);
                login = new LoginListener(channel);
                logout = new LogoutListener(channel);
                server.getPluginManager().registerEvents(new ChatListener(channel, plugin), plugin); // Initializes MC -> D Chat Listener
                server.getPluginManager().registerEvents(new LoginListener(channel), plugin); //Initializes Login Listener
                server.getPluginManager().registerEvents(new LogoutListener(channel), plugin); // Initializes Logout Listener
            } else {
                System.out.println("ERROR: Main Text Channel Not Found!");
            }
        }
    }

    @Override
    public void onEnable() {

        /* Config Stuff */
        saveDefaultConfig();
        FileConfiguration config = this.getConfig();
        config.options().copyDefaults(true);
        saveConfig();

        getServer().getPluginManager().registerEvents(this, this); //Necessary for listener events in this class

        /* Initialize all Listeners */
        if (!Objects.equals(config.getString("BotToken"), "BOTTOKEN")) {
            api = new DiscordApiBuilder().setToken(config.getString("BotToken")).login().join();
            System.out.println(api.createBotInvite());
        } else {
            System.out.println("Please enter a Bot Token in config.yml!");
        }

        /* Gets the channel in which to route the listeners to */
        if (!Objects.equals(config.getString("Channel"), "000000000000000000") || !Objects.equals(config.getString("Channel"), "")) {
            Optional<TextChannel> channels = api.getTextChannelById(config.getString("Channel"));
            if (channels.isPresent()) {
                TextChannel channel = channels.get();
                getServer().getPluginManager().registerEvents(new ChatListener(channel, this), this); // Initializes MC -> D Chat Listener
                getServer().getPluginManager().registerEvents(new LoginListener(channel), this); //Initializes Login Listener
                getServer().getPluginManager().registerEvents(new LogoutListener(channel), this);// Initializes Logout Listener
                api.addListener(new DiscordListener(this));
            } else {
                System.out.println("ERROR: Main Text Channel Not Found!");
            }
        }

        /* Command Initializers */
        this.getCommand("mcdb").setExecutor(new AllCommands());

        /* Sends Message to Discord alerting that server has restarted */
        ChatListener.sendServerStartMessage();

    }

    /*public static void initListeners(DiscordApi api, Server server, Plugin plugin, Boolean enable) {
        ChatListener chat = null;
        LoginListener login = null;
        LogoutListener logout = null;
        DiscordListener d = null;
        FileConfiguration config = plugin.getConfig();
        /* Initializes the main DiscordAPI to build all discord functions
        if (enable) {
            if (!Objects.equals(config.getString("BotToken"), "BOTTOKEN")) {
                api = new DiscordApiBuilder().setToken(config.getString("BotToken")).login().join();
                System.out.println(api.createBotInvite());
            } else {
                System.out.println("Please enter a Bot Token in config.yml!");
            }

            /* Gets the channel in which to route the listeners to
            if (!Objects.equals(config.getString("Channel"), "000000000000000000") || !Objects.equals(config.getString("Channel"), "")) {
                Optional<TextChannel> channels = api.getTextChannelById(config.getString("Channel"));
                if (channels.isPresent()) {
                    TextChannel channel = channels.get();
                    chat = new ChatListener(channel, plugin);
                    login = new LoginListener(channel);
                    logout = new LogoutListener(channel);
                    d = new DiscordListener(plugin);
                    server.getPluginManager().registerEvents(chat, plugin); // Initializes MC -> D Chat Listener
                    server.getPluginManager().registerEvents(login, plugin); //Initializes Login Listener
                    server.getPluginManager().registerEvents(logout, plugin);// Initializes Logout Listener
                    api.addListener(d); // Initialize D -> MC Chat Listener
                } else {
                    System.out.println("ERROR: Main Text Channel Not Found!");
                }
            }
        } else {
            if (!Objects.equals(config.getString("BotToken"), "BOTTOKEN")) {
                api = new DiscordApiBuilder().setToken(config.getString("BotToken")).login().join();
                System.out.println(api.createBotInvite());
            } else {
                System.out.println("Please enter a Bot Token in config.yml!");
            }

            /* Gets the channel in which to route the listeners to
            if (!Objects.equals(config.getString("Channel"), "000000000000000000") || !Objects.equals(config.getString("Channel"), "")) {
                Optional<TextChannel> channels = api.getTextChannelById(config.getString("Channel"));
                if (channels.isPresent()) {
                    TextChannel channel = channels.get();
                    chat = new ChatListener(channel, plugin);
                    login = new LoginListener(channel);
                    logout = new LogoutListener(channel);
                    d = new DiscordListener(plugin);
                    server.getPluginManager().registerEvents(chat, plugin); // Initializes MC -> D Chat Listener
                    server.getPluginManager().registerEvents(login, plugin); //Initializes Login Listener
                    server.getPluginManager().registerEvents(logout, plugin); // Initializes Logout Listener
                    api.addListener(d); // Initialize D -> MC Chat Listener
                } else {
                    System.out.println("ERROR: Main Text Channel Not Found!");
                }
            }
        }
    }

    */

    @Override
    public void onDisable(){
        if (api != null) {
            api.disconnect();
            api = null;
        }
        /* Sends Message to Discord alerting that server has closed */
        ChatListener.sendServerCloseMessage();

        saveConfig();
        System.out.println("§cWARNING: If this is being reloaded during a bukkkit reload, console spam will start!");
    }

}
