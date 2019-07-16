package com.company;

import java.util.List;

public class SubscriberResponse {
    List<Message> msgs;
    Subscriber s;
    boolean shouldRetry = false;
    public SubscriberResponse(List<Message> msgs, Subscriber s) {
        this.s = s;
        this.msgs = msgs;
    }
    boolean shouldRetry() {
        return shouldRetry;
    }
}
