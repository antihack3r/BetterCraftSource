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

class DoubleInfo
extends ConstInfo {
    static final int tag = 6;
    double value;

    public DoubleInfo(double d2, int index) {
        super(index);
        this.value = d2;
    }

    public DoubleInfo(DataInputStream in2, int index) throws IOException {
        super(index);
        this.value = in2.readDouble();
    }

    public int hashCode() {
        long v2 = Double.doubleToLongBits(this.value);
        return (int)(v2 ^ v2 >>> 32);
    }

    public boolean equals(Object obj) {
        return obj instanceof DoubleInfo && ((DoubleInfo)obj).value == this.value;
    }

    @Override
    public int getTag() {
        return 6;
    }

    @Override
    public int copy(ConstPool src, ConstPool dest, Map<String, String> map) {
        return dest.addDoubleInfo(this.value);
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeByte(6);
        out.writeDouble(this.value);
    }

    @Override
    public void print(PrintWriter out) {
        out.print("Double ");
        out.println(this.value);
    }
}

