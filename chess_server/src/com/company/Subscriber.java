package com.company;

import java.util.ArrayList;
import java.util.List;

public class Subscriber {
    SubscriberSink sink;
    public Subscriber(SubscriberSink sink) {
       this.sink = sink;
    }
    public SubscriberResponse send(List<Message> ms) {
        boolean success = sink.send(ms);
        return new SubscriberResponse(ms, this);
    }
}
