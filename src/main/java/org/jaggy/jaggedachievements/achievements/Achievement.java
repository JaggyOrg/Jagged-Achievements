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
package org.jaggy.jaggedachievements.achievements;

import java.util.List;

/**
 *
 * @author Quirkylee
 */
public class Achievement {
    public String title;
    public String subtitle;
    public int xp;
    public List<String> commands;
    
    public String getTitle() {
        return title;
    }
    
    public String getSubTitle() {
        return subtitle;
    }
    
    public int getXP() {
        return xp;
    }
    
    public List<String> getCommands() {
        return commands;
    }
    
    public void SetCommands(List<String> commands) {
        this.commands = commands;
    }
    public void setXP(int xp) {
        this.xp = xp;
    }
    public void setSubTitle(String subtitle) {
        this.subtitle = subtitle;
    }
    public void setTitle(String title) {
        this.title = title;
    }
}
