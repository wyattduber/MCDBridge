package com.Wcash;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author Wcash
 */
public class Main extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {

        //Necessary for listener events
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable(){

    }

}
