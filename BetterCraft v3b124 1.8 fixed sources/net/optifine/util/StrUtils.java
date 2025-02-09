/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.util;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class StrUtils {
    public static boolean equalsMask(String str, String mask, char wildChar, char wildCharSingle) {
        if (mask != null && str != null) {
            String s1;
            if (mask.indexOf(wildChar) < 0) {
                return mask.indexOf(wildCharSingle) < 0 ? mask.equals(str) : StrUtils.equalsMaskSingle(str, mask, wildCharSingle);
            }
            ArrayList<String> list = new ArrayList<String>();
            String s2 = "" + wildChar;
            if (mask.startsWith(s2)) {
                list.add("");
            }
            StringTokenizer stringtokenizer = new StringTokenizer(mask, s2);
            while (stringtokenizer.hasMoreElements()) {
                list.add(stringtokenizer.nextToken());
            }
            if (mask.endsWith(s2)) {
                list.add("");
            }
            if (!StrUtils.startsWithMaskSingle(str, s1 = (String)list.get(0), wildCharSingle)) {
                return false;
            }
            String s22 = (String)list.get(list.size() - 1);
            if (!StrUtils.endsWithMaskSingle(str, s22, wildCharSingle)) {
                return false;
            }
            int i2 = 0;
            int j2 = 0;
            while (j2 < list.size()) {
                String s3 = (String)list.get(j2);
                if (s3.length() > 0) {
                    int k2 = StrUtils.indexOfMaskSingle(str, s3, i2, wildCharSingle);
                    if (k2 < 0) {
                        return false;
                    }
                    i2 = k2 + s3.length();
                }
                ++j2;
            }
            return true;
        }
        return mask == str;
    }

    private static boolean equalsMaskSingle(String str, String mask, char wildCharSingle) {
        if (str != null && mask != null) {
            if (str.length() != mask.length()) {
                return false;
            }
            int i2 = 0;
            while (i2 < mask.length()) {
                char c0 = mask.charAt(i2);
                if (c0 != wildCharSingle && str.charAt(i2) != c0) {
                    return false;
                }
                ++i2;
            }
            return true;
        }
        return str == mask;
    }

    private static int indexOfMaskSingle(String str, String mask, int startPos, char wildCharSingle) {
        if (str != null && mask != null) {
            if (startPos >= 0 && startPos <= str.length()) {
                if (str.length() < startPos + mask.length()) {
                    return -1;
                }
                int i2 = startPos;
                while (i2 + mask.length() <= str.length()) {
                    String s2 = str.substring(i2, i2 + mask.length());
                    if (StrUtils.equalsMaskSingle(s2, mask, wildCharSingle)) {
                        return i2;
                    }
                    ++i2;
                }
                return -1;
            }
            return -1;
        }
        return -1;
    }

    private static boolean endsWithMaskSingle(String str, String mask, char wildCharSingle) {
        if (str != null && mask != null) {
            if (str.length() < mask.length()) {
                return false;
            }
            String s2 = str.substring(str.length() - mask.length(), str.length());
            return StrUtils.equalsMaskSingle(s2, mask, wildCharSingle);
        }
        return str == mask;
    }

    private static boolean startsWithMaskSingle(String str, String mask, char wildCharSingle) {
        if (str != null && mask != null) {
            if (str.length() < mask.length()) {
                return false;
            }
            String s2 = str.substring(0, mask.length());
            return StrUtils.equalsMaskSingle(s2, mask, wildCharSingle);
        }
        return str == mask;
    }

    public static boolean equalsMask(String str, String[] masks, char wildChar) {
        int i2 = 0;
        while (i2 < masks.length) {
            String s2 = masks[i2];
            if (StrUtils.equalsMask(str, s2, wildChar)) {
                return true;
            }
            ++i2;
        }
        return false;
    }

    public static boolean equalsMask(String str, String mask, char wildChar) {
        if (mask != null && str != null) {
            String s1;
            if (mask.indexOf(wildChar) < 0) {
                return mask.equals(str);
            }
            ArrayList<String> list = new ArrayList<String>();
            String s2 = "" + wildChar;
            if (mask.startsWith(s2)) {
                list.add("");
            }
            StringTokenizer stringtokenizer = new StringTokenizer(mask, s2);
            while (stringtokenizer.hasMoreElements()) {
                list.add(stringtokenizer.nextToken());
            }
            if (mask.endsWith(s2)) {
                list.add("");
            }
            if (!str.startsWith(s1 = (String)list.get(0))) {
                return false;
            }
            String s22 = (String)list.get(list.size() - 1);
            if (!str.endsWith(s22)) {
                return false;
            }
            int i2 = 0;
            int j2 = 0;
            while (j2 < list.size()) {
                String s3 = (String)list.get(j2);
                if (s3.length() > 0) {
                    int k2 = str.indexOf(s3, i2);
                    if (k2 < 0) {
                        return false;
                    }
                    i2 = k2 + s3.length();
                }
                ++j2;
            }
            return true;
        }
        return mask == str;
    }

    public static String[] split(String str, String separators) {
        if (str != null && str.length() > 0) {
            if (separators == null) {
                return new String[]{str};
            }
            ArrayList<String> list = new ArrayList<String>();
            int i2 = 0;
            int j2 = 0;
            while (j2 < str.length()) {
                char c0 = str.charAt(j2);
                if (StrUtils.equals(c0, separators)) {
                    list.add(str.substring(i2, j2));
                    i2 = j2 + 1;
                }
                ++j2;
            }
            list.add(str.substring(i2, str.length()));
            return list.toArray(new String[list.size()]);
        }
        return new String[0];
    }

    private static boolean equals(char ch, String matches) {
        int i2 = 0;
        while (i2 < matches.length()) {
            if (matches.charAt(i2) == ch) {
                return true;
            }
            ++i2;
        }
        return false;
    }

    public static boolean equalsTrim(String a2, String b2) {
        if (a2 != null) {
            a2 = a2.trim();
        }
        if (b2 != null) {
            b2 = b2.trim();
        }
        return StrUtils.equals(a2, (Object)b2);
    }

    public static boolean isEmpty(String string) {
        return string == null ? true : string.trim().length() <= 0;
    }

    public static String stringInc(String str) {
        String s2;
        int i2 = StrUtils.parseInt(str, -1);
        if (i2 == -1) {
            return "";
        }
        return (s2 = "" + ++i2).length() > str.length() ? "" : StrUtils.fillLeft("" + i2, str.length(), '0');
    }

    public static int parseInt(String s2, int defVal) {
        if (s2 == null) {
            return defVal;
        }
        try {
            return Integer.parseInt(s2);
        }
        catch (NumberFormatException var3) {
            return defVal;
        }
    }

    public static boolean isFilled(String string) {
        return !StrUtils.isEmpty(string);
    }

    public static String addIfNotContains(String target, String source) {
        int i2 = 0;
        while (i2 < source.length()) {
            if (target.indexOf(source.charAt(i2)) < 0) {
                target = String.valueOf(target) + source.charAt(i2);
            }
            ++i2;
        }
        return target;
    }

    public static String fillLeft(String s2, int len, char fillChar) {
        if (s2 == null) {
            s2 = "";
        }
        if (s2.length() >= len) {
            return s2;
        }
        StringBuffer stringbuffer = new StringBuffer();
        int i2 = len - s2.length();
        while (stringbuffer.length() < i2) {
            stringbuffer.append(fillChar);
        }
        return String.valueOf(stringbuffer.toString()) + s2;
    }

    public static String fillRight(String s2, int len, char fillChar) {
        if (s2 == null) {
            s2 = "";
        }
        if (s2.length() >= len) {
            return s2;
        }
        StringBuffer stringbuffer = new StringBuffer(s2);
        while (stringbuffer.length() < len) {
            stringbuffer.append(fillChar);
        }
        return stringbuffer.toString();
    }

    public static boolean equals(Object a2, Object b2) {
        return a2 == b2 ? true : (a2 != null && a2.equals(b2) ? true : b2 != null && b2.equals(a2));
    }

    public static boolean startsWith(String str, String[] prefixes) {
        if (str == null) {
            return false;
        }
        if (prefixes == null) {
            return false;
        }
        int i2 = 0;
        while (i2 < prefixes.length) {
            String s2 = prefixes[i2];
            if (str.startsWith(s2)) {
                return true;
            }
            ++i2;
        }
        return false;
    }

    public static boolean endsWith(String str, String[] suffixes) {
        if (str == null) {
            return false;
        }
        if (suffixes == null) {
            return false;
        }
        int i2 = 0;
        while (i2 < suffixes.length) {
            String s2 = suffixes[i2];
            if (str.endsWith(s2)) {
                return true;
            }
            ++i2;
        }
        return false;
    }

    public static String removePrefix(String str, String prefix) {
        if (str != null && prefix != null) {
            if (str.startsWith(prefix)) {
                str = str.substring(prefix.length());
            }
            return str;
        }
        return str;
    }

    public static String removeSuffix(String str, String suffix) {
        if (str != null && suffix != null) {
            if (str.endsWith(suffix)) {
                str = str.substring(0, str.length() - suffix.length());
            }
            return str;
        }
        return str;
    }

    public static String replaceSuffix(String str, String suffix, String suffixNew) {
        if (str != null && suffix != null) {
            if (!str.endsWith(suffix)) {
                return str;
            }
            if (suffixNew == null) {
                suffixNew = "";
            }
            str = str.substring(0, str.length() - suffix.length());
            return String.valueOf(str) + suffixNew;
        }
        return str;
    }

    public static String replacePrefix(String str, String prefix, String prefixNew) {
        if (str != null && prefix != null) {
            if (!str.startsWith(prefix)) {
                return str;
            }
            if (prefixNew == null) {
                prefixNew = "";
            }
            str = str.substring(prefix.length());
            return String.valueOf(prefixNew) + str;
        }
        return str;
    }

    public static int findPrefix(String[] strs, String prefix) {
        if (strs != null && prefix != null) {
            int i2 = 0;
            while (i2 < strs.length) {
                String s2 = strs[i2];
                if (s2.startsWith(prefix)) {
                    return i2;
                }
                ++i2;
            }
            return -1;
        }
        return -1;
    }

    public static int findSuffix(String[] strs, String suffix) {
        if (strs != null && suffix != null) {
            int i2 = 0;
            while (i2 < strs.length) {
                String s2 = strs[i2];
                if (s2.endsWith(suffix)) {
                    return i2;
                }
                ++i2;
            }
            return -1;
        }
        return -1;
    }

    public static String[] remove(String[] strs, int start, int end) {
        if (strs == null) {
            return strs;
        }
        if (end > 0 && start < strs.length) {
            if (start >= end) {
                return strs;
            }
            ArrayList<String> list = new ArrayList<String>(strs.length);
            int i2 = 0;
            while (i2 < strs.length) {
                String s2 = strs[i2];
                if (i2 < start || i2 >= end) {
                    list.add(s2);
                }
                ++i2;
            }
            String[] astring = list.toArray(new String[list.size()]);
            return astring;
        }
        return strs;
    }

    public static String removeSuffix(String str, String[] suffixes) {
        if (str != null && suffixes != null) {
            int i2 = str.length();
            int j2 = 0;
            while (j2 < suffixes.length) {
                String s2 = suffixes[j2];
                if ((str = StrUtils.removeSuffix(str, s2)).length() != i2) break;
                ++j2;
            }
            return str;
        }
        return str;
    }

    public static String removePrefix(String str, String[] prefixes) {
        if (str != null && prefixes != null) {
            int i2 = str.length();
            int j2 = 0;
            while (j2 < prefixes.length) {
                String s2 = prefixes[j2];
                if ((str = StrUtils.removePrefix(str, s2)).length() != i2) break;
                ++j2;
            }
            return str;
        }
        return str;
    }

    public static String removePrefixSuffix(String str, String[] prefixes, String[] suffixes) {
        str = StrUtils.removePrefix(str, prefixes);
        str = StrUtils.removeSuffix(str, suffixes);
        return str;
    }

    public static String removePrefixSuffix(String str, String prefix, String suffix) {
        return StrUtils.removePrefixSuffix(str, new String[]{prefix}, new String[]{suffix});
    }

    public static String getSegment(String str, String start, String end) {
        if (str != null && start != null && end != null) {
            int i2 = str.indexOf(start);
            if (i2 < 0) {
                return null;
            }
            int j2 = str.indexOf(end, i2);
            return j2 < 0 ? null : str.substring(i2, j2 + end.length());
        }
        return null;
    }

    public static String addSuffixCheck(String str, String suffix) {
        return str != null && suffix != null ? (str.endsWith(suffix) ? str : String.valueOf(str) + suffix) : str;
    }

    public static String addPrefixCheck(String str, String prefix) {
        return str != null && prefix != null ? (str.endsWith(prefix) ? str : String.valueOf(prefix) + str) : str;
    }

    public static String trim(String str, String chars) {
        if (str != null && chars != null) {
            str = StrUtils.trimLeading(str, chars);
            str = StrUtils.trimTrailing(str, chars);
            return str;
        }
        return str;
    }

    public static String trimLeading(String str, String chars) {
        if (str != null && chars != null) {
            int i2 = str.length();
            int j2 = 0;
            while (j2 < i2) {
                char c0 = str.charAt(j2);
                if (chars.indexOf(c0) < 0) {
                    return str.substring(j2);
                }
                ++j2;
            }
            return "";
        }
        return str;
    }

    public static String trimTrailing(String str, String chars) {
        if (str != null && chars != null) {
            int i2;
            int j2 = i2 = str.length();
            while (j2 > 0) {
                char c0 = str.charAt(j2 - 1);
                if (chars.indexOf(c0) < 0) break;
                --j2;
            }
            return j2 == i2 ? str : str.substring(0, j2);
        }
        return str;
    }
}

