/*
 * Decompiled with CFR 0.152.
 */
package javassist.bytecode;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import javassist.bytecode.ConstInfo;
import javassist.bytecode.ConstPool;

class LongInfo
extends ConstInfo {
    static final int tag = 5;
    long value;

    public LongInfo(long l2, int index) {
        super(index);
        this.value = l2;
    }

    public LongInfo(DataInputStream in2, int index) throws IOException {
        super(index);
        this.value = in2.readLong();
    }

    public int hashCode() {
        return (int)(this.value ^ this.value >>> 32);
    }

    public boolean equals(Object obj) {
        return obj instanceof LongInfo && ((LongInfo)obj).value == this.value;
    }

    @Override
    public int getTag() {
        return 5;
    }

    @Override
    public int copy(ConstPool src, ConstPool dest, Map<String, String> map) {
        return dest.addLongInfo(this.value);
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeByte(5);
        out.writeLong(this.value);
    }

    @Override
    public void print(PrintWriter out) {
        out.print("Long ");
        out.println(this.value);
    }
}

