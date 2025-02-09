/*
 * Decompiled with CFR 0.152.
 */
package javassist.bytecode.annotation;

import java.io.IOException;
import java.lang.reflect.Method;
import javassist.ClassPool;
import javassist.bytecode.ConstPool;
import javassist.bytecode.Descriptor;
import javassist.bytecode.annotation.AnnotationsWriter;
import javassist.bytecode.annotation.MemberValueVisitor;
import javassist.bytecode.annotation.NoSuchClassError;

public abstract class MemberValue {
    ConstPool cp;
    char tag;

    MemberValue(char tag, ConstPool cp2) {
        this.cp = cp2;
        this.tag = tag;
    }

    abstract Object getValue(ClassLoader var1, ClassPool var2, Method var3) throws ClassNotFoundException;

    abstract Class<?> getType(ClassLoader var1) throws ClassNotFoundException;

    static Class<?> loadClass(ClassLoader cl2, String classname) throws ClassNotFoundException, NoSuchClassError {
        try {
            return Class.forName(MemberValue.convertFromArray(classname), true, cl2);
        }
        catch (LinkageError e2) {
            throw new NoSuchClassError(classname, e2);
        }
    }

    private static String convertFromArray(String classname) {
        int index = classname.indexOf("[]");
        if (index != -1) {
            String rawType = classname.substring(0, index);
            StringBuffer sb2 = new StringBuffer(Descriptor.of(rawType));
            while (index != -1) {
                sb2.insert(0, "[");
                index = classname.indexOf("[]", index + 1);
            }
            return sb2.toString().replace('/', '.');
        }
        return classname;
    }

    public abstract void accept(MemberValueVisitor var1);

    public abstract void write(AnnotationsWriter var1) throws IOException;
}

