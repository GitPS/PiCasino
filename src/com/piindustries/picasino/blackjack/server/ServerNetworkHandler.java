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
import com.piindustries.picasino.blackjack.domain.GameEventType;
import com.piindustries.picasino.blackjack.domain.Network;

import java.io.IOException;
import java.util.HashMap;

/**
 * A server network handler.
 *
 * @version 1.0
 * @see com.piindustries.picasino.api.NetworkHandler
 */
public class ServerNetworkHandler implements com.piindustries.picasino.api.NetworkHandler {
    private Server server;
    private HashMap<String, Integer> connectedUsers;

    public ServerNetworkHandler(final PiCasino pi) {
        server = new Server();
        connectedUsers = new HashMap<>();

        /* Register any classes that will be sent over the network */
        Network.register(server);

        server.addListener(new Listener() {
            /* Connection with a client is established. */
            public void connected(Connection connection){
                PiCasino.LOGGER.info("Client connected with ID: " + connection.getID());
            }

            /* Object received from a client. */
            public void received(Connection connection, Object object) {
                if (object instanceof GameEvent) {
                    GameEvent event = (GameEvent) object;
                    /* DEBUG START */
                    PiCasino.LOGGER.info("Received a game event from a client.");
                    PiCasino.LOGGER.info("Game Event Data: " + event.getType().name());
                    /* DEBUG END */
                    try {
                        pi.getGameState().invoke(event);
                    } catch (InvalidGameEventException e) {
                        PiCasino.LOGGER.severe(e.getMessage());
                    }
                } else if (object instanceof String) {
                    String username = (String)object;
                    addConnectedUser(username, connection.getID());

                    /* Add the user to the server waiting list */
                    GameEvent gameEvent = new GameEvent(GameEventType.ADD_PLAYER_TO_WAITING_LIST);
                    gameEvent.setValue(username);
                    try {
                        pi.getGameState().invoke(gameEvent);
                    } catch (InvalidGameEventException e) {
                        PiCasino.LOGGER.severe("Failed to add player to waiting list!");
                        PiCasino.LOGGER.severe(e.getMessage());
                    }
                }
            }

            /* Connection with a client is lost. */
            public void disconnected(Connection connection) {
                PiCasino.LOGGER.info("Client disconnected with ID: " + connection.getID());
                String username = null;
                /* Determine which username is associated with the disconnected client */
                for(String s : connectedUsers.keySet()){
                    if(connectedUsers.get(s) == connection.getID()){
                        username = s;
                        /* There should only ever be on instance of the same client ID */
                        break;
                    }
                }
                /* If we found the user in the connected user list */
                if(username != null){
                    /* Remove them from the network handler list */
                    connectedUsers.remove(username);
                    PiCasino.LOGGER.info("Removed " + username + " from the network handler.");
                    /* Create a game event and let the game state know of disconnect */
                    GameEvent gameEvent = new GameEvent(GameEventType.PLAYER_DISCONNECT);
                    gameEvent.setValue(username);
                    try{
                        pi.getGameState().invoke(gameEvent);
                    } catch (InvalidGameEventException e) {
                        PiCasino.LOGGER.severe("Failed to properly handle disconnect by client!");
                        PiCasino.LOGGER.severe(e.getMessage());
                    }

                }
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
        PiCasino.LOGGER.info("All players were sent a GameEvent.");
    }

    /**
     * Transmit and handle an GameEvent for one specific user.
     *
     * @param toSend   the GameEvent to transmit.
     * @param userName to send the to.
     */
    public void send(com.piindustries.picasino.api.GameEvent toSend, String userName) {
        userName = userName.toLowerCase();
        if (connectedUsers.containsKey(userName)) {
            server.sendToTCP(connectedUsers.get(userName), toSend);
            PiCasino.LOGGER.info(userName + " was sent an individual GameEvent.");
        } else {
            PiCasino.LOGGER.severe("Attempted to send a GameEvent to an untracked user with ID: " + connectedUsers
                    .get(userName) + " and name " + userName + ".");
        }
    }

    /**
     * Adds a connected user to a HashMap for internal use by the network handler.  This map
     * will allow individual messages to be sent based on the user's client ID.
     *
     * @param name of user.
     * @param id assigned on connection.
     */
    public void addConnectedUser(String name, int id) {
        name = name.toLowerCase();
        /* Only add it to the map if it doesn't already exist */
        if (!connectedUsers.containsKey(name)) {
            connectedUsers.put(name, id);
            PiCasino.LOGGER.info("Added username: " + name + " with ID: " + id);
        }
    }
}
