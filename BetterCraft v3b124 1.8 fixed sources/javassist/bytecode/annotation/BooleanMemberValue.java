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

public class BooleanMemberValue
extends MemberValue {
    int valueIndex;

    public BooleanMemberValue(int index, ConstPool cp2) {
        super('Z', cp2);
        this.valueIndex = index;
    }

    public BooleanMemberValue(boolean b2, ConstPool cp2) {
        super('Z', cp2);
        this.setValue(b2);
    }

    public BooleanMemberValue(ConstPool cp2) {
        super('Z', cp2);
        this.setValue(false);
    }

    @Override
    Object getValue(ClassLoader cl2, ClassPool cp2, Method m2) {
        return this.getValue();
    }

    @Override
    Class<?> getType(ClassLoader cl2) {
        return Boolean.TYPE;
    }

    public boolean getValue() {
        return this.cp.getIntegerInfo(this.valueIndex) != 0;
    }

    public void setValue(boolean newValue) {
        this.valueIndex = this.cp.addIntegerInfo(newValue ? 1 : 0);
    }

    public String toString() {
        return this.getValue() ? "true" : "false";
    }

    @Override
    public void write(AnnotationsWriter writer) throws IOException {
        writer.constValueIndex(this.getValue());
    }

    @Override
    public void accept(MemberValueVisitor visitor) {
        visitor.visitBooleanMemberValue(this);
    }
}

