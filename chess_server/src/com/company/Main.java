package com.company;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.*;

import static com.company.PieceTypeSerializer.pieceTypeSerializer;

public class Main {
    public static void main(String[] args) {
        Gson gson = new GsonBuilder().
                registerTypeAdapter(PieceType.class, pieceTypeSerializer()).
                create();

        Broker b = new Broker();
        File file = new File("./chess_log.txt");
        try {
            PrintStream out = new PrintStream(file);
            SubscriberSink ts = new TextOutputSubscriberSink(gson, out);
            Subscriber s = new Subscriber(ts);
            b.addSubscriber(s);
        } catch(FileNotFoundException fnf) {
        }
        try {
            b.start(); // start broker thread
            Game g = new Game(gson, b);
            GameController gc = new GameController(gson, b, g);
            gc.run(); // run game controller, blocks and listens to sockets
            b.noMoreMessages(); // exception in game controller, process remaining msgs
            b.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void testCastling(Game g){
        // kingside pawn
        g.move("e7","e5");

        // qs pawn black
        g.move("d2","d4");

        // ks bishop
        g.move("f8","c5");

        // black qs knight out
        g.move("b1","c3");

        // get ks knight out
        g.move("g8","f6");

        // black qs bishop out
        g.move("c1","e3");

        // castle white king side
        g.move("e8","g8");

        // black queen out
        g.move("d1","d2");

        // white move corner pawn
        g.move("h7","h6");

        // black castles
        g.move("e1","c1");
    }
    public static void testFourMoveMate(Game g){
        g.move("e7","e5");
        g.move("e2","e4");
        g.move("d8","f6");
        g.move("b1","c3");
        g.move("f8","c5");
        g.move("h2","h3");
        g.move("f6","f2");
    }
    public static void interactive(Game g){
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("Move: ");
            String moveStr = scanner.next();
            String[] mvs = moveStr.split(",");
            g.move(mvs[0], mvs[1]);
        }
    }
}
