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
import com.piindustries.picasino.blackjack.domain.Network;

import java.io.IOException;
import java.net.SocketTimeoutException;

/**
 * A ClientNetworkHandler.
 *
 * @version 1.0
 * @see com.piindustries.picasino.api.NetworkHandler
 */
public class ClientNetworkHandler implements com.piindustries.picasino.api.NetworkHandler {
    private Client client;

    public ClientNetworkHandler(final PiCasino pi, String host, final String username) {
        client = new Client();
        client.start();

        /* Register any classes that will be sent over the network */
        Network.register(client);

        /* Create a threaded listener */
        client.addListener(new Listener.ThreadedListener(new Listener() {
            /* Connection with a server is established. */
            public void connected(Connection connection) {
                PiCasino.LOGGER.info("Connection with server established.");
                client.sendTCP(username);
            }

            /* Object received from the server. */
            public void received(Connection connection, Object object) {
                if (object instanceof GameEvent) {
                    GameEvent event = (GameEvent) object;
                    /* DEBUG START */
                    PiCasino.LOGGER.info("Received a game event from the server.");
                    PiCasino.LOGGER.info("Game Event Data: " + event.getType().name());
                    /* DEBUG END */
                    try {
                        pi.getGameState().invoke(event);
                    } catch (InvalidGameEventException e) {
                        PiCasino.LOGGER.severe(e.getMessage());
                    }

                }
            }

            /* Connection with a server is lost. */
            public void disconnected(Connection connection) {
                PiCasino.LOGGER.severe("Connection with server lost.  Program will exit.");
                System.exit(0);
            }
        }));

        try {
            client.connect(5000, host, Network.port);
            // Server communication after connection can go here, or in Listener#connected().
        } catch (SocketTimeoutException e){
            PiCasino.LOGGER.severe(e.getMessage());
            System.exit(1);
        } catch (IOException e) {
            PiCasino.LOGGER.severe(e.getMessage());
            System.exit(1);
        }
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
