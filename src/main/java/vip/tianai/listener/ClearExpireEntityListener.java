package vip.tianai.listener;


import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import vip.tianai.ExpiringMap;
import vip.tianai.event.InitTimeMapEvent;
import vip.tianai.event.PutEntityEvent;
import vip.tianai.event.RemoveEntityEvent;
import vip.tianai.event.TimeMapEvent;

import java.util.*;


@Data
@Slf4j
public class ClearExpireEntityListener implements ApplicationListener<TimeMapEvent>, Runnable, Thread.UncaughtExceptionHandler {

    public static final int RANDOM_EXPIRE_KEYS_FACTOR = 10;

    /**
     * 守护线程名称.
     */
    public static final String CLEAR_EXPIRE_ENTITY_THREAD_NAME = "ClearExpireEntityThread";
    /**
     * 这里使用list的原因.
     */
    List<Object> expireKeys = Collections.synchronizedList(new ArrayList<>(128));


    private ExpiringMap expiringMap;

    /** 是否启动. */
    private boolean start = true;

    /** 每次最大读取数. */
    private Integer maxReadExpireKeys = 20;

    @Deprecated
    private Long maxReadExpireTimeout = 200L;

    /** 每次读取过期key间隔时间. */
    private Long intervalTime = 200L;

    /** 随机数生成器. */
    private Random random;

    public ClearExpireEntityListener init() {
        this.start = true;
        random = new Random();
        registerShutdownHook();
        initClearExpireEntityThread();
        log.info("初始化 ClearExpireEntityListener --->");
        return this;
    }

    /**
     * 初始化守护线程
     */
    private void initClearExpireEntityThread() {
        Thread thread = new Thread(this);
        thread.setName(CLEAR_EXPIRE_ENTITY_THREAD_NAME);
        thread.setDaemon(true);
        thread.setUncaughtExceptionHandler(this);
        thread.start();
    }

    @Override
    public void onApplicationEvent(TimeMapEvent event) {
        if (event instanceof InitTimeMapEvent) {
            initTimeMap((InitTimeMapEvent) event);
        } else if (event instanceof PutEntityEvent) {
            putExpireKey((PutEntityEvent) event);
        } else if (event instanceof RemoveEntityEvent) {
            removeExpireKey((RemoveEntityEvent) event);
        }
    }

    private void initTimeMap(InitTimeMapEvent event) {
        Object source = event.getSource();
        if (!(source instanceof ExpiringMap)) {
            throw new RuntimeException("InitTimeMapEvent source not is TimeMap");
        }
        this.expiringMap = (ExpiringMap) source;
    }

    private void removeExpireKey(RemoveEntityEvent event) {
        Object source = event.getSource();
        if (source instanceof ExpiringMap.TimeMapEntity) {
            ExpiringMap.TimeMapEntity timeMapEntity = (ExpiringMap.TimeMapEntity) source;
            if (timeMapEntity.getExpire() != null && !ExpiringMap.DEFAULT_EXPIRE.equals(timeMapEntity.getExpire())) {
                removeExpireKey(timeMapEntity.getKey());
            }
        }
    }

    private void removeExpireKey(Object key) {
        expireKeys.remove(key);
    }


    private void putExpireKey(PutEntityEvent event) {
        Object source = event.getSource();
        if (source instanceof ExpiringMap.TimeMapEntity) {
            ExpiringMap.TimeMapEntity timeMapEntity = (ExpiringMap.TimeMapEntity) source;
            Long expire = timeMapEntity.getExpire();
            if (expire != null && !ExpiringMap.DEFAULT_EXPIRE.equals(expire)) {
                expireKeys.add(timeMapEntity.getKey());
            }
        }
    }

    private void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::closed));
    }

    private void closed() {
        start = false;
        log.debug("清除过期key 关闭hook， 目前过期hook数据有 [{}]个", expireKeys.size());
    }

    @Override
    public void run() {
        while (expiringMap != null && start) {
            processExpireKeys();
            sleep(intervalTime);
        }
        // 如果说退出来了。则重新调用自己，重新启动
        log.debug("restart ClearExpireEntityThread.run()...");
        sleep(intervalTime);
        run();
    }

    private void processExpireKeys() {
        List<Object> readExpireKeys = getRandomExpireKeys(maxReadExpireKeys);
        log.debug("剩余过期key总数[{}] 监听过期key --> {} ", expireKeys.size(), readExpireKeys);
        System.out.println("剩余过期key总数["+ expireKeys.size()+"] 监听过期key --> "  + readExpireKeys);
        for (Object expireKey : readExpireKeys) {
            Object val = expiringMap.get(expireKey);
            if (Objects.isNull(val)) {
                // 说明已经超时
                removeExpireKey(expireKey);
            }
        }
    }

    private void sleep(Long intervalTime) {
        try {
            Thread.sleep(intervalTime);
        } catch (InterruptedException e) {
        }
    }

    private List<Object> getRandomExpireKeys(Integer maxReadExpireKeys) {
        int size = maxReadExpireKeys > expireKeys.size() ? expireKeys.size() : maxReadExpireKeys;
        if (size >= expireKeys.size()) {
            return expireKeys;
        }
        int randomIndex = random.nextInt(expireKeys.size() - size);
        ArrayList<Object> result = new ArrayList<>(size);
        for (int i = randomIndex; i < randomIndex + size; i++) {
            result.add(expireKeys.get(i));
        }
        return result;
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        if (CLEAR_EXPIRE_ENTITY_THREAD_NAME.equals(t.getName())) {
            log.warn("clear ExpireEntityListener error e= {}", e.getMessage());
            // 报错，重新初始化守护线程
            sleep(1000L);
            initClearExpireEntityThread();
        } else {
            e.printStackTrace();
        }
    }
}
