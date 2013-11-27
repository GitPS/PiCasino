package com.piindustries.picasino.blackjack.test;

import com.piindustries.picasino.api.DatabaseConnector;
import java.security.MessageDigest;
import java.sql.*;
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
public class DatabaseConnectorTest implements DatabaseConnector {
    Connection conn;

    public static void main(String[] args){
        DatabaseConnectorTest dbt = new DatabaseConnectorTest();
        boolean playerAdded = dbt.createNewPlayer("areis422","testtest","Andrew","Reis","reis.andr@uwlax.edu");
        if(!playerAdded){
            System.out.println("Add failed");
        }else{
            System.out.println("Add succeeded");
        }
    }

    public DatabaseConnectorTest(){
        try{
            System.out.println("Running test connection via WAN to home.andrewreiscomputers.com:PiCasino");
            conn = DriverManager.getConnection("jdbc:mysql://home.andrewreiscomputers.com/PiCasino","picasino","nASXn178WgQm6nx1YvF36gdq");
            conn.setCatalog("PiCasino");
            if(conn.isValid(30)){
                System.out.println("Connection Successful! Connected to: " + conn.getCatalog());
            }else{
                throw new SQLException("Connection Failed");
            }
        }catch(SQLException e){
            System.out.println("Error: " + e.getErrorCode() + "\nSQLState: " + e.getSQLState() + "\nError Message: " + e.toString());
        }
    }

    private String sha1(String password){
        String temp = null;

        return temp;
    }

    public boolean createNewPlayer(String username, String password, String firstName, String lastName, String email) {

        String query = "INSERT INTO `login` NATURAL JOIN `userdata` VALUES('" + username +
                "','" + sha1(password) + "'),'" + firstName + " " + lastName + "','" + email + "',0,0);";
        try{
        Statement addPlayer = conn.createStatement();
        int result = addPlayer.executeUpdate(query);
            if(result > 0){
                System.out.println("Successfully added " + result + "row: " + username);
            }
           return true;
        }catch(SQLException e){
            System.out.println("SQL Error: " + e.getErrorCode());
            System.out.println("Error Message: " + e.toString());
            System.out.println("Query Executed: " + query);
            return false;
        }
    }

    @Override
    public boolean updateLoginDate(String username) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean updatePlayerCurrentChipCount(String username, int chipCount) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean updatePlayerHighScore(String username, int highChipCount) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean checkPlayerLogin(String username, String password) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean changeUserPassword(String username, String oldPassword, String newPassword) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
