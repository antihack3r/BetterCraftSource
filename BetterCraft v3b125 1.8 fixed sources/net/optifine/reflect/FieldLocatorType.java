/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.reflect;

import java.lang.reflect.Field;
import net.optifine.Log;
import net.optifine.reflect.IFieldLocator;
import net.optifine.reflect.ReflectorClass;

public class FieldLocatorType
implements IFieldLocator {
    private ReflectorClass reflectorClass = null;
    private Class targetFieldType = null;
    private int targetFieldIndex;

    public FieldLocatorType(ReflectorClass reflectorClass, Class targetFieldType) {
        this(reflectorClass, targetFieldType, 0);
    }

    public FieldLocatorType(ReflectorClass reflectorClass, Class targetFieldType, int targetFieldIndex) {
        this.reflectorClass = reflectorClass;
        this.targetFieldType = targetFieldType;
        this.targetFieldIndex = targetFieldIndex;
    }

    @Override
    public Field getField() {
        Class oclass = this.reflectorClass.getTargetClass();
        if (oclass == null) {
            return null;
        }
        try {
            Field[] afield = oclass.getDeclaredFields();
            int i2 = 0;
            int j2 = 0;
            while (j2 < afield.length) {
                Field field = afield[j2];
                if (field.getType() == this.targetFieldType) {
                    if (i2 == this.targetFieldIndex) {
                        field.setAccessible(true);
                        return field;
                    }
                    ++i2;
                }
                ++j2;
            }
            Log.log("(Reflector) Field not present: " + oclass.getName() + ".(type: " + this.targetFieldType + ", index: " + this.targetFieldIndex + ")");
            return null;
        }
        catch (SecurityException securityexception) {
            securityexception.printStackTrace();
            return null;
        }
        catch (Throwable throwable) {
            throwable.printStackTrace();
            return null;
        }
    }
}

