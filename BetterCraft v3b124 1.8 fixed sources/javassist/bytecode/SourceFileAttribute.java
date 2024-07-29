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

public class SourceFileAttribute
extends AttributeInfo {
    public static final String tag = "SourceFile";

    SourceFileAttribute(ConstPool cp2, int n2, DataInputStream in2) throws IOException {
        super(cp2, n2, in2);
    }

    public SourceFileAttribute(ConstPool cp2, String filename) {
        super(cp2, tag);
        int index = cp2.addUtf8Info(filename);
        byte[] bvalue = new byte[]{(byte)(index >>> 8), (byte)index};
        this.set(bvalue);
    }

    public String getFileName() {
        return this.getConstPool().getUtf8Info(ByteArray.readU16bit(this.get(), 0));
    }

    @Override
    public AttributeInfo copy(ConstPool newCp, Map<String, String> classnames) {
        return new SourceFileAttribute(newCp, this.getFileName());
    }
}

