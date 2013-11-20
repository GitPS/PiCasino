package com.piindustries.picasino.blackjack.test;

import com.piindustries.picasino.api.InvalidGameEventException;
import com.piindustries.picasino.blackjack.client.ClientGameState;
import com.piindustries.picasino.blackjack.domain.GameEvent;
import com.piindustries.picasino.blackjack.domain.GameEventType;

public class TestMethods {


    public static void defaultSetup(ClientGameState e){
        String[] names = new String[]{ "Aaron", "Lyndsay", "Phil", "Mike", "Andrew" };
        for( String s: names ){
            try {
                GameEvent event = new GameEvent();
                event.setType(GameEventType.ADD_PLAYER);
                event.setValue(s);
                e.invoke(event);
            } catch (InvalidGameEventException e){
                System.out.println( "Invalid Game Event Caught" );
                System.out.flush();
            }
        }
    }
}
