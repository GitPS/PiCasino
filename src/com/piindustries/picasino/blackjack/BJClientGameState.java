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
 *
 * The Server will also implement a BJClientGameState but
 * will handle other networking processes on top of it.
 *
 * @see com.piindustries.picasino.api.GameState
 * @author A. Jensen
 * @version 1.0
 */
public class BJClientGameState implements GameState {

    private BJPhases phase;
    private LinkedList<Hand> hands;
    private NetworkHandler networkHandler;

    // The following are collections of the names of which BJGameEvents this can handle in each of its phases.
    // Can be directly access by inheriting implementations because it is final and immutable.
    protected static final String[] INITIALIZATION_EVENTS = new String[] { "AddPlayer", "RemovePlayer", "AdvancePhase" };
    protected static final String[] BETTING_EVENTS = new String[] { "Bet", "Pass", "AdvancePhase" };
    protected static final String[] PLAYING_EVENTS = new String[] { "RequestCard", "SendCard", "Pass", "DoubleDown", "Split", "AdvancePhase" };
    protected static final String[] CONCLUSION_EVENTS = new String[] { "AdvancePhase" };

    /**
     * A Simple enumeration that represent all the phases of a Black Jack game.
     */
    protected enum BJPhases {
        INITIALIZATION,
        BETTING,
        PLAYING,
        CONCLUSION
    }


    public BJClientGameState(){
        this.hands = new LinkedList<Hand>();
        this.phase = BJPhases.INITIALIZATION;
    }

    /**
     * Invokes a GameEvent on this GameState.
     *
     *
     *
     * @param event
     *
     * @throws InvalidGameEventException if `this` cannot handle `event` in its
     * current state.
     */
    public void invoke(GameEvent event) throws InvalidGameEventException {

        // Check if this phase can handle `BJEvent`
        if( !this.isValidEvent(event))
            throw new InvalidGameEventException();

        BJGameEvent BJEvent = (BJGameEvent)event;

        // Determine which phase this is in.
        switch( this.phase ){
            case INITIALIZATION:

                // Handle Initialization Requests or throw error
                if(BJEvent.getName().equals("AddPlayer")) {
                    this.hands.add(new Hand((String)BJEvent.getValue(),new LinkedList<Integer>()));
                } else if (BJEvent.getName().equals("RemovePlayer")) {
                    for(Hand h : this.hands)
                        if( h.getUsername().equals( (String)BJEvent.getValue() ))
                            this.hands.remove(h);
                } else if (BJEvent.getName().equals("AdvancePhase")) {
                    this.phase = BJPhases.BETTING;
                }  else {
                    throw new Error("Failed to implement a supported Action");
                }
                break;

            case BETTING:
                if(BJEvent.getName().equals("Bet"))
                    this.hands.getFirst().setBet( (Integer)BJEvent.getValue() );
                else if(BJEvent.getName().equals("Pass")){
                    Hand tmp = this.hands.getFirst();
                    this.hands.removeFirst();
                    this.hands.addLast(tmp);
                } else if (BJEvent.getName().equals("AdvancePhase")) {
                    this.phase = BJPhases.PLAYING;
                } else {
                    throw new Error("Failed to implement a supported Action");
                }
                break;
            case PLAYING:
                if(BJEvent.getName().equals("RequestCard")){
                    // If this isn't caught here, it will lead to an Error later.
                    break;
                } else if(BJEvent.getName().equals("SendCard")){
                    this.hands.getFirst().cards.addLast((Integer)BJEvent.getValue());
                } else if(BJEvent.getName().equals("Pass")){
                    Hand tmp = this.hands.getFirst();
                    this.hands.removeFirst();
                    this.hands.addLast(tmp);
                } else if(BJEvent.getName().equals("DoubleDown")){
                    this.hands.getFirst().bet = this.hands.getFirst().bet * 2;
                    Hand tmp = this.hands.getFirst();
                    this.hands.removeFirst();
                    this.hands.addLast(tmp);
                } else if(BJEvent.getName().equals("Split")){
                    Hand toSplit = this.hands.getFirst();
                    LinkedList<Integer> cards = new LinkedList<Integer>();
                    cards.add( toSplit.getCards().getFirst() );
                    this.hands.removeFirst();
                    this.hands.addFirst( new Hand( toSplit.getUsername(), cards ) );
                    this.hands.addFirst( new Hand( toSplit.getUsername(), cards ) );
                } else if (BJEvent.getName().equals("AdvancePhase")) {
                    this.phase = BJPhases.CONCLUSION;
                } else {
                    throw new Error("Failed to implement a supported Action");
                }
                break;
            case CONCLUSION:
                if(BJEvent.getName().equals("AdvancePhase")){
                    this.phase = BJPhases.INITIALIZATION;
                } else {
                    throw new Error("Failed to implement a supported Action");
                }
                break;
            default:
                throw new Error("Unreachable code reached");
        }
    }

