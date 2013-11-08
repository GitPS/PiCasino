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
import com.piindustries.picasino.blackjack.BJDirectedGameEvent;
import com.piindustries.picasino.blackjack.BJGameEvent;
import com.piindustries.picasino.blackjack.BJServerGameState;

import java.util.HashMap;

public class ServerTesterServer implements NetworkHandler {
    BJServerGameState innards;
    HashMap<String,ClientTesterServer> sockets;

    public ServerTesterServer(){
        innards  = new BJServerGameState();
        innards.setNetworkHandler(this);
    }

    public void send(GameEvent e){
        if( e instanceof BJDirectedGameEvent){
            BJDirectedGameEvent event = (BJDirectedGameEvent)e;
            BJGameEvent toSend = new BJGameEvent();
            toSend.setType(event.getType());
            toSend.setValue(event.getValue());
            this.sockets.get(event.getToUser()).receive(toSend);
        } else if (e instanceof BJGameEvent ) {
            for(String s : this.sockets.keySet()){
                sockets.get(s).receive(e);
            }
        }
    }

    /**
     * Receive and handle an GameEvent.
     *
     * @param toReceive the GameEvent to receive/handle
     */
    public void receive(GameEvent toReceive){
        if (toReceive instanceof BJGameEvent ) {
            BJGameEvent event = (BJGameEvent)toReceive;
            try {
                innards.invoke( toReceive);
            } catch (InvalidGameEventException e) {
                System.err.println("InvalidGameEvent has been caught.");
            }
        }
    }

    public void establishConnection(String username, ClientTesterServer client){
        sockets.put(username, client);
        innards.addPlayerToWaitingList(username);
    }
}
