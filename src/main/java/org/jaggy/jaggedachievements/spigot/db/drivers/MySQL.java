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

import java.io.IOException;
import java.sql.*;
import java.util.logging.Level;

public class MySQL extends DBHandler {
    private final Jagged plugin;
    private final String MysqlHost;
    private final String MysqlUser;
    private final String DBName;
    private final String MysqlPass;
    private final int MysqlPort;
    private final String Prefix;
    private final boolean useSSL;
    private Connection db;

    public MySQL(Jagged jagged) {
        plugin = jagged;
        MysqlHost = plugin.config.getMysqlHost();
        MysqlUser = plugin.config.getMysqlUser();
        DBName = plugin.config.getDBName();
        MysqlPass = plugin.config.getMysqlPass();
        MysqlPort = plugin.config.getMysqlPort();
        Prefix = plugin.config.getPrefix();
        useSSL = plugin.config.useSSL();
    }

    @Override
    public void createDB() throws SQLException, IOException {
        // Create Settings table
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
                + "PRIMARY KEY (EventID)) ENGINE=InnoDB DEFAULT CHARSET=latain1 AUTO_INCREMENT=1;");

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
                + "Gold INT(64) DEFAULT 0,\n"
                + "XP INT(64) NOT NULL,\n"
                + "Server VARCHAR(60),\n"
                + "eventtime TIMESTAMP DEFAULT CURRENT_TIMESTAMP,\n"
                + "PRIMARY KEY (AID)) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1;");
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
            Class.forName("com.mysql.jdbc.Driver");
            // Setup the connection with the DB
            plugin.log.info("Connecting to database...");
            db = DriverManager.getConnection("jdbc:mysql://"
                    + MysqlHost + ":" + MysqlPort + "/" + DBName + "?"
                    + "user=" + MysqlUser + "&useSSL=" + useSSL + "&password=" + MysqlPass);
        } catch (ClassNotFoundException | SQLException ex) {
            plugin.log.severe(ex.getMessage());
        }
        if (db != null) {
            try {
                this.createDB();
            } catch (SQLException | IOException e) {
                e.printStackTrace();
            }
            plugin.loaded = true;
        } else {
            plugin.log.severe("Can not connect to database. Jagged Achievements will not work!");
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
