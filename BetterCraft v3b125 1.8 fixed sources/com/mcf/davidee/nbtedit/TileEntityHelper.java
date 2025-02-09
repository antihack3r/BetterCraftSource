/*
 * Decompiled with CFR 0.152.
 */
package com.mcf.davidee.nbtedit;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.tileentity.TileEntity;

public class TileEntityHelper {
    public static <T extends TileEntity> void copyData(T from, T into) throws Exception {
        Class<?> clazz = from.getClass();
        Set<Field> fields = TileEntityHelper.asSet(clazz.getFields(), clazz.getDeclaredFields());
        Field modifiers = Field.class.getDeclaredField("modifiers");
        modifiers.setAccessible(true);
        for (Field field : fields) {
            field.setAccessible(true);
            modifiers.setInt(field, field.getModifiers() & 0xFFFFFFEF);
            field.set(into, field.get(from));
        }
    }

    public static Set<Field> asSet(Field[] a2, Field[] b2) {
        HashSet<Field> s2 = new HashSet<Field>();
        Collections.addAll(s2, a2);
        Collections.addAll(s2, b2);
        return s2;
    }
}

