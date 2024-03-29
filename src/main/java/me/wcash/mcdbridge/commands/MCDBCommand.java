package me.wcash.mcdbridge.commands;

import me.wcash.mcdbridge.javacord.JavacordHelper;
import me.wcash.mcdbridge.MCDBridge;
import me.wcash.mcdbridge.database.Database;
import me.wcash.mcdbridge.listeners.discord.DMListener;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.user.User;
import org.jetbrains.annotations.NotNull;

public class MCDBCommand implements CommandExecutor {

    private final MCDBridge mcdb = MCDBridge.getPlugin();
    private final JavacordHelper js;
    private TextChannel pmChannel;

    public MCDBCommand() {
        js = mcdb.js;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, String[] args) {
        if (args.length > 0) {
            if (sender instanceof Player player) {
                if (args[0].equalsIgnoreCase("reload")) {
                    mcdb.reload();
                    mcdb.sendMessage(player, "&fConfiguration Reloaded!");
                    return true;
                } else if (args[0].equalsIgnoreCase("retrolink")) {
                    if (args.length == 1) {
                        js.retroLink(player);
                    } else if (args.length == 3) {
                        js.retroLinkSingle(args[1], args[2]);
                    }
                    return true;
                } else if (args[0].equalsIgnoreCase("link")) {
                    if (args.length == 1) return false;
                    startLinking(player, args[1]);
                    return true;
                } else if (args[0].equalsIgnoreCase("unlink")) {
                    unlink(player);
                    return true;
                } else {
                    mcdb.sendMessage(player, "&cCommand not Found!");
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
            mcdb.sendMessage(player,"&cAccount Already Linked!");
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
                mcdb.error("Error sending message to user! Stack Trace:");
                mcdb.error(e.getMessage());
            }
            user.addUserAttachableListener(new DMListener(pmChannel));
        } catch (NullPointerException e) {
            mcdb.sendMessage(player, "&c Player Not in Discord Server!");
            mcdb.error("Player not found in Discord Server! Stack Trace:");
            mcdb.error(e.getMessage());
        }
        mcdb.sendMessage(player, "Check your Discord DM's to continue!");
    }

    public void unlink(Player player) {
        Database db = MCDBridge.getDatabase();
        if (!db.doesEntryExist(player.getUniqueId())) {
            mcdb.sendMessage(player, "&cAccount Not Linked!");
            return;
        }
        db.removeLink(player.getUniqueId());
        mcdb.sendMessage(player, "Account Successfully Unlinked!");
    }

}