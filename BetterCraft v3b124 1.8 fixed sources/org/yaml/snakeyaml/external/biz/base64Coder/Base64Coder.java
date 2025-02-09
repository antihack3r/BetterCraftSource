/*
 * Decompiled with CFR 0.152.
 */
package org.yaml.snakeyaml.external.biz.base64Coder;

public class Base64Coder {
    private static final String systemLineSeparator;
    private static final char[] map1;
    private static final byte[] map2;

    public static String encodeString(String s2) {
        return new String(Base64Coder.encode(s2.getBytes()));
    }

    public static String encodeLines(byte[] in2) {
        return Base64Coder.encodeLines(in2, 0, in2.length, 76, systemLineSeparator);
    }

    public static String encodeLines(byte[] in2, int iOff, int iLen, int lineLen, String lineSeparator) {
        int l2;
        int blockLen = lineLen * 3 / 4;
        if (blockLen <= 0) {
            throw new IllegalArgumentException();
        }
        int lines = (iLen + blockLen - 1) / blockLen;
        int bufLen = (iLen + 2) / 3 * 4 + lines * lineSeparator.length();
        StringBuilder buf = new StringBuilder(bufLen);
        for (int ip2 = 0; ip2 < iLen; ip2 += l2) {
            l2 = Math.min(iLen - ip2, blockLen);
            buf.append(Base64Coder.encode(in2, iOff + ip2, l2));
            buf.append(lineSeparator);
        }
        return buf.toString();
    }

    public static char[] encode(byte[] in2) {
        return Base64Coder.encode(in2, 0, in2.length);
    }

    public static char[] encode(byte[] in2, int iLen) {
        return Base64Coder.encode(in2, 0, iLen);
    }

    public static char[] encode(byte[] in2, int iOff, int iLen) {
        int oDataLen = (iLen * 4 + 2) / 3;
        int oLen = (iLen + 2) / 3 * 4;
        char[] out = new char[oLen];
        int ip2 = iOff;
        int iEnd = iOff + iLen;
        int op2 = 0;
        while (ip2 < iEnd) {
            int i0 = in2[ip2++] & 0xFF;
            int i1 = ip2 < iEnd ? in2[ip2++] & 0xFF : 0;
            int i2 = ip2 < iEnd ? in2[ip2++] & 0xFF : 0;
            int o0 = i0 >>> 2;
            int o1 = (i0 & 3) << 4 | i1 >>> 4;
            int o2 = (i1 & 0xF) << 2 | i2 >>> 6;
            int o3 = i2 & 0x3F;
            out[op2++] = map1[o0];
            out[op2++] = map1[o1];
            out[op2] = op2 < oDataLen ? map1[o2] : 61;
            int n2 = ++op2 < oDataLen ? map1[o3] : 61;
            out[op2] = n2;
            ++op2;
        }
        return out;
    }

    public static String decodeString(String s2) {
        return new String(Base64Coder.decode(s2));
    }

    public static byte[] decodeLines(String s2) {
        char[] buf = new char[s2.length()];
        int p2 = 0;
        for (int ip2 = 0; ip2 < s2.length(); ++ip2) {
            char c2 = s2.charAt(ip2);
            if (c2 == ' ' || c2 == '\r' || c2 == '\n' || c2 == '\t') continue;
            buf[p2++] = c2;
        }
        return Base64Coder.decode(buf, 0, p2);
    }

    public static byte[] decode(String s2) {
        return Base64Coder.decode(s2.toCharArray());
    }

    public static byte[] decode(char[] in2) {
        return Base64Coder.decode(in2, 0, in2.length);
    }

    public static byte[] decode(char[] in2, int iOff, int iLen) {
        if (iLen % 4 != 0) {
            throw new IllegalArgumentException("Length of Base64 encoded input string is not a multiple of 4.");
        }
        while (iLen > 0 && in2[iOff + iLen - 1] == '=') {
            --iLen;
        }
        int oLen = iLen * 3 / 4;
        byte[] out = new byte[oLen];
        int ip2 = iOff;
        int iEnd = iOff + iLen;
        int op2 = 0;
        while (ip2 < iEnd) {
            int i3;
            char i0 = in2[ip2++];
            char i1 = in2[ip2++];
            int i2 = ip2 < iEnd ? in2[ip2++] : 65;
            int n2 = i3 = ip2 < iEnd ? in2[ip2++] : 65;
            if (i0 > '\u007f' || i1 > '\u007f' || i2 > 127 || i3 > 127) {
                throw new IllegalArgumentException("Illegal character in Base64 encoded data.");
            }
            byte b0 = map2[i0];
            byte b1 = map2[i1];
            byte b2 = map2[i2];
            byte b3 = map2[i3];
            if (b0 < 0 || b1 < 0 || b2 < 0 || b3 < 0) {
                throw new IllegalArgumentException("Illegal character in Base64 encoded data.");
            }
            int o0 = b0 << 2 | b1 >>> 4;
            int o1 = (b1 & 0xF) << 4 | b2 >>> 2;
            int o2 = (b2 & 3) << 6 | b3;
            out[op2++] = (byte)o0;
            if (op2 < oLen) {
                out[op2++] = (byte)o1;
            }
            if (op2 >= oLen) continue;
            out[op2++] = (byte)o2;
        }
        return out;
    }

    private Base64Coder() {
    }

    static {
        int c2;
        systemLineSeparator = System.getProperty("line.separator");
        map1 = new char[64];
        int i2 = 0;
        for (c2 = 65; c2 <= 90; c2 = (int)((char)(c2 + 1))) {
            Base64Coder.map1[i2++] = c2;
        }
        for (c2 = 97; c2 <= 122; c2 = (int)((char)(c2 + 1))) {
            Base64Coder.map1[i2++] = c2;
        }
        for (c2 = 48; c2 <= 57; c2 = (int)((char)(c2 + 1))) {
            Base64Coder.map1[i2++] = c2;
        }
        Base64Coder.map1[i2++] = 43;
        Base64Coder.map1[i2++] = 47;
        map2 = new byte[128];
        for (i2 = 0; i2 < map2.length; ++i2) {
            Base64Coder.map2[i2] = -1;
        }
        for (i2 = 0; i2 < 64; ++i2) {
            Base64Coder.map2[Base64Coder.map1[i2]] = (byte)i2;
        }
    }
}

