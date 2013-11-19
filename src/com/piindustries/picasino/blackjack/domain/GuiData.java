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

package com.piindustries.picasino.blackjack.domain;

public class GuiData {
    private Player player1;
    private Player player2;
    private Player player3;
    private Player player4;
    private Player player5;
    private Player player6;
    private Player player7;
    private Player player8;
    private Player dealer;

    public Player getPlayer(int index){
        switch(index){
            case 1: return player1; break;
            case 2: return player2; break;
            case 3: return player3; break;
            case 4: return player4; break;
            case 5: return player5; break;
            case 6: return player6; break;
            case 7: return player7; break;
            case 8: return player8; break;
            case 0: return dealer; break;
            default: throw new IllegalArgumentException("Player not defined at index "+index+ '.');
        }
    }

    public Player setPlayer(int index, Player toSet){
        switch(index){
            case 1: player1 = toSet; break;
            case 2: player2 = toSet; break;
            case 3: player3 = toSet; break;
            case 4: player4 = toSet; break;
            case 5: player5 = toSet; break;
            case 6: player6 = toSet; break;
            case 7: player7 = toSet; break;
            case 8: player8 = toSet; break;
            case 0: dealer = toSet; break;
            default: throw new IllegalArgumentException("Player not defined at index "+index+ '.');
        }
    }
}
