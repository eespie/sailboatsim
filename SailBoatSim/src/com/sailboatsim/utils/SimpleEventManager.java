package com.sailboatsim.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleEventManager {

    private final Map<String, List<SimpleEventListener>> listenersByEvent = new HashMap<String, List<SimpleEventListener>>();

    public void register(String event, SimpleEventListener listener) {
        List<SimpleEventListener> listeners = listenersByEvent.get(event);
        if (listeners == null) {
            listeners = new ArrayList<SimpleEventListener>();
            listenersByEvent.put(event, listeners);
        }
        listeners.add(listener);
    }

    public void triggerEvent(String event, Object eventData) {
        List<SimpleEventListener> listeners = listenersByEvent.get(event);
        if (listeners != null) {
            for (SimpleEventListener listener : listeners) {
                listener.onEvent(event, eventData);
            }
        }
    }

}
