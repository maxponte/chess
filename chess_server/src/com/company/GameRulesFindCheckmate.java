package com.company;

import com.google.gson.Gson;

public class GameRulesFindCheckmate implements GameRules {
    int minPieces;
    public GameRulesFindCheckmate(int minPieces) {
        this.minPieces = minPieces;
    }
    public void handleMessage(Gson gson, Broker broker, Game g, ClientMessage cm) {
        if(cm.kind == ClientMessage.messageTypeAuto) {
            broker.silenceAll();
            if(g.winner != null) g.reset();
            while(g.winner == null) {
                boolean can = g.autoMove();
                if(!can) {
                    broker.unsilenceAll();
                    g.simpleMessage("tie");
                    g.winner = Color.GREY;
                    break;
                }
                if (g.nPieces() < minPieces) g.reset();
            }
            broker.unsilenceAll();
            g.showBoard();
        } else {
            GameRules.super.handleMessage(gson, broker, g, cm);
        }
    }
}
