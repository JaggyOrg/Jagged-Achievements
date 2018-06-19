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
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.logging.Level;
import java.util.logging.Logger;

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

    public void load(Jagged p) {
        plugin = p;
        DBType = plugin.config.getDBType();
        MysqlHost = plugin.config.getMysqlHost();
        DBLocation = plugin.config.getDBLocation();
        MysqlUser = plugin.config.getMysqlUser();
        DBName = plugin.config.getDBName();
        MysqlPass = plugin.config.getMysqlPass();
        MysqlPort = plugin.config.getMysqlPort();

        if (DBType.toLowerCase().equals("mysql")) {
            try {
                // This will load the MySQL driver, each DB has its own driver
                Class.forName("com.mysql.jdbc.Driver");
                // Setup the connection with the DB
                db = DriverManager.getConnection("jdbc:mysql://"
                        + MysqlHost + ":" + MysqlPort + "/" + DBName + "?"
                        + "user=" + MysqlUser + "&password=" + MysqlPass);
            } catch (Exception e) {
                plugin.log.info("Error loading db: "+e.getMessage());
            }
        } else {
            String path = DBLocation + DBName + ".h2";
            File file = new File(path);
            String absolutePath = file.getAbsolutePath();
            plugin.log.info(absolutePath);
            try {
                Class.forName("org.h2.Driver");
                db = DriverManager.getConnection("jdbc:h2:" + absolutePath, "sa", "");
            } catch (Exception e) {
                plugin.log.info("Error loading db: "+e.getMessage());
            }
        }
    }
}
