package com.company;

import com.google.gson.Gson;

public class GameRulesAI implements GameRules {
    public void handleMessage(Gson gson, Broker broker, Game g, ClientMessage cm) {
        GameRules.super.handleMessage(gson, broker, g, cm);
        if(g.nextMoveID % 2 == 1 && cm.kind == ClientMessage.messageTypeMove) {
            boolean can = g.autoMinimaxMove();
            if(!can) {
                g.simpleMessage("tie");
                g.winner = Color.GREY;
            }
            g.showBoard();
        }
    }
}
