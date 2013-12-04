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

package com.piindustries.picasino.blackjack.server;

import com.piindustries.picasino.PiCasino;
import com.piindustries.picasino.api.InvalidGameEventException;
import com.piindustries.picasino.blackjack.client.ClientGameState;
import com.piindustries.picasino.blackjack.domain.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;

/**
 * A ClientGameState implements ClientGameState.
 *
 * It has an underlying ClientGameState that manages the logic of the
 * game exactly as if were a client.
 *
 * On top of the ClientGameState if broadcasts BJGameEvents to the
 * appropriate clients when required.
 *
 * @see com.piindustries.picasino.api.GameState
 * @author A. Jensen
 * @version 1.0
 */
public class ServerGameState implements com.piindustries.picasino.api.GameState {

    // The underlying gameState
    private ClientGameState gameState;

    // A managed a universal deck of cards
    private ArrayList<Integer> deck;

    // A list of connected players who are waiting to be added to the next game
    private LinkedList<String> waitingList;

    // A global game timer
    private Timer gameTimer;

    // number of seconds between games
    private int intermissionTime = 5;

    private ServerNetworkHandler networkHandler;


    /**
     * Default constructor.  Builds the underlying ClientGameState,
     * Resets its ClientNetworkHandler to a new ClientNetworkHandler and
     * instantiates a deck of cards.
     */
    public ServerGameState(PiCasino pi){
        this.gameState  = new ClientGameState(pi, "$Server");
        this.deck = buildDeck();
        this.gameTimer = new Timer(1000, new Listener() );
        PiCasino.LOGGER.info("Server Game State Constructed");
    }

    // TODO Make sure splitting works as designed
    // TODO Make sure doubling down works as designed
    // TODO Make sure empty games don't explode
    // TODO Make sure empty games caused by disconnects don't explode
    // TODO Make sure empty games caused by passes don't explode

    /**
     * Invokes GameEvents.  First calls the invoke method
     * on its underlying ClientGameState so the logic of
     * the game progresses as it should.
     *
     * Then, handles the event its own manner.
     *
     * @param e the GameEvent to invoke on `this`.
     *
     * @throws InvalidGameEventException
     */
    public synchronized void invoke(com.piindustries.picasino.api.GameEvent e) throws InvalidGameEventException {
        GameEvent event = (GameEvent)e;
        if( handleGlobalEvent(event) )
            return; // If a global event is handled, than there is no need to continue.
        switch( gameState.getPhase() ){
            case INITIALIZATION:
                throw new InvalidGameEventException(event.getType().name());
            case BETTING:
                switch(event.getType()){
                    case BET: bet(event); break;
                    case PASS: pass(); break;
                    default: throw new InvalidGameEventException(event.getType().name());
                }
                break;
            case DEALING:
                // No actions should ever be received by this during the dealing phase
                // because it is dictated by the server and requires no user interaction.
                throw new InvalidGameEventException(event.getType().name());
            case PLAYING:
                switch(event.getType()){
                    case HIT: this.requestCard(); break;
                    // case SEND_CARD: gameState.invoke(event); break; TODO CHECK IF THIS CAN BE REMOVED
                    case STAND: this.stand(event); break;
                    case DOUBLE_DOWN: this.doubleDown(event); break;
                    case SPLIT: this.split(event); break;
                    // case ADVANCE_TO_CONCLUDING: this.advanceToConclusion(); break;  Should be called internally
                    default: throw new InvalidGameEventException(event.getType().name());
                } break;
            case CONCLUSION:
                switch(event.getType()){
                    case ADVANCE_TO_INITIALIZATION: advanceToInitialization(event); break;
                    default: throw new InvalidGameEventException(event.getType().name());
                } break;
            default: throw new Error("Logical Error, Cannot Recover.");
        }
    }

    // TODO comment
    ClientGameState getClientGameState(){ return gameState; }

