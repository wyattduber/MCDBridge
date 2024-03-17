package me.wcash.mcdbridge.listeners.discord;

import me.wcash.mcdbridge.MCDBridge;
import me.wcash.mcdbridge.database.Database;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.server.role.UserRoleAddEvent;
import org.javacord.api.listener.message.MessageCreateListener;
import org.javacord.api.listener.server.role.UserRoleAddListener;

import java.util.HashMap;

public class RoleAddListener implements UserRoleAddListener {

    private final MCDBridge mcdb;
    private final Database db;
    private final Role[] roles;
    private static Role addedRole;
    private TextChannel pmChannel;
    public static int i;


    public RoleAddListener(Role[] roles) {
        mcdb = MCDBridge.getPlugin();
        db = MCDBridge.getDatabase();
        this.roles = roles;
        i = 0;
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
            mcdb.warn("User already exists within database!");
        }

        User user = roleEvent.getUser();
        if (i == 0) {
            try {
                new MessageBuilder()
                        .append("You were added to a role with Minecraft Rewards on the " + roleEvent.getServer().getName() + " Discord Server!")
                        .append("\nDo you have a Minecraft account? Answer using either \"yes\" or \"no\".")
                        .send(user).thenAccept(msg -> pmChannel = msg.getChannel()).join();
            } catch (Exception e) {
                mcdb.error("Error sending message to user: " + user.getDiscriminatedName() + ". Stack Trace:");
                mcdb.error(e.getMessage());
            }
            user.addUserAttachableListener(new DMListener(addedRole, pmChannel));
            i++;
        }
    }

    public static void removeListener(User user, MessageCreateListener listener) {
        user.removeUserAttachableListener(listener);
        i = 0;
    }

    public static void runCommands(MCDBridge mcdb, String[] roleNames, HashMap<String, String[]> commands, Role role, String username) {
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

            if (cmdSend.contains("%USER%")) {
                cmdSend = cmdSend.replace("%USER%", username);
            }

            try {
                String finalCmdSend = cmdSend;
                Bukkit.getScheduler().callSyncMethod(mcdb, () -> Bukkit.dispatchCommand(console, finalCmdSend)).get();
            } catch (Exception e) {
                mcdb.error("Error running command: " + cmdSend + ". Stack Trace:");
                mcdb.error(e.getMessage());
            }
        }
    }

}
