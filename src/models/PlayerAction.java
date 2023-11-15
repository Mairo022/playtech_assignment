package models;

import enums.Action;

public class PlayerAction {
    public final String playerID;
    public final String matchID;
    public final Action action;
    public final String side;
    public final int coin;

    public PlayerAction(String playerID, String matchID, Action action, int coin, String side) {
        this.playerID = playerID;
        this.matchID = matchID;
        this.action = action;
        this.side = side;
        this.coin = coin;
    }
}
