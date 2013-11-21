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

package com.piindustries.picasino.blackjack.domain;

import java.io.Serializable;
import java.util.LinkedList;

/** A basic data class.  That holds a Username, a list of their cards, and the value of their current bet. */
public class Hand implements Serializable {

    private Integer bet;
    private LinkedList<Integer> cards;
    private String username;
    private boolean isSplit;    // false by default

    /**
     * Default constructor.
     * @param username the username associated with this hand.
     * @param cards the cards that this hand has
     */
    public Hand(String username, LinkedList<Integer> cards) {
        this.setUsername(username);
        this.setCards(cards);
        this.setSplit(false);
    }

    public int getBestHandValue() {
        int[] toEval = new int[getCards().size()];
        for( int i = 0; i < getCards().size(); i++ )
            toEval[i] = getCards().get(i);
        return Cards.getMaxHandValue(toEval);
    }

    public boolean hasBusted(){
        return getBestHandValue() > 21;
    }

    /**
     * Lazily gets the bet of this. If it is null, it sets it to 0.
     * @return the bet value of `this`.  If null, lazily sets to 0 and returns.
     */
    public Integer getBet() {
        if (this.bet == null)
            this.bet = 0;
        return bet;
    }

    /**
     * Sets the bet value of `this`
     * @param bet the Integer to set `this.bet` to.
     */
    public void setBet(Integer bet) {
        this.bet = bet;
    }

    /**
     * @return the cards of `this`.
     *
     *     {{{
     *         A card `c` must be contained in the range [0,52]
     *
     *         if( `c` == 52 ) then `c` represents an unknown card and must be handled as such. else c/4 denotes the
     *         value of the card. i.e. c/4 = 0 represents an Ace and c = 12 denote a King. c%4 denotes the suit of the
     *         card. i.e. c%4 = 0 represents a Spade
     *     }}}
     */
    public LinkedList<Integer> getCards() {
        if (this.cards == null)
            this.setCards(new LinkedList<Integer>());
        return cards;
    }

    /**
     * Sets the Cards of `this`.
     *
     * Cards are represented as follows.... {{{ A card `c` must be contained in the range [0,52] if( `c` == 52 ) then
     * `c` represents an unknown card and must be handled as such. else c/4 denotes the value of the card. i.e. c/4 = 0
     * represents an Ace and c = 12 denote a King. c%4 denotes the suit of the card. i.e. c%4 = 0 represents a Spade
     * }}}
     * @param cards the LinkedList<Integer> to set `this.cards` to.
     */
    public void setCards(LinkedList<Integer> cards) {
        this.cards = cards;
    }

    /** @return the username of `this`. */
    public String getUsername() {
        if (username == null)
            this.setUsername("");
        return username;
    }

    /**
     * Sets the username of `this`
     * @param username the String to set `this.username` to.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    // TODO comment
    public boolean isSplit() {
        return isSplit;
    }

    // TODO comment
    public void setSplit(boolean split) {
        isSplit = split;
    }
}
