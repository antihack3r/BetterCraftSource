/*
 * Decompiled with CFR 0.152.
 */
package javassist.bytecode;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Map;
import javassist.bytecode.AttributeInfo;
import javassist.bytecode.ConstPool;

public class SyntheticAttribute
extends AttributeInfo {
    public static final String tag = "Synthetic";

    SyntheticAttribute(ConstPool cp2, int n2, DataInputStream in2) throws IOException {
        super(cp2, n2, in2);
    }

    public SyntheticAttribute(ConstPool cp2) {
        super(cp2, tag, new byte[0]);
    }

    @Override
    public AttributeInfo copy(ConstPool newCp, Map<String, String> classnames) {
        return new SyntheticAttribute(newCp);
    }
}

