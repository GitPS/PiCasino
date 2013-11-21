package com.piindustries.picasino.blackjack.test;

import com.piindustries.picasino.PiCasino;
import com.piindustries.picasino.api.InvalidGameEventException;
import com.piindustries.picasino.blackjack.client.ClientGameState;
import com.piindustries.picasino.blackjack.domain.GameEvent;
import com.piindustries.picasino.blackjack.domain.GameEventType;

public class BetTest {

    public static void main(String[] args){
        PiCasino pi = new PiCasino();
        ClientGameState gameState = new ClientGameState(pi,"Lyndsay");
        TestMethods.defaultSetup(gameState);
        GameEvent gameEvent = new GameEvent(GameEventType.ADVANCE_TO_BETTING, null);
        try {
            gameState.invoke(gameEvent);
            System.out.println(gameState.getStatus());
            System.out.flush();

            for( int i = 0; i <  gameState.getHands().size(); i++ ){
                gameState.invoke(new GameEvent(GameEventType.BET, i * 100));
                System.out.println(gameState.getStatus());
                System.out.flush();
                Thread.sleep(500);
            }
        } catch (InvalidGameEventException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


        /* Final Status Print */
        System.out.println( "Test Complete" );
        System.out.println( "Final Status");
        System.out.print( gameState.getStatus() );
        System.out.flush();
    }
}
