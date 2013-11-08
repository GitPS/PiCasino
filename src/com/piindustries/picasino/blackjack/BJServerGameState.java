/*
 *
 *       ___ _   ___          _
 *      / _ (_) / __\__ _ ___(_)_ __   ___
 *     / /_)/ |/ /  / _` / __| | '_ \ / _ \
 *    / ___/| / /__| (_| \__ \ | | | | (_) |
 *    \/    |_\____/\__,_|___/_|_| |_|\___/
 *
 *
 * Class: com.piindustries.picasino.blackjack.BJServerGameState
 * Version: 1.0
 * Date: October 30, 2013
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

package com.piindustries.picasino.blackjack;

import com.piindustries.picasino.api.GameEvent;
import com.piindustries.picasino.api.GameState;
import com.piindustries.picasino.api.InvalidGameEventException;
import com.piindustries.picasino.api.NetworkHandler;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;

/**
 * A BJServerGameState implements GameState.
 *
 * It has an underlying BJClientGameState that manages the logic of the
 * game exactly as if were a client.
 *
 * On top of the BJClientGameState if broadcasts BJGameEvents to the
 * appropriate clients when required.
 *
 * @see com.piindustries.picasino.api.GameState
 * @author A. Jensen
 * @version 1.0
 */
public class BJServerGameState implements GameState {

    // The underlying gameState
    private BJClientGameState gameState;

    // A managed a universal deck of cards
    private ArrayList<Integer> deck;

    // A list of connected players who are waiting to be added to the next game
    private LinkedList<String> waitingList;

    // A global game timer
    private Timer gameTimer;

    // number of seconds between games
    private int intermissionTime = 5;


    /**
     * Default constructor.  Builds the underlying BJClientGameState,
     * Resets its NetworkHandler to a new BJServerNetworkHandler and
     * instantiates a deck of cards.
     */
    public BJServerGameState(){
        this.gameState  = new BJClientGameState();
        this.gameState.setNetworkHandler( new BJServerNetworkHandler() );
        this.deck = buildDeck();
        this.gameTimer = new Timer(1000, new Listener() );
        this.gameState.appendLog("Server Game State Constructed");
    }

    /**
     * Invokes GameEvents.  First calls the invoke method
     * on its underlying BJClientGameState so the logic of
     * the game progresses as it should.
     *
     * Then, handles the event its own manner.
     *
     * @param e the GameEvent to invoke on `this`.
     *
     * @throws InvalidGameEventException
     */
    public void invoke(GameEvent e) throws InvalidGameEventException {
        BJGameEvent event = (BJGameEvent)e;
        switch( gameState.getPhase() ){
            case INITIALIZATION:
                if( !gameState.getValidEvents().contains(event.getName()))
                    throw new InvalidGameEventException(event.getName());
                gameState.invoke(e);
                break;  // Break Initialization phase
            case BETTING:
                if( event.getName().equals("Bet") )
                    this.bet(event);
                else if( event.getName().equals("Pass") )
                    this.pass();
                else
                    throw new InvalidGameEventException(event.getName());
                if( gameState.getHands().getFirst() instanceof BJClientGameState.DealerHand)    // If the dealer is up to bet.
                    this.advanceToDealing();
                break;
            case DEALING:
                throw new InvalidGameEventException(event.getName());   // No actions should ever be received by this during the dealing phase.
            case PLAYING:
                if( event.getName().equals("RequestCard") )
                    this.requestCard();
                else if( event.getName().equals("SendCard") )
                    gameState.invoke(event);
                else if( event.getName().equals("Stay") )
                    this.stay(event);
                else if( event.getName().equals("DoubleDown") )
                    this.doubleDown(event);
                else if( event.getName().equals("Split") )
                    this.split(event);
                else if( event.getName().equals("AdvanceToConclusion") )
                    this.advanceToConclusion(event);
                else
                    throw new InvalidGameEventException(event.getName());
                break;
            case CONCLUSION:
                if( event.getName().equals("AdvanceToInitialization") )
                    this.advanceToInitialization(event);
                else
                    throw new InvalidGameEventException(event.getName());
                break;
            default:
                throw new Error("Logical Error, Cannot Recover.");
        }
    }

