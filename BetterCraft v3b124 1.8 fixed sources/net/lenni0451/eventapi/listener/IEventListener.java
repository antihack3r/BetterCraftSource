/*
 * Decompiled with CFR 0.152.
 */
package net.lenni0451.eventapi.listener;

import net.lenni0451.eventapi.events.IEvent;

public interface IEventListener {
    public void onEvent(IEvent var1);

    default public byte getPriority() {
        return 0;
    }
}
