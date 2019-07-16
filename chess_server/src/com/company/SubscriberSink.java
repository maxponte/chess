package com.company;

import java.io.Serializable;
import java.util.List;

public interface SubscriberSink {
    boolean send(List<Message> s);
}
