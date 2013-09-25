package com.piindustries.picasino;

/**
 * Created with IntelliJ IDEA.
 * User: 20578
 * Date: 9/25/13
 * Time: 12:42 PM
 * To change this template use File | Settings | File Templates.
 */

public class ServerListener{
    static int port = 63400;
    java.net.ServerSocket ss = null;
    public ServerListener(){
        try{
            ss = new java.net.ServerSocket(63400);
        }catch(java.io.IOException io){
            System.err.println("Could not listen on port: " + port);
        }
    }
}
