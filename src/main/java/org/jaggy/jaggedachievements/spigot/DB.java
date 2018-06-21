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

import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

/**
 * Database class library for plugin
 *
 * @author Quirkylee
 */
public class DB {

    private Jagged plugin;
    private String DBType;
    private String MysqlHost;
    private String DBLocation;
    private String MysqlUser;
    private String MysqlPass;
    private Connection db;
    private String DBName;
    private int MysqlPort;
    private String Prefix;
    private Object settings;

    public void load(Jagged p) {
        plugin = p;
        DBType = plugin.config.getDBType();
        MysqlHost = plugin.config.getMysqlHost();
        DBLocation = plugin.config.getDBLocation();
        MysqlUser = plugin.config.getMysqlUser();
        DBName = plugin.config.getDBName();
        MysqlPass = plugin.config.getMysqlPass();
        MysqlPort = plugin.config.getMysqlPort();
        Prefix = plugin.config.getPrefix();
        settings = null;

        try {
            Class.forName("com.mysql.jdbc.Driver");
            // Setup the connection with the DB
            plugin.log.info("Connecting to database...");
            db = DriverManager.getConnection("jdbc:mysql://"
                    + MysqlHost + ":" + MysqlPort + "/" + DBName + "?"
                    + "user=" + MysqlUser + "&useSSL=false&password=" + MysqlPass);
        } catch (ClassNotFoundException | SQLException ex) {
            plugin.log.severe(ex.getMessage());
        }
        if (db != null) {
            try {
                this.createDB();
                plugin.loaded = true;
            } catch (SQLException | IOException ex) {
                plugin.loaded = false;
                plugin.log.log(Level.SEVERE, null, ex);
            }
        } else {
            plugin.log.severe("Can not connect to database. Jagged Achievements will not work!");
            plugin.loaded = false;
        }
    }

    private void createDB() throws SQLException, IOException {
        this.query("CREATE TABLE IF NOT EXISTS " + Prefix + "Settings (\n"
                + "ServerID INT(64) NOT NULL AUTO_INCREMENT,\n"
                + "Server VARCHAR(60),\n"
                + "Version VARCHAR(60),\n"
                + "PRIMARY KEY (ServerID)) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1;");
        this.query("CREATE TABLE IF NOT EXISTS " + Prefix + "Users (\n"
                + "UUID VARCHAR(40) NOT NULL,\n"
                + "Joined TIMESTAMP DEFAULT CURRENT_TIMESTAMP,\n"
                + "LastSeen TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,\n"
                + "Server VARCHAR(60),\n"
                + "PRIMARY KEY (UUID)) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1;");
        this.query("CREATE TABLE IF NOT EXISTS " + Prefix + "ItemEvents (\n"
                + "EventID int(64) NOT NULL AUTO_INCREMENT,\n"
                + "ItemID VARCHAR(30) NOT NULL,\n"
                + "UUID VARCHAR(40) NOT NULL,\n"
                + "Location VARCHAR(60) NOT NULL,\n"
                + "EventType INT(1) NOT NULL,\n"
                + "Server VARCHAR(60),\n"
                + "eventtime TIMESTAMP NOT NULL,\n"
                + "PRIMARY KEY (EventID)) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1;");
        this.query("CREATE TABLE IF NOT EXISTS " + Prefix + "BlockEvents (\n"
                + "EventID int(64) NOT NULL AUTO_INCREMENT,\n"
                + "BlockID VARCHAR(30) NOT NULL,\n"
                + "UUID VARCHAR(40) NOT NULL,\n"
                + "Location VARCHAR(60) NOT NULL,\n"
                + "EventType INT(1) NOT NULL,\n"
                + "Server VARCHAR(60),\n"
                + "eventtime TIMESTAMP NOT NULL,\n"
                + "PRIMARY KEY (EventID)) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1;");
    }

    public void unload() {
        if (plugin.loaded == true) {
            try {
                db.close();
            } catch (SQLException ex) {
                plugin.log.log(Level.SEVERE, null, ex);
            }
        }
    }

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

    /**
     * Initializes key data
     */
    public void enable() {
        try {
            if (this.tableExists(plugin.config.getPrefix() + "Settings")) {
                ResultSet data = this.query("SELECT * FROM " + plugin.config.getPrefix() + "Settings WHERE Server = '" + plugin.config.getServerName() + "'");
                if (data.next()) {
                    String version = data.getString("Version");

                    if (!plugin.getDescription().getVersion().equals(version)) {
                        //String current = plugin.getDescription().getVersion();
                        //plugin.getResource(version + "to" + current + ".sql");
                    }
                } else {
                    plugin.log.info("IN SCOPE!");
                    this.query("INSERT INTO " + Prefix + "Settings (Server, Version) VALUES ('" + plugin.config.getServerName() + "', '" + plugin.getDescription().getVersion() + "')");
                }
            }
        } catch (SQLException ex) {
            plugin.log.severe(ex.getMessage());
        }
        plugin.loaded = true;
    }
}
