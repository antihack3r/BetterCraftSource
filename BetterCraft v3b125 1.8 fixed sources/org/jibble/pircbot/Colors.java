/*
 * Decompiled with CFR 0.152.
 */
package org.jibble.pircbot;

public class Colors {
    public static final String NORMAL = "\u000f";
    public static final String BOLD = "\u0002";
    public static final String UNDERLINE = "\u001f";
    public static final String REVERSE = "\u0016";
    public static final String WHITE = "\u000300";
    public static final String BLACK = "\u000301";
    public static final String DARK_BLUE = "\u000302";
    public static final String DARK_GREEN = "\u000303";
    public static final String RED = "\u000304";
    public static final String BROWN = "\u000305";
    public static final String PURPLE = "\u000306";
    public static final String OLIVE = "\u000307";
    public static final String YELLOW = "\u000308";
    public static final String GREEN = "\u000309";
    public static final String TEAL = "\u000310";
    public static final String CYAN = "\u000311";
    public static final String BLUE = "\u000312";
    public static final String MAGENTA = "\u000313";
    public static final String DARK_GRAY = "\u000314";
    public static final String LIGHT_GRAY = "\u000315";

    private Colors() {
    }

    public static String removeColors(String string) {
        int n2 = string.length();
        StringBuffer stringBuffer = new StringBuffer();
        int n3 = 0;
        while (n3 < n2) {
            char c2 = string.charAt(n3);
            if (c2 == '\u0003') {
                if (++n3 >= n2 || !Character.isDigit(c2 = string.charAt(n3))) continue;
                if (++n3 < n2 && Character.isDigit(c2 = string.charAt(n3))) {
                    ++n3;
                }
                if (n3 >= n2 || (c2 = string.charAt(n3)) != ',') continue;
                if (++n3 < n2) {
                    c2 = string.charAt(n3);
                    if (Character.isDigit(c2)) {
                        if (++n3 >= n2 || !Character.isDigit(c2 = string.charAt(n3))) continue;
                        ++n3;
                        continue;
                    }
                    --n3;
                    continue;
                }
                --n3;
                continue;
            }
            if (c2 == '\u000f') {
                ++n3;
                continue;
            }
            stringBuffer.append(c2);
            ++n3;
        }
        return stringBuffer.toString();
    }

    public static String removeFormatting(String string) {
        int n2 = string.length();
        StringBuffer stringBuffer = new StringBuffer();
        for (int i2 = 0; i2 < n2; ++i2) {
            char c2 = string.charAt(i2);
            if (c2 == '\u000f' || c2 == '\u0002' || c2 == '\u001f' || c2 == '\u0016') continue;
            stringBuffer.append(c2);
        }
        return stringBuffer.toString();
    }

    public static String removeFormattingAndColors(String string) {
        return Colors.removeFormatting(Colors.removeColors(string));
    }
}

