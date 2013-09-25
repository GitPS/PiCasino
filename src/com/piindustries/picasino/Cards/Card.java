package com.piindustries.picasino.Cards;

import com.sun.javaws.exceptions.InvalidArgumentException;
import org.omg.CORBA.DynAnyPackage.InvalidValue;

/**
 * Created with IntelliJ IDEA.
 * User: 20578
 * Date: 9/25/13
 * Time: 1:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class Card implements Comparable<Card> {

    int value = 0;

    public Card(int inValue){
        if(inValue > 0 && inValue < 14){
            value=inValue;
        }else{
            throw new IllegalArgumentException("Invalid input value for a card");
        }
    }

    public int getValue(){
        return value;
    }

    public int compareTo(Card c){
        if(getValue() > c.getValue()){
            return 1;
        }else if(getValue() == c.getValue()){
            return 0;
        }else{
            return -1;
        }
    }
}
