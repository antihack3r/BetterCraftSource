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

public class NestHostAttribute
extends AttributeInfo {
    public static final String tag = "NestHost";

    NestHostAttribute(ConstPool cp2, int n2, DataInputStream in2) throws IOException {
        super(cp2, n2, in2);
    }

    private NestHostAttribute(ConstPool cp2, int hostIndex) {
        super(cp2, tag, new byte[2]);
        ByteArray.write16bit(hostIndex, this.get(), 0);
    }

    @Override
    public AttributeInfo copy(ConstPool newCp, Map<String, String> classnames) {
        int hostIndex = ByteArray.readU16bit(this.get(), 0);
        int newHostIndex = this.getConstPool().copy(hostIndex, newCp, classnames);
        return new NestHostAttribute(newCp, newHostIndex);
    }

    public int hostClassIndex() {
        return ByteArray.readU16bit(this.info, 0);
    }
}

