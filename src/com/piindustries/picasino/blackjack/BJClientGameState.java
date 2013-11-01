/*
 *
 *       ___ _   ___          _
 *      / _ (_) / __\__ _ ___(_)_ __   ___
 *     / /_)/ |/ /  / _` / __| | '_ \ / _ \
 *    / ___/| / /__| (_| \__ \ | | | | (_) |
 *    \/    |_\____/\__,_|___/_|_| |_|\___/
 *
 *
 * Class: com.piindustries.picasino.blackjack.BJGameState
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

/**
 * A Client-side GameState.  Handles the logic of the game.
 * <p/>
 * The Server will also implement a BJClientGameState but
 * will handle other networking processes on top of it.
 *
 * @author A. Jensen
 * @version 1.0
 * @see com.piindustries.picasino.api.GameState
 */
public class BJClientGameState implements GameState {

    // The following are collections of the names of which BJGameEvents this can handle in each of its phases.
    // Can be directly access by inheriting implementations because it is final and immutable.
    protected static final String[] INITIALIZATION_EVENTS = new String[]{"AddPlayer", "RemovePlayer", "AdvancePhase"};
    protected static final String[] BETTING_EVENTS = new String[]{"Bet", "Pass", "AdvancePhase"};
    protected static final String[] PLAYING_EVENTS = new String[]{"RequestCard", "SendCard", "Stay", "DoubleDown", "Split"};
    protected static final String[] CONCLUSION_EVENTS = new String[]{"AdvancePhase"};
    private BJPhases phase;
    private LinkedList<Hand> hands;
    private LinkedList<Hand> passedList;
    private NetworkHandler networkHandler;
    private LinkedList<String> eventLog;
    private int logSize;
    private int logCounter;
    private LinkedList<Hand> betList;

    /**
     * Default Constructor.
     */
    public BJClientGameState() {
        this.setHands(new LinkedList<Hand>());
        this.setPhase(BJPhases.INITIALIZATION);
        this.setLogSize(-1);
        this.setLogCounter(0);
    }

    private LinkedList<Hand> getPassedList() {
        if(this.passedList == null)
            this.setPassedList(new LinkedList<Hand>());
        return passedList;
    }

    private void setPassedList(LinkedList<Hand> passedList) {
        this.passedList = passedList;
    }

    /**
     * Invokes a GameEvent on this GameState.
     *
     * @param event the GameEvent to invoke on `this`.
     * @throws InvalidGameEventException if `this` cannot handle `event` in its
     *                                   current state.
     */
    public void invoke(GameEvent event) throws InvalidGameEventException {
        if (!this.isValidEvent(event))
            throw new InvalidGameEventException();
        BJGameEvent BJEvent = (BJGameEvent) event;
        switch (this.getPhase()) {
            case INITIALIZATION:
                if (BJEvent.getName().equals("AddPlayer"))
                    addPlayer((String) BJEvent.getValue());
                else if (BJEvent.getName().equals("RemovePlayer"))
                    removePlayer((String) BJEvent.getValue());
                else if (BJEvent.getName().equals("AdvancePhase"))
                    advancePhase();
                else
                    throw new Error("Failed to implement a supported Action");
                break;
            case BETTING:
                if (BJEvent.getName().equals("Bet"))
                    bet((Integer) BJEvent.getValue());
                else if (BJEvent.getName().equals("Pass"))
                    pass();
                else
                    throw new Error("Failed to implement a supported Action");
                break;
            case PLAYING:
                if (BJEvent.getName().equals("RequestCard"))
                    requestCard();
                else if (BJEvent.getName().equals("SendCard"))
                    sendCard((Integer) BJEvent.getValue());
                else if (BJEvent.getName().equals("Stay"))
                    stay();
                else if (BJEvent.getName().equals("DoubleDown"))
                    doubleDown();
                else if (BJEvent.getName().equals("Split"))
                    split();
                else
                    throw new Error("Failed to implement a supported Action");
                break;
            case CONCLUSION:
                if (BJEvent.getName().equals("AdvancePhase"))
                    advancePhase();
                else
                    throw new Error("Failed to implement a supported Action");
                break;
            default:
                throw new Error("Logical Error, Cannot Recover");
        }
    }

