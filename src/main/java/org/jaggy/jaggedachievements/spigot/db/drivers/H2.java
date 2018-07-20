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

package org.jaggy.jaggedachievements.spigot.db.drivers;

import org.jaggy.jaggedachievements.spigot.Jagged;
import org.jaggy.jaggedachievements.spigot.db.DBHandler;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.logging.Level;

public class H2 extends DBHandler {
    private final Jagged plugin;
    private final String DBLocation;
    private final String DBName;
    private final String Prefix;
    private Connection db;

    public H2(Jagged jagged) {
        plugin = jagged;
        DBLocation = plugin.config.getDBLocation();
        DBName = plugin.config.getDBName();
        Prefix = plugin.config.getPrefix();
    }

    @Override
    public void createDB() throws SQLException, IOException {
        // Create Settings table
        this.query("CREATE TABLE IF NOT EXISTS " + Prefix + "Settings (\n"
                + "ServerID INT NOT NULL AUTO_INCREMENT PRIMARY KEY,\n"
                + "Server VARCHAR(60),\n"
                + "Version VARCHAR(60))\n"
                + " ENGINE=InnoDB DEFAULT CHARSET=UTF8;");
        if (tableExists(Prefix + "Settings")) {
            plugin.log.info("Setting up Settings Table.");
        }

        //Create Players table
        this.query("CREATE TABLE IF NOT EXISTS " + Prefix + "Players (\n"
                + "UID INT NOT NULL AUTO_INCREMENT  PRIMARY KEY,\n"
                + "Joined TIMESTAMP DEFAULT CURRENT_TIMESTAMP,\n"
                + "Chats INT NOT NULL DEFAULT 0,\n"
                + "LastSeen TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,\n"
                + "Server VARCHAR(60),\n"
                + "Name VARCHAR(60),\n"
                + "Level INT DEFAULT 0)\n"
                + " ENGINE=InnoDB DEFAULT CHARSET=UTF8;");

        //Create Player Events table
        this.query("CREATE TABLE IF NOT EXISTS " + Prefix + "PlayerEvents (\n"
                + "EventID INT NOT NULL AUTO_INCREMENT PRIMARY KEY,\n"
                + "UID VARCHAR(64) NOT NULL,\n"
                + "Location VARCHAR(255) NOT NULL,\n"
                + "EventType INT NOT NULL,\n"
                + "Server VARCHAR(60),\n"
                + "eventtime TIMESTAMP DEFAULT CURRENT_TIMESTAMP)\n"
                + " ENGINE=InnoDB DEFAULT CHARSET=UTF8;");

        //Create Item Events table
        this.query("CREATE TABLE IF NOT EXISTS " + Prefix + "ItemEvents (\n"
                + "EventID INT NOT NULL AUTO_INCREMENT PRIMARY KEY,\n"
                + "ItemID VARCHAR(30) NOT NULL,\n"
                + "UID VARCHAR(64) NOT NULL,\n"
                + "Location VARCHAR(255) NOT NULL,\n"
                + "EventType INT NOT NULL,\n"
                + "Server VARCHAR(60),\n"
                + "eventtime TIMESTAMP DEFAULT CURRENT_TIMESTAMP)\n"
                + " ENGINE=InnoDB DEFAULT CHARSET=UTF8;");

        //Create Block Events table
        this.query("CREATE TABLE IF NOT EXISTS " + Prefix + "BlockEvents (\n"
                + "EventID INT NOT NULL AUTO_INCREMENT PRIMARY KEY,\n"
                + "BlockID VARCHAR(30) NOT NULL,\n"
                + "UID VARCHAR(64) NOT NULL,\n"
                + "Location VARCHAR(255) NOT NULL,\n"
                + "EventType INT NOT NULL,\n"
                + "Server VARCHAR(60),\n"
                + "eventtime TIMESTAMP DEFAULT CURRENT_TIMESTAMP)\n"
                + " ENGINE=InnoDB DEFAULT CHARSET=UTF8;");

        //Create Entity Events table
        this.query("CREATE TABLE IF NOT EXISTS " + Prefix + "EntityEvents (\n"
                + "EventID INT NOT NULL AUTO_INCREMENT PRIMARY KEY,\n"
                + "Entity VARCHAR(50) NOT NULL,\n"
                + "UID VARCHAR(64) NOT NULL,\n"
                + "Location VARCHAR(255) DEFAULT NULL,\n"
                + "Server VARCHAR(60),\n"
                + "eventtime TIMESTAMP DEFAULT CURRENT_TIMESTAMP)\n"
                + " ENGINE=InnoDB DEFAULT CHARSET=UTF8;");

        //Create achievement table
        this.query("CREATE TABLE IF NOT EXISTS " + Prefix + "Achievements (\n"
                + "AID INT NOT NULL AUTO_INCREMENT PRIMARY KEY,\n"
                + "UID VARCHAR(30) NOT NULL,\n"
                + "Achievement VARCHAR(200) NOT NULL,\n"
                + "Location VARCHAR(255) DEFAULT NULL,\n"
                + "EventType INT NOT NULL,\n"
                + "Gold INT(64) DEFAULT 0,\n"
                + "XP INT(64) NOT NULL,\n"
                + "Server VARCHAR(60),\n"
                + "eventtime TIMESTAMP DEFAULT CURRENT_TIMESTAMP)\n"
                + " ENGINE=InnoDB DEFAULT CHARSET=UTF8;");
    }

    @Override
    public ResultSet query(String query) {
        ResultSet result = null;
        try {
            Statement statement = db.createStatement();
            String[] type = query.split(" ", 2);
            if (type[0].equalsIgnoreCase("SELECT")) {
                result = statement.executeQuery(query);
            } else {
                statement.executeUpdate(query);
            }
        } catch (SQLException ex) {
            plugin.log.severe(ex.getMessage());
        }
        return result;
    }

    @Override
    public void connect() {
        try {
            plugin.log.info("Opening database...");
            File file;
            String loc;
            //test to see if it exists and set the location to look
            if(DBLocation.equals("plugins/JaggedAchievements/")) {
                file = new File(plugin.getDataFolder(), DBName);
                loc = "./"+DBLocation+ DBName;
            } else {
                file = new File(DBLocation, DBName);
                loc = DBLocation+File.separator+DBName;
            }
            Class.forName("org.h2.Driver");
            db = DriverManager.
                    getConnection("jdbc:h2:file:"+loc, "sa", "");
        } catch (ClassNotFoundException | SQLException ex) {
            plugin.log.log(Level.SEVERE,ex.getMessage(),ex);
        }
        if (db != null) {
            try {
                this.createDB();
            } catch (SQLException | IOException e) {
                plugin.log.log(Level.SEVERE,e.getMessage());
            }
            plugin.loaded = true;
        } else {
            plugin.log.severe("Can not open the database. Jagged Achievements will not work!");
            plugin.loaded = false;
        }
    }

    @Override
    public void disconnect() {
        try {
            db.close();
        } catch (SQLException ex) {
            plugin.log.log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public boolean tableExists(String table) {
        try {
            DatabaseMetaData dbm = db.getMetaData();
            ResultSet tables = dbm.getTables(null, null, table, null);
            return tables.next();
        } catch (SQLException ex) {
            plugin.log.severe(ex.getMessage());
        }
        return false;
    }
}
