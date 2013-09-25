package com.piindustries.picasino;

/**
 * Date: 9/20/13
 * Time: 1:18 PM
 */

public class PiCasino {

    public static void main(String[] args) {
        PiCasino pc = new PiCasino();
    }


    public PiCasino() {
        ServerListener sl = new ServerListener(63400);
        System.out.println(sl.isListening());
    }
}
