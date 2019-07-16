package com.company;

import java.util.*;
import java.util.concurrent.*;

// TODO - send timeouts
// TODO - + removing subscribers that are dead
public class Broker extends Thread {
    class Mailbox {
        Queue<Message> q;
        boolean sending = false;
        public Mailbox() {
            q = new ConcurrentLinkedQueue<>();
        }
    }
    List<Subscriber> subs = new ArrayList<>();
    HashMap<Subscriber, Mailbox> mailboxes;
    Executor executor;
    CompletionService<SubscriberResponse> cs;
    boolean noMoreMessages;
    boolean silenced;
    public Broker() {
        int aps = 1;//Runtime.getRuntime().availableProcessors();
        executor = Executors.newFixedThreadPool(aps);
        cs = new ExecutorCompletionService<>(executor);
        mailboxes = new HashMap<>();
    }
    int jobs = 0;
    private synchronized boolean changeJobs(int delta) {
       jobs += delta;
       return jobs == 0;
    }

    public void unsilenceAll() {
        silenced = false;
    }
    public void silenceAll() {
        silenced = true;
    }

    public void noMoreMessages() {
       noMoreMessages = true;
    }
    public void publish(Message msg) {
//        System.out.println("publishing");
//        System.out.println(msg.body);
        for(Subscriber s : subs) {
            Mailbox q = mailboxes.get(s);
//            System.out.println("about to acquire q lock");
            synchronized (q) {
//                System.out.println("acquired.");
//                System.out.println("sending to "+s.sink.getClass().getTypeName());
                if(!q.sending) {
//                    System.out.println("first."+s.sink.getClass().getTypeName());
                    q.sending = true;
                    changeJobs(1);
//                    System.out.println("directly: "+msg.body);
                    asyncSend(s, Collections.singletonList(msg));
                } else {
//                    System.out.println("q'd."+s.sink.getClass().getTypeName());
                    q.sending = true;
                    q.q.add(msg);
//                    System.out.println("via enqueue: "+msg.body);
                }
            }
        }
//        System.out.println("done enqueue publish.");
    }
    public void run() {
        while(!noMoreMessages) {
            try {
                Future<SubscriberResponse> resultFuture = cs.take(); // blocks if none available
                SubscriberResponse result = resultFuture.get();
                if (result.shouldRetry()) {
                    asyncSend(result.s, result.msgs);
                } else {
                    Mailbox q = mailboxes.get(result.s);
                    // success!
                    synchronized (q) {
                        if (!q.q.isEmpty()) {
                            // drain queue, submit new send task
                            Message next = q.q.poll();
                            List<Message> nexts = new ArrayList<>();
                            while(next != null) {
                                nexts.add(next);
                                next = q.q.poll();
                            }
//                            System.out.println("later sent");
                            asyncSend(result.s, nexts);
                        } else {
                            q.sending = false;
                        }
                        if (changeJobs(-1)) {
                            continue;
                        }
                    }
                }
            }
            catch(InterruptedException e) {
                // we don't care
            }
            catch(ExecutionException e) {
                // basically should not happen - b/c interface of SubscriberSink
            }
        }
    }
    public void removeSubscriber(Subscriber s) {
        subs.remove(s);
        mailboxes.remove(s);
    }
    public void addSubscriber(Subscriber s) {
        subs.add(s);
        mailboxes.put(s, new Mailbox());
    }
    private void asyncSend(Subscriber s, List<Message> nexts) {
            cs.submit(() -> s.send(nexts));

//        System.out.println("broadcasting");
//        System.out.println(s.sink.getClass().getTypeName());
    }
}
