/*
 * Decompiled with CFR 0.152.
 */
package wdl.api;

import java.util.Map;
import wdl.api.IWDLMessageType;
import wdl.api.IWDLMod;

public interface IMessageTypeAdder
extends IWDLMod {
    public Map<String, IWDLMessageType> getMessageTypes();
}

