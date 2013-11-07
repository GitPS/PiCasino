/*
 *
 *       ___ _   ___          _
 *      / _ (_) / __\__ _ ___(_)_ __   ___
 *     / /_)/ |/ /  / _` / __| | '_ \ / _ \
 *    / ___/| / /__| (_| \__ \ | | | | (_) |
 *    \/    |_\____/\__,_|___/_|_| |_|\___/
 *
 *
 * Class: com.piindustries.picasino.blackjack.BJClientGameState
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

import java.io.Serializable;
import java.util.LinkedList;
import java.util.NoSuchElementException;

/**
 * A Client-Side {@link GameState}. For blackjack.
 * <p>
 * A {@link BJClientGameState} will strictly and exclusively manage all data needed
 * to determine the state of a Blackjack game.  A {@link BJClientGameState} will
 * also manage micro-transitions between states within a given phase of the game.
 * A {@link BJClientGameState} will operate in tandem with a {@link BJServerGameState}
 * to determine when to transition between phases of a blackjack game.
 * </p>
 * <p>
 * A {@link BJClientGameState} can be and must be in exactly one {@link BJPhases} at a
 * given time.  What a {@link BJClientGameState} can handle during any of the specified
 * phases will be described in detail below.
 * </p>
 *
 * <pre>{{{
 *     During the {@link BJPhases#INITIALIZATION} phase, a {@link BJClientGameState} can
 *     receive the following {@link BJGameEvent}s.
 *       - A {@link BJGameEvent} whose name is "AddPlayer" and whose value is of type
 *         {@link String}.  The value should be the username of the player to be added.
 *         When "AddPlayer" is invoked during this phase, A hand is created for a player
 *         with the specified username.
 *       - A {@link BJGameEvent} whose name is "RemovePlayer" and whose value is of type
 *         {@link String}.  The value should be the username of the player to be removed.
 *         When "RemovePlayer" is invoked during this phase, Any hand mapped to the specified
 *         username will be removed from this {@link BJClientGameState}.
 *       - A {@link BJGameEvent} whose name is "AdvanceToBetting" and whose value is of any
 *         type.  When "AdvanceToBetting" is invoked during this phase, the phase of
 *         <code>this</code> will be changed from {@link BJPhases#INITIALIZATION} to
 *         {@link BJPhases#BETTING}.
 *     During the {@link BJPhases#BETTING} phase, a {@link BJClientGameState} can
 *     receive the following {@link BJGameEvent}s.
 *       - A {@link BJGameEvent} whose name is "Bet" and whose value is of type
 *         {@link Integer}.  The value should be the value of the bet that the currently
 *         acting player is betting.  When "Bet" is invoked during this phase, the bet
 *         of the currently acting player is updated to reflect the specified value, and
 *         the next player to bet is moved to the front of the acting queue.
 *       - A {@link BJGameEvent} whose name is "Pass" and whose value is of any type.
 *         When "Pass" is invoked during this phase, the currently acting player is removed
 *         from the current game for the duration of the current hand.  They will be added
 *         back in to the beginning of the next next game.
 *       - A {@link BJGameEvent} whose name is "AdvanceToDealing" and whose value is of any
 *         type.  When "AdvanceToDealing" is invoked during this phase, the phase of
 *         <code>this</code> will be changed from {@link BJPhases#BETTING} to
 *         {@link BJPhases#DEALING}.
 *     During the {@link BJPhases#DEALING} phase, a {@link BJClientGameState} can
 *     receive the following {@link BJGameEvent}s.
 *       - A {@link BJGameEvent} whose name is "SendCard" and whose value is of type
 *         {@link Integer}.  The value should represent the card that was sent to the player.
 *         When "SendCard" is invoked during this phase, the currently acting player's hand
 *         is updated to contain the specified card and the next player to act is moved
 *         to the front of the action queue.
 *       - A {@link BJGameEvent} whose name is "AdvanceToPlaying" and whose value is of any type.
 *         When "AdvanceToPlaying" is invoked during this phase, the phase of <code>this</code>
 *         will be changed from {@link BJPhases#DEALING} to {@link BJPhases#PLAYING}.
 *     During the {@link BJPhases#PLAYING} phase, a {@link BJClientGameState} can
 *     receive the following {@link BJGameEvent}s.
 *       - A {@link BJGameEvent} whose name is "SendCard" and whose value is of type
 *         {@link Integer}.  The value should represent the card that was sent to the player.
 *         When "SendCard" is invoked during this phase, the currently acting player's hand
 *         is updated to contain the specified card.
 *       - A {@link BJGameEvent} whose name is "Stay" and whose value is of any type.
 *         When "Stay" is invoked during this phase, the next player to act is moved
 *         to the front of the action queue.
 *       - A {@link BJGameEvent} whose name is "DoubleDown" and whose value is of any type.
 *         When "DoubleDown" is invoked during this phase, the currently acting player's bet is
 *       - A {@link BJGameEvent} whose name is "Split" and whose value is of any type.
 *         When "Split" is invoked during this phase, the currently acting player's hand is split
 *         and both hands are located at the front of the action queue.
 *       - A {@link BJGameEvent} whose name is "AdvanceToConclusion" and whose value is of any type.
 *         When "AdvanceToConclusion" is invoked during this phase, the phase of <code>this</code>
 *         will be changed from {@link BJPhases#PLAYING} to {@link BJPhases#CONCLUSION}.
 *     During the {@link BJPhases#CONCLUSION} phase, a {@link BJClientGameState} can
 *     receive the following {@link BJGameEvent}s.
 *       - A {@link BJGameEvent} whose name is "AdvanceToInitialization" and whose value is of any type.
 *         When "AdvanceToInitialization" is invoked during this phase, the phase of <code>this</code>
 *         will be changed from {@link BJPhases#CONCLUSION} to {@link BJPhases#INITIALIZATION}.
 *
 *
 * }}}</pre>
 *
 *
 * @author A. Jensen
 * @version 1.0
 * @see com.piindustries.picasino.api.GameState
 */
