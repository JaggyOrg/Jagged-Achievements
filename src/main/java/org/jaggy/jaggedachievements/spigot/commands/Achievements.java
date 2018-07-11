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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

/**
 *
 * @author Quirkylee
 */
public class Achievements implements CommandExecutor {

    private final Jagged plugin;
    private final Config config;
    private final DB db;
    private String name;

    public Achievements(Jagged p) {
        plugin = p;
        config = p.config;
        db = p.db;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length > 0) {
            if (args[0].toLowerCase().equals("reload")) {
                return this.reload(sender);
            } else {
                return this.past(sender);
            }
        } else {
            return this.past(sender);
        }
    }

    private boolean reload(CommandSender sender) {
        if (sender.hasPermission("jachievements.admin")) {
            config.reload();
            plugin.cmds.sendMessage(sender, ChatColor.GREEN + "Jagged Achievements Reloaded!");
        }
        return true;
    }

    private boolean past(CommandSender sender) {
        if (sender.hasPermission("jachievements.user") && sender instanceof Player) {
            ResultSet data = db.query("SELECT UID FROM " + config.getPrefix() + "Players WHERE Name = '" + sender.getName() + "'");
            try {
                if (data.first()) {
                    ResultSet result = db.query("SELECT * FROM " + config.getPrefix() + "Achievements WHERE UID ='" + data.getInt(1) 
                            + "' ORDER BY eventtime DESC LIMIT 10");
                    plugin.cmds.sendMessage(sender, ChatColor.AQUA + "" + "Your Past 10 Achievements:");
                    while (result.next()) {
                        String type = "";
                        switch (result.getInt("EventType")) {
                            case 1:
                                type = "Block Place";
                                break;
                            case 2:
                                type = "Block Break";
                                break;
                            case 3:
                                type = "Joins";
                                break;
                            case 4:
                                type = "Chat";
                                break;
                            case 5:
                                type = "Time";
                                break;
                            case 6:
                                type = "Entity";
                                break;
                            default:
                                break;
                        }

                        plugin.cmds.sendMessage(sender, ChatColor.GOLD + "Type: " + ChatColor.RESET + type
                                + ChatColor.GOLD + " Name: " + ChatColor.RESET + result.getString("Achievement"));
                    }
                }
            } catch (SQLException ex) {
                plugin.log.log(Level.SEVERE, null, ex);
            }
        }
        return true;
    }
}
