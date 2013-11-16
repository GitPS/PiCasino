package com.piindustries.picasino.blackjack.test;

import com.piindustries.picasino.api.InvalidGameEventException;
import com.piindustries.picasino.blackjack.client.ClientGameState;
import com.piindustries.picasino.blackjack.domain.Cards;
import com.piindustries.picasino.blackjack.domain.GameEvent;
import com.piindustries.picasino.blackjack.domain.GameEventType;
import com.piindustries.picasino.blackjack.domain.Hand;

import java.io.IOException;

/**
 * User: A. Jensen
 * Date: 11/15/13
 * Time: 3:47 PM
 */
public class Invoker {
    private ClientGameState innards;

    public Invoker(ClientGameState toSet){
        this.setInnards(toSet);
        System.out.println("What action?");
        printActions();
    }

    public ClientGameState getInnards() {
        return innards;
    }

    public void setInnards(ClientGameState innards) {
        this.innards = innards;
    }

    private void printActions(){
        for( GameEventType s : GameEventType.values() )
            System.out.println( '\t' + s.name());
    }

    public synchronized void step(){
        String[] input = readLine().trim().split("[ ]");
        String type = input[0].toLowerCase();
        GameEvent toInvoke = new GameEvent();
        try{
            switch(type){
                case "status":
                    System.out.println("Phase : " + this.getInnards().getPhase().name());
                    for( Hand h : this.getInnards().getHands() ){
                        System.out.print( h.getUsername() + " " );
                        for( int c : h.getCards() )
                            System.out.print(Cards.evaluateCardName(c) + " ");
                        System.out.print('\n');
                    }
                    step();
                    return;
                case "quit":
                    return;
                case "addplayer":
                    toInvoke.setType(GameEventType.ADD_PLAYER);
                    toInvoke.setValue( input[1] );
                    break;
                case "removeplayer":
                    toInvoke.setType(GameEventType.REMOVE_PLAYER);
                    toInvoke.setValue( input[1] );
                    break;
                case "advancetobetting":
                    toInvoke.setType(GameEventType.ADVANCE_TO_BETTING);
                    break;
                case "bet":
                    toInvoke.setType(GameEventType.BET);
                    toInvoke.setValue( Integer.valueOf(input[1]) );
                    break;
                case "pass":
                    toInvoke.setType(GameEventType.PASS);
                    break;
                case "advancetodealing":
                    toInvoke.setType(GameEventType.ADVANCE_TO_DEALING);
                    break;
                case "dealcard":
                    toInvoke.setType(GameEventType.DEAL_CARD);
                    toInvoke.setValue( Integer.valueOf(input[1]) );
                    break;
                case "advancetoplaying":
                    toInvoke.setType(GameEventType.ADVANCE_TO_PLAYING);
                    break;
                case "hit":
                    toInvoke.setType(GameEventType.HIT);
                    break;
                case "sendcard":
                    toInvoke.setType(GameEventType.SEND_CARD);
                    toInvoke.setValue( Integer.valueOf(input[1]) );
                    break;
                case "stand":
                    toInvoke.setType(GameEventType.STAND);
                    break;
                case "doubledown":
                    toInvoke.setType(GameEventType.DOUBLE_DOWN);
                    break;
                case "advancetoconcluding":
                    toInvoke.setType(GameEventType.ADVANCE_TO_CONCLUDING);
                    break;
                case "advancetoinitialization":
                    toInvoke.setType(GameEventType.ADVANCE_TO_INITIALIZATION);
                    break;
                default: /* do nothing */
                    break;
            }
        } catch (IndexOutOfBoundsException e){
            System.out.println("Not enough arguments for "+type);
        }
        try{    // FIXME double invoked
            this.getInnards().invoke(toInvoke);
        } catch(InvalidGameEventException e) {
            System.out.println("Invalid");
        }
        this.getInnards().getNetworkHandler().send(toInvoke);
        step();
    }

    private synchronized String readLine(){
        StringBuilder result = new StringBuilder();
        try {
            char focus = (char)System.in.read();
            while( focus != '\n' ){
                result.append(focus);
                focus = (char)System.in.read();
            }
        } catch (IOException e){
            System.out.println("IO Failure");
        }
        return result.toString();
    }
}
