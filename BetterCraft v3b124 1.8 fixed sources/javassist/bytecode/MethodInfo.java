/*
 * Decompiled with CFR 0.152.
 */
package javassist.bytecode;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javassist.ClassPool;
import javassist.bytecode.AnnotationDefaultAttribute;
import javassist.bytecode.AttributeInfo;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.ByteArray;
import javassist.bytecode.ClassFile;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.CodeIterator;
import javassist.bytecode.ConstPool;
import javassist.bytecode.Descriptor;
import javassist.bytecode.ExceptionsAttribute;
import javassist.bytecode.LineNumberAttribute;
import javassist.bytecode.StackMap;
import javassist.bytecode.StackMapTable;
import javassist.bytecode.stackmap.MapMaker;

public class MethodInfo {
    ConstPool constPool;
    int accessFlags;
    int name;
    String cachedName;
    int descriptor;
    List<AttributeInfo> attribute;
    public static boolean doPreverify = false;
    public static final String nameInit = "<init>";
    public static final String nameClinit = "<clinit>";

    private MethodInfo(ConstPool cp2) {
        this.constPool = cp2;
        this.attribute = null;
    }

    public MethodInfo(ConstPool cp2, String methodname, String desc) {
        this(cp2);
        this.accessFlags = 0;
        this.name = cp2.addUtf8Info(methodname);
        this.cachedName = methodname;
        this.descriptor = this.constPool.addUtf8Info(desc);
    }

    MethodInfo(ConstPool cp2, DataInputStream in2) throws IOException {
        this(cp2);
        this.read(in2);
    }

    public MethodInfo(ConstPool cp2, String methodname, MethodInfo src, Map<String, String> classnameMap) throws BadBytecode {
        this(cp2);
        this.read(src, methodname, classnameMap);
    }

    public String toString() {
        return this.getName() + " " + this.getDescriptor();
    }

    void compact(ConstPool cp2) {
        this.name = cp2.addUtf8Info(this.getName());
        this.descriptor = cp2.addUtf8Info(this.getDescriptor());
        this.attribute = AttributeInfo.copyAll(this.attribute, cp2);
        this.constPool = cp2;
    }

    void prune(ConstPool cp2) {
        AttributeInfo signature;
        ExceptionsAttribute ea2;
        AnnotationDefaultAttribute defaultAttribute;
        AttributeInfo parameterVisibleAnnotations;
        AttributeInfo parameterInvisibleAnnotations;
        AttributeInfo visibleAnnotations;
        ArrayList<AttributeInfo> newAttributes = new ArrayList<AttributeInfo>();
        AttributeInfo invisibleAnnotations = this.getAttribute("RuntimeInvisibleAnnotations");
        if (invisibleAnnotations != null) {
            invisibleAnnotations = invisibleAnnotations.copy(cp2, null);
            newAttributes.add(invisibleAnnotations);
        }
        if ((visibleAnnotations = this.getAttribute("RuntimeVisibleAnnotations")) != null) {
            visibleAnnotations = visibleAnnotations.copy(cp2, null);
            newAttributes.add(visibleAnnotations);
        }
        if ((parameterInvisibleAnnotations = this.getAttribute("RuntimeInvisibleParameterAnnotations")) != null) {
            parameterInvisibleAnnotations = parameterInvisibleAnnotations.copy(cp2, null);
            newAttributes.add(parameterInvisibleAnnotations);
        }
        if ((parameterVisibleAnnotations = this.getAttribute("RuntimeVisibleParameterAnnotations")) != null) {
            parameterVisibleAnnotations = parameterVisibleAnnotations.copy(cp2, null);
            newAttributes.add(parameterVisibleAnnotations);
        }
        if ((defaultAttribute = (AnnotationDefaultAttribute)this.getAttribute("AnnotationDefault")) != null) {
            newAttributes.add(defaultAttribute);
        }
        if ((ea2 = this.getExceptionsAttribute()) != null) {
            newAttributes.add(ea2);
        }
        if ((signature = this.getAttribute("Signature")) != null) {
            signature = signature.copy(cp2, null);
            newAttributes.add(signature);
        }
        this.attribute = newAttributes;
        this.name = cp2.addUtf8Info(this.getName());
        this.descriptor = cp2.addUtf8Info(this.getDescriptor());
        this.constPool = cp2;
    }

    public String getName() {
        if (this.cachedName == null) {
            this.cachedName = this.constPool.getUtf8Info(this.name);
        }
        return this.cachedName;
    }

    public void setName(String newName) {
        this.name = this.constPool.addUtf8Info(newName);
        this.cachedName = newName;
    }

    public boolean isMethod() {
        String n2 = this.getName();
        return !n2.equals(nameInit) && !n2.equals(nameClinit);
    }

    public ConstPool getConstPool() {
        return this.constPool;
    }

    public boolean isConstructor() {
        return this.getName().equals(nameInit);
    }

    public boolean isStaticInitializer() {
        return this.getName().equals(nameClinit);
    }

    public int getAccessFlags() {
        return this.accessFlags;
    }

    public void setAccessFlags(int acc2) {
        this.accessFlags = acc2;
    }

    public String getDescriptor() {
        return this.constPool.getUtf8Info(this.descriptor);
    }

    public void setDescriptor(String desc) {
        if (!desc.equals(this.getDescriptor())) {
            this.descriptor = this.constPool.addUtf8Info(desc);
        }
    }

