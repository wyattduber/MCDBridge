package com.Wcash.LinkingWIP;

import java.util.UUID;

public class Link {

    private final UUID minecraftID;
    private long discordID;
    private boolean linked;

    public Link(UUID minecraftID, long discordID) {
        this.minecraftID = minecraftID;
        this.discordID = discordID;
        this.linked = true;
    }

    public Link(UUID minecraftID, long discordID, boolean linked) {
        this.minecraftID = minecraftID;
        this.discordID = discordID;
        this.linked = linked;
    }

    protected void unLink() {
        linked = false;
        this.discordID = 0;
    }

    public UUID getMinecraftID() {
        return minecraftID;
    }

    public long getDiscordID() {
        return discordID;
    }

    public void setDiscordID(long discordID) {
        this.discordID = discordID;
    }

    public boolean isLinked() {
        return linked;
    }

    public Link copy() {
        return new Link(minecraftID, discordID, linked);
    }


}
