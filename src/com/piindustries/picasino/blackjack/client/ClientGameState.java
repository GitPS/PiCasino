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

package com.piindustries.picasino.blackjack.client;

import com.piindustries.picasino.PiCasino;
import com.piindustries.picasino.api.InvalidGameEventException;
import com.piindustries.picasino.api.NetworkHandler;
import com.piindustries.picasino.blackjack.database.BlackjackDatabaseConnector;
import com.piindustries.picasino.blackjack.domain.*;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * A Client-Side {@link com.piindustries.picasino.api.GameState}. For blackjack.
 * <p>
 * A {@link ClientGameState} will strictly and exclusively manage all data needed
 * to determine the state of a Blackjack game.  A {@link ClientGameState} will
 * also manage micro-transitions between states within a given phase of the game.
 * A {@link ClientGameState} will operate in tandem with a {@link com.piindustries.picasino.blackjack.server.ServerGameState}
 * to determine when to transition between phases of a blackjack game.
 * </p>
 *
 *
 * }}}</pre>
 *
 *
 * @author A. Jensen
 * @version 1.0
 * @see com.piindustries.picasino.api.GameState
 */
public class ClientGameState implements com.piindustries.picasino.api.GameState {

    // The following are collections of the names of which BJGameEvents this can handle in each of its phases.
    // Can be directly access by inheriting implementations because it is final and immutable.
    private Phase phase;
    private LinkedList<Hand> hands;
    private LinkedList<Hand> passedList;
    private NetworkHandler networkHandler;
    private com.piindustries.picasino.api.GuiHandler guiHandler;
    private String thisUser;
    private boolean isServer = false;
    private BlackjackDatabaseConnector blackjackDatabaseConnector;

    /**
     * Default Constructor.
     */
    public ClientGameState(PiCasino pi, String username) {
        this(pi,username,false);
    }

    /**
     * Default Constructor.
     */
    public ClientGameState(PiCasino pi, String username, boolean isServer) {
        this.setPhase(Phase.INITIALIZATION);     // Set phase
        LinkedList<Hand> h = new LinkedList<>();   // Build Player List
        h.add(new DealerHand());  // Add a dealer hand
        this.setHands(h);
        this.setNetworkHandler(pi.getNetworkHandler());
        this.setThisUser(username);
        this.passedList = new LinkedList<>();   // Create an empty passed list
        this.isServer = isServer;
        blackjackDatabaseConnector = new BlackjackDatabaseConnector();
        if( ! isServer ){
            Player player = new Player.Builder().username(username).hands(new LinkedList<LinkedList<Integer>>()).value(1000).split(false).busted(false).handValue(0).index(0).result();
            this.guiHandler = new GUI(player,(ClientNetworkHandler)networkHandler);
        }
    }

    // TODO Make sure splitting works as designed
    // TODO Make sure doubling down works as designed
    // TODO Make sure empty games caused by disconnects don't explode

    /**
     * Invokes a GameEvent on this ClientGameState.
     *
     * @param e the GameEvent to invoke on `this`.
     * @throws InvalidGameEventException if `this` cannot handle `event` in its
     *                                   current state.
     */
    public synchronized void invoke(com.piindustries.picasino.api.GameEvent e) throws InvalidGameEventException {
        if (!(e instanceof GameEvent))        // If the GameEvent is not a Blackjack game event, throw exception
            throw new InvalidGameEventException(e.toString());
        GameEvent event = (GameEvent) e;    // Case event to type correct type.
        if( handleGlobalEvent(event) )
            return; // If a global event is handled, there is no need to continue.
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
        if( !isServer ) {
            guiHandler.updateGui( getAvailableActions(), getGuiData());
        }
    }

    public void setIsServer(boolean b){
        this.isServer = b;
    }

