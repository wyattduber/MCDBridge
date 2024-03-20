package me.wcash.mcdbridge.listeners.minecraft;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.wcash.mcdbridge.MCDBridge;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

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
    public void onChatMessage(AsyncChatEvent event) {
        if (mcdb.useChatStream) {
            if (mcdb.usePex) {
                PermissionUser user = PermissionsEx.getUser(event.getPlayer());
                String groupName = "";
                try { groupName = user.getRankLadderGroup("default").getName(); } catch (NullPointerException ignored) {}

                TextComponent message = (TextComponent) event.message();
                new MessageBuilder()
                        .append("**" + groupName + "** ")
                        .append(event.getPlayer().getName())
                        .append(" » ")
                        .append(message)
                        .send(mcdb.js.chatStreamChannel);
            } else {
                TextComponent message = (TextComponent) event.message();
                new MessageBuilder()
                        .append(event.getPlayer().getName())
                        .append(" » ")
                        .append(message.content())
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
