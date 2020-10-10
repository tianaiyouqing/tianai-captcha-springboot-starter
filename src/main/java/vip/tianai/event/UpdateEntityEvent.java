package vip.tianai.event;


import vip.tianai.ExpiringMap;

public class UpdateEntityEvent extends TimeMapEvent {
    public UpdateEntityEvent(ExpiringMap.TimeMapEntity oldEntity, ExpiringMap.TimeMapEntity newEntity) {
        super(newEntity);
    }
}
