
/*
 * Copyright (C) 2018 Jaggy Enterprises
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.jaggy.jaggedachievements.spigot;

import org.bstats.Metrics;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jaggy.gold.api.GoldManager;
import org.jaggy.jaggedachievements.spigot.listeners.BlockEvents;
import org.jaggy.jaggedachievements.spigot.listeners.EntityEvents;
import org.jaggy.jaggedachievements.spigot.listeners.SessionEvents;
import org.jaggy.jaggedachievements.util.Logging;

/**
 *
 * @author Quirkylee
 */
public class Jagged extends JavaPlugin {

    public Config config;
    public DB db;
    public boolean loaded;
    public Logging log;
    private PluginManager manager;
    public Commands cmds;
    public Levels levels;
    public GoldManager gm;

    public void onLoad() {
        manager = getServer().getPluginManager();
        log = new Logging();
        
        config = new Config();
        config.load(this);
        
        db = new DB();
        db.load(this);
        levels = new Levels(this);
    }
    
    public void onEnable() {
        // All you have to do is adding this line in your onEnable method:
        Metrics metrics = new Metrics(this);
        if (loaded) {
            db.enable();
            //Register event Listeners
            manager.registerEvents(new SessionEvents(this), this);
            manager.registerEvents(new BlockEvents(this), this);
            manager.registerEvents(new EntityEvents(this), this);

            if (manager.isPluginEnabled("JaggyGold")) {
                gm = new GoldManager();
                log.info("Jaggy Gold hook enabled.");
            }

            cmds = new Commands(this);
        } else {
            throw new Error("Will not function because it is a first install or there is a problem connecting/opening the database.");
        }
    }
    
    public void onDisable() {
        db.unload();
    }
}
