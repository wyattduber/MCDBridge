package me.wcash.mcdbridge.listeners.minecraft;

import me.wcash.mcdbridge.MCDBridge;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;

import java.awt.Color;

public class LogoutListener implements Listener {

    private final MCDBridge mcdb;

    public LogoutListener() {
        mcdb = MCDBridge.getPlugin();
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if (mcdb.useChatStream) {
            new MessageBuilder()
                    .setEmbed(new EmbedBuilder()
                            .setTitle(":heavy_minus_sign:" + event.getPlayer().getName() + " left the server")
                            .setColor(Color.red))
                    .send(mcdb.js.chatStreamChannel);
        }
    }

}
