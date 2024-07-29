/*
 * Decompiled with CFR 0.152.
 */
package javassist.bytecode;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Map;
import javassist.bytecode.ByteArray;
import javassist.bytecode.ConstPool;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.SignatureAttribute;

public class LocalVariableTypeAttribute
extends LocalVariableAttribute {
    public static final String tag = "LocalVariableTypeTable";

    public LocalVariableTypeAttribute(ConstPool cp2) {
        super(cp2, tag, new byte[2]);
        ByteArray.write16bit(0, this.info, 0);
    }

    LocalVariableTypeAttribute(ConstPool cp2, int n2, DataInputStream in2) throws IOException {
        super(cp2, n2, in2);
    }

    private LocalVariableTypeAttribute(ConstPool cp2, byte[] dest) {
        super(cp2, tag, dest);
    }

    @Override
    String renameEntry(String desc, String oldname, String newname) {
        return SignatureAttribute.renameClass(desc, oldname, newname);
    }

    @Override
    String renameEntry(String desc, Map<String, String> classnames) {
        return SignatureAttribute.renameClass(desc, classnames);
    }

    @Override
    LocalVariableAttribute makeThisAttr(ConstPool cp2, byte[] dest) {
        return new LocalVariableTypeAttribute(cp2, dest);
    }
}

