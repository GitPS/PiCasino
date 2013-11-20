package com.piindustries.picasino.blackjack.domain;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;
import com.esotericsoftware.kryonet.Server;
import com.piindustries.picasino.PiCasino;
import com.piindustries.picasino.api.InvalidGameEventException;
import com.piindustries.picasino.api.NetworkHandler;
import com.piindustries.picasino.blackjack.client.ClientGameState;
import com.piindustries.picasino.blackjack.server.ServerNetworkHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * ___ _   ___          _
 * / _ (_) / __\__ _ ___(_)_ __   ___
 * / /_)/ |/ /  / _` / __| | '_ \ / _ \
 * / ___/| / /__| (_| \__ \ | | | | (_) |
 * \/    |_\____/\__,_|___/_|_| |_|\___/
 * <p/>
 * <p/>
 * Class: com.piindustries.picasino.blackjack.domain.Network
 * Version: 1.0
 * Date: 11/14/13
 * <p/>
 * Copyright 2013 - Michael Hoyt, Aaron Jensen, Andrew Reis, and Phillip Sime.
 * <p/>
 * This file is part of PiCasino.
 * <p/>
 * PiCasino is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p/>
 * PiCasino is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with PiCasino.  If not, see <http://www.gnu.org/licenses/>.
 */

public class Network {
    static public final int port = 1337;

    /* This registers objects that are going to be sent over the network. */
    static public void register(EndPoint endPoint) {
        Kryo kryo = endPoint.getKryo();
        kryo.register(GameEvent.class);
        kryo.register(GameEventType.class);
        kryo.register(ClientGameState.class);
        kryo.register(LinkedList.class);
        kryo.register(Phase.class);
        kryo.register(Hand.class);
        kryo.register(DealerHand.class);
        kryo.register(NetworkHandler.class);
        kryo.register(ServerNetworkHandler.class);
        kryo.register(HashMap.class);
        kryo.register(InvalidGameEventException.class);
        kryo.register(PiCasino.class);
        kryo.register(Cards.class);
        kryo.register(Game.class);
        kryo.register(ArrayList.class);
        kryo.register(Server.class);
    }
}