public class BJClientGameState implements GameState {

    // The following are collections of the names of which BJGameEvents this can handle in each of its phases.
    // Can be directly access by inheriting implementations because it is final and immutable.
    protected static final String[] INITIALIZATION_EVENTS = new String[]{"AddPlayer", "RemovePlayer", "AdvanceToBetting"};
    protected static final String[] BETTING_EVENTS = new String[]{"Bet", "Pass", "AdvanceToDealing"};
    protected static final String[] DEALING_EVENTS = new String[]{"SendCard","AdvanceToPlaying"};
    protected static final String[] PLAYING_EVENTS = new String[]{"RequestCard", "SendCard", "Stay", "DoubleDown", "Split","AdvanceToConclusion"};
    protected static final String[] CONCLUSION_EVENTS = new String[]{"AdvanceToInitialization"};
    private BJPhases phase;
    private LinkedList<Hand> hands;
    private LinkedList<Hand> passedList;
    private NetworkHandler networkHandler;
    private LinkedList<String> eventLog;
    private int logSize;
    private int logCounter;
    private boolean isVerbose; // False by default

    /**
     * Default Constructor.
     */
    public BJClientGameState() {
        this.setPhase(BJPhases.INITIALIZATION);
        LinkedList<Hand> h = new LinkedList<Hand>();
        h.add( new DealerHand() );
        this.setHands( h );
        this.passedList = new LinkedList<Hand>();
        this.setVerbose(false);
    }

