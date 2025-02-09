/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.reflect;

import net.optifine.reflect.ReflectorClass;
import net.optifine.reflect.ReflectorField;

public class ReflectorFields {
    private ReflectorClass reflectorClass;
    private Class fieldType;
    private int fieldCount;
    private ReflectorField[] reflectorFields;

    public ReflectorFields(ReflectorClass reflectorClass, Class fieldType, int fieldCount) {
        this.reflectorClass = reflectorClass;
        this.fieldType = fieldType;
        if (reflectorClass.exists() && fieldType != null) {
            this.reflectorFields = new ReflectorField[fieldCount];
            int i2 = 0;
            while (i2 < this.reflectorFields.length) {
                this.reflectorFields[i2] = new ReflectorField(reflectorClass, fieldType, i2);
                ++i2;
            }
        }
    }

    public ReflectorClass getReflectorClass() {
        return this.reflectorClass;
    }

    public Class getFieldType() {
        return this.fieldType;
    }

    public int getFieldCount() {
        return this.fieldCount;
    }

    public ReflectorField getReflectorField(int index) {
        return index >= 0 && index < this.reflectorFields.length ? this.reflectorFields[index] : null;
    }
}

