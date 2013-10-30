/*
 *
 *       ___ _   ___          _
 *      / _ (_) / __\__ _ ___(_)_ __   ___
 *     / /_)/ |/ /  / _` / __| | '_ \ / _ \
 *    / ___/| / /__| (_| \__ \ | | | | (_) |
 *    \/    |_\____/\__,_|___/_|_| |_|\___/
 *
 *
 * Class: com.piindustries.picasino.api.InvalidActionException
 * Version: 1.0
 * Date: October 29, 2013
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

package com.piindustries.picasino.api;

/**
 * A throwable exception.  An InvalidGameEventException should be thrown
 * if a GameState invokes an action that cannot be invoked in its current
 * state.
 *
 * @author A. Jensen
 * @version 1.0
 */
public class InvalidGameEventException extends IllegalArgumentException {

    /**
     * Calls java.lang.Exception's default constructor passing `msg`.
     *
     * @param msg an error message
     * @see java.lang.Exception
     */
    public InvalidGameEventException(String msg){
        super(msg);
    }

    /**
     * Calls java.lang.Exception's default constructor.
     *
     * @see java.lang.Exception
     */
    public InvalidGameEventException(){
        super();
    }
}
