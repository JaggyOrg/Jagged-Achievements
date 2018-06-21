
package org.jaggy.jaggedachievements.spigot;

import org.jaggy.jaggedachievements.util.Logging;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Gallant Team (mldevelop)
 */
public class Jagged extends JavaPlugin {

    public Config config;
    public DB db;
    boolean loaded;
    public Logging log;
    
    public void onLoad() {
        log = new Logging();
        
        config = new Config();
        config.load(this);
        
        db = new DB();
        db.load(this);
    }
    public void onEnable() {
        db.enable();
    }
    public void onDisable() {
        db.unload();
    }
}
