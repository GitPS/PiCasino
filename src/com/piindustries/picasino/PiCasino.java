package com.piindustries.picasino;

import com.piindustries.picasino.api.GameState;
import com.piindustries.picasino.blackjack.client.ClientGameState;
import com.piindustries.picasino.blackjack.server.ServerGameState;

/**
 * Date: 9/20/13
 * Time: 1:18 PM
 */

public class PiCasino {

    private static GameState gameState = null;

    /* Error messages */
    private static final String invalidArgsMsg = "Error: Invalid or missing command line arguments.";
    private static final String invalidModeMsg = "Error: Invalid or missing flag for launch mode.  Valid flags are 'client' or 'server'.";

    public static void main(String[] args){
        if(args.length < 2){
            System.err.println(invalidArgsMsg);
            /* Nothing else we can do so we exit */
            System.exit(0);
        }
        /* Check for valid game type */
        if(args[0].equalsIgnoreCase("blackjack")){
            if(args[0].equalsIgnoreCase("client")){
                buildClientBlackJack();
            } else if (args[0].equalsIgnoreCase("server")){
                buildServerBlackJack();
            } else{
                System.err.println(invalidModeMsg);
                /* Nothing else we can do here so we exit */
                System.exit(0);
            }
        }
        /* Any other game mode should follow in the same format as above */
        else{
            System.err.println(invalidArgsMsg);
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

    public static GameState getGameState(){
        return gameState;
    }


}
