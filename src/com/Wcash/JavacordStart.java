package com.Wcash;

import com.Wcash.listeners.RoleAddListener;
import com.Wcash.listeners.RoleRemoveListener;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;

import java.util.HashMap;

public class JavacordStart {

    public DiscordApi api;
    public Server discordServer;
    public RoleAddListener roleAddListener;
    public RoleRemoveListener roleRemoveListener;
    private final MCDBridge mcdb = MCDBridge.getPlugin();
    private final String[] roleNames;
    public Role[] roles;
    private final HashMap<String, String> roleAndID;
    private boolean doListeners = false;

    public JavacordStart(String[] roleNames) {
        this.roleNames = roleNames;
        roles = new Role[roleNames.length];
        roleAndID = mcdb.roleAndID;
        parseConfig();
        if (doListeners) initListeners();
    }

    public void disableAPI() {
        if (api != null) {
            api.disconnect();
        }
        api = null;
    }

    public void reload() {
        api.removeListener(roleAddListener);
        api.removeListener(roleRemoveListener);
        roleAddListener = null;
        roleRemoveListener = null;

        disableAPI();
        parseConfig();
        if (doListeners) initListeners();
    }

    private void initListeners() {
        roleAddListener = new RoleAddListener(roles, api);
        roleRemoveListener = new RoleRemoveListener(roles);
        api.addListener(roleAddListener);
        api.addListener(roleRemoveListener);
    }

    private void parseConfig() {
        if (mcdb.botToken == null) {
            return;
        }

        try {
            api = new DiscordApiBuilder().setToken(mcdb.botToken).setAllIntents().login().join();
            doListeners = true;
        } catch (Exception e) {
            mcdb.warn("Could not connect to API! Please enter a valid Bot Token in config.yml and reload the plugin.");
            mcdb.warn("If the bot-token is valid, please file an issue on our GitHub.");
        }

        try {
            if (api.getServerById(mcdb.serverID).isPresent())
            discordServer = api.getServerById(mcdb.serverID).get();
        } catch (Exception e) {
            mcdb.warn("Server not Found! Please enter a valid Server ID in config.yml and reload the plugin.");
        }

        try {
            for (int i = 0; i < roleNames.length; i++) {
                if (api.getRoleById(roleAndID.get(roleNames[i])).isPresent())
                    roles[i] = api.getRoleById(roleAndID.get(roleNames[i])).get();
            }
        } catch (Exception e) {
            mcdb.warn("Invalid Role List! Please enter valid Role ID's in the config.yml and reload the plugin.");
        }

    }

}
