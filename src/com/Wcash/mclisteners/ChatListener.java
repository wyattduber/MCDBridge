package com.Wcash.mclisteners;

import com.Wcash.JavacordStart;
import com.Wcash.MCDBridge;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {

    private final MCDBridge mcdb;
    private final JavacordStart js;

    public ChatListener() {
        mcdb = MCDBridge.getPlugin();
        js = mcdb.js;
    }

    @EventHandler
    public void onChatMessage(AsyncPlayerChatEvent event) {
        if (mcdb.useChatStream) {
            if (mcdb.usePermissions) {

            }
        }
    }

}
