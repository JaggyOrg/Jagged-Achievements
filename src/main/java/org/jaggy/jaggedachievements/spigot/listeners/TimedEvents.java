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

import java.util.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jaggy.jaggedachievements.spigot.Config;
import org.jaggy.jaggedachievements.spigot.DB;
import org.jaggy.jaggedachievements.spigot.Jagged;

/**
 * Timed events handler
 *
 * @author Quirkylee
 */
public class TimedEvents extends BukkitRunnable {

    Jagged plugin;
    Player player;
    Date Date;

    private final ResultSet result;
    private final long Joined;
    private final Config config;
    private final DB db;
    private int UID = 0;
    private String nextKey;
    private final Iterator<String> keys;

    public TimedEvents(Jagged p, PlayerJoinEvent event) {
        plugin = p;
        config = p.config;
        db = p.db;
        keys = config.Timed.getKeys(false).iterator();
        player = event.getPlayer();
        Joined = Calendar.getInstance().getTime().getTime();
        nextKey = keys.next();
        result = plugin.db.query("SELECT * FROM " + plugin.config.getPrefix() + "Players WHERE Name = '" + player.getName() + "'");
        try {
            result.first();
            UID = result.getInt("UID");
        } catch (SQLException ex) {
            plugin.log.log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        long now = Calendar.getInstance().getTime().getTime();
        long difference = now - Joined;
        long minutes = (difference / 1000) / 60;
        if (config.Timed.contains("" + minutes) && nextKey.equals(""+minutes)) {
            String title = config.Timed.getString(minutes + ".title");
            String subtitle = config.Timed.getString(minutes + ".subtitle");
            int xp = config.Timed.getInt(minutes + ".xp");
            List<String> commands = config.Timed.getStringList(minutes + ".commands");
            player.sendTitle(ChatColor.GOLD + title, ChatColor.BLUE + subtitle, 20, 90, 20);
            player.sendMessage(ChatColor.BOLD + "New Achievement: " + title);
            db.query("INSERT INTO " + config.getPrefix() + "Achievements (UID, Achievement,"
                    + " EventType, XP, Server) VALUES ("
                    + "'" + UID + "', '" + title + "', "
                    + "5, " + xp + ", '" + config.getServerName() + "')");
            nextKey = keys.next();
            if (commands.iterator().hasNext()) {
                commands.forEach((command) -> {
                    plugin.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
                });
            }
        }
    }

}
