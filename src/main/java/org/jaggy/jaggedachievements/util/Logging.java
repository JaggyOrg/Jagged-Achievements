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
package org.jaggy.jaggedachievements.util;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Basic Logging handling class
 * @author Quirkylee
 */
public class Logging {
    private static final Logger log = Logger.getLogger("JaggedAchievements");
    
    public void info(String str) {
        Logging.log.log(Level.INFO, "[JaggyAchievements] {0}", str);
    }
    
    public void severe(String str) {
        Logging.log.log(Level.SEVERE, "[JaggyAchievements] {0}", str);
    }
    public void fine(String str) {
        Logging.log.log(Level.FINE, "[JaggyAchievements] {0}", str);
    }
    public void log(Level Level, String str) {
       Logging.log.log(Level, "[JaggyAchievements] {0}", str);
    }
    public void log(Level Level, String str, Throwable ex) {
       Logging.log.log(Level,"[JaggyAchievements] " +str, ex);
    }
}
