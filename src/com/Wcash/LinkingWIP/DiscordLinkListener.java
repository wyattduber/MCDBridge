package com.Wcash.LinkingWIP;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.javacord.api.event.message.reaction.ReactionAddEvent;

import java.net.http.WebSocket;

public class DiscordLinkListener implements WebSocket.Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onReact(ReactionAddEvent event) {
        event.getUser().getId();
    }

}
