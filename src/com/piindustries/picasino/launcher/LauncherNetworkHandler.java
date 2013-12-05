package com.piindustries.picasino.launcher;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.piindustries.picasino.PiCasino;

import java.io.IOException;

public class LauncherNetworkHandler {
    private Server server;

    public LauncherNetworkHandler() {
        server = new Server();

        /* Register any classes that will be sent over the network */
        Network.register(server);

        server.addListener(new Listener() {
            /* Connection with a client is established. */
            public void connected(Connection connection){
                PiCasino.LOGGER.info("Launcher connected with ID: " + connection.getID());
            }

            /* Object received from a launcher. */
            public void received(Connection connection, Object object) {
                if (object instanceof User) {
                    User user = (User) object;
                    PiCasino.LOGGER.info("Received a user object from the launcher.");
                }
            }

            /* Connection with a launcher is lost. */
            public void disconnected(Connection connection) {
                PiCasino.LOGGER.info("Launcher disconnected with ID: " + connection.getID());
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
        PiCasino.LOGGER.info("Launcher connection server is running and waiting for connections...");
    }
}