    /**
     * If `event` is a global event that `this` can handle
     * `#handleGlobalEvent` will handle it and return `true`.
     * If `event` is not a global event or `this` cannot handle
     * it, `this` will return `false`.
     *
     * @param event Any {@link com.piindustries.picasino.blackjack.domain.GameEvent}
     * @return 'true` if event was handled by `this#handleGlobalEvent`
     * otherwise `false`
     */
    private boolean handleGlobalEvent(GameEvent event){
        switch(event.getType()){
            case SET_NETWORK_HANDLER:
                if( event.getValue() instanceof ClientNetworkHandler ){
                    setNetworkHandler( (ClientNetworkHandler) event.getValue() );
                    PiCasino.LOGGER.info("Client GameState's network handler has been set.");
                } else
                    PiCasino.LOGGER.severe("Client GameState's network handler could not be set. Reason: Value does not conform to type ClientNetworkHandler.");
                return true;
            case SET_PHASE:
                if( event.getValue() instanceof Phase ){
                    this.setPhase((Phase)event.getValue());
                    PiCasino.LOGGER.info("Client GameState Phase has been set.");
                } else
                    PiCasino.LOGGER.severe("Client GameState Phase could not be set. Reason: Value does not conform to type Phase.");
                return true;
            case SET_PASSED_LIST:
                if( event.getValue() instanceof LinkedList ){
                    this.passedList = (LinkedList<Hand>)event.getValue();
                    PiCasino.LOGGER.info("Client GameState Passed List has been set.");
                } else
                    PiCasino.LOGGER.severe("Client GameState Passed List could not be set. Reason: Value does not conform to type LinkedList<Hand>.");
                return true;
            case SET_HANDS:
                if( event.getValue() instanceof LinkedList ){
                    this.setHands((LinkedList<Hand>) event.getValue());
                    PiCasino.LOGGER.info("Client GameState Hands List has been set.");
                } else
                    PiCasino.LOGGER.severe("Client GameState Hands List could not be set. Reason: Value does not conform to type LinkedList<Hand>.");
                return true;
            default: return false;
        }
    }

    public LinkedList<Hand> getPassedList(){ return this.passedList; }

    public String getThisUser() {
        return thisUser;
    }

    public void setThisUser(String thisUser) {
        this.thisUser = thisUser;
    }

    /**
     * Resets all hands, adds players from passedList back into the
     * game.  Removes duplicate hands cause by splits.  Resets Bets.
     * And advances the phase of `this` to INITIALIZATION.
     */
    private void advanceToInitialization(){
        for( Hand h : this.passedList ){
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
        for( Hand h : this.getHands() ){
            h.getCards().clear();
            h.setBet(0);
        }
        // Move dealer to the end of the action list
        this.getHands().addLast(this.getHands().removeFirst());
        // Advance phase
        this.setPhase(Phase.INITIALIZATION);
        PiCasino.LOGGER.info("Advancing phase from Concluding to Initialization");
    }

    /**
     * Advances the phase of `this` to CONCLUSION`
     * and logs it.
     */
    private void advanceToConclusion(){
        this.setPhase(Phase.CONCLUSION);
        PiCasino.LOGGER.info("Advancing phase from Playing to Concluding");
    }

    /**
     * Create 2 hands for the currently acting player and add them to the
     * front of the queue.
     */
    private void split(){
        // FIXME Behavior unverified
        Hand toDuplicate = this.getHands().removeFirst();
        LinkedList<Integer> c1 = new LinkedList<>();
        LinkedList<Integer> c2 = new LinkedList<>();
        Hand h1 = new Hand( toDuplicate.getUsername(), c1 );
        Hand h2 = new Hand( toDuplicate.getUsername(), c2 );
        h1.setSplit(true);
        h2.setSplit(true);
        h1.getCards().addFirst(toDuplicate.getCards().getFirst());
        h2.getCards().addFirst(toDuplicate.getCards().getFirst());
        this.getHands().addFirst( h1 );
        this.getHands().addFirst( h2 );
        // Append to log
        PiCasino.LOGGER.info(this.getHands().getFirst().getUsername() + " split their hand.");
    }

    /**
     * Sends a card to the currently acting player, logs it, and moves the
     * currently acting player to the back of the action queue.
     *
     * @param event a GameEvent named "SendCard" whose value is id of the card.
     */
    private void dealCard(GameEvent event){
        Integer card = (Integer)event.getValue();
        this.getCurrentHand().getCards().addLast(card);
        // Log Event
        PiCasino.LOGGER.info(this.getCurrentUser() + " has been dealt a " + Cards.evaluateCardName((Integer) event.getValue()));
        // Move to back
        this.firstHandToBack();
    }

    /**
     * Advances the phase of `this` to PLAYING`
     * and logs it.
     */
    private void advanceToPlaying(){
        PiCasino.LOGGER.info("Advancing phase from Dealing to Playing");
        this.setPhase(Phase.PLAYING);
    }

    /**
     *  Increase the bet of the currently acting player to twice its magnitude
     *  Logs the event
     */
    private void doubleDown(){
        // FIXME Behavior unverified
        this.getCurrentHand().setBet(this.getCurrentHand().getBet() * 2);
        PiCasino.LOGGER.info(this.getHands().getFirst().getUsername() + " doubled down.");

    }

    /**
     * Advances action to the next player to act.
     */
    private void stand(){
        // Append to the log
        PiCasino.LOGGER.info(this.getCurrentUser() + " has elected to stand.");
        // Move player to back
        this.firstHandToBack();
    }

    /**
     * Sends a card to a player
     *
     * @param event a GameEvent named "SendCard" whose value is id of the card.
     */
    private void sendCard(GameEvent event){
        Integer card = (Integer)event.getValue();
        this.getCurrentHand().getCards().addLast(card);
        // Log event
        PiCasino.LOGGER.info(this.getHands().getFirst().getUsername() + " was dealt a " + Cards.evaluateCardName(card));
    }

    // TODO Test adding 2 players with the same username.
    /**
     * Adds a player to the current game if a player by the specified username does
     * not already exist.
     *
     * @param event a GameEvent named "AddPlayer" whose value is the username of the
     *              player to add.
     */
    private void addPlayer( GameEvent event ){
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
            Hand dh = this.getHands().removeLast();
            this.getHands().addLast( new Hand(username, new LinkedList<Integer>()));
            this.getHands().addLast(dh);
            PiCasino.LOGGER.info(username + " was added to the game.");
        } else
            PiCasino.LOGGER.info(username + " was could not be added to the game.");
    }

