/*
 * Decompiled with CFR 0.152.
 */
package javassist.bytecode.annotation;

import java.io.IOException;
import java.lang.reflect.Method;
import javassist.ClassPool;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.AnnotationsWriter;
import javassist.bytecode.annotation.MemberValue;
import javassist.bytecode.annotation.MemberValueVisitor;

public class FloatMemberValue
extends MemberValue {
    int valueIndex;

    public FloatMemberValue(int index, ConstPool cp2) {
        super('F', cp2);
        this.valueIndex = index;
    }

    public FloatMemberValue(float f2, ConstPool cp2) {
        super('F', cp2);
        this.setValue(f2);
    }

    public FloatMemberValue(ConstPool cp2) {
        super('F', cp2);
        this.setValue(0.0f);
    }

    @Override
    Object getValue(ClassLoader cl2, ClassPool cp2, Method m2) {
        return Float.valueOf(this.getValue());
    }

    @Override
    Class<?> getType(ClassLoader cl2) {
        return Float.TYPE;
    }

    public float getValue() {
        return this.cp.getFloatInfo(this.valueIndex);
    }

    public void setValue(float newValue) {
        this.valueIndex = this.cp.addFloatInfo(newValue);
    }

    public String toString() {
        return Float.toString(this.getValue());
    }

    @Override
    public void write(AnnotationsWriter writer) throws IOException {
        writer.constValueIndex(this.getValue());
    }

    @Override
    public void accept(MemberValueVisitor visitor) {
        visitor.visitFloatMemberValue(this);
    }
}

