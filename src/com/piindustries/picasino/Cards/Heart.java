package com.piindustries.picasino.Cards;

/**
 * Created with IntelliJ IDEA.
 * User: 20578
 * Date: 9/25/13
 * Time: 1:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class Heart extends Card {
    String suit = "Hearts";

    public Heart(int inValue){
        super(inValue);
    }

    public String getSuit(){
        return suit;
    }
}
