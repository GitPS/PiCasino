/*
 *
 *       ___ _   ___          _
 *      / _ (_) / __\__ _ ___(_)_ __   ___
 *     / /_)/ |/ /  / _` / __| | '_ \ / _ \
 *    / ___/| / /__| (_| \__ \ | | | | (_) |
 *    \/    |_\____/\__,_|___/_|_| |_|\___/
 *
 *
 * Class: com.piindustries.picasino.api.Game
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

import com.piindustries.picasino.PiCasino;

/**
 * A game can build all the components necessary to run a PiCasino game.
 *
 * @author A. Jensen
 * @version 1.0
 */
public interface Game {

    /**
     * Builds and returns an object conforming to type ClientGameState
     * to be constructed on the client-side;
     *
     * @return a ClientGameState
     */
    public GameState buildClientGameState(PiCasino pi, String username);

    /**
     * Builds and returns an object conforming to type ClientGameState
     * to be constructed on the server-side;
     *
     * @return a ClientGameState
     */
    public GameState buildServerGameState(PiCasino pi);

    /**
     * Builds and returns an object conforming to type GuiHandler to
     * handle graphics on the client side.
     *
     * @return a GuiHandler that will handle graphics on the client.
     */
    public GuiHandler buildGuiHandler();
}