    /**
     * Invokes a GameEvent on this GameState.
     *
     * @param e the GameEvent to invoke on `this`.
     * @throws InvalidGameEventException if `this` cannot handle `event` in its
     *                                   current state.
     */
    public void invoke(GameEvent e) throws InvalidGameEventException {

        // If the GameEvent is not a Blackjack game event, throw exception
        if (!(e instanceof BJGameEvent))
            throw new InvalidGameEventException(e.toString());
        // Case event to type correct type.
        BJGameEvent event = (BJGameEvent) e;
        switch (this.getPhase()) {

            // Handle INITIALIZATION GameEvents.
            case INITIALIZATION:
                // ADD PLAYER
                if( event.getName().equals("AddPlayer") ){

                    // If the player is already in the game, don't add them
                    // Otherwise, add them to the game
                    String username = (String)event.getValue();
                    boolean contained = false;
                    for( Hand h : this.getHands() ){
                        if( h.getUsername().equals(username)){
                            contained = true;
                            break;
                        }
                    }

                    // Append to the log appropriately
                    if( !contained ){
                        this.getHands().add( this.getHands().size() - 1, new Hand(username, new LinkedList<Integer>()));
                        this.appendLog(username + " was added to the game.");
                    } else {
                        this.appendLog(username + " could not be added to the game.");
                    }
                // REMOVE PLAYER
                } else if ( event.getName().equals("RemovePlayer") ) {

                    // If the player is in the game, remove them.
                    boolean removed = false;
                    String toRemove = (String)event.getValue();
                    for( Hand h : this.getHands() ){
                        if( h.getUsername().equals( toRemove ) ){
                            this.getHands().remove( h );
                            removed = true;
                            break;  // Break for loop
                        }
                    }

                    // Append to the log appropriately
                    if( removed ){
                        this.appendLog( toRemove +" has been removed from the game." );
                    } else {
                        this.appendLog( toRemove +" could not be removed from the game." );
                    }
                // ADVANCE TO BETTING
                } else if ( event.getName().equals("AdvanceToBetting") ) {
                    //Append Log
                    this.appendLog("Advancing phase from Initialization to Betting");
                    this.setPhase(BJPhases.BETTING);
                // ANYTHING ELSE
                } else {
                    throw new InvalidGameEventException(event.getName());
                }
                break;  // Break INITIALIZATION case
            case BETTING:
                // BET
                if( event.getName().equals("Bet") ){
                    // Set the bet of the current hand
                    Integer value = (Integer)event.getValue();
                    this.getHands().getFirst().setBet(value);

                    // Shift hand to back and bring next hand to front.
                    Hand toBack = this.getHands().removeFirst();
                    this.getHands().addLast(toBack);
                    this.appendLog(toBack.getUsername()+" bets "+value+".");
                // PASS
                } else if ( event.getName().equals("Pass") ) {
                    // Add the passing player to the passed list and remove them from the queue.
                    this.appendLog( this.getHands().getFirst().getUsername() + " passes." );
                    this.passedList.add(this.getHands().removeFirst());
                // ADVANCE TO DEALING
                } else if( event.getName().equals("AdvanceToDealing") ) {
                    //Append Log
                    this.appendLog("Advancing phase from Betting to Dealing");
                    this.setPhase(BJPhases.DEALING);    // Advance Phase
                 // ANYTHING ELSE
                } else {
                    throw new InvalidGameEventException(event.getName());
                }
                break;  // break BETTING case
            // Handle DEALING GameEvents
            case DEALING:
                if ( event.getName().equals("SendCard") ) {
                    // Add the passed card to the current acting player
                    int card = (Integer)event.getValue();
                    this.getHands().getFirst().getCards().addLast(card);

                    // Shift hand to back and bring next hand to front.
                    Hand toBack = this.getHands().removeFirst();
                    this.getHands().addLast(toBack);
                    this.appendLog( toBack.getUsername() + " has been dealt a "+BJCards.evaluateCardName(card) );
                // ADVANCE TO PLAYING
                } else if( event.getName().equals("AdvanceToPlaying") ) {
                    //Append Log
                    this.appendLog("Advancing phase from Dealing to Playing");
                    //Advance Phase
                    this.setPhase(BJPhases.PLAYING);    // Advance Phase
                // ANYTHING ELSE
                } else {
                    throw new InvalidGameEventException(event.getName());
                }
                break;  // BREAK DEALING CASE
            case PLAYING:
                // SEND CARD
                if( event.getName().equals("SendCard") ){
                    // Add the card to the currently acting player
                    Integer card = (Integer)event.getValue();
                    this.getHands().getFirst().getCards().addLast(card);
                    this.appendLog(this.getHands().getFirst().getUsername() + " was dealt a "+BJCards.evaluateCardName(card));
                // STAY
                } else if ( event.getName().equals("Stay") ) {
                    // Add the currently acting player to the end of the queue
                    Hand toBack = this.getHands().removeFirst();
                    this.getHands().addLast(toBack);

                    // Append to the log
                    this.appendLog(toBack.getUsername() + " has elected to stay.");
                // DOUBLE DOWN
                } else if ( event.getName().equals("DoubleDown") ) {
                    // Increase the bet of the currently acting player to twice its magnitude
                    this.getHands().getFirst().setBet(this.getHands().getFirst().getBet() * 2);
                    // Append to the log
                    this.appendLog(this.getHands().getFirst().getUsername() + " doubled down.");
                // SPLIT
                } else if ( event.getName().equals("Split") ) {
                    // Create 2 hands and add them to the front of the queue
                    Hand toDuplicate = this.getHands().removeFirst();
                    LinkedList<Integer> c1 = new LinkedList<Integer>();
                    LinkedList<Integer> c2 = new LinkedList<Integer>();
                    Hand h1 = new Hand( toDuplicate.getUsername(), c1 );
                    Hand h2 = new Hand( toDuplicate.getUsername(), c2 );
                    this.getHands().addFirst( h1 );
                    this.getHands().addFirst( h2 );
                    // Append to log
                    this.appendLog(this.getHands().getFirst().getUsername() + " split their hand.");
                // ADVANCE TO CONCLUSION
                } else if( event.getName().equals("AdvanceToConclusion") ) {
                    // Advance Phase
                    this.setPhase(BJPhases.CONCLUSION);
                    // Append Log
                    this.appendLog("Advancing phase from Playing to Concluding");
                // ANYTHING ELSE
                } else {
                    throw new InvalidGameEventException();
                }
                break;  // BREAK PLAYING CASE
            case CONCLUSION:
                //ADVANCE TO INITIALIZATION
                if ( event.getName().equals("AdvanceToInitialization") ) {
                    // Reset hand data and move to next hand
                    // Add back passed hands and clear passedList
                    for( Hand h : this.passedList ){
                        this.getHands().addLast(h);
                    }
                    this.passedList.clear();
                    // Remove duplicate hands (Split usernames)
                    for( int x = 0; x < this.getHands().size(); x++ ){
                        for( int y = x + 1; y < this.getHands().size(); y++ ){
                            if( this.getHands().get(x).getUsername().equals( this.getHands().get(y).getUsername() ) ){
                                this.getHands().remove(y);
                                y--;
                            }
                        }
                    }

                    // Reset bets and cards
                    for( Hand h : this.getHands() ){
                        h.getCards().clear();
                        h.setBet(0);
                    }
                    // Advance Phase
                    this.setPhase(BJPhases.INITIALIZATION);
                    this.appendLog("Advancing phase from Concluding to Initialization");
                // ADVANCE TO INITIALIZATION
                } else {
                    throw new InvalidGameEventException();
                }
                break;  // BREAK CONCLUSION PHASE
            default:
                throw new Error("Logical Error, Cannot Recover");
        }
    }

