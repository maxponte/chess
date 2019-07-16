package com.company;

import com.google.gson.Gson;
import org.apache.commons.text.StringEscapeUtils;

public interface GameRules {
    default void handleMessage(Gson gson, Broker broker, Game g, ClientMessage cm) {
        if(cm.kind == ClientMessage.messageTypeReset) {
            g.reset();
            g.initBoard();
        } else if(cm.kind == ClientMessage.messageTypeMove) {
            String quoted = StringEscapeUtils.unescapeJson(cm.message);
            String unq = quoted.substring(1, quoted.length()-1);
            RemoteMove rm = gson.fromJson(unq, RemoteMove.class);
            g.move(rm.source, rm.target);
        }
    }
}
