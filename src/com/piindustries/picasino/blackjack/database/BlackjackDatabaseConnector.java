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
    private Connection conn;
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
        StringBuilder stmt = new StringBuilder();
        try{
            stmt.append("INSERT INTO `login`(username,password) VALUES(");
            stmt.append("'"+username + "',sha1('" + password + "'));");
            Statement addPlayer = conn.createStatement();
            if(addPlayer.executeUpdate(stmt.toString()) != 1){
                System.out.println("INSERT statement returned something other than 0. Please debug further.");
                return false;
            }else{
                stmt = new StringBuilder();
                stmt.append("UPDATE `userdata` SET name='" + firstName + " " + lastName + "',email='" + email + "' WHERE username='" + username + "';");
                if(addPlayer.executeUpdate(stmt.toString()) != 1){
                    System.out.println("UPDATE statement returned something other than 0. Please debug further.");
                    return false;
                }
                return true;
            }
        }catch(SQLException sql){
            if(sql.getErrorCode() == 1062){
                System.out.println("User " + username + " already exists! Please choose another username");
            }else{
                System.out.println("Query: " + stmt.toString());
                System.out.println("SQL Exception:\n" + sql.getErrorCode() + ": " + sql.getMessage());
                System.out.println("SQL Message:\n" + sql.toString());
            }
            return false;
        }
    }

    public boolean changeUserPassword(String username, String oldPassword, String newPassword){
        int rowcount = 0;
        String query = "SELECT password FROM login where username='" + username + "';";
        try{
            Statement updatePassword = conn.createStatement();
            ResultSet oldPwd = updatePassword.executeQuery(query);
            if(oldPwd.last()){
                rowcount = oldPwd.getRow();
                oldPwd.beforeFirst();
            }

            if(rowcount < 1 || rowcount > 1){
                System.out.println("Rows returned was either < or > 1. Please correct the database mistake.");
                return false;
            }

            while(oldPwd.next()){
                String prevPassword = oldPwd.getString(1);
                if(oldPassword.compareTo(prevPassword) == 0){
                    query = "UPDATE login SET password='" + newPassword + "' WHERE username='" + username + "';";
                    int rs = updatePassword.executeUpdate(query);
                    if(rs != 1){
                        System.out.println("Something went wrong with password update. Please check and correct.");
                        return false;
                    }else{
                        return true;
                    }
                }
            }
        }catch(SQLException sql){
            System.out.println("Query: " + query.toString());
            System.out.println("SQL Exception:\n" + sql.getMessage());
            System.out.println("SQL Message:\n" + sql.toString());
            return false;
        }
        return false;
    }

    public boolean updateLoginDate(String username) {
        return false;
    }

    public boolean updatePlayerCurrentChipCount(String username, int chipCount) {
        return false;
    }

    @Override
    public boolean updatePlayerHighScore(String username, int highChipCount) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean checkPlayerLogin(String username, String password) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
