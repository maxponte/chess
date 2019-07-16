package com.company;

import com.google.gson.Gson;

public class GameRulesUniformRandom implements GameRules {
    public GameRulesUniformRandom() {
    }
    public void handleMessage(Gson gson, Broker broker, Game g, ClientMessage cm) {
        if(cm.kind == ClientMessage.messageTypeAuto) {
            boolean can = g.autoMove();
            if(!can) {
                g.simpleMessage("tie");
                g.winner = Color.GREY;
            }
            g.showBoard();
        } else {
            GameRules.super.handleMessage(gson, broker, g, cm);
        }
    }
}