    // TODO more descriptive comment
    /**
     * Handles any global event
     *
     * @param event the event to handle
     * @return `true` if a global game event is handled, otherwise `false`.
     */
    private boolean handleGlobalEvent(GameEvent event) {
        switch(event.getType()){
            case ADD_PLAYER_TO_WAITING_LIST:
                if( event.getValue() instanceof String )
                    addPlayerToWaitingList( (String)event.getValue() );
                else
                    PiCasino.LOGGER.severe("Player could not be added to the waiting list. Reason: Value does not conform to type String");
                return true;
            case START_TIMER:
                this.startTimer();
                return true;
            case SET_NETWORK_HANDLER:
                if( event.getValue() instanceof ServerNetworkHandler ) {
                    this.setNetworkHandler( (ServerNetworkHandler)event.getValue());
                    PiCasino.LOGGER.info("Server GameState's network handler has been set.");
                } else
                    PiCasino.LOGGER.severe("Server GameState's network handler could not be set. Reason: Value does not conform to type ServerNetworkHandler.");
                return true;
            case SET_INTERMISSION_TIME:
                if( event.getValue() instanceof Integer ) {
                    this.setIntermissionTime( (Integer) event.getValue() );
                    PiCasino.LOGGER.info("Server GameState's network handler has been set.");
                } else
                    PiCasino.LOGGER.severe("Server GameState's network handler could not be set. Reason: Value does not conform to type ServerNetworkHandler.");
                return true;
            default: return false;
        }
    }

    /**
     * Informs clients that it is time to advance to INITIALIZATION phase.
     *
     * @param event a GameEvent whose name is "AdvanceToInitialization"
     * @throws InvalidGameEventException
     */
    private void advanceToInitialization(GameEvent event) throws InvalidGameEventException {
        gameState.invoke(event);
        getNetworkHandler().send(event);
        startTimer();
        this.deck = buildDeck();
    }

    /**
     * Informs all clients to that they should advance to the CONCLUSION phase.
     *
     * @throws InvalidGameEventException
     */
    private void advanceToConclusion() throws InvalidGameEventException {
        GameEvent result = new GameEvent(GameEventType.ADVANCE_TO_CONCLUDING);
        gameState.invoke(result);
        getNetworkHandler().send(result);
        conclude();
    }

    /**
     * Informs all clients that the current player has elected to split their hand.
     *
     * @param event a GameEvent whose name is "Split"
     * @throws InvalidGameEventException
     */
    private void split(GameEvent event) throws InvalidGameEventException {
        // FIXME Behavior unverified
        GameEvent result = new GameEvent();
        result.setType(GameEventType.SPLIT);
        result.setValue(null);
        gameState.invoke(event);
        this.getNetworkHandler().send( result );
        if(gameState.getCurrentHand().isSplit() && gameState.getCurrentHand().getCards().size() < 2){
            while(gameState.getCurrentHand().getCards().size() < 2){
                GameEvent toSend = new GameEvent();
                toSend.setType(GameEventType.SEND_CARD);
                toSend.setValue(getRandomCard());
                gameState.invoke(toSend);
                this.getNetworkHandler().send(toSend);
            }
        }
    }

    /**
     * Informs all clients that the current player has elected to double down
     *
     * @param event a GameEvent whose name is "DoubleDown".
     * @throws InvalidGameEventException
     */
    private void doubleDown(GameEvent event) throws InvalidGameEventException {
        // FIXME Behavior unverified
        GameEvent result = new GameEvent(); // TODO check if i can reuse event
        result.setType(GameEventType.DOUBLE_DOWN);
        result.setValue(null);
        gameState.invoke(event);
        this.getNetworkHandler().send(result);   // TODO check if i can reuse event
        // If it is the dealer's turn to act. Play as the dealer
        if(gameState.getCurrentHand() instanceof DealerHand)
            playDealersHand();

    }

    /**
     * Informs all players that the current player has elected to stand.
     *
     * If the next player is the dealer, `this` will continue to play the
     * dealer's hand.
     *
     * @param event a GameEvent whose name is "Stay"
     * @throws InvalidGameEventException
     */
    private void stand(GameEvent event) throws InvalidGameEventException {
        GameEvent result = new GameEvent(); // TODO check if i can reuse event
        result.setType(GameEventType.STAND);
        result.setValue(null);
        gameState.invoke(event);
        this.getNetworkHandler().send(result); // TODO check if I can just pass the same event on to all other clients.
        // TODO verify that when a player splits, they are dealt a new card.
        if(gameState.getCurrentHand().isSplit() && gameState.getCurrentHand().getCards().size() < 2){
            while(gameState.getCurrentHand().getCards().size() < 2){
                GameEvent toSend = new GameEvent();
                toSend.setType(GameEventType.SEND_CARD);
                toSend.setValue(getRandomCard());
                gameState.invoke(toSend);
                this.getNetworkHandler().send(toSend);
            }
        }
        // If it is the dealer's turn to act. Play as the dealer
        if(gameState.getCurrentHand() instanceof DealerHand)
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
        GameEvent standardEvent = new GameEvent();
        for( Hand h : gameState.getHands() ){
            standardEvent.setType(GameEventType.SEND_CARD);
            standardEvent.setValue(cardVal);
            if( !(h instanceof DealerHand) ){
                this.getNetworkHandler().send( standardEvent, h.getUsername() );
            }
        }
        // Update the underlying gameState
        standardEvent.setValue(cardVal);
        standardEvent.setType(GameEventType.SEND_CARD);
        gameState.invoke(standardEvent);
    }

