// 
// Decompiled by Procyon v0.6.0
// 

package net.lenni0451.eventapi.listener;

import net.lenni0451.eventapi.events.IEvent;

public interface IEventListener
{
    void onEvent(final IEvent p0);
    
    default byte getPriority() {
        return 0;
    }
}
