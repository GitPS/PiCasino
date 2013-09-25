package com.piindustries.picasino;

import com.piindustries.picasino.server_net.ServerListener;
import com.piindustries.picasino.Cards.*;

/**
 * Date: 9/20/13
 * Time: 1:18 PM
 */

public class PiCasino {

    public static void main(String[] args){
        PiCasino pc = new PiCasino();
    }


    public PiCasino(){
        java.util.ArrayList<Card> cardList = new java.util.ArrayList<Card>();
        for(int i = 0; i < 15; i++){
            cardList.add(new Heart(i));
        }

        for(int i = 0; i < cardList.size(); i++){
            Card c = cardList.get(i);
            System.out.println("This is a " + c.getValue() + " of " + c.getSuit());
        }
    }
}
