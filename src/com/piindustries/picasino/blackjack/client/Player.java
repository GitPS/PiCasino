/*
 * [Class]
 * [Current Version]
 * [Date last modified]
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

import java.util.LinkedList;

public class Player {
    String username;
    LinkedList<LinkedList<Integer>> hands;
    int index;
    int value;
    int bet;
    boolean hasSplit;
    boolean hasBusted;
    boolean isUpToAct;
    int handValue;

    public static Builder builder(){
        return new Builder();
    }

    private Player(String username,
                   LinkedList<LinkedList<Integer>> hands,
                   int value,
                   int bet,
                   boolean hasSplit,
                   boolean hasBusted,
                   boolean upToAct,
                   int handValue,
                   int index ) {
        this.username = username;
        this.hands = hands;
        this.value = value;
        this.bet = bet;
        this.hasSplit = hasSplit;
        this.hasBusted = hasBusted;
        isUpToAct = upToAct;
        this.handValue = handValue;
        this.index = index;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LinkedList<LinkedList<Integer>> getHands() {
        return hands;
    }

    public void setHands(LinkedList<LinkedList<Integer>> hands) {
        this.hands = hands;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getBet() {
        return bet;
    }

    public void setBet(int bet) {
        this.bet = bet;
    }

    public boolean isHasSplit() {
        return hasSplit;
    }

    public void setHasSplit(boolean hasSplit) {
        this.hasSplit = hasSplit;
    }

    public boolean isHasBusted() {
        return hasBusted;
    }

    public void setHasBusted(boolean hasBusted) {
        this.hasBusted = hasBusted;
    }

    public boolean isUpToAct() {
        return isUpToAct;
    }

    public void setUpToAct(boolean upToAct) {
        isUpToAct = upToAct;
    }

    public int getHandValue() {
        return handValue;
    }

    public void setHandValue(int handValue) {
        this.handValue = handValue;
    }

    public void setIndex( int index ) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }


    public static class Builder {
        String username;
        LinkedList<LinkedList<Integer>> hands;
        int value;
        int bet;
        boolean hasSplit;
        boolean hasBusted;
        boolean isUpToAct;
        int handValue;
        int index;

        public Builder(){}

        public Builder username(String username){
            this.username = username;
            return this;
        }

        public Builder hands(LinkedList<LinkedList<Integer>> hands) {
            this.hands = hands;
            return this;
        }

        public Builder value(int value) {
            this.value = value;
            return this;
        }

        public Builder bet(int bet) {
            this.bet = bet;
            return this;
        }

        public Builder split(boolean hasSplit) {
            this.hasSplit = hasSplit;
            return this;
        }

        public Builder acting(boolean upToAct) {
            isUpToAct = upToAct;
            return this;
        }

        public Builder busted(boolean hasBusted){
            this.hasBusted = hasBusted;
            return this;
        }

        public Builder handValue(int handValue) {
            this.handValue = handValue;
            return this;
        }

        public Builder index(int index){
            this.index = index;
            return this;
        }

        public Player result(){
            return new Player( username, hands, value, bet, hasSplit, hasBusted, isUpToAct, handValue, index );
        }
    }
}
