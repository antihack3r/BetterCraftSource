/*
 * Decompiled with CFR 0.152.
 */
package org.spongepowered.asm.launch.platform.container;

import java.util.Collection;

public interface IContainerHandle {
    public String getAttribute(String var1);

    public Collection<IContainerHandle> getNestedContainers();
}

