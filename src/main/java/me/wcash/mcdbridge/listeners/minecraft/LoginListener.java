package me.wcash.mcdbridge.listeners.minecraft;

import me.wcash.mcdbridge.javacord.JavacordHelper;
import me.wcash.mcdbridge.MCDBridge;
import me.wcash.mcdbridge.database.Database;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;

import java.awt.Color;

public final class LoginListener implements Listener {

    private final boolean updateRequired;
    private final String[] versions;
    private final Database db = MCDBridge.getDatabase();
    private final MCDBridge mcdb;
    private final JavacordHelper js;

    public LoginListener(boolean updateRequired, String[] versions) {
        this.updateRequired = updateRequired;
        this.versions = versions;
        mcdb = MCDBridge.getPlugin();
        js = mcdb.js;
    }

    @EventHandler
    public void onLogin(PlayerJoinEvent event) {

        /* Check for Updates and send message to player with permission to see updates */
        if (updateRequired && (event.getPlayer().hasPermission("mcdb.update") || event.getPlayer().isOp())) {
            event.getPlayer().sendMessage("Version &c" + versions[0] + "&favailable! You have &c" + versions[1] + ".");
            event.getPlayer().sendMessage("Download it at: &9https://www.spigotmc.org/resources/mcdbridge-beta.88409/");

            mcdb.log("Version " + versions[0] + " available! You have " + versions[1] + ".");
            mcdb.log("Download it at: https://www.spigotmc.org/resources/mcdbridge-beta.88409/");

        }

        /* Check if Username has changed since last login */
        if (db.doesEntryExist(event.getPlayer().getUniqueId())) {
            if (!db.getUsername(event.getPlayer().getUniqueId()).equals(event.getPlayer().getName())) {
                db.updateMinecraftUsername(event.getPlayer().getName(), event.getPlayer().getUniqueId());
            }
        }

        if (mcdb.useChatStream) {
            new MessageBuilder()
                    .setEmbed(new EmbedBuilder()
                            .setTitle(":heavy_plus_sign:" + event.getPlayer().getName() + " joined the server")
                            .setColor(Color.green)
                    ).send(js.chatStreamChannel);
        }
    }
}