    /**
     * Sets the bet of the currently acting player, and moves that
     * player to the back of the action queue.
     *
     * @param event a GameEvent named "Bet" whose value is the integer value
     *              of the bet.
     */
    private void bet(GameEvent event){
        // Set the bet of the current hand
        Integer value = (Integer)event.getValue();
        this.getHands().getFirst().setBet(value);

        // Append Log
        PiCasino.LOGGER.info(this.getCurrentUser()+" has bet "+value+".");
        this.firstHandToBack();
    }

    /**
     * Removes a player from the current game ifa a player ith the specified username
     * currently exists in the game.
     *
     * @param event a GameEvent named "RemovePlayer" whose value is the username of the
     *              player to remove.
     */
    private void removePlayer(GameEvent event){
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
        if( removed ) PiCasino.LOGGER.info(toRemove +" has been removed from the game.");
        else PiCasino.LOGGER.info(toRemove +" could not be removed from the game.");
    }

    /**
     * Adds the current player to the passedList and logs the event
     */
    private void pass(){
        // FIXME Behavior unverified
        PiCasino.LOGGER.info(this.getCurrentUser() + " has elected to pass this round.");
        this.passedList.add(this.getHands().removeFirst());
    }

    /**
     * Advances `this.phase` to BETTING
     */
    private void advanceToBetting(){
        //Append Log
        PiCasino.LOGGER.info("Advancing phase from INITIALIZATION to BETTING");
        this.setPhase(Phase.BETTING);
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
        PiCasino.LOGGER.info("Advancing phase from BETTING to DEALING");
        this.setPhase(Phase.DEALING);
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
     * @return the ClientNetworkHandler of `this`
     */
    public NetworkHandler getNetworkHandler() {
        if( this.networkHandler == null )
            throw new Error("Network handler not set.  Cannot Recover"); // Further specifies null point exception.  Network handler must be constructed.
        return networkHandler;
    }

    /**
     * Sets the ClientNetworkHandler of `this`.
     *
     * @param networkHandler the Network Handler of `this`.
     */
    public void setNetworkHandler(NetworkHandler networkHandler) {
        this.networkHandler = networkHandler;
    }

    /**
     * Provides read access to `this.phase` to inheriting implementations.
     *
     * @return `this.phase`
     */
    public Phase getPhase() {
        if(this.phase == null)
            throw new Error("Phase not set. Cannot Recover"); // Most specific Exception than a null pointer exception
        return phase;
    }

    /**
     * Provides write access to `this.phase` to inheriting implementations.
     *
     * @param phase the value to set `this.phase` to.
     */
    protected void setPhase(Phase phase) {
        this.phase = phase;
    }

    /**
     * Provides read access to `this.hands` to inheriting implementations.
     *
     * @return `this.hands`
     */
    public LinkedList<Hand> getHands() {
        if( this.hands == null ){
            this.hands = new LinkedList<>();
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
     * In beta
     *
     * Returns a list of actions a client can invoke.
     *
     * @return a list of actions a client can invoke.
     */
    public ArrayList<GameEventType> getAvailableActions(){
        ArrayList<GameEventType> result = new ArrayList<>();
        switch(this.getPhase()){
            case INITIALIZATION:
                // There are no actions to take in the initialization phase.
                break;
            case BETTING:
                if(this.getThisUser().equals( this.getCurrentUser() )){
                    result.add(GameEventType.BET);
                    result.add(GameEventType.PASS);
                }
                break;
            case DEALING:
                // There are no actions to take in the dealing phase.
                break;
            case PLAYING:
                if(this.getThisUser().equals( this.getCurrentUser() )){
                    result.add(GameEventType.HIT);
                    result.add(GameEventType.STAND);
                    if( this.getCurrentHand().getCards().size() == 2 ){
                        result.add(GameEventType.DOUBLE_DOWN);
                        // Check if both cards have the same face
                        if( Cards.getBJCard(this.getCurrentHand().getCards().get(0)) == Cards.getBJCard(this.getCurrentHand().getCards().get(1)) )
                            result.add(GameEventType.SPLIT);
                    }
                }
                break;
            case CONCLUSION:
                break;
            default: throw new Error("Logical Error.  Cannot Recover");
        }
        return result;
    }

    // FIXME this DEFINITELY needs to be tested...
    // TODO comment
    // In Beta
    private GuiData getGuiData(){
        LinkedList<Hand> tmpHands = (LinkedList<Hand>) getHands().clone();
        Player[] tmpData = new Player[9];
        // Queue first player to front.
        while( !(tmpHands.getLast() instanceof DealerHand) )
            tmpHands.addLast( tmpHands.removeFirst() );
        int index = 0;
        for( Hand focus: tmpHands ){
            Player.Builder builder = Player.builder();

            if( focus.getUsername().startsWith("$") ){
                // Populate know info
                builder.username(focus.getUsername())
                        .bet(focus.getBet())
                        .handValue(focus.getBestHandValue())
                        .split( focus.isSplit() )
                        .index(0)
                        .value(999999)
                        .busted(focus.getBestHandValue() > 21);    // TODO verify
            } else {
                // Populate known info
                System.out.println( focus.getUsername() );
                builder.username(focus.getUsername())
                        .bet(focus.getBet())
                        .handValue(focus.getBestHandValue())
                        .split( focus.isSplit() )
                        .index(index + 1)
                        .value(Integer.parseInt(blackjackDatabaseConnector.getAllPlayerData(focus.getUsername()).get("currentChipCount")))
                        .busted(focus.getBestHandValue() > 21);    // TODO verify
            }

            // Generate a hand for each hand assigned to this player
            LinkedList<LinkedList<Integer>> toAddHands = new LinkedList<>();
            for( Hand hands: tmpHands ){
                if( hands.getUsername().equals(focus.getUsername()) ){
                    toAddHands.add(hands.getCards());
                }
            }
            PiCasino.LOGGER.info(focus.getUsername());
            for( int i : focus.getCards() ){
                PiCasino.LOGGER.info( i + " : " + Cards.evaluateCardName(i) );
            }
            builder.hands(toAddHands);
            if( focus instanceof DealerHand ){
                // Assign the dealers hand to the dealers position
                builder.index(0);
                tmpData[8] = builder.result();
                return buildGuiDataFromArray(tmpData);
            } else {
                // Assign player data to position
                tmpData[index] = builder.result();
                // Progress
            }
            index += 1;
        }
        throw new Error("Unreachable code.  Method should have ended upon evaluation of dealer's hand.");
    }

    // TODO comment
    private GuiData buildGuiDataFromArray(Player[] data){
        GuiData result = new GuiData();
        for(int i = 1; i < 9; i++ ){
            result.setPlayer( i, data[0] );
        }
        result.setPlayer(0, data[8]);
        return result;
    }

    public String getStatus(){
        StringBuilder sb = new StringBuilder();
        sb.append( "\tPhase: " + this.getPhase().name() +'\n' );
        sb.append( "\tPlayers List...\n" );
        for( Hand h : this.getHands() ){
            sb.append( "\t\t"+h.getUsername() + " Bet: "+h.getBet()+" Cards: [ " );
            for( int i: h.getCards() )
                sb.append( Cards.evaluateCardName(i)+" " );
            sb.append("] " );
            if( h.isSplit() )sb.append( "Split\n" );
            else sb.append("Not Split\n");
        }
        sb.append( "\tPassed List..." );
        if( this.getPassedList().isEmpty() ) sb.append( " the passed list is empty\n" );
        else {
            for( Hand h : this.getPassedList() ){
                sb.append( "\t\t"+h.getUsername() + " Bet: "+h.getBet()+" Cards: [ " );
                for( int i: h.getCards() )
                    sb.append( Cards.evaluateCardName(i)+" " );
                sb.append("]" + '\n' );
            }
        }
        return sb.toString();
    }
}
