package com.company;

import com.google.gson.Gson;

import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;

public class GameController {
    private Game game;
    private Broker broker;
    Gson gson;
    public GameController(Gson gson, Broker broker, Game game) {
        this.broker = broker;
        this.game = game;
        this.gson = gson;
    }
    public void run() {
        try {
            WebSocketServer ws = new WebSocketServer();
            Map<SocketChannel, Subscriber> active = new HashMap<>();
//            GameRules rules = new GameRulesFindCheckmate(15);
//            GameRules rules = new GameRulesUniformRandom();
            GameRules rules = new GameRulesAI();
            Object next;
            while ((next = ws.q.take())  != null) {
                if (next instanceof Frame) {
                    String pl = ((Frame)next).payload;
                    ClientMessage cm = gson.fromJson(pl, ClientMessage.class);
                    rules.handleMessage(gson, broker, game, cm);
                } else if (next instanceof SocketChannel)  {
                    SocketChannel chan = (SocketChannel)next;
                    if(active.containsKey(chan)) {
                        broker.removeSubscriber(active.get(chan));
                        active.remove(chan);
                    } else {
                        SubscriberSink ts = new SocketSubscriberSink(new Gson(), chan);
                        Subscriber s = new Subscriber(ts);
                        active.put(chan, s);
                        broker.addSubscriber(s);
                        game.initBoard();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
