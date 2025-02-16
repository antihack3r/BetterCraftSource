/*
 * Decompiled with CFR 0.152.
 */
package javassist.bytecode;

import java.io.DataInputStream;
import java.io.IOException;
import javassist.bytecode.ConstPool;
import javassist.bytecode.MemberrefInfo;

class InterfaceMethodrefInfo
extends MemberrefInfo {
    static final int tag = 11;

    public InterfaceMethodrefInfo(int cindex, int ntindex, int thisIndex) {
        super(cindex, ntindex, thisIndex);
    }

    public InterfaceMethodrefInfo(DataInputStream in2, int thisIndex) throws IOException {
        super(in2, thisIndex);
    }

    @Override
    public int getTag() {
        return 11;
    }

    @Override
    public String getTagName() {
        return "Interface";
    }

    @Override
    protected int copy2(ConstPool dest, int cindex, int ntindex) {
        return dest.addInterfaceMethodrefInfo(cindex, ntindex);
    }
}

