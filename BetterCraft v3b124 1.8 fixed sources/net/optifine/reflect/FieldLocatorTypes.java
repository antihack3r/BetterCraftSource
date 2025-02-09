/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.reflect;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import net.optifine.Log;
import net.optifine.reflect.IFieldLocator;

public class FieldLocatorTypes
implements IFieldLocator {
    private Field field = null;

    public FieldLocatorTypes(Class cls, Class[] preTypes, Class type, Class[] postTypes, String errorName) {
        Field[] afield = cls.getDeclaredFields();
        ArrayList list = new ArrayList();
        int i2 = 0;
        while (i2 < afield.length) {
            Field field = afield[i2];
            list.add(field.getType());
            ++i2;
        }
        ArrayList<Class> list1 = new ArrayList<Class>();
        list1.addAll(Arrays.asList(preTypes));
        list1.add(type);
        list1.addAll(Arrays.asList(postTypes));
        int l2 = Collections.indexOfSubList(list, list1);
        if (l2 < 0) {
            Log.log("(Reflector) Field not found: " + errorName);
        } else {
            int j2 = Collections.indexOfSubList(list.subList(l2 + 1, list.size()), list1);
            if (j2 >= 0) {
                Log.log("(Reflector) More than one match found for field: " + errorName);
            } else {
                int k2 = l2 + preTypes.length;
                this.field = afield[k2];
            }
        }
    }

    @Override
    public Field getField() {
        return this.field;
    }
}

