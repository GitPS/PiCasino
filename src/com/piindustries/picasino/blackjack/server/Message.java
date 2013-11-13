/*
 *
 *       ___ _   ___          _
 *      / _ (_) / __\__ _ ___(_)_ __   ___
 *     / /_)/ |/ /  / _` / __| | '_ \ / _ \
 *    / ___/| / /__| (_| \__ \ | | | | (_) |
 *    \/    |_\____/\__,_|___/_|_| |_|\___/
 *
 *
 * Class: com.piindustries.picasino.blackjack.BJMessage
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

package com.piindustries.picasino.blackjack.server;

// TODO comment
class Message {
    private String from;
    private String message;

    // TODO comment
    public Message(String from, String message){
        setFrom(from);
        setMessage(message);
    }

    // TODO comment
    private String getFrom() {
        return from;
    }

    // TODO comment
    private void setFrom(String from) {
        this.from = from;
    }

    // TODO comment
    private String getMessage() {
        return message;
    }

    // TODO comment
    private void setMessage(String message) {
        this.message = message;
    }
}
