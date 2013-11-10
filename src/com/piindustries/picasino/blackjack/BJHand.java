/*
 *
 *       ___ _   ___          _
 *      / _ (_) / __\__ _ ___(_)_ __   ___
 *     / /_)/ |/ /  / _` / __| | '_ \ / _ \
 *    / ___/| / /__| (_| \__ \ | | | | (_) |
 *    \/    |_\____/\__,_|___/_|_| |_|\___/
 *
 *
 * Class: com.piindustries.picasino.blackjack.BJHand
 * Version: 1.0
 * Date: November 9, 2013
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

import java.io.Serializable;
import java.util.LinkedList;

/**
 * A basic data class.  That holds a Username, a list
 * of their cards, and the value of their current bet.
 */
public class BJHand implements Serializable {

    private LinkedList<Integer> cards;
    private String username;
    private Integer bet;

    /**
     * Default constructor.
     *
     * @param username the username associated with this hand.
     * @param cards    the cards that this hand has
     */
    public BJHand(String username, LinkedList<Integer> cards) {
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