    /**
     * Informs clients that it is time to advance to INITIALIZATION phase.
     *
     * @param event a BJGameEvent whose name is "AdvanceToInitialization"
     * @throws InvalidGameEventException
     */
    private void advanceToInitialization(BJGameEvent event) throws InvalidGameEventException {
        gameState.invoke(event);
        getNetworkHandler().send(event);
        startTimer();
    }

    /**
     * Informs all clients to that they should advance to the CONCLUSION phase.
     *
     * @param event a BJGameEvent whose name "AdvanceToConclusion".
     * @throws InvalidGameEventException
     */
    private void advanceToConclusion(BJGameEvent event) throws InvalidGameEventException {
        gameState.invoke(event);
        getNetworkHandler().send(event);
        conclude();
    }

    /**
     * Informs all clients that the current player has elected to split their hand.
     *
     * @param event a BJGameEvent whose name is "Split"
     * @throws InvalidGameEventException
     */
    private void split(BJGameEvent event) throws InvalidGameEventException {
        BJGameEvent result = new BJGameEvent();
        result.setName("Split");
        result.setValue(null);
        gameState.invoke(event);
        gameState.getNetworkHandler().send( result );
    }

    /**
     * Informs all clients that the current player has elected to double down
     *
     * @param event a BJGameEvent whose name is "DoubleDown".
     * @throws InvalidGameEventException
     */
    private void doubleDown(BJGameEvent event) throws InvalidGameEventException {
        BJGameEvent result = new BJGameEvent(); // TODO check if i can reuse event
        result.setName("DoubleDown");
        result.setValue(null);
        gameState.invoke(event);
        gameState.getNetworkHandler().send(result);   // TODO check if i can reuse event
        // If it is the dealer's turn to act. Play as the dealer
        if(gameState.getCurrentHand() instanceof BJClientGameState.DealerHand)
            playDealersHand();

    }

    /**
     * Informs all players that the current player has elected to stay.
     *
     * If the next player is the dealer, `this` will continue to play the
     * dealer's hand.
     *
     * @param event a BJGameEvent whose name is "Stay"
     * @throws InvalidGameEventException
     */
    private void stay(BJGameEvent event) throws InvalidGameEventException {
        BJGameEvent result = new BJGameEvent(); // TODO check if i can reuse event
        result.setName("Stay");
        result.setValue(null);
        gameState.invoke(event);
        gameState.getNetworkHandler().send(result); // TODO check if I can just pass the same event on to all other clients.
        // If it is the dealer's turn to act. Play as the dealer
        if(gameState.getCurrentHand() instanceof BJClientGameState.DealerHand)
            playDealersHand();
    }

    /**
     * Deals a card back to the requesting player and informs all other players
     * about it.
     *
     * @throws InvalidGameEventException
     */
    private void requestCard() throws InvalidGameEventException {
        Integer cardVal = getRandomCard();
        BJDirectedGameEvent directedEvent = new BJDirectedGameEvent();
        BJGameEvent standardEvent = new BJGameEvent();
        for( BJClientGameState.Hand h : gameState.getHands() ){
            directedEvent.setName("SendCard");
            directedEvent.setValue(cardVal);
            if( !(h instanceof BJClientGameState.DealerHand) ){
                directedEvent.setToUser(h.getUsername());
                gameState.getNetworkHandler().send( directedEvent );
            }
        }
        // Update the underlying gameState
        standardEvent.setValue(cardVal);
        standardEvent.setName("SendCard");
        gameState.invoke(standardEvent);
    }

    /**
     * Bets a default value of 0 for the dealer, and advances
     * the phase of `this` to DEALING.
     *
     * @throws InvalidGameEventException
     */
    private void advanceToDealing() throws InvalidGameEventException {
        BJGameEvent result = new BJGameEvent();
        result.setName("Bet");
        result.setValue(0);
        this.gameState.invoke(result);
        this.getNetworkHandler().send(result);
        result.setName("AdvanceToDealing");
        this.gameState.invoke(result);
        this.getNetworkHandler().send(result);
        deal();
    }

    /**
     *  Send a Pass event to all players
     */
    private void pass(){
        BJGameEvent result = new BJGameEvent();
        result.setName("Pass");
        result.setValue(null);
        gameState.getNetworkHandler().send(result);
        // Update the underlying gameState
    }

