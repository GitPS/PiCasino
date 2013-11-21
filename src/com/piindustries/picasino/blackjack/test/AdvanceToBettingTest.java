package com.piindustries.picasino.blackjack.test;

import com.piindustries.picasino.PiCasino;
import com.piindustries.picasino.api.InvalidGameEventException;
import com.piindustries.picasino.blackjack.client.ClientGameState;
import com.piindustries.picasino.blackjack.domain.GameEvent;
import com.piindustries.picasino.blackjack.domain.GameEventType;

public class AdvanceToBettingTest {

    public static void main(String[] args){
        PiCasino pi = new PiCasino();
        ClientGameState gameState = new ClientGameState(pi,"Lyndsay");
        GameEvent gameEvent = new GameEvent(GameEventType.ADVANCE_TO_BETTING, null);
        try {
            gameState.invoke(gameEvent);
            System.out.println(gameState.getStatus());
            System.out.flush();
        } catch (InvalidGameEventException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        gameState = new ClientGameState(pi, "Lyndsay");
        TestMethods.defaultSetup(gameState);
        gameEvent = new GameEvent(GameEventType.ADVANCE_TO_BETTING, null);
        try {
            gameState.invoke(gameEvent);
        } catch (InvalidGameEventException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        /* Final Status Print */
        System.out.println( "Test Complete" );
        System.out.println( "Final Status");
        System.out.print( gameState.getStatus() );
        System.out.flush();
    }
}
