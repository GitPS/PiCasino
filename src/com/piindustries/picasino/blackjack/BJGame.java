/*
 *
 *       ___ _   ___          _
 *      / _ (_) / __\__ _ ___(_)_ __   ___
 *     / /_)/ |/ /  / _` / __| | '_ \ / _ \
 *    / ___/| / /__| (_| \__ \ | | | | (_) |
 *    \/    |_\____/\__,_|___/_|_| |_|\___/
 *
 *
 * Class: com.piindustries.picasino.blackjack.BJGame
 * Version: 1.0
 * Date: October 30, 2013
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

package com.piindustries.picasino.blackjack;

import com.piindustries.picasino.api.Game;
import com.piindustries.picasino.api.GuiHandler;
import com.piindustries.picasino.api.NetworkHandler;


/**
 * A Black Jack Game.
 *
 * @author A. Jensen
 * @version 1.0
 */
public class BJGame implements Game {

    /**
     * Builds and returns an object conforming to type GameState
     * to be constructed on the client-side;
     *
     * @return a GameState
     */
    public BJClientGameState buildClientGameState(){
        return new BJClientGameState();
    }

    /**
     * Builds and returns an object conforming to type GameState
     * to be constructed on the server-side;
     *
     * @return a GameState
     */
    public BJServerGameState buildServerGameState(){
        return new BJServerGameState();
    }

    /**
     * Builds and returns an object conforming to type NetworkHandler
     *
     * @return a NetworkHandler to be instantiated on the server to handle
     * server-side communications.
     */
    public NetworkHandler buildServerNetworkHandler(){
        return new BJServerNetworkHandler();
    }


    /**
     * Builds and returns an object conforming to type NetworkHandler
     *
     * @return a NetworkHandler to be instantiated on the client to handle
     * client-side communications.
     *
     */
    public NetworkHandler buildClientNetworkHandler(){
        return new BJClientNetworkHandler();
    }

    /**
     * Builds and returns an object conforming to type GuiHandler to
     * handle graphics on the client side.
     *
     * @return a GuiHandler that will handle graphics on the client.
     */
    public GuiHandler buildGuiHandler(){
        return new BJGuiHandler();
    }
}
