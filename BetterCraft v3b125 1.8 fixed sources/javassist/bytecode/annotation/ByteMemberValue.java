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

public class ByteMemberValue
extends MemberValue {
    int valueIndex;

    public ByteMemberValue(int index, ConstPool cp2) {
        super('B', cp2);
        this.valueIndex = index;
    }

    public ByteMemberValue(byte b2, ConstPool cp2) {
        super('B', cp2);
        this.setValue(b2);
    }

    public ByteMemberValue(ConstPool cp2) {
        super('B', cp2);
        this.setValue((byte)0);
    }

    @Override
    Object getValue(ClassLoader cl2, ClassPool cp2, Method m2) {
        return this.getValue();
    }

    @Override
    Class<?> getType(ClassLoader cl2) {
        return Byte.TYPE;
    }

    public byte getValue() {
        return (byte)this.cp.getIntegerInfo(this.valueIndex);
    }

    public void setValue(byte newValue) {
        this.valueIndex = this.cp.addIntegerInfo(newValue);
    }

    public String toString() {
        return Byte.toString(this.getValue());
    }

    @Override
    public void write(AnnotationsWriter writer) throws IOException {
        writer.constValueIndex(this.getValue());
    }

    @Override
    public void accept(MemberValueVisitor visitor) {
        visitor.visitByteMemberValue(this);
    }
}

