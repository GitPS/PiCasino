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

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.piindustries.picasino.PiCasino;
import com.piindustries.picasino.api.InvalidGameEventException;
import com.piindustries.picasino.blackjack.domain.DirectedGameEvent;
import com.piindustries.picasino.blackjack.domain.GameEvent;
import com.piindustries.picasino.blackjack.test.Network;

/**
 * A server network handler.
 *
 * @see com.piindustries.picasino.api.NetworkHandler
 * @author A. Jensen
 * @version 1.0
 */
public class ServerNetworkHandler implements com.piindustries.picasino.api.NetworkHandler {
    Server server;

    public ServerNetworkHandler() {
        server = new Server();
        server.start();

        /* Register any classes that will be sent over the network */
        Network.register(server);

        server.addListener(new Listener() {
            public void received(Connection connection, Object object) {
                if (object instanceof GameEvent) {
                    GameEvent event = (GameEvent) object;
                    // TODO
                    try{
                        PiCasino.getGameState().invoke(event);
                    } catch(InvalidGameEventException e){
                        e.printStackTrace();
                        // TODO
                    }
                }
            }

            public void disconnected(Connection connection) {
                System.exit(0);
            }
        });
    }

    /**
     * Transmit and handle an GameEvent.
     *
     * @param toSend the GameEvent to transmit.
     */
    public void send(com.piindustries.picasino.api.GameEvent toSend) {

        if (toSend instanceof DirectedGameEvent) {
            // TODO HANDLE AS A SERVER GAME EVENT
            // Server events need to be sent to specific players denoted by toSend.getToUser()

            DirectedGameEvent event = (DirectedGameEvent) toSend;
            // TODO Change this to the client number we want to send the event to
            server.sendToTCP(1, event);

        } else if (toSend instanceof GameEvent) {
            // TODO HANDLE AS A NORMAL EVENT
            //BJGameEvents need to be sent to all connected players

            GameEvent event = (GameEvent) toSend;
            server.sendToAllTCP(event);
        }
    }


}
