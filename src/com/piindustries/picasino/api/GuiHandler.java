/*
 *
 *       ___ _   ___          _
 *      / _ (_) / __\__ _ ___(_)_ __   ___
 *     / /_)/ |/ /  / _` / __| | '_ \ / _ \
 *    / ___/| / /__| (_| \__ \ | | | | (_) |
 *    \/    |_\____/\__,_|___/_|_| |_|\___/
 *
 *
 * Class: com.piindustries.picasino.api.GuiHandler
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
 * An interface that handles the graphics
 * of a com.piindustries.picasino.Game.
 *
 * @author A. Jensen
 * @version 1.0
 */
public interface GuiHandler {

    /**
     * Updates the Graphical Interface of this game
     * given its current GameState.
     */
    public void updateGui();

    /**
     * @param toSet the {@link GameState} to set as the
     *              primary GameState of `this`.
     */
    public void setGameState(GameState toSet);

    /**
     * @param toSet the {@link NetworkHandler} to set as the
     *              primary NetworkHandler of `this`.
     */
    public void setNetworkHandler(NetworkHandler toSet);
}
