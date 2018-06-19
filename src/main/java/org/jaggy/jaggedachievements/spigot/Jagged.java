
package org.jaggy.jaggedachievements.spigot;

import java.util.logging.Logger;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Gallant Team (mldevelop)
 */
public class Jagged extends JavaPlugin {

    public Config config;
    public DB db;
    public Logger log;
    
    public void onLoad() {
        log = Logger.getLogger("JaggedAchievements");
        
        config = new Config();
        config.load(this);
        
        db = new DB();
        db.load(this);
    }
    public void onEnable() {
        
    }
}
