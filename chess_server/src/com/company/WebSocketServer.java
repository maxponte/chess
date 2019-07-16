package com.company;

import com.google.gson.Gson;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebSocketServer extends Thread {
    LinkedBlockingQueue<Object> q;
    ByteBuffer buffer;
   public WebSocketServer() {
       q = new LinkedBlockingQueue<>();
        start();
    }
    public void run() {
try {
            Selector s = Selector.open();
            ServerSocketChannel ssc = ServerSocketChannel.open();
            InetSocketAddress addr = new InetSocketAddress("maxwells-macbook-pro.local", 1099);
            ssc.bind(addr);
            ssc.configureBlocking(false);
            int ops = ssc.validOps();
            SelectionKey sk = ssc.register(s, ops, null);
            while (true) {
                s.select();
                Set<SelectionKey> keys = s.selectedKeys();
                Iterator<SelectionKey> iterator = keys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    if (key.isAcceptable()) {
                        SocketChannel client = ssc.accept();
                        client.configureBlocking(false);
                        client.register(s, SelectionKey.OP_READ);
                    } else if (key.isReadable()) {
                        SocketChannel chan = (SocketChannel) key.channel();
                        buffer = ByteBuffer.allocate(1024);
                        boolean closed = readMessage(chan);
                        if(closed) {
                            key.cancel();
                            q.put(chan);
                        }
                    }
                    iterator.remove();
                }
            }
} catch (Exception e){
    e.printStackTrace();
}

    }
    private boolean readMessage(SocketChannel client) {
       try {
           int read = client.read(buffer);
           buffer.flip();
           int n = buffer.remaining();
           byte[] arr = new byte[n];
           buffer.get(arr);
           String bufstr = new String(arr, StandardCharsets.UTF_8);
           Scanner scanner = new Scanner(bufstr).useDelimiter("\\r\\n\\r\\n");
           String data = scanner.next();

           Matcher get = Pattern.compile("^GET").matcher(data);

           if (get.find()) {
               Matcher match = Pattern.compile("Sec-WebSocket-Key: (.*)").matcher(data);
               match.find();
               byte[] response = ("HTTP/1.1 101 Switching Protocols\r\n"
                       + "Connection: Upgrade\r\n"
                       + "Upgrade: websocket\r\n"
                       + "Sec-WebSocket-Accept: "
                       + DatatypeConverter
                       .printBase64Binary(
                               MessageDigest
                                       .getInstance("SHA-1")
                                       .digest((match.group(1) + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11")
                                               .getBytes("UTF-8")))
                       + "\r\n\r\n")
                       .getBytes("UTF-8");

               ByteBuffer res = ByteBuffer.allocate(1024);
               res.put(response);
               res.flip();
               client.write(res);
               q.put(client);
               System.out.println("A client connected." + client);
               return false;
           }  else {
               if(Frame.isPing(arr)) {
                   Frame.toPong(arr);
                   ByteBuffer res = ByteBuffer.allocate(1024);
                   res.put(arr);
                   res.flip();
                   client.write(res);
                   System.out.println("ping! sent pong.");
               } else {
                   String x= Frame.bytesToHex(arr);
                   Frame f = new Frame(arr);
                   if (f.opcode == 8) {
                       // closing the connection
                       ByteBuffer res = ByteBuffer.allocate(1024);
                       res.put(arr);
                       res.flip();
                       client.write(res);
                       client.close();
                       return true;
                   } else {
                       q.put(f);
                   }
               }
           }
       } catch (Exception e) {
           e.printStackTrace();
       }
       return false;
    }
}
