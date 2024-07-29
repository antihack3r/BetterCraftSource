/*
 * Decompiled with CFR 0.152.
 */
package org.spongepowered.asm.mixin.injection.code;

import java.util.HashMap;
import java.util.Map;
import org.objectweb.asm.tree.AnnotationNode;
import org.spongepowered.asm.mixin.injection.code.ISliceContext;
import org.spongepowered.asm.mixin.injection.code.MethodSlice;
import org.spongepowered.asm.mixin.injection.struct.InjectionInfo;
import org.spongepowered.asm.mixin.injection.throwables.InvalidSliceException;
import org.spongepowered.asm.util.Annotations;

public final class MethodSlices {
    private final InjectionInfo info;
    private final Map<String, MethodSlice> slices = new HashMap<String, MethodSlice>(4);

    private MethodSlices(InjectionInfo info) {
        this.info = info;
    }

    private void add(MethodSlice slice) {
        String id2 = this.info.getSliceId(slice.getId());
        if (this.slices.containsKey(id2)) {
            throw new InvalidSliceException((ISliceContext)this.info, slice + " has a duplicate id, '" + id2 + "' was already defined");
        }
        this.slices.put(id2, slice);
    }

    public MethodSlice get(String id2) {
        return this.slices.get(id2);
    }

    public String toString() {
        return String.format("MethodSlices%s", this.slices.keySet());
    }

    public static MethodSlices parse(InjectionInfo info) {
        MethodSlices slices = new MethodSlices(info);
        AnnotationNode annotation = info.getAnnotationNode();
        if (annotation != null) {
            for (AnnotationNode node : Annotations.getValue(annotation, "slice", true)) {
                MethodSlice slice = MethodSlice.parse((ISliceContext)info, node);
                slices.add(slice);
            }
        }
        return slices;
    }
}

