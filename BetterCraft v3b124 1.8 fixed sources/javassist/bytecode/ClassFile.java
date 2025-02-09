/*
 * Decompiled with CFR 0.152.
 */
package javassist.bytecode;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import javassist.CannotCompileException;
import javassist.bytecode.AttributeInfo;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.ConstPool;
import javassist.bytecode.Descriptor;
import javassist.bytecode.DuplicateMemberException;
import javassist.bytecode.FieldInfo;
import javassist.bytecode.InnerClassesAttribute;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.SourceFileAttribute;

public final class ClassFile {
    int major;
    int minor;
    ConstPool constPool;
    int thisClass;
    int accessFlags;
    int superClass;
    int[] interfaces;
    List<FieldInfo> fields;
    List<MethodInfo> methods;
    List<AttributeInfo> attributes;
    String thisclassname;
    String[] cachedInterfaces;
    String cachedSuperclass;
    public static final int JAVA_1 = 45;
    public static final int JAVA_2 = 46;
    public static final int JAVA_3 = 47;
    public static final int JAVA_4 = 48;
    public static final int JAVA_5 = 49;
    public static final int JAVA_6 = 50;
    public static final int JAVA_7 = 51;
    public static final int JAVA_8 = 52;
    public static final int JAVA_9 = 53;
    public static final int JAVA_10 = 54;
    public static final int JAVA_11 = 55;
    public static final int MAJOR_VERSION;

    public ClassFile(DataInputStream in2) throws IOException {
        this.read(in2);
    }

    public ClassFile(boolean isInterface, String classname, String superclass) {
        this.major = MAJOR_VERSION;
        this.minor = 0;
        this.constPool = new ConstPool(classname);
        this.thisClass = this.constPool.getThisClassInfo();
        this.accessFlags = isInterface ? 1536 : 32;
        this.initSuperclass(superclass);
        this.interfaces = null;
        this.fields = new ArrayList<FieldInfo>();
        this.methods = new ArrayList<MethodInfo>();
        this.thisclassname = classname;
        this.attributes = new ArrayList<AttributeInfo>();
        this.attributes.add(new SourceFileAttribute(this.constPool, ClassFile.getSourcefileName(this.thisclassname)));
    }

    private void initSuperclass(String superclass) {
        if (superclass != null) {
            this.superClass = this.constPool.addClassInfo(superclass);
            this.cachedSuperclass = superclass;
        } else {
            this.superClass = this.constPool.addClassInfo("java.lang.Object");
            this.cachedSuperclass = "java.lang.Object";
        }
    }

    private static String getSourcefileName(String qname) {
        return qname.replaceAll("^.*\\.", "") + ".java";
    }

    public void compact() {
        ConstPool cp2 = this.compact0();
        for (MethodInfo minfo : this.methods) {
            minfo.compact(cp2);
        }
        for (FieldInfo finfo : this.fields) {
            finfo.compact(cp2);
        }
        this.attributes = AttributeInfo.copyAll(this.attributes, cp2);
        this.constPool = cp2;
    }

    private ConstPool compact0() {
        ConstPool cp2 = new ConstPool(this.thisclassname);
        this.thisClass = cp2.getThisClassInfo();
        String sc2 = this.getSuperclass();
        if (sc2 != null) {
            this.superClass = cp2.addClassInfo(this.getSuperclass());
        }
        if (this.interfaces != null) {
            for (int i2 = 0; i2 < this.interfaces.length; ++i2) {
                this.interfaces[i2] = cp2.addClassInfo(this.constPool.getClassInfo(this.interfaces[i2]));
            }
        }
        return cp2;
    }

    public void prune() {
        AttributeInfo signature;
        AttributeInfo visibleAnnotations;
        ConstPool cp2 = this.compact0();
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
        if ((signature = this.getAttribute("Signature")) != null) {
            signature = signature.copy(cp2, null);
            newAttributes.add(signature);
        }
        for (MethodInfo minfo : this.methods) {
            minfo.prune(cp2);
        }
        for (FieldInfo finfo : this.fields) {
            finfo.prune(cp2);
        }
        this.attributes = newAttributes;
        this.constPool = cp2;
    }

    public ConstPool getConstPool() {
        return this.constPool;
    }

