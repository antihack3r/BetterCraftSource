/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.lang3;

import org.apache.commons.lang3.CharSet;
import org.apache.commons.lang3.StringUtils;

public class CharSetUtils {
    public static String squeeze(String str, String ... set) {
        if (StringUtils.isEmpty(str) || CharSetUtils.deepEmpty(set)) {
            return str;
        }
        CharSet chars = CharSet.getInstance(set);
        StringBuilder buffer = new StringBuilder(str.length());
        char[] chrs = str.toCharArray();
        int sz = chrs.length;
        int lastChar = 32;
        int ch = 32;
        for (int i2 = 0; i2 < sz; ++i2) {
            ch = chrs[i2];
            if (ch == lastChar && i2 != 0 && chars.contains((char)ch)) continue;
            buffer.append((char)ch);
            lastChar = ch;
        }
        return buffer.toString();
    }

    public static boolean containsAny(String str, String ... set) {
        if (StringUtils.isEmpty(str) || CharSetUtils.deepEmpty(set)) {
            return false;
        }
        CharSet chars = CharSet.getInstance(set);
        for (char c2 : str.toCharArray()) {
            if (!chars.contains(c2)) continue;
            return true;
        }
        return false;
    }

    public static int count(String str, String ... set) {
        if (StringUtils.isEmpty(str) || CharSetUtils.deepEmpty(set)) {
            return 0;
        }
        CharSet chars = CharSet.getInstance(set);
        int count = 0;
        for (char c2 : str.toCharArray()) {
            if (!chars.contains(c2)) continue;
            ++count;
        }
        return count;
    }

    public static String keep(String str, String ... set) {
        if (str == null) {
            return null;
        }
        if (str.isEmpty() || CharSetUtils.deepEmpty(set)) {
            return "";
        }
        return CharSetUtils.modify(str, set, true);
    }

    public static String delete(String str, String ... set) {
        if (StringUtils.isEmpty(str) || CharSetUtils.deepEmpty(set)) {
            return str;
        }
        return CharSetUtils.modify(str, set, false);
    }

    private static String modify(String str, String[] set, boolean expect) {
        CharSet chars = CharSet.getInstance(set);
        StringBuilder buffer = new StringBuilder(str.length());
        char[] chrs = str.toCharArray();
        int sz = chrs.length;
        for (int i2 = 0; i2 < sz; ++i2) {
            if (chars.contains(chrs[i2]) != expect) continue;
            buffer.append(chrs[i2]);
        }
        return buffer.toString();
    }

    private static boolean deepEmpty(String[] strings) {
        if (strings != null) {
            for (String s2 : strings) {
                if (!StringUtils.isNotEmpty(s2)) continue;
                return false;
            }
        }
        return true;
    }
}

