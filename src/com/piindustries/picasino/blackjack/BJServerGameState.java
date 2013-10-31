package com.piindustries.picasino.blackjack;

import com.piindustries.picasino.api.GameEvent;
import com.piindustries.picasino.api.GameState;
import com.piindustries.picasino.api.InvalidGameEventException;

import java.util.ArrayList;

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

    // Used to determine when it is time to advance phase
    private int handsPlayed;

    /**
     * Default constructor.  Builds the underlying BJClientGameState,
     * Resets its NetworkHandler to a new BJServerNetworkHandler and
     * instantiates a deck of cards.
     */
    public BJServerGameState(){
        this.gameState  = new BJClientGameState();
        this.gameState.setNetworkHandler( new BJServerNetworkHandler() );
        this.deck = buildDeck();
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
        gameState.invoke(e);

        BJGameEvent event = (BJGameEvent)e;

        switch( gameState.getPhase() ){
            case INITIALIZATION:
                // TODO send player add for players in the waiting list.
                this.deck = buildDeck();    // reset deck
            case BETTING:
                if( event.getName().equals("Bet") ){
                    BJGameEvent result = new BJGameEvent();
                    result.setName("Bet");
                    result.setValue( event.getValue() );
                    gameState.getNetworkHandler().send( result );
                } else if( event.getName().equals("Pass") ){
                    BJGameEvent result = new BJGameEvent();
                    result.setName("Pass");
                    result.setValue( null );
                    gameState.getNetworkHandler().send( result );
                    this.handsPlayed++; // Another hand Serviced  this.handsPlayed will be reset in this.canAdvancePhase()
                }
                break;
            case PLAYING:
                if( event.getName().equals("RequestCard") ){
                    boolean first = true;
                    for( BJClientGameState.Hand h : gameState.getHands() ){
                        if(first) {
                            Integer cardVal = getRandomCard();
                            BJServerGameEvent result = new BJServerGameEvent();
                            result.setName("SendCard");
                            result.setValue(cardVal);
                            result.setToUser( h.getUsername() );
                            gameState.getNetworkHandler().send( result );   // TODO not sure if type eraser will remove necessary data here.
                            first = false;
                        } else {
                            BJServerGameEvent result = new BJServerGameEvent();
                            result.setName("SendCard");
                            result.setValue( 52 );  // Send a hidden card.
                            result.setToUser( h.getUsername() );
                            gameState.getNetworkHandler().send( result );   // TODO not sure if type eraser will remove necessary data here.
                        }
                    }
                } else if( event.getName().equals("SendCard") ) {
                    // Unsupported by this GameState.  Handled by the underlying Client Game State.
                    break;
                } else if( event.getName().equals("Pass") ) {
                    BJGameEvent result = new BJGameEvent();
                    result.setName("Pass");
                    result.setValue( null );
                    gameState.getNetworkHandler().send( result );
                    this.handsPlayed++; // Another hand Serviced.  this.handsPlayed will be reset in this.canAdvancePhase()
                } else if( event.getName().equals("DoubleDown") ) {
                    BJGameEvent result = new BJGameEvent();
                    result.setName("DoubleDown");
                    result.setValue( null );
                    gameState.getNetworkHandler().send( result );
                } else if( event.getName().equals("Split") ) {
                    BJGameEvent result = new BJGameEvent();
                    result.setName("Split");
                    result.setValue( null );
                    gameState.getNetworkHandler().send( result );
                }
                break;
            case CONCLUSION:
                // TODO Determine who won and write their stats to file.
                // Advance Phase Game Event will be broadcasted below
                break;

        }

        // Determine whether it is time to advance phase
        if( canAdvancePhase() ){
            BJGameEvent result = new BJGameEvent();
            result.setName("AdvancePhase");
            result.setValue(null);
            gameState.getNetworkHandler().send(result);
        }
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
        return this.deck.remove((int) (Math.random() * 52));
    }

    /**
     * @return true if and only if it is safe to advance to the next phase of play, otherwise false;
     */
    private boolean canAdvancePhase(){
        switch( this.gameState.getPhase() ){
            case INITIALIZATION:
                return true;  // TODO Verify:  All players in the waiting list should've already been added to the game by this point.
            case BETTING:
                if( this.handsPlayed == gameState.getHands().size() ){
                    this.handsPlayed = 0;
                    return true;
                } else if ( this.handsPlayed < gameState.getHands().size() ){
                    return false;
                } else {
                    throw new IllegalStateException("Hands Played Exceeds Number of Players");
                }
            case PLAYING:
                if( this.handsPlayed == gameState.getHands().size() ){
                    this.handsPlayed = 0;
                    return true;
                } else if ( this.handsPlayed < gameState.getHands().size() ){
                    return false;
                } else {
                    throw new IllegalStateException("Hands Played Exceeds Number of Players");
                }
            case CONCLUSION:
                return true;
            default:
                throw new Error("Unreachable Code Reached");  // Signals Error in logic.
        }
    }
}
