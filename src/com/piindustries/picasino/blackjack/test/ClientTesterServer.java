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

import com.piindustries.picasino.api.InvalidGameEventException;
import com.piindustries.picasino.api.NetworkHandler;
import com.piindustries.picasino.blackjack.domain.GameEvent;
import com.piindustries.picasino.blackjack.client.GameState;

public class ClientTesterServer implements NetworkHandler {
    GameState innards;
    NetworkHandler server;

    public ClientTesterServer(){
        innards = new GameState();
        innards.setNetworkHandler(this);
    }

    public void send(com.piindustries.picasino.api.GameEvent e){
        if (e instanceof GameEvent) {
            server.receive(e);
        }
    }

    /**
     * Receive and handle an GameEvent.
     *
     * @param toReceive the GameEvent to receive/handle
     */
    public void receive(com.piindustries.picasino.api.GameEvent toReceive){
        if (toReceive instanceof GameEvent) {
            try {
                innards.invoke(toReceive);
            } catch (InvalidGameEventException e) {
                System.err.println("InvalidGameEvent has been caught.");
            }
        }
    }
}