    /**
     * Adds a new player to this GameState and the end of the
     * action list.
     *
     * @param username the username of the new player to add.
     *
     * @return true if the player is added, otherwise false.
     */
    private boolean addPlayer(String username) {
        for(Hand h : this.getHands() ){
            if( h.getUsername().equals(username) ){
                this.appendLog(username + " could not be added to game because a player by that name already exists.");
                return false;
            }
        }
        this.getHands().add(new Hand(username, new LinkedList<Integer>()));
        this.appendLog(username + " has been added to game.");
        return true;
    }

    /**
     * To be called when a player has requested a card
     */
    private void requestCard(){
        this.appendLog(this.getCurrentPlayer() + " has requested a card." );
    }

    /**
     * Removes a player from this GameState
     *
     * @param username the username of the player to remove
     * @return true if player is removed, otherwise false
     */
    private boolean removePlayer(String username) {
        boolean removed = false;
        for (Hand h : this.getHands())
            if (h.getUsername().equals(username)){
                removed = true;
                this.getHands().remove(h);
            }
        if( removed ){
            this.appendLog( username + " has been removed from the game.");
            return true;
        }
        else{
            this.appendLog( "Attempted to removed "+username+" from game but no player with that username exists.");
            return false;
        }
    }

    /**
     * Advances the state of this to the next logical state.
     */
    private void advancePhase() {
        String initialPhase = this.getPhase().name();
        switch (this.getPhase()) {
            case INITIALIZATION:
                if(this.getHands().isEmpty()){
                    this.appendLog("Not enough players to advance phase. Player count: "+this.getHands().size());
                    return;
                }
                this.setPhase(BJPhases.BETTING);
                break;
            case BETTING:
                this.setBetList(new LinkedList<Hand>());
                this.setPhase(BJPhases.PLAYING);
                break;
            case PLAYING:
                this.setBetList(new LinkedList<Hand>());
                this.setPhase(BJPhases.CONCLUSION);
                break;
            case CONCLUSION:
                this.setPhase(BJPhases.INITIALIZATION);
                break;
            default:
                throw new Error("Logical flaw.  Cannot Recover");
        }
        this.appendLog("Phase advanced from " + initialPhase + " to " + this.getPhase().name() + '.');
        if(this.getPhase().equals(BJPhases.BETTING))
            this.appendLog("It is now "+this.getCurrentPlayer()+"'s turn to act.");
    }

    /**
     * @return the max size (in events) of the log file.
     */
    public int getLogSize() {
        return logSize;
    }

    /**
     * @param logSize the number of events to store in this log. A negative
     *                denotes an unbounded size.
     */
    public void setLogSize(int logSize) {
        this.logSize = logSize;
    }

    /**
     * @return the value of the log counter.
     */
    private int getLogCount() {
        return this.logCounter;
    }

    /**
     * Sets the bet of the person who's turn it is to `value`
     *
     * @param value the value of the bet made.
     */
    private void bet(int value) {
        this.getBetList().add(this.getCurrentHand());
        this.getCurrentHand().setBet(value);
        Hand tmp = this.getCurrentHand();
        this.getHands().removeFirst();
        this.getHands().addLast(tmp);
        if( this.getBetList().contains(this.getCurrentHand()) ){
            this.appendLog(tmp.getUsername() + " bet " + value + ".\nBetting this round has concluded.");
            this.advancePhase();
        } else {
            this.appendLog(tmp.getUsername() + " bet " + value + ". It is now " + this.getCurrentPlayer() + "'s turn to act.");
        }
    }

    public LinkedList<Hand> getBetList() {
        if( this.betList == null )
            this.setBetList( new LinkedList<Hand>() );
        return betList;
    }

    public void setBetList(LinkedList<Hand> betList) {
        this.betList = betList;
    }

