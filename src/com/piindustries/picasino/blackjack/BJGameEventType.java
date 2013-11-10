/*
 *
 *       ___ _   ___          _
 *      / _ (_) / __\__ _ ___(_)_ __   ___
 *     / /_)/ |/ /  / _` / __| | '_ \ / _ \
 *    / ___/| / /__| (_| \__ \ | | | | (_) |
 *    \/    |_\____/\__,_|___/_|_| |_|\___/
 *
 *
 * Class: com.piindustries.picasino.blackjack.BJGameEventType
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

/**
 * Enumeration that contains all possible
 * {@link BJGameEvent} types.
 */
public enum BJGameEventType {
    ADD_PLAYER,
    REMOVE_PLAYER,
    ADVANCE_TO_BETTING,
    BET,
    PASS,
    ADVANCE_TO_DEALING,
    DEAL_CARD,
    ADVANCE_TO_PLAYING,
    HIT,
    SEND_CARD,
    STAND,
    SPLIT,
    DOUBLE_DOWN,
    ADVANCE_TO_CONCLUDING,
    ADVANCE_TO_INITIALIZATION,
    MESSAGE,
    PING,
    INTEGRITY_CHECK,
    PLAYER_DISCONNECT,
    REQUEST_GAME_STATE
}
