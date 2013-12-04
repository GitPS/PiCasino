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

package com.piindustries.picasino.blackjack.domain;

import com.piindustries.picasino.PiCasino;
import com.piindustries.picasino.api.GuiHandler;
import com.piindustries.picasino.blackjack.client.ClientGameState;
import com.piindustries.picasino.blackjack.server.ServerGameState;


/**
 * A Black Jack Game.
 *
 * @author A. Jensen
 * @version 1.0
 */
public class Game implements com.piindustries.picasino.api.Game {

    /**
     * Builds and returns an object conforming to type ClientGameState
     * to be constructed on the client-side;
     *
     * @return a ClientGameState
     */
    public ClientGameState buildClientGameState(PiCasino pi, String username){
        return new ClientGameState(pi, username);
    }

    /**
     * Builds and returns an object conforming to type ClientGameState
     * to be constructed on the server-side;
     *
     * @return a ClientGameState
     */
    public ServerGameState buildServerGameState(PiCasino pi){
        return new ServerGameState(pi);
    }

    @Override
    public GuiHandler buildGuiHandler() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
