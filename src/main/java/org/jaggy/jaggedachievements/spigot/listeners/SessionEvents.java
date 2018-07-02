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
import java.util.List;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;
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
    BukkitTask task;
    /**
     * Handle and log players signing in
     * @param event 
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        
        Player player = event.getPlayer();
        if(plugin.loaded) {
            // Create the task and schedule
            task = new TimedEvents(this.plugin, event).runTaskTimer(this.plugin, 10, 20);
            
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
                ResultSet rows  = db.query("SELECT COUNT(*) FROM "+config.getPrefix()+
                        "PlayerEvents WHERE UID = '"+data.getInt("UID")+"' AND EventType = 0");
                
                if(rows.first()) {
                    
                    Integer count = rows.getInt(1);
                    if(config.Joins.contains(count.toString())) {
                        String title = config.Joins.getString(+count+".title");
                        String subtitle = config.Joins.getString(count+".subtitle");
                        int xp = config.Chat.getInt(count+".xp");
                        List<String> commands = config.Joins.getStringList(count+".commands");
                        
                        player.sendTitle(ChatColor.GOLD+title, ChatColor.BLUE+subtitle, 20, 90, 20);
                        player.sendMessage(ChatColor.BOLD+"New Achievement: "+title);
                        db.query("INSERT INTO "+config.getPrefix()+"Achievements (UID, Achievement, Location,"
                                +" EventType, XP, Server) VALUES ("
                                +"'"+data.getInt("UID")+"', '"+title+"', '"+player.getLocation()+"', "
                                +"3, "+xp+", '"+config.getServerName()+"')");
                        if(commands.iterator().hasNext()) {
                            for(String command: commands) {
                                plugin.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
                            }
                        }
                        plugin.levels.checkStatus(player);
                    }
                }
            } catch (SQLException ex) {
                plugin.log.log(Level.SEVERE, null, ex);
            }
        }
    }
    
    /**
     * Handle and log players signing out
     * @param event 
     */
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
                task.cancel();
            } catch (SQLException ex) {
                plugin.log.log(Level.SEVERE, null, ex);
            }
        }
    }
    
    /**
     * Handle chat events
     * @param event 
     */
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if(plugin.loaded) {
            Player player = event.getPlayer();
            
            try {
                ResultSet data = db.query("SELECT * FROM "+config.getPrefix()+"Players WHERE Name ='"+player.getName()+"'");
                if(data.first()) {
                    Integer count = data.getInt("Chats")+1;
                    db.query("UPDATE "+config.getPrefix()+"Players SET Chats = '"+count+"' WHERE UID = "+data.getInt("UID"));
                    if(config.Chat.contains(count.toString())) {
                        String title = config.Chat.getString(+count+".title");
                        String subtitle = config.Chat.getString(count+".subtitle");
                        int xp = config.Chat.getInt(count+".xp");
                        List<String> commands = config.Chat.getStringList(count+".commands");
                        
                        player.sendTitle(ChatColor.GOLD+title, ChatColor.BLUE+subtitle, 20, 90, 20);
                        player.sendMessage(ChatColor.BOLD+"New Achievement: "+title);
                        db.query("INSERT INTO "+config.getPrefix()+"Achievements (UID, Achievement, Location,"
                                +" EventType, XP, Server) VALUES ("
                                +"'"+data.getInt("UID")+"', '"+title+"', '"+player.getLocation()+"', "
                                +"3, "+xp+", '"+config.getServerName()+"')");
                        if(commands.iterator().hasNext()) {
                            for(String command: commands) {
                                plugin.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
                            }
                        }
                        plugin.levels.checkStatus(player);
                    }
                }
            } catch (SQLException ex) {
                plugin.log.log(Level.SEVERE, null, ex);
            }
        }
    }
}
