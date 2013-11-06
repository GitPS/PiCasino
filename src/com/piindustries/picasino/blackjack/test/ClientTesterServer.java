/*
 * [Class]
 * [Current Version]
 * [Date last modified]
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

package com.piindustries.picasino.blackjack.test;

import com.piindustries.picasino.api.GameEvent;
import com.piindustries.picasino.api.InvalidGameEventException;
import com.piindustries.picasino.api.NetworkHandler;
import com.piindustries.picasino.blackjack.BJClientGameState;
import com.piindustries.picasino.blackjack.BJGameEvent;

public class ClientTesterServer implements NetworkHandler {
    BJClientGameState innards;
    NetworkHandler server;

    public ClientTesterServer(){
        innards = new BJClientGameState();
        innards.setNetworkHandler(this);
    }

    public void send(GameEvent e){
        if (e instanceof BJGameEvent) {
            server.receive(e);
        }
    }

    /**
     * Receive and handle an GameEvent.
     *
     * @param toReceive the GameEvent to receive/handle
     */
    public void receive(GameEvent toReceive){
        if (toReceive instanceof BJGameEvent ) {
            try {
                innards.invoke(toReceive);
            } catch (InvalidGameEventException e) {
                System.err.println("InvalidGameEvent has been caught.");
            }
        }
    }
}