    /**
     * Advances the focus to the next player to act.
     *
     * Signifies that a player is sitting out of a hand.
     */
    private void pass() {
        this.getPassedList().add( this.getCurrentHand() );
        this.getHands().removeFirst();
        if( this.getHands().isEmpty() ){
            this.appendLog("User choices to sit out. There are not enough players to continue");
            this.setPhase(BJPhases.INITIALIZATION);
        } else
            this.appendLog(this.getCurrentPlayer() + " choices to sit out this hand.");
    }

    /**
     * Advances the focus to the next player to act.
     *
     * Signifies that a player is done playing on a hand.
     */
    private void stay() {
        Hand tmp = this.getCurrentHand();
        this.getBetList().add(tmp);
        this.getHands().removeFirst();
        this.getHands().addLast(tmp);
        if(this.getBetList().contains(this.getCurrentHand())){
           this.appendLog( tmp.getUsername() + " stays. This concludes the playing phase for this round." );
           advancePhase();
        } else {
            this.appendLog(tmp.getUsername() + " stays.  It is now "+this.getCurrentPlayer()+"'s turn to act.");
        }
    }

    /**
     * Called when a client receives a card from the server.
     */
    private void sendCard(int cardId) {
        this.getCurrentHand().getCards().addLast(cardId);
        if (cardId % 13 > 12)
            this.appendLog(this.getCurrentPlayer() + " was dealt an unknown card.");
        else
            this.appendLog(this.getCurrentPlayer() + " was dealt a " + cardId % 13 + " of " + evaluateCardSuit(cardId) + "s.");
    }

    /**
     * @return Evaluates and returns the named suit of a card
     */
    private String evaluateCardSuit(int cardId) {
        switch (cardId / 13) {
            case 0:
                return "Spade";
            case 1:
                return "Heart";
            case 2:
                return "Club";
            case 3:
                return "Diamond";
            default:
                throw new Error("Logical Error. Cannot recover");
        }
    }

    /**
     * Called when a player Doubles Down.
     */
    private void doubleDown() {
        // Separated from .bet() and .pass for logging purposes.
        this.getCurrentHand().setBet(this.getCurrentHand().getBet() * 2);
        Hand tmp = this.getCurrentHand();
        this.getHands().removeFirst();
        this.getHands().addLast(tmp);
        this.appendLog(tmp.getUsername() + " doubles down.");
    }

    /**
     * Called when a player splits
     */
    private void split() {
        Hand toSplit = this.getCurrentHand();
        LinkedList<Integer> cards = new LinkedList<Integer>();
        cards.add(toSplit.getCards().getFirst());
        this.getHands().removeFirst();
        this.getHands().addFirst(new Hand(toSplit.getUsername(), cards));
        this.getHands().addFirst(new Hand(toSplit.getUsername(), cards));
        this.appendLog(this.getCurrentPlayer() + " split their hand.");
    }

    /**
     * Checks if this BJGameState can handle the given
     * GameEvent in its current state.
     *
     * @param e the event to evaluate.
     * @return `true` if e is an instance of BJGameEvent and its name
     *         can be handled in the phase that `this` is in. Otherwise false.
     */
    private boolean isValidEvent(GameEvent e) {
        if (!(e instanceof BJGameEvent))
            return false;
        BJGameEvent event = (BJGameEvent) e;
        String name = event.getName();
        switch (this.getPhase()) {
            case INITIALIZATION:
                for (String s : BJClientGameState.INITIALIZATION_EVENTS)
                    if (s.equals(name))
                        return true;
                break;
            case BETTING:
                for (String s : BJClientGameState.BETTING_EVENTS)
                    if (s.equals(name))
                        return true;
                break;
            case PLAYING:
                for (String s : BJClientGameState.PLAYING_EVENTS)
                    if (s.equals(name))
                        return true;
                break;
            case CONCLUSION:
                for (String s : BJClientGameState.CONCLUSION_EVENTS)
                    if (s.equals(name))
                        return true;
                break;
            default:
                throw new Error("Unreachable code reached");
        }
        return false;
    }

