/*
 *
 *       ___ _   ___          _
 *      / _ (_) / __\__ _ ___(_)_ __   ___
 *     / /_)/ |/ /  / _` / __| | '_ \ / _ \
 *    / ___/| / /__| (_| \__ \ | | | | (_) |
 *    \/    |_\____/\__,_|___/_|_| |_|\___/
 *
 *
 * Class: com.piindustries.picasino.blackjack.BJClientNetworkHandler
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

package com.piindustries.picasino.blackjack.client;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.piindustries.picasino.PiCasino;
import com.piindustries.picasino.api.InvalidGameEventException;
import com.piindustries.picasino.blackjack.domain.GameEvent;
import com.piindustries.picasino.blackjack.test.Network;

import java.io.IOException;

/**
 * A ClientNetworkHandler.
 *
 * @author A. Jensen
 * @version 1.0
 * @see com.piindustries.picasino.api.NetworkHandler
 */
public class ClientNetworkHandler implements com.piindustries.picasino.api.NetworkHandler {
    private Client client;

    public ClientNetworkHandler(final PiCasino pi, String host) {
        client = new Client();
        client.start();

        /* Register any classes that will be sent over the network */
        Network.register(client);

        /* Create a threaded listener */
        client.addListener(new Listener.ThreadedListener(new Listener() {
            public void connected(Connection connection) {
            }

            public void received(Connection connection, Object object) {
                if (object instanceof GameEvent) {
                    GameEvent event = (GameEvent) object;
                    // TODO get the GameState and call invoke()
                    try {
                        pi.getGameState().invoke(event);
                    } catch (InvalidGameEventException e) {
                        e.printStackTrace();
                        // TODO
                    }

                }
            }

            public void disconnected(Connection connection) {
                System.exit(0);
            }
        }));

        try {
            client.connect(5000, host, Network.port);
            // Server communication after connection can go here, or in Listener#connected().
        } catch (IOException ex) {
            ex.printStackTrace();
            System.exit(1);
        }
        /* DEBUG START */
        if(client.isConnected()){
            PiCasino.LOGGER.info("Client has successfully connected to a server...");
        } else{
            PiCasino.LOGGER.info("Client is not connected to a server...");
        }
        /* DEBUG END */
    }


    /**
     * Transmit and handle an GameEvent.
     *
     * @param toSend the GameEvent to transmit.
     */
    public void send(com.piindustries.picasino.api.GameEvent toSend) {
        client.sendTCP(toSend);
    }
}
