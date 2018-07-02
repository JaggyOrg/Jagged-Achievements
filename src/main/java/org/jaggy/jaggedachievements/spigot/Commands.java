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

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jaggy.jaggedachievements.spigot.commands.Achievements;
import org.jaggy.jaggedachievements.spigot.commands.Profile;
import org.jaggy.jaggedachievements.spigot.commands.Required;

/**
 * Commands registration class
 * @author Quirkylee
 */
public class Commands {

    private final Jagged plugin;
    private CommandExecutor New;
    public Commands(Jagged p) {
        plugin = p;
        plugin.getCommand("profile").setExecutor(new Profile(plugin));
        plugin.getCommand("achievements").setExecutor(new Achievements(plugin));
        plugin.getCommand("required").setExecutor(new Required(plugin));
    }
    
    public void sendMessage(CommandSender sender, String str) {
        if(sender instanceof Player) {
            sender.sendMessage(str);
        } else {
            plugin.log.info(str);
        }
    }

}
