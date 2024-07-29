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

public class IntegerMemberValue
extends MemberValue {
    int valueIndex;

    public IntegerMemberValue(int index, ConstPool cp2) {
        super('I', cp2);
        this.valueIndex = index;
    }

    public IntegerMemberValue(ConstPool cp2, int value) {
        super('I', cp2);
        this.setValue(value);
    }

    public IntegerMemberValue(ConstPool cp2) {
        super('I', cp2);
        this.setValue(0);
    }

    @Override
    Object getValue(ClassLoader cl2, ClassPool cp2, Method m2) {
        return this.getValue();
    }

    @Override
    Class<?> getType(ClassLoader cl2) {
        return Integer.TYPE;
    }

    public int getValue() {
        return this.cp.getIntegerInfo(this.valueIndex);
    }

    public void setValue(int newValue) {
        this.valueIndex = this.cp.addIntegerInfo(newValue);
    }

    public String toString() {
        return Integer.toString(this.getValue());
    }

    @Override
    public void write(AnnotationsWriter writer) throws IOException {
        writer.constValueIndex(this.getValue());
    }

    @Override
    public void accept(MemberValueVisitor visitor) {
        visitor.visitIntegerMemberValue(this);
    }
}

