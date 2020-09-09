package com.Wcash;

import com.Wcash.commands.*;
import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.channel.TextChannel;

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
        PlayerLoginEvent.getHandlerList().unregister(plugin);
        PlayerQuitEvent.getHandlerList().unregister(plugin);
        System.out.println("§f[§9MCDBridge§f] Listeners Successfully Removed!");
        api = null;

        if (!Objects.equals(config.getString("BotToken"), "BOTTOKEN")) {
            api = new DiscordApiBuilder().setToken(config.getString("BotToken")).login().join();
            System.out.println(api.createBotInvite());
        } else {
            System.out.println("Please enter a Bot Token in config.yml!");
        }

        /* Gets the channel in which to route the listeners to */
        if (!Objects.equals(config.getString("Channel"), "000000000000000000") || !Objects.equals(config.getString("Channel"), "")) {
            assert api != null;
            Optional<TextChannel> channels = api.getTextChannelById(config.getString("Channel"));
            if (channels.isPresent()) {
                TextChannel channel = channels.get();
                System.out.println("§f[§9MCDBridge§f] Text Channel Found!");
                server.getPluginManager().registerEvents(new ChatListener(channel, plugin), plugin); // Initializes MC -> D Chat Listener
                System.out.println("§f[§9MCDBridge§f] Chat Listener Successfully Initialized!");
                server.getPluginManager().registerEvents(new LoginListener(channel), plugin); // Initializes Login Listener
                System.out.println("§f[§9MCDBridge§f] Login Listener Successfully Initialized!");
                server.getPluginManager().registerEvents(new LogoutListener(channel), plugin); // Initializes Logout Listener
                System.out.println("§f[§9MCDBridge§f] Logout Listener Successfully Initialized!");
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
                api.addListener(new DiscordListener(this, api));
            } else {
                System.out.println("ERROR: Main Text Channel Not Found!");
            }
        }

        /* Command Initializers */
        this.getCommand("mcdb").setExecutor(new AllCommands());

        /* Sends Message to Discord alerting that server has restarted */
        ChatListener.sendServerStartMessage();

    }

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
