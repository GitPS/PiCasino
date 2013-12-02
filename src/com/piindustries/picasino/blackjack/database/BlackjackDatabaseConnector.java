package com.piindustries.picasino.blackjack.database;

import com.piindustries.picasino.api.DatabaseConnector;
import java.sql.*;
/**
 * Created with IntelliJ IDEA.
 * User: reis_as
 * Date: 11/25/13
 * Time: 7:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class BlackjackDatabaseConnector implements DatabaseConnector{
    private Connection conn;        // Connection object for DriverManager.getConnection
    private StringBuilder sb;       // StringBuilder used in most methods
    private Statement stmt;         // Statement used for queries

    public BlackjackDatabaseConnector(){
        try{
            //Used for testing via WAN
            conn = DriverManager.getConnection("jdbc:mysql://home.andrewreiscomputers.com:3306/PiCasino","picasino","nASXn178WgQm6nx1YvF36gdq");

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
            sb.append("INSERT INTO `login`(username,password) VALUES(");
            sb.append("'"+username + "',sha1('" + password + "'));");
            stmt = conn.createStatement();
            if(stmt.executeUpdate(sb.toString()) != 1){
                System.out.println("INSERT statement returned something other than 0. Please debug further.");
                stmt.close();
                return false;
            }else{
                sb = new StringBuilder();
                sb.append("UPDATE `userdata` SET name='" + firstName + " " + lastName + "',email='" + email + "' WHERE username='" + username + "';");
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
        String oldPwd = null, oldPwdEntered = null;
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
        }
    }

    public boolean updateLoginDate(String username) {
        sb = new StringBuilder();
        sb.append("UPDATE login SET lastLoggedInDate=DATE_FORMAT( CURDATE(), '%m/%d/%Y') WHERE username='"+username+"';");
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
        sb.append("UPDATE `userdata` SET currentChipCount=" + chipCount + " WHERE username='" + username + "';");
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
        sb.append("UPDATE `userdata` SET highChipCount=" + highChipCount + " WHERE username='" + username + "';");
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

            if(oldPwdHash.compareTo(loginPwdHash) == 0){
                return true;
            }else{
                System.out.println("Passwords did not match. Please try logging in again.");
                return false;
            }
        }catch(SQLException sql){
            printError(sql, sb.toString());
            return false;
        }
    }

    private void printError(SQLException sql, String query){
        sql.printStackTrace();
        System.out.println("Query: " + query.toString());
        System.out.println("SQL Exception:\n" + sql.getMessage());
        System.out.println("SQL Message:\n" + sql.toString());
    }
}
