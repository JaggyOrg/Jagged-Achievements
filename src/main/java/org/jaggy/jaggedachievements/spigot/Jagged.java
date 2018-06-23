
package org.jaggy.jaggedachievements.spigot;

import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.jaggy.jaggedachievements.util.Logging;
import org.bukkit.plugin.java.JavaPlugin;
import org.jaggy.jaggedachievements.spigot.listeners.BlockEvents;
import org.jaggy.jaggedachievements.spigot.listeners.SessionEvents;

/**
 *
 * @author Gallant Team (mldevelop)
 */
public class Jagged extends JavaPlugin {

    public Config config;
    public DB db;
    public boolean loaded;
    public Logging log;
    private PluginManager manager;
    
    public void onLoad() {
        manager = getServer().getPluginManager();
        log = new Logging();
        
        config = new Config();
        config.load(this);
        
        db = new DB();
        db.load(this);
    }
    public void onEnable() {
        db.enable();
        
        //Register event Listeners
        manager.registerEvents(new SessionEvents(this), this);
        manager.registerEvents( new BlockEvents(this), this);
    }
    public void onDisable() {
        db.unload();
    }
}
