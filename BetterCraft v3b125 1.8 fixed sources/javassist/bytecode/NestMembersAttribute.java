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

public class NestMembersAttribute
extends AttributeInfo {
    public static final String tag = "NestMembers";

    NestMembersAttribute(ConstPool cp2, int n2, DataInputStream in2) throws IOException {
        super(cp2, n2, in2);
    }

    private NestMembersAttribute(ConstPool cp2, byte[] info) {
        super(cp2, tag, info);
    }

    @Override
    public AttributeInfo copy(ConstPool newCp, Map<String, String> classnames) {
        byte[] src = this.get();
        byte[] dest = new byte[src.length];
        ConstPool cp2 = this.getConstPool();
        int n2 = ByteArray.readU16bit(src, 0);
        ByteArray.write16bit(n2, dest, 0);
        int i2 = 0;
        int j2 = 2;
        while (i2 < n2) {
            int index = ByteArray.readU16bit(src, j2);
            int newIndex = cp2.copy(index, newCp, classnames);
            ByteArray.write16bit(newIndex, dest, j2);
            ++i2;
            j2 += 2;
        }
        return new NestMembersAttribute(newCp, dest);
    }

    public int numberOfClasses() {
        return ByteArray.readU16bit(this.info, 0);
    }

    public int memberClass(int index) {
        return ByteArray.readU16bit(this.info, index * 2 + 2);
    }
}

