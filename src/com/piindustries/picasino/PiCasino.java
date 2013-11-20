package com.piindustries.picasino;

import com.piindustries.picasino.api.GameState;
import com.piindustries.picasino.api.InvalidGameEventException;
import com.piindustries.picasino.api.NetworkHandler;
import com.piindustries.picasino.blackjack.client.ClientGameState;
import com.piindustries.picasino.blackjack.client.ClientNetworkHandler;
import com.piindustries.picasino.blackjack.domain.GameEvent;
import com.piindustries.picasino.blackjack.domain.GameEventType;
import com.piindustries.picasino.blackjack.server.ServerGameState;
import com.piindustries.picasino.blackjack.server.ServerNetworkHandler;
import com.piindustries.picasino.blackjack.test.Invoker;

import java.util.logging.Logger;

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
        /* Debug Start */
        for(String s : args)
            LOGGER.info(s);
        /* Debug End */
        if (args.length < 2) {
            LOGGER.severe(invalidArgsMsg);
            /* Nothing else we can do so we exit */
            System.exit(0);
        }
        /* Check for valid game type */
        if (args[0].equalsIgnoreCase("blackjack")) {
            if (args[1].equalsIgnoreCase("client")) {
                if(args.length >= 4){
                    pi.buildClientBlackJack(args[2], args[3]);
                    /* Debug start */
                    Invoker i = new Invoker((ClientGameState)pi.getGameState());
                    i.getInnards().setNetworkHandler( pi.getNetworkHandler() );
                    i.step();
                    /* Debug end */
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
        networkHandler = new ServerNetworkHandler(this);
        gameState = new ServerGameState(this);
        GameEvent gameEvent = new GameEvent(GameEventType.SET_NETWORK_HANDLER);
        gameEvent.setValue(networkHandler);

        /* Set NetworkHandler in GameState */
        try{
            gameState.invoke(gameEvent);
        } catch (InvalidGameEventException e) {
            LOGGER.severe("Failed to bind NetworkHandler to GameState!");
            LOGGER.severe(e.getMessage());
        }

        gameEvent = new GameEvent(GameEventType.SET_INTERMISSION_TIME);
        gameEvent.setValue(30);

        /* Set intermission time */
        try{
            gameState.invoke(gameEvent);
        } catch (InvalidGameEventException e) {
            LOGGER.severe("Failed to set intermission timer!");
            LOGGER.severe(e.getMessage());
        }

        /* Start timer in GameState */
        try{
            gameState.invoke(new GameEvent(GameEventType.START_TIMER));
        } catch (InvalidGameEventException e) {
            LOGGER.severe("Failed to start timer!");
            LOGGER.severe(e.getMessage());
        }
    }

    private void buildClientBlackJack(String host, String userName) {
        ClientNetworkHandler clientNetworkHandler = new ClientNetworkHandler(this, host, userName);
        /* DEBUG */
        LOGGER.info("Is Connected = " + clientNetworkHandler.isConnected());
        /* DEBUG*/

        while(!clientNetworkHandler.isConnected()){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                LOGGER.severe(e.getMessage());
            }
        }

        networkHandler = clientNetworkHandler;
        gameState = new ClientGameState(this, userName);
        GameEvent gameEvent = new GameEvent(GameEventType.SET_NETWORK_HANDLER);
        gameEvent.setValue(networkHandler);

        /* Set NetworkHandler in GameState */
        try{
            gameState.invoke(gameEvent);
        } catch (InvalidGameEventException e) {
            LOGGER.severe("Failed to bind NetworkHandler to GameState!");
            LOGGER.severe(e.getMessage());
        }
    }

    public GameState getGameState() {
        return gameState;
    }

    public NetworkHandler getNetworkHandler(){
        return networkHandler;
    }

}
