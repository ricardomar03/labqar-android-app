package com.ua.ricardomartins.qualar;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by ricardo on 18/05/16.
 */
public class NotificationID {
    private final static AtomicInteger id = new AtomicInteger(0);
    public static int getID() {
        return id.incrementAndGet();
    }
}
