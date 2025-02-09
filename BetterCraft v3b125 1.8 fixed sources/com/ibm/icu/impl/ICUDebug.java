/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.icu.impl;

import com.ibm.icu.util.VersionInfo;

public final class ICUDebug {
    private static String params;
    private static boolean debug;
    private static boolean help;
    public static final String javaVersionString;
    public static final boolean isJDK14OrHigher;
    public static final VersionInfo javaVersion;

    public static VersionInfo getInstanceLenient(String s2) {
        int[] ver = new int[4];
        boolean numeric = false;
        int i2 = 0;
        int vidx = 0;
        while (i2 < s2.length()) {
            char c2;
            if ((c2 = s2.charAt(i2++)) < '0' || c2 > '9') {
                if (!numeric) continue;
                if (vidx == 3) break;
                numeric = false;
                ++vidx;
                continue;
            }
            if (numeric) {
                ver[vidx] = ver[vidx] * 10 + (c2 - 48);
                if (ver[vidx] <= 255) continue;
                ver[vidx] = 0;
                break;
            }
            numeric = true;
            ver[vidx] = c2 - 48;
        }
        return VersionInfo.getInstance(ver[0], ver[1], ver[2], ver[3]);
    }

    public static boolean enabled() {
        return debug;
    }

    public static boolean enabled(String arg2) {
        if (debug) {
            boolean result;
            boolean bl2 = result = params.indexOf(arg2) != -1;
            if (help) {
                System.out.println("\nICUDebug.enabled(" + arg2 + ") = " + result);
            }
            return result;
        }
        return false;
    }

    public static String value(String arg2) {
        String result = "false";
        if (debug) {
            int index = params.indexOf(arg2);
            if (index != -1) {
                int limit;
                result = params.length() > (index += arg2.length()) && params.charAt(index) == '=' ? params.substring(index, (limit = params.indexOf(",", ++index)) == -1 ? params.length() : limit) : "true";
            }
            if (help) {
                System.out.println("\nICUDebug.value(" + arg2 + ") = " + result);
            }
        }
        return result;
    }

    static {
        VersionInfo java14Version;
        try {
            params = System.getProperty("ICUDebug");
        }
        catch (SecurityException e2) {
            // empty catch block
        }
        debug = params != null;
        boolean bl2 = help = debug && (params.equals("") || params.indexOf("help") != -1);
        if (debug) {
            System.out.println("\nICUDebug=" + params);
        }
        isJDK14OrHigher = (javaVersion = ICUDebug.getInstanceLenient(javaVersionString = System.getProperty("java.version", "0"))).compareTo(java14Version = VersionInfo.getInstance("1.4.0")) >= 0;
    }
}

