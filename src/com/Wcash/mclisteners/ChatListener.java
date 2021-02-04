package com.Wcash.mclisteners;

import com.Wcash.JavacordStart;
import com.Wcash.MCDBridge;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import java.awt.*;

public class ChatListener implements Listener {

    private final MCDBridge mcdb;
    private static JavacordStart js;
    private String message;

    public ChatListener() {
        mcdb = MCDBridge.getPlugin();
        js = mcdb.js;
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
                        .send(js.chatStreamChannel);
            } else {
                new MessageBuilder()
                        .append(event.getPlayer().getName())
                        .append(" » ")
                        .append(event.getMessage())
                    .send(js.chatStreamChannel);
            }
        }
    }

    public static void sendServerStartMessage() {
        new MessageBuilder()
                .setEmbed(new EmbedBuilder()
                        .setTitle(":white_check_mark: Server has Started")
                        .setColor(Color.green))
                .send(js.chatStreamChannel);
    }

    public static void sendServerCloseMessage() {
        new MessageBuilder()
                .setEmbed(new EmbedBuilder()
                        .setTitle(":octagonal_sign: Server has Closed")
                        .setColor(Color.red))
                .send(js.chatStreamChannel);
    }

}
