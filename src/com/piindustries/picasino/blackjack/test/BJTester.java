package com.piindustries.picasino.blackjack.test;

import com.piindustries.picasino.blackjack.BJCards;
import com.piindustries.picasino.blackjack.BJClientGameState;
import com.piindustries.picasino.blackjack.BJGameEvent;

import java.io.IOException;

public class BJTester {
    private BJClientGameState gameState;
    private boolean verbose;
    private boolean quit;

    public BJTester(){
        System.out.println("\nThis Debugger is case sensitive");
        System.out.println("At any time throughout the simulation you can type \n \t\"quit\", \t\"status\", \t\"verbose\"\t\"initial\".\n");
        gameState = new BJClientGameState();
        verbose = false;
        quit = false;
        step();
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

    public void step(){
        System.out.println( "Available Actions..." );
        String input;
        switch( gameState.getPhase() ){
            case INITIALIZATION:
                printOption(gameState.getValidEvents());
                input = this.readLine();
                if( isValid( gameState.getPhase(), input ) ){
                    BJGameEvent event = new BJGameEvent();
                    event.setName(input);
                    if( input.equals("AdvanceToBetting") ){
                        gameState.invoke( event );
                    } else {
                        System.out.println("What is your username?");
                        String user = this.readLine();
                        event.setValue(user);
                        gameState.invoke( event );
                    }
                } else {
                    System.out.println( "Invalid Entry, try again");
                    step();
                }
                break;
            case BETTING:
                printOption(gameState.getValidEvents());
                input = this.readLine();
                if( isValid( gameState.getPhase(), input ) ){
                    BJGameEvent event = new BJGameEvent();
                    event.setName(input);
                    if( input.equals("Pass") ){
                        gameState.invoke( event );
                    } else {
                        System.out.println("What value is your bet?");
                        Integer val = Integer.valueOf(this.readLine());
                        event.setValue(val);
                        gameState.invoke( event );
                    }
                } else {
                    System.out.println( "Invalid Entry, try again");
                    step();
                }
                break;
            case DEALING:
                BJGameEvent event2 = new BJGameEvent();
                event2.setName( "SendCard" );
                event2.setValue( (int)(Math.random() * 52) );
                gameState.invoke( event2 );
                break;
            case PLAYING:
                printOption(gameState.getValidEvents());
                input = this.readLine();
                if( isValid( gameState.getPhase(), input ) ){
                    BJGameEvent event = new BJGameEvent();
                    event.setName(input);
                    if( input.equals("Stay") || input.equals("DoubleDown") || input.equals("Split") ){
                        gameState.invoke(event);
                    } else if( input.equals("DoubleDown") ){
                        System.out.println("Doubling Down is not supported in this simulator yet");
                    } else if( input.equals("Split") ){
                        System.out.println("Splitting is not supported in this simulator yet");
                    } else if( input.equals("RequestCard") ){
                        event.setValue(null);
                        gameState.invoke(event);
                        System.out.println(gameState.getMostRecentLog());
                        System.out.println( "Automatically Sending Random Card" );
                        BJGameEvent send = new BJGameEvent();
                        send.setName("SendCard");
                        send.setValue((int)(Math.random()*52));
                        gameState.invoke(send);
                    } else {
                        System.out.println("What is the id of your card [0,51]?");
                        Integer val = Integer.valueOf(this.readLine());
                        event.setValue(val);
                        gameState.invoke( event );
                    }
                } else {
                    System.out.println( "Invalid Entry, try again");
                    step();
                }
                break;
            case CONCLUSION:
                printOption( gameState.getValidEvents() );
                System.out.println("Simulation is automatically advancing the phase");
                BJGameEvent event = new BJGameEvent();
                event.setName("AdvanceToInitialization");
                gameState.invoke(event);
                break;
            default:
                throw new Error("BAD");
        }
        System.out.println(this.gameState.getMostRecentLog());
        if( this.verbose )
            printStatus();
        if( !this.quit )
            step();
    }

    private boolean isValid(com.piindustries.picasino.blackjack.BJClientGameState.BJPhases phase, String input ){
        if( input.equals("quit")){
            this.quit = true;
        } else if( input.equals("status")){
            printStatus();
            step();
        } else if( input.equals("verbose")){
            this.verbose = !verbose;
            step();
        } else if( input.equals("initial")){
            gameState = new BJClientGameState();
            BJGameEvent e = new BJGameEvent();
            e.setName("AddPlayer");
            e.setValue("Aaron");
            gameState.invoke(e);
            System.out.println(gameState.getMostRecentLog());
            e.setValue("Andrew");
            gameState.invoke(e);
            System.out.println(gameState.getMostRecentLog());
            e.setValue("Mike");
            gameState.invoke(e);
            System.out.println(gameState.getMostRecentLog());
            e.setValue("Phil");
            gameState.invoke(e);
            System.out.println(gameState.getMostRecentLog());
            e.setName("AdvancePhase");
            gameState.invoke(e);
            System.out.println(gameState.getMostRecentLog());
            e.setName("Bet");
            e.setValue(100);
            gameState.invoke(e);
            System.out.println(gameState.getMostRecentLog());
            e.setValue(110);
            gameState.invoke(e);
            System.out.println(gameState.getMostRecentLog());
            e.setValue(120);
            gameState.invoke(e);
            System.out.println(gameState.getMostRecentLog());
            e.setValue(130);
            gameState.invoke(e);
            System.out.println(gameState.getMostRecentLog());
        }
        switch( phase ){
            case INITIALIZATION:
                for( String s : gameState.getValidEvents() )
                    if( s.equals(input) )
                        return true;
                return false;
            case BETTING:
                for( String s : gameState.getValidEvents() )
                    if( s.equals(input) )
                        return true;
                return false;
            case DEALING:
                for( String s: gameState.getValidEvents() )
                    if( s.equals(input))
                        return true;
                return false;
            case PLAYING:
                for( String s : gameState.getValidEvents() )
                    if( s.equals(input) )
                        return true;
                return false;
            case CONCLUSION:
                for( String s : gameState.getValidEvents() )
                    if( s.equals(input) )
                        return true;
                return false;
            default:
                throw new Error("BAD");
        }
    }

    private void printOption(java.util.LinkedList<String> s){
        for( String str : s){
            System.out.println( "\t"+str );
        }
    }

    private void printStatus(){
        System.out.println("\tCurrent Phase: "+gameState.getPhase().name());
        System.out.println("\tPlayer: \tBet: \tHand: ");
        for(BJClientGameState.Hand h: gameState.getHands()){
            String toPrint = "\t"+h.getUsername() + '\t' + h.getBet() + "\t";
            for( Integer i : h.getCards() )
                toPrint += "[ " + i % 13 + " of "+ BJCards.evaluateCardSuit(i)+"s ]";
            System.out.println(toPrint);
        }
    }

    public static void main(String[] args ){
        new BJTester();
    }

}
