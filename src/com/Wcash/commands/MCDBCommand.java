package com.Wcash.commands;

import com.Wcash.JavacordStart;
import com.Wcash.MCDBridge;
import com.Wcash.database.Database;
import com.Wcash.discordlisteners.LinkListener;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.user.User;



import java.util.concurrent.ExecutionException;

public class MCDBCommand implements CommandExecutor {

    private final MCDBridge mcdb = MCDBridge.getPlugin();
    private final JavacordStart js;
    private TextChannel pmChannel;
    private int i = 0;

    public MCDBCommand() {
        js = mcdb.js;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (args.length > 0) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (args[0].equalsIgnoreCase("reload")) {
                    mcdb.reload();
                    player.sendMessage("§f[§9MCDBridge§f] Configuration Reloaded!");
                    return true;
                } else if (args[0].equalsIgnoreCase("retrolink")) {
                    js.retroLink(player);
                    return true;
                } else if (args[0].equalsIgnoreCase("link")) {
                    if (args.length == 1) return false;
                    startLinking(player, args[1]);
                    return true;
                } else {
                    player.sendMessage("§f[§9MCDBridge§f]§c Command not Found!");
                    return true;
                }
            } else if (sender instanceof ConsoleCommandSender) {
                if (args.length == 1) {
                    if (args[0].equalsIgnoreCase("reload")) {
                        mcdb.reload();
                        return true;
                    } else if (args[0].equalsIgnoreCase("retrolink")) {
                        js.retroLink();
                    } else if (args[0].equalsIgnoreCase("link")) {
                        mcdb.warn("Command can only be ran by a player!");
                        return true;
                    } else {
                        mcdb.warn("Command Not Found!");
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void startLinking(Player player, String discName) {
        Database db = MCDBridge.getDatabase();
        if (db.doesEntryExist(player.getUniqueId())) {
            player.sendMessage("§f[§9MCDBridge§f]§c Account Already Linked!");
            return;
        }

        try {
            User user = js.api.getServerById(mcdb.serverID).get().getMemberByDiscriminatedName(discName).get();
            try {
                new MessageBuilder()
                        .append("You are attempting to link your discord and minecraft accounts on the " + js.api.getServerById(mcdb.serverID).get().getName() + " Discord Server!")
                        .append("\nSay \"yes\" to continue, or \"no\" if this was in error.")
                        .send(user).thenAccept(msg -> pmChannel = msg.getChannel()).join();
            } catch (Exception e) {
                e.printStackTrace();
            }
            user.addUserAttachableListener(new LinkListener(pmChannel));
        } catch (NullPointerException e) {
            player.sendMessage("§f[§9MCDBridge§f]§c Player Not in Discord Server!");
            e.printStackTrace();
        }

    }

}
