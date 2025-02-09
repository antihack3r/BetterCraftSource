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

public class ConstantAttribute
extends AttributeInfo {
    public static final String tag = "ConstantValue";

    ConstantAttribute(ConstPool cp2, int n2, DataInputStream in2) throws IOException {
        super(cp2, n2, in2);
    }

    public ConstantAttribute(ConstPool cp2, int index) {
        super(cp2, tag);
        byte[] bvalue = new byte[]{(byte)(index >>> 8), (byte)index};
        this.set(bvalue);
    }

    public int getConstantValue() {
        return ByteArray.readU16bit(this.get(), 0);
    }

    @Override
    public AttributeInfo copy(ConstPool newCp, Map<String, String> classnames) {
        int index = this.getConstPool().copy(this.getConstantValue(), newCp, classnames);
        return new ConstantAttribute(newCp, index);
    }
}

