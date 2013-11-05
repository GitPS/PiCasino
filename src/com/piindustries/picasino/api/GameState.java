/*
 *
 *       ___ _   ___          _
 *      / _ (_) / __\__ _ ___(_)_ __   ___
 *     / /_)/ |/ /  / _` / __| | '_ \ / _ \
 *    / ___/| / /__| (_| \__ \ | | | | (_) |
 *    \/    |_\____/\__,_|___/_|_| |_|\___/
 *
 *
 * Class: com.piindustries.picasino.api.GameState
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

import java.io.Serializable;

/**
 * An object that contains all the information required to exactly know the
 * state of a game.  The information between game state across a game may
 * vary slightly, but they must progress through the logic and phases of the
 * game at the same rate.
 *
 * For instance, a server GameState may hold all of
 * cards of all of the players, where a client GameState will only hold the
 * cards of that player.
 *
 * When a GameState invokes an GameEvent it progresses to its the next state.
 *
 * @author A. Jensen
 * @version 1.0
 */
public interface GameState extends Serializable, Cloneable {

    /**
     * Progresses `this` to its next logical state according to the GameEvent.
     *
     * @param toInvoke the GameEvent for `this` to invoke.
     *
     * @throws InvalidGameEventException if `toInvoke` cannot be handled by
     * `this` in its current state.
     */
    public void invoke(GameEvent toInvoke) throws InvalidGameEventException;
}