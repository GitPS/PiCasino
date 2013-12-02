package com.piindustries.picasino.blackjack.test;
import com.piindustries.picasino.blackjack.database.BlackjackDatabaseConnector;

/**
 * Created with IntelliJ IDEA.
 * User: Andrew Reis
 * Date: 11/25/13
 * Time: 8:21 PM
 * To change this template use File | Settings | File Templates.
 */
/*
 *
 *       ___ _   ___          _
 *      / _ (_) / __\__ _ ___(_)_ __   ___
 *     / /_)/ |/ /  / _` / __| | '_ \ / _ \
 *    / ___/| / /__| (_| \__ \ | | | | (_) |
 *    \/    |_\____/\__,_|___/_|_| |_|\___/
 *
 *
 * Class: com.piindustries.picasino.api.GameEvent
 * Version: 1.0
 * Date: October 29, 2013
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
public class DatabaseConnectorTest{

    public static void main(String[] args){
        BlackjackDatabaseConnector dbt = new BlackjackDatabaseConnector();
        //Test Create Player
        boolean playerAdded = dbt.createNewPlayer("areis422","testtest","Andrew","Reis","reis.andr@uwlax.edu");
        if(!playerAdded){
            System.out.println("Add failed");
        }else{
            System.out.println("Add succeeded");
        }

        //Test Update Password
        boolean passwordChanged = dbt.changeUserPassword("areis422","testtest","Eagles2013!");
        if(!passwordChanged){
            System.out.println("Change password failed");
        }else{
            System.out.println("Change password succeeded");
        }

        //Test Updating chip count
        boolean currentChipCountUpdated = dbt.updatePlayerCurrentChipCount("areis422",999999);
        if(!currentChipCountUpdated){
            System.out.println("Current chip Count update failed");
        }else{
            System.out.println("Current Chip Count update Succeeded");
        }

        //Test Updating high chip count
        boolean highChipCountUpdated = dbt.updatePlayerHighScore("areis422",999999);
        if(!highChipCountUpdated){
            System.out.println("Current chip Count update failed");
        }else{
            System.out.println("Current Chip Count update Succeeded");
        }
    }
}
