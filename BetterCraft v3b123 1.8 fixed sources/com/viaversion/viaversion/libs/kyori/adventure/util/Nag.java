// 
// Decompiled by Procyon v0.6.0
// 

package com.viaversion.viaversion.libs.kyori.adventure.util;

import org.jetbrains.annotations.NotNull;

public abstract class Nag extends RuntimeException
{
    private static final long serialVersionUID = -695562541413409498L;
    
    public static void print(@NotNull final Nag nag) {
        nag.printStackTrace();
    }
    
    protected Nag(final String message) {
        super(message);
    }
}
