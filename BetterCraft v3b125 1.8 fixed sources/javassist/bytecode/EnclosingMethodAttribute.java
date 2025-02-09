/*
 * Decompiled with CFR 0.152.
 */
package javassist.bytecode;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Map;
import javassist.bytecode.AttributeInfo;
import javassist.bytecode.ByteArray;
import javassist.bytecode.ConstPool;

public class EnclosingMethodAttribute
extends AttributeInfo {
    public static final String tag = "EnclosingMethod";

    EnclosingMethodAttribute(ConstPool cp2, int n2, DataInputStream in2) throws IOException {
        super(cp2, n2, in2);
    }

    public EnclosingMethodAttribute(ConstPool cp2, String className, String methodName, String methodDesc) {
        super(cp2, tag);
        int ci = cp2.addClassInfo(className);
        int ni2 = cp2.addNameAndTypeInfo(methodName, methodDesc);
        byte[] bvalue = new byte[]{(byte)(ci >>> 8), (byte)ci, (byte)(ni2 >>> 8), (byte)ni2};
        this.set(bvalue);
    }

    public EnclosingMethodAttribute(ConstPool cp2, String className) {
        super(cp2, tag);
        int ci = cp2.addClassInfo(className);
        int ni2 = 0;
        byte[] bvalue = new byte[]{(byte)(ci >>> 8), (byte)ci, (byte)(ni2 >>> 8), (byte)ni2};
        this.set(bvalue);
    }

    public int classIndex() {
        return ByteArray.readU16bit(this.get(), 0);
    }

    public int methodIndex() {
        return ByteArray.readU16bit(this.get(), 2);
    }

    public String className() {
        return this.getConstPool().getClassInfo(this.classIndex());
    }

    public String methodName() {
        ConstPool cp2 = this.getConstPool();
        int mi = this.methodIndex();
        if (mi == 0) {
            return "<clinit>";
        }
        int ni2 = cp2.getNameAndTypeName(mi);
        return cp2.getUtf8Info(ni2);
    }

    public String methodDescriptor() {
        ConstPool cp2 = this.getConstPool();
        int mi = this.methodIndex();
        int ti = cp2.getNameAndTypeDescriptor(mi);
        return cp2.getUtf8Info(ti);
    }

    @Override
    public AttributeInfo copy(ConstPool newCp, Map<String, String> classnames) {
        if (this.methodIndex() == 0) {
            return new EnclosingMethodAttribute(newCp, this.className());
        }
        return new EnclosingMethodAttribute(newCp, this.className(), this.methodName(), this.methodDescriptor());
    }
}

