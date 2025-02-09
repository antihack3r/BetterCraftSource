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

public class ExceptionsAttribute
extends AttributeInfo {
    public static final String tag = "Exceptions";

    ExceptionsAttribute(ConstPool cp2, int n2, DataInputStream in2) throws IOException {
        super(cp2, n2, in2);
    }

    private ExceptionsAttribute(ConstPool cp2, ExceptionsAttribute src, Map<String, String> classnames) {
        super(cp2, tag);
        this.copyFrom(src, classnames);
    }

    public ExceptionsAttribute(ConstPool cp2) {
        super(cp2, tag);
        byte[] data = new byte[2];
        data[1] = 0;
        data[0] = 0;
        this.info = data;
    }

    @Override
    public AttributeInfo copy(ConstPool newCp, Map<String, String> classnames) {
        return new ExceptionsAttribute(newCp, this, classnames);
    }

    private void copyFrom(ExceptionsAttribute srcAttr, Map<String, String> classnames) {
        ConstPool srcCp = srcAttr.constPool;
        ConstPool destCp = this.constPool;
        byte[] src = srcAttr.info;
        int num = src.length;
        byte[] dest = new byte[num];
        dest[0] = src[0];
        dest[1] = src[1];
        for (int i2 = 2; i2 < num; i2 += 2) {
            int index = ByteArray.readU16bit(src, i2);
            ByteArray.write16bit(srcCp.copy(index, destCp, classnames), dest, i2);
        }
        this.info = dest;
    }

    public int[] getExceptionIndexes() {
        byte[] blist = this.info;
        int n2 = blist.length;
        if (n2 <= 2) {
            return null;
        }
        int[] elist = new int[n2 / 2 - 1];
        int k2 = 0;
        for (int j2 = 2; j2 < n2; j2 += 2) {
            elist[k2++] = (blist[j2] & 0xFF) << 8 | blist[j2 + 1] & 0xFF;
        }
        return elist;
    }

    public String[] getExceptions() {
        byte[] blist = this.info;
        int n2 = blist.length;
        if (n2 <= 2) {
            return null;
        }
        String[] elist = new String[n2 / 2 - 1];
        int k2 = 0;
        for (int j2 = 2; j2 < n2; j2 += 2) {
            int index = (blist[j2] & 0xFF) << 8 | blist[j2 + 1] & 0xFF;
            elist[k2++] = this.constPool.getClassInfo(index);
        }
        return elist;
    }

    public void setExceptionIndexes(int[] elist) {
        int n2 = elist.length;
        byte[] blist = new byte[n2 * 2 + 2];
        ByteArray.write16bit(n2, blist, 0);
        for (int i2 = 0; i2 < n2; ++i2) {
            ByteArray.write16bit(elist[i2], blist, i2 * 2 + 2);
        }
        this.info = blist;
    }

    public void setExceptions(String[] elist) {
        int n2 = elist.length;
        byte[] blist = new byte[n2 * 2 + 2];
        ByteArray.write16bit(n2, blist, 0);
        for (int i2 = 0; i2 < n2; ++i2) {
            ByteArray.write16bit(this.constPool.addClassInfo(elist[i2]), blist, i2 * 2 + 2);
        }
        this.info = blist;
    }

    public int tableLength() {
        return this.info.length / 2 - 1;
    }

    public int getException(int nth) {
        int index = nth * 2 + 2;
        return (this.info[index] & 0xFF) << 8 | this.info[index + 1] & 0xFF;
    }
}

