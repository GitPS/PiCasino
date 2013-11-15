package com.piindustries.picasino;

import com.piindustries.picasino.api.GameState;
import com.piindustries.picasino.api.NetworkHandler;
import com.piindustries.picasino.blackjack.client.ClientGameState;
import com.piindustries.picasino.blackjack.client.ClientNetworkHandler;
import com.piindustries.picasino.blackjack.server.ServerGameState;
import com.piindustries.picasino.blackjack.server.ServerNetworkHandler;

import java.util.logging.Logger;

/**
 * Date: 9/20/13
 * Time: 1:18 PM
 */

public class PiCasino {

    public static Logger LOGGER = Logger.getLogger("PiCasino");

    private GameState gameState = null;
    private NetworkHandler networkHandler = null;

    /* Error messages */
    private static final String invalidArgsMsg = "Invalid or missing command line arguments.";
    private static final String invalidModeMsg = "Invalid or missing flag for launch mode.  Valid flags are 'client' " +
            "or 'server'.";

    public static void main(String[] args) {
        PiCasino pi = new PiCasino();
        String host;
        if (args.length < 2) {
            LOGGER.severe(invalidArgsMsg);
            /* Nothing else we can do so we exit */
            System.exit(0);
        }
        /* Check for valid game type */
        if (args[0].equalsIgnoreCase("blackjack")) {
            if (args[1].equalsIgnoreCase("client")) {
                if(args.length >= 3){
                    pi.buildClientBlackJack(args[2]);
                } else{
                    LOGGER.severe(invalidArgsMsg);
                }
            } else if (args[1].equalsIgnoreCase("server")) {
                pi.buildServerBlackJack();
            } else {
                LOGGER.severe(invalidModeMsg);
                /* Nothing else we can do here so we exit */
                System.exit(0);
            }
        }
        /* Any other game mode should follow in the same format as above */
        else {
            LOGGER.severe(invalidArgsMsg);
        }
    }

    private void buildServerBlackJack() {
        /* We should only ever assign the GameState once. */
        gameState = new ServerGameState();
        networkHandler = new ServerNetworkHandler(this);
    }

    private void buildClientBlackJack(String host) {
        /* We should only ever assign the GameState once. */
        gameState = new ClientGameState();
        networkHandler = new ClientNetworkHandler(this, host);
    }

    public GameState getGameState() {
        return gameState;
    }

    public NetworkHandler getNetworkHandler(){
        return networkHandler;
    }

}
