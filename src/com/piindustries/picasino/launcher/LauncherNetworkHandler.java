package com.piindustries.picasino.launcher;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.piindustries.picasino.PiCasino;
import com.piindustries.picasino.blackjack.database.BlackjackDatabaseConnector;

import java.io.IOException;

public class LauncherNetworkHandler {
    private Server server;
    private BlackjackDatabaseConnector db;

    public LauncherNetworkHandler() {
        server = new Server();
        db = new BlackjackDatabaseConnector();

        /* Register any classes that will be sent over the network */
        Network.register(server);

        server.addListener(new Listener() {
            /* Connection with a client is established. */
            public void connected(Connection connection){
                PiCasino.LOGGER.info("Launcher connected with ID: " + connection.getID());
            }

            /* Object received from a launcher. */
            public void received(Connection connection, Object object) {
                PiCasino.LOGGER.info("Received an Object from client: " + connection.getID());
                if (object instanceof User) {
                    PiCasino.LOGGER.info("Starting to process a user object.");
                    User user = (User) object;
                    /* Allow the called method to verify the user is authenticated */
                    user.setAuthenticated(handleUser(user));
                    /* Send the user back to the launcher */
                    send(user, connection.getID());
                    PiCasino.LOGGER.info("Finished processing a user object.");
                }
                else if(object instanceof Update){
                    PiCasino.LOGGER.info("Starting to process an update object.");
                    Update update = (Update)object;
                    /* Check for update and set version and download URL for PiCasino.jar */
                    update.setUpdateNeeded(update.getClientVersion().equalsIgnoreCase(PiCasino.VERSION));
                    update.setServerVersion(PiCasino.VERSION);
                    update.setUpdateURL(PiCasino.UPDATE_URL);
                    /* Send the update information back to the launcher */
                    send(update, connection.getID());
                    PiCasino.LOGGER.info("Finished processing an update object.");
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

    private void send(Object o, int id){
        server.sendToTCP(id, o);
    }

    private boolean handleUser(User user){
        /* Check if player exists already, if not create that user */
        if(db.checkUserExist(user.getUsername())){
            return db.checkPlayerLogin(user.getUsername(), user.getPassword());
        }
        else{
            return db.createNewPlayer(user.getUsername(), user.getPassword(), user.getFirstName(), user.getLastName(), user.getEmail());
        }
    }
}
