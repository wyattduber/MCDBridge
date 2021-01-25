package com.Wcash.listeners;

import com.Wcash.MCDBridge;
import com.Wcash.database.Database;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import java.util.HashMap;
import java.util.Random;

public class PMListener implements MessageCreateListener {

    private final MCDBridge mcdb;
    private final org.bukkit.Server server;
    private final Database db;
    private int randInt;
    private int step = 1;
    private Player player;
    private final String[] roleNames;
    private final Role[] roles;
    private static Role addedRole;
    private final HashMap<String, String[]> addCommands;
    private final TextChannel channel;


    public PMListener(Role[] roles, TextChannel channel) {
        mcdb = MCDBridge.getPlugin();
        server = mcdb.getServer();
        db = MCDBridge.getDatabase();
        roleNames = mcdb.roleNames;
        this.roles = roles;
        addCommands = mcdb.addCommands;
        this.channel = channel;
    }

    @Override
    public void onMessageCreate(MessageCreateEvent event) {

        if (event.getChannel() != channel) {
            return;
        }

        if (step == 3 && event.getMessageContent().equalsIgnoreCase("resend")) {
            step = 2;
        }
        if (event.getMessageContent().equalsIgnoreCase("cancel")) {
            step = 0;
        }
        if (step == 4) {
            RoleAddListener.removeListener(this);
            return;
        }

        if (step == 0) {
            event.getChannel().sendMessage("Operation Canceled. Contact an admin if this was done in error.");
        } else if (step == 1) {
            if (event.getMessageContent().equalsIgnoreCase("no")) {
                event.getChannel().sendMessage("Alright, thanks for supporting us!");
            } else if (event.getMessageContent().equalsIgnoreCase("yes")) {
                new MessageBuilder()
                        .append("__**Please follow these next steps exactly:**__")
                        .append("\n1. Join the Minecraft Server using the desired Account")
                        .append("\n2. Enter your Minecraft Username into this channel __(Username is Case-Sensitive!)__")
                        .append("\n3. A code will be sent to you in minecraft chat, enter that code into this channel")
                        .append("\n4. Profit!")
                        .send(event.getChannel());
                step = 2;
                event.getChannel().sendMessage("To begin, enter your minecraft username. (Username is Case-Sensitive!)");
            } else {
                event.getChannel().sendMessage("Please answer using either \"yes\" or \"no\".");
            }
        } else if (step == 2) {
            try {
                if (server.getPlayer(event.getMessageContent()).isOnline()) {
                    player = server.getPlayer(event.getMessageContent());
                    randInt = Integer.parseInt(getRandomNumber());
                    server.getPlayer(event.getMessageContent()).sendMessage("§f[§9MCDBridge§f] Your code is: §c" + randInt);
                    event.getChannel().sendMessage("Please enter the code sent to you on the server.");
                    step = 3;
                } else {
                    event.getChannel().sendMessage("Player Not Online! Make sure you are logged into the server!");
                }
            } catch (NullPointerException e) {
                event.getChannel().sendMessage("Player Not Found! Make sure that you are logged into the server and that your username was typed in correctly Usernames are Case-Sensitive!");
            }
        } else if (step == 3) {
            try {
                if (event.getMessageContent().equals(String.format("%06d", randInt))) {
                    if (!db.doesEntryExist(player.getUniqueId())) {
                        db.insertLink(event.getMessageAuthor().getId(), player.getName(), player.getUniqueId());
                        runCommands();
                        event.getChannel().sendMessage("Rewards Given! Message one of the online administrators if this process had any errors or you still haven't received your rewards.");
                        player.sendMessage("§f[§9MCDBridge§f] Rewards Received!");
                        step = 4;
                        RoleAddListener.removeListener(this);
                    } else {
                        event.getChannel().sendMessage("Rewards Already Given!");
                    }
                } else {
                    event.getChannel().sendMessage("Code does not match! Make sure you've entered the right code. Say \"resend\" if you need the code again.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                event.getChannel().sendMessage("Something went wrong! Please contact a server administrator");
            }
        }
    }

    private String getRandomNumber() {
        Random rng = new Random();
        int number = rng.nextInt(999999);
        return String.format("%06d", number);
    }

    private void runCommands() {
        RoleAddListener.runCommands(mcdb, roleNames, addCommands, addedRole);
    }

}
