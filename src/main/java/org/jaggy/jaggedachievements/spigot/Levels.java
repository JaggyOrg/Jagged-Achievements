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
package org.jaggy.jaggedachievements.spigot;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

/**
 * Levels handler class
 *
 * @author Quirkylee
 */
public class Levels {

    private final Jagged plugin;
    private final Config config;
    private final DB db;
    private final YamlConfiguration lconfig;
    private int xp;
    private List commands;
    private String name;

    public Levels(Jagged p) {
        plugin = p;
        config = p.config;
        lconfig = config.Levels;
        db = p.db;
    }

    /**
     * Gets the level name.
     *
     * @param level
     * @return String
     */
    public String getName(Integer level) {

        return lconfig.getString(level.toString() + ".Name");
    }

    /**
     * Gets the required xp to level.
     *
     * @param level
     * @return
     */
    public int getRequiredXP(Integer level) {
        return lconfig.getInt(level.toString() + ".RequiredXP");
    }

    /**
     * Get the list of commands to run on level up.
     *
     * @param level
     * @return
     */
    public List getCommands(Integer level) {
        return lconfig.getList(level.toString() + ".Commands");
    }

    public void checkStatus(Player player) {
        try {
            if (!player.hasPermission("jachievements.exempt")) {
                ResultSet info = db.query("SELECT * FROM " + config.getPrefix() + "Players WHERE Name = '" + player.getName() + "'");
                if (info.first()) {
                    ResultSet xpr = db.query("SELECT sum(xp) FROM " + config.getPrefix() + "Achievements WHERE UID = " + info.getInt("UID") + ";");
                    xpr.first();
                    int currentXP = xpr.getInt(1);
                    xpr.close();

                    Integer level = info.getInt("Level");
                    if (lconfig.contains(level.toString())) {
                        xp = this.getRequiredXP(level);
                        commands = this.getCommands(level);
                        name = this.getName(level);
                    }
                    int nextLevel = level + 1;
                    int nextXP = 0;
                    String nextName = null;
                    List<String> nextCommands = new ArrayList<>();
                    if (lconfig.contains("" + nextLevel)) {
                        nextXP = this.getRequiredXP(nextLevel);
                        nextName = this.getName(nextLevel);
                        nextCommands = this.getCommands(nextLevel);

                        if (currentXP >= nextXP) {
                            db.query("UPDATE " + config.getPrefix() + "Players SET Level = '" + nextLevel + "' WHERE UID = " + info.getInt("UID"));
                            for (String command : nextCommands) {
                                plugin.getServer().dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player", player.getName()));
                            }
                            plugin.log.info(player.getName() + " was leveled up to " + nextName);
                            player.sendMessage(ChatColor.BOLD + "Your have have been leveled up to " + nextName);
                            player.sendTitle(ChatColor.GOLD + "Your now a " + nextName, ChatColor.BLUE + "Keep up the hard work!", 20, 90, 20);
                        }
                    }
                }
            }
        } catch (SQLException ex) {
            plugin.log.log(Level.SEVERE, null, ex);
        }
    }
}