    /**
     * Sends a bet event to all players with the value specified
     *
     * @param event a BJGameEvent whose name is "Bet"
     */
    private void bet(BJGameEvent event) throws InvalidGameEventException{
        BJGameEvent result = new BJGameEvent();
        result.setName("Bet");
        result.setValue( event.getValue() );
        gameState.getNetworkHandler().send(result);
        // Update the underlying gameState
        gameState.invoke(result);
    }

    /**
     * Performs concluding tasks.  Append to log.
     * Advances the phase of `this` to INITIALIZATION.
     *
     * @throws InvalidGameEventException
     */
    public void conclude() throws InvalidGameEventException {
        // TODO concluding tasks
        gameState.appendLog("Writing back data to database.");
        BJGameEvent result = new BJGameEvent();
        result.setName("AdvanceToInitialization");
        result.setValue(null);
        this.invoke( result );
    }

    /** Starts the intermission game timer */
    public void startTimer(){
        gameState.appendLog("Initializing a new game.");
        System.out.println("A new game will begin in " + intermissionTime + " seconds.");
        gameTimer.start();
    }

    /**
     * @return `true` if `this` is set to behave verbosely,
     * otherwise `false`.
     */
    public boolean isVerbose(){
        return this.gameState.isVerbose();
    }

    /**
     * @param toSet a boolean.  If true, this game state will
     *                print all new log events to the standard
     *                output console, otherwise logging will take
     *                place silently.
     */
    public void setVerbose(boolean toSet){
        this.gameState.setVerbose(toSet);
    }

    /**
     * Plays through the dealers hand.
     *
     * @throws InvalidGameEventException
     */
    private void playDealersHand() throws InvalidGameEventException {
        // Ensure the dealer is up
        BJClientGameState.DealerHand d;
        if( gameState.getCurrentHand() instanceof BJClientGameState.DealerHand )
            d = (BJClientGameState.DealerHand) gameState.getCurrentHand();
        else
            throw new Error("Logical error. Cannot recover.");

        // Play dealers hand
        while(d.mustHit()){
            BJGameEvent toSend = new BJGameEvent();
            toSend.setName("SendCard");
            toSend.setValue(getRandomCard());
            gameState.invoke(toSend);
            getNetworkHandler().send(toSend);
        }

        // Advance Phase
        BJGameEvent toSend = new BJGameEvent();
        toSend.setName("AdvanceToConclusion");
        this.invoke(toSend);
    }

    /**
     * Deals 1 card at a time to all players in order until all players
     * and the dealer have 2 cards.
     */
    private void deal() throws InvalidGameEventException {
        // While not all players have 2 cards, continue dealing
        while( gameState.getCurrentHand().getCards().size() < 2 ){
            // If this is the first card, it need to be hidden to other players
            if( gameState.getCurrentHand().getCards().size() == 0){
                BJDirectedGameEvent toSend = new BJDirectedGameEvent();
                toSend.setName("SendCard");
                int card = getRandomCard();
                // For all hands except the dealer's
                for(BJClientGameState.Hand h : gameState.getHands()){
                    if( !(h instanceof BJClientGameState.DealerHand) ){
                        toSend.setToUser(h.getUsername());
                        // If it is the current hand
                        if( h.getUsername().equals(gameState.getCurrentUser()))
                            toSend.setValue(card);  // Actual card value
                        else
                            toSend.setValue(52);    // Hidden card value
                        getNetworkHandler().send(toSend);
                    }
                }

                // Update the server data
                BJGameEvent toSend2 = new BJGameEvent();
                toSend2.setName("SendCard");
                toSend2.setValue(card);
                gameState.invoke(toSend2);
            } else {
                BJGameEvent toSend = new BJGameEvent();
                toSend.setName("SendCard");
                toSend.setValue(getRandomCard());
                gameState.invoke(toSend);
                this.getNetworkHandler().send(toSend);
            }
        }
        // Once every hand has been dealt, Advance Phase
        BJGameEvent toSend = new BJGameEvent();
        toSend.setName("AdvanceToPlaying");
        this.getNetworkHandler().send(toSend);
        gameState.invoke(toSend);
    }

