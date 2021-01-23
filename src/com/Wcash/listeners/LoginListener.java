package com.Wcash.listeners;

import com.Wcash.MCDBridge;
import com.Wcash.database.Database;
import org.bukkit.Color;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public class LoginListener implements Listener {

    private final boolean updateRequired;
    private final String[] versions;
    private final Database db = MCDBridge.getDatabase();

    public LoginListener(boolean updateRequired, String[] versions) {
        this.updateRequired = updateRequired;
        this.versions = versions;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onLogin(PlayerLoginEvent event) {

        /* Check for Updates and send message to player with permission to see updates */
        if (updateRequired && (event.getPlayer().hasPermission("patreoncraft.update") || event.getPlayer().isOp())) {
            event.getPlayer().sendMessage("§f[" + Color.fromRGB(255, 128, 0)+ "PatreonCraft§f] Version §c" + versions[0] + " §favailable! You have §c" + versions[1] + "§f. Click here to download.");
        }

        /* Check if Username has changed since last login */
        if (db.doesEntryExist(event.getPlayer().getUniqueId())) {
            if (!db.getUsername(event.getPlayer().getUniqueId()).equals(event.getPlayer().getName())) {
                db.updateMinecraftUsername(event.getPlayer().getName(), event.getPlayer().getUniqueId());
            }
        }

    }

}
