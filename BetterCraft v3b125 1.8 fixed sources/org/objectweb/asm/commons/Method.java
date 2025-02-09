/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm.commons;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import org.objectweb.asm.Type;

public class Method {
    private final String name;
    private final String desc;
    private static final Map DESCRIPTORS;

    public Method(String string, String string2) {
        this.name = string;
        this.desc = string2;
    }

    public Method(String string, Type type, Type[] typeArray) {
        this(string, Type.getMethodDescriptor(type, typeArray));
    }

    public static Method getMethod(java.lang.reflect.Method method) {
        return new Method(method.getName(), Type.getMethodDescriptor(method));
    }

    public static Method getMethod(Constructor constructor) {
        return new Method("<init>", Type.getConstructorDescriptor(constructor));
    }

    public static Method getMethod(String string) throws IllegalArgumentException {
        return Method.getMethod(string, false);
    }

    public static Method getMethod(String string, boolean bl2) throws IllegalArgumentException {
        int n2;
        int n3 = string.indexOf(32);
        int n4 = string.indexOf(40, n3) + 1;
        int n5 = string.indexOf(41, n4);
        if (n3 == -1 || n4 == -1 || n5 == -1) {
            throw new IllegalArgumentException();
        }
        String string2 = string.substring(0, n3);
        String string3 = string.substring(n3 + 1, n4 - 1).trim();
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append('(');
        do {
            String string4;
            if ((n2 = string.indexOf(44, n4)) == -1) {
                string4 = Method.map(string.substring(n4, n5).trim(), bl2);
            } else {
                string4 = Method.map(string.substring(n4, n2).trim(), bl2);
                n4 = n2 + 1;
            }
            stringBuffer.append(string4);
        } while (n2 != -1);
        stringBuffer.append(')');
        stringBuffer.append(Method.map(string2, bl2));
        return new Method(string3, stringBuffer.toString());
    }

    private static String map(String string, boolean bl2) {
        if ("".equals(string)) {
            return string;
        }
        StringBuffer stringBuffer = new StringBuffer();
        int n2 = 0;
        while ((n2 = string.indexOf("[]", n2) + 1) > 0) {
            stringBuffer.append('[');
        }
        String string2 = string.substring(0, string.length() - stringBuffer.length() * 2);
        String string3 = (String)DESCRIPTORS.get(string2);
        if (string3 != null) {
            stringBuffer.append(string3);
        } else {
            stringBuffer.append('L');
            if (string2.indexOf(46) < 0) {
                if (!bl2) {
                    stringBuffer.append("java/lang/");
                }
                stringBuffer.append(string2);
            } else {
                stringBuffer.append(string2.replace('.', '/'));
            }
            stringBuffer.append(';');
        }
        return stringBuffer.toString();
    }

    public String getName() {
        return this.name;
    }

    public String getDescriptor() {
        return this.desc;
    }

    public Type getReturnType() {
        return Type.getReturnType(this.desc);
    }

    public Type[] getArgumentTypes() {
        return Type.getArgumentTypes(this.desc);
    }

    public String toString() {
        return this.name + this.desc;
    }

    public boolean equals(Object object) {
        if (!(object instanceof Method)) {
            return false;
        }
        Method method = (Method)object;
        return this.name.equals(method.name) && this.desc.equals(method.desc);
    }

    public int hashCode() {
        return this.name.hashCode() ^ this.desc.hashCode();
    }

    static {
        Method._clinit_();
        DESCRIPTORS = new HashMap();
        DESCRIPTORS.put("void", "V");
        DESCRIPTORS.put("byte", "B");
        DESCRIPTORS.put("char", "C");
        DESCRIPTORS.put("double", "D");
        DESCRIPTORS.put("float", "F");
        DESCRIPTORS.put("int", "I");
        DESCRIPTORS.put("long", "J");
        DESCRIPTORS.put("short", "S");
        DESCRIPTORS.put("boolean", "Z");
    }

    static /* synthetic */ void _clinit_() {
    }
}

