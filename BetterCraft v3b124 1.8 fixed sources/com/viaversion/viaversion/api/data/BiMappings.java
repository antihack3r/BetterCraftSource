/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.api.data;

import com.viaversion.viaversion.api.data.BiMappingsBase;
import com.viaversion.viaversion.api.data.Mappings;

public interface BiMappings
extends Mappings {
    @Override
    public BiMappings inverse();

    public static BiMappings of(Mappings mappings) {
        return BiMappings.of(mappings, mappings.inverse());
    }

    public static BiMappings of(Mappings mappings, Mappings inverse) {
        return new BiMappingsBase(mappings, inverse);
    }
}

