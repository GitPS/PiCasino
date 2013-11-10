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

import java.util.ArrayList;
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
 * A {@link BJClientGameState} can be and must be in exactly one {@link com.piindustries.picasino.blackjack.BJPhases} at a
 * given time.  What a {@link BJClientGameState} can handle during any of the specified
 * phases will be described in detail below.
 * </p>
 *
 * <pre>{{{
 *     During the {@link com.piindustries.picasino.blackjack.BJPhases#INITIALIZATION} phase, a {@link BJClientGameState} can
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
 *         <code>this</code> will be changed from {@link com.piindustries.picasino.blackjack.BJPhases#INITIALIZATION} to
 *         {@link com.piindustries.picasino.blackjack.BJPhases#BETTING}.
 *     During the {@link com.piindustries.picasino.blackjack.BJPhases#BETTING} phase, a {@link BJClientGameState} can
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
 *         <code>this</code> will be changed from {@link com.piindustries.picasino.blackjack.BJPhases#BETTING} to
 *         {@link com.piindustries.picasino.blackjack.BJPhases#DEALING}.
 *     During the {@link com.piindustries.picasino.blackjack.BJPhases#DEALING} phase, a {@link BJClientGameState} can
 *     receive the following {@link BJGameEvent}s.
 *       - A {@link BJGameEvent} whose name is "SendCard" and whose value is of type
 *         {@link Integer}.  The value should represent the card that was sent to the player.
 *         When "SendCard" is invoked during this phase, the currently acting player's hand
 *         is updated to contain the specified card and the next player to act is moved
 *         to the front of the action queue.
 *       - A {@link BJGameEvent} whose name is "AdvanceToPlaying" and whose value is of any type.
 *         When "AdvanceToPlaying" is invoked during this phase, the phase of <code>this</code>
 *         will be changed from {@link com.piindustries.picasino.blackjack.BJPhases#DEALING} to {@link com.piindustries.picasino.blackjack.BJPhases#PLAYING}.
 *     During the {@link com.piindustries.picasino.blackjack.BJPhases#PLAYING} phase, a {@link BJClientGameState} can
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
 *         will be changed from {@link com.piindustries.picasino.blackjack.BJPhases#PLAYING} to {@link com.piindustries.picasino.blackjack.BJPhases#CONCLUSION}.
 *     During the {@link com.piindustries.picasino.blackjack.BJPhases#CONCLUSION} phase, a {@link BJClientGameState} can
 *     receive the following {@link BJGameEvent}s.
 *       - A {@link BJGameEvent} whose name is "AdvanceToInitialization" and whose value is of any type.
 *         When "AdvanceToInitialization" is invoked during this phase, the phase of <code>this</code>
 *         will be changed from {@link com.piindustries.picasino.blackjack.BJPhases#CONCLUSION} to {@link com.piindustries.picasino.blackjack.BJPhases#INITIALIZATION}.
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
    private BJPhases phase;
    private LinkedList<BJHand> hands;
    private LinkedList<BJHand> passedList;
    private LinkedList<String> eventLog;
    private NetworkHandler networkHandler;
    private LinkedList<BJMessage> messages;
    private int logSize;
    private int logCounter;
    private boolean isVerbose; // False by default

    /**
     * Default Constructor.
     */
    public BJClientGameState() {
        this.setPhase(BJPhases.INITIALIZATION);     // Set phase
        LinkedList<BJHand> h = new LinkedList<>();   // Build Player List
        h.add( new BJDealerHand() );                  // Add a dealer
        this.setHands( h );
        this.passedList = new LinkedList<>();   // Create an empty passed list
        this.setVerbose(false);
    }

    // TODO Make sure splitting works as designed
    // TODO Make sure doubling down works as designed
    // TODO Make sure empty games don't explode
    // TODO Make sure empty games caused by disconnects don't explode
    // TODO Make sure empty games caused by passes don't explode

    /**
     * Invokes a GameEvent on this GameState.
     *
     * @param e the GameEvent to invoke on `this`.
     * @throws InvalidGameEventException if `this` cannot handle `event` in its
     *                                   current state.
     */
    public void invoke(GameEvent e) throws InvalidGameEventException {
        if (!(e instanceof BJGameEvent))        // If the GameEvent is not a Blackjack game event, throw exception
            throw new InvalidGameEventException(e.toString());
        BJGameEvent event = (BJGameEvent) e;    // Case event to type correct type.
        if(handleGlobalEvent(event)) return;    // If is a global event and it can be handled, handle it, and return.
        switch (this.getPhase()) {
            case INITIALIZATION:
                switch( event.getType() ){
                    case ADD_PLAYER: addPlayer(event); break;
                    case ADVANCE_TO_BETTING: advanceToBetting(); break;
                    case REMOVE_PLAYER: removePlayer(event); break;
                    default: throw new InvalidGameEventException(event.getType().name());
                } break;
            case BETTING:
                switch(event.getType()){
                    case BET: bet(event); break;
                    case PASS: pass(); break;
                    case ADVANCE_TO_DEALING: advanceToDealing(); break;
                    default: throw new InvalidGameEventException(event.getType().name());
                } break;
            case DEALING:
                switch(event.getType()){
                    case DEAL_CARD: dealCard(event); break;
                    case ADVANCE_TO_PLAYING: advanceToPlaying(); break;
                    default: throw new InvalidGameEventException(event.getType().name());
                } break;
            case PLAYING:
                switch(event.getType()){
                    case SEND_CARD: sendCard(event); break;
                    case STAND: stand(); break;
                    case DOUBLE_DOWN: doubleDown(); break;
                    case SPLIT: split(); break;
                    case ADVANCE_TO_CONCLUDING: advanceToConclusion(); break;
                    default: throw new InvalidGameEventException(event.getType().name());
                } break;
            case CONCLUSION:
                switch(event.getType()){
                    case ADVANCE_TO_INITIALIZATION: advanceToInitialization(); break;
                    default: throw new InvalidGameEventException(event.getType().name());
                } break;
            default: throw new Error("Logical Error, Cannot Recover");
        }
    }

    /**
     * If `event` is a global event that `this` can handle
     * `#handleGlobalEvent` will handle it and return `true`.
     * If `event` is not a global event or `this` cannot handle
     * it, `this` will return `false`.
     *
     * @param event Any {@link BJGameEvent}
     * @return 'true` if event was handled by `this#handleGlobalEvent`
     * otherwise `false`
     */
    private boolean handleGlobalEvent(BJGameEvent event){
        switch(event.getType()){    // TODO add logic and establish global events
            case MESSAGE:
                String data = (String)event.getValue(); // TODO out of bounds handling
                String from = data.substring(0, data.indexOf('|') );
                String message = data.substring( data.indexOf('|') + 1 );
                appendMessage(new BJMessage(from, message));
                return true;
            default: return false;
        }
    }

    // TODO comment
    private void appendMessage(BJMessage m){
        this.getMessages().addLast(m);
    }

    /**
     * @param event Any {@link BJGameEvent}
     * @return `True` if this is a global event, otherwise
     * `false`.
     */
    private boolean isGlobalEvent(BJGameEvent event){
        return false;   // TODO add logic
    }

    /**
     * Resets all hands, adds players from passedList back into the
     * game.  Removes duplicate hands cause by splits.  Resets Bets.
     * And advances the phase of `this` to INITIALIZATION.
     */
    private void advanceToInitialization(){
        for( BJHand h : this.passedList ){
            this.getHands().addLast(h);
        }
        this.passedList.clear();
        // Remove duplicate hands
        for( int x = 0; x < this.getHands().size(); x++ ){
            for( int y = x + 1; y < this.getHands().size(); y++ ){
                if( this.getHands().get(x).getUsername().equals( this.getHands().get(y).getUsername() ) ){
                    this.getHands().remove(y);
                    y--;
                }
            }
        }
        for( BJHand h : this.getHands() ){
            h.getCards().clear();
            h.setBet(0);
        }
        this.setPhase(BJPhases.INITIALIZATION);
        this.appendLog("Advancing phase from Concluding to Initialization");
    }

    /**
     * Advances the phase of `this` to CONCLUSION`
     * and logs it.
     */
    private void advanceToConclusion(){
        this.setPhase(BJPhases.CONCLUSION);
        this.appendLog("Advancing phase from Playing to Concluding");
    }

    /**
     * Create 2 hands for the currently acting player and add them to the
     * front of the queue.
     */
    private void split(){
        // FIXME Behavior unverified
        BJHand toDuplicate = this.getHands().removeFirst();
        LinkedList<Integer> c1 = new LinkedList<>();
        LinkedList<Integer> c2 = new LinkedList<>();
        BJHand h1 = new BJHand( toDuplicate.getUsername(), c1 );
        BJHand h2 = new BJHand( toDuplicate.getUsername(), c2 );
        this.getHands().addFirst( h1 );
        this.getHands().addFirst( h2 );
        // Append to log
        this.appendLog(this.getHands().getFirst().getUsername() + " split their hand.");
    }

    /**
     * Sends a card to the currently acting player, logs it, and moves the
     * currently acting player to the back of the action queue.
     *
     * @param event a BJGameEvent named "SendCard" whose value is id of the card.
     */
    private void dealCard(BJGameEvent event){
        Integer card = (Integer)event.getValue();
        this.getCurrentHand().getCards().addLast(card);
        // Log Event
        this.appendLog( this.getCurrentUser() + " has been dealt a "+BJCards.evaluateCardName((Integer)event.getValue()) );
        // Move to back
        this.firstHandToBack();
    }

    /**
     * Advances the phase of `this` to PLAYING`
     * and logs it.
     */
    private void advanceToPlaying(){
        this.appendLog("Advancing phase from Dealing to Playing");
        this.setPhase(BJPhases.PLAYING);
    }

    /**
     *  Increase the bet of the currently acting player to twice its magnitude
     *  Logs the event
     */
    private void doubleDown(){
        // FIXME Behavior unverified
        // FIXME Behavior unverified
        this.getCurrentHand().setBet(this.getCurrentHand().getBet() * 2);
        this.appendLog(this.getHands().getFirst().getUsername() + " doubled down.");
    }

    /**
     * Advances action to the next player to act.
     */
    private void stand(){
        // Append to the log
        this.appendLog(this.getCurrentUser() + " has elected to stand.");
        // Move player to back
        this.firstHandToBack();
    }

    /**
     * Sends a card to a player
     *
     * @param event a BJGameEvent named "SendCard" whose value is id of the card.
     */
    private void sendCard(BJGameEvent event){
        Integer card = (Integer)event.getValue();
        this.getCurrentHand().getCards().addLast(card);
        // Log event
        this.appendLog(this.getHands().getFirst().getUsername() + " was dealt a " + BJCards.evaluateCardName(card));
    }

    /**
     * Adds a player to the current game if a player by the specified username does
     * not already exist.
     *
     * @param event a BJGameEvent named "AddPlayer" whose value is the username of the
     *              player to add.
     */
    private void addPlayer( BJGameEvent event ){
        String username = (String)event.getValue();
        boolean contained = false;
        for( BJHand h : this.getHands() ){
            if( h.getUsername().equals(username)){
                contained = true;
                break;
            }
        }
        // Append to the log appropriately
        if( !contained ){
            this.getHands().add( this.getHands().size() - 1, new BJHand(username, new LinkedList<Integer>()));
            this.appendLog(username + " was added to the game.");
        } else
            this.appendLog(username + " could not be added to the game.");
    }

    /**
     * Sets the bet of the currently acting player, and moves that
     * player to the back of the action queue.
     *
     * @param event a BJGameEvent named "Bet" whose value is the integer value
     *              of the bet.
     */
    private void bet(BJGameEvent event){
        // Set the bet of the current hand
        Integer value = (Integer)event.getValue();
        this.getHands().getFirst().setBet(value);

        // Append Log
        this.appendLog(this.getCurrentUser()+" has bet "+value+".");
        this.firstHandToBack();
    }

    /**
     * Removes a player from the current game ifa a player ith the specified username
     * currently exists in the game.
     *
     * @param event a BJGameEvent named "RemovePlayer" whose value is the username of the
     *              player to remove.
     */
    private void removePlayer(BJGameEvent event){
        // If the player is in the game, remove them.
        boolean removed = false;
        String toRemove = (String)event.getValue();
        for( BJHand h : this.getHands() ){
            if( h.getUsername().equals( toRemove ) ){
                this.getHands().remove( h );
                removed = true;
                break;  // Break for loop
            }
        }

        // Append to the log appropriately
        if( removed )
            this.appendLog( toRemove +" has been removed from the game." );
        else
            this.appendLog( toRemove +" could not be removed from the game." );
    }

    /**
     * Adds the current player to the passedList and logs the event
     */
    private void pass(){
        // FIXME Behavior unverified
        this.appendLog( this.getCurrentUser() + " has elected to pass this round." );
        this.passedList.add(this.getHands().removeFirst());
    }

    /**
     * Advances `this.phase` to BETTING
     */
    private void advanceToBetting(){
        //Append Log
        this.appendLog("Advancing phase from INITIALIZATION to BETTING");
        this.setPhase(BJPhases.BETTING);
    }

    /**
     * Moves the first hand in the action queue to the
     * back.
     */
    private void firstHandToBack(){
        this.getHands().addLast(this.getHands().removeFirst());
    }

    /**
     * Advances the phase of `this.phase` to DEALING
     */
    private void advanceToDealing(){
        this.appendLog("Advancing phase from BETTING to DEALING");
        this.setPhase(BJPhases.DEALING);
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
     * Appends a string to the event log, and if `this.isVerbose` == `true`, than
     * `s` will be printed to the standard output console.
     *
     * @param s a {@link String} to append to the end of <code>this</code> log.
     */
    protected synchronized void appendLog(String s){
        if(this.isVerbose)
            System.out.println(s);
        this.getEventLog().addFirst(s);
        System.out.flush();
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
    public BJHand getCurrentHand(){
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
     * @param s value to set event log to
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

    // TODO comment
    private LinkedList<BJMessage> getMessages() {
        if( this.messages == null )
            this.setMessages(new LinkedList<BJMessage>());
        return messages;
    }

    // TODO comment
    private void setMessages(LinkedList<BJMessage> messages) {
        this.messages = messages;
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
    public LinkedList<BJHand> getHands() {
        if( this.hands == null ){
            this.hands = new LinkedList<>();
        }
        return hands;
    }

    /**
     * @see java.lang.Object#clone()
     *
     * @return `this` cloned.
     */
    @Override
    public BJClientGameState clone() throws CloneNotSupportedException {
        return (BJClientGameState)super.clone();
    }

    /**
     * Provides write access to `this.hands` to inheriting implementations.
     *
     * @param hands the value to set `this.hands` to.
     */
    protected void setHands(LinkedList<BJHand> hands) {
        this.hands = hands;
    }

    public ArrayList<BJGameEventType> getValidEvents(){
        ArrayList<BJGameEventType> result = new ArrayList<>();
        switch(this.getPhase()){
            case INITIALIZATION:
                result.add(BJGameEventType.ADD_PLAYER);
                result.add(BJGameEventType.REMOVE_PLAYER);
                result.add(BJGameEventType.ADVANCE_TO_BETTING);
                break;
            case BETTING:
                result.add(BJGameEventType.BET);
                result.add(BJGameEventType.PASS);
                result.add(BJGameEventType.ADVANCE_TO_DEALING);
                break;
            case DEALING:
                result.add(BJGameEventType.DEAL_CARD);
                result.add(BJGameEventType.ADVANCE_TO_PLAYING);
                break;
            case PLAYING:
                result.add(BJGameEventType.HIT);
                result.add(BJGameEventType.SEND_CARD);
                result.add(BJGameEventType.STAND);
                result.add(BJGameEventType.SPLIT);
                result.add(BJGameEventType.DOUBLE_DOWN);
                result.add(BJGameEventType.ADVANCE_TO_CONCLUDING);
                break;
            case CONCLUSION:
                result.add(BJGameEventType.ADVANCE_TO_INITIALIZATION);
                break;
            default: throw new Error("Logical Error.  Cannot Recover");
        }
        result.add(BJGameEventType.MESSAGE);
        result.add(BJGameEventType.PING);
        result.add(BJGameEventType.INTEGRITY_CHECK);
        result.add(BJGameEventType.PLAYER_DISCONNECT);
        result.add(BJGameEventType.REQUEST_GAME_STATE);
        return result;
    }

}
