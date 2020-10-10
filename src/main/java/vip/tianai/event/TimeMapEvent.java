package vip.tianai.event;

import org.springframework.context.ApplicationEvent;

public class TimeMapEvent<K,V> extends ApplicationEvent {

    public TimeMapEvent(Object source) {
        super(source);
    }
}
