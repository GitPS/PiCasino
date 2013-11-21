package com.piindustries.picasino.blackjack.test;

import com.piindustries.picasino.blackjack.domain.Cards;

public class CardMaxValue {

    public static void main(String[] args){
        for(int i =0; i < 20; i++ ){
            int[] deck = newDeck();
            int[] hand = new int[(int)(Math.random()*5) + 1];
            for(int run = 0; run <  hand.length; run++)
                hand[run] = deck[ (int)(Math.random()*52) ];
            StringBuilder sb = new StringBuilder();
            sb.append("Ids: [ ");
            for( int card: hand )
                sb.append( card + " " );
            sb.append("] Hand: [ ");
            for( int card : hand ){ sb.append(Cards.evaluateCardName(card) + " "); }
            sb.append("] Value: " + Cards.getMaxHandValue(hand));
            System.out.println( sb.toString() );
        }
    }

    public static int[] newDeck(){
        int[] cards = new int[52];
        // Populate cards array
        for(int i = 0; i < 52; i ++)
            cards[i] = i;
        return cards;
    }
}
