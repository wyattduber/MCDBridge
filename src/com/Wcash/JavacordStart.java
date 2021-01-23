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

    public JavacordStart(String[] roleNames) {
        this.roleNames = roleNames;
        roles = new Role[roleNames.length];
        roleAndID = mcdb.roleAndID;
        parseConfig();
        initListeners();
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
        initListeners();
    }

    private void initListeners() {
        roleAddListener = new RoleAddListener(roles);
        roleRemoveListener = new RoleRemoveListener(roles);
        api.addListener(roleAddListener);
        api.addListener(roleRemoveListener);
    }

    private void parseConfig() {
        if (mcdb.botToken == null) {
            return;
        }

        api = new DiscordApiBuilder().setToken(mcdb.botToken).setAllIntents().login().join();

        if (api.getServerById(mcdb.serverID).isPresent()) {
            discordServer = api.getServerById(mcdb.serverID).get();
        }

        for (int i = 0; i < roleNames.length; i++) {
            if (api.getRoleById(roleAndID.get(roleNames[i])).isPresent()) {
                roles[i] = api.getRoleById(roleAndID.get(roleNames[i])).get();
            }
        }

    }

}
