package com.Wcash.commands;

import com.Wcash.MCDBridge;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class MCDBCommand implements CommandExecutor {

    private final MCDBridge mcdb = MCDBridge.getPlugin();

    public MCDBCommand() {

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length == 1) {
                if (args[0].equals("reload")){
                    mcdb.reload();
                    return true;
                } else {
                    player.sendMessage("§f[§9MCDBridge§f]§c Command not Found!");
                }
            }
        } else if (sender instanceof ConsoleCommandSender) {
            if (args.length == 1) {
                if (args[0].equals("reload")){
                    mcdb.reload();
                    return true;
                } else {
                    sender.sendMessage("§f[§9MCDBridge§f]§c Command not Found!");
                }
            }
        }
        return true;
    }
}
