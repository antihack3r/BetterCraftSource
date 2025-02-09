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

public class MethodParametersAttribute
extends AttributeInfo {
    public static final String tag = "MethodParameters";

    MethodParametersAttribute(ConstPool cp2, int n2, DataInputStream in2) throws IOException {
        super(cp2, n2, in2);
    }

    public MethodParametersAttribute(ConstPool cp2, String[] names, int[] flags) {
        super(cp2, tag);
        byte[] data = new byte[names.length * 4 + 1];
        data[0] = (byte)names.length;
        for (int i2 = 0; i2 < names.length; ++i2) {
            ByteArray.write16bit(cp2.addUtf8Info(names[i2]), data, i2 * 4 + 1);
            ByteArray.write16bit(flags[i2], data, i2 * 4 + 3);
        }
        this.set(data);
    }

    public int size() {
        return this.info[0] & 0xFF;
    }

    public int name(int i2) {
        return ByteArray.readU16bit(this.info, i2 * 4 + 1);
    }

    public int accessFlags(int i2) {
        return ByteArray.readU16bit(this.info, i2 * 4 + 3);
    }

    @Override
    public AttributeInfo copy(ConstPool newCp, Map<String, String> classnames) {
        int s2 = this.size();
        ConstPool cp2 = this.getConstPool();
        String[] names = new String[s2];
        int[] flags = new int[s2];
        for (int i2 = 0; i2 < s2; ++i2) {
            names[i2] = cp2.getUtf8Info(this.name(i2));
            flags[i2] = this.accessFlags(i2);
        }
        return new MethodParametersAttribute(newCp, names, flags);
    }
}

