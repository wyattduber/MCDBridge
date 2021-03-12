package com.Wcash.discordlisteners;

import com.Wcash.JavacordStart;
import com.Wcash.MCDBridge;
import com.Wcash.database.Database;
import org.bukkit.entity.Player;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import java.util.HashMap;
import java.util.Objects;
import java.util.Random;

public class LinkListener implements MessageCreateListener {

    private final MCDBridge mcdb;
    private final org.bukkit.Server server;
    private final Database db;
    private int randInt;
    private int step;
    private Player player;
    private final String[] roleNames;
    private final HashMap<String, String[]> addCommands;
    private final TextChannel channel;
    private boolean resent = false;
    private JavacordStart js;

    public LinkListener(TextChannel channel) {
        mcdb = MCDBridge.getPlugin();
        server = mcdb.getServer();
        db = MCDBridge.getDatabase();
        roleNames = mcdb.roleNames;
        addCommands = mcdb.addCommands;
        this.channel = channel;
        step = 1;
    }

    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        js = mcdb.js;
        Server discordServer = js.api.getServerById(mcdb.serverID).get();
        User user = event.getMessageAuthor().asUser().get();

        if (event.getChannel() != channel) {
            return;
        }
        if (event.getMessageContent().equalsIgnoreCase("resend")) resent = true;
        if (event.getMessageContent().equalsIgnoreCase("cancel"))  {
            event.getChannel().sendMessage("Operation Canceled. Contact an admin if this was done in error.");
            RoleAddListener.removeListener(user, this);
            step = 0;
        }
        if (step == 1) {
            if (event.getMessageContent().equalsIgnoreCase("no")) {
                event.getChannel().sendMessage("Thanks! Contact an admin if this was done fraudulently.");
                RoleAddListener.removeListener(user, this);
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
                if (!resent) {
                    if (Objects.requireNonNull(server.getPlayer(event.getMessageContent())).isOnline()) {
                        player = server.getPlayer(event.getMessageContent());
                        randInt = Integer.parseInt(getRandomNumber());
                        Objects.requireNonNull(server.getPlayer(event.getMessageContent())).sendMessage("§f[§9MCDBridge§f] Your code is: §c" + randInt);
                        event.getChannel().sendMessage("Please enter the code sent to you on the server. Say \"resend\" if you need a new code.");
                        step = 3;
                    } else {
                        event.getChannel().sendMessage("Player Not Online! Make sure you are logged into the server!");
                    }
                } else {
                    step = 3;
                    resent = false;
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
                event.getChannel().sendMessage("Player Not Found! Make sure that you are logged into the server and that your username was typed in correctly Usernames are Case-Sensitive!");
            }
        } else if (step == 3) {
            try {
                if (event.getMessageContent().equals(String.format("%06d", randInt)) && !resent) {
                    db.insertLink(event.getMessageAuthor().getId(), player.getName(), player.getUniqueId());
                    if (mcdb.changeNickOnLink) { js.api.getServerById(mcdb.serverID).get().updateNickname(user, player.getName()).join(); }
                    event.getChannel().sendMessage("Accounts Linked! Message one of the online administrators if this process had any errors or if your nickname hasn't changed.");
                    player.sendMessage("§f[§9MCDBridge§f] Accounts Linked!");
                    step = 4;
                    RoleAddListener.removeListener(user, this);
                } else if (resent && event.getMessageContent().equalsIgnoreCase("resend")) {
                    randInt = Integer.parseInt(getRandomNumber());
                    player.sendMessage("§f[§9MCDBridge§f] Your code is: §c" + randInt);
                    event.getChannel().sendMessage("New Code Sent.");
                    step = 2;
                } else if (!resent && !event.getMessageContent().equals(String.format("%06d", randInt))) {
                    event.getChannel().sendMessage("Code does not match! Make sure you've entered the right code. Say \"resend\" if you need a new code.");
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

}

