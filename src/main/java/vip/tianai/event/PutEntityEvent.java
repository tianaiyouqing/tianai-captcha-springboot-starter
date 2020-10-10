package vip.tianai.event;


import vip.tianai.ExpiringMap;

public class PutEntityEvent<K,V> extends TimeMapEvent<K,V> {

    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public PutEntityEvent(ExpiringMap.TimeMapEntity<K, V> source) {
        super(source);
    }
}
