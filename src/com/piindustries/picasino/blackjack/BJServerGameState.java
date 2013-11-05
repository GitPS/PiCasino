package com.piindustries.picasino.blackjack;

import com.piindustries.picasino.api.GameEvent;
import com.piindustries.picasino.api.GameState;
import com.piindustries.picasino.api.InvalidGameEventException;
import com.piindustries.picasino.api.NetworkHandler;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
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
                if( event.getName().equals("Bet") ){
                    // Send a a Bet event to all players with the value specified
                    BJGameEvent result = new BJGameEvent();
                    result.setName("Bet");
                    result.setValue( event.getValue() );
                    gameState.getNetworkHandler().send( result );
                    // Update the underlying gameState
                    gameState.invoke(result);
                } else if( event.getName().equals("Pass") ){
                    // Send a Pass event to all players
                    BJGameEvent result = new BJGameEvent();
                    result.setName("Pass");
                    result.setValue(null);
                    gameState.getNetworkHandler().send( result );
                    // Update the underlying gameState
                } else {
                    throw new InvalidGameEventException(event.getName());
                }

                // If the dealer is up to bet, Advance to dealing
                if( gameState.getHands().getFirst() instanceof BJClientGameState.DealerHand){
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
                break;  //BREAK BETTING CASE
            case DEALING:
                // No actions should ever be received by this during the dealing phase.
                throw new InvalidGameEventException(event.getName());
            case PLAYING:
                if( event.getName().equals("RequestCard") ){
                    Integer cardVal = getRandomCard();
                    BJDirectedGameEvent directedEvent = new BJDirectedGameEvent();
                    BJGameEvent standardEvent = new BJGameEvent();
                    // Send cards to all other players except the dealer
                    for( BJClientGameState.Hand h : gameState.getHands() ){
                        directedEvent.setName("SendCard");
                        directedEvent.setValue(cardVal);
                        if( !(h instanceof BJClientGameState.DealerHand) ){
                            directedEvent.setToUser(h.getUsername());
                            gameState.getNetworkHandler().send( directedEvent );   // TODO not sure if type eraser will remove necessary data here.
                        }
                    }
                    // Update the underlying gameState
                    standardEvent.setValue(cardVal);
                    standardEvent.setName("SendCard");
                    gameState.invoke(standardEvent);
                } else if( event.getName().equals("SendCard") ) {
                    gameState.invoke(event);
                } else if( event.getName().equals("Stay") ) {
                    BJGameEvent result = new BJGameEvent();
                    result.setName("Stay");
                    result.setValue(null);
                    gameState.invoke(event);
                    gameState.getNetworkHandler().send(result);
                    // If it is the dealer's turn to act. Play as the dealer
                    if(gameState.getCurrentHand() instanceof BJClientGameState.DealerHand){
                        playDealersHand();
                    }
                } else if( event.getName().equals("DoubleDown") ) {
                    BJGameEvent result = new BJGameEvent();
                    result.setName("DoubleDown");
                    result.setValue(null);
                    gameState.invoke(event);
                    gameState.getNetworkHandler().send( result );
                    // If it is the dealer's turn to act. Play as the dealer
                    if(gameState.getCurrentHand() instanceof BJClientGameState.DealerHand){
                        playDealersHand();
                    }
                } else if( event.getName().equals("Split") ) {
                    BJGameEvent result = new BJGameEvent();
                    result.setName("Split");
                    result.setValue(null);
                    gameState.invoke(event);
                    gameState.getNetworkHandler().send( result );
                } else if( event.getName().equals("AdvanceToConclusion") ) {
                    gameState.invoke(event);
                    getNetworkHandler().send(event);
                    conclude();
                } else {
                    throw new InvalidGameEventException(event.getName());
                }
                break;
            case CONCLUSION:
                if( event.getName().equals("AdvanceToInitialization") ){
                    gameState.invoke(event);
                    getNetworkHandler().send(event);
                    startTimer();
                } else {
                    throw new InvalidGameEventException(event.getName());
                }
                break;
        }
    }

    public void conclude() throws InvalidGameEventException {
        // TODO concluding tasks
        BJGameEvent result = new BJGameEvent();
        result.setName("AdvanceToInitialization");
        result.setValue(null);
        this.invoke( result );
    }

    public void startTimer(){
        System.out.println("Game will begin in "+intermissionTime+" seconds.");
        gameTimer.start();
    }

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
        this.gameState.appendLog("Game Started at "+System.currentTimeMillis());
        BJGameEvent event = new BJGameEvent();
        event.setName("AdvanceToBetting");
        this.getNetworkHandler().send(event);
        this.gameState.invoke(event);
    }

    public BJServerGameState clone() throws CloneNotSupportedException {
        return (BJServerGameState)super.clone();
    }

    public NetworkHandler getNetworkHandler(){
        return this.gameState.getNetworkHandler();
    }

    public void setNetworkHandler(NetworkHandler nh){
        this.gameState.setNetworkHandler(nh);
    }

    public BJClientGameState.BJPhases getPhase(){
        return this.gameState.getPhase();
    }

    //TODO comment
    public void addPlayerToWaitingList(String username){
        if( !getWaitingList().contains(username)){
            this.getWaitingList().add(username);
        }
        this.gameState.appendLog(username + " has been added to the waiting list.");
    }

    //TODO comment
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

    private LinkedList<String> getWaitingList() {
        if( this.waitingList == null )
            this.waitingList = new LinkedList<String>();
        return waitingList;
    }

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
     * @param seconds
     */
    public void setIntermissionTime(int seconds){
        if( !(seconds < 1) )
            this.intermissionTime = seconds;
    }

}
