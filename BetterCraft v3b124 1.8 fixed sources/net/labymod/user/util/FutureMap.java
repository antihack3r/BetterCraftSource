/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.user.util;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.function.Consumer;

public class FutureMap<K, V> {
    private static ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(0, new ThreadFactoryBuilder().setNameFormat("LabyMod Futuremap Resolver #%d").setDaemon(true).build());
    private Map<K, SettableFuture<V>> futureMap = new ConcurrentHashMap<K, SettableFuture<V>>();
    private Map<K, Long> startLoadTimesMap = new ConcurrentHashMap<K, Long>();
    private Consumer<K> loadCallback;
    private long timeout;
    private V defaultValue;

    public FutureMap(Consumer<K> loadCallback, long timeout, V defaultValue) {
        this.loadCallback = loadCallback;
        this.timeout = timeout;
        this.defaultValue = defaultValue;
    }

    public ListenableFuture<V> get(K key) {
        if (this.futureMap.containsKey(key)) {
            return this.futureMap.get(key);
        }
        SettableFuture valueFuture = SettableFuture.create();
        this.futureMap.put(key, valueFuture);
        this.startLoadTimesMap.put(key, System.currentTimeMillis());
        this.loadCallback.accept(key);
        return valueFuture;
    }

    public void resolve(K key, V value) {
        SettableFuture<V> valueFuture = this.futureMap.remove(key);
        if (valueFuture != null) {
            valueFuture.set(value);
            this.startLoadTimesMap.remove(key);
        }
    }
}

