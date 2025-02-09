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

public class ShortMemberValue
extends MemberValue {
    int valueIndex;

    public ShortMemberValue(int index, ConstPool cp2) {
        super('S', cp2);
        this.valueIndex = index;
    }

    public ShortMemberValue(short s2, ConstPool cp2) {
        super('S', cp2);
        this.setValue(s2);
    }

    public ShortMemberValue(ConstPool cp2) {
        super('S', cp2);
        this.setValue((short)0);
    }

    @Override
    Object getValue(ClassLoader cl2, ClassPool cp2, Method m2) {
        return this.getValue();
    }

    @Override
    Class<?> getType(ClassLoader cl2) {
        return Short.TYPE;
    }

    public short getValue() {
        return (short)this.cp.getIntegerInfo(this.valueIndex);
    }

    public void setValue(short newValue) {
        this.valueIndex = this.cp.addIntegerInfo(newValue);
    }

    public String toString() {
        return Short.toString(this.getValue());
    }

    @Override
    public void write(AnnotationsWriter writer) throws IOException {
        writer.constValueIndex(this.getValue());
    }

    @Override
    public void accept(MemberValueVisitor visitor) {
        visitor.visitShortMemberValue(this);
    }
}

