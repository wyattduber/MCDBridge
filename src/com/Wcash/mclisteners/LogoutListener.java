package com.Wcash.mclisteners;

import com.Wcash.JavacordStart;
import com.Wcash.MCDBridge;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;

import java.awt.*;

public class LogoutListener implements Listener {

    private final MCDBridge mcdb;
    private final JavacordStart js;

    public LogoutListener() {
        mcdb = MCDBridge.getPlugin();
        js = mcdb.js;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if (mcdb.useChatStream) {
            new MessageBuilder()
                    .setEmbed(new EmbedBuilder()
                            .setTitle(":heavy_minus_sign:" + event.getPlayer().getName() + " left the server")
                            .setColor(Color.red)
                    ).send(js.chatStreamChannel);
        }
    }

}
