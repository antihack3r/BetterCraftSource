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

public class CharMemberValue
extends MemberValue {
    int valueIndex;

    public CharMemberValue(int index, ConstPool cp2) {
        super('C', cp2);
        this.valueIndex = index;
    }

    public CharMemberValue(char c2, ConstPool cp2) {
        super('C', cp2);
        this.setValue(c2);
    }

    public CharMemberValue(ConstPool cp2) {
        super('C', cp2);
        this.setValue('\u0000');
    }

    @Override
    Object getValue(ClassLoader cl2, ClassPool cp2, Method m2) {
        return Character.valueOf(this.getValue());
    }

    @Override
    Class<?> getType(ClassLoader cl2) {
        return Character.TYPE;
    }

    public char getValue() {
        return (char)this.cp.getIntegerInfo(this.valueIndex);
    }

    public void setValue(char newValue) {
        this.valueIndex = this.cp.addIntegerInfo(newValue);
    }

    public String toString() {
        return Character.toString(this.getValue());
    }

    @Override
    public void write(AnnotationsWriter writer) throws IOException {
        writer.constValueIndex(this.getValue());
    }

    @Override
    public void accept(MemberValueVisitor visitor) {
        visitor.visitCharMemberValue(this);
    }
}