    /**
     * Begin a new game.
     */
    private void beginGame() throws InvalidGameEventException {
        // Advance all client phases
        this.gameState.appendLog("Game Started at "+new Date());
        BJGameEvent event = new BJGameEvent();
        event.setName("AdvanceToBetting");
        this.getNetworkHandler().send(event);
        this.gameState.invoke(event);
    }

    /**
     * @return a clone of this
     * @throws CloneNotSupportedException
     * @see java.lang.Object#clone()
     */
    public BJServerGameState clone() throws CloneNotSupportedException {
        return (BJServerGameState)super.clone();
    }

    /** @return `this` network handler */
    public NetworkHandler getNetworkHandler(){
        return this.gameState.getNetworkHandler();
    }

    /**
     * Just a setter method.  A dime a dozen.
     *
     * @param toSet the value to set `this.networkHandler` to.
     */
    public void setNetworkHandler(NetworkHandler toSet){
        this.gameState.setNetworkHandler(toSet);
    }

    /**
     * @return the phase of the underlying GameState of `this`
     */
    public BJClientGameState.BJPhases getPhase(){
        return this.gameState.getPhase();
    }

    /**
     * Adds a player with the specified user name to the waiting list
     * and appends to the log of `this`.
     *
     * @param username the username of the player to add to the list.
     */
    public void addPlayerToWaitingList(String username){
        if( !getWaitingList().contains(username)){
            this.getWaitingList().add(username);
        }
        this.gameState.appendLog(username + " has been added to the waiting list.");
    }

    /**
     * Removes the player with the specified user name from the waiting list
     * if there is a player with the given username in the waiting list, otherwise
     * it nothing happens.
     *
     * @param username the username of the player to remove from the waiting list.
     * @return `true` if the player was removed, otherwise `false`.
     */
    public boolean removePlayerFromWaitingList(String username){
        return this.getWaitingList().remove(username);
    }

    /**
     * Builds a deck containing all 52 cards.
     *
     * @return a deck containing all 52 cards.
     */
    private ArrayList<Integer> buildDeck(){
        ArrayList<Integer> result = new ArrayList<Integer>();
        for( int i = 0; i < 52; i++ )
            result.add(i);
        return result;
    }

    /**
     * @return a random card from the deck
     */
    private Integer getRandomCard(){
        return this.deck.remove( (int)(Math.random()* deck.size() ) );
    }

    /**
     * Adds all players from the waiting list to the game
     */
    private void addPlayersFromWaitingListToGame() throws InvalidGameEventException {
        for( String player: getWaitingList()){
            BJGameEvent toSend = new BJGameEvent();
            toSend.setName("AddPlayer");
            toSend.setValue(player);
            gameState.invoke(toSend);
            getNetworkHandler().send(toSend);
        }
        getWaitingList().clear();
    }

    /**
     * Lazily returns the waiting list
     *
     * @return the waiting list, or a new waiting list if none
     * exists.
     */
    private LinkedList<String> getWaitingList() {
        if( this.waitingList == null )
            this.setWaitingList(new LinkedList<String>());
        return waitingList;
    }

    /**
     * Sets the waiting list.
     *
     * @param waitingList the list to set the waiting list to.
     */
    private void setWaitingList(LinkedList<String> waitingList) {
        this.waitingList = waitingList;
    }

    /**
     * A simple listener that responds to gameTimer events.
     */
    private class Listener implements ActionListener {
        private int counter = intermissionTime;

        @Override
        public void actionPerformed(ActionEvent e) {
            if( counter == 0 ){
                counter = intermissionTime + 1;
                gameTimer.stop();
                try {
                    addPlayersFromWaitingListToGame();
                    beginGame();
                } catch (InvalidGameEventException e1) {
                    System.err.println("InvalidGameEventException caught at game timer event");
                }
            } else if( counter % 10 == 0 ) {
                System.out.println("Game will begin in "+counter+" seconds.");
                try {
                    addPlayersFromWaitingListToGame();
                } catch (InvalidGameEventException e1) {
                    System.err.println("InvalidGameEventException caught at game timer event");
                }
            }
            counter--;
        }
    }

    /**
     * Must be greater than 0.
     *
     * @param seconds the number of seconds to wait in between
     *                games.
     */
    public void setIntermissionTime(int seconds){
        if( !(seconds < 1) )
            this.intermissionTime = seconds;
    }

}
