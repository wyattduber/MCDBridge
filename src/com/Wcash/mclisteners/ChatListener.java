package com.Wcash.mclisteners;

import com.Wcash.JavacordStart;
import com.Wcash.MCDBridge;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import java.awt.*;

public class ChatListener implements Listener {

    private static MCDBridge mcdb;

    public ChatListener() {
        mcdb = MCDBridge.getPlugin();
    }

    @EventHandler
    public void onChatMessage(AsyncPlayerChatEvent event) {
        if (mcdb.useChatStream) {
            if (mcdb.usePermissions) {
                PermissionUser user = PermissionsEx.getUser(event.getPlayer());
                new MessageBuilder()
                        .append("**" + user.getRankLadderGroup("default").getName() + "** ")
                        .append(event.getPlayer().getName())
                        .append(" » ")
                        .append(event.getMessage())
                        .send(mcdb.js.chatStreamChannel);
            } else {
                new MessageBuilder()
                        .append(event.getPlayer().getName())
                        .append(" » ")
                        .append(event.getMessage())
                    .send(mcdb.js.chatStreamChannel);
            }
        }
    }

    public static void sendServerStartMessage() {
        new MessageBuilder()
                .setEmbed(new EmbedBuilder()
                        .setTitle(":white_check_mark: Server has Started")
                        .setColor(Color.green))
                .send(mcdb.js.chatStreamChannel);
    }

    public static void sendServerCloseMessage() {
        new MessageBuilder()
                .setEmbed(new EmbedBuilder()
                        .setTitle(":octagonal_sign: Server has Stopped")
                        .setColor(Color.red))
                .send(mcdb.js.chatStreamChannel);
    }

}
