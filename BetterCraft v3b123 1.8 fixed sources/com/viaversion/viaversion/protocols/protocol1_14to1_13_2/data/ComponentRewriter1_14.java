// 
// Decompiled by Procyon v0.6.0
// 

package com.viaversion.viaversion.protocols.protocol1_14to1_13_2.data;

import com.viaversion.viaversion.libs.gson.JsonObject;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.data.ComponentRewriter1_13;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;

public class ComponentRewriter1_14<C extends ClientboundPacketType> extends ComponentRewriter1_13<C>
{
    public ComponentRewriter1_14(final Protocol<C, ?, ?, ?> protocol) {
        super(protocol);
    }
    
    @Override
    protected void handleTranslate(final JsonObject object, final String translate) {
    }
}
