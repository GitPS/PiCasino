/*
 * com.piindustries.picasino.data
 * Version 1.0
 * November 6, 2013
 * 
 * Copyright 2013 - Michael Hoyt, Aaron Jensen, Andrew Reis, and Phillip Sime.
 *
 * This file is part of PiCasino.
 *
 * PiCasino is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * PiCasino is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with PiCasino.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.piindustries.picasino.data;

/**
 * A basic Database access interface.
 */
public class DataAccessObject {

    /**
     * @param username the username to query to the database for.
     *
     * @return `True` if the data base contains data for a user with username `username`,
     * otherwise `false`.
     */
    public boolean containsUsername(String username){ return true; } // TODO add logic and database queries

    /**
     * `True` if `username's` password is `password` otherwise `false`
     *
     * @param username the username to query
     * @param password the password to authenticate
     *
     * @return `True` if `username's` password is `password` otherwise `false`
     */
    public boolean isAuthenticated(String username, String password ){ return true; } // TODO add logic and database queries

    /**
     * Returns the amount of chips that `username` has.
     *
     * @param username the username to query
     *
     * @return the amount of chips that `username` has.
     */
    public int getChipCount(String username){ return 500 } // TODO add logic and database queries

    /**
     * Sets a users chip count.
     *
     * @param username the user to set the chip count of
     * @param count the int to set `username's` chip count to
     */
    public void setChipCount(String username, int count){ /* do nothing */ } // TODO add logic and database queries

    /**
     * Adds a username to the database.
     *
     * @param username the username of the new user to add to this database.
     */
    public void addUser(String username){ /* Do Nothing */ } // TODO add logic and database queries
}
