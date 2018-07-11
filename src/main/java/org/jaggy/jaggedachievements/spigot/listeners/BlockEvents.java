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
package org.jaggy.jaggedachievements.spigot.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.jaggy.jaggedachievements.spigot.Config;
import org.jaggy.jaggedachievements.spigot.DB;
import org.jaggy.jaggedachievements.spigot.Jagged;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;

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
            Block block = event.getBlockPlaced();
            try {
                Player player = event.getPlayer();
                ResultSet data = db.query("SELECT * FROM "+config.getPrefix()+"Players WHERE Name ='"+player.getName()+"'");
                data.first();
                db.query("INSERT INTO "+config.getPrefix()+"BlockEvents (BlockID, UID, Location, EventType, Server) "+
                        "VALUES ('"+block.getType()+"', '"+data.getInt("UID")+"', '"+player.getLocation()+
                        "', 0, '"+config.getServerName()+"')");
                ResultSet rows = db.query("SELECT COUNT(*) FROM "+config.getPrefix()+
                        "BlockEvents WHERE UID = '"+data.getInt("UID")+"' AND EventType = 0 AND BlockID = '"+block.getType()+"'");
                if(rows.first()) {
                    int count = rows.getInt(1);
                    if(config.Place.contains(block.getType().toString()+"."+count)) {
                        String title = config.Place.getString(block.getType().toString()+"."+count+".title");
                        String subtitle = config.Place.getString(block.getType().toString()+"."+count+".subtitle");
                        int xp = config.Place.getInt(block.getType().toString()+"."+count+".xp");
                        List<String> commands = config.Place.getStringList(block.getType().toString()+"."+count+".commands");
                        player.sendTitle(ChatColor.GOLD+title, ChatColor.BLUE+subtitle, 20, 90, 20);
                        player.sendMessage(ChatColor.BOLD+"New Achievement: "+title);
                        db.query("INSERT INTO "+config.getPrefix()+"Achievements (UID, Achievement, Location,"
                                +" EventType, XP, Server) VALUES ("
                                +"'"+data.getInt("UID")+"', '"+title+"', '"+player.getLocation()+"', "
                                +"1, "+xp+", '"+config.getServerName()+"')");
                        
                        
                        if(commands.iterator().hasNext()) {
                            for(String command: commands) {
                                plugin.getServer().dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player", player.getName()));
                            }
                        }
                        plugin.levels.checkStatus(player);
                    }
                }
                
            } catch (SQLException | SecurityException ex) {
                plugin.log.log(Level.SEVERE, null, ex);
            }
        }
    }
    @EventHandler
    void OnBlockBreak(BlockBreakEvent event) {
        if(plugin.loaded) {
            Block block = event.getBlock();
            try {
                Player player = event.getPlayer();
                ResultSet data = db.query("SELECT * FROM "+config.getPrefix()+"Players WHERE Name ='"+player.getName()+"'");
                data.first();
                db.query("INSERT INTO "+config.getPrefix()+"BlockEvents (BlockID, UID, Location, EventType, Server) "+
                        "VALUES ('"+block.getType()+"', '"+data.getInt("UID")+"', '"+player.getLocation()+
                        "', 1, '"+config.getServerName()+"')");
                
                ResultSet rows = db.query("SELECT COUNT(*) FROM "+config.getPrefix()+
                        "BlockEvents WHERE UID = '"+data.getInt("UID")+"' AND EventType = 1 AND BlockID = '"+block.getType()+"'");
                
                if(rows.next()) {
                    int count = rows.getInt(1);
                    if(config.Breaks.contains(block.getType()+"."+count)) {
                        String title = config.Breaks.getString(block.getType().toString()+"."+count+".title");
                        String subtitle = config.Breaks.getString(block.getType().toString()+"."+count+".subtitle");
                        int xp = config.Breaks.getInt(block.getType().toString()+"."+count+".xp");
                        List<String> commands = config.Breaks.getStringList(block.getType().toString()+"."+count+".commands");
                        player.sendTitle(ChatColor.GOLD+title, ChatColor.BLUE+subtitle, 20, 90, 20);
                        player.sendMessage(ChatColor.BOLD+"New Achievement: "+title);
                        db.query("INSERT INTO "+config.getPrefix()+"Achievements (UID, Achievement, Location,"
                                +" EventType, XP, Server) VALUES ("
                                +"'"+data.getInt("UID")+"', '"+title+"', '"+player.getLocation()+"', "
                                +"2, "+xp+", '"+config.getServerName()+"')");
                        
                        
                        if(commands.iterator().hasNext()) {
                            commands.forEach((command) -> {
                                plugin.getServer().dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player", player.getName()));
                            });
                        }
                    }
                    plugin.levels.checkStatus(player);
                }
                
            } catch (SQLException | SecurityException ex) {
                plugin.log.log(Level.SEVERE, null, ex);
            }
        }
    }
}
