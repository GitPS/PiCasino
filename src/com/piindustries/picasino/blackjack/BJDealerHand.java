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

package com.piindustries.picasino.blackjack;

import java.util.LinkedList;

/**
 * A dealer hand
 */
public class BJDealerHand extends BJHand {

    public BJDealerHand(){
        super("PiCasino Dealer",new LinkedList<Integer>());
    }

    /**
     * @return true if this hand must hit.  Otherwise false;
     */
    public boolean mustHit(){
        return this.getBestHandValue() < 17 && this.getBestHandValue() >= 0;
    }
}
