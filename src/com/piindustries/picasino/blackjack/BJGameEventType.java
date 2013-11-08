package com.piindustries.picasino.blackjack;

import java.util.ArrayList;

/**
 * Enumeration that contains all possible
 * {@link BJGameEvent} types.
 */
public enum BJGameEventType {
    ADD_PLAYER,
    REMOVE_PLAYER,
    ADVANCE_TO_BETTING,
    BET,
    PASS,
    ADVANCE_TO_DEALING,
    DEAL_CARD,
    ADVANCE_TO_PLAYING,
    HIT,
    SEND_CARD,
    STAND,
    SPLIT,
    DOUBLE_DOWN,
    ADVANCE_TO_CONCLUDING,
    ADVANCE_TO_INITIALIZATION,
    MESSAGE,
    PING,
    INTEGRETY_CHECK,
    PLAYER_DISCONNECT,
    REQUEST_GAMESTATE;

    public static ArrayList<BJGameEventType> getValidEvents( BJClientGameState gameState ){
        ArrayList<BJGameEventType> result = new ArrayList<>();
        BJClientGameState.BJPhases phase = gameState.getPhase();
        result.add(BJGameEventType.MESSAGE);
        result.add(BJGameEventType.PING);
        result.add(BJGameEventType.INTEGRETY_CHECK);
        result.add(BJGameEventType.PLAYER_DISCONNECT);
        result.add(BJGameEventType.REQUEST_GAMESTATE);
        switch(phase){
            case INITIALIZATION:
                result.add(BJGameEventType.ADD_PLAYER);
                result.add(BJGameEventType.REMOVE_PLAYER);
                result.add(BJGameEventType.ADVANCE_TO_BETTING);
                break;
            case BETTING:
                result.add(BJGameEventType.BET);
                result.add(BJGameEventType.PASS);
                result.add(BJGameEventType.ADVANCE_TO_DEALING);
                break;
            case DEALING:
                result.add(BJGameEventType.DEAL_CARD);
                result.add(BJGameEventType.ADVANCE_TO_PLAYING);
                break;
            case PLAYING:
                result.add(BJGameEventType.HIT);
                result.add(BJGameEventType.SEND_CARD);
                result.add(BJGameEventType.STAND);
                result.add(BJGameEventType.SPLIT);
                result.add(BJGameEventType.DOUBLE_DOWN);
                result.add(BJGameEventType.ADVANCE_TO_CONCLUDING);
                break;
            case CONCLUSION:
                result.add(BJGameEventType.ADVANCE_TO_INITIALIZATION);
                break;
            default: throw new Error("Logical Error.  Cannot Recover");
        }
        return result;
    }
}
