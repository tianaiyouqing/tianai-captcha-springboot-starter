package vip.tianai.event;

public class ClearEntityEvent<K,V> extends TimeMapEvent<K,V> {


    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public ClearEntityEvent(Object source) {
        super(source);
    }

}