    /**
     * @return True if `this` is set to log event verbosely,
     * otherwise false.
     */
    public boolean isVerbose() {
        return isVerbose;
    }

    /**
     * @param verbose a boolean.  If true, this game state will
     *                print all new log events to the standard
     *                output console, otherwise logging will take
     *                place silently.
     */
    public void setVerbose(boolean verbose) {
        isVerbose = verbose;
    }

    /**
     * @return a list of GameEvents that <code>this</code> can accept in
     * its current state.
     */
    public LinkedList<String> getValidEvents(){
        LinkedList<String> result = new LinkedList<String>();
        switch (this.getPhase()){
            case INITIALIZATION:
                for( String s : BJClientGameState.INITIALIZATION_EVENTS )
                    result.add(s);
                break;
            case BETTING:
                for( String s : BJClientGameState.BETTING_EVENTS )
                    result.add(s);
                break;
            case DEALING:
                for( String s : BJClientGameState.DEALING_EVENTS )
                    result.add(s);
                break;
            case PLAYING:
                for( String s : BJClientGameState.PLAYING_EVENTS ){
                    if( s.equals("DoubleDown") ){
                        if( this.getHands().getFirst().getCards().size() == 2 ){
                            result.add("DoubleDown");
                        }
                    } else if (s.equals("Split")){
                        if( this.getHands().getFirst().getCards().size() == 2 ){
                            if( this.getHands().getFirst().getCards().get(0) == this.getHands().getFirst().getCards().get(1) ){
                                result.add("Split");
                            }
                        }
                    } else {
                        result.add(s);
                    }
                }
                break;
            case CONCLUSION:
                for( String s : BJClientGameState.CONCLUSION_EVENTS )
                    result.add(s);
                break;
            default:
                throw new Error("Logical error.  Cannot recover.");
        }
        return result;
    }

