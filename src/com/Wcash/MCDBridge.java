package com.Wcash;

import com.Wcash.commands.MCDBCommand;
import com.Wcash.database.Database;
import com.Wcash.mclisteners.LoginListener;
//import net.byteflux.libby.BukkitLibraryManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Objects;
import java.util.logging.Level;

public class MCDBridge extends JavaPlugin {

    public FileConfiguration config;
    public File customConfigFile;
    public Plugin permissionsPlugin = null;
    public PluginManager pluginManager;
    public boolean usePermissions;
    private static Database db;
    public static String[] versions = new String[2];
    public boolean usePex = false;
    public String botToken;
    public String serverID;
    public JavacordStart js;
    public String[] roleNames;
    public HashMap<String, String> roleAndID = new HashMap<>(64);
    public HashMap<String, String[]> addCommands = new HashMap<>(144);
    public HashMap<String, String[]> removeCommands = new HashMap<>(144);
    public String chatStreamID;
    public String chatStreamMessageFormat;
    public boolean useChatStream;

    public static MCDBridge getPlugin() {
        return getPlugin(MCDBridge.class);
    }

    @Override
    public void onEnable() {

        /* Use Libby */
        //loadDependencies();

        /* Load and Initiate Configs */
        try {
            reloadCustomConfig();
            config = getCustomConfig();
            saveCustomConfig();
        } catch (Exception e) {
            error("Error setting up the config! Contact the developer if you cannot fix this issue");
        }

        /* Load the Database */
        try {
            db = new Database("mcdb.sqlite.db");
            log("Database Found! Path is " + db.getDbPath());
        } catch (Exception e) {
            error("Error setting up database! Contact the developer if you cannot fix this issue");
        }

        /* Config Parsing */
        if (parseConfig()) {
            parseRoles();
            initChatStream();
            js = new JavacordStart(roleNames);
            initListeners();
        } else {
            error("Config Not Properly Configured! Plugin will not function!");
        }

        /* Get the Plugin manager for finding other permissions plugins */
        pluginManager = getServer().getPluginManager();
        permissionsPlugin = getPermissionsPlugin(pluginManager);

        /* Commands */
        try {
            Objects.requireNonNull(this.getCommand("mcdb")).setExecutor(new MCDBCommand());
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onDisable() {
        if (js != null) {
            js.disableAPI();
        }
    }

    /*public void loadDependencies() {
        BukkitLibraryManager manager = new BukkitLibraryManager(this); //depends on the server core you are using
        manager.addMavenCentral(); //there are also methods for other repositories
        manager.fromGeneratedResource(this.getResource("AzimDP.json")).forEach(library->{
            try {
                manager.loadLibrary(library);
            }catch(RuntimeException e) { // in case some of the libraries cant be found or dont have .jar file or etc
                getLogger().info("Skipping download of\""+library+"\", it either doesnt exist or has no .jar file");
            }
        });
    }*/

    public void reload() {
        reloadCustomConfig();
        config = getCustomConfig();
        saveCustomConfig();

        PlayerLoginEvent.getHandlerList().unregister(this);

        if (parseConfig()) {
            parseRoles();
            initListeners();
            initChatStream();
        } else {
            error("Config Not Properly Configured! Plugin will not function!");
        }

        if (parseConfig() && js == null) {
            js = new JavacordStart(roleNames);
        } else {
            js.reload();
        }

    }

    public void initListeners() {
        try {
            new UpdateChecker(this, 88409).getVersion(version -> {
                // Initializes Login Listener when no Updates
                if (!this.getDescription().getVersion().equalsIgnoreCase(version)) {
                    versions[0] = version;
                    versions[1] = this.getDescription().getVersion();
                    getServer().getPluginManager().registerEvents(new LoginListener(true, versions), this);
                } else {
                    getServer().getPluginManager().registerEvents(new LoginListener(false, versions), this);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        log("Minecraft Listeners Loaded!");
    }

    public boolean parseConfig() {
        try {
            botToken = getConfigString("bot-token");
            if (getConfigString("bot-token").equalsIgnoreCase("BOTTOKEN") || getConfigString("bot-token").equalsIgnoreCase("")) throw new Exception();
        } catch (Exception e) {
            saveDefaultConfig();
            warn("Invalid Bot Token! Please enter a valid Bot Token in config.yml and reload the plugin.");
            return false;
        }

        try {
            serverID = getConfigString("server-id");
            if (getConfigString("server-id").equalsIgnoreCase("000000000000000000") || getConfigString("server-id").equalsIgnoreCase("")) throw new Exception();
            log("Discord Server Found!");
        } catch (Exception e) {
            saveDefaultConfig();
            warn("Invalid Server ID! Please enter a valid Server ID in config.yml and reload the plugin.");
            return false;
        }

        log("Config Loaded!");
        return true;
    }

    public Plugin getPermissionsPlugin(PluginManager pluginManager) {

        try {
            permissionsPlugin = pluginManager.getPlugin("PermissionsEx");
            if (getConfigBool("chatstream-use-groups")) {
                log("PermissionsEx Detected! Hooking with PermissionsEx");
                usePex = true;
            }
        } catch (Exception e) {
            log("No permissions plugin found!");
        }

        if (pluginManager.getPlugin("PermissionsEx") != null) {
            permissionsPlugin = pluginManager.getPlugin("PermissionsEx");
        } else if (pluginManager.getPlugin("LuckPerms") != null) {
            permissionsPlugin = pluginManager.getPlugin("LuckPerms");
        }

        if (permissionsPlugin != null) {
            if (permissionsPlugin.isEnabled() && permissionsPlugin.getName().equals("PermissionsEx") && config.getBoolean("Groups")) {
                log("PermissionsEx Detected! Hooking permissions with PermissionsEx!");
                usePex = true;
            }
        }
        return permissionsPlugin;
    }

    private void parseRoles() {

        try {
            roleNames = new String[config.getStringList("roles").size()];

            roleNames = config.getStringList("roles").toArray(roleNames);

            for (String roleName : roleNames) {
                String[] tempAdd = new String[config.getStringList(roleName + ".add-commands").size()];
                tempAdd = config.getStringList(roleName + ".add-commands").toArray(tempAdd);
                addCommands.put(roleName, tempAdd);

                String[] tempRemove = new String[config.getStringList(roleName + ".remove-commands").size()];
                tempRemove = config.getStringList(roleName + ".remove-commands").toArray(tempRemove);
                removeCommands.put(roleName, tempRemove);

                roleAndID.put(roleName, Objects.requireNonNull(config.getConfigurationSection(roleName)).getString("role-id"));
            }
        } catch (Exception e) {
            saveDefaultConfig();
            error("Error parsing roles! Make sure the config.yml is correct and reload the plugin.");
        }
    }

    public void initChatStream() {
        useChatStream = getConfigBool("enable-chatstream");
        if (useChatStream) {
            log("ChatStream enabled! Loading necessary config items");
            try {
                chatStreamID = getConfigString("chatstream-channel");
                chatStreamMessageFormat = getConfigString("chatstream-message-format");
            } catch (Exception e) {
                saveDefaultConfig();
                warn("Invalid Channel ID for ChatStream! Please enter a valid Channel ID in the config.yml and reload the plugin.");
            }
        }



    }

    public String getConfigString(String entryName) {
        return config.getString(entryName);
    }

    public boolean getConfigBool(String entryName) {
        return config.getBoolean(entryName);
    }

    public static Database getDatabase() {
        return db;
    }

    public void reloadCustomConfig() {
        saveDefaultConfig();
        config = YamlConfiguration.loadConfiguration(customConfigFile);

        // Look for defaults in the jar
        Reader defConfigStream = null;
        try {
            defConfigStream = new InputStreamReader(Objects.requireNonNull(this.getResource("config.yml")), StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (defConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
            config.setDefaults(defConfig);
        }
    }

    public FileConfiguration getCustomConfig() {
        if (config == null) {
            reloadCustomConfig();
        }
        return config;
    }

    public void saveCustomConfig() {
        if (config == null || customConfigFile == null) {
            return;
        }
        try {
            getCustomConfig().save(customConfigFile);
        } catch (IOException ex) {
            getLogger().log(Level.SEVERE, "Could not save config to " + customConfigFile, ex);
        }
    }

    @Override
    public void saveDefaultConfig() {
        if (customConfigFile == null) {
            customConfigFile = new File(getDataFolder(), "config.yml");
        }
        if (!customConfigFile.exists()) {
            this.saveResource("config.yml", false);
        }
    }

    public void log(String message) {
        this.getLogger().log(Level.INFO, message);
    }

    public void warn(String message) {
        this.getLogger().log(Level.WARNING, message);
    }

    public void error(String message) {
        this.getLogger().log(Level.SEVERE, message);
    }

}