    /**
     * Bets a default value of 0 for the dealer, and advances
     * the phase of `this` to DEALING.
     *
     * @throws InvalidGameEventException
     */
    private void advanceToDealing() throws InvalidGameEventException {
        GameEvent result = new GameEvent(GameEventType.ADVANCE_TO_DEALING);
        this.gameState.invoke(result);
        this.getNetworkHandler().send(result);
        deal();
    }

    /**
     *  Send a Pass event to all players
     */
    private void pass() throws InvalidGameEventException {
        // FIXME Behavior unverified
        GameEvent result = new GameEvent();
        result.setType(GameEventType.PASS);
        result.setValue(null);
        this.getNetworkHandler().send(result);
        // Update the underlying gameState
        gameState.invoke(result);
        // If the dealer is up to bet.
        if( gameState.getCurrentHand() instanceof DealerHand){
            result.setType(GameEventType.BET);
            result.setValue(0);
            this.gameState.invoke(result);
            this.getNetworkHandler().send(result);
            this.advanceToDealing();
        }
    }

    /**
     * Sends a bet event to all players with the value specified
     *
     * @param event a GameEvent whose name is "Bet"
     */
    private void bet(GameEvent event) throws InvalidGameEventException{
        GameEvent result = new GameEvent();
        result.setType(GameEventType.BET);
        result.setValue( event.getValue() );
        this.getNetworkHandler().send(result);
        // Update the underlying gameState
        synchronized (this){
            gameState.invoke(result);
        }
        // If the dealer is up to bet.
        if( gameState.getCurrentHand() instanceof DealerHand){
            result.setType(GameEventType.BET);
            result.setValue(0);
            this.gameState.invoke(result);
            this.getNetworkHandler().send(result);
            this.advanceToDealing();
        }
    }

    /**
     * Performs concluding tasks.  Append to log.
     * Advances the phase of `this` to INITIALIZATION.
     *
     * @throws InvalidGameEventException
     */
    public void conclude() throws InvalidGameEventException {
        // TODO concluding tasks
        PiCasino.LOGGER.info("Writing back data to database.");
        GameEvent result = new GameEvent();
        result.setType(GameEventType.ADVANCE_TO_INITIALIZATION);
        result.setValue(null);
        this.invoke(result);
    }

    /** Starts the intermission game timer */
    public void startTimer(){
        PiCasino.LOGGER.info("Initializing a new game.");
        PiCasino.LOGGER.info("A new game will begin in " + intermissionTime + " seconds.");
        PiCasino.LOGGER.info("Available Heap Space = " + Runtime.getRuntime().totalMemory() / 1048576.0 + " megabytes.");
        gameTimer.start();
    }

    /**
     * Plays through the dealers hand.
     *
     * @throws InvalidGameEventException
     */
    private void playDealersHand() throws InvalidGameEventException {
        // Ensure the dealer is up
        DealerHand d;
        if( gameState.getCurrentHand() instanceof DealerHand)
            d = (DealerHand) gameState.getCurrentHand();
        else
            throw new Error("Logical error. Cannot recover.");

        // Play dealers hand
        while(d.mustHit()){
            GameEvent toSend = new GameEvent(GameEventType.SEND_CARD,getRandomCard());
            PiCasino.LOGGER.info(gameState.getCurrentUser() + " has been dealt an " + Cards.evaluateCardName((Integer) toSend.getValue()) + ".");
            gameState.invoke(toSend);
            getNetworkHandler().send(toSend);
        }
        PiCasino.LOGGER.info(gameState.getCurrentUser()+" must stand.");

        // Advance Phase
        this.advanceToConclusion();
        PiCasino.LOGGER.info("Advancing phase from PLAYING to CONCLUSION.");
    }

    public void setIsServer(boolean b){
        this.gameState.setIsServer(b);
    }

