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
import com.piindustries.picasino.blackjack.domain.GameEvent;
import com.piindustries.picasino.blackjack.test.Network;

import java.io.IOException;

/**
 * A server network handler.
 *
 * @author A. Jensen
 * @version 1.0
 * @see com.piindustries.picasino.api.NetworkHandler
 */
public class ServerNetworkHandler implements com.piindustries.picasino.api.NetworkHandler {
    Server server;

    public ServerNetworkHandler(final PiCasino pi) {
        server = new Server();

        /* Register any classes that will be sent over the network */
        Network.register(server);

        server.addListener(new Listener() {
            public void received(Connection connection, Object object) {
                if (object instanceof GameEvent) {
                    GameEvent event = (GameEvent) object;
                    /* DEBUG START */
                    PiCasino.LOGGER.info("Received a game event from a client.");
                    PiCasino.LOGGER.info("Game Event Data: " + event.getType().name());
                    /* DEBUG END */
                    // TODO
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
        });

        try {
            server.bind(Network.port);
        } catch (IOException e) {
            PiCasino.LOGGER.severe("Failed to bind to port " + Network.port + "!");
            PiCasino.LOGGER.severe(e.getMessage());
            /* If we can't bind to our port we can't do anything so exit */
            System.exit(1);
        }
        server.start();

        /* Notify console that the server started and is waiting for connections */
        PiCasino.LOGGER.info("Server is running and waiting for connections...");
    }

    /**
     * Transmit and handle an GameEvent.
     *
     * @param toSend the GameEvent to transmit.
     */
    public void send(com.piindustries.picasino.api.GameEvent toSend) {
        server.sendToAllTCP(toSend);
    }

    /**
     * Transmit and handle an GameEvent for one specific user.
     *
     * @param toSend   the GameEvent to transmit.
     * @param userName to send the to.
     */
    public void send(com.piindustries.picasino.api.GameEvent toSend, String userName) {
        // TODO Change this to the client number we want to send the event to
        server.sendToTCP(1, toSend);
    }


}
