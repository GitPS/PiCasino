package com.piindustries.picasino.server_net;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.ServerSocket;

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

    public void accept(){
        try{
            ss.accept();
            System.out.println("Listening on port: " + ss.getLocalPort());
        }catch(IOException e){
            System.out.println("Could not listen on port:" + ss.getLocalPort());
        }
    }
}
