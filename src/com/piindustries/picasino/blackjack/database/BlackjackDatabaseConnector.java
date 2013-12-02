package com.piindustries.picasino.blackjack.database;

import com.piindustries.picasino.api.DatabaseConnector;
import java.sql.*;

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

public class BlackjackDatabaseConnector implements DatabaseConnector{
    private Connection conn;                    // Connection object for DriverManager.getConnection
    private StringBuilder sb;                   // StringBuilder used in most methods
    private Statement stmt;                     // Statement used for queries
    private static final boolean DEBUG = true;  //Set to true for debug messages

    public BlackjackDatabaseConnector(){
        try{
            if(DEBUG){
                //Used for testing via WAN
                conn = DriverManager.getConnection("jdbc:mysql://home.andrewreiscomputers.com:3306/PiCasino","picasino","nASXn178WgQm6nx1YvF36gdq");
            }else{
                //Used for production
                conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/PiCasino","picasino","nASXn178WgQm6nx1YvF36gdq");
            }

            if(DEBUG){
                System.out.println("Connected to Database: " + conn.getCatalog() + " located on " + conn.getMetaData().getURL() + " as " + conn.getMetaData().getUserName() );
            }
            //Final connection string when using locally
            //conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/PiCasino","picasino","nASXn178WgQm6nx1YvF36gdq");
        }catch(SQLException sql){
            System.out.println("SQL Exception:" + sql.getMessage());
            System.out.println("SQL State: " + sql.getSQLState());
            System.out.println("Vendor Error: " + sql.getErrorCode());
        }
    }

    public boolean createNewPlayer(String username, String password, String firstName, String lastName, String email) {
        sb = new StringBuilder();
        try{
            //Create Insert Statement
            sb.append("INSERT INTO `login`(username,password) VALUES(");
            sb.append("'");
            sb.append(username);
            sb.append("',sha1('");
            sb.append(password);
            sb.append("'));");

            stmt = conn.createStatement();
            if(stmt.executeUpdate(sb.toString()) != 1){
                System.out.println("INSERT statement returned something other than 0. Please debug further.");
                stmt.close();
                return false;
            }else{
                sb = new StringBuilder();
                //Build query
                sb.append("UPDATE `userdata` SET name='");
                sb.append(firstName);
                sb.append(" ");
                sb.append(lastName);
                sb.append("',email='");
                sb.append(email);
                sb.append("' WHERE username='");
                sb.append(username);
                sb.append("';");

                if(stmt.executeUpdate(sb.toString()) != 1){
                    System.out.println("UPDATE statement returned something other than 0. Please debug further.");
                    stmt.close();
                    return false;
                }else{
                    stmt.close();
                    sb = null;
                    System.gc();
                    return true;
                }
            }
        }catch(SQLException sql){
            if(sql.getErrorCode() == 1062){
                System.out.println("User " + username + " already exists! Please choose another username");
            }else{
                printError(sql,sb.toString());
            }
            return false;
        }
    }

    public boolean changeUserPassword(String username, String oldPassword, String newPassword){
        String oldPwd = "", oldPwdEntered = "";
        String oldPwdStmt = "SELECT sha1('"+oldPassword+"') LIMIT 1;";
        String query = "SELECT password FROM login where username='" + username + "' LIMIT 1;";

        try{
            //Create new Statement
            stmt = conn.createStatement();

            //Execute two lookups to get hashes to compare
            ResultSet oldPwdEnteredRS = stmt.executeQuery(oldPwdStmt);
            while(oldPwdEnteredRS.next()){
                oldPwdEntered = oldPwdEnteredRS.getString(1);
            }
            oldPwdEnteredRS.close();

            ResultSet oldPwdRS = stmt.executeQuery(query);
            while(oldPwdRS.next()){
                oldPwd = oldPwdRS.getString(1);
            }
            oldPwdRS.close();

            //Compare hashes. If same, execute update, otherwise fail
            if(oldPwd.compareTo(oldPwdEntered) == 0){
                query = "UPDATE login SET password=sha1('"+newPassword+"') WHERE username='"+username+"' LIMIT 1;";
                if(stmt.executeUpdate(query) != 1){
                    System.out.println("Something broke during password update");
                    return false;
                }
            }else{
                System.out.println("Old and New passwords do not match. Please try again later");
                return false;
            }
            stmt.close();
            //If everything executes ok, then return true.
            sb = null;
            System.gc();
            return true;
        }catch(SQLException sql){
            printError(sql,query);
            return false;
        }catch(NullPointerException npe){
            npe.printStackTrace();
            return false;
        }
    }

