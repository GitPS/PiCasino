/*
 *
 *       ___ _   ___          _
 *      / _ (_) / __\__ _ ___(_)_ __   ___
 *     / /_)/ |/ /  / _` / __| | '_ \ / _ \
 *    / ___/| / /__| (_| \__ \ | | | | (_) |
 *    \/    |_\____/\__,_|___/_|_| |_|\___/
 *
 *
 * Class: com.piindustries.picasino.blackjack.BJGameEvent
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

/**
 * A basic GameEvent.  Unlike a DirectedGameEvent
 * a GameEvent does not contain toUser information.
 *
 * @see com.piindustries.picasino.api.GameEvent
 * @author A. Jensen
 * @version 1.0
 */
public class GameEvent implements com.piindustries.picasino.api.GameEvent {
    private GameEventType type;
    private Object value;

    public GameEvent(){}

    public GameEvent(GameEventType type, Object value){
        this.type = type;
        this.value = value;
    }

    public GameEvent(GameEventType type){
        this.type = type;
    }

    public GameEventType getType() {
        return type;
    }

    public void setType(GameEventType type) {
        this.type = type;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
