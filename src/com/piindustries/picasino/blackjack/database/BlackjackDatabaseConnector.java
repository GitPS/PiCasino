package com.piindustries.picasino.blackjack.database;

import com.piindustries.picasino.api.DatabaseConnector;
import com.piindustries.picasino.PiCasino;
import java.sql.*;
import java.util.HashMap;

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

    // CHANGE TO FALSE FOR PRODUCTION
    private static final boolean DEBUG = true;  // Set to true for debug messages

    /**
     * Constructor
     */
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
                PiCasino.LOGGER.info("Connected to Database: " + conn.getCatalog() + " located on " + conn.getMetaData().getURL() + " as " + conn.getMetaData().getUserName() );
            }
        }catch(SQLException sql){
            printError(sql,null);
        }
    }

    /**
     *
     * Creates new player in database
     *
     * @param username  The username of the player to add
     * @param password  The desired password of the player
     * @param firstName The first name of the player
     * @param lastName  The last name of the player
     * @param email     The email of the player
     * @return boolean: true if added, false with error message if failed
     */
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
                    PiCasino.LOGGER.severe("UPDATE statement returned something other than 0. Please debug further.");
                    stmt.close();
                    return false;
                }else{
                    stmt.close();
                    System.gc();
                    return true;
                }
            }
        }catch(SQLException sql){
            if(sql.getErrorCode() == 1062){
                PiCasino.LOGGER.severe("User " + username + " already exists! Please choose another username");
            }else{
                printError(sql,sb.toString());
            }
            return false;
        }
    }

    /**
     *
     * Returns all player data for player username
     *
     * @param username  Username to retrieve data for
     * @return  HashMap\<String, String> with all player data
     */
    public HashMap<String, String> getAllPlayerData(String username) {
        sb = new StringBuilder();
        HashMap<String,String> userdata = new HashMap<>();
        sb.append("SELECT * FROM login NATURAL JOIN userdata WHERE username='");
        sb.append(username);
        sb.append("';");
        try{
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sb.toString());
            ResultSetMetaData rsmd = rs.getMetaData();
            int numOfCols = rsmd.getColumnCount();
            while(rs.next()){
                for(int i = 1; i <= numOfCols; i++){
                    userdata.put(rsmd.getColumnName(i),rs.getString(i));
                }
            }
            rs.close();
            stmt.close();
        }catch(SQLException sql){
            printError(sql, sb.toString());
            return new HashMap<>();
        }

        return userdata;
    }

    /**
     *
     * Changes the user's password
     *
     * @param username      The username of the player to change the password for
     * @param oldPassword   The players old password
     * @param newPassword   The desired password
     * @return  boolean: true if changed, false with error if not.
     */
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
                    PiCasino.LOGGER.warning("Something broke during password update");
                    return false;
                }
            }else{
                PiCasino.LOGGER.warning("Old and New passwords do not match. Please try again later");
                return false;
            }
            stmt.close();
            //If everything executes ok, then return true.
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

    /**
     *
     * Updates the login date for player 'username'
     *
     * @param username  The username of the player to update
     * @return  boolean: true if updated, false if not updated
     */
    public boolean updateLoginDate(String username) {
        sb = new StringBuilder();
        sb.append("UPDATE login SET lastLoggedInDate=DATE_FORMAT( CURDATE(), '%m/%d/%Y') WHERE username='");
        sb.append(username);
        sb.append("';");
        try{
            Statement stmt = conn.createStatement();
            if(stmt.executeUpdate(sb.toString()) != 1){
                PiCasino.LOGGER.warning("lastLoggedInDate UPDATE failed. Please debug.");
                return false;
            }else{
                System.gc();
                return true;
            }
        }catch(SQLException sql){
            printError(sql,sb.toString());
            return false;
        }
    }

    /**
     *
     * Updates the Current chip Count of player username to chipCount
     *
     * @param username  The username of the player to update
     * @param chipCount The chip count to be updated to
     * @return  boolean: true if updated, false with error message if not updated
     */
    public boolean updatePlayerCurrentChipCount(String username, int chipCount) {
        // Get current High score from database
        int highCount = 0;
        sb = new StringBuilder();
        sb.append("SELECT highChipCount FROM userdata WHERE username='");
        sb.append(username);
        sb.append("';");

        try{
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sb.toString());
            while(rs.next()){
                highCount = rs.getInt(1);
            }
            rs.close();
            stmt.close();

            //Update High Chip count if current count > high count from DB.
            if(chipCount > highCount){
                //Create Query to update high score
                sb = new StringBuilder();
                sb.append("UPDATE `userdata` SET highChipCount=");
                sb.append(chipCount);
                sb.append(" WHERE username='");
                sb.append(username);
                sb.append("';");

                stmt = conn.createStatement();
                int result = stmt.executeUpdate(sb.toString());
                if(result != 1){
                    PiCasino.LOGGER.warning("Update of high chip count failed.");
                    return false;
                }
            }
            sb = new StringBuilder();
            sb.append("UPDATE userdata SET currentChipCount=");
            sb.append(chipCount);
            sb.append(" WHERE username='");
            sb.append(username);
            sb.append("';");

            stmt = conn.createStatement();
            int result = stmt.executeUpdate(sb.toString());
            if(result != 1){
                PiCasino.LOGGER.warning("Update of current chip count failed.");
                return false;
            }else{
                return true;
            }
        }catch(SQLException sql){
            printError(sql,sb.toString());
            return false;
        }
    }

    /**
     *
     * Checks if a player's login is valid
     *
     * @param username  The player to test login
     * @param password  The player's password
     * @return  boolean: true if login is valid, false with error if note
     */
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
                PiCasino.LOGGER.info("Stored password hash:\t" + oldPwdHash);
                PiCasino.LOGGER.info("Entered password hash:\t" + loginPwdHash);
                if(oldPwdHash.compareTo(loginPwdHash) == 0){
                    PiCasino.LOGGER.info("When compared, they match");
                }else{
                    PiCasino.LOGGER.info("When compared, they don't match");
                }
            }

            if(oldPwdHash.compareTo(loginPwdHash) == 0){
                return true;
            }else{
                if(DEBUG){
                    PiCasino.LOGGER.info("DEBUG: Passwords did not match. Please try logging in again.");
                }
                return false;
            }
        }catch(SQLException sql){
            printError(sql, sb.toString());
            return false;
        }
    }

    /**
     *
     * Checks if the user is already in the database
     *
     * @param username  The username to check
     * @return  boolean: true if username is already used, false if not
     */
    public boolean checkUserExist(String username){
        sb = new StringBuilder();
        sb.append("SELECT username FROM login WHERE username='");
        sb.append(username);
        sb.append("';");

        try{
            stmt = conn.createStatement();
            return stmt.executeQuery(sb.toString()).next();
        }catch(SQLException sql){
            printError(sql,sb.toString());
            return false;
        }

    }

    /**
     *
     * Private method to handle printing errors
     *
     * @param sql   The SQL Exception that was caught
     * @param query The query executed when the exception occurred
     */
    private void printError(SQLException sql, String query){
        PiCasino.LOGGER.warning(sql.toString());
        PiCasino.LOGGER.warning("Query: " + query);
        PiCasino.LOGGER.warning("SQL Exception:\n" + sql.getMessage());
        PiCasino.LOGGER.warning("SQL Message:\n" + sql.toString());
    }

    /**
     *  @pre Database with data in it
     * Cleans the database for testing
     *
     *  Returns nothing
     *  @post   Empty database
     */
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
