package vip.tianai;


import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import vip.tianai.event.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Accessors(chain = true)
public class ConCurrentExpiringMap<K, V> implements ExpiringMap<K, V> {

    private ConcurrentHashMap<K, TimeMapEntity<K, V>> storage;

    @Getter
    @Setter
    private SimpleApplicationEventMulticaster applicationEventPublisher;

    public ConCurrentExpiringMap() {
        this(null);
    }

    public ConCurrentExpiringMap(SimpleApplicationEventMulticaster applicationEventPublisher) {
        this(128, applicationEventPublisher);
    }

    @Override
    public void init() {
        this.applicationEventPublisher.multicastEvent(new InitTimeMapEvent(this));
    }

    public ConCurrentExpiringMap(Integer initialCapacity, SimpleApplicationEventMulticaster applicationEventPublisher) {
        storage = new ConcurrentHashMap<>(initialCapacity);
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public TimeMapEntity<K, V> put(K k, V v, Long expire) {
        if (expire == null || expire < 1) {
            expire = DEFAULT_EXPIRE;
        }
        TimeMapEntity<K, V> entity = new TimeMapEntity<>(k, v, expire, System.currentTimeMillis());
        TimeMapEntity<K, V> oldEntity = storage.put(k, entity);
        // 发送添加成功事件
        applicationEventPublisher.multicastEvent(new PutEntityEvent<K, V>(entity));
        return storage.put(k, entity);
    }

    @Override
    public Optional<TimeMapEntity<K, V>> getData(K k) {
        return Optional.ofNullable(storage.get(k));
    }

    @Override
    public Long getExpire(K k) {
        return getData(k).map(TimeMapEntity::getExpire).orElse(DEFAULT_EXPIRE);
    }

    @Override
    public boolean incr(K k, Long expire) {
        Optional<TimeMapEntity<K, V>> entityOptional = getData(k);
        if (!entityOptional.isPresent()) {
            return false;
        }
        synchronized (k) {
            // 双重校验
            entityOptional = getData(k);
            if (!entityOptional.isPresent()) {
                return false;
            }
            TimeMapEntity<K, V> entity = entityOptional.get();
            TimeMapEntity<K, V> oldEntity = new TimeMapEntity<K, V>(entity);

            TimeMapEntity<K, V> newEntity = entity;
            newEntity.setExpire(entity.getExpire() + expire);
            applicationEventPublisher.multicastEvent(new UpdateEntityEvent(oldEntity, newEntity));
            return true;
        }
    }

    @Override
    public int size() {
        return storage.size();
    }

    @Override
    public boolean isEmpty() {
        return storage.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return storage.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        Collection<TimeMapEntity<K, V>> values = storage.values();
        Optional<TimeMapEntity<K, V>> any = values.stream().filter(v -> v.getValue().equals(value)).findAny();
        return any.isPresent();
    }

    @Override
    public V get(Object key) {
        TimeMapEntity<K, V> timeMapEntity = storage.get(key);
        if (isTimeout(timeMapEntity)) {
            removeData(key);
            return null;
        }
        return timeMapEntity.getValue();
    }

    protected boolean isTimeout(K key) {
        Optional<TimeMapEntity<K, V>> data = getData(key);
        return isTimeout(data.orElse(null));
    }

    protected boolean isTimeout(TimeMapEntity<K, V> timeMapEntity) {
        if (timeMapEntity == null || DEFAULT_EXPIRE.equals(timeMapEntity.getExpire())) {
            return true;
        }
        long currentTimeMillis = System.currentTimeMillis();
        long timeout = timeMapEntity.getExpire() + timeMapEntity.getCreateTime();
        return timeout < currentTimeMillis;
    }

    @Override
    public V put(K key, V value) {
        return put(key, value, DEFAULT_EXPIRE).getValue();
    }

    @Override
    public V remove(Object key) {
        return removeData(key).map(TimeMapEntity::getValue).orElse(null);
    }

    protected Optional<TimeMapEntity<K, V>> removeData(Object key) {
        synchronized (key) {
            TimeMapEntity<K, V> oldValue = storage.get(key);
            if(oldValue != null) {
                TimeMapEntity<K, V> entity = storage.remove(key);
                applicationEventPublisher.multicastEvent(new RemoveEntityEvent<>(oldValue));
                if (entity != null) {
                    return Optional.of(entity);
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        m.forEach(this::put);
    }

    @Override
    public void clear() {
        Map<K, TimeMapEntity<K, V>> copyStorage = new HashMap<>(storage);
        storage.clear();
        // 发送清除事件
        applicationEventPublisher.multicastEvent(new ClearEntityEvent(copyStorage));
    }

    /**
     * 这个可能会消耗点cpu
     * @return
     */
    @Override
    public Set<K> keySet() {
        return storage.keySet()
                .stream()
                .parallel()
                .filter(k -> !isTimeout(k))
                .collect(Collectors.toSet());
    }

    @Override
    public Collection<V> values() {
        return storage.values().stream().map(TimeMapEntity::getValue).collect(Collectors.toSet());
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        throw new IllegalArgumentException("timemap not impl entrySet.");
    }
}
