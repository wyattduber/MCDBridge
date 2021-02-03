package com.Wcash.database;

import com.Wcash.MCDBridge;
import org.bukkit.plugin.Plugin;

import java.sql.*;
import java.util.UUID;

/**
 * Database manager class with specific pull/push methods for a sqlite database
 * @author TheFuzzyFish, Wcash
 */

public class Database {

    private String dbPath;
    private Connection dbcon;

    /**
     * Constructor; Builds a new database object
     * @param dbName The name of the database; should be user-supplied. If it exists, a connection will be made. If it does not exist, it will be created and initialized
     */
    public Database(String dbName) {

        try {
            Plugin plugin = MCDBridge.getPlugin();
            dbPath = (plugin.getDataFolder().toString() + "/" + dbName);
            dbPath = "jdbc:sqlite:" + dbPath;
            dbcon = DriverManager.getConnection(dbPath);
            PreparedStatement statement = dbcon.prepareStatement("CREATE TABLE IF NOT EXISTS link(minecraftid TEXT NOT NULL, discordid TEXT NOT NULL, username TEXT NOT NULL)");
            statement.execute();
        } catch (SQLException e) {
            MCDBridge.getPlugin().error("Error while setting up database! Plugin will not save data!");;
        }
    }

    /**
     * Accessor; Returns the SQL database path;
     * @return path of the SQLite DB
     */
    public String getDbPath() {
        return dbPath;
    }

    /**
     * Inserts a new link between a discord and minecraft account
     * @param discordID Discord User ID
     * @param username Minecraft Username
     * @param minecraftID Minecraft UUID
     */
    public void insertLink(long discordID, String username, UUID minecraftID) {
        try {
            PreparedStatement stmt = dbcon.prepareStatement("INSERT INTO link(minecraftid,discordid,username) VALUES (?,?,?)");
            stmt.setString(1, minecraftID.toString());
            stmt.setString(2, Long.toString(discordID));
            stmt.setString(3, username);
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getUsername(UUID minecraftID) {
        ResultSet rs;
        String username;
        try {
            PreparedStatement stmt = dbcon.prepareStatement("SELECT username FROM link WHERE minecraftid=?");
            stmt.setString(1, minecraftID.toString());
            rs = stmt.executeQuery();
            username = rs.getString("username");

            return username;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

    }

    public void updateMinecraftUsername(String newUsername, UUID minecraftID) {
        try {
            PreparedStatement stmt = dbcon.prepareStatement("UPDATE link set username=? where minecraftid=?");
            stmt.setString(1, newUsername);
            stmt.setString(2, minecraftID.toString());

            stmt.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean doesEntryExist(UUID minecraftID) {
        ResultSet rs;
        try {
            PreparedStatement stmt = dbcon.prepareStatement("SELECT minecraftid FROM link WHERE minecraftid=?");
            stmt.setString(1, minecraftID.toString());
            rs = stmt.executeQuery();

            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getUsername(long discordID) {
        ResultSet rs;
        try {
            PreparedStatement stmt = dbcon.prepareStatement("SELECT username FROM link WHERE discordid=?");
            stmt.setString(1, Long.toString(discordID));
            rs = stmt.executeQuery();
            String username = rs.getString("username");

            return username;
        } catch (SQLException e) {
            e.printStackTrace();
            return "";
        }
    }

    public String getUUID(long discordID) {
        ResultSet rs;
        try {
            PreparedStatement stmt = dbcon.prepareStatement("SELECT minecraftid FROM link WHERE discordid=?");
            stmt.setString(1, Long.toString(discordID));
            rs = stmt.executeQuery();
            String minecraftid = rs.getString("minecraftid");
            System.out.println("test1");
            System.out.println(minecraftid);
            System.out.println("test2");

            return minecraftid;
        } catch (SQLException e) {
         e.printStackTrace();
         return "";
        }
    }

    public boolean doesEntryExist(long discordID) {
        ResultSet rs;
        try {
            PreparedStatement stmt = dbcon.prepareStatement("SELECT discordid FROM link WHERE discordid=?");
            stmt.setString(1, Long.toString(discordID));
            rs = stmt.executeQuery();

            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void removeLink(long discordID) {
        try {
            PreparedStatement stmt = dbcon.prepareStatement("DELETE FROM link WHERE discordid=?");
            stmt.setString(1, Long.toString(discordID));
            stmt.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