    /**
     * Deals 1 card at a time to all players in order until all players
     * and the dealer have 2 cards.
     */
    private void deal() throws InvalidGameEventException {
        // Check for empty players list due to players passing in betting round.
        if( gameState.getHands().size() <= 1 ){
            // Deal the dealer 2 cards to maintain gameState order
            GameEvent toSend2 = new GameEvent();
            toSend2.setType(GameEventType.DEAL_CARD);
            toSend2.setValue(0);
            for(int i = 0; i < 2; i++){
                gameState.invoke(toSend2);
                this.getNetworkHandler().send(toSend2);
            }
        } else {
            // While not all players have 2 cards, continue dealing
            while( gameState.getCurrentHand().getCards().size() < 2 ){
                // If this is the first card, it need to be hidden to other players
                if( gameState.getCurrentHand().getCards().size() == 0){
                    GameEvent toSend = new GameEvent();
                    toSend.setType(GameEventType.DEAL_CARD);
                    int card = getRandomCard();
                    // For all hands except the dealer's
                    for(Hand h : gameState.getHands()){
                        if( !(h instanceof DealerHand) ){
                            // If it is the current hand
                            if( h.getUsername().equals(gameState.getCurrentUser()))
                                toSend.setValue(card);  // Actual card value
                            else
                                toSend.setValue(52);    // Hidden card value
                            getNetworkHandler().send(toSend, h.getUsername());
                        }
                    }
                    // Update the server data
                    GameEvent toSend2 = new GameEvent();
                    toSend2.setType(GameEventType.DEAL_CARD);
                    toSend2.setValue(card);
                    gameState.invoke(toSend2);
                } else {
                    GameEvent toSend = new GameEvent();
                    toSend.setType(GameEventType.DEAL_CARD);
                    toSend.setValue(getRandomCard());
                    gameState.invoke(toSend);
                    this.getNetworkHandler().send(toSend);
                }
            }
        }
        // Once every hand has been dealt, Advance Phase
        advanceToPlaying();
    }

    /**
     * Informs all connected clients to advance their
     * phase to PLAYING
     */
    private void advanceToPlaying() throws InvalidGameEventException {
        GameEvent toSend = new GameEvent();
        toSend.setType(GameEventType.ADVANCE_TO_PLAYING);
        this.getNetworkHandler().send(toSend);
        gameState.invoke(toSend);
        // If nobody is playing, advance to conclusion
        if(gameState.getHands().size() <= 1){
            GameEvent conclude = new GameEvent();
            conclude.setType(GameEventType.ADVANCE_TO_CONCLUDING);
            this.advanceToConclusion();
        }
    }

    /**
     * Begin a new game.
     */
    private void beginGame() throws InvalidGameEventException {
        // Advance all client phases
        PiCasino.LOGGER.info("Game Started at "+new Date());
        GameEvent event = new GameEvent();
        if( this.gameState.getHands().size() < 2 ){
            this.startTimer();
        } else {
            event.setType(GameEventType.ADVANCE_TO_BETTING);
            this.getNetworkHandler().send(event);
            this.gameState.invoke(event);
        }
    }

    /** @return `this` network handler */
    public ServerNetworkHandler getNetworkHandler(){
        return this.networkHandler;
    }

    /**
     * Just a setter method.  A dime a dozen.
     *
     * @param toSet the value to set `this.networkHandler` to.
     */
    public void setNetworkHandler(com.piindustries.picasino.api.NetworkHandler toSet){
        if( toSet instanceof ServerNetworkHandler )
            this.networkHandler = (ServerNetworkHandler)toSet;
        else throw new Error("Invalid Network Handler");
    }

    /**
     * @return the phase of the underlying ClientGameState of `this`
     */
    public Phase getPhase(){
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
            PiCasino.LOGGER.info(username + " has been added to the waiting list.");
        } else
            PiCasino.LOGGER.warning("Player "+username+" could not be added to the waiting list.  Reason: A player by that name already exists");
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
        ArrayList<Integer> result = new ArrayList<>();
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
            GameEvent toSend = new GameEvent();
            toSend.setType(GameEventType.ADD_PLAYER);
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
                    PiCasino.LOGGER.severe("InvalidGameEventException caught at game timer event");
                }
            } else if( counter % 10 == 0 ) {
                PiCasino.LOGGER.info("Game will begin in " + counter + " seconds.");
                try {
                    addPlayersFromWaitingListToGame();
                } catch (InvalidGameEventException e1) {
                    PiCasino.LOGGER.severe("InvalidGameEventException caught at game timer event");
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
    void setIntermissionTime(int seconds){
        if( !(seconds < 1) )
            this.intermissionTime = seconds;
    }

}