    /**
     * Appends a string to the event log, and if `this.isVerbose` == `true`, than
     * `s` will be printed to the standard output console.
     *
     * @param s a {@link String} to append to the end of <code>this</code> log.
     */
    protected void appendLog(String s){
        if(this.isVerbose)
            System.out.println(s);
        this.getEventLog().addFirst(s);
    }

    /**
     * @param logSize the number of events to store in this log. A negative
     *                denotes an unbounded size.
     */
    public void setLogSize(int logSize) {
        this.logSize = logSize;
    }

    /**
     * @return the hand currently in action.
     */
    public Hand getCurrentHand(){
        return this.getHands().getFirst();
    }

    /**
     * @return the username of the currently action hand.
     */
    public String getCurrentUser(){
        return getCurrentHand().getUsername();
    }

    /**
     * @return the value of the log counter.
     */
    public int getLogCount() {
        return this.logCounter;
    }

    /**
     * @return Lazily returns this.eventLog
     */
    public LinkedList<String> getEventLog() {
        if (this.eventLog == null)
            this.setEventLog( new LinkedList<String>() );
        return this.eventLog;
    }

    /**
     * @param s
     */
    private void setEventLog(LinkedList<String> s){
        this.eventLog = s;
    }

    /**
     * @return the NetworkHandler of `this`
     */
    public NetworkHandler getNetworkHandler() {
        if( this.networkHandler == null )
            throw new Error("Network handler not set.  Cannot Recover"); // Further specifies null point exception.  Network handler must be constructed.
        return networkHandler;
    }

    /**
     * Sets the NetworkHandler of `this`.
     *
     * @param networkHandler the Network Handler of `this`.
     */
    public void setNetworkHandler(NetworkHandler networkHandler) {
        this.networkHandler = networkHandler;
    }

    /**
     * @return a log of everything that has occurred in this game.
     */
    public String getLog() {
        int count = this.getLogCount() - 1;
        StringBuilder sb = new StringBuilder();
        for (String s : this.getEventLog()){
            sb.append(count).append(":\t").append(s).append('\n');
            count--;
        }
        return sb.toString();
    }

    /**
     * @return the most recent log.
     */
    public String getMostRecentLog() {
        try {
            return this.getEventLog().getFirst();
        } catch (NoSuchElementException e){
            return "No log history.";
        }
    }

    /**
     * @param value the value to set `this.logCounter` to.
     */
    private void setLogCounter(int value) {
        this.logCounter = value;
    }

    /**
     * Provides read access to `this.phase` to inheriting implementations.
     *
     * @return `this.phase`
     */
    public BJPhases getPhase() {
        if(this.phase == null)
            throw new Error("Phase not set. Cannot Recover"); // Most specific Exception than a null pointer exception
        return phase;
    }

    /**
     * Provides write access to `this.phase` to inheriting implementations.
     *
     * @param phase the value to set `this.phase` to.
     */
    protected void setPhase(BJPhases phase) {
        this.phase = phase;
    }

    /**
     * Provides read access to `this.hands` to inheriting implementations.
     *
     * @return `this.hands`
     */
    public LinkedList<Hand> getHands() {
        if( this.hands == null ){
            this.hands = new LinkedList<Hand>();
        }
        return hands;
    }


