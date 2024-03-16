package me.wcash.mcdbridge.listeners.discord;

import me.wcash.mcdbridge.JavacordStart;
import me.wcash.mcdbridge.MCDBridge;
import me.wcash.mcdbridge.database.Database;
import org.bukkit.entity.Player;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import net.luckperms.api.*;

public class DiscordMessageListener implements MessageCreateListener {

    private final MCDBridge mcdb;
    private String messageFormat;

    public DiscordMessageListener() {
        mcdb = MCDBridge.getPlugin();
        messageFormat = mcdb.chatStreamMessageFormat;
    }

    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        JavacordStart js = mcdb.js;
        Database db = MCDBridge.getDatabase();
        if (event.getChannel() != mcdb.js.chatStreamChannel || event.getMessageAuthor().isYourself()) return;
        String group = "";
        String prefix = "";
        String role = "";
        User user = event.getMessageAuthor().asUser().get();

        if (db.doesEntryExist(event.getMessageAuthor().getId())) {
            Player player = mcdb.getServer().getPlayer(db.getUUID(event.getMessageAuthor().getId()));
            if (mcdb.usePex) {
                PermissionUser pexUser = PermissionsEx.getUser(player);
                prefix = pexUser.getPrefix(player.getWorld().toString());
                group = pexUser.getRankLadderGroup("default").toString();
            } else if (mcdb.useLuckPerms) {
                LuckPerms luckPerms = mcdb.lp;
                net.luckperms.api.model.user.User luckPermsUser = luckPerms.getPlayerAdapter(Player.class).getUser(player);
                group = luckPermsUser.getPrimaryGroup();
            }
        }

        try {
            role = user.getRoles(js.api.getServerById(mcdb.serverID).get()).get(0).getName();
        } catch (NullPointerException ignored) {}

        colorCodeFormatting();

        //Placeholder Replacement
        messageFormat = messageFormat.replaceAll("%GROUP%", group);
        messageFormat = messageFormat.replaceAll("%PREFIX%", prefix);
        messageFormat = messageFormat.replaceAll("%ROLE%", role);
        messageFormat = messageFormat.replaceAll("%USER%", event.getMessageAuthor().getName());
        messageFormat = messageFormat.replaceAll("%MESSAGE%", event.getMessageContent());

        mcdb.getServer().broadcastMessage(messageFormat);

    }

    private void colorCodeFormatting() {
        messageFormat = messageFormat.replaceAll("&a", "§a");
        messageFormat = messageFormat.replaceAll("&b", "§b");
        messageFormat = messageFormat.replaceAll("&c", "§c");
        messageFormat = messageFormat.replaceAll("&d", "§d");
        messageFormat = messageFormat.replaceAll("&e", "§e");
        messageFormat = messageFormat.replaceAll("&f", "§f");
        messageFormat = messageFormat.replaceAll("&1", "§1");
        messageFormat = messageFormat.replaceAll("&2", "§2");
        messageFormat = messageFormat.replaceAll("&3", "§3");
        messageFormat = messageFormat.replaceAll("&4", "§4");
        messageFormat = messageFormat.replaceAll("&5", "§5");
        messageFormat = messageFormat.replaceAll("&6", "§6");
        messageFormat = messageFormat.replaceAll("&7", "§7");
        messageFormat = messageFormat.replaceAll("&8", "§8");
        messageFormat = messageFormat.replaceAll("&9", "§9");
        messageFormat = messageFormat.replaceAll("&0", "§0");
    }

}