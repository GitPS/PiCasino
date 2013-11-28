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
            conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1/PiCasino","picasino","nASXn178WgQm6nx1YvF36gdq");
        }catch(SQLException sql){
            System.out.println("SQL Exception:" + sql.getMessage());
            System.out.println("SQL State: " + sql.getSQLState());
            System.out.println("Vendor Error: " + sql.getErrorCode());
        }
    }

    public boolean createNewPlayer(String username, String password, String firstName, String lastName, String email) {
        try{
            StringBuilder stmt = new StringBuilder();
            stmt.append("INSERT INTO `login` NATURAL JOIN `userdata` VALUES('");
            stmt.append(username + "','" + password + "','" + firstName + " " + lastName + "','" + email + "',0,0");
            stmt.append(");");
            Statement addPlayer = conn.createStatement();
            ResultSet rs = addPlayer.executeQuery(stmt.toString());

            return true;
        }catch(SQLException sql){
            System.out.println("SQL Exception: " + sql.getMessage());
            System.out.println("SQL Message: " + sql.toString());
            return false;
        }
    }

    @Override
    public boolean changeUserPassword(String username, String oldPassword, String newPassword){
        return false;
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
}
