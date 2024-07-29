/*
 * Decompiled with CFR 0.152.
 */
package javassist.bytecode.annotation;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import javassist.ClassPool;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.AnnotationsWriter;
import javassist.bytecode.annotation.MemberValue;
import javassist.bytecode.annotation.MemberValueVisitor;

public class ArrayMemberValue
extends MemberValue {
    MemberValue type;
    MemberValue[] values;

    public ArrayMemberValue(ConstPool cp2) {
        super('[', cp2);
        this.type = null;
        this.values = null;
    }

    public ArrayMemberValue(MemberValue t2, ConstPool cp2) {
        super('[', cp2);
        this.type = t2;
        this.values = null;
    }

    @Override
    Object getValue(ClassLoader cl2, ClassPool cp2, Method method) throws ClassNotFoundException {
        Class<?> clazz;
        if (this.values == null) {
            throw new ClassNotFoundException("no array elements found: " + method.getName());
        }
        int size = this.values.length;
        if (this.type == null) {
            clazz = method.getReturnType().getComponentType();
            if (clazz == null || size > 0) {
                throw new ClassNotFoundException("broken array type: " + method.getName());
            }
        } else {
            clazz = this.type.getType(cl2);
        }
        Object a2 = Array.newInstance(clazz, size);
        for (int i2 = 0; i2 < size; ++i2) {
            Array.set(a2, i2, this.values[i2].getValue(cl2, cp2, method));
        }
        return a2;
    }

    @Override
    Class<?> getType(ClassLoader cl2) throws ClassNotFoundException {
        if (this.type == null) {
            throw new ClassNotFoundException("no array type specified");
        }
        Object a2 = Array.newInstance(this.type.getType(cl2), 0);
        return a2.getClass();
    }

    public MemberValue getType() {
        return this.type;
    }

    public MemberValue[] getValue() {
        return this.values;
    }

    public void setValue(MemberValue[] elements) {
        this.values = elements;
        if (elements != null && elements.length > 0) {
            this.type = elements[0];
        }
    }

    public String toString() {
        StringBuffer buf = new StringBuffer("{");
        if (this.values != null) {
            for (int i2 = 0; i2 < this.values.length; ++i2) {
                buf.append(this.values[i2].toString());
                if (i2 + 1 >= this.values.length) continue;
                buf.append(", ");
            }
        }
        buf.append("}");
        return buf.toString();
    }

    @Override
    public void write(AnnotationsWriter writer) throws IOException {
        int num = this.values == null ? 0 : this.values.length;
        writer.arrayValue(num);
        for (int i2 = 0; i2 < num; ++i2) {
            this.values[i2].write(writer);
        }
    }

    @Override
    public void accept(MemberValueVisitor visitor) {
        visitor.visitArrayMemberValue(this);
    }
}

