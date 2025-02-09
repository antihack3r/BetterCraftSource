/*
 * Decompiled with CFR 0.152.
 */
package javassist.bytecode;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javassist.bytecode.AttributeInfo;
import javassist.bytecode.ConstPool;
import javassist.bytecode.ConstantAttribute;

public final class FieldInfo {
    ConstPool constPool;
    int accessFlags;
    int name;
    String cachedName;
    String cachedType;
    int descriptor;
    List<AttributeInfo> attribute;

    private FieldInfo(ConstPool cp2) {
        this.constPool = cp2;
        this.accessFlags = 0;
        this.attribute = null;
    }

    public FieldInfo(ConstPool cp2, String fieldName, String desc) {
        this(cp2);
        this.name = cp2.addUtf8Info(fieldName);
        this.cachedName = fieldName;
        this.descriptor = cp2.addUtf8Info(desc);
    }

    FieldInfo(ConstPool cp2, DataInputStream in2) throws IOException {
        this(cp2);
        this.read(in2);
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
        int index;
        AttributeInfo signature;
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
        if ((signature = this.getAttribute("Signature")) != null) {
            signature = signature.copy(cp2, null);
            newAttributes.add(signature);
        }
        if ((index = this.getConstantValue()) != 0) {
            index = this.constPool.copy(index, cp2, null);
            newAttributes.add(new ConstantAttribute(cp2, index));
        }
        this.attribute = newAttributes;
        this.name = cp2.addUtf8Info(this.getName());
        this.descriptor = cp2.addUtf8Info(this.getDescriptor());
        this.constPool = cp2;
    }

    public ConstPool getConstPool() {
        return this.constPool;
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

    public int getConstantValue() {
        if ((this.accessFlags & 8) == 0) {
            return 0;
        }
        ConstantAttribute attr = (ConstantAttribute)this.getAttribute("ConstantValue");
        if (attr == null) {
            return 0;
        }
        return attr.getConstantValue();
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

