/*
 * Decompiled with CFR 0.152.
 */
package org.slf4j.helpers;

import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.slf4j.helpers.ThreadLocalMapOfStacks;
import org.slf4j.spi.MDCAdapter;

public class BasicMDCAdapter
implements MDCAdapter {
    private final ThreadLocalMapOfStacks threadLocalMapOfDeques = new ThreadLocalMapOfStacks();
    private final InheritableThreadLocal<Map<String, String>> inheritableThreadLocalMap = new InheritableThreadLocal<Map<String, String>>(){

        @Override
        protected Map<String, String> childValue(Map<String, String> parentValue) {
            if (parentValue == null) {
                return null;
            }
            return new HashMap<String, String>(parentValue);
        }
    };

    @Override
    public void put(String key, String val) {
        if (key == null) {
            throw new IllegalArgumentException("key cannot be null");
        }
        HashMap<String, String> map = (HashMap<String, String>)this.inheritableThreadLocalMap.get();
        if (map == null) {
            map = new HashMap<String, String>();
            this.inheritableThreadLocalMap.set(map);
        }
        map.put(key, val);
    }

    @Override
    public String get(String key) {
        Map map = (Map)this.inheritableThreadLocalMap.get();
        if (map != null && key != null) {
            return (String)map.get(key);
        }
        return null;
    }

    @Override
    public void remove(String key) {
        Map map = (Map)this.inheritableThreadLocalMap.get();
        if (map != null) {
            map.remove(key);
        }
    }

    @Override
    public void clear() {
        Map map = (Map)this.inheritableThreadLocalMap.get();
        if (map != null) {
            map.clear();
            this.inheritableThreadLocalMap.remove();
        }
    }

    public Set<String> getKeys() {
        Map map = (Map)this.inheritableThreadLocalMap.get();
        if (map != null) {
            return map.keySet();
        }
        return null;
    }

    @Override
    public Map<String, String> getCopyOfContextMap() {
        Map oldMap = (Map)this.inheritableThreadLocalMap.get();
        if (oldMap != null) {
            return new HashMap<String, String>(oldMap);
        }
        return null;
    }

    @Override
    public void setContextMap(Map<String, String> contextMap) {
        HashMap<String, String> copy = null;
        if (contextMap != null) {
            copy = new HashMap<String, String>(contextMap);
        }
        this.inheritableThreadLocalMap.set(copy);
    }

    @Override
    public void pushByKey(String key, String value) {
        this.threadLocalMapOfDeques.pushByKey(key, value);
    }

    @Override
    public String popByKey(String key) {
        return this.threadLocalMapOfDeques.popByKey(key);
    }

    @Override
    public Deque<String> getCopyOfDequeByKey(String key) {
        return this.threadLocalMapOfDeques.getCopyOfDequeByKey(key);
    }

    @Override
    public void clearDequeByKey(String key) {
        this.threadLocalMapOfDeques.clearDequeByKey(key);
    }
}