    public boolean isInterface() {
        return (this.accessFlags & 0x200) != 0;
    }

    public boolean isFinal() {
        return (this.accessFlags & 0x10) != 0;
    }

    public boolean isAbstract() {
        return (this.accessFlags & 0x400) != 0;
    }

    public int getAccessFlags() {
        return this.accessFlags;
    }

    public void setAccessFlags(int acc2) {
        if ((acc2 & 0x200) == 0) {
            acc2 |= 0x20;
        }
        this.accessFlags = acc2;
    }

    public int getInnerAccessFlags() {
        InnerClassesAttribute ica = (InnerClassesAttribute)this.getAttribute("InnerClasses");
        if (ica == null) {
            return -1;
        }
        String name = this.getName();
        int n2 = ica.tableLength();
        for (int i2 = 0; i2 < n2; ++i2) {
            if (!name.equals(ica.innerClass(i2))) continue;
            return ica.accessFlags(i2);
        }
        return -1;
    }

    public String getName() {
        return this.thisclassname;
    }

    public void setName(String name) {
        this.renameClass(this.thisclassname, name);
    }

    public String getSuperclass() {
        if (this.cachedSuperclass == null) {
            this.cachedSuperclass = this.constPool.getClassInfo(this.superClass);
        }
        return this.cachedSuperclass;
    }

    public int getSuperclassId() {
        return this.superClass;
    }

    public void setSuperclass(String superclass) throws CannotCompileException {
        if (superclass == null) {
            superclass = "java.lang.Object";
        }
        try {
            this.superClass = this.constPool.addClassInfo(superclass);
            for (MethodInfo minfo : this.methods) {
                minfo.setSuperclass(superclass);
            }
        }
        catch (BadBytecode e2) {
            throw new CannotCompileException(e2);
        }
        this.cachedSuperclass = superclass;
    }

    public final void renameClass(String oldname, String newname) {
        String desc;
        if (oldname.equals(newname)) {
            return;
        }
        if (oldname.equals(this.thisclassname)) {
            this.thisclassname = newname;
        }
        oldname = Descriptor.toJvmName(oldname);
        newname = Descriptor.toJvmName(newname);
        this.constPool.renameClass(oldname, newname);
        AttributeInfo.renameClass(this.attributes, oldname, newname);
        for (MethodInfo minfo : this.methods) {
            desc = minfo.getDescriptor();
            minfo.setDescriptor(Descriptor.rename(desc, oldname, newname));
            AttributeInfo.renameClass(minfo.getAttributes(), oldname, newname);
        }
        for (FieldInfo finfo : this.fields) {
            desc = finfo.getDescriptor();
            finfo.setDescriptor(Descriptor.rename(desc, oldname, newname));
            AttributeInfo.renameClass(finfo.getAttributes(), oldname, newname);
        }
    }

    public final void renameClass(Map<String, String> classnames) {
        String desc;
        String jvmNewThisName = classnames.get(Descriptor.toJvmName(this.thisclassname));
        if (jvmNewThisName != null) {
            this.thisclassname = Descriptor.toJavaName(jvmNewThisName);
        }
        this.constPool.renameClass(classnames);
        AttributeInfo.renameClass(this.attributes, classnames);
        for (MethodInfo minfo : this.methods) {
            desc = minfo.getDescriptor();
            minfo.setDescriptor(Descriptor.rename(desc, classnames));
            AttributeInfo.renameClass(minfo.getAttributes(), classnames);
        }
        for (FieldInfo finfo : this.fields) {
            desc = finfo.getDescriptor();
            finfo.setDescriptor(Descriptor.rename(desc, classnames));
            AttributeInfo.renameClass(finfo.getAttributes(), classnames);
        }
    }

    public final void getRefClasses(Map<String, String> classnames) {
        String desc;
        this.constPool.renameClass(classnames);
        AttributeInfo.getRefClasses(this.attributes, classnames);
        for (MethodInfo minfo : this.methods) {
            desc = minfo.getDescriptor();
            Descriptor.rename(desc, classnames);
            AttributeInfo.getRefClasses(minfo.getAttributes(), classnames);
        }
        for (FieldInfo finfo : this.fields) {
            desc = finfo.getDescriptor();
            Descriptor.rename(desc, classnames);
            AttributeInfo.getRefClasses(finfo.getAttributes(), classnames);
        }
    }

