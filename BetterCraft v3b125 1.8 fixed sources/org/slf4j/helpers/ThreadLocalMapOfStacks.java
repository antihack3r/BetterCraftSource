/*
 * Decompiled with CFR 0.152.
 */
package org.slf4j.helpers;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

public class ThreadLocalMapOfStacks {
    final ThreadLocal<Map<String, Deque<String>>> tlMapOfStacks = new ThreadLocal();

    public void pushByKey(String key, String value) {
        Deque<String> deque;
        if (key == null) {
            return;
        }
        Map<String, Deque<String>> map = this.tlMapOfStacks.get();
        if (map == null) {
            map = new HashMap<String, Deque<String>>();
            this.tlMapOfStacks.set(map);
        }
        if ((deque = map.get(key)) == null) {
            deque = new ArrayDeque<String>();
        }
        deque.push(value);
        map.put(key, deque);
    }

    public String popByKey(String key) {
        if (key == null) {
            return null;
        }
        Map<String, Deque<String>> map = this.tlMapOfStacks.get();
        if (map == null) {
            return null;
        }
        Deque<String> deque = map.get(key);
        if (deque == null) {
            return null;
        }
        return deque.pop();
    }

    public Deque<String> getCopyOfDequeByKey(String key) {
        if (key == null) {
            return null;
        }
        Map<String, Deque<String>> map = this.tlMapOfStacks.get();
        if (map == null) {
            return null;
        }
        Deque<String> deque = map.get(key);
        if (deque == null) {
            return null;
        }
        return new ArrayDeque<String>(deque);
    }

    public void clearDequeByKey(String key) {
        if (key == null) {
            return;
        }
        Map<String, Deque<String>> map = this.tlMapOfStacks.get();
        if (map == null) {
            return;
        }
        Deque<String> deque = map.get(key);
        if (deque == null) {
            return;
        }
        deque.clear();
    }
}

