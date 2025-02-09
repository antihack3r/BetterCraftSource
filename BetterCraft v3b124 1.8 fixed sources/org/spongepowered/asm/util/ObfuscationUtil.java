/*
 * Decompiled with CFR 0.152.
 */
package org.spongepowered.asm.util;

public abstract class ObfuscationUtil {
    private ObfuscationUtil() {
    }

    public static String mapDescriptor(String desc, IClassRemapper remapper) {
        return ObfuscationUtil.remapDescriptor(desc, remapper, false);
    }

    public static String unmapDescriptor(String desc, IClassRemapper remapper) {
        return ObfuscationUtil.remapDescriptor(desc, remapper, true);
    }

    private static String remapDescriptor(String desc, IClassRemapper remapper, boolean unmap) {
        StringBuilder sb2 = new StringBuilder();
        StringBuilder token = null;
        for (int pos = 0; pos < desc.length(); ++pos) {
            char c2 = desc.charAt(pos);
            if (token != null) {
                if (c2 == ';') {
                    sb2.append('L').append(ObfuscationUtil.remap(token.toString(), remapper, unmap)).append(';');
                    token = null;
                    continue;
                }
                token.append(c2);
                continue;
            }
            if (c2 == 'L') {
                token = new StringBuilder();
                continue;
            }
            sb2.append(c2);
        }
        if (token != null) {
            throw new IllegalArgumentException("Invalid descriptor '" + desc + "', missing ';'");
        }
        return sb2.toString();
    }

    private static Object remap(String typeName, IClassRemapper remapper, boolean unmap) {
        String result = unmap ? remapper.unmap(typeName) : remapper.map(typeName);
        return result != null ? result : typeName;
    }

    public static interface IClassRemapper {
        public String map(String var1);

        public String unmap(String var1);
    }
}

