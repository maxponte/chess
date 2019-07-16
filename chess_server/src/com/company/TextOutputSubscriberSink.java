package com.company;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class TextOutputSubscriberSink implements SubscriberSink {
    Board board;
    PrintStream out;
    Gson gson;
    public TextOutputSubscriberSink(Gson gson, PrintStream out) {
        this.gson = gson;
        this.out = out;
    }
    @Override
    public boolean send(List<Message> ms) {
        List<Callable> defers = new ArrayList<>();
        for(Message s : ms) {
            if (s.type == 1) {
                try {
                    board = gson.fromJson(s.body, Board.class);
                } catch (Exception e) {
                    return false;
                }
                defers.add(() -> {
                    board.print(out);
                    return null;
                });
            } else if (s.type == 2) {
                defers.add(() -> {
                    out.println(s.body);
                    return null;
                });
            }
        }
        for (Callable c : defers) {
            try {
                c.call();
            } catch(Exception e) {
                // won't happen
            }
        }
        return true;
    }
}
