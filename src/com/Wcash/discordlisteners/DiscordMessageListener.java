package com.Wcash.discordlisteners;

import com.Wcash.JavacordStart;
import com.Wcash.MCDBridge;
import com.Wcash.database.Database;
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

        messageFormat = messageFormat.replace("%GROUP%", group);
        messageFormat = messageFormat.replace("%PREFIX%", prefix);
        messageFormat = messageFormat.replace("%ROLE%", role);
        messageFormat = messageFormat.replace("%USER%", event.getMessageAuthor().getName());
        messageFormat = messageFormat.replace("%MESSAGE%", event.getMessageContent());

        mcdb.getServer().broadcastMessage(messageFormat);

    }

}
