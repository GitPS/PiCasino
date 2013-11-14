/*
 *
 *       ___ _   ___          _
 *      / _ (_) / __\__ _ ___(_)_ __   ___
 *     / /_)/ |/ /  / _` / __| | '_ \ / _ \
 *    / ___/| / /__| (_| \__ \ | | | | (_) |
 *    \/    |_\____/\__,_|___/_|_| |_|\___/
 *
 *
 * Class: com.piindustries.picasino.blackjack.BJCards
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

package com.piindustries.picasino.blackjack.domain;

import java.util.LinkedList;

public class Cards {
    public static String evaluateCardName(int cardId){
        if( cardId == 52 )
            return "Unknown Card";
        String result = "";
        int value = cardId % 13;
        if( value == 0 )
            result = "Ace of ";
        else if( value == 10 )
            result = "Jack of ";
        else if( value == 11 )
            result = "Queen of ";
        else if( value == 12 )
            result = "King of ";
        else if( value > 0 && value < 10)
            result = Integer.valueOf(value + 1).toString()+" of ";
        return result + Cards.evaluateCardSuit(cardId) + "s";
    }

    public static String evaluateCardSuit(int cardId){
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
     * @param cardId the id of the card to eval.
     *
     * @return the blackjack value of the card.
     * If it is an Ace, it will return 2 values.
     */
    public static int[] getBJValue(int cardId){
        int[] result;
        if( cardId / 13 == 0 )
            result =  new int[] { 1, 11 };
        else
            result =  new int[] { Math.min( cardId/13 + 1, 10 ) };
        return result;
    }

    /**
     * Gets the id of a card.
     *
     * @param cardId a card id.
     *
     * @return the face value of a card.
     */
    public static int getBJCard(int cardId){
        return cardId / 13;
    }

    public static LinkedList<Integer> getHandValues(int[] hand){
        LinkedList<Integer> result = new LinkedList<>();
        if( hand == null || hand.length < 1 )
            return null;
        result.add(hand[0]);    // TODO make sure there can't be an index out of bounds exception
        for(int i : hand ){
            int[] value = getBJValue(i);
            if( value.length > 1 ){
                for( int r : result ){
                    result.addLast(r);
                }
                for(int v = 0; v < result.size()/2; v++ ){
                    result.set(v, result.get(v) + value[0]);
                    result.set(v+result.size()/2, result.get(v+result.size()/2) + value[1]);
                }
            } else {
                for( int r = 0; r < result.size(); r++ ){
                    result.set(r, result.get(r) + value[0] );
                }
            }
        }
        return result;
    }

    public static int getMaxHandValue(int[] hand){
        int result = Integer.MIN_VALUE;
        for( int v : getHandValues( hand ) ){
            result = Math.max(result,v);
        }
        return result;
    }

    public static int getMinHandValue(int[] hand){
        int result = Integer.MAX_VALUE;
        for(int v: getHandValues(hand))
            result = Math.min(result,v);
        return result;
    }
}