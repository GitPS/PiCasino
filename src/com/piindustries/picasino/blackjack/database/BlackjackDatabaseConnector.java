package com.piindustries.picasino.blackjack.database;

import com.piindustries.picasino.api.DatabaseConnector;

/**
 * Created with IntelliJ IDEA.
 * User: reis_as
 * Date: 11/25/13
 * Time: 7:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class BlackjackDatabaseConnector implements DatabaseConnector{
    @Override
    public boolean createNewPlayer(String username, String password, String firstName, String lastName, String email) {
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