    /**
     * Checks if this BJGameState can handle the given
     * GameEvent in its current state.
     *
     * @param e the event to evaluate.
     *
     * @return `true` if e is an instance of BJGameEvent and its name
     * can be handled in the phase that `this` is in. Otherwise false.
     */
    private boolean isValidEvent(GameEvent e){
        if( !(e instanceof BJGameEvent) ){
            return false;
        }
        BJGameEvent event = (BJGameEvent)e;
        String name = event.getName();
        switch(this.phase){
            case INITIALIZATION:
                for(String s : BJClientGameState.INITIALIZATION_EVENTS)
                    if( s.equals(name) )
                        return true;
                break;
            case BETTING:
                for(String s : BJClientGameState.BETTING_EVENTS)
                    if( s.equals(name) )
                        return true;
                break;
            case PLAYING:
                for(String s : BJClientGameState.PLAYING_EVENTS)
                    if( s.equals(name) )
                        return true;
                break;
            case CONCLUSION:
                for(String s : BJClientGameState.CONCLUSION_EVENTS)
                    if( s.equals(name) )
                        return true;
                break;
            default:
                throw new Error("Unreachable code reached");
        }
        return false;
    }

    /**
     * @return the NetworkHandler of `this`
     */
    public NetworkHandler getNetworkHandler() {
        return networkHandler;
    }

    /**
     * Sets the NetworkHandler of `this`.
     * @param networkHandler the Network Handler of `this`.
     */
    public void setNetworkHandler(NetworkHandler networkHandler) {
        this.networkHandler = networkHandler;
    }

    /**
     * Provides read access to `this.phase` to inheriting implementations.
     * @return `this.phase`
     */
    protected BJPhases getPhase() {
        return phase;
    }

    /**
     * Provides write access to `this.phase` to inheriting implementations.
     * @param phase the value to set `this.phase` to.
     */
    protected void setPhase(BJPhases phase) {
        this.phase = phase;
    }

    /**
     * Provides read access to `this.hands` to inheriting implementations.
     * @return `this.hands`
     */
    protected LinkedList<Hand> getHands() {
        return hands;
    }

    /**
     * Provides write access to `this.hands` to inheriting implementations.
     * @param hands the value to set `this.hands` to.
     */
    protected void setHands(LinkedList<Hand> hands) {
        this.hands = hands;
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
         * @param cards the cards that this hand has
         *
         */
        public Hand(String username, LinkedList<Integer> cards) {
            this.username = username;
            this.cards = cards;
        }

        /**
         * @return the username of `this`.
         */
        protected String getUsername() {
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
         *
         * {{{
         *      A card `c` must be contained in the range [0,52]
         *
         *      if( `c` == 52 )
         *          then `c` represents an unknown card and must be handled as such.
         *      else
         *          c/4 denotes the value of the card.
         *              i.e. c/4 = 0 represents an Ace and c = 12 denote a King.
         *          c%4 denotes the suit of the card.
         *              i.e. c%4 = 0 represents a Spade
         *
         *
         * }}}
         *
         */
        protected LinkedList<Integer> getCards() {
            return cards;
        }

        /**
         * Sets the Cards of `this`.
         *
         * Cards are represented as follows....
         * {{{
         *      A card `c` must be contained in the range [0,52]
         *
         *      if( `c` == 52 )
         *          then `c` represents an unknown card and must be handled as such.
         *      else
         *          c/4 denotes the value of the card.
         *              i.e. c/4 = 0 represents an Ace and c = 12 denote a King.
         *          c%4 denotes the suit of the card.
         *              i.e. c%4 = 0 represents a Spade
         *
         *
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
            if(this.bet == null)
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
