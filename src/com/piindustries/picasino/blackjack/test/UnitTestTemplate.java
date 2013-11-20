package com.piindustries.picasino.blackjack.test;

import com.piindustries.picasino.PiCasino;
import com.piindustries.picasino.blackjack.client.ClientGameState;

public class UnitTestTemplate {

    public synchronized static void main(String[] args){
        PiCasino pi = new PiCasino();
        ClientGameState gameState = new ClientGameState(pi,"Lyndsay");


        /* Final Status Print */
        System.out.println( "Test Complete" );
        System.out.println( "Final Status");
        System.out.print( gameState.getStatus() );
        System.out.flush();
    }
}
