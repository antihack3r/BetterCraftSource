// 
// Decompiled by Procyon v0.6.0
// 

package com.viaversion.viaversion.api.type.types.version;

import com.viaversion.viaversion.api.type.types.minecraft.MetaListType;
import com.viaversion.viaversion.api.minecraft.metadata.types.MetaTypes;
import java.util.List;
import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.minecraft.metadata.types.MetaTypes1_19;
import com.viaversion.viaversion.api.type.types.minecraft.ParticleType;

public final class Types1_19
{
    public static final ParticleType PARTICLE;
    public static final MetaTypes1_19 META_TYPES;
    public static final Type<Metadata> METADATA;
    public static final Type<List<Metadata>> METADATA_LIST;
    
    static {
        PARTICLE = new ParticleType();
        META_TYPES = new MetaTypes1_19(Types1_19.PARTICLE);
        METADATA = new MetadataType(Types1_19.META_TYPES);
        METADATA_LIST = new MetaListType(Types1_19.METADATA);
    }
}
