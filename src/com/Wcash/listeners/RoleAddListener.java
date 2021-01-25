package com.Wcash.listeners;

import com.Wcash.MCDBridge;
import com.Wcash.database.Database;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.server.role.UserRoleAddEvent;
import org.javacord.api.listener.server.role.UserRoleAddListener;
import org.javacord.api.util.logging.ExceptionLogger;

import java.util.HashMap;

public class RoleAddListener implements UserRoleAddListener {

    private final MCDBridge mcdb;
    private final org.bukkit.Server server;
    private final Database db;
    private int randInt;
    private int step = 1;
    private Player player;
    private final String[] roleNames;
    private final Role[] roles;
    private static Role addedRole;
    private final HashMap<String, String[]> addCommands;
    private TextChannel pmChannel;
    private DiscordApi api;
    private static User user;


    public RoleAddListener(Role[] roles, DiscordApi api) {
        mcdb = MCDBridge.getPlugin();
        server = mcdb.getServer();
        db = MCDBridge.getDatabase();
        roleNames = mcdb.roleNames;
        this.roles = roles;
        addCommands = mcdb.addCommands;
        this.api = api;
    }

    @Override
    public void onUserRoleAdd(UserRoleAddEvent roleEvent) {

        int rolesChanged = 0;

        for (Role role : roles) {
            if (roleEvent.getRole() != role) {
                rolesChanged++;
            } else {
                addedRole = roleEvent.getRole();
            }
        }
        if (rolesChanged >= roles.length) {
            return;
        }

        if (db.doesEntryExist(roleEvent.getUser().getId())) {
            System.out.println("§f[§9MCDBridge§f] User Already Exists!");
        }

        user = roleEvent.getUser();
        user.sendMessage("You were added to a role with Minecraft Rewards! Do you have a Minecraft account? Answer using either \"yes\" or \"no\".")
                .thenAccept(msg -> {
                    pmChannel = msg.getChannel();
                }).exceptionally(ExceptionLogger.get());
        user.addUserAttachableListener(new PMListener(addedRole, pmChannel));

    }

    public static void removeListener(PMListener listener) {
        user.removeUserAttachableListener(listener);
    }

    public static void runCommands(MCDBridge mcdb, String[] roleNames, HashMap<String, String[]> commands, Role role) {
        ConsoleCommandSender console = mcdb.getServer().getConsoleSender();

        String roleName = "";
        for (String name : roleNames) {
            if (name.equalsIgnoreCase(role.getName())) {
                roleName = name;
                break;
            }
        }

        String[] cmds = commands.get(roleName);

        for (String cmdSend : cmds) {
            try {
                Bukkit.getScheduler().callSyncMethod(mcdb, () -> Bukkit.dispatchCommand(console, cmdSend)).get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
