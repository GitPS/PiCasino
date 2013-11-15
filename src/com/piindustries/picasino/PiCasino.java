package com.piindustries.picasino;

import com.piindustries.picasino.api.GameState;
import com.piindustries.picasino.blackjack.client.ClientGameState;
import com.piindustries.picasino.blackjack.server.ServerGameState;

/**
 * Date: 9/20/13
 * Time: 1:18 PM
 */

public class PiCasino {

    private static GameState gameState;

    public static void main(String[] args){
        // TODO Check if this is client or server
        if(args.length < 2){
            System.err.println("Error: No or too few command line arguments found.");
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
                System.err.println("Error: Invalid flag for mode.  Should be 'client' or 'server'.");
                /* Nothing else we can do here so we exit */
                System.exit(0);
            }
        }
    }

    private static void buildServerBlackJack() {
        gameState = new ServerGameState();
    }

    private static void buildClientBlackJack() {
        gameState = new ClientGameState();
    }

    public static void setGameState(GameState gs){
        gameState = gs;
    }

    public static GameState getGameState(){
        return gameState;
    }


}
