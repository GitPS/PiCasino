package com.piindustries.picasino.blackjack.test;
import com.piindustries.picasino.blackjack.database.BlackjackDatabaseConnector;
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

        //Clean database to test
        dbt.cleanDatabaseForTesting();

        //Test Create Player
        System.out.println("\nTesting Add User");
        boolean playerAdded = dbt.createNewPlayer("test_user","testtest","Test","User","test@test.user");
        if(!playerAdded){
            System.out.println("Add failed");
        }else{
            System.out.println("Add succeeded");
        }

        //Test Update Password
        System.out.println("\nTesting changing password");
        boolean passwordChanged = dbt.changeUserPassword("test_user","testtest","Eagles2013!");
        if(!passwordChanged){
            System.out.println("Change password failed");
        }else{
            System.out.println("Change password succeeded");
        }

        //Test Updating chip count (2500 tests, values between 0 and 1 billion)
        System.out.println("\nTesting updating Current Chip Count");
        for(int i = 0; i < 15; i++){
            int chipCount = (int)(Math.random() * 1000000000);
            boolean currentChipCountUpdated = dbt.updatePlayerCurrentChipCount("test_user",chipCount);
            if(!currentChipCountUpdated){
                System.out.println("Current chip Count update failed with value: " + chipCount);
            }else{
                System.out.println("Current Chip Count update Succeeded with value: " + chipCount);
            }
        }


        //Test Updating high chip count (2500 tests, values between 0 and 1 billion)
        System.out.println("\nTesting updating High Chip Count");
        for(int i = 0; i < 15; i++){
            int chipCount = (int)(Math.random() * 1000000000);
            boolean highChipCountUpdated = dbt.updatePlayerHighScore("test_user",chipCount);
            if(!highChipCountUpdated){
                System.out.println("High chip Count update failed for value: " + chipCount);
            }else{
                System.out.println("High Chip Count update Succeeded for value: " + chipCount);
            }
        }

        //Test Updating Login Date
        boolean loginDateUpdated = dbt.updateLoginDate("test_user");
        if(!loginDateUpdated){
            System.out.println("Login Date update failed\n");
        }else{
            System.out.println("Login Date update Succeeded\n");
        }

        //Test check player login with correct password
        System.out.println("Testing login for test_user with password Eagles2013! (Correct)");
        boolean loginSuccess = dbt.checkPlayerLogin("test_user","Eagles2013!");
        if(loginSuccess){
            System.out.println("Logging in with correct password: login test succeeded. EXPECTED\n");
        }else{
            System.out.println("Logging in with correct password: login test failed. UNEXPECTED\n");
        }

        //Test check player login with incorrect password. Should receive error message
        System.out.println("Testing login for test_user with password ThisPassw0rdShouldntW0rk (incorrect)");
        boolean loginFailed = dbt.checkPlayerLogin("test_user","ThisPassw0rdShouldntW0rk");
        if(loginFailed){
            System.out.println("Logging in with wrong password: Login succeeded. UNEXPECTED\n");
        }else{
            System.out.println("Logging in with wrong password: Login failed. EXPECTED\n");
        }

        //Test getting all player data
        System.out.println("\nGetting all player data for test_user");
        java.util.HashMap<String,String> userdata = dbt.getAllPlayerData("test_user");
        java.util.ArrayList<String> keys = new java.util.ArrayList<>(userdata.keySet());
        for (String key : keys) {
            System.out.println(key + ": " + userdata.get(key));
        }
    }
}
