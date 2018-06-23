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
import java.util.logging.Logger;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.jaggy.jaggedachievements.spigot.Config;
import org.jaggy.jaggedachievements.spigot.DB;
import org.jaggy.jaggedachievements.spigot.Jagged;

/**
 *
 * @author Quirkylee
 */
public class BlockEvents implements Listener {
    public Jagged plugin;
    private final DB db;
    private final Config config;
    public BlockEvents(Jagged p) {
        plugin = p;
        db = plugin.db;
        config = plugin.config;
    }
    @EventHandler
    void onBlockPlace(BlockPlaceEvent event) {
        if(plugin.loaded) {
            try {
                Player player = event.getPlayer();
                ResultSet data = db.query("SELECT * FROM "+config.getPrefix()+"Players WHERE Name ='"+player.getName()+"'");
                data.first();
                Block block = event.getBlockPlaced();
                db.query("INSERT INTO "+config.getPrefix()+"BlockEvents (BlockID, UID, Location, EventType, Server) "+
                        "VALUES ('"+block.getType()+"', '"+data.getInt("UID")+"', '"+player.getLocation()+
                        "', 0, '"+config.getServerName()+"')");
                ResultSet rows = db.query("SELECT COUNT(*) FROM "+config.getPrefix()+
                        "BlockEvents WHERE UID = '"+data.getInt("UID")+"' AND BlockID = '"+block.getType()+"'");
                if(rows.first()) {
                    int count = rows.getInt(1);
                }
                
            } catch (SQLException | SecurityException ex) {
                plugin.log.log(Level.SEVERE, null, ex);
            }
        }
    }
    @EventHandler
    void OnBlockBreak(BlockBreakEvent event) {
        if(plugin.loaded) {
            try {
                Player player = event.getPlayer();
                ResultSet data = db.query("SELECT * FROM "+config.getPrefix()+"Players WHERE Name ='"+player.getName()+"'");
                data.first();
                Block block = event.getBlock();
                db.query("INSERT INTO "+config.getPrefix()+"BlockEvents (BlockID, UID, Location, EventType, Server) "+
                        "VALUES ('"+block.getType()+"', '"+data.getInt("UID")+"', '"+player.getLocation()+
                        "', 1, '"+config.getServerName()+"')");
            } catch (SQLException ex) {
                plugin.log.log(Level.SEVERE, null, ex);
            } catch (SecurityException ex) {
                Logger.getLogger(BlockEvents.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}