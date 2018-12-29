package com.pratham.prathamdigital.services;

import org.greenrobot.eventbus.EventBus;

public class EventProvider {
    private static final EventBus BUS = new EventBus();

    public static EventBus getInstance() {
        return BUS;
    }

    private EventProvider() {
        // No instances.
    }
}
