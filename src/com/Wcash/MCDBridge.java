package com.Wcash;

import com.Wcash.commands.MCDBCommand;
import com.Wcash.database.Database;
import com.Wcash.listeners.LoginListener;
import net.byteflux.libby.BukkitLibraryManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;

public class MCDBridge extends JavaPlugin {

    public FileConfiguration config;
    public File customConfigFile;
    public Plugin permissionsPlugin = null;
    public PluginManager pluginManager;
    private static Database db;
    public static String[] versions;
    public boolean updateRequired = false;
    private LoginListener loginListener;
    public boolean usePex = false;
    public String botToken;
    public String serverID;
    public JavacordStart js;
    public String[] roleNames;
    public HashMap<String, String> roleAndID = new HashMap<>(64);
    public HashMap<String, String[]> addCommands = new HashMap<>(144);
    public HashMap<String, String[]> removeCommands = new HashMap<>(144);

    public static MCDBridge getPlugin() {
        return getPlugin(MCDBridge.class);
    }

    @Override
    public void onEnable() {

        /* Use Libby */
        //loadDependencies();

        /* Load and Initiate Configs */
        reloadCustomConfig();
        config = getCustomConfig();
        saveCustomConfig();

        /* Load the Database */
        db = new Database("mcdb.sqlite.db");

        /* Config Parsing */
        parseConfig();
        parseRoles();
        js = new JavacordStart(roleNames);

        initListeners();

        /* Get the Plugin manager for finding other permissions plugins */
        pluginManager = getServer().getPluginManager();
        permissionsPlugin = getPermissionsPlugin(pluginManager);

        /* Commands */
        this.getCommand("mcdb").setExecutor(new MCDBCommand());

    }

    @Override
    public void onDisable() {
        js.disableAPI();
    }

    public void loadDependencies() {
        BukkitLibraryManager manager = new BukkitLibraryManager(this); //depends on the server core you are using
        manager.addMavenCentral(); //there are also methods for other repositories
        manager.fromGeneratedResource(this.getResource("AzimDP.json")).forEach(library->{
            try {
                manager.loadLibrary(library);
            }catch(RuntimeException e) { // in case some of the libraries cant be found or dont have .jar file or etc
                getLogger().info("Skipping download of\""+library+"\", it either doesnt exist or has no .jar file");
            }
        });
    }

    public void reload() {
        reloadCustomConfig();
        config = getCustomConfig();
        saveCustomConfig();

        js.reload();

        PlayerLoginEvent.getHandlerList().unregister(loginListener);
        loginListener = null;

        parseConfig();
        initListeners();
    }

    public void initListeners() {
        try {
            new UpdateChecker(this, 12345).getVersion(version -> {
                // Initializes Login Listener when no Updates
                if (!this.getDescription().getVersion().equalsIgnoreCase(version)) {
                    versions[0] = version;
                    versions[1] = this.getDescription().getVersion();
                    updateRequired = true;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        loginListener = new LoginListener(updateRequired, versions);
        getServer().getPluginManager().registerEvents(loginListener, this);
    }

    public void parseConfig() {
        if (!getConfigEntry("bot-token").equalsIgnoreCase("BOTTOKEN")) {
            botToken = getConfigEntry("bot-token");
        } else {
            System.out.println("§f[§9MCDBridge§f] Please enter a Bot Token in config.yml!");
        }
        if (!getConfigEntry("server-id").equalsIgnoreCase("000000000000000000")) {
            serverID = getConfigEntry("server-id");
        }
    }

    public Plugin getPermissionsPlugin(PluginManager pluginManager) {
        if (pluginManager.getPlugin("PermissionsEx") != null) {
            permissionsPlugin = pluginManager.getPlugin("PermissionsEx");
        } else if (pluginManager.getPlugin("LuckPerms") != null) {
            permissionsPlugin = pluginManager.getPlugin("LuckPerms");
        }

        if (permissionsPlugin != null) {
            if (permissionsPlugin.isEnabled() && permissionsPlugin.getName().equals("PermissionsEx") && config.getBoolean("Groups")) {
                System.out.println("§f[§9MCDBridge§f] PermissionsEx Detected! Hooking permissions with PermissionsEx!");
                usePex = true;
            }
        }
        return permissionsPlugin;
    }

    private void parseRoles() {

        roleNames = new String[config.getStringList("roles").size()];

        roleNames = config.getStringList("roles").toArray(roleNames);

        for (String roleName : roleNames) {
            String[] tempAdd = new String[config.getStringList(roleName + ".add-commands").size()];
            tempAdd = config.getStringList(roleName + ".add-commands").toArray(tempAdd);
            addCommands.put(roleName, tempAdd);

            String[] tempRemove = new String[config.getStringList(roleName + ".remove-commands").size()];
            tempRemove = config.getStringList(roleName + ".remove-commands").toArray(tempRemove);
            removeCommands.put(roleName, tempRemove);

            roleAndID.put(roleName, config.getConfigurationSection(roleName).getString("role-id"));
        }
    }

    public String getConfigEntry(String entryName) {
        return config.getString(entryName);
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
            defConfigStream = new InputStreamReader(this.getResource("config.yml"), "UTF8");
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
}
