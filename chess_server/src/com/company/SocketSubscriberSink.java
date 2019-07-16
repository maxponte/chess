package com.company;

import com.google.gson.Gson;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.List;

public class SocketSubscriberSink extends Thread implements SubscriberSink {
    SocketChannel client;
    Gson gson;
    public SocketSubscriberSink(Gson gson, SocketChannel client) {
        this.client = client;
        this.gson = gson;
        try {

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public boolean send(List<Message> msgs) {
        String yeah = gson.toJson(msgs);
        byte[] ba = Frame.convert(yeah);
        ByteBuffer b = ByteBuffer.allocate(ba.length);
        b.put(ba);
        b.flip();
        try {
            client.write(b);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }
}
