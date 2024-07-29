/*
 * Decompiled with CFR 0.152.
 */
package org.slf4j.helpers;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.NormalizedParameters;
import org.slf4j.helpers.Util;

public final class MessageFormatter {
    static final char DELIM_START = '{';
    static final char DELIM_STOP = '}';
    static final String DELIM_STR = "{}";
    private static final char ESCAPE_CHAR = '\\';

    public static final FormattingTuple format(String messagePattern, Object arg2) {
        return MessageFormatter.arrayFormat(messagePattern, new Object[]{arg2});
    }

    public static final FormattingTuple format(String messagePattern, Object arg1, Object arg2) {
        return MessageFormatter.arrayFormat(messagePattern, new Object[]{arg1, arg2});
    }

    public static final FormattingTuple arrayFormat(String messagePattern, Object[] argArray) {
        Throwable throwableCandidate = MessageFormatter.getThrowableCandidate(argArray);
        Object[] args = argArray;
        if (throwableCandidate != null) {
            args = MessageFormatter.trimmedCopy(argArray);
        }
        return MessageFormatter.arrayFormat(messagePattern, args, throwableCandidate);
    }

    public static final String basicArrayFormat(String messagePattern, Object[] argArray) {
        FormattingTuple ft2 = MessageFormatter.arrayFormat(messagePattern, argArray, null);
        return ft2.getMessage();
    }

    public static String basicArrayFormat(NormalizedParameters np2) {
        return MessageFormatter.basicArrayFormat(np2.getMessage(), np2.getArguments());
    }

    public static final FormattingTuple arrayFormat(String messagePattern, Object[] argArray, Throwable throwable) {
        if (messagePattern == null) {
            return new FormattingTuple(null, argArray, throwable);
        }
        if (argArray == null) {
            return new FormattingTuple(messagePattern);
        }
        int i2 = 0;
        StringBuilder sbuf = new StringBuilder(messagePattern.length() + 50);
        for (int L = 0; L < argArray.length; ++L) {
            int j2 = messagePattern.indexOf(DELIM_STR, i2);
            if (j2 == -1) {
                if (i2 == 0) {
                    return new FormattingTuple(messagePattern, argArray, throwable);
                }
                sbuf.append(messagePattern, i2, messagePattern.length());
                return new FormattingTuple(sbuf.toString(), argArray, throwable);
            }
            if (MessageFormatter.isEscapedDelimeter(messagePattern, j2)) {
                if (!MessageFormatter.isDoubleEscaped(messagePattern, j2)) {
                    --L;
                    sbuf.append(messagePattern, i2, j2 - 1);
                    sbuf.append('{');
                    i2 = j2 + 1;
                    continue;
                }
                sbuf.append(messagePattern, i2, j2 - 1);
                MessageFormatter.deeplyAppendParameter(sbuf, argArray[L], new HashMap<Object[], Object>());
                i2 = j2 + 2;
                continue;
            }
            sbuf.append(messagePattern, i2, j2);
            MessageFormatter.deeplyAppendParameter(sbuf, argArray[L], new HashMap<Object[], Object>());
            i2 = j2 + 2;
        }
        sbuf.append(messagePattern, i2, messagePattern.length());
        return new FormattingTuple(sbuf.toString(), argArray, throwable);
    }

    static final boolean isEscapedDelimeter(String messagePattern, int delimeterStartIndex) {
        if (delimeterStartIndex == 0) {
            return false;
        }
        char potentialEscape = messagePattern.charAt(delimeterStartIndex - 1);
        return potentialEscape == '\\';
    }

    static final boolean isDoubleEscaped(String messagePattern, int delimeterStartIndex) {
        return delimeterStartIndex >= 2 && messagePattern.charAt(delimeterStartIndex - 2) == '\\';
    }

    private static void deeplyAppendParameter(StringBuilder sbuf, Object o2, Map<Object[], Object> seenMap) {
        if (o2 == null) {
            sbuf.append("null");
            return;
        }
        if (!o2.getClass().isArray()) {
            MessageFormatter.safeObjectAppend(sbuf, o2);
        } else if (o2 instanceof boolean[]) {
            MessageFormatter.booleanArrayAppend(sbuf, (boolean[])o2);
        } else if (o2 instanceof byte[]) {
            MessageFormatter.byteArrayAppend(sbuf, (byte[])o2);
        } else if (o2 instanceof char[]) {
            MessageFormatter.charArrayAppend(sbuf, (char[])o2);
        } else if (o2 instanceof short[]) {
            MessageFormatter.shortArrayAppend(sbuf, (short[])o2);
        } else if (o2 instanceof int[]) {
            MessageFormatter.intArrayAppend(sbuf, (int[])o2);
        } else if (o2 instanceof long[]) {
            MessageFormatter.longArrayAppend(sbuf, (long[])o2);
        } else if (o2 instanceof float[]) {
            MessageFormatter.floatArrayAppend(sbuf, (float[])o2);
        } else if (o2 instanceof double[]) {
            MessageFormatter.doubleArrayAppend(sbuf, (double[])o2);
        } else {
            MessageFormatter.objectArrayAppend(sbuf, (Object[])o2, seenMap);
        }
    }

