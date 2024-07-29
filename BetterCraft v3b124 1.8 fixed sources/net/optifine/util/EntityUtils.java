/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.util;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.src.Config;

public class EntityUtils {
    private static final Map<Class, Integer> mapIdByClass = new HashMap<Class, Integer>();
    private static final Map<String, Integer> mapIdByName = new HashMap<String, Integer>();
    private static final Map<String, Class> mapClassByName = new HashMap<String, Class>();

    static {
        int i2 = 0;
        while (i2 < 1000) {
            String s2;
            Class<? extends Entity> oclass = EntityList.getClassFromID(i2);
            if (oclass != null && (s2 = EntityList.getStringFromID(i2)) != null) {
                if (mapIdByClass.containsKey(oclass)) {
                    Config.warn("Duplicate entity class: " + oclass + ", id1: " + mapIdByClass.get(oclass) + ", id2: " + i2);
                }
                if (mapIdByName.containsKey(s2)) {
                    Config.warn("Duplicate entity name: " + s2 + ", id1: " + mapIdByName.get(s2) + ", id2: " + i2);
                }
                if (mapClassByName.containsKey(s2)) {
                    Config.warn("Duplicate entity name: " + s2 + ", class1: " + mapClassByName.get(s2) + ", class2: " + oclass);
                }
                mapIdByClass.put(oclass, i2);
                mapIdByName.put(s2, i2);
                mapClassByName.put(s2, oclass);
            }
            ++i2;
        }
    }

    public static int getEntityIdByClass(Entity entity) {
        return entity == null ? -1 : EntityUtils.getEntityIdByClass(entity.getClass());
    }

    public static int getEntityIdByClass(Class cls) {
        Integer integer = mapIdByClass.get(cls);
        return integer == null ? -1 : integer;
    }

    public static int getEntityIdByName(String name) {
        Integer integer = mapIdByName.get(name);
        return integer == null ? -1 : integer;
    }

    public static Class getEntityClassByName(String name) {
        Class oclass = mapClassByName.get(name);
        return oclass;
    }
}

