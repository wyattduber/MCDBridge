package me.wcash.mcdbridge.commands.tabcomplete;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MCDBTabComplete implements TabCompleter {

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {

        ArrayList<String> tabs = new ArrayList<>();

        return switch (args.length) {
            case 1 -> {
                if (sender.hasPermission("mcdb.update"))
                    tabs.add("reload");
                if (sender.hasPermission("mcdb.link"))
                    tabs.add("link");
                if (sender.hasPermission("mcdb.unlink"))
                    tabs.add("unlink");
                if (sender.hasPermission("mcdb.retrolink"))
                    tabs.add("retrolink");
                yield tabs;
            }
            case 2 -> {
                if (args[0].equalsIgnoreCase("link") && sender.hasPermission("mcdb.link"))
                    tabs.add("<discordID>");
                else if (args[0].equalsIgnoreCase("retrolink") && sender.hasPermission("mcdb.retrolink"))
                    tabs.add("<discordID>");
                yield tabs;
            }
            case 3 -> {
                if (args[0].equalsIgnoreCase("retrolink") && sender.hasPermission("mcdb.retrolink"))
                    tabs.add("<roleName>");
                yield tabs;
            }
            default -> tabs;
        };
    }

}
