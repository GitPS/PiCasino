package com.piindustries.picasino.Cards;

/**
 * Created with IntelliJ IDEA.
 * User: 20578
 * Date: 9/25/13
 * Time: 1:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class Card {

    int value = 0;

    public Card(int inValue){
        if(inValue > 0 || inValue < 14){
            value=inValue;
        }else{
            System.err.println("Invalid input value for a card!");
        }
    }

    public int getValue(){
        return value;
    }
}
