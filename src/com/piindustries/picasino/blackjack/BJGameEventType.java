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
    INTEGRITY_CHECK,
    PLAYER_DISCONNECT,
    REQUEST_GAME_STATE
}
