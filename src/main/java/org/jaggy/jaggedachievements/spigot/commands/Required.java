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
package org.jaggy.jaggedachievements.spigot.commands;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jaggy.jaggedachievements.spigot.Config;
import org.jaggy.jaggedachievements.spigot.DB;
import org.jaggy.jaggedachievements.spigot.Jagged;

/**
 *
 * @author Quirkylee
 */
public class Required implements CommandExecutor {

    private final Jagged plugin;
    private final Config config;
    private final DB db;

    public Required(Jagged p) {
        plugin = p;
        config = p.config;
        db = p.db;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender.hasPermission("jachievements.user") && sender instanceof Player) {
            ResultSet data = db.query("SELECT * FROM " + config.getPrefix() + "Players WHERE Name = '" + sender.getName() + "'");
            try {
                if(data.first()) {
                    ResultSet result = db.query("SELECT SUM(XP) FROM " + config.getPrefix() 
                            +"Achievements WHERE UID ='" + data.getInt(1) + "'");
                    result.next();
                    int xp = result.getInt(1);
                    int level = data.getInt("Level")+1;
                    int nextLevel = config.Levels.getInt(level+".RequiredXP");
                    plugin.cmds.sendMessage(sender, ChatColor.GOLD+"Your XP: "
                            +ChatColor.RESET+xp+" "+ChatColor.GOLD+"Required XP: "
                            +ChatColor.RESET+nextLevel);
                }
            } catch (SQLException ex) {
                plugin.log.log(Level.SEVERE, null, ex);
            }
            return true;
        }
        return false;
    }
}
