package me.wcash.mcdbridge;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.wcash.mcdbridge.commands.MCDBCommand;
import me.wcash.mcdbridge.commands.tabcomplete.MCDBTabComplete;
import me.wcash.mcdbridge.database.Database;
import me.wcash.mcdbridge.javacord.JavacordHelper;
import me.wcash.mcdbridge.lib.LibrarySetup;
import me.wcash.mcdbridge.listeners.minecraft.ChatListener;
import me.wcash.mcdbridge.listeners.minecraft.LoginListener;
import me.wcash.mcdbridge.listeners.minecraft.LogoutListener;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;
import java.util.logging.Level;

public class MCDBridge extends JavaPlugin {

    public FileConfiguration config;
    public File customConfigFile;
    public Plugin permissionsPlugin = null;
    public PluginManager pluginManager;
    private static Database db;
    public static String[] versions = new String[2];
    public boolean debugMode = false;
    public boolean usePex = false;
    public boolean useLuckPerms = false;
    public boolean changeNickOnLink;
    public String botToken;
    public String serverID;
    public JavacordHelper js;
    public LuckPerms lp;
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

        /* Load Dependencies */
        LibrarySetup librarySetup = new LibrarySetup();
        librarySetup.loadLibraries();

        /* Load and Initiate Configs */
        try {
            reloadCustomConfig();
            config = getCustomConfig();
            saveCustomConfig();
        } catch (Exception e) {
            error("Error setting up the config! Contact the developer if you cannot fix this issue. Stack Trace:");
            error(e.getMessage());
        }

        /* Load the Database */
        try {
            db = new Database("mcdb.sqlite.db");
            log("Database Found! Path is " + db.getDbPath());
        } catch (Exception e) {
            error("Error setting up database! Contact the developer if you cannot fix this issue. Stack Trace:");
            error(e.getMessage());
        }

        /* Config Parsing */
        if (parseConfig()) {
            parseRoles();
            initChatStream();
            js = new JavacordHelper(roleNames);
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
            Objects.requireNonNull(this.getCommand("mcdb")).setTabCompleter(new MCDBTabComplete());
        } catch (NullPointerException e) {
            error("Error setting up commands! Contact the developer if you cannot fix this issue. Stack Trace:");
            error(e.getMessage());
        }

        if (useChatStream) {
            ChatListener.sendServerStartMessage();
        }

    }

    @Override
    public void onDisable() {
        if (useChatStream) {
            ChatListener.sendServerCloseMessage();
        }

        if (js != null) {
            js.disableAPI();
        }
    }

    public void reload() {
        reloadCustomConfig();
        config = getCustomConfig();
        saveCustomConfig();

        PlayerJoinEvent.getHandlerList().unregister(this);
        if (useChatStream) {
            PlayerQuitEvent.getHandlerList().unregister(this);
            AsyncChatEvent.getHandlerList().unregister(this);
        }

        if (parseConfig()) {
            parseRoles();
            initListeners();
            initChatStream();
        } else {
            error("Config Not Properly Configured! Plugin will not function!");
            return;
        }

        if (js == null) {
            js = new JavacordHelper(roleNames);
        } else {
            js.reload();
        }

    }

    public void initListeners() {
        try {
            new UpdateChecker(this, 88409).getVersion(version -> {
                // Initializes Login Listener when no Updates
                if (!this.getPluginMeta().getVersion().equalsIgnoreCase(version)) {
                    versions[0] = version;
                    versions[1] = this.getPluginMeta().getVersion();
                    getServer().getPluginManager().registerEvents(new LoginListener(true, versions), this);
                } else {
                    getServer().getPluginManager().registerEvents(new LoginListener(false, versions), this);
                }
            });
        } catch (Exception e) {
            error("Error initializing Update Checker! Contact the developer if you cannot fix this issue. Stack Trace:");
            error(e.getMessage());
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

        changeNickOnLink = getConfigBool("change-nickname-on-link");

        log("Config Loaded!");
        return true;
    }

    public Plugin getPermissionsPlugin(PluginManager pluginManager) {
        try {
            permissionsPlugin = pluginManager.getPlugin("PermissionsEx");
            assert permissionsPlugin != null;
            if (permissionsPlugin.isEnabled() && getConfigBool("chatstream-use-permission-groups")) {
                usePex = true;
                useLuckPerms = false;
                log("PermissionsEx Detected! Hooking Permissions");
            }
        } catch (AssertionError | NullPointerException e) {
            try {
                permissionsPlugin = pluginManager.getPlugin("LuckPerms");
                assert permissionsPlugin != null;
                if (permissionsPlugin.isEnabled() && getConfigBool("chatstream-use-permission-groups")) {
                    RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
                    if (provider != null) {
                        lp = provider.getProvider();
                    }
                    useLuckPerms = true;
                    usePex = false;
                    log("LuckPerms Detected! Hooking Permissions");
                }
            } catch (AssertionError | NullPointerException f) {
                log("No permissions plugin found!");
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
            error("Error parsing roles! Make sure the config.yml is correct and reload the plugin. Stack Trace:");
        }
    }

    public void initChatStream() {
        useChatStream = getConfigBool("enable-chatstream");
        if (!useChatStream) return;
        log("ChatStream enabled! Loading necessary config items");
        try {
            chatStreamID = getConfigString("chatstream-channel");
            chatStreamMessageFormat = replaceColors(getConfigString("chatstream-message-format"));
        } catch (Exception e) {
            saveDefaultConfig();
            warn("Invalid Channel ID for ChatStream! Please enter a valid Channel ID in the config.yml and reload the plugin.");
        }
        getServer().getPluginManager().registerEvents(new LogoutListener(), this);
        getServer().getPluginManager().registerEvents(new ChatListener(), this);
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
            error("Error loading default config! Contact the developer if you cannot fix this issue. Stack Trace:");
            error(e.getMessage());
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

    public void debug(String message) {
        this.getLogger().log(Level.FINE, message);
    }

    public void sendMessage(CommandSender sender, String message) {
        if (sender instanceof Player player) {
            player.sendMessage("§f[§9MCDBridge§f] " + replaceColors(message));
        } else {
            log(message);
        }
    }

    /**
     * The escape sequence for minecraft special chat codes
     */
    public static final char ESCAPE = '§';

    /**
     * Replace all the color codes (prepended with &) with the corresponding color code.
     * This uses raw char arrays, so it can be considered to be extremely fast.
     *
     * @param text the text to replace the color codes in
     * @return string with color codes replaced
     */
    public static String replaceColors(String text) {
        char[] chrarray = text.toCharArray();

        for (int index = 0; index < chrarray.length; index ++) {
            char chr = chrarray[index];

            // Ignore anything that we don't want
            if (chr != '&') {
                continue;
            }

            if ((index + 1) == chrarray.length) {
                // we are at the end of the array
                break;
            }

            // get the forward char
            char forward = chrarray[index + 1];

            // is it in range?
            if ((forward >= '0' && forward <= '9') || (forward >= 'a' && forward <= 'f') || (forward >= 'k' && forward <= 'r')) {
                // It is! Replace the char we are at now with the escape sequence
                chrarray[index] = ESCAPE;
            }
        }

        // Rebuild the string and return it
        return new String(chrarray);
    }
}
