/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm.xml;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.TypePath;
import org.objectweb.asm.xml.SAXAdapter;
import org.objectweb.asm.xml.SAXAnnotationAdapter;
import org.objectweb.asm.xml.SAXCodeAdapter;
import org.objectweb.asm.xml.SAXFieldAdapter;
import org.xml.sax.ContentHandler;
import org.xml.sax.helpers.AttributesImpl;

public final class SAXClassAdapter
extends ClassVisitor {
    SAXAdapter sa;
    private final boolean singleDocument;

    public SAXClassAdapter(ContentHandler contentHandler, boolean bl2) {
        super(327680);
        this.sa = new SAXAdapter(contentHandler);
        this.singleDocument = bl2;
        if (!bl2) {
            this.sa.addDocumentStart();
        }
    }

    public void visitSource(String string, String string2) {
        AttributesImpl attributesImpl = new AttributesImpl();
        if (string != null) {
            attributesImpl.addAttribute("", "file", "file", "", SAXClassAdapter.encode(string));
        }
        if (string2 != null) {
            attributesImpl.addAttribute("", "debug", "debug", "", SAXClassAdapter.encode(string2));
        }
        this.sa.addElement("source", attributesImpl);
    }

    public void visitOuterClass(String string, String string2, String string3) {
        AttributesImpl attributesImpl = new AttributesImpl();
        attributesImpl.addAttribute("", "owner", "owner", "", string);
        if (string2 != null) {
            attributesImpl.addAttribute("", "name", "name", "", string2);
        }
        if (string3 != null) {
            attributesImpl.addAttribute("", "desc", "desc", "", string3);
        }
        this.sa.addElement("outerclass", attributesImpl);
    }

    public AnnotationVisitor visitAnnotation(String string, boolean bl2) {
        return new SAXAnnotationAdapter(this.sa, "annotation", bl2 ? 1 : -1, null, string);
    }

    public AnnotationVisitor visitTypeAnnotation(int n2, TypePath typePath, String string, boolean bl2) {
        return new SAXAnnotationAdapter(this.sa, "typeAnnotation", bl2 ? 1 : -1, null, string, n2, typePath);
    }

    public void visit(int n2, int n3, String string, String string2, String string3, String[] stringArray) {
        StringBuffer stringBuffer = new StringBuffer();
        SAXClassAdapter.appendAccess(n3 | 0x40000, stringBuffer);
        AttributesImpl attributesImpl = new AttributesImpl();
        attributesImpl.addAttribute("", "access", "access", "", stringBuffer.toString());
        if (string != null) {
            attributesImpl.addAttribute("", "name", "name", "", string);
        }
        if (string2 != null) {
            attributesImpl.addAttribute("", "signature", "signature", "", SAXClassAdapter.encode(string2));
        }
        if (string3 != null) {
            attributesImpl.addAttribute("", "parent", "parent", "", string3);
        }
        attributesImpl.addAttribute("", "major", "major", "", Integer.toString(n2 & 0xFFFF));
        attributesImpl.addAttribute("", "minor", "minor", "", Integer.toString(n2 >>> 16));
        this.sa.addStart("class", attributesImpl);
        this.sa.addStart("interfaces", new AttributesImpl());
        if (stringArray != null && stringArray.length > 0) {
            for (int i2 = 0; i2 < stringArray.length; ++i2) {
                AttributesImpl attributesImpl2 = new AttributesImpl();
                attributesImpl2.addAttribute("", "name", "name", "", stringArray[i2]);
                this.sa.addElement("interface", attributesImpl2);
            }
        }
        this.sa.addEnd("interfaces");
    }

    public FieldVisitor visitField(int n2, String string, String string2, String string3, Object object) {
        StringBuffer stringBuffer = new StringBuffer();
        SAXClassAdapter.appendAccess(n2 | 0x80000, stringBuffer);
        AttributesImpl attributesImpl = new AttributesImpl();
        attributesImpl.addAttribute("", "access", "access", "", stringBuffer.toString());
        attributesImpl.addAttribute("", "name", "name", "", string);
        attributesImpl.addAttribute("", "desc", "desc", "", string2);
        if (string3 != null) {
            attributesImpl.addAttribute("", "signature", "signature", "", SAXClassAdapter.encode(string3));
        }
        if (object != null) {
            attributesImpl.addAttribute("", "value", "value", "", SAXClassAdapter.encode(object.toString()));
        }
        return new SAXFieldAdapter(this.sa, attributesImpl);
    }

    public MethodVisitor visitMethod(int n2, String string, String string2, String string3, String[] stringArray) {
        StringBuffer stringBuffer = new StringBuffer();
        SAXClassAdapter.appendAccess(n2, stringBuffer);
        AttributesImpl attributesImpl = new AttributesImpl();
        attributesImpl.addAttribute("", "access", "access", "", stringBuffer.toString());
        attributesImpl.addAttribute("", "name", "name", "", string);
        attributesImpl.addAttribute("", "desc", "desc", "", string2);
        if (string3 != null) {
            attributesImpl.addAttribute("", "signature", "signature", "", string3);
        }
        this.sa.addStart("method", attributesImpl);
        this.sa.addStart("exceptions", new AttributesImpl());
        if (stringArray != null && stringArray.length > 0) {
            for (int i2 = 0; i2 < stringArray.length; ++i2) {
                AttributesImpl attributesImpl2 = new AttributesImpl();
                attributesImpl2.addAttribute("", "name", "name", "", stringArray[i2]);
                this.sa.addElement("exception", attributesImpl2);
            }
        }
        this.sa.addEnd("exceptions");
        return new SAXCodeAdapter(this.sa, n2);
    }

    public final void visitInnerClass(String string, String string2, String string3, int n2) {
        StringBuffer stringBuffer = new StringBuffer();
        SAXClassAdapter.appendAccess(n2 | 0x100000, stringBuffer);
        AttributesImpl attributesImpl = new AttributesImpl();
        attributesImpl.addAttribute("", "access", "access", "", stringBuffer.toString());
        if (string != null) {
            attributesImpl.addAttribute("", "name", "name", "", string);
        }
        if (string2 != null) {
            attributesImpl.addAttribute("", "outerName", "outerName", "", string2);
        }
        if (string3 != null) {
            attributesImpl.addAttribute("", "innerName", "innerName", "", string3);
        }
        this.sa.addElement("innerclass", attributesImpl);
    }

    public final void visitEnd() {
        this.sa.addEnd("class");
        if (!this.singleDocument) {
            this.sa.addDocumentEnd();
        }
    }

    static final String encode(String string) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i2 = 0; i2 < string.length(); ++i2) {
            char c2 = string.charAt(i2);
            if (c2 == '\\') {
                stringBuffer.append("\\\\");
                continue;
            }
            if (c2 < ' ' || c2 > '\u007f') {
                stringBuffer.append("\\u");
                if (c2 < '\u0010') {
                    stringBuffer.append("000");
                } else if (c2 < '\u0100') {
                    stringBuffer.append("00");
                } else if (c2 < '\u1000') {
                    stringBuffer.append('0');
                }
                stringBuffer.append(Integer.toString(c2, 16));
                continue;
            }
            stringBuffer.append(c2);
        }
        return stringBuffer.toString();
    }

    static void appendAccess(int n2, StringBuffer stringBuffer) {
        if ((n2 & 1) != 0) {
            stringBuffer.append("public ");
        }
        if ((n2 & 2) != 0) {
            stringBuffer.append("private ");
        }
        if ((n2 & 4) != 0) {
            stringBuffer.append("protected ");
        }
        if ((n2 & 0x10) != 0) {
            stringBuffer.append("final ");
        }
        if ((n2 & 8) != 0) {
            stringBuffer.append("static ");
        }
        if ((n2 & 0x20) != 0) {
            if ((n2 & 0x40000) == 0) {
                stringBuffer.append("synchronized ");
            } else {
                stringBuffer.append("super ");
            }
        }
        if ((n2 & 0x40) != 0) {
            if ((n2 & 0x80000) == 0) {
                stringBuffer.append("bridge ");
            } else {
                stringBuffer.append("volatile ");
            }
        }
        if ((n2 & 0x80) != 0) {
            if ((n2 & 0x80000) == 0) {
                stringBuffer.append("varargs ");
            } else {
                stringBuffer.append("transient ");
            }
        }
        if ((n2 & 0x100) != 0) {
            stringBuffer.append("native ");
        }
        if ((n2 & 0x800) != 0) {
            stringBuffer.append("strict ");
        }
        if ((n2 & 0x200) != 0) {
            stringBuffer.append("interface ");
        }
        if ((n2 & 0x400) != 0) {
            stringBuffer.append("abstract ");
        }
        if ((n2 & 0x1000) != 0) {
            stringBuffer.append("synthetic ");
        }
        if ((n2 & 0x2000) != 0) {
            stringBuffer.append("annotation ");
        }
        if ((n2 & 0x4000) != 0) {
            stringBuffer.append("enum ");
        }
        if ((n2 & 0x20000) != 0) {
            stringBuffer.append("deprecated ");
        }
        if ((n2 & 0x8000) != 0) {
            stringBuffer.append("mandated ");
        }
    }
}

