// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.common.model;

import java.util.Optional;

public interface IModelState
{
    Optional<TRSRTransformation> apply(final Optional<? extends IModelPart> p0);
}
