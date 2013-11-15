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

package com.piindustries.picasino.blackjack.test;

import com.piindustries.picasino.blackjack.client.GameState;

public class ClientTesterServer extends com.piindustries.picasino.blackjack.server.NetworkHandler {
    GameState innards;
    com.piindustries.picasino.blackjack.server.NetworkHandler server;

    public ClientTesterServer(){
        innards = new GameState();
        innards.setNetworkHandler(this);
    }
}
