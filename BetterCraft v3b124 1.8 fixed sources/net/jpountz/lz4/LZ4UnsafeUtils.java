/*
 * Decompiled with CFR 0.152.
 */
package net.jpountz.lz4;

import java.nio.ByteOrder;
import net.jpountz.lz4.LZ4Exception;
import net.jpountz.lz4.LZ4SafeUtils;
import net.jpountz.util.UnsafeUtils;
import net.jpountz.util.Utils;

final class LZ4UnsafeUtils
extends Enum<LZ4UnsafeUtils> {
    private static final /* synthetic */ LZ4UnsafeUtils[] $VALUES;

    public static LZ4UnsafeUtils[] values() {
        return (LZ4UnsafeUtils[])$VALUES.clone();
    }

    public static LZ4UnsafeUtils valueOf(String name) {
        return Enum.valueOf(LZ4UnsafeUtils.class, name);
    }

    static void safeArraycopy(byte[] src, int srcOff, byte[] dest, int destOff, int len) {
        int fastLen = len & 0xFFFFFFF8;
        LZ4UnsafeUtils.wildArraycopy(src, srcOff, dest, destOff, fastLen);
        int slowLen = len & 7;
        for (int i2 = 0; i2 < slowLen; ++i2) {
            UnsafeUtils.writeByte(dest, destOff + fastLen + i2, UnsafeUtils.readByte(src, srcOff + fastLen + i2));
        }
    }

    static void wildArraycopy(byte[] src, int srcOff, byte[] dest, int destOff, int len) {
        for (int i2 = 0; i2 < len; i2 += 8) {
            UnsafeUtils.writeLong(dest, destOff + i2, UnsafeUtils.readLong(src, srcOff + i2));
        }
    }

    static void wildIncrementalCopy(byte[] dest, int matchOff, int dOff, int matchCopyEnd) {
        if (dOff - matchOff < 4) {
            for (int i2 = 0; i2 < 4; ++i2) {
                UnsafeUtils.writeByte(dest, dOff + i2, UnsafeUtils.readByte(dest, matchOff + i2));
            }
            int dec = 0;
            assert ((dOff += 4) >= (matchOff += 4) && dOff - matchOff < 8);
            switch (dOff - matchOff) {
                case 1: {
                    matchOff -= 3;
                    break;
                }
                case 2: {
                    matchOff -= 2;
                    break;
                }
                case 3: {
                    matchOff -= 3;
                    dec = -1;
                    break;
                }
                case 5: {
                    dec = 1;
                    break;
                }
                case 6: {
                    dec = 2;
                    break;
                }
                case 7: {
                    dec = 3;
                    break;
                }
            }
            UnsafeUtils.writeInt(dest, dOff, UnsafeUtils.readInt(dest, matchOff));
            dOff += 4;
            matchOff -= dec;
        } else if (dOff - matchOff < 8) {
            UnsafeUtils.writeLong(dest, dOff, UnsafeUtils.readLong(dest, matchOff));
            dOff += dOff - matchOff;
        }
        while (dOff < matchCopyEnd) {
            UnsafeUtils.writeLong(dest, dOff, UnsafeUtils.readLong(dest, matchOff));
            dOff += 8;
            matchOff += 8;
        }
    }

    static void safeIncrementalCopy(byte[] dest, int matchOff, int dOff, int matchLen) {
        for (int i2 = 0; i2 < matchLen; ++i2) {
            dest[dOff + i2] = dest[matchOff + i2];
            UnsafeUtils.writeByte(dest, dOff + i2, UnsafeUtils.readByte(dest, matchOff + i2));
        }
    }

    static int readShortLittleEndian(byte[] src, int srcOff) {
        short s2 = UnsafeUtils.readShort(src, srcOff);
        if (Utils.NATIVE_BYTE_ORDER == ByteOrder.BIG_ENDIAN) {
            s2 = Short.reverseBytes(s2);
        }
        return s2 & 0xFFFF;
    }

    static void writeShortLittleEndian(byte[] dest, int destOff, int value) {
        short s2 = (short)value;
        if (Utils.NATIVE_BYTE_ORDER == ByteOrder.BIG_ENDIAN) {
            s2 = Short.reverseBytes(s2);
        }
        UnsafeUtils.writeShort(dest, destOff, s2);
    }

    static boolean readIntEquals(byte[] src, int ref, int sOff) {
        return UnsafeUtils.readInt(src, ref) == UnsafeUtils.readInt(src, sOff);
    }

    static int commonBytes(byte[] src, int ref, int sOff, int srcLimit) {
        int matchLen = 0;
        while (sOff <= srcLimit - 8) {
            if (UnsafeUtils.readLong(src, sOff) == UnsafeUtils.readLong(src, ref)) {
                matchLen += 8;
                ref += 8;
                sOff += 8;
                continue;
            }
            int zeroBits = Utils.NATIVE_BYTE_ORDER == ByteOrder.BIG_ENDIAN ? Long.numberOfLeadingZeros(UnsafeUtils.readLong(src, sOff) ^ UnsafeUtils.readLong(src, ref)) : Long.numberOfTrailingZeros(UnsafeUtils.readLong(src, sOff) ^ UnsafeUtils.readLong(src, ref));
            return matchLen + (zeroBits >>> 3);
        }
        while (sOff < srcLimit && UnsafeUtils.readByte(src, ref++) == UnsafeUtils.readByte(src, sOff++)) {
            ++matchLen;
        }
        return matchLen;
    }

    static int writeLen(int len, byte[] dest, int dOff) {
        while (len >= 255) {
            UnsafeUtils.writeByte(dest, dOff++, 255);
            len -= 255;
        }
        UnsafeUtils.writeByte(dest, dOff++, len);
        return dOff;
    }

    static int encodeSequence(byte[] src, int anchor, int matchOff, int matchRef, int matchLen, byte[] dest, int dOff, int destEnd) {
        int token;
        int runLen = matchOff - anchor;
        int tokenOff = dOff++;
        if (runLen >= 15) {
            token = -16;
            dOff = LZ4UnsafeUtils.writeLen(runLen - 15, dest, dOff);
        } else {
            token = runLen << 4;
        }
        LZ4UnsafeUtils.wildArraycopy(src, anchor, dest, dOff, runLen);
        dOff += runLen;
        int matchDec = matchOff - matchRef;
        dest[dOff++] = (byte)matchDec;
        dest[dOff++] = (byte)(matchDec >>> 8);
        if (dOff + 6 + ((matchLen -= 4) >>> 8) > destEnd) {
            throw new LZ4Exception("maxDestLen is too small");
        }
        if (matchLen >= 15) {
            token |= 0xF;
            dOff = LZ4UnsafeUtils.writeLen(matchLen - 15, dest, dOff);
        } else {
            token |= matchLen;
        }
        dest[tokenOff] = (byte)token;
        return dOff;
    }

    static int commonBytesBackward(byte[] b2, int o1, int o2, int l1, int l2) {
        int count = 0;
        while (o1 > l1 && o2 > l2 && UnsafeUtils.readByte(b2, --o1) == UnsafeUtils.readByte(b2, --o2)) {
            ++count;
        }
        return count;
    }

    static int lastLiterals(byte[] src, int sOff, int srcLen, byte[] dest, int dOff, int destEnd) {
        return LZ4SafeUtils.lastLiterals(src, sOff, srcLen, dest, dOff, destEnd);
    }

    static {
        $VALUES = new LZ4UnsafeUtils[0];
    }
}