    public boolean updateLoginDate(String username) {
        sb = new StringBuilder();
        sb.append("UPDATE login SET lastLoggedInDate=DATE_FORMAT( CURDATE(), '%m/%d/%Y') WHERE username='");
        sb.append(username);
        sb.append("';");
        try{
            Statement stmt = conn.createStatement();
            if(stmt.executeUpdate(sb.toString()) != 1){
                System.out.println("lastLoggedInDate UPDATE failed. Please debug.");
                return false;
            }else{
                sb = null;
                System.gc();
                return true;
            }
        }catch(SQLException sql){
            printError(sql,sb.toString());
            return false;
        }
    }

    public boolean updatePlayerCurrentChipCount(String username, int chipCount) {
        sb = new StringBuilder();

        //Create Query
        sb.append("UPDATE `userdata` SET currentChipCount=");
        sb.append(chipCount);
        sb.append(" WHERE username='");
        sb.append(username);
        sb.append("';");

        try{
            Statement stmt = conn.createStatement();
            if(stmt.executeUpdate(sb.toString()) != 1){
                System.out.println("Something went wrong during current chip count update for " + username + "! Please contact PiCasino support!");
                return false;
            }else{
                sb = null;
                System.gc();
                return true;
            }
        }catch(SQLException sql){
            printError(sql, sb.toString());
            return false;
        }
    }

    public boolean updatePlayerHighScore(String username, int highChipCount) {
        sb = new StringBuilder();

        //Generate Update Query
        sb = new StringBuilder();
        sb.append("UPDATE `userdata` SET highChipCount=");
        sb.append(highChipCount);
        sb.append(" WHERE username='");
        sb.append(username);
        sb.append("';");

        try{
            stmt = conn.createStatement();
            if(stmt.executeUpdate(sb.toString()) != 1){
                System.out.println("Something went wrong during high chip count update for " + username + "! Please contact PiCasino support!");
            }else{
                sb = null;
                System.gc();
                return true;
            }
        }catch(SQLException sql){
            printError(sql, sb.toString());
            return false;
        }
        return false;
    }

    public boolean checkPlayerLogin(String username, String password) {
        sb = new StringBuilder();
        try{
            stmt = conn.createStatement();
            String oldPwdHash = "SELECT password FROM login WHERE username='" + username + "';";

            ResultSet oldpwdrs = stmt.executeQuery(oldPwdHash);
            while(oldpwdrs.next()){
                oldPwdHash = oldpwdrs.getString(1);
            }
            oldpwdrs.close();

            String loginPwdHash = "SELECT sha1('" + password + "');";
            ResultSet loginPwdRS = stmt.executeQuery(loginPwdHash);
            while(loginPwdRS.next()){
                loginPwdHash = loginPwdRS.getString(1);
            }
            loginPwdRS.close();

            if(DEBUG){
                System.out.println("Stored password hash:\t" + oldPwdHash);
                System.out.println("Entered password hash:\t" + loginPwdHash);
                if(oldPwdHash.compareTo(loginPwdHash) == 0){
                    System.out.println("When compared, they match");
                }else{
                    System.out.println("When compared, they don't match");
                }
            }

            if(oldPwdHash.compareTo(loginPwdHash) == 0){
                return true;
            }else{
                if(DEBUG){
                    System.out.println("DEBUG: Passwords did not match. Please try logging in again.");
                }
                return false;
            }
        }catch(SQLException sql){
            printError(sql, sb.toString());
            return false;
        }
    }

    private void printError(SQLException sql, String query){
        sql.printStackTrace();
        System.out.println("Query: " + query);
        System.out.println("SQL Exception:\n" + sql.getMessage());
        System.out.println("SQL Message:\n" + sql.toString());
    }

    public void cleanDatabaseForTesting(){
        System.out.println("Cleaning up the Database for testing");
        java.util.ArrayList<String> cleanup = new java.util.ArrayList<>();
        cleanup.add("SET foreign_key_checks = 0;");
        cleanup.add("TRUNCATE login;");
        cleanup.add("TRUNCATE userdata;");
        cleanup.add("SET foreign_key_checks = 1;");
        try{
            stmt = conn.createStatement();
            while(cleanup.size() > 0){
                stmt.execute(cleanup.get(0));
                cleanup.remove(0);
            }
            stmt.close();
        }catch(SQLException sql){
            printError(sql, sb.toString());
        }catch(NullPointerException npe){
            npe.printStackTrace();
        }
    }
}
