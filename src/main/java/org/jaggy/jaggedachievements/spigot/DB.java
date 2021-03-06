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
package org.jaggy.jaggedachievements.spigot;

import org.jaggy.jaggedachievements.spigot.db.DBHandler;
import org.jaggy.jaggedachievements.spigot.db.drivers.H2;
import org.jaggy.jaggedachievements.spigot.db.drivers.MySQL;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.logging.Level;

/**
 * Database class library for plugin
 *
 * @author Quirkylee
 */
public class DB {

    private Jagged plugin;
    private String DBType;
    private DBHandler dbHandler;


    public void load(Jagged p) {
        plugin = p;
        DBType = plugin.config.getDBType();
        if (DBType.toUpperCase().equals("MYSQL")) {
            dbHandler = new MySQL(p);
        } else {
            dbHandler = new H2(p);
        }
        if (db != null) {
            this.createDB();
            plugin.loaded = true;
        } else {
            plugin.log.severe("Can not connect to database. Jagged Achievements will not work!");
            plugin.loaded = false;
        }
    }

    private void createDB() throws SQLException, IOException {
        // CReate Settings table
        this.query("CREATE TABLE IF NOT EXISTS " + Prefix + "Settings (\n"
                + "ServerID INT(64) NOT NULL AUTO_INCREMENT,\n"
                + "Server VARCHAR(60),\n"
                + "Version VARCHAR(60),\n"
                + "PRIMARY KEY (ServerID)) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1;");
        
        //Create Players table
        this.query("CREATE TABLE IF NOT EXISTS " + Prefix + "Players (\n"
                + "UID INT(64) NOT NULL AUTO_INCREMENT,\n"
                + "Joined TIMESTAMP DEFAULT CURRENT_TIMESTAMP,\n"
                + "Chats INT(64) NOT NULL DEFAULT 0,\n"
                + "LastSeen TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,\n"
                + "Server VARCHAR(60),\n"
                + "Name VARCHAR(60),\n"
                + "Level INT(4) DEFAULT 0,\n"
                + "PRIMARY KEY (UID)) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1;");
                
        //Create Player Events table
        this.query("CREATE TABLE IF NOT EXISTS " + Prefix + "PlayerEvents (\n"
                + "EventID int(64) NOT NULL AUTO_INCREMENT,\n"
                + "UID VARCHAR(64) NOT NULL,\n"
                + "Location VARCHAR(255) NOT NULL,\n"
                + "EventType INT(1) NOT NULL,\n"
                + "Server VARCHAR(60),\n"
                + "eventtime TIMESTAMP DEFAULT CURRENT_TIMESTAMP,\n"
                + "PRIMARY KEY (EventID)) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1;");
        
        //Create Item Events table
        this.query("CREATE TABLE IF NOT EXISTS " + Prefix + "ItemEvents (\n"
                + "EventID int(64) NOT NULL AUTO_INCREMENT,\n"
                + "ItemID VARCHAR(30) NOT NULL,\n"
                + "UID VARCHAR(64) NOT NULL,\n"
                + "Location VARCHAR(255) NOT NULL,\n"
                + "EventType INT(1) NOT NULL,\n"
                + "Server VARCHAR(60),\n"
                + "eventtime TIMESTAMP DEFAULT CURRENT_TIMESTAMP,\n"
                + "PRIMARY KEY (EventID)) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1;");
        
        //Create Block Events table
        this.query("CREATE TABLE IF NOT EXISTS " + Prefix + "BlockEvents (\n"
                + "EventID int(64) NOT NULL AUTO_INCREMENT,\n"
                + "BlockID VARCHAR(30) NOT NULL,\n"
                + "UID VARCHAR(64) NOT NULL,\n"
                + "Location VARCHAR(255) NOT NULL,\n"
                + "EventType INT(1) NOT NULL,\n"
                + "Server VARCHAR(60),\n"
                + "eventtime TIMESTAMP DEFAULT CURRENT_TIMESTAMP,\n"
                + "PRIMARY KEY (EventID)) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1;");
        
        //Create Entity Events table
        this.query("CREATE TABLE IF NOT EXISTS " + Prefix + "EntityEvents (\n"
                + "EventID int(64) NOT NULL AUTO_INCREMENT,\n"
                + "Entity VARCHAR(50) NOT NULL,\n"
                + "UID VARCHAR(64) NOT NULL,\n"
                + "Location VARCHAR(255) DEFAULT NULL,\n"
                + "Server VARCHAR(60),\n"
                + "eventtime TIMESTAMP DEFAULT CURRENT_TIMESTAMP,\n"
                + "PRIMARY KEY (EventID)) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1;");
        
        //Create achievement table
        this.query("CREATE TABLE IF NOT EXISTS " + Prefix + "Achievements (\n"
                + "AID int(64) NOT NULL AUTO_INCREMENT,\n"
                + "UID VARCHAR(30) NOT NULL,\n"
                + "Achievement VARCHAR(200) NOT NULL,\n"
                + "Location VARCHAR(255) DEFAULT NULL,\n"
                + "EventType INT(1) NOT NULL,\n"
                + "XP INT(64) NOT NULL,\n"
                + "Server VARCHAR(60),\n"
                + "eventtime TIMESTAMP DEFAULT CURRENT_TIMESTAMP,\n"
                + "PRIMARY KEY (AID)) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1;");
    }

    public void unload() {
        dbHandler.disconnect();
    }


    /**
     * Initializes key data
     */
    public void enable() {
        try {
            if (dbHandler.tableExists(plugin.config.getPrefix() + "Settings")) {
                ResultSet data = dbHandler.query("SELECT * FROM " + plugin.config.getPrefix() + "Settings WHERE Server = '" + plugin.config.getServerName() + "'");
                if (data.next()) {
                    String version = data.getString("Version");

                    if (!plugin.getDescription().getVersion().equals(version)) {
                        //String current = plugin.getDescription().getVersion();
                        //plugin.getResource(version + "to" + current + ".sql");
                    }
                } else {
                    dbHandler.query("INSERT INTO " + plugin.config.getPrefix() + "Settings (Server, Version) VALUES ('" + plugin.config.getServerName() + "', '" + plugin.getDescription().getVersion() + "')");
                }
            }
        } catch (SQLException ex) {
            plugin.log.severe(ex.getMessage());
        }
    }

    public DBHandler getHandler() {
        return dbHandler;
    }
}