    public List<AttributeInfo> getAttributes() {
        if (this.attribute == null) {
            this.attribute = new ArrayList<AttributeInfo>();
        }
        return this.attribute;
    }

    public AttributeInfo getAttribute(String name) {
        return AttributeInfo.lookup(this.attribute, name);
    }

    public AttributeInfo removeAttribute(String name) {
        return AttributeInfo.remove(this.attribute, name);
    }

    public void addAttribute(AttributeInfo info) {
        if (this.attribute == null) {
            this.attribute = new ArrayList<AttributeInfo>();
        }
        AttributeInfo.remove(this.attribute, info.getName());
        this.attribute.add(info);
    }

    public ExceptionsAttribute getExceptionsAttribute() {
        AttributeInfo info = AttributeInfo.lookup(this.attribute, "Exceptions");
        return (ExceptionsAttribute)info;
    }

    public CodeAttribute getCodeAttribute() {
        AttributeInfo info = AttributeInfo.lookup(this.attribute, "Code");
        return (CodeAttribute)info;
    }

    public void removeExceptionsAttribute() {
        AttributeInfo.remove(this.attribute, "Exceptions");
    }

    public void setExceptionsAttribute(ExceptionsAttribute cattr) {
        this.removeExceptionsAttribute();
        if (this.attribute == null) {
            this.attribute = new ArrayList<AttributeInfo>();
        }
        this.attribute.add(cattr);
    }

    public void removeCodeAttribute() {
        AttributeInfo.remove(this.attribute, "Code");
    }

    public void setCodeAttribute(CodeAttribute cattr) {
        this.removeCodeAttribute();
        if (this.attribute == null) {
            this.attribute = new ArrayList<AttributeInfo>();
        }
        this.attribute.add(cattr);
    }

    public void rebuildStackMapIf6(ClassPool pool, ClassFile cf2) throws BadBytecode {
        if (cf2.getMajorVersion() >= 50) {
            this.rebuildStackMap(pool);
        }
        if (doPreverify) {
            this.rebuildStackMapForME(pool);
        }
    }

    public void rebuildStackMap(ClassPool pool) throws BadBytecode {
        CodeAttribute ca2 = this.getCodeAttribute();
        if (ca2 != null) {
            StackMapTable smt = MapMaker.make(pool, this);
            ca2.setAttribute(smt);
        }
    }

    public void rebuildStackMapForME(ClassPool pool) throws BadBytecode {
        CodeAttribute ca2 = this.getCodeAttribute();
        if (ca2 != null) {
            StackMap sm2 = MapMaker.make2(pool, this);
            ca2.setAttribute(sm2);
        }
    }

    public int getLineNumber(int pos) {
        CodeAttribute ca2 = this.getCodeAttribute();
        if (ca2 == null) {
            return -1;
        }
        LineNumberAttribute ainfo = (LineNumberAttribute)ca2.getAttribute("LineNumberTable");
        if (ainfo == null) {
            return -1;
        }
        return ainfo.toLineNumber(pos);
    }

    public void setSuperclass(String superclass) throws BadBytecode {
        if (!this.isConstructor()) {
            return;
        }
        CodeAttribute ca2 = this.getCodeAttribute();
        byte[] code = ca2.getCode();
        CodeIterator iterator = ca2.iterator();
        int pos = iterator.skipSuperConstructor();
        if (pos >= 0) {
            ConstPool cp2 = this.constPool;
            int mref = ByteArray.readU16bit(code, pos + 1);
            int nt2 = cp2.getMethodrefNameAndType(mref);
            int sc2 = cp2.addClassInfo(superclass);
            int mref2 = cp2.addMethodrefInfo(sc2, nt2);
            ByteArray.write16bit(mref2, code, pos + 1);
        }
    }

    private void read(MethodInfo src, String methodname, Map<String, String> classnames) {
        CodeAttribute cattr;
        ConstPool destCp = this.constPool;
        this.accessFlags = src.accessFlags;
        this.name = destCp.addUtf8Info(methodname);
        this.cachedName = methodname;
        ConstPool srcCp = src.constPool;
        String desc = srcCp.getUtf8Info(src.descriptor);
        String desc2 = Descriptor.rename(desc, classnames);
        this.descriptor = destCp.addUtf8Info(desc2);
        this.attribute = new ArrayList<AttributeInfo>();
        ExceptionsAttribute eattr = src.getExceptionsAttribute();
        if (eattr != null) {
            this.attribute.add(eattr.copy(destCp, classnames));
        }
        if ((cattr = src.getCodeAttribute()) != null) {
            this.attribute.add(cattr.copy(destCp, classnames));
        }
    }

    private void read(DataInputStream in2) throws IOException {
        this.accessFlags = in2.readUnsignedShort();
        this.name = in2.readUnsignedShort();
        this.descriptor = in2.readUnsignedShort();
        int n2 = in2.readUnsignedShort();
        this.attribute = new ArrayList<AttributeInfo>();
        for (int i2 = 0; i2 < n2; ++i2) {
            this.attribute.add(AttributeInfo.read(this.constPool, in2));
        }
    }

    void write(DataOutputStream out) throws IOException {
        out.writeShort(this.accessFlags);
        out.writeShort(this.name);
        out.writeShort(this.descriptor);
        if (this.attribute == null) {
            out.writeShort(0);
        } else {
            out.writeShort(this.attribute.size());
            AttributeInfo.writeAll(this.attribute, out);
        }
    }
}

