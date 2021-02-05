package com.Wcash.discordlisteners;

import com.Wcash.JavacordStart;
import com.Wcash.MCDBridge;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

public class DiscordMessageListener implements MessageCreateListener {

    private final MCDBridge mcdb;
    private String messageFormat;

    public DiscordMessageListener() {
        mcdb = MCDBridge.getPlugin();
        messageFormat = mcdb.chatStreamMessageFormat;
    }

    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        System.out.println(mcdb.js.chatStreamChannel);
        System.out.println(event.getMessageAuthor().isYourself());
        if (event.getChannel() != mcdb.js.chatStreamChannel || event.getMessageAuthor().isYourself()) return;

        messageFormat = messageFormat.replace("%USER%", event.getMessageAuthor().getName());
        messageFormat = messageFormat.replace("%MESSAGE%", event.getMessageContent());

        event.getChannel().sendMessage(messageFormat);

    }

}
