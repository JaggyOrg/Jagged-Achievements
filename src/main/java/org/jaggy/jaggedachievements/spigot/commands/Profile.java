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
package org.jaggy.jaggedachievements.spigot.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jaggy.jaggedachievements.spigot.Config;
import org.jaggy.jaggedachievements.spigot.DB;
import org.jaggy.jaggedachievements.spigot.Jagged;
import org.jaggy.jaggedachievements.spigot.db.DBHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

/**
 *
 * @author Quirkylee
 */
public class Profile implements CommandExecutor {

    private final Jagged plugin;
    private final Config config;
    private final DBHandler db;
    private String name;
    public Profile(Jagged p) {
        plugin = p;
        config = p.config;
        db = p.db.getHandler();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(sender.hasPermission("jachievements.user")) {
            try {
               
                ResultSet result;
                if(args.length == 1) {
                    result = db.query("SELECT * FROM "+config.getPrefix()+"Players WHERE Name = '"+args[0]+"'");
                    name = args[0];
                } else {
                    if(sender instanceof Player) {
                        result = db.query("SELECT * FROM "+config.getPrefix()+"Players WHERE Name = '"+sender.getName()+"'");
                        name = sender.getName();
                    } else {
                        return false;
                    }
                }
                if(result.first()) {
                    ResultSet blocksr = db.query("SELECT COUNT(*) FROM "+config.getPrefix()+"BlockEvents WHERE EventType = 0 AND UID = '"+result.getInt("UID")+"'");
                    blocksr.first();
                    int blockPlaced = blocksr.getInt(1);
                    blocksr = db.query("SELECT COUNT(*) FROM "+config.getPrefix()+"BlockEvents WHERE EventType = 1 AND UID = '"+result.getInt("UID")+"'");
                    blocksr.first();
                    int blockBroke = blocksr.getInt(1);
                    
                    
                    ResultSet xpr = db.query("SELECT sum(xp) FROM "+config.getPrefix()+"Achievements WHERE UID = '"+result.getInt("UID")+"'");
                    xpr.first();
                    int xp = xpr.getInt(1);
                    
                    
                    ResultSet entitiesr = db.query("SELECT COUNT(*) FROM "+config.getPrefix()+"EntityEvents WHERE UID = '"+result.getInt("UID")+"'");
                    entitiesr.first();
                    int entities = entitiesr.getInt(1);
                    
                    
                    ResultSet playerEvents = db.query("SELECT COUNT(*) FROM "+config.getPrefix()+"PlayerEvents WHERE EventType = 0 AND UID = '"+result.getInt("UID")+"'");
                    playerEvents.first();
                    int joins = playerEvents.getInt(1);
                    
                    String levelName = plugin.levels.getName(result.getInt("Level"));
                    
                    plugin.cmds.sendMessage(sender, ChatColor.GOLD+""+ChatColor.BOLD+name);
                    plugin.cmds.sendMessage(sender, ChatColor.AQUA+""+ChatColor.BOLD+"Blocks Placed: "+ChatColor.RESET+blockPlaced);
                    plugin.cmds.sendMessage(sender, ChatColor.AQUA+""+ChatColor.BOLD+"Blocks Broke: "+ChatColor.RESET+blockBroke);
                    plugin.cmds.sendMessage(sender, ChatColor.AQUA+""+ChatColor.BOLD+"XP: "+ChatColor.RESET+xp);
                    plugin.cmds.sendMessage(sender, ChatColor.AQUA+""+ChatColor.BOLD+"Current Level: "
                            +ChatColor.RESET+result.getInt("Level")+" "+levelName);
                    plugin.cmds.sendMessage(sender, ChatColor.AQUA+""+ChatColor.BOLD+"Entities Killed: "+ChatColor.RESET+entities);
                    plugin.cmds.sendMessage(sender, ChatColor.AQUA+""+ChatColor.BOLD+"Joins: "+ChatColor.RESET+joins);
                } else {
                    plugin.cmds.sendMessage(sender, ChatColor.GOLD+"Player Not Found!");
                }
                return true;
            } catch (SQLException ex) {
                plugin.log.log(Level.SEVERE, null, ex);
            }
        }
        return false;
    }
    
}
