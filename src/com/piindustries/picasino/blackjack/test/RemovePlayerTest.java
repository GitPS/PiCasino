package com.piindustries.picasino.blackjack.test;

import com.piindustries.picasino.PiCasino;
import com.piindustries.picasino.api.InvalidGameEventException;
import com.piindustries.picasino.blackjack.client.ClientGameState;
import com.piindustries.picasino.blackjack.domain.GameEvent;
import com.piindustries.picasino.blackjack.domain.GameEventType;

public class RemovePlayerTest {

    public synchronized static void main(String[] args){
        PiCasino pi = new PiCasino();
        ClientGameState gameState = new ClientGameState(pi,"Lyndsay");
        String[] names = new String[]{ "Aaron", "Aaron", "Aaron", "Lyndsay", "Phil", "Mike", "Andrew" };
        for( String s: names ){
            try {
                GameEvent event = new GameEvent();
                event.setType(GameEventType.ADD_PLAYER);
                event.setValue(s);
                gameState.invoke(event);
            } catch (InvalidGameEventException e){
                System.out.println( "Invalid Game Event Caught" );
                System.out.flush();
            }
        }

        System.out.println( "Initial State" );
        System.out.println( gameState.getStatus() );
        for( String s: names ){
            try {
                GameEvent event = new GameEvent();
                event.setType(GameEventType.REMOVE_PLAYER);
                event.setValue(s);
                int preSize = gameState.getHands().size();
                gameState.invoke(event);
                if( preSize == gameState.getHands().size() ){System.out.println( "No Player Removed" );}
                else { System.out.println(s + " removed."); }
                System.out.println( gameState.getStatus() );
                System.out.flush();
            } catch (InvalidGameEventException e){
                System.out.println( "Invalid Game Event Caught" );
                System.out.flush();
            }
        }

        /* Final Status Print */
        System.out.println( "Test Complete" );
        System.out.println( "Final Status");
        System.out.print( gameState.getStatus() );
        System.out.flush();
    }
}