    public BJClientGameState clone(){
        try{
            return (BJClientGameState)super.clone();
        } catch (CloneNotSupportedException e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Provides write access to `this.hands` to inheriting implementations.
     *
     * @param hands the value to set `this.hands` to.
     */
    protected void setHands(LinkedList<Hand> hands) {
        this.hands = hands;
    }

    /**
     * A Simple enumeration that represent all the phases of a Black Jack game.
     */
    public enum BJPhases {
        INITIALIZATION,
        BETTING,
        DEALING,
        PLAYING,
        CONCLUSION
    }

    /**
     * A basic data class.  That holds a Username, a list
     * of their cards, and the value of their current bet.
     */
    public class Hand implements Serializable {

        private LinkedList<Integer> cards;
        private String username;
        private Integer bet;

        /**
         * Default constructor.
         *
         * @param username the username associated with this hand.
         * @param cards    the cards that this hand has
         */
        public Hand(String username, LinkedList<Integer> cards) {
            this.setUsername(username);
            this.setCards(cards);
        }

        /**
         * @return the username of `this`.
         */
        public String getUsername() {
            if( username == null )
                this.setUsername("");
            return username;
        }

        /**
         * Sets the username of `this`
         *
         * @param username the String to set `this.username` to.
         */
        public void setUsername(String username) {
            this.username = username;
        }

        /**
         * @return the cards of `this`.
         *
         *   {{{
         *      A card `c` must be contained in the range [0,52]
         *
         *      if( `c` == 52 )
         *         then `c` represents an unknown card and must be handled as such.
         *      else
         *         c/4 denotes the value of the card.
         *          i.e. c/4 = 0 represents an Ace and c = 12 denote a King.
         *         c%4 denotes the suit of the card.
         *           i.e. c%4 = 0 represents a Spade
         *    }}}
         */
        public LinkedList<Integer> getCards() {
            if( this.cards == null )
                this.setCards(new LinkedList<Integer>());
            return cards;
        }

        /**
         * Sets the Cards of `this`.
         *
         * Cards are represented as follows....
         * {{{
         *     A card `c` must be contained in the range [0,52]
         *     if( `c` == 52 )
         *         then `c` represents an unknown card and must be handled as such.
         *     else
         *         c/4 denotes the value of the card.
         *             i.e. c/4 = 0 represents an Ace and c = 12 denote a King.
         *         c%4 denotes the suit of the card.
         *             i.e. c%4 = 0 represents a Spade
         * }}}
         *
         * @param cards the LinkedList<Integer> to set `this.cards` to.
         */
        public void setCards(LinkedList<Integer> cards) {
            this.cards = cards;
        }

        /**
         * Lazily gets the bet of this. If it is null, it sets it to 0.
         *
         * @return the bet value of `this`.  If null, lazily sets to 0 and returns.
         */
        public Integer getBet() {
            if (this.bet == null)
                this.bet = 0;
            return bet;
        }

        /**
         * @return the highest value that this hand can
         * have that is equal to or less than 21.  If the hand
         * can only bust, than -1 is returned indicating a bust.
         */
        public int getBestHandValue(){
            int[] v = this.getHandValues();
            int result = -1;
            for( int i : v )
                result = ( i > 21 ) ? result : Math.max( result, i );
            return result;
        }

        /**
         * Sets the bet value of `this`
         *
         * @param bet the Integer to set `this.bet` to.
         */
        public void setBet(Integer bet) {
            this.bet = bet;
        }

        /**
         * @return an array of the possible values that this hand could have.  Because
         * Aces can be handled as 1's or 11's, the number of aces determines the size of
         * the array returned.  A hand with no aces will return an array of length one. A hand
         * with one ace will return a hand of length 2.  A hand with 2 aces will return an
         * array of length 4.  And so on.  All possibilities are returned, including those
         * that have a value over 21.
         */
        public int[] getHandValues(){
            int[] result = new int[1];
            result[0] = 0;
            for( int c : this.getCards() ){
                if( c % 13 == 0 ){ // Ace
                    int[] tmp = new int[result.length*2];
                    for( int i = 0; i < result.length; i++ ){
                        tmp[i] = result[i] + 1;
                        tmp[i + result.length] = result[i] + 11;
                    }
                    result = tmp;
                } else if( c % 13 <= 12){ // 2 through K
                    for( int i = 0; i < result.length; i++){
                        result[i] = result[i] + Math.min(c + 1,10);
                    }
                }
            }
            return result;
        }
    }

    /**
     * A dealer hand
     */
    public class DealerHand extends Hand {

        public DealerHand(){
            super("PiCasino Dealer",new LinkedList<Integer>());
        }

        /**
         * @return true if this hand must hit.  Otherwise false;
         */
        public boolean mustHit(){
            return this.getBestHandValue() < 17 && this.getBestHandValue() >= 0;
        }
    }
}
