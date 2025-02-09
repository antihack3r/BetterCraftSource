/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.reflect;

import java.lang.reflect.Field;
import net.optifine.Log;
import net.optifine.reflect.IFieldLocator;
import net.optifine.reflect.ReflectorClass;

public class FieldLocatorName
implements IFieldLocator {
    private ReflectorClass reflectorClass = null;
    private String targetFieldName = null;

    public FieldLocatorName(ReflectorClass reflectorClass, String targetFieldName) {
        this.reflectorClass = reflectorClass;
        this.targetFieldName = targetFieldName;
    }

    @Override
    public Field getField() {
        Class oclass = this.reflectorClass.getTargetClass();
        if (oclass == null) {
            return null;
        }
        try {
            Field field = this.getDeclaredField(oclass, this.targetFieldName);
            field.setAccessible(true);
            return field;
        }
        catch (NoSuchFieldException var3) {
            Log.log("(Reflector) Field not present: " + oclass.getName() + "." + this.targetFieldName);
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

    private Field getDeclaredField(Class cls, String name) throws NoSuchFieldException {
        Field[] afield = cls.getDeclaredFields();
        int i2 = 0;
        while (i2 < afield.length) {
            Field field = afield[i2];
            if (field.getName().equals(name)) {
                return field;
            }
            ++i2;
        }
        if (cls == Object.class) {
            throw new NoSuchFieldException(name);
        }
        return this.getDeclaredField(cls.getSuperclass(), name);
    }
}

