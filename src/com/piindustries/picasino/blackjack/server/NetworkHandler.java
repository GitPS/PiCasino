/*
 *
 *       ___ _   ___          _
 *      / _ (_) / __\__ _ ___(_)_ __   ___
 *     / /_)/ |/ /  / _` / __| | '_ \ / _ \
 *    / ___/| / /__| (_| \__ \ | | | | (_) |
 *    \/    |_\____/\__,_|___/_|_| |_|\___/
 *
 *
 * Class: com.piindustries.picasino.blackjack.BJServerNetworkHandler
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

package com.piindustries.picasino.blackjack.server;

import com.piindustries.picasino.blackjack.domain.DirectedGameEvent;
import com.piindustries.picasino.blackjack.domain.GameEvent;

/**
 * A server network handler.
 *
 * @see com.piindustries.picasino.api.NetworkHandler
 * @author A. Jensen
 * @version 1.0
 */
public class NetworkHandler implements com.piindustries.picasino.api.NetworkHandler {

    /**
     * Transmit and handle an GameEvent.
     *
     * @param toSend the GameEvent to transmit.
     */
    public void send(com.piindustries.picasino.api.GameEvent toSend){

        if( toSend instanceof DirectedGameEvent){
            // TODO HANDLE AS A SERVER GAME EVENT
            // Server events need to be sent to specific players denoted by toSend.getToUser()

            DirectedGameEvent event = (DirectedGameEvent)toSend;


        } else if (toSend instanceof GameEvent) {
            // TODO HANDLE AS A NORMAL EVENT
            //BJGameEvents need to be sent to all connected players

            GameEvent event = (GameEvent)toSend;
        }
    }

    /**
     * Receive and handle an GameEvent.
     *
     * @param toReceive the GameEvent to receive/handle
     */
    public void receive(com.piindustries.picasino.api.GameEvent toReceive){
        if (toReceive instanceof GameEvent) {
            // TODO HANDLE AS A NORMAL EVENT
            GameEvent event = (GameEvent)toReceive;
        }
    }
}