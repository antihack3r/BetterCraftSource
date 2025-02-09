/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jna.platform.win32;

import com.sun.jna.platform.win32.WinError;
import com.sun.jna.platform.win32.WinNT;

public abstract class W32Errors
implements WinError {
    public static final boolean SUCCEEDED(int hr2) {
        return hr2 >= 0;
    }

    public static final boolean FAILED(int hr2) {
        return hr2 < 0;
    }

    public static final int HRESULT_CODE(int hr2) {
        return hr2 & 0xFFFF;
    }

    public static final int SCODE_CODE(int sc2) {
        return sc2 & 0xFFFF;
    }

    public static final int HRESULT_FACILITY(int hr2) {
        return (hr2 >>= 16) & 0x1FFF;
    }

    public static final int SCODE_FACILITY(short sc2) {
        sc2 = (short)(sc2 >> 16);
        return sc2 & 0x1FFF;
    }

    public static short HRESULT_SEVERITY(int hr2) {
        return (short)((hr2 >>= 31) & 1);
    }

    public static short SCODE_SEVERITY(short sc2) {
        sc2 = (short)(sc2 >> 31);
        return (short)(sc2 & 1);
    }

    public static int MAKE_HRESULT(short sev, short fac, short code) {
        return sev << 31 | fac << 16 | code;
    }

    public static final int MAKE_SCODE(short sev, short fac, short code) {
        return sev << 31 | fac << 16 | code;
    }

    public static final WinNT.HRESULT HRESULT_FROM_WIN32(int x2) {
        int f2 = 7;
        return new WinNT.HRESULT(x2 <= 0 ? x2 : x2 & 0xFFFF | (f2 <<= 16) | Integer.MIN_VALUE);
    }

    public static final int FILTER_HRESULT_FROM_FLT_NTSTATUS(int x2) {
        int f2 = 31;
        return x2 & 0x8000FFFF | (f2 <<= 16);
    }
}

