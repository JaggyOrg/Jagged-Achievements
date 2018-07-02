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
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.jaggy.jaggedachievements.spigot.Config;
import org.jaggy.jaggedachievements.spigot.DB;
import org.jaggy.jaggedachievements.spigot.Jagged;

/**
 * Entity events handler
 *
 * @author Quirkylee
 */
public class EntityEvents implements Listener {

    private final Jagged plugin;
    private final DB db;
    private final Config config;

    public EntityEvents(Jagged p) {
        plugin = p;
        db = plugin.db;
        config = plugin.config;
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {

        LivingEntity entity = event.getEntity();
        Player player = entity.getKiller();
        EntityType type = entity.getType();
        if(player instanceof Player) {
        try {
            ResultSet data = db.query("SELECT * FROM " + config.getPrefix() + "Players WHERE Name ='" + player.getName() + "'");
            data.first();
            db.query("INSERT INTO " + config.getPrefix() + "EntityEvents (Entity, UID, Location, Server) "
                    + "VALUES ('" + type.toString() + "', '" + data.getInt("UID") + "', '" + player.getLocation()
                    + "', '" + config.getServerName() + "')");
            ResultSet rows = db.query("SELECT COUNT(*) FROM " + config.getPrefix()
                    + "EntityEvents WHERE UID = '" + data.getInt("UID") + "' AND Entity = '" + type.toString() + "'");
           

            if (rows.next()) {
                int count = rows.getInt(1);
                if (config.Entity.contains(type + "." + count)) {
                    String title = config.Entity.getString(type.toString() + "." + count + ".title");
                    String subtitle = config.Entity.getString(type.toString() + "." + count + ".subtitle");
                    int xp = config.Entity.getInt(type.toString() + "." + count + ".xp");
                    List<String> commands = config.Entity.getStringList(type.toString() + "." + count + ".commands");
                    player.sendTitle(ChatColor.GOLD + title, ChatColor.BLUE + subtitle, 20, 90, 20);
                    player.sendMessage(ChatColor.BOLD + "New Achievement: " + title);
                    db.query("INSERT INTO " + config.getPrefix() + "Achievements (UID, Achievement, Location,"
                            + " EventType, XP, Server) VALUES ("
                            + "'" + data.getInt("UID") + "', '" + title + "', '" + player.getLocation() + "', "
                            + "6, " + xp + ", '" + config.getServerName() + "')");

                    if (commands.iterator().hasNext()) {
                        commands.forEach((command) -> {
                            plugin.getServer().dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player", player.getName()));
                        });
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
