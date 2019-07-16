package com.company;

public class Message {
    int type;
    String body;
    public Message(int type, String body) {
        /*
            Message types
             initialize – the initial board state, player information
             move – a list of effects
         */
        this.type = type;
        this.body = body;
    }
}