    public String[] getInterfaces() {
        if (this.cachedInterfaces != null) {
            return this.cachedInterfaces;
        }
        String[] rtn = null;
        if (this.interfaces == null) {
            rtn = new String[]{};
        } else {
            String[] list = new String[this.interfaces.length];
            for (int i2 = 0; i2 < this.interfaces.length; ++i2) {
                list[i2] = this.constPool.getClassInfo(this.interfaces[i2]);
            }
            rtn = list;
        }
        this.cachedInterfaces = rtn;
        return rtn;
    }

    public void setInterfaces(String[] nameList) {
        this.cachedInterfaces = null;
        if (nameList != null) {
            this.interfaces = new int[nameList.length];
            for (int i2 = 0; i2 < nameList.length; ++i2) {
                this.interfaces[i2] = this.constPool.addClassInfo(nameList[i2]);
            }
        }
    }

    public void addInterface(String name) {
        this.cachedInterfaces = null;
        int info = this.constPool.addClassInfo(name);
        if (this.interfaces == null) {
            this.interfaces = new int[1];
            this.interfaces[0] = info;
        } else {
            int n2 = this.interfaces.length;
            int[] newarray = new int[n2 + 1];
            System.arraycopy(this.interfaces, 0, newarray, 0, n2);
            newarray[n2] = info;
            this.interfaces = newarray;
        }
    }

    public List<FieldInfo> getFields() {
        return this.fields;
    }

    public void addField(FieldInfo finfo) throws DuplicateMemberException {
        this.testExistingField(finfo.getName(), finfo.getDescriptor());
        this.fields.add(finfo);
    }

    public final void addField2(FieldInfo finfo) {
        this.fields.add(finfo);
    }

    private void testExistingField(String name, String descriptor) throws DuplicateMemberException {
        for (FieldInfo minfo : this.fields) {
            if (!minfo.getName().equals(name)) continue;
            throw new DuplicateMemberException("duplicate field: " + name);
        }
    }

    public List<MethodInfo> getMethods() {
        return this.methods;
    }

    public MethodInfo getMethod(String name) {
        for (MethodInfo minfo : this.methods) {
            if (!minfo.getName().equals(name)) continue;
            return minfo;
        }
        return null;
    }

    public MethodInfo getStaticInitializer() {
        return this.getMethod("<clinit>");
    }

    public void addMethod(MethodInfo minfo) throws DuplicateMemberException {
        this.testExistingMethod(minfo);
        this.methods.add(minfo);
    }

    public final void addMethod2(MethodInfo minfo) {
        this.methods.add(minfo);
    }

    private void testExistingMethod(MethodInfo newMinfo) throws DuplicateMemberException {
        String name = newMinfo.getName();
        String descriptor = newMinfo.getDescriptor();
        ListIterator<MethodInfo> it2 = this.methods.listIterator(0);
        while (it2.hasNext()) {
            if (!ClassFile.isDuplicated(newMinfo, name, descriptor, it2.next(), it2)) continue;
            throw new DuplicateMemberException("duplicate method: " + name + " in " + this.getName());
        }
    }

    private static boolean isDuplicated(MethodInfo newMethod, String newName, String newDesc, MethodInfo minfo, ListIterator<MethodInfo> it2) {
        if (!minfo.getName().equals(newName)) {
            return false;
        }
        String desc = minfo.getDescriptor();
        if (!Descriptor.eqParamTypes(desc, newDesc)) {
            return false;
        }
        if (desc.equals(newDesc)) {
            if (ClassFile.notBridgeMethod(minfo)) {
                return true;
            }
            it2.remove();
            return false;
        }
        return false;
    }

    private static boolean notBridgeMethod(MethodInfo minfo) {
        return (minfo.getAccessFlags() & 0x40) == 0;
    }

    public List<AttributeInfo> getAttributes() {
        return this.attributes;
    }

    public AttributeInfo getAttribute(String name) {
        for (AttributeInfo ai2 : this.attributes) {
            if (!ai2.getName().equals(name)) continue;
            return ai2;
        }
        return null;
    }

