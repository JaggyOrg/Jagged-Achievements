/*
 * Copyright (C) 2018 Quirkylee
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
 */
package org.jaggy.jaggedachievements.spigot.listeners;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jaggy.jaggedachievements.spigot.Config;
import org.jaggy.jaggedachievements.spigot.DB;
import org.jaggy.jaggedachievements.spigot.Jagged;

/**
 *
 * @author Quirkylee
 */
public class SessionEvents implements Listener {

    private final Jagged plugin;
    private final DB db;
    private final Config config;
    
    public SessionEvents(Jagged p) {
        plugin = p;
        db = plugin.db;
        config = plugin.config;
    }
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if(plugin.loaded) {
            ResultSet data;
            data = db.query("SELECT * FROM "+config.getPrefix()+"Players WHERE Name ='"+player.getName()+"'");
            try {
                if(data.next()) {
                    db.query("UPDATE "+config.getPrefix()+"Players SET Joined = NOW(), Server = '"+config.getServerName()+"' WHERE UID = "+data.getInt("UID"));
                    db.query("INSERT INTO "+config.getPrefix()+"PlayerEvents (UID, Location, EventType, Server) " +
                            "VALUES ('"+data.getInt("UID")+"', '"+player.getLocation()+"', 0, '"+config.getServerName()+"')");
                } else {
                    db.query("INSERT INTO "+config.getPrefix()+"Players (Server, Name) VALUES ('"+config.getServerName()+"', '"+player.getName()+"');");
                    data = db.query("SELECT * FROM "+config.getPrefix()+"Players WHERE Name ='"+player.getName()+"'");
                    data.first();
                    db.query("INSERT INTO "+config.getPrefix()+"PlayerEvents (UID, Location, EventType, Server) " +
                            "VALUES ('"+data.getInt("UID")+"', '"+player.getLocation()+"', 0, '"+config.getServerName()+"')");
                }
            } catch (SQLException ex) {
                plugin.log.log(Level.SEVERE, null, ex);
            }
        }
    }
    
    @EventHandler
    public void OnPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if(plugin.loaded) {
            ResultSet data = db.query("SELECT * FROM "+config.getPrefix()+"Players WHERE Name ='"+player.getName()+"'");
            try {
                if(data.next()) {
                    db.query("INSERT INTO "+config.getPrefix()+"PlayerEvents (UID, Location, EventType, Server) " +
                            "VALUES ('"+data.getInt("UID")+"', '"+player.getLocation()+"', 1, '"+config.getServerName()+"')");
                    db.query("UPDATE "+config.getPrefix()+"Players SET Server = '"+config.getServerName()+"' WHERE UID = "+data.getInt("UID"));
                }
            } catch (SQLException ex) {
                plugin.log.log(Level.SEVERE, null, ex);
            }
        }
    }
}
