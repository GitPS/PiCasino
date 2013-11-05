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

import com.piindustries.picasino.blackjack.BJClientGameState;
import com.piindustries.picasino.blackjack.BJGameEvent;

import java.io.Console;
import java.io.IOException;
import java.util.HashMap;

public class BJTester2 {
    ClientTesterServer client;
    ServerTesterServer server;
    boolean verbose = false;
    boolean quit = false;

    public BJTester2() {
        client = new ClientTesterServer();
        server = new ServerTesterServer();
        client.server = this.server;
        server.sockets = new HashMap<String, ClientTesterServer>();
        server.establishConnection("Test_User",client);
        BJGameEvent event = new BJGameEvent();
        event.setName("AddPlayer");
        event.setValue("Test_User");
        server.receive(event);
        server.send(event);
        while(!quit){step();}
    }

    private synchronized BJGameEvent buildEvent(String name, Object value ) {
        BJGameEvent result = new BJGameEvent();
        result.setName(name);
        result.setValue(value);
        return result;
    }

    public synchronized boolean step(){
        if( !client.innards.getValidEvents().isEmpty() && !(client.innards.getPhase() == BJClientGameState.BJPhases.INITIALIZATION) ){
            printOptions();
            String input = readLine();
            if( isValid(input) ){
                switch(client.innards.getPhase()){
                    case INITIALIZATION:

                        break;
                    case BETTING:
                        if( input.equals("Bet") ){
                            System.out.println("How Much?");
                            client.send( buildEvent( "Bet", Integer.valueOf(readLine()) ) );
                        } else if( input.equals("Pass") ){
                            client.send( buildEvent( "Pass", null ) );
                        } else {
                            System.out.println("Invalid input, try again.");
                            step();
                        }
                        break;
                    case DEALING:

                        break;
                    case PLAYING:

                        break;
                    case CONCLUSION:

                        break;
                }
            } else {
                System.out.println( "Invalid input, try again.");
            }
        }
        if( verbose){
            printStatus();
        }
        return quit;
    }

    private synchronized String readLine(){
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

    private synchronized void writeLine(String s){
        System.out.println(s);
    }

    private synchronized boolean isValid(String s){
        return client.innards.getValidEvents().contains(s);
    }

    private synchronized void printOptions(){
        System.out.println("Available Actions...");
        for( String str : client.innards.getValidEvents()){
            System.out.println( "\t"+str );
        }
    }

    private synchronized void printStatus(){
        System.out.println("\t-Status-");
        System.out.println('\t' + client.innards.getPhase().name());
        for( BJClientGameState.Hand h : this.client.innards.getHands() ){
            System.out.print( "\t"+h.getUsername()+'\t'+h.getBet()+"\t[ " );
            for( int i : h.getCards() )
                System.out.print(i+'\t');
            System.out.print(" ]\n");
        }
    }

    public static void main(String[] args ){
        new BJTester2();
    }
}