    public AttributeInfo removeAttribute(String name) {
        return AttributeInfo.remove(this.attributes, name);
    }

    public void addAttribute(AttributeInfo info) {
        AttributeInfo.remove(this.attributes, info.getName());
        this.attributes.add(info);
    }

    public String getSourceFile() {
        SourceFileAttribute sf2 = (SourceFileAttribute)this.getAttribute("SourceFile");
        if (sf2 == null) {
            return null;
        }
        return sf2.getFileName();
    }

    private void read(DataInputStream in2) throws IOException {
        int i2;
        int magic = in2.readInt();
        if (magic != -889275714) {
            throw new IOException("bad magic number: " + Integer.toHexString(magic));
        }
        this.minor = in2.readUnsignedShort();
        this.major = in2.readUnsignedShort();
        this.constPool = new ConstPool(in2);
        this.accessFlags = in2.readUnsignedShort();
        this.thisClass = in2.readUnsignedShort();
        this.constPool.setThisClassInfo(this.thisClass);
        this.superClass = in2.readUnsignedShort();
        int n2 = in2.readUnsignedShort();
        if (n2 == 0) {
            this.interfaces = null;
        } else {
            this.interfaces = new int[n2];
            for (i2 = 0; i2 < n2; ++i2) {
                this.interfaces[i2] = in2.readUnsignedShort();
            }
        }
        ConstPool cp2 = this.constPool;
        n2 = in2.readUnsignedShort();
        this.fields = new ArrayList<FieldInfo>();
        for (i2 = 0; i2 < n2; ++i2) {
            this.addField2(new FieldInfo(cp2, in2));
        }
        n2 = in2.readUnsignedShort();
        this.methods = new ArrayList<MethodInfo>();
        for (i2 = 0; i2 < n2; ++i2) {
            this.addMethod2(new MethodInfo(cp2, in2));
        }
        this.attributes = new ArrayList<AttributeInfo>();
        n2 = in2.readUnsignedShort();
        for (i2 = 0; i2 < n2; ++i2) {
            this.addAttribute(AttributeInfo.read(cp2, in2));
        }
        this.thisclassname = this.constPool.getClassInfo(this.thisClass);
    }

    public void write(DataOutputStream out) throws IOException {
        int i2;
        out.writeInt(-889275714);
        out.writeShort(this.minor);
        out.writeShort(this.major);
        this.constPool.write(out);
        out.writeShort(this.accessFlags);
        out.writeShort(this.thisClass);
        out.writeShort(this.superClass);
        int n2 = this.interfaces == null ? 0 : this.interfaces.length;
        out.writeShort(n2);
        for (i2 = 0; i2 < n2; ++i2) {
            out.writeShort(this.interfaces[i2]);
        }
        n2 = this.fields.size();
        out.writeShort(n2);
        for (i2 = 0; i2 < n2; ++i2) {
            FieldInfo finfo = this.fields.get(i2);
            finfo.write(out);
        }
        out.writeShort(this.methods.size());
        for (MethodInfo minfo : this.methods) {
            minfo.write(out);
        }
        out.writeShort(this.attributes.size());
        AttributeInfo.writeAll(this.attributes, out);
    }

    public int getMajorVersion() {
        return this.major;
    }

    public void setMajorVersion(int major) {
        this.major = major;
    }

    public int getMinorVersion() {
        return this.minor;
    }

    public void setMinorVersion(int minor) {
        this.minor = minor;
    }

    public void setVersionToJava5() {
        this.major = 49;
        this.minor = 0;
    }

    static {
        int ver = 47;
        try {
            Class.forName("java.lang.StringBuilder");
            ver = 49;
            Class.forName("java.util.zip.DeflaterInputStream");
            ver = 50;
            Class.forName("java.lang.invoke.CallSite", false, ClassLoader.getSystemClassLoader());
            ver = 51;
            Class.forName("java.util.function.Function");
            ver = 52;
            Class.forName("java.lang.Module");
            ver = 53;
            List.class.getMethod("copyOf", Collection.class);
            ver = 54;
            Class.forName("java.util.Optional").getMethod("isEmpty", new Class[0]);
            ver = 55;
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        MAJOR_VERSION = ver;
    }
}

