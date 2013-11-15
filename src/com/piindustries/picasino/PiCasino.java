package com.piindustries.picasino;

import com.piindustries.picasino.api.GameState;
import com.piindustries.picasino.blackjack.client.ClientGameState;
import com.piindustries.picasino.blackjack.server.ServerGameState;

import java.util.logging.Logger;

/**
 * Date: 9/20/13
 * Time: 1:18 PM
 */

public class PiCasino {

    public static Logger LOGGER = Logger.getLogger("PiCasino");

    private static GameState gameState = null;

    /* Error messages */
    private static final String invalidArgsMsg = "Invalid or missing command line arguments.";
    private static final String invalidModeMsg = "Invalid or missing flag for launch mode.  Valid flags are 'client' " +
            "or 'server'.";

    public static void main(String[] args) {
        if (args.length < 2) {
            LOGGER.severe(invalidArgsMsg);
            /* Nothing else we can do so we exit */
            System.exit(0);
        }
        /* Check for valid game type */
        if (args[0].equalsIgnoreCase("blackjack")) {
            if (args[1].equalsIgnoreCase("client")) {
                buildClientBlackJack();
            } else if (args[1].equalsIgnoreCase("server")) {
                buildServerBlackJack();
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

    private static void buildServerBlackJack() {
        /* We should only ever assign the GameState once. */
        gameState = new ServerGameState();
    }

    private static void buildClientBlackJack() {
        /* We should only ever assign the GameState once. */
        gameState = new ClientGameState();
    }

    public static GameState getGameState() {
        return gameState;
    }

}