    /**
     * Adds `toAppend` to the log
     *
     * @param toAppend the String to append to this.
     */
    private void appendLog(String toAppend) {
        if (this.getLogSize() < 0 || this.getEventLog().size() < this.getLogSize()) {
            this.getEventLog().addFirst(toAppend);
        } else if (this.getLogSize() != 0) {
            this.getEventLog().removeLast();
            this.getEventLog().addFirst(toAppend);
        }
        logCounter++;
    }

    //TODO comment
    /**
     *
     * @return
     */
    public LinkedList<String> getValidEvents(){
        LinkedList<String> result = new LinkedList<String>();
        switch( this.getPhase() ){
            case INITIALIZATION:
               for(String s: BJClientGameState.INITIALIZATION_EVENTS)
                   result.add(s);
                break;
            case BETTING:
                for(String s : BJClientGameState.BETTING_EVENTS )
                    if( !s.equals("AdvancePhase"))
                        result.add(s);
                break;
            case PLAYING:
                for(String s: BJClientGameState.PLAYING_EVENTS){
                    if( s.equals("Split")  ){
                        if( this.getCurrentHand().getCards().size() == 2 && ( this.getCurrentHand().getCards().get(0) % 13 == ( this.getCurrentHand().getCards().get(1) % 13 ) ) ){
                            result.add(s);
                        }
                    } else if( s.equals("DoubleDown") ){
                        if( this.getCurrentHand().getCards().size() == 2 ){
                            result.add(s);
                        }
                    } else /* if( !s.equals("AdvancePhase")  && !s.equals("Split") && !s.equals("DoubleDown")  ) */{
                        result.add(s);
                    }
                }
                break;
            case CONCLUSION:
                for(String s: BJClientGameState.CONCLUSION_EVENTS)
                    result.add(s);
                break;
            default:
                throw new Error("Error in logic.  Cannot recover");
        }
        return result;
    }

    /**
     * @return the player who is next to act.
     */
    public String getCurrentPlayer(){
       return this.getCurrentHand().getUsername(); 
    }

    /**
     * @return the hand that is next to act.
     */
    protected Hand getCurrentHand(){
        return this.getHands().getFirst();
    }

    /**
     * @return Lazily returns this.eventLog
     */
    private LinkedList<String> getEventLog() {
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
        return this.getEventLog().getFirst();
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
    protected BJPhases getPhase() {
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
    protected LinkedList<Hand> getHands() {
        if( this.hands == null ){
            this.hands = new LinkedList<Hand>();
        }
        return hands;
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
    protected enum BJPhases {
        INITIALIZATION,
        BETTING,
        PLAYING,
        CONCLUSION
    }

    /**
     * A basic data class.  That holds a Username, a list
     * of their cards, and the value of their current bet.
     */
    protected class Hand implements Serializable {

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
        protected String getUsername() {
            if( username == null )
                this.setUsername("");
            return username;
        }

        /**
         * Sets the username of `this`
         *
         * @param username the String to set `this.username` to.
         */
        protected void setUsername(String username) {
            this.username = username;
        }

        /**
         * @return the cards of `this`.
         *         <p/>
         *         {{{
         *         A card `c` must be contained in the range [0,52]
         *         <p/>
         *         if( `c` == 52 )
         *         then `c` represents an unknown card and must be handled as such.
         *         else
         *         c/4 denotes the value of the card.
         *         i.e. c/4 = 0 represents an Ace and c = 12 denote a King.
         *         c%4 denotes the suit of the card.
         *         i.e. c%4 = 0 represents a Spade
         *         <p/>
         *         <p/>
         *         }}}
         */
        protected LinkedList<Integer> getCards() {
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
        protected void setCards(LinkedList<Integer> cards) {
            this.cards = cards;
        }

        /**
         * Lazily gets the bet of this. If it is null, it sets it to 0.
         *
         * @return the bet value of `this`.  If null, lazily sets to 0 and returns.
         */
        protected Integer getBet() {
            if (this.bet == null)
                this.bet = 0;
            return bet;
        }

        /**
         * Sets the bet value of `this`
         *
         * @param bet the Integer to set `this.bet` to.
         */
        protected void setBet(Integer bet) {
            this.bet = bet;
        }
    }
}
