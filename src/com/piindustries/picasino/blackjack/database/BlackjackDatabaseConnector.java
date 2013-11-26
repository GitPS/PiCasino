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

    Connection conn;

    public BlackjackDatabaseConnector(){
        try{
            conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1/PiCasino","picasino","nASXn178WgQm6nx1YvF36gdq");
        }catch(SQLException sql){
            System.out.println("SQLException:" + sql.getMessage());
            System.out.println("SQLState: " + sql.getSQLState());
            System.out.println("VendorError: " + sql.getErrorCode());
        }
    }

    public boolean createNewPlayer(String username, String password, String firstName, String lastName, String email) {
        try{
            Statement addplayer = conn.createStatement();
            ResultSet rs = addplayer.executeQuery("INSERT INTO `userdata` natural join `login` VALUES('"+username+",sha1("+password+"),'"firstName + " " + lastName+"','"+email+"';");
        }catch(SQLException sql){

        }
        return false;  //To change body of implemented methods use File | Settings | File Templates.
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
