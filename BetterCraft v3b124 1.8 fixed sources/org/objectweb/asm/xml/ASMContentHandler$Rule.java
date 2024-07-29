/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm.xml;

import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.xml.ASMContentHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public abstract class ASMContentHandler$Rule {
    final /* synthetic */ ASMContentHandler this$0;
    static /* synthetic */ Class class$org$objectweb$asm$Type;
    static /* synthetic */ Class class$org$objectweb$asm$Handle;

    protected ASMContentHandler$Rule(ASMContentHandler aSMContentHandler) {
        this.this$0 = aSMContentHandler;
    }

    public void begin(String string, Attributes attributes) throws SAXException {
    }

    public void end(String string) {
    }

    protected final Object getValue(String string, String string2) throws SAXException {
        Object object = null;
        if (string2 != null) {
            if ("Ljava/lang/String;".equals(string)) {
                object = this.decode(string2);
            } else if ("Ljava/lang/Integer;".equals(string) || "I".equals(string) || "S".equals(string) || "B".equals(string) || "C".equals(string) || "Z".equals(string)) {
                object = new Integer(string2);
            } else if ("Ljava/lang/Short;".equals(string)) {
                object = new Short(string2);
            } else if ("Ljava/lang/Byte;".equals(string)) {
                object = new Byte(string2);
            } else if ("Ljava/lang/Character;".equals(string)) {
                object = new Character(this.decode(string2).charAt(0));
            } else if ("Ljava/lang/Boolean;".equals(string)) {
                object = Boolean.valueOf(string2);
            } else if ("Ljava/lang/Long;".equals(string) || "J".equals(string)) {
                object = new Long(string2);
            } else if ("Ljava/lang/Float;".equals(string) || "F".equals(string)) {
                object = new Float(string2);
            } else if ("Ljava/lang/Double;".equals(string) || "D".equals(string)) {
                object = new Double(string2);
            } else if (Type.getDescriptor(class$org$objectweb$asm$Type).equals(string)) {
                object = Type.getType(string2);
            } else if (Type.getDescriptor(class$org$objectweb$asm$Handle).equals(string)) {
                object = this.decodeHandle(string2);
            } else {
                throw new SAXException("Invalid value:" + string2 + " desc:" + string + " ctx:" + this);
            }
        }
        return object;
    }

    Handle decodeHandle(String string) throws SAXException {
        try {
            int n2 = string.indexOf(46);
            int n3 = string.indexOf(40, n2 + 1);
            int n4 = string.lastIndexOf(40);
            int n5 = string.indexOf(32, n4 + 1);
            boolean bl2 = n5 != -1;
            int n6 = Integer.parseInt(string.substring(n4 + 1, bl2 ? string.length() - 1 : n5));
            String string2 = string.substring(0, n2);
            String string3 = string.substring(n2 + 1, n3);
            String string4 = string.substring(n3, n4 - 1);
            return new Handle(n6, string2, string3, string4, bl2);
        }
        catch (RuntimeException runtimeException) {
            throw new SAXException("Malformed handle " + string, runtimeException);
        }
    }

    private final String decode(String string) throws SAXException {
        StringBuffer stringBuffer = new StringBuffer(string.length());
        try {
            for (int i2 = 0; i2 < string.length(); ++i2) {
                char c2 = string.charAt(i2);
                if (c2 == '\\') {
                    if ((c2 = string.charAt(++i2)) == '\\') {
                        stringBuffer.append('\\');
                        continue;
                    }
                    stringBuffer.append((char)Integer.parseInt(string.substring(++i2, i2 + 4), 16));
                    i2 += 3;
                    continue;
                }
                stringBuffer.append(c2);
            }
        }
        catch (RuntimeException runtimeException) {
            throw new SAXException(runtimeException);
        }
        return stringBuffer.toString();
    }

    protected final Label getLabel(Object object) {
        Label label = (Label)this.this$0.labels.get(object);
        if (label == null) {
            label = new Label();
            this.this$0.labels.put(object, label);
        }
        return label;
    }

    protected final MethodVisitor getCodeVisitor() {
        return (MethodVisitor)this.this$0.peek();
    }

    protected final int getAccess(String string) {
        int n2 = 0;
        if (string.indexOf("public") != -1) {
            n2 |= 1;
        }
        if (string.indexOf("private") != -1) {
            n2 |= 2;
        }
        if (string.indexOf("protected") != -1) {
            n2 |= 4;
        }
        if (string.indexOf("static") != -1) {
            n2 |= 8;
        }
        if (string.indexOf("final") != -1) {
            n2 |= 0x10;
        }
        if (string.indexOf("super") != -1) {
            n2 |= 0x20;
        }
        if (string.indexOf("synchronized") != -1) {
            n2 |= 0x20;
        }
        if (string.indexOf("volatile") != -1) {
            n2 |= 0x40;
        }
        if (string.indexOf("bridge") != -1) {
            n2 |= 0x40;
        }
        if (string.indexOf("varargs") != -1) {
            n2 |= 0x80;
        }
        if (string.indexOf("transient") != -1) {
            n2 |= 0x80;
        }
        if (string.indexOf("native") != -1) {
            n2 |= 0x100;
        }
        if (string.indexOf("interface") != -1) {
            n2 |= 0x200;
        }
        if (string.indexOf("abstract") != -1) {
            n2 |= 0x400;
        }
        if (string.indexOf("strict") != -1) {
            n2 |= 0x800;
        }
        if (string.indexOf("synthetic") != -1) {
            n2 |= 0x1000;
        }
        if (string.indexOf("annotation") != -1) {
            n2 |= 0x2000;
        }
        if (string.indexOf("enum") != -1) {
            n2 |= 0x4000;
        }
        if (string.indexOf("deprecated") != -1) {
            n2 |= 0x20000;
        }
        if (string.indexOf("mandated") != -1) {
            n2 |= 0x8000;
        }
        return n2;
    }

    static /* synthetic */ Class class$(String string) {
        try {
            return Class.forName(string);
        }
        catch (ClassNotFoundException classNotFoundException) {
            String string2 = classNotFoundException.getMessage();
            throw new NoClassDefFoundError(string2);
        }
    }

    static {
        class$org$objectweb$asm$Type = ASMContentHandler$Rule.class$("org.objectweb.asm.Type");
        class$org$objectweb$asm$Handle = ASMContentHandler$Rule.class$("org.objectweb.asm.Handle");
    }
}

