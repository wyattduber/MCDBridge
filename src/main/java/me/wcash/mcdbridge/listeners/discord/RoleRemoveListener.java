package me.wcash.mcdbridge.listeners.discord;

import me.wcash.mcdbridge.MCDBridge;
import me.wcash.mcdbridge.database.Database;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.event.server.role.UserRoleRemoveEvent;
import org.javacord.api.listener.server.role.UserRoleRemoveListener;

import java.util.HashMap;

public class RoleRemoveListener implements UserRoleRemoveListener {

    private final Database db = MCDBridge.getDatabase();
    private final MCDBridge mcdb = MCDBridge.getPlugin();
    private final String[] roleNames;
    private final Role[] roles;
    private final HashMap<String, String[]> removeCommands;
    private String username;

    public RoleRemoveListener(Role[] roles) {
        roleNames = mcdb.roleNames;
        this.roles = roles;
        removeCommands = mcdb.removeCommands;
    }

    @Override
    public void onUserRoleRemove(UserRoleRemoveEvent roleEvent) {

        int rolesChanged = 0;

        username = db.getUsername(roleEvent.getUser().getId());

        for (Role role : roles) {
            if (roleEvent.getRole() != role) {
                rolesChanged++;
            }
        }
        if (rolesChanged >= roles.length || !db.doesEntryExist(roleEvent.getUser().getId())) {
            return;
        }

        try {
            db.removeLink(roleEvent.getUser().getId());
            runCommands(roleEvent.getRole());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void runCommands(Role role) {
        RoleAddListener.runCommands(mcdb, roleNames, removeCommands, role, username);
    }

}
