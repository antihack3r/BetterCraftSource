// 
// Decompiled by Procyon v0.6.0
// 

package org.yaml.snakeyaml.events;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.error.Mark;

public final class SequenceStartEvent extends CollectionStartEvent
{
    public SequenceStartEvent(final String anchor, final String tag, final boolean implicit, final Mark startMark, final Mark endMark, final DumperOptions.FlowStyle flowStyle) {
        super(anchor, tag, implicit, startMark, endMark, flowStyle);
    }
    
    @Override
    public ID getEventId() {
        return ID.SequenceStart;
    }
}