    private static void safeObjectAppend(StringBuilder sbuf, Object o2) {
        try {
            String oAsString = o2.toString();
            sbuf.append(oAsString);
        }
        catch (Throwable t2) {
            Util.report("SLF4J: Failed toString() invocation on an object of type [" + o2.getClass().getName() + "]", t2);
            sbuf.append("[FAILED toString()]");
        }
    }

    private static void objectArrayAppend(StringBuilder sbuf, Object[] a2, Map<Object[], Object> seenMap) {
        sbuf.append('[');
        if (!seenMap.containsKey(a2)) {
            seenMap.put(a2, null);
            int len = a2.length;
            for (int i2 = 0; i2 < len; ++i2) {
                MessageFormatter.deeplyAppendParameter(sbuf, a2[i2], seenMap);
                if (i2 == len - 1) continue;
                sbuf.append(", ");
            }
            seenMap.remove(a2);
        } else {
            sbuf.append("...");
        }
        sbuf.append(']');
    }

    private static void booleanArrayAppend(StringBuilder sbuf, boolean[] a2) {
        sbuf.append('[');
        int len = a2.length;
        for (int i2 = 0; i2 < len; ++i2) {
            sbuf.append(a2[i2]);
            if (i2 == len - 1) continue;
            sbuf.append(", ");
        }
        sbuf.append(']');
    }

    private static void byteArrayAppend(StringBuilder sbuf, byte[] a2) {
        sbuf.append('[');
        int len = a2.length;
        for (int i2 = 0; i2 < len; ++i2) {
            sbuf.append(a2[i2]);
            if (i2 == len - 1) continue;
            sbuf.append(", ");
        }
        sbuf.append(']');
    }

    private static void charArrayAppend(StringBuilder sbuf, char[] a2) {
        sbuf.append('[');
        int len = a2.length;
        for (int i2 = 0; i2 < len; ++i2) {
            sbuf.append(a2[i2]);
            if (i2 == len - 1) continue;
            sbuf.append(", ");
        }
        sbuf.append(']');
    }

    private static void shortArrayAppend(StringBuilder sbuf, short[] a2) {
        sbuf.append('[');
        int len = a2.length;
        for (int i2 = 0; i2 < len; ++i2) {
            sbuf.append(a2[i2]);
            if (i2 == len - 1) continue;
            sbuf.append(", ");
        }
        sbuf.append(']');
    }

    private static void intArrayAppend(StringBuilder sbuf, int[] a2) {
        sbuf.append('[');
        int len = a2.length;
        for (int i2 = 0; i2 < len; ++i2) {
            sbuf.append(a2[i2]);
            if (i2 == len - 1) continue;
            sbuf.append(", ");
        }
        sbuf.append(']');
    }

    private static void longArrayAppend(StringBuilder sbuf, long[] a2) {
        sbuf.append('[');
        int len = a2.length;
        for (int i2 = 0; i2 < len; ++i2) {
            sbuf.append(a2[i2]);
            if (i2 == len - 1) continue;
            sbuf.append(", ");
        }
        sbuf.append(']');
    }

    private static void floatArrayAppend(StringBuilder sbuf, float[] a2) {
        sbuf.append('[');
        int len = a2.length;
        for (int i2 = 0; i2 < len; ++i2) {
            sbuf.append(a2[i2]);
            if (i2 == len - 1) continue;
            sbuf.append(", ");
        }
        sbuf.append(']');
    }

    private static void doubleArrayAppend(StringBuilder sbuf, double[] a2) {
        sbuf.append('[');
        int len = a2.length;
        for (int i2 = 0; i2 < len; ++i2) {
            sbuf.append(a2[i2]);
            if (i2 == len - 1) continue;
            sbuf.append(", ");
        }
        sbuf.append(']');
    }

    public static Throwable getThrowableCandidate(Object[] argArray) {
        return NormalizedParameters.getThrowableCandidate(argArray);
    }

    public static Object[] trimmedCopy(Object[] argArray) {
        return NormalizedParameters.trimmedCopy(argArray);
    }
}

