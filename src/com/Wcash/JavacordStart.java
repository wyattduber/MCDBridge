package com.Wcash;

import com.Wcash.database.Database;
import com.Wcash.discordlisteners.DiscordMessageListener;
import com.Wcash.discordlisteners.PMListener;
import com.Wcash.discordlisteners.RoleAddListener;
import com.Wcash.discordlisteners.RoleRemoveListener;
import org.bukkit.entity.Player;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import java.util.HashMap;

public class JavacordStart {

    public DiscordApi api;
    public Server discordServer;
    public RoleAddListener roleAddListener;
    public RoleRemoveListener roleRemoveListener;
    public DiscordMessageListener discordMessageListener;
    public Role[] roles;
    public TextChannel chatStreamChannel;

    private final MCDBridge mcdb = MCDBridge.getPlugin();
    private final String[] roleNames;
    private final HashMap<String, String> roleAndID;
    private boolean doListeners = false;
    private TextChannel pmChannel;
    private final Database db;

    public JavacordStart(String[] roleNames) {
        this.roleNames = roleNames;
        roles = new Role[roleNames.length];
        roleAndID = mcdb.roleAndID;
        parseConfig();
        if (doListeners) initListeners();
        db = MCDBridge.getDatabase();
    }

    public void disableAPI() {
        try {
            if (api != null) {
                api.disconnect();
            }
            api = null;
        } catch (Exception e) {
            mcdb.error("Error Disconnecting from API! Contact the developer.");
        }
    }

    public void reload() {
        api.removeListener(roleAddListener);
        api.removeListener(roleRemoveListener);
        roleAddListener = null;
        roleRemoveListener = null;
        if (mcdb.useChatStream) {
            api.removeListener(discordMessageListener);
            discordMessageListener = null;
        }

        disableAPI();
        parseConfig();
        if (doListeners) initListeners();
    }

    private void initListeners() {
        roleAddListener = new RoleAddListener(roles);
        roleRemoveListener = new RoleRemoveListener(roles);
        api.addListener(roleAddListener);
        api.addListener(roleRemoveListener);
        mcdb.log("Discord Listeners Loaded!");
    }

    private void parseConfig() {
        if (mcdb.botToken == null) {
            return;
        }

        try {
            api = new DiscordApiBuilder().setToken(mcdb.botToken).setAllIntents().login().join();
            doListeners = true;
            mcdb.log("Connected to " + api.getYourself().getName() + " Bot!");
        } catch (NullPointerException e) {
            mcdb.warn("Could not connect to API! Please enter a valid Bot Token in config.yml and reload the plugin.");
            mcdb.warn("If the bot-token is valid, please file an issue on our GitHub.");
        }

        try {
            if (api.getServerById(mcdb.serverID).isPresent())
            discordServer = api.getServerById(mcdb.serverID).get();
            mcdb.log("Connected to " + discordServer.getName() + " Discord Server!");
        } catch (Exception e) {
            mcdb.warn("Server not Found! Please enter a valid Server ID in config.yml and reload the plugin.");
        }

        try {
            for (int i = 0; i < roleNames.length; i++) {
                if (api.getRoleById(roleAndID.get(roleNames[i])).isPresent())
                    roles[i] = api.getRoleById(roleAndID.get(roleNames[i])).get();
                mcdb.log("Role " + roles[i].getName() + " Loaded!");
            }
        } catch (Exception e) {
            mcdb.warn("Invalid Role List! Please enter valid Role ID's in the config.yml and reload the plugin.");
        }

        if (mcdb.useChatStream) {
            try {
                if (api.getTextChannelById(mcdb.chatStreamID).isPresent()) {
                    chatStreamChannel = api.getTextChannelById(mcdb.chatStreamID).get();
                }
                discordMessageListener = new DiscordMessageListener();
                api.addListener(discordMessageListener);
            } catch (Exception e) {
                mcdb.warn("The specified Chat Stream Channel cannot be found! Please make sure the channel ID is valid in the config.yml and the channel exists, then reload the plugin.");
            }
        }

    }

    public void retroLink(Player player) {
        int users = 0;
        for (Role role : roles) {
            User[] usersInRole = new User[role.getUsers().size()];
            usersInRole = role.getUsers().toArray(usersInRole);
            if (usersInRole.length == 0) {
                mcdb.warn("No users in" + role.getName() + " role!");
                player.sendMessage("§f[§9MCDBridge§f] No users in " + role.getName() + " role!");
                break;
            }
            users = getUsers(users, role, usersInRole);
            player.sendMessage(role.getName() + ": " + users);
        }
        mcdb.log("Total: " + users);
        player.sendMessage("Total: " + users);
    }

    public void retroLink() {
        int users = 0;
        for (Role role : roles) {
            User[] usersInRole = new User[role.getUsers().size()];
            usersInRole = role.getUsers().toArray(usersInRole);
            if (usersInRole.length == 0) {
                mcdb.warn("No users in" + role.getName() + " role!");
                break;
            }
            users = getUsers(users, role, usersInRole);
        }
        mcdb.log("Total: " + users);
    }

    private int getUsers(int users, Role role, User[] usersInRole) {
        for (User user : usersInRole) {
            if (db.doesEntryExist(user.getId())) break;
            try {
                if (api.getServerById(mcdb.serverID).isPresent()) {
                    new MessageBuilder()
                            .append("You were added to a role with Minecraft Rewards on the " + api.getServerById(mcdb.serverID).get().getName() + " Discord Server!")
                            .append("\nDo you have a Minecraft account? Answer using either \"yes\" or \"no\".")
                            .send(user).thenAccept(msg -> pmChannel = msg.getChannel()).join();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            user.addUserAttachableListener(new PMListener(role, pmChannel));
            users++;
        }
        mcdb.log(role.getName() + " : " + users);
        return users;
    }

}
