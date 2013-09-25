package com.piindustries.picasino;

/**
 * Created with IntelliJ IDEA.
 * User: 20578
 * Date: 9/25/13
 * Time: 12:42 PM
 * To change this template use File | Settings | File Templates.
 */

public class ServerListener{
    static int port;
    java.net.ServerSocket ss = null;
    public ServerListener(int portNum){
        port = portNum;
        try{
            ss = new java.net.ServerSocket(port);
        }catch(java.io.IOException io){
            System.err.println("Could not listen on port: " + port);
        }
        if(ss.isBound()){
               System.out.println("Server listening on port: " + port);
        }
    }

    public boolean isListening(){
        return ss.isBound();
    }
}
