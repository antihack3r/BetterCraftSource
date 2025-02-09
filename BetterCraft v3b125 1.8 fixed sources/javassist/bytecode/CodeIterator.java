/*
 * Decompiled with CFR 0.152.
 */
package javassist.bytecode;

import java.util.ArrayList;
import java.util.List;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.ByteArray;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.ConstPool;
import javassist.bytecode.ExceptionTable;
import javassist.bytecode.LineNumberAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.Opcode;
import javassist.bytecode.StackMap;
import javassist.bytecode.StackMapTable;

public class CodeIterator
implements Opcode {
    protected CodeAttribute codeAttr;
    protected byte[] bytecode;
    protected int endPos;
    protected int currentPos;
    protected int mark;
    protected int mark2;
    private static final int[] opcodeLength = new int[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 3, 2, 3, 3, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 3, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 2, 0, 0, 1, 1, 1, 1, 1, 1, 3, 3, 3, 3, 3, 3, 3, 5, 5, 3, 2, 3, 1, 1, 3, 3, 1, 1, 0, 4, 3, 3, 5, 5};

    protected CodeIterator(CodeAttribute ca2) {
        this.codeAttr = ca2;
        this.bytecode = ca2.getCode();
        this.begin();
    }

    public void begin() {
        this.mark2 = 0;
        this.mark = 0;
        this.currentPos = 0;
        this.endPos = this.getCodeLength();
    }

    public void move(int index) {
        this.currentPos = index;
    }

    public void setMark(int index) {
        this.mark = index;
    }

    public void setMark2(int index) {
        this.mark2 = index;
    }

    public int getMark() {
        return this.mark;
    }

    public int getMark2() {
        return this.mark2;
    }

    public CodeAttribute get() {
        return this.codeAttr;
    }

    public int getCodeLength() {
        return this.bytecode.length;
    }

    public int byteAt(int index) {
        return this.bytecode[index] & 0xFF;
    }

    public int signedByteAt(int index) {
        return this.bytecode[index];
    }

    public void writeByte(int value, int index) {
        this.bytecode[index] = (byte)value;
    }

    public int u16bitAt(int index) {
        return ByteArray.readU16bit(this.bytecode, index);
    }

    public int s16bitAt(int index) {
        return ByteArray.readS16bit(this.bytecode, index);
    }

    public void write16bit(int value, int index) {
        ByteArray.write16bit(value, this.bytecode, index);
    }

    public int s32bitAt(int index) {
        return ByteArray.read32bit(this.bytecode, index);
    }

    public void write32bit(int value, int index) {
        ByteArray.write32bit(value, this.bytecode, index);
    }

    public void write(byte[] code, int index) {
        for (byte this.bytecode[index++] : code) {
        }
    }

    public boolean hasNext() {
        return this.currentPos < this.endPos;
    }

    public int next() throws BadBytecode {
        int pos = this.currentPos;
        this.currentPos = CodeIterator.nextOpcode(this.bytecode, pos);
        return pos;
    }

    public int lookAhead() {
        return this.currentPos;
    }

    public int skipConstructor() throws BadBytecode {
        return this.skipSuperConstructor0(-1);
    }

    public int skipSuperConstructor() throws BadBytecode {
        return this.skipSuperConstructor0(0);
    }

    public int skipThisConstructor() throws BadBytecode {
        return this.skipSuperConstructor0(1);
    }

    private int skipSuperConstructor0(int skipThis) throws BadBytecode {
        this.begin();
        ConstPool cp2 = this.codeAttr.getConstPool();
        String thisClassName = this.codeAttr.getDeclaringClass();
        int nested = 0;
        while (this.hasNext()) {
            int mref;
            int index = this.next();
            int c2 = this.byteAt(index);
            if (c2 == 187) {
                ++nested;
                continue;
            }
            if (c2 != 183 || !cp2.getMethodrefName(mref = ByteArray.readU16bit(this.bytecode, index + 1)).equals("<init>") || --nested >= 0) continue;
            if (skipThis < 0) {
                return index;
            }
            String cname = cp2.getMethodrefClassName(mref);
            if (cname.equals(thisClassName) != skipThis > 0) break;
            return index;
        }
        this.begin();
        return -1;
    }

    public int insert(byte[] code) throws BadBytecode {
        return this.insert0(this.currentPos, code, false);
    }

    public void insert(int pos, byte[] code) throws BadBytecode {
        this.insert0(pos, code, false);
    }

    public int insertAt(int pos, byte[] code) throws BadBytecode {
        return this.insert0(pos, code, false);
    }

    public int insertEx(byte[] code) throws BadBytecode {
        return this.insert0(this.currentPos, code, true);
    }

    public void insertEx(int pos, byte[] code) throws BadBytecode {
        this.insert0(pos, code, true);
    }

    public int insertExAt(int pos, byte[] code) throws BadBytecode {
        return this.insert0(pos, code, true);
    }

    private int insert0(int pos, byte[] code, boolean exclusive) throws BadBytecode {
        int len = code.length;
        if (len <= 0) {
            return pos;
        }
        int p2 = pos = this.insertGapAt((int)pos, (int)len, (boolean)exclusive).position;
        for (int j2 = 0; j2 < len; ++j2) {
            this.bytecode[p2++] = code[j2];
        }
        return pos;
    }

    public int insertGap(int length) throws BadBytecode {
        return this.insertGapAt((int)this.currentPos, (int)length, (boolean)false).position;
    }

    public int insertGap(int pos, int length) throws BadBytecode {
        return this.insertGapAt((int)pos, (int)length, (boolean)false).length;
    }

    public int insertExGap(int length) throws BadBytecode {
        return this.insertGapAt((int)this.currentPos, (int)length, (boolean)true).position;
    }

    public int insertExGap(int pos, int length) throws BadBytecode {
        return this.insertGapAt((int)pos, (int)length, (boolean)true).length;
    }

    public Gap insertGapAt(int pos, int length, boolean exclusive) throws BadBytecode {
        int length2;
        byte[] c2;
        Gap gap = new Gap();
        if (length <= 0) {
            gap.position = pos;
            gap.length = 0;
            return gap;
        }
        if (this.bytecode.length + length > Short.MAX_VALUE) {
            c2 = this.insertGapCore0w(this.bytecode, pos, length, exclusive, this.get().getExceptionTable(), this.codeAttr, gap);
            pos = gap.position;
            length2 = length;
        } else {
            int cur = this.currentPos;
            c2 = CodeIterator.insertGapCore0(this.bytecode, pos, length, exclusive, this.get().getExceptionTable(), this.codeAttr);
            length2 = c2.length - this.bytecode.length;
            gap.position = pos;
            gap.length = length2;
            if (cur >= pos) {
                this.currentPos = cur + length2;
            }
            if (this.mark > pos || this.mark == pos && exclusive) {
                this.mark += length2;
            }
            if (this.mark2 > pos || this.mark2 == pos && exclusive) {
                this.mark2 += length2;
            }
        }
        this.codeAttr.setCode(c2);
        this.bytecode = c2;
        this.endPos = this.getCodeLength();
        this.updateCursors(pos, length2);
        return gap;
    }

    protected void updateCursors(int pos, int length) {
    }

    public void insert(ExceptionTable et2, int offset) {
        this.codeAttr.getExceptionTable().add(0, et2, offset);
    }

    public int append(byte[] code) {
        int size = this.getCodeLength();
        int len = code.length;
        if (len <= 0) {
            return size;
        }
        this.appendGap(len);
        byte[] dest = this.bytecode;
        for (int i2 = 0; i2 < len; ++i2) {
            dest[i2 + size] = code[i2];
        }
        return size;
    }

    public void appendGap(int gapLength) {
        int i2;
        byte[] code = this.bytecode;
        int codeLength = code.length;
        byte[] newcode = new byte[codeLength + gapLength];
        for (i2 = 0; i2 < codeLength; ++i2) {
            newcode[i2] = code[i2];
        }
        for (i2 = codeLength; i2 < codeLength + gapLength; ++i2) {
            newcode[i2] = 0;
        }
        this.codeAttr.setCode(newcode);
        this.bytecode = newcode;
        this.endPos = this.getCodeLength();
    }

    public void append(ExceptionTable et2, int offset) {
        ExceptionTable table = this.codeAttr.getExceptionTable();
        table.add(table.size(), et2, offset);
    }

    static int nextOpcode(byte[] code, int index) throws BadBytecode {
        int opcode;
        try {
            opcode = code[index] & 0xFF;
        }
        catch (IndexOutOfBoundsException e2) {
            throw new BadBytecode("invalid opcode address");
        }
        try {
            int len = opcodeLength[opcode];
            if (len > 0) {
                return index + len;
            }
            if (opcode == 196) {
                if (code[index + 1] == -124) {
                    return index + 6;
                }
                return index + 4;
            }
            int index2 = (index & 0xFFFFFFFC) + 8;
            if (opcode == 171) {
                int npairs = ByteArray.read32bit(code, index2);
                return index2 + npairs * 8 + 4;
            }
            if (opcode == 170) {
                int low = ByteArray.read32bit(code, index2);
                int high = ByteArray.read32bit(code, index2 + 4);
                return index2 + (high - low + 1) * 4 + 8;
            }
        }
        catch (IndexOutOfBoundsException indexOutOfBoundsException) {
            // empty catch block
        }
        throw new BadBytecode(opcode);
    }

    static byte[] insertGapCore0(byte[] code, int where, int gapLength, boolean exclusive, ExceptionTable etable, CodeAttribute ca2) throws BadBytecode {
        if (gapLength <= 0) {
            return code;
        }
        try {
            return CodeIterator.insertGapCore1(code, where, gapLength, exclusive, etable, ca2);
        }
        catch (AlignmentException e2) {
            try {
                return CodeIterator.insertGapCore1(code, where, gapLength + 3 & 0xFFFFFFFC, exclusive, etable, ca2);
            }
            catch (AlignmentException e22) {
                throw new RuntimeException("fatal error?");
            }
        }
    }

    private static byte[] insertGapCore1(byte[] code, int where, int gapLength, boolean exclusive, ExceptionTable etable, CodeAttribute ca2) throws BadBytecode, AlignmentException {
        StackMap sm2;
        StackMapTable smt;
        LocalVariableAttribute vta;
        LocalVariableAttribute va2;
        int codeLength = code.length;
        byte[] newcode = new byte[codeLength + gapLength];
        CodeIterator.insertGap2(code, where, gapLength, codeLength, newcode, exclusive);
        etable.shiftPc(where, gapLength, exclusive);
        LineNumberAttribute na2 = (LineNumberAttribute)ca2.getAttribute("LineNumberTable");
        if (na2 != null) {
            na2.shiftPc(where, gapLength, exclusive);
        }
        if ((va2 = (LocalVariableAttribute)ca2.getAttribute("LocalVariableTable")) != null) {
            va2.shiftPc(where, gapLength, exclusive);
        }
        if ((vta = (LocalVariableAttribute)ca2.getAttribute("LocalVariableTypeTable")) != null) {
            vta.shiftPc(where, gapLength, exclusive);
        }
        if ((smt = (StackMapTable)ca2.getAttribute("StackMapTable")) != null) {
            smt.shiftPc(where, gapLength, exclusive);
        }
        if ((sm2 = (StackMap)ca2.getAttribute("StackMap")) != null) {
            sm2.shiftPc(where, gapLength, exclusive);
        }
        return newcode;
    }

    private static void insertGap2(byte[] code, int where, int gapLength, int endPos, byte[] newcode, boolean exclusive) throws BadBytecode, AlignmentException {
        int i2 = 0;
        int j2 = 0;
        while (i2 < endPos) {
            int defaultbyte;
            int i22;
            int offset;
            if (i2 == where) {
                int j22 = j2 + gapLength;
                while (j2 < j22) {
                    newcode[j2++] = 0;
                }
            }
            int nextPos = CodeIterator.nextOpcode(code, i2);
            int inst = code[i2] & 0xFF;
            if (153 <= inst && inst <= 168 || inst == 198 || inst == 199) {
                offset = code[i2 + 1] << 8 | code[i2 + 2] & 0xFF;
                offset = CodeIterator.newOffset(i2, offset, where, gapLength, exclusive);
                newcode[j2] = code[i2];
                ByteArray.write16bit(offset, newcode, j2 + 1);
                j2 += 3;
            } else if (inst == 200 || inst == 201) {
                offset = ByteArray.read32bit(code, i2 + 1);
                offset = CodeIterator.newOffset(i2, offset, where, gapLength, exclusive);
                newcode[j2++] = code[i2];
                ByteArray.write32bit(offset, newcode, j2);
                j2 += 4;
            } else if (inst == 170) {
                if (i2 != j2 && (gapLength & 3) != 0) {
                    throw new AlignmentException();
                }
                i22 = (i2 & 0xFFFFFFFC) + 4;
                j2 = CodeIterator.copyGapBytes(newcode, j2, code, i2, i22);
                defaultbyte = CodeIterator.newOffset(i2, ByteArray.read32bit(code, i22), where, gapLength, exclusive);
                ByteArray.write32bit(defaultbyte, newcode, j2);
                int lowbyte = ByteArray.read32bit(code, i22 + 4);
                ByteArray.write32bit(lowbyte, newcode, j2 + 4);
                int highbyte = ByteArray.read32bit(code, i22 + 8);
                ByteArray.write32bit(highbyte, newcode, j2 + 8);
                j2 += 12;
                int i0 = i22 + 12;
                i22 = i0 + (highbyte - lowbyte + 1) * 4;
                while (i0 < i22) {
                    int offset2 = CodeIterator.newOffset(i2, ByteArray.read32bit(code, i0), where, gapLength, exclusive);
                    ByteArray.write32bit(offset2, newcode, j2);
                    j2 += 4;
                    i0 += 4;
                }
            } else if (inst == 171) {
                if (i2 != j2 && (gapLength & 3) != 0) {
                    throw new AlignmentException();
                }
                i22 = (i2 & 0xFFFFFFFC) + 4;
                j2 = CodeIterator.copyGapBytes(newcode, j2, code, i2, i22);
                defaultbyte = CodeIterator.newOffset(i2, ByteArray.read32bit(code, i22), where, gapLength, exclusive);
                ByteArray.write32bit(defaultbyte, newcode, j2);
                int npairs = ByteArray.read32bit(code, i22 + 4);
                ByteArray.write32bit(npairs, newcode, j2 + 4);
                j2 += 8;
                int i0 = i22 + 8;
                i22 = i0 + npairs * 8;
                while (i0 < i22) {
                    ByteArray.copy32bit(code, i0, newcode, j2);
                    int offset3 = CodeIterator.newOffset(i2, ByteArray.read32bit(code, i0 + 4), where, gapLength, exclusive);
                    ByteArray.write32bit(offset3, newcode, j2 + 4);
                    j2 += 8;
                    i0 += 8;
                }
            } else {
                while (i2 < nextPos) {
                    newcode[j2++] = code[i2++];
                }
            }
            i2 = nextPos;
        }
    }

    private static int copyGapBytes(byte[] newcode, int j2, byte[] code, int i2, int iEnd) {
        switch (iEnd - i2) {
            case 4: {
                newcode[j2++] = code[i2++];
            }
            case 3: {
                newcode[j2++] = code[i2++];
            }
            case 2: {
                newcode[j2++] = code[i2++];
            }
            case 1: {
                newcode[j2++] = code[i2++];
            }
        }
        return j2;
    }

    private static int newOffset(int i2, int offset, int where, int gapLength, boolean exclusive) {
        int target = i2 + offset;
        if (i2 < where) {
            if (where < target || exclusive && where == target) {
                offset += gapLength;
            }
        } else if (i2 == where) {
            if (target < where) {
                offset -= gapLength;
            }
        } else if (target < where || !exclusive && where == target) {
            offset -= gapLength;
        }
        return offset;
    }

    static byte[] changeLdcToLdcW(byte[] code, ExceptionTable etable, CodeAttribute ca2, CodeAttribute.LdcEntry ldcs) throws BadBytecode {
        Pointers pointers = new Pointers(0, 0, 0, 0, etable, ca2);
        List<Branch> jumps = CodeIterator.makeJumpList(code, code.length, pointers);
        while (ldcs != null) {
            CodeIterator.addLdcW(ldcs, jumps);
            ldcs = ldcs.next;
        }
        byte[] r2 = CodeIterator.insertGap2w(code, 0, 0, false, jumps, pointers);
        return r2;
    }

    private static void addLdcW(CodeAttribute.LdcEntry ldcs, List<Branch> jumps) {
        int where = ldcs.where;
        LdcW ldcw = new LdcW(where, ldcs.index);
        int s2 = jumps.size();
        for (int i2 = 0; i2 < s2; ++i2) {
            if (where >= jumps.get((int)i2).orgPos) continue;
            jumps.add(i2, ldcw);
            return;
        }
        jumps.add(ldcw);
    }

    private byte[] insertGapCore0w(byte[] code, int where, int gapLength, boolean exclusive, ExceptionTable etable, CodeAttribute ca2, Gap newWhere) throws BadBytecode {
        if (gapLength <= 0) {
            return code;
        }
        Pointers pointers = new Pointers(this.currentPos, this.mark, this.mark2, where, etable, ca2);
        List<Branch> jumps = CodeIterator.makeJumpList(code, code.length, pointers);
        byte[] r2 = CodeIterator.insertGap2w(code, where, gapLength, exclusive, jumps, pointers);
        this.currentPos = pointers.cursor;
        this.mark = pointers.mark;
        this.mark2 = pointers.mark2;
        int where2 = pointers.mark0;
        if (where2 == this.currentPos && !exclusive) {
            this.currentPos += gapLength;
        }
        if (exclusive) {
            where2 -= gapLength;
        }
        newWhere.position = where2;
        newWhere.length = gapLength;
        return r2;
    }

    /*
     * Unable to fully structure code
     */
    private static byte[] insertGap2w(byte[] code, int where, int gapLength, boolean exclusive, List<Branch> jumps, Pointers ptrs) throws BadBytecode {
        if (gapLength > 0) {
            ptrs.shiftPc(where, gapLength, exclusive);
            for (Branch b : jumps) {
                b.shift(where, gapLength, exclusive);
            }
        }
        unstable = true;
        block1: while (true) {
            if (unstable) {
                unstable = false;
                var7_8 = jumps.iterator();
                block2: while (true) {
                    if (!var7_8.hasNext()) continue block1;
                    b = var7_8.next();
                    if (!b.expanded()) continue;
                    unstable = true;
                    p = b.pos;
                    delta = b.deltaSize();
                    ptrs.shiftPc(p, delta, false);
                    var11_12 = jumps.iterator();
                    while (true) {
                        if (var11_12.hasNext()) ** break;
                        continue block2;
                        bb = var11_12.next();
                        bb.shift(p, delta, false);
                    }
                    break;
                }
            }
            for (Branch b : jumps) {
                diff = b.gapChanged();
                if (diff <= 0) continue;
                unstable = true;
                p = b.pos;
                ptrs.shiftPc(p, diff, false);
                for (Branch bb : jumps) {
                    bb.shift(p, diff, false);
                }
            }
            if (!unstable) break;
        }
        return CodeIterator.makeExapndedCode(code, jumps, where, gapLength);
    }

    private static List<Branch> makeJumpList(byte[] code, int endPos, Pointers ptrs) throws BadBytecode {
        ArrayList<Branch> jumps = new ArrayList<Branch>();
        int i2 = 0;
        while (i2 < endPos) {
            int i22;
            int offset;
            int nextPos = CodeIterator.nextOpcode(code, i2);
            int inst = code[i2] & 0xFF;
            if (153 <= inst && inst <= 168 || inst == 198 || inst == 199) {
                offset = code[i2 + 1] << 8 | code[i2 + 2] & 0xFF;
                Branch16 b2 = inst == 167 || inst == 168 ? new Jump16(i2, offset) : new If16(i2, offset);
                jumps.add(b2);
            } else if (inst == 200 || inst == 201) {
                offset = ByteArray.read32bit(code, i2 + 1);
                jumps.add(new Jump32(i2, offset));
            } else if (inst == 170) {
                i22 = (i2 & 0xFFFFFFFC) + 4;
                int defaultbyte = ByteArray.read32bit(code, i22);
                int lowbyte = ByteArray.read32bit(code, i22 + 4);
                int highbyte = ByteArray.read32bit(code, i22 + 8);
                int i0 = i22 + 12;
                int size = highbyte - lowbyte + 1;
                int[] offsets = new int[size];
                for (int j2 = 0; j2 < size; ++j2) {
                    offsets[j2] = ByteArray.read32bit(code, i0);
                    i0 += 4;
                }
                jumps.add(new Table(i2, defaultbyte, lowbyte, highbyte, offsets, ptrs));
            } else if (inst == 171) {
                i22 = (i2 & 0xFFFFFFFC) + 4;
                int defaultbyte = ByteArray.read32bit(code, i22);
                int npairs = ByteArray.read32bit(code, i22 + 4);
                int i0 = i22 + 8;
                int[] matches = new int[npairs];
                int[] offsets = new int[npairs];
                for (int j3 = 0; j3 < npairs; ++j3) {
                    matches[j3] = ByteArray.read32bit(code, i0);
                    offsets[j3] = ByteArray.read32bit(code, i0 + 4);
                    i0 += 8;
                }
                jumps.add(new Lookup(i2, defaultbyte, matches, offsets, ptrs));
            }
            i2 = nextPos;
        }
        return jumps;
    }

    private static byte[] makeExapndedCode(byte[] code, List<Branch> jumps, int where, int gapLength) throws BadBytecode {
        int bpos;
        Branch b2;
        int n2 = jumps.size();
        int size = code.length + gapLength;
        for (Branch b3 : jumps) {
            size += b3.deltaSize();
        }
        byte[] newcode = new byte[size];
        int src = 0;
        int dest = 0;
        int bindex = 0;
        int len = code.length;
        if (0 < n2) {
            b2 = jumps.get(0);
            bpos = b2.orgPos;
        } else {
            b2 = null;
            bpos = len;
        }
        while (src < len) {
            if (src == where) {
                int pos2 = dest + gapLength;
                while (dest < pos2) {
                    newcode[dest++] = 0;
                }
            }
            if (src != bpos) {
                newcode[dest++] = code[src++];
                continue;
            }
            int s2 = b2.write(src, code, dest, newcode);
            src += s2;
            dest += s2 + b2.deltaSize();
            if (++bindex < n2) {
                b2 = jumps.get(bindex);
                bpos = b2.orgPos;
                continue;
            }
            b2 = null;
            bpos = len;
        }
        return newcode;
    }

    static class Lookup
    extends Switcher {
        int[] matches;

        Lookup(int pos, int defaultByte, int[] matches, int[] offsets, Pointers ptrs) {
            super(pos, defaultByte, offsets, ptrs);
            this.matches = matches;
        }

        @Override
        int write2(int dest, byte[] newcode) {
            int n2 = this.matches.length;
            ByteArray.write32bit(n2, newcode, dest);
            dest += 4;
            for (int i2 = 0; i2 < n2; ++i2) {
                ByteArray.write32bit(this.matches[i2], newcode, dest);
                ByteArray.write32bit(this.offsets[i2], newcode, dest + 4);
                dest += 8;
            }
            return 4 + 8 * n2;
        }

        @Override
        int tableSize() {
            return 4 + 8 * this.matches.length;
        }
    }

    static class Table
    extends Switcher {
        int low;
        int high;

        Table(int pos, int defaultByte, int low, int high, int[] offsets, Pointers ptrs) {
            super(pos, defaultByte, offsets, ptrs);
            this.low = low;
            this.high = high;
        }

        @Override
        int write2(int dest, byte[] newcode) {
            ByteArray.write32bit(this.low, newcode, dest);
            ByteArray.write32bit(this.high, newcode, dest + 4);
            int n2 = this.offsets.length;
            dest += 8;
            for (int i2 = 0; i2 < n2; ++i2) {
                ByteArray.write32bit(this.offsets[i2], newcode, dest);
                dest += 4;
            }
            return 8 + 4 * n2;
        }

        @Override
        int tableSize() {
            return 8 + 4 * this.offsets.length;
        }
    }

    static abstract class Switcher
    extends Branch {
        int gap;
        int defaultByte;
        int[] offsets;
        Pointers pointers;

        Switcher(int pos, int defaultByte, int[] offsets, Pointers ptrs) {
            super(pos);
            this.gap = 3 - (pos & 3);
            this.defaultByte = defaultByte;
            this.offsets = offsets;
            this.pointers = ptrs;
        }

        @Override
        void shift(int where, int gapLength, boolean exclusive) {
            int p2 = this.pos;
            this.defaultByte = Switcher.shiftOffset(p2, this.defaultByte, where, gapLength, exclusive);
            int num = this.offsets.length;
            for (int i2 = 0; i2 < num; ++i2) {
                this.offsets[i2] = Switcher.shiftOffset(p2, this.offsets[i2], where, gapLength, exclusive);
            }
            super.shift(where, gapLength, exclusive);
        }

        @Override
        int gapChanged() {
            int newGap = 3 - (this.pos & 3);
            if (newGap > this.gap) {
                int diff = newGap - this.gap;
                this.gap = newGap;
                return diff;
            }
            return 0;
        }

        @Override
        int deltaSize() {
            return this.gap - (3 - (this.orgPos & 3));
        }

        @Override
        int write(int src, byte[] code, int dest, byte[] newcode) throws BadBytecode {
            int padding = 3 - (this.pos & 3);
            int nops = this.gap - padding;
            int bytecodeSize = 5 + (3 - (this.orgPos & 3)) + this.tableSize();
            if (nops > 0) {
                this.adjustOffsets(bytecodeSize, nops);
            }
            newcode[dest++] = code[src];
            while (padding-- > 0) {
                newcode[dest++] = 0;
            }
            ByteArray.write32bit(this.defaultByte, newcode, dest);
            int size = this.write2(dest + 4, newcode);
            dest += size + 4;
            while (nops-- > 0) {
                newcode[dest++] = 0;
            }
            return 5 + (3 - (this.orgPos & 3)) + size;
        }

        abstract int write2(int var1, byte[] var2);

        abstract int tableSize();

        void adjustOffsets(int size, int nops) throws BadBytecode {
            this.pointers.shiftForSwitch(this.pos + size, nops);
            if (this.defaultByte == size) {
                this.defaultByte -= nops;
            }
            for (int i2 = 0; i2 < this.offsets.length; ++i2) {
                if (this.offsets[i2] != size) continue;
                int n2 = i2;
                this.offsets[n2] = this.offsets[n2] - nops;
            }
        }
    }

    static class Jump32
    extends Branch {
        int offset;

        Jump32(int p2, int off) {
            super(p2);
            this.offset = off;
        }

        @Override
        void shift(int where, int gapLength, boolean exclusive) {
            this.offset = Jump32.shiftOffset(this.pos, this.offset, where, gapLength, exclusive);
            super.shift(where, gapLength, exclusive);
        }

        @Override
        int write(int src, byte[] code, int dest, byte[] newcode) {
            newcode[dest] = code[src];
            ByteArray.write32bit(this.offset, newcode, dest + 1);
            return 5;
        }
    }

    static class If16
    extends Branch16 {
        If16(int p2, int off) {
            super(p2, off);
        }

        @Override
        int deltaSize() {
            return this.state == 2 ? 5 : 0;
        }

        @Override
        void write32(int src, byte[] code, int dest, byte[] newcode) {
            newcode[dest] = (byte)this.opcode(code[src] & 0xFF);
            newcode[dest + 1] = 0;
            newcode[dest + 2] = 8;
            newcode[dest + 3] = -56;
            ByteArray.write32bit(this.offset - 3, newcode, dest + 4);
        }

        int opcode(int op2) {
            if (op2 == 198) {
                return 199;
            }
            if (op2 == 199) {
                return 198;
            }
            if ((op2 - 153 & 1) == 0) {
                return op2 + 1;
            }
            return op2 - 1;
        }
    }

    static class Jump16
    extends Branch16 {
        Jump16(int p2, int off) {
            super(p2, off);
        }

        @Override
        int deltaSize() {
            return this.state == 2 ? 2 : 0;
        }

        @Override
        void write32(int src, byte[] code, int dest, byte[] newcode) {
            newcode[dest] = (byte)((code[src] & 0xFF) == 167 ? 200 : 201);
            ByteArray.write32bit(this.offset, newcode, dest + 1);
        }
    }

    static abstract class Branch16
    extends Branch {
        int offset;
        int state;
        static final int BIT16 = 0;
        static final int EXPAND = 1;
        static final int BIT32 = 2;

        Branch16(int p2, int off) {
            super(p2);
            this.offset = off;
            this.state = 0;
        }

        @Override
        void shift(int where, int gapLength, boolean exclusive) {
            this.offset = Branch16.shiftOffset(this.pos, this.offset, where, gapLength, exclusive);
            super.shift(where, gapLength, exclusive);
            if (this.state == 0 && (this.offset < Short.MIN_VALUE || Short.MAX_VALUE < this.offset)) {
                this.state = 1;
            }
        }

        @Override
        boolean expanded() {
            if (this.state == 1) {
                this.state = 2;
                return true;
            }
            return false;
        }

        @Override
        abstract int deltaSize();

        abstract void write32(int var1, byte[] var2, int var3, byte[] var4);

        @Override
        int write(int src, byte[] code, int dest, byte[] newcode) {
            if (this.state == 2) {
                this.write32(src, code, dest, newcode);
            } else {
                newcode[dest] = code[src];
                ByteArray.write16bit(this.offset, newcode, dest + 1);
            }
            return 3;
        }
    }

    static class LdcW
    extends Branch {
        int index;
        boolean state;

        LdcW(int p2, int i2) {
            super(p2);
            this.index = i2;
            this.state = true;
        }

        @Override
        boolean expanded() {
            if (this.state) {
                this.state = false;
                return true;
            }
            return false;
        }

        @Override
        int deltaSize() {
            return 1;
        }

        @Override
        int write(int srcPos, byte[] code, int destPos, byte[] newcode) {
            newcode[destPos] = 19;
            ByteArray.write16bit(this.index, newcode, destPos + 1);
            return 2;
        }
    }

    static abstract class Branch {
        int pos;
        int orgPos;

        Branch(int p2) {
            this.pos = this.orgPos = p2;
        }

        void shift(int where, int gapLength, boolean exclusive) {
            if (where < this.pos || where == this.pos && exclusive) {
                this.pos += gapLength;
            }
        }

        static int shiftOffset(int i2, int offset, int where, int gapLength, boolean exclusive) {
            int target = i2 + offset;
            if (i2 < where) {
                if (where < target || exclusive && where == target) {
                    offset += gapLength;
                }
            } else if (i2 == where) {
                if (target < where && exclusive) {
                    offset -= gapLength;
                } else if (where < target && !exclusive) {
                    offset += gapLength;
                }
            } else if (target < where || !exclusive && where == target) {
                offset -= gapLength;
            }
            return offset;
        }

        boolean expanded() {
            return false;
        }

        int gapChanged() {
            return 0;
        }

        int deltaSize() {
            return 0;
        }

        abstract int write(int var1, byte[] var2, int var3, byte[] var4) throws BadBytecode;
    }

    static class Pointers {
        int cursor;
        int mark0;
        int mark;
        int mark2;
        ExceptionTable etable;
        LineNumberAttribute line;
        LocalVariableAttribute vars;
        LocalVariableAttribute types;
        StackMapTable stack;
        StackMap stack2;

        Pointers(int cur, int m2, int m22, int m0, ExceptionTable et2, CodeAttribute ca2) {
            this.cursor = cur;
            this.mark = m2;
            this.mark2 = m22;
            this.mark0 = m0;
            this.etable = et2;
            this.line = (LineNumberAttribute)ca2.getAttribute("LineNumberTable");
            this.vars = (LocalVariableAttribute)ca2.getAttribute("LocalVariableTable");
            this.types = (LocalVariableAttribute)ca2.getAttribute("LocalVariableTypeTable");
            this.stack = (StackMapTable)ca2.getAttribute("StackMapTable");
            this.stack2 = (StackMap)ca2.getAttribute("StackMap");
        }

        void shiftPc(int where, int gapLength, boolean exclusive) throws BadBytecode {
            if (where < this.cursor || where == this.cursor && exclusive) {
                this.cursor += gapLength;
            }
            if (where < this.mark || where == this.mark && exclusive) {
                this.mark += gapLength;
            }
            if (where < this.mark2 || where == this.mark2 && exclusive) {
                this.mark2 += gapLength;
            }
            if (where < this.mark0 || where == this.mark0 && exclusive) {
                this.mark0 += gapLength;
            }
            this.etable.shiftPc(where, gapLength, exclusive);
            if (this.line != null) {
                this.line.shiftPc(where, gapLength, exclusive);
            }
            if (this.vars != null) {
                this.vars.shiftPc(where, gapLength, exclusive);
            }
            if (this.types != null) {
                this.types.shiftPc(where, gapLength, exclusive);
            }
            if (this.stack != null) {
                this.stack.shiftPc(where, gapLength, exclusive);
            }
            if (this.stack2 != null) {
                this.stack2.shiftPc(where, gapLength, exclusive);
            }
        }

        void shiftForSwitch(int where, int gapLength) throws BadBytecode {
            if (this.stack != null) {
                this.stack.shiftForSwitch(where, gapLength);
            }
            if (this.stack2 != null) {
                this.stack2.shiftForSwitch(where, gapLength);
            }
        }
    }

    static class AlignmentException
    extends Exception {
        private static final long serialVersionUID = 1L;

        AlignmentException() {
        }
    }

    public static class Gap {
        public int position;
        public int length;
    }
}

