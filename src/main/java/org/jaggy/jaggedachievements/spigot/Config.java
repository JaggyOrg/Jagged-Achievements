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

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * Standard config and defaults class
 *
 * @author Quirkylee
 */
public class Config {

    /**
     * Container for the config.yml
     */
    FileConfiguration config;
    /**
     * Reference to parent class
     */
    Jagged plugin;

    /**
     * Tells if it is a server acting without bungeecord.
     */
    public boolean defaultStandAlone = true;
    /**
     * Sets the default database type. MYSQL and H2 is supported.
     */
    public String defaultDBType = "H2";
    /**
     * Sets the default database Location if using H2.
     */
    public String defaultDBLocation = "plugins/JaggedAchievements/db.h2";
    /**
     * Sets the default Mysql Host.
     */
    public String defaultMysqlHost = "localhost";
    /**
     * Sets the default Mysql Username.
     */
    public String defaultMysqlUser = "root";
    /**
     * Sets the default Mysql Port.
     */
    public String defaultMysqlPass = "";
    /**
     * Set the default Mysql port
     */
    public int defaultMysqlPort = 3306;
    /**
     * Sets the default Database name
     */
    public String defaultDBName = "achievements";
    /**
     * Sets the default Table Prefix
     */
    public String defaultDBPrefix = "achievement_";
    /**
     * Sets the default Server Name
     */
    public String defaultServerName = "main";
    /**
     * Container for the Joins.yml
     */
    public YamlConfiguration Joins;
    /**
     * Container for the BlockBreak.yml
     */
    public YamlConfiguration Breaks;
    /**
     * Container for the BlockPlace.yml
     */
    public YamlConfiguration Place;
    /**
     * Container for the Chat.yml
     */
    public YamlConfiguration Chat;
    /**
     * Container for the Timed.yml
     */
    public YamlConfiguration Timed;

    /**
     * Gets if the server is a stand alone server.
     *
     * @return Boolean
     */
    public boolean getStandAlone() {
        return config.getBoolean("StandAlone", defaultStandAlone);
    }

    /**
     * Gets the type of database
     *
     * @return String
     */
    public String getDBType() {
        return config.getString("DatabaseType", defaultDBType);
    }

    /**
     * Gets the location of database if using H2
     *
     * @return String
     */
    public String getDBLocation() {
        return config.getString("DatabaseLocation", defaultDBLocation);
    }

    /**
     * Gets the location of database if using H2
     *
     * @return String
     */
    public String getDBName() {
        return config.getString("DatabaseName", defaultDBName);
    }

    /**
     * Gets the Mysql host.
     *
     * @return String
     */
    public String getMysqlHost() {
        return config.getString("MysqlHost", defaultMysqlHost);
    }

    /**
     * Gets the Mysql username.
     *
     * @return String
     */
    public String getMysqlUser() {
        return config.getString("MysqlUser", defaultMysqlUser);
    }

    /**
     * Gets the Mysql password.
     *
     * @return String
     */
    public String getMysqlPass() {
        return config.getString("MysqlPass", defaultMysqlPass);
    }

    /**
     * Gets the Server Name.
     *
     * @return String
     */
    public String getServerName() {
        return config.getString("ServerName", defaultServerName);
    }

    /**
     * Gets the Mysql port.
     *
     * @return String
     */
    public int getMysqlPort() {
        return config.getInt("MysqlPort", defaultMysqlPort);
    }

    /**
     * Gets the table prefix
     *
     * @return String
     */
    public String getPrefix() {
        return config.getString("DBPrefix", defaultDBPrefix);
    }

    /**
     * Loads the config.yml
     *
     * @param p passes the parent class to this class
     */
    public void load(Jagged p) {
        plugin = p;

        File File = new File("plugins/JaggedAchievements/config.yml");
        if (File.exists()) { // config.yml exists? use it
            config = plugin.getConfig();
        } else { // neither exists yet (new installation), create and use it
            plugin.saveDefaultConfig();
            config = plugin.getConfig();
        }
        File = new File(plugin.getDataFolder(), "achievements/Joins.yml");

        //Check and save joins file
        Joins = new YamlConfiguration();
        if (!File.exists()) {
            plugin.saveResource("achievements/Joins.yml", false);
        }
        try {
            File = new File(plugin.getDataFolder(), "achievements/Joins.yml");
            Joins.load(File);
        } catch (IOException | InvalidConfigurationException ex) {
            plugin.log.log(Level.SEVERE, null, ex);
        }
        
        //Check and save breaks file
        Breaks = new YamlConfiguration();
        File = new File(plugin.getDataFolder(), "achievements/BlockBreak.yml");
        if (!File.exists()) {
            plugin.saveResource("achievements/BlockBreak.yml", false);
        }
        try {
            File = new File(plugin.getDataFolder(), "achievements/BlockBreak.yml");
            Breaks.load(File);
        } catch (IOException | InvalidConfigurationException ex) {
            plugin.log.log(Level.SEVERE, null, ex);
        }
        
        //Check and save breaks file
        Place = new YamlConfiguration();
        File = new File(plugin.getDataFolder(), "achievements/BlockPlace.yml");
        if (!File.exists()) {
            plugin.saveResource("achievements/BlockPlace.yml", false);
        }
        try {
            File = new File(plugin.getDataFolder(), "achievements/BlockPlace.yml");
            Place.load(File);
        } catch (IOException | InvalidConfigurationException ex) {
            plugin.log.log(Level.SEVERE, null, ex);
        }
        
        //Check and save chat file
        Chat = new YamlConfiguration();
        File = new File(plugin.getDataFolder(), "achievements/Chat.yml");
        if (!File.exists()) {
            plugin.saveResource("achievements/Chat.yml", false);
        }
        try {
            File = new File(plugin.getDataFolder(), "achievements/Chat.yml");
            Chat.load(File);
        } catch (IOException | InvalidConfigurationException ex) {
            plugin.log.log(Level.SEVERE, null, ex);
        }
        
        //Check and save timed file
        Timed = new YamlConfiguration();
        File = new File(plugin.getDataFolder(), "achievements/Timed.yml");
        if (!File.exists()) {
            plugin.saveResource("achievements/Timed.yml", false);
        }
        try {
            File = new File(plugin.getDataFolder(), "achievements/Timed.yml");
            Timed.load(File);
        } catch (IOException | InvalidConfigurationException ex) {
            plugin.log.log(Level.SEVERE, null, ex);
        }
    }
}
