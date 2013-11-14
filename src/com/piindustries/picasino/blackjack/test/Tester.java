/*
 * [Class]
 * [Current Version]
 * [Date last modified]
 *
 * Copyright 2013 - Michael Hoyt, Aaron Jensen, Andrew Reis, and Phillip Sime.
 *
 * This file is part of PiCasino.
 *
 * PiCasino is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PiCasino is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PiCasino.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.piindustries.picasino.blackjack.test;

import com.piindustries.picasino.blackjack.domain.*;

import java.io.IOException;
import java.util.HashMap;

public class Tester {
    ClientTesterServer client;
    ServerTesterServer server;
    boolean quit = false;

    public Tester() {
        client = new ClientTesterServer();
        server = new ServerTesterServer();
        client.server = this.server;
        server.sockets = new HashMap<>();
        server.establishConnection("Test_User",client);
        initialization();
        server.innards.setVerbose(true);
        server.innards.startTimer();
        while(!quit){step();}
    }

    private void initialization(){
        System.out.println("Would you like to add additional players?");
        System.out.println("Yes or No");
        String input = readLine();
        if( input.equalsIgnoreCase("Yes") ){
            System.out.println("Username?");
            input = readLine();
            ClientTesterServer toAdd = new ClientTesterServer();
            toAdd.server = this.server;
            server.establishConnection(input, toAdd );
            System.out.println(client.innards.getMostRecentLog());
            initialization();
        } else if ( !input.equalsIgnoreCase("No") ){
            initialization();
        }
    }

    private GameEvent buildEvent(GameEventType type, Object value ) {
        GameEvent result = new GameEvent();
        result.setType(type);
        result.setValue(value);
        return result;
    }

    public boolean step(){
        if( !client.innards.getValidEvents().isEmpty() && !(client.innards.getPhase() == Phase.INITIALIZATION) ){
            printOptions();
            String input = readLine();
            if( input.equalsIgnoreCase("status") ){
                printStatus();
            } else if ( input.equalsIgnoreCase("verbose") ){
                server.innards.setVerbose( !server.innards.isVerbose() );
            } else if(input.equalsIgnoreCase("quit")){
                quit = true;
            } else if( isValid(input) ){
                switch(client.innards.getPhase()){
                    case INITIALIZATION:

                        break;
                    case BETTING:
                        if( input.equalsIgnoreCase("Bet") ){
                            System.out.println("How Much?");
                            client.send( buildEvent( GameEventType.BET, Integer.valueOf(readLine()) ) );
                        } else if( input.equalsIgnoreCase("Pass") ){
                            client.send( buildEvent( GameEventType.PASS, null ) );
                        } else {
                            System.out.println("Invalid input, try again.");
                            step();
                        }
                        break;
                    case DEALING:

                        break;
                    case PLAYING:
                        if( input.equalsIgnoreCase("Hit") ){
                            client.send( buildEvent( GameEventType.HIT, null ) );
                        } else if( input.equalsIgnoreCase("Stand") ){
                            if( client.innards.getCurrentHand() instanceof DealerHand)
                                server.send( buildEvent( GameEventType.STAND, null ));
                            else
                                client.send( buildEvent( GameEventType.STAND, null ) );
                        } else if( input.equalsIgnoreCase("DoubleDown") ){
                            client.send( buildEvent( GameEventType.DOUBLE_DOWN, null ) );
                        } else if( input.equalsIgnoreCase("Split") ){
                            client.send( buildEvent( GameEventType.SPLIT, null ) );
                        } else {
                            System.out.println("Invalid input, try again.");
                            step();
                        }
                        break;
                    case CONCLUSION:

                        break;
                }
            } else {
                System.out.println( "Invalid input, try again.");
            }
            System.out.println(client.innards.getMostRecentLog());
        }
        return quit;
    }

    private String readLine(){
        StringBuilder sb = new StringBuilder();
        try{
            char focus = (char)System.in.read();
            while( focus != '\n' ){
                sb.append( focus );
                focus = (char)System.in.read();
            }
            if( sb.toString().length() > 0)
                return sb.toString();
        } catch(IOException e) {
            e.printStackTrace();
        }
        return sb.toString().trim();
    }

    private boolean isValid(String e){
        try{
            return client.innards.getValidEvents().contains(GameEventType.valueOf(e.toUpperCase()));
        } catch (IllegalArgumentException exception){
            return false;
        }
    }

    private void printOptions(){
        System.out.println("Available Actions...");
        for( GameEventType str : client.innards.getValidEvents()){
            System.out.println( "\t"+str.name() );
        }
    }

    private void printStatus(){
        System.out.println( client.innards.getPhase().name());
        for( Hand h : this.client.innards.getHands() ){
            System.out.print(h.getUsername()+'\t'+h.getBet()+"\t[" );
            for( int i : h.getCards() )
                System.out.print( " " + Cards.evaluateCardName(i) );
            System.out.print(" ]\n" );
        }
    }

    public static void main(String[] args ){
        new Tester();
    }
}
