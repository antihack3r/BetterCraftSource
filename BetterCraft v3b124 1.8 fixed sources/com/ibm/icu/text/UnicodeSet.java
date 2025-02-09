/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.icu.text;

import com.ibm.icu.impl.BMPSet;
import com.ibm.icu.impl.Norm2AllModes;
import com.ibm.icu.impl.PatternProps;
import com.ibm.icu.impl.RuleCharacterIterator;
import com.ibm.icu.impl.SortedSetRelation;
import com.ibm.icu.impl.UBiDiProps;
import com.ibm.icu.impl.UCaseProps;
import com.ibm.icu.impl.UCharacterProperty;
import com.ibm.icu.impl.UPropertyAliases;
import com.ibm.icu.impl.UnicodeSetStringSpan;
import com.ibm.icu.impl.Utility;
import com.ibm.icu.lang.CharSequences;
import com.ibm.icu.lang.UCharacter;
import com.ibm.icu.lang.UScript;
import com.ibm.icu.text.BreakIterator;
import com.ibm.icu.text.Replaceable;
import com.ibm.icu.text.SymbolTable;
import com.ibm.icu.text.UTF16;
import com.ibm.icu.text.UnicodeFilter;
import com.ibm.icu.text.UnicodeMatcher;
import com.ibm.icu.text.UnicodeSetIterator;
import com.ibm.icu.util.Freezable;
import com.ibm.icu.util.ULocale;
import com.ibm.icu.util.VersionInfo;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class UnicodeSet
extends UnicodeFilter
implements Iterable<String>,
Comparable<UnicodeSet>,
Freezable<UnicodeSet> {
    public static final UnicodeSet EMPTY = new UnicodeSet().freeze();
    public static final UnicodeSet ALL_CODE_POINTS = new UnicodeSet(0, 0x10FFFF).freeze();
    private static XSymbolTable XSYMBOL_TABLE = null;
    private static final int LOW = 0;
    private static final int HIGH = 0x110000;
    public static final int MIN_VALUE = 0;
    public static final int MAX_VALUE = 0x10FFFF;
    private int len;
    private int[] list;
    private int[] rangeList;
    private int[] buffer;
    TreeSet<String> strings = new TreeSet();
    private String pat = null;
    private static final int START_EXTRA = 16;
    private static final int GROW_EXTRA = 16;
    private static final String ANY_ID = "ANY";
    private static final String ASCII_ID = "ASCII";
    private static final String ASSIGNED = "Assigned";
    private static UnicodeSet[] INCLUSIONS = null;
    private BMPSet bmpSet;
    private UnicodeSetStringSpan stringSpan;
    private static final VersionInfo NO_VERSION = VersionInfo.getInstance(0, 0, 0, 0);
    public static final int IGNORE_SPACE = 1;
    public static final int CASE = 2;
    public static final int CASE_INSENSITIVE = 2;
    public static final int ADD_CASE_MAPPINGS = 4;

    public UnicodeSet() {
        this.list = new int[17];
        this.list[this.len++] = 0x110000;
    }

    public UnicodeSet(UnicodeSet other) {
        this.set(other);
    }

    public UnicodeSet(int start, int end) {
        this();
        this.complement(start, end);
    }

    public UnicodeSet(int ... pairs) {
        if ((pairs.length & 1) != 0) {
            throw new IllegalArgumentException("Must have even number of integers");
        }
        this.list = new int[pairs.length + 1];
        this.len = this.list.length;
        int last = -1;
        int i2 = 0;
        while (i2 < pairs.length) {
            int start = pairs[i2];
            if (last >= start) {
                throw new IllegalArgumentException("Must be monotonically increasing.");
            }
            this.list[i2++] = last = start;
            int end = pairs[i2] + 1;
            if (last >= end) {
                throw new IllegalArgumentException("Must be monotonically increasing.");
            }
            this.list[i2++] = last = end;
        }
        this.list[i2] = 0x110000;
    }

    public UnicodeSet(String pattern) {
        this();
        this.applyPattern(pattern, null, null, 1);
    }

    public UnicodeSet(String pattern, boolean ignoreWhitespace) {
        this();
        this.applyPattern(pattern, null, null, ignoreWhitespace ? 1 : 0);
    }

    public UnicodeSet(String pattern, int options) {
        this();
        this.applyPattern(pattern, null, null, options);
    }

    public UnicodeSet(String pattern, ParsePosition pos, SymbolTable symbols) {
        this();
        this.applyPattern(pattern, pos, symbols, 1);
    }

    public UnicodeSet(String pattern, ParsePosition pos, SymbolTable symbols, int options) {
        this();
        this.applyPattern(pattern, pos, symbols, options);
    }

    public Object clone() {
        UnicodeSet result = new UnicodeSet(this);
        result.bmpSet = this.bmpSet;
        result.stringSpan = this.stringSpan;
        return result;
    }

    public UnicodeSet set(int start, int end) {
        this.checkFrozen();
        this.clear();
        this.complement(start, end);
        return this;
    }

    public UnicodeSet set(UnicodeSet other) {
        this.checkFrozen();
        this.list = (int[])other.list.clone();
        this.len = other.len;
        this.pat = other.pat;
        this.strings = new TreeSet<String>((SortedSet<String>)other.strings);
        return this;
    }

    public final UnicodeSet applyPattern(String pattern) {
        this.checkFrozen();
        return this.applyPattern(pattern, null, null, 1);
    }

    public UnicodeSet applyPattern(String pattern, boolean ignoreWhitespace) {
        this.checkFrozen();
        return this.applyPattern(pattern, null, null, ignoreWhitespace ? 1 : 0);
    }

    public UnicodeSet applyPattern(String pattern, int options) {
        this.checkFrozen();
        return this.applyPattern(pattern, null, null, options);
    }

    public static boolean resemblesPattern(String pattern, int pos) {
        return pos + 1 < pattern.length() && pattern.charAt(pos) == '[' || UnicodeSet.resemblesPropertyPattern(pattern, pos);
    }

    private static void _appendToPat(StringBuffer buf, String s2, boolean escapeUnprintable) {
        int cp2;
        for (int i2 = 0; i2 < s2.length(); i2 += Character.charCount(cp2)) {
            cp2 = s2.codePointAt(i2);
            UnicodeSet._appendToPat(buf, cp2, escapeUnprintable);
        }
    }

    private static void _appendToPat(StringBuffer buf, int c2, boolean escapeUnprintable) {
        if (escapeUnprintable && Utility.isUnprintable(c2) && Utility.escapeUnprintable(buf, c2)) {
            return;
        }
        switch (c2) {
            case 36: 
            case 38: 
            case 45: 
            case 58: 
            case 91: 
            case 92: 
            case 93: 
            case 94: 
            case 123: 
            case 125: {
                buf.append('\\');
                break;
            }
            default: {
                if (!PatternProps.isWhiteSpace(c2)) break;
                buf.append('\\');
            }
        }
        UTF16.append(buf, c2);
    }

    @Override
    public String toPattern(boolean escapeUnprintable) {
        StringBuffer result = new StringBuffer();
        return this._toPattern(result, escapeUnprintable).toString();
    }

    private StringBuffer _toPattern(StringBuffer result, boolean escapeUnprintable) {
        if (this.pat != null) {
            int backslashCount = 0;
            int i2 = 0;
            while (i2 < this.pat.length()) {
                int c2 = UTF16.charAt(this.pat, i2);
                i2 += UTF16.getCharCount(c2);
                if (escapeUnprintable && Utility.isUnprintable(c2)) {
                    if (backslashCount % 2 != 0) {
                        result.setLength(result.length() - 1);
                    }
                    Utility.escapeUnprintable(result, c2);
                    backslashCount = 0;
                    continue;
                }
                UTF16.append(result, c2);
                if (c2 == 92) {
                    ++backslashCount;
                    continue;
                }
                backslashCount = 0;
            }
            return result;
        }
        return this._generatePattern(result, escapeUnprintable, true);
    }

    public StringBuffer _generatePattern(StringBuffer result, boolean escapeUnprintable) {
        return this._generatePattern(result, escapeUnprintable, true);
    }

    public StringBuffer _generatePattern(StringBuffer result, boolean escapeUnprintable, boolean includeStrings) {
        int end;
        int start;
        int i2;
        result.append('[');
        int count = this.getRangeCount();
        if (count > 1 && this.getRangeStart(0) == 0 && this.getRangeEnd(count - 1) == 0x10FFFF) {
            result.append('^');
            for (i2 = 1; i2 < count; ++i2) {
                start = this.getRangeEnd(i2 - 1) + 1;
                end = this.getRangeStart(i2) - 1;
                UnicodeSet._appendToPat(result, start, escapeUnprintable);
                if (start == end) continue;
                if (start + 1 != end) {
                    result.append('-');
                }
                UnicodeSet._appendToPat(result, end, escapeUnprintable);
            }
        } else {
            for (i2 = 0; i2 < count; ++i2) {
                start = this.getRangeStart(i2);
                end = this.getRangeEnd(i2);
                UnicodeSet._appendToPat(result, start, escapeUnprintable);
                if (start == end) continue;
                if (start + 1 != end) {
                    result.append('-');
                }
                UnicodeSet._appendToPat(result, end, escapeUnprintable);
            }
        }
        if (includeStrings && this.strings.size() > 0) {
            for (String s2 : this.strings) {
                result.append('{');
                UnicodeSet._appendToPat(result, s2, escapeUnprintable);
                result.append('}');
            }
        }
        return result.append(']');
    }

    public int size() {
        int n2 = 0;
        int count = this.getRangeCount();
        for (int i2 = 0; i2 < count; ++i2) {
            n2 += this.getRangeEnd(i2) - this.getRangeStart(i2) + 1;
        }
        return n2 + this.strings.size();
    }

    public boolean isEmpty() {
        return this.len == 1 && this.strings.size() == 0;
    }

    @Override
    public boolean matchesIndexValue(int v2) {
        for (int i2 = 0; i2 < this.getRangeCount(); ++i2) {
            int high;
            int low = this.getRangeStart(i2);
            if (!((low & 0xFFFFFF00) == ((high = this.getRangeEnd(i2)) & 0xFFFFFF00) ? (low & 0xFF) <= v2 && v2 <= (high & 0xFF) : (low & 0xFF) <= v2 || v2 <= (high & 0xFF))) continue;
            return true;
        }
        if (this.strings.size() != 0) {
            for (String s2 : this.strings) {
                int c2 = UTF16.charAt(s2, 0);
                if ((c2 & 0xFF) != v2) continue;
                return true;
            }
        }
        return false;
    }

    @Override
    public int matches(Replaceable text, int[] offset, int limit, boolean incremental) {
        if (offset[0] == limit) {
            if (this.contains(65535)) {
                return incremental ? 1 : 2;
            }
            return 0;
        }
        if (this.strings.size() != 0) {
            boolean forward = offset[0] < limit;
            char firstChar = text.charAt(offset[0]);
            int highWaterLength = 0;
            for (String trial : this.strings) {
                char c2 = trial.charAt(forward ? 0 : trial.length() - 1);
                if (forward && c2 > firstChar) break;
                if (c2 != firstChar) continue;
                int length = UnicodeSet.matchRest(text, offset[0], limit, trial);
                if (incremental) {
                    int maxLen;
                    int n2 = maxLen = forward ? limit - offset[0] : offset[0] - limit;
                    if (length == maxLen) {
                        return 1;
                    }
                }
                if (length != trial.length()) continue;
                if (length > highWaterLength) {
                    highWaterLength = length;
                }
                if (!forward || length >= highWaterLength) continue;
                break;
            }
            if (highWaterLength != 0) {
                offset[0] = offset[0] + (forward ? highWaterLength : -highWaterLength);
                return 2;
            }
        }
        return super.matches(text, offset, limit, incremental);
    }

    private static int matchRest(Replaceable text, int start, int limit, String s2) {
        int maxLen;
        int slen = s2.length();
        if (start < limit) {
            maxLen = limit - start;
            if (maxLen > slen) {
                maxLen = slen;
            }
            for (int i2 = 1; i2 < maxLen; ++i2) {
                if (text.charAt(start + i2) == s2.charAt(i2)) continue;
                return 0;
            }
        } else {
            maxLen = start - limit;
            if (maxLen > slen) {
                maxLen = slen;
            }
            --slen;
            for (int i3 = 1; i3 < maxLen; ++i3) {
                if (text.charAt(start - i3) == s2.charAt(slen - i3)) continue;
                return 0;
            }
        }
        return maxLen;
    }

    public int matchesAt(CharSequence text, int offset) {
        int cp2;
        int lastLen;
        block4: {
            lastLen = -1;
            if (this.strings.size() != 0) {
                int tempLen;
                char firstChar = text.charAt(offset);
                String trial = null;
                Iterator<String> it2 = this.strings.iterator();
                while (it2.hasNext()) {
                    trial = it2.next();
                    char firstStringChar = trial.charAt(0);
                    if (firstStringChar < firstChar || firstStringChar <= firstChar) continue;
                    break block4;
                }
                while (lastLen <= (tempLen = UnicodeSet.matchesAt(text, offset, trial))) {
                    lastLen = tempLen;
                    if (!it2.hasNext()) break;
                    trial = it2.next();
                }
            }
        }
        if (lastLen < 2 && this.contains(cp2 = UTF16.charAt(text, offset))) {
            lastLen = UTF16.getCharCount(cp2);
        }
        return offset + lastLen;
    }

    private static int matchesAt(CharSequence text, int offsetInText, CharSequence substring) {
        int len = substring.length();
        int textLength = text.length();
        if (textLength + offsetInText > len) {
            return -1;
        }
        int i2 = 0;
        int j2 = offsetInText;
        while (i2 < len) {
            char tc2;
            char pc2 = substring.charAt(i2);
            if (pc2 != (tc2 = text.charAt(j2))) {
                return -1;
            }
            ++i2;
            ++j2;
        }
        return i2;
    }

    @Override
    public void addMatchSetTo(UnicodeSet toUnionTo) {
        toUnionTo.addAll(this);
    }

    public int indexOf(int c2) {
        if (c2 < 0 || c2 > 0x10FFFF) {
            throw new IllegalArgumentException("Invalid code point U+" + Utility.hex(c2, 6));
        }
        int i2 = 0;
        int n2 = 0;
        int start;
        while (c2 >= (start = this.list[i2++])) {
            int limit;
            if (c2 < (limit = this.list[i2++])) {
                return n2 + c2 - start;
            }
            n2 += limit - start;
        }
        return -1;
    }

    public int charAt(int index) {
        if (index >= 0) {
            int len2 = this.len & 0xFFFFFFFE;
            int i2 = 0;
            while (i2 < len2) {
                int start;
                int count;
                if (index < (count = this.list[i2++] - (start = this.list[i2++]))) {
                    return start + index;
                }
                index -= count;
            }
        }
        return -1;
    }

    public UnicodeSet add(int start, int end) {
        this.checkFrozen();
        return this.add_unchecked(start, end);
    }

    public UnicodeSet addAll(int start, int end) {
        this.checkFrozen();
        return this.add_unchecked(start, end);
    }

    private UnicodeSet add_unchecked(int start, int end) {
        if (start < 0 || start > 0x10FFFF) {
            throw new IllegalArgumentException("Invalid code point U+" + Utility.hex(start, 6));
        }
        if (end < 0 || end > 0x10FFFF) {
            throw new IllegalArgumentException("Invalid code point U+" + Utility.hex(end, 6));
        }
        if (start < end) {
            this.add(this.range(start, end), 2, 0);
        } else if (start == end) {
            this.add(start);
        }
        return this;
    }

    public final UnicodeSet add(int c2) {
        this.checkFrozen();
        return this.add_unchecked(c2);
    }

    private final UnicodeSet add_unchecked(int c2) {
        if (c2 < 0 || c2 > 0x10FFFF) {
            throw new IllegalArgumentException("Invalid code point U+" + Utility.hex(c2, 6));
        }
        int i2 = this.findCodePoint(c2);
        if ((i2 & 1) != 0) {
            return this;
        }
        if (c2 == this.list[i2] - 1) {
            this.list[i2] = c2;
            if (c2 == 0x10FFFF) {
                this.ensureCapacity(this.len + 1);
                this.list[this.len++] = 0x110000;
            }
            if (i2 > 0 && c2 == this.list[i2 - 1]) {
                System.arraycopy(this.list, i2 + 1, this.list, i2 - 1, this.len - i2 - 1);
                this.len -= 2;
            }
        } else if (i2 > 0 && c2 == this.list[i2 - 1]) {
            int n2 = i2 - 1;
            this.list[n2] = this.list[n2] + 1;
        } else {
            if (this.len + 2 > this.list.length) {
                int[] temp = new int[this.len + 2 + 16];
                if (i2 != 0) {
                    System.arraycopy(this.list, 0, temp, 0, i2);
                }
                System.arraycopy(this.list, i2, temp, i2 + 2, this.len - i2);
                this.list = temp;
            } else {
                System.arraycopy(this.list, i2, this.list, i2 + 2, this.len - i2);
            }
            this.list[i2] = c2;
            this.list[i2 + 1] = c2 + 1;
            this.len += 2;
        }
        this.pat = null;
        return this;
    }

    public final UnicodeSet add(CharSequence s2) {
        this.checkFrozen();
        int cp2 = UnicodeSet.getSingleCP(s2);
        if (cp2 < 0) {
            this.strings.add(((Object)s2).toString());
            this.pat = null;
        } else {
            this.add_unchecked(cp2, cp2);
        }
        return this;
    }

    private static int getSingleCP(CharSequence s2) {
        if (s2.length() < 1) {
            throw new IllegalArgumentException("Can't use zero-length strings in UnicodeSet");
        }
        if (s2.length() > 2) {
            return -1;
        }
        if (s2.length() == 1) {
            return s2.charAt(0);
        }
        int cp2 = UTF16.charAt(s2, 0);
        if (cp2 > 65535) {
            return cp2;
        }
        return -1;
    }

    public final UnicodeSet addAll(CharSequence s2) {
        int cp2;
        this.checkFrozen();
        for (int i2 = 0; i2 < s2.length(); i2 += UTF16.getCharCount(cp2)) {
            cp2 = UTF16.charAt(s2, i2);
            this.add_unchecked(cp2, cp2);
        }
        return this;
    }

    public final UnicodeSet retainAll(String s2) {
        return this.retainAll(UnicodeSet.fromAll(s2));
    }

    public final UnicodeSet complementAll(String s2) {
        return this.complementAll(UnicodeSet.fromAll(s2));
    }

    public final UnicodeSet removeAll(String s2) {
        return this.removeAll(UnicodeSet.fromAll(s2));
    }

    public final UnicodeSet removeAllStrings() {
        this.checkFrozen();
        if (this.strings.size() != 0) {
            this.strings.clear();
            this.pat = null;
        }
        return this;
    }

    public static UnicodeSet from(String s2) {
        return new UnicodeSet().add(s2);
    }

    public static UnicodeSet fromAll(String s2) {
        return new UnicodeSet().addAll((CharSequence)s2);
    }

    public UnicodeSet retain(int start, int end) {
        this.checkFrozen();
        if (start < 0 || start > 0x10FFFF) {
            throw new IllegalArgumentException("Invalid code point U+" + Utility.hex(start, 6));
        }
        if (end < 0 || end > 0x10FFFF) {
            throw new IllegalArgumentException("Invalid code point U+" + Utility.hex(end, 6));
        }
        if (start <= end) {
            this.retain(this.range(start, end), 2, 0);
        } else {
            this.clear();
        }
        return this;
    }

    public final UnicodeSet retain(int c2) {
        return this.retain(c2, c2);
    }

    public final UnicodeSet retain(String s2) {
        int cp2 = UnicodeSet.getSingleCP(s2);
        if (cp2 < 0) {
            boolean isIn = this.strings.contains(s2);
            if (isIn && this.size() == 1) {
                return this;
            }
            this.clear();
            this.strings.add(s2);
            this.pat = null;
        } else {
            this.retain(cp2, cp2);
        }
        return this;
    }

    public UnicodeSet remove(int start, int end) {
        this.checkFrozen();
        if (start < 0 || start > 0x10FFFF) {
            throw new IllegalArgumentException("Invalid code point U+" + Utility.hex(start, 6));
        }
        if (end < 0 || end > 0x10FFFF) {
            throw new IllegalArgumentException("Invalid code point U+" + Utility.hex(end, 6));
        }
        if (start <= end) {
            this.retain(this.range(start, end), 2, 2);
        }
        return this;
    }

    public final UnicodeSet remove(int c2) {
        return this.remove(c2, c2);
    }

    public final UnicodeSet remove(String s2) {
        int cp2 = UnicodeSet.getSingleCP(s2);
        if (cp2 < 0) {
            this.strings.remove(s2);
            this.pat = null;
        } else {
            this.remove(cp2, cp2);
        }
        return this;
    }

    public UnicodeSet complement(int start, int end) {
        this.checkFrozen();
        if (start < 0 || start > 0x10FFFF) {
            throw new IllegalArgumentException("Invalid code point U+" + Utility.hex(start, 6));
        }
        if (end < 0 || end > 0x10FFFF) {
            throw new IllegalArgumentException("Invalid code point U+" + Utility.hex(end, 6));
        }
        if (start <= end) {
            this.xor(this.range(start, end), 2, 0);
        }
        this.pat = null;
        return this;
    }

    public final UnicodeSet complement(int c2) {
        return this.complement(c2, c2);
    }

    public UnicodeSet complement() {
        this.checkFrozen();
        if (this.list[0] == 0) {
            System.arraycopy(this.list, 1, this.list, 0, this.len - 1);
            --this.len;
        } else {
            this.ensureCapacity(this.len + 1);
            System.arraycopy(this.list, 0, this.list, 1, this.len);
            this.list[0] = 0;
            ++this.len;
        }
        this.pat = null;
        return this;
    }

    public final UnicodeSet complement(String s2) {
        this.checkFrozen();
        int cp2 = UnicodeSet.getSingleCP(s2);
        if (cp2 < 0) {
            if (this.strings.contains(s2)) {
                this.strings.remove(s2);
            } else {
                this.strings.add(s2);
            }
            this.pat = null;
        } else {
            this.complement(cp2, cp2);
        }
        return this;
    }

    @Override
    public boolean contains(int c2) {
        if (c2 < 0 || c2 > 0x10FFFF) {
            throw new IllegalArgumentException("Invalid code point U+" + Utility.hex(c2, 6));
        }
        if (this.bmpSet != null) {
            return this.bmpSet.contains(c2);
        }
        if (this.stringSpan != null) {
            return this.stringSpan.contains(c2);
        }
        int i2 = this.findCodePoint(c2);
        return (i2 & 1) != 0;
    }

    private final int findCodePoint(int c2) {
        if (c2 < this.list[0]) {
            return 0;
        }
        if (this.len >= 2 && c2 >= this.list[this.len - 2]) {
            return this.len - 1;
        }
        int lo2 = 0;
        int hi2 = this.len - 1;
        int i2;
        while ((i2 = lo2 + hi2 >>> 1) != lo2) {
            if (c2 < this.list[i2]) {
                hi2 = i2;
                continue;
            }
            lo2 = i2;
        }
        return hi2;
    }

    public boolean contains(int start, int end) {
        if (start < 0 || start > 0x10FFFF) {
            throw new IllegalArgumentException("Invalid code point U+" + Utility.hex(start, 6));
        }
        if (end < 0 || end > 0x10FFFF) {
            throw new IllegalArgumentException("Invalid code point U+" + Utility.hex(end, 6));
        }
        int i2 = this.findCodePoint(start);
        return (i2 & 1) != 0 && end < this.list[i2];
    }

    public final boolean contains(String s2) {
        int cp2 = UnicodeSet.getSingleCP(s2);
        if (cp2 < 0) {
            return this.strings.contains(s2);
        }
        return this.contains(cp2);
    }

    public boolean containsAll(UnicodeSet b2) {
        block6: {
            int[] listB = b2.list;
            boolean needA = true;
            boolean needB = true;
            int aPtr = 0;
            int bPtr = 0;
            int aLen = this.len - 1;
            int bLen = b2.len - 1;
            int startA = 0;
            int startB = 0;
            int limitA = 0;
            int limitB = 0;
            while (true) {
                if (needA) {
                    if (aPtr >= aLen) {
                        if (!needB || bPtr < bLen) {
                            return false;
                        }
                        break block6;
                    }
                    startA = this.list[aPtr++];
                    limitA = this.list[aPtr++];
                }
                if (needB) {
                    if (bPtr >= bLen) break block6;
                    startB = listB[bPtr++];
                    limitB = listB[bPtr++];
                }
                if (startB >= limitA) {
                    needA = true;
                    needB = false;
                    continue;
                }
                if (startB < startA || limitB > limitA) break;
                needA = false;
                needB = true;
            }
            return false;
        }
        return this.strings.containsAll(b2.strings);
    }

    public boolean containsAll(String s2) {
        int cp2;
        for (int i2 = 0; i2 < s2.length(); i2 += UTF16.getCharCount(cp2)) {
            cp2 = UTF16.charAt(s2, i2);
            if (this.contains(cp2)) continue;
            if (this.strings.size() == 0) {
                return false;
            }
            return this.containsAll(s2, 0);
        }
        return true;
    }

    private boolean containsAll(String s2, int i2) {
        if (i2 >= s2.length()) {
            return true;
        }
        int cp2 = UTF16.charAt(s2, i2);
        if (this.contains(cp2) && this.containsAll(s2, i2 + UTF16.getCharCount(cp2))) {
            return true;
        }
        for (String setStr : this.strings) {
            if (!s2.startsWith(setStr, i2) || !this.containsAll(s2, i2 + setStr.length())) continue;
            return true;
        }
        return false;
    }

    public String getRegexEquivalent() {
        if (this.strings.size() == 0) {
            return this.toString();
        }
        StringBuffer result = new StringBuffer("(?:");
        this._generatePattern(result, true, false);
        for (String s2 : this.strings) {
            result.append('|');
            UnicodeSet._appendToPat(result, s2, true);
        }
        return result.append(")").toString();
    }

    public boolean containsNone(int start, int end) {
        if (start < 0 || start > 0x10FFFF) {
            throw new IllegalArgumentException("Invalid code point U+" + Utility.hex(start, 6));
        }
        if (end < 0 || end > 0x10FFFF) {
            throw new IllegalArgumentException("Invalid code point U+" + Utility.hex(end, 6));
        }
        int i2 = -1;
        while (start >= this.list[++i2]) {
        }
        return (i2 & 1) == 0 && end < this.list[i2];
    }

    public boolean containsNone(UnicodeSet b2) {
        block4: {
            int[] listB = b2.list;
            boolean needA = true;
            boolean needB = true;
            int aPtr = 0;
            int bPtr = 0;
            int aLen = this.len - 1;
            int bLen = b2.len - 1;
            int startA = 0;
            int startB = 0;
            int limitA = 0;
            int limitB = 0;
            while (true) {
                if (needA) {
                    if (aPtr >= aLen) break block4;
                    startA = this.list[aPtr++];
                    limitA = this.list[aPtr++];
                }
                if (needB) {
                    if (bPtr >= bLen) break block4;
                    startB = listB[bPtr++];
                    limitB = listB[bPtr++];
                }
                if (startB >= limitA) {
                    needA = true;
                    needB = false;
                    continue;
                }
                if (startA < limitB) break;
                needA = false;
                needB = true;
            }
            return false;
        }
        return SortedSetRelation.hasRelation(this.strings, 5, b2.strings);
    }

    public boolean containsNone(String s2) {
        return this.span(s2, SpanCondition.NOT_CONTAINED) == s2.length();
    }

    public final boolean containsSome(int start, int end) {
        return !this.containsNone(start, end);
    }

    public final boolean containsSome(UnicodeSet s2) {
        return !this.containsNone(s2);
    }

    public final boolean containsSome(String s2) {
        return !this.containsNone(s2);
    }

    public UnicodeSet addAll(UnicodeSet c2) {
        this.checkFrozen();
        this.add(c2.list, c2.len, 0);
        this.strings.addAll(c2.strings);
        return this;
    }

    public UnicodeSet retainAll(UnicodeSet c2) {
        this.checkFrozen();
        this.retain(c2.list, c2.len, 0);
        this.strings.retainAll(c2.strings);
        return this;
    }

    public UnicodeSet removeAll(UnicodeSet c2) {
        this.checkFrozen();
        this.retain(c2.list, c2.len, 2);
        this.strings.removeAll(c2.strings);
        return this;
    }

    public UnicodeSet complementAll(UnicodeSet c2) {
        this.checkFrozen();
        this.xor(c2.list, c2.len, 0);
        SortedSetRelation.doOperation(this.strings, 5, c2.strings);
        return this;
    }

    public UnicodeSet clear() {
        this.checkFrozen();
        this.list[0] = 0x110000;
        this.len = 1;
        this.pat = null;
        this.strings.clear();
        return this;
    }

    public int getRangeCount() {
        return this.len / 2;
    }

    public int getRangeStart(int index) {
        return this.list[index * 2];
    }

    public int getRangeEnd(int index) {
        return this.list[index * 2 + 1] - 1;
    }

    public UnicodeSet compact() {
        this.checkFrozen();
        if (this.len != this.list.length) {
            int[] temp = new int[this.len];
            System.arraycopy(this.list, 0, temp, 0, this.len);
            this.list = temp;
        }
        this.rangeList = null;
        this.buffer = null;
        return this;
    }

    public boolean equals(Object o2) {
        if (o2 == null) {
            return false;
        }
        if (this == o2) {
            return true;
        }
        try {
            UnicodeSet that = (UnicodeSet)o2;
            if (this.len != that.len) {
                return false;
            }
            for (int i2 = 0; i2 < this.len; ++i2) {
                if (this.list[i2] == that.list[i2]) continue;
                return false;
            }
            if (!this.strings.equals(that.strings)) {
                return false;
            }
        }
        catch (Exception e2) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        int result = this.len;
        for (int i2 = 0; i2 < this.len; ++i2) {
            result *= 1000003;
            result += this.list[i2];
        }
        return result;
    }

    public String toString() {
        return this.toPattern(true);
    }

    public UnicodeSet applyPattern(String pattern, ParsePosition pos, SymbolTable symbols, int options) {
        boolean parsePositionWasNull;
        boolean bl2 = parsePositionWasNull = pos == null;
        if (parsePositionWasNull) {
            pos = new ParsePosition(0);
        }
        StringBuffer rebuiltPat = new StringBuffer();
        RuleCharacterIterator chars = new RuleCharacterIterator(pattern, symbols, pos);
        this.applyPattern(chars, symbols, rebuiltPat, options);
        if (chars.inVariable()) {
            UnicodeSet.syntaxError(chars, "Extra chars in variable value");
        }
        this.pat = rebuiltPat.toString();
        if (parsePositionWasNull) {
            int i2 = pos.getIndex();
            if ((options & 1) != 0) {
                i2 = PatternProps.skipWhiteSpace(pattern, i2);
            }
            if (i2 != pattern.length()) {
                throw new IllegalArgumentException("Parse of \"" + pattern + "\" failed at " + i2);
            }
        }
        return this;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    void applyPattern(RuleCharacterIterator chars, SymbolTable symbols, StringBuffer rebuiltPat, int options) {
        int opts = 3;
        if ((options & 1) != 0) {
            opts |= 4;
        }
        StringBuffer patBuf = new StringBuffer();
        StringBuffer buf = null;
        boolean usePat = false;
        UnicodeSet scratch = null;
        Object backup = null;
        char lastItem = '\u0000';
        int lastChar = 0;
        int mode = 0;
        char op2 = '\u0000';
        boolean invert = false;
        this.clear();
        block25: while (mode != 2 && !chars.atEnd()) {
            int setMode;
            UnicodeSet nested;
            boolean literal;
            int c2;
            block67: {
                UnicodeMatcher m2;
                block69: {
                    block68: {
                        c2 = 0;
                        literal = false;
                        nested = null;
                        setMode = 0;
                        if (!UnicodeSet.resemblesPropertyPattern(chars, opts)) break block68;
                        setMode = 2;
                        break block67;
                    }
                    backup = chars.getPos(backup);
                    c2 = chars.next(opts);
                    literal = chars.isEscaped();
                    if (c2 != 91 || literal) break block69;
                    if (mode == 1) {
                        chars.setPos(backup);
                        setMode = 1;
                        break block67;
                    } else {
                        mode = 1;
                        patBuf.append('[');
                        backup = chars.getPos(backup);
                        c2 = chars.next(opts);
                        literal = chars.isEscaped();
                        if (c2 == 94 && !literal) {
                            invert = true;
                            patBuf.append('^');
                            backup = chars.getPos(backup);
                            c2 = chars.next(opts);
                            literal = chars.isEscaped();
                        }
                        if (c2 == 45) {
                            literal = true;
                            break block67;
                        } else {
                            chars.setPos(backup);
                            continue;
                        }
                    }
                }
                if (symbols != null && (m2 = symbols.lookupMatcher(c2)) != null) {
                    try {
                        nested = (UnicodeSet)m2;
                        setMode = 3;
                    }
                    catch (ClassCastException e2) {
                        UnicodeSet.syntaxError(chars, "Syntax error");
                    }
                }
            }
            if (setMode != 0) {
                if (lastItem == '\u0001') {
                    if (op2 != '\u0000') {
                        UnicodeSet.syntaxError(chars, "Char expected after operator");
                    }
                    this.add_unchecked(lastChar, lastChar);
                    UnicodeSet._appendToPat(patBuf, lastChar, false);
                    op2 = '\u0000';
                    lastItem = '\u0000';
                }
                if (op2 == '-' || op2 == '&') {
                    patBuf.append(op2);
                }
                if (nested == null) {
                    if (scratch == null) {
                        scratch = new UnicodeSet();
                    }
                    nested = scratch;
                }
                switch (setMode) {
                    case 1: {
                        nested.applyPattern(chars, symbols, patBuf, options);
                        break;
                    }
                    case 2: {
                        chars.skipIgnored(opts);
                        nested.applyPropertyPattern(chars, patBuf, symbols);
                        break;
                    }
                    case 3: {
                        nested._toPattern(patBuf, false);
                        break;
                    }
                }
                usePat = true;
                if (mode == 0) {
                    this.set(nested);
                    mode = 2;
                    break;
                }
                switch (op2) {
                    case '-': {
                        this.removeAll(nested);
                        break;
                    }
                    case '&': {
                        this.retainAll(nested);
                        break;
                    }
                    case '\u0000': {
                        this.addAll(nested);
                        break;
                    }
                }
                op2 = '\u0000';
                lastItem = '\u0002';
                continue;
            }
            if (mode == 0) {
                UnicodeSet.syntaxError(chars, "Missing '['");
            }
            if (!literal) {
                switch (c2) {
                    case 93: {
                        if (lastItem == '\u0001') {
                            this.add_unchecked(lastChar, lastChar);
                            UnicodeSet._appendToPat(patBuf, lastChar, false);
                        }
                        if (op2 == '-') {
                            this.add_unchecked(op2, op2);
                            patBuf.append(op2);
                        } else if (op2 == '&') {
                            UnicodeSet.syntaxError(chars, "Trailing '&'");
                        }
                        patBuf.append(']');
                        mode = 2;
                        continue block25;
                    }
                    case 45: {
                        if (op2 == '\u0000') {
                            if (lastItem != '\u0000') {
                                op2 = (char)c2;
                                continue block25;
                            }
                            this.add_unchecked(c2, c2);
                            c2 = chars.next(opts);
                            literal = chars.isEscaped();
                            if (c2 == 93 && !literal) {
                                patBuf.append("-]");
                                mode = 2;
                                continue block25;
                            }
                        }
                        UnicodeSet.syntaxError(chars, "'-' not after char or set");
                        break;
                    }
                    case 38: {
                        if (lastItem == '\u0002' && op2 == '\u0000') {
                            op2 = (char)c2;
                            continue block25;
                        }
                        UnicodeSet.syntaxError(chars, "'&' not after set");
                        break;
                    }
                    case 94: {
                        UnicodeSet.syntaxError(chars, "'^' not after '['");
                        break;
                    }
                    case 123: {
                        if (op2 != '\u0000') {
                            UnicodeSet.syntaxError(chars, "Missing operand after operator");
                        }
                        if (lastItem == '\u0001') {
                            this.add_unchecked(lastChar, lastChar);
                            UnicodeSet._appendToPat(patBuf, lastChar, false);
                        }
                        lastItem = '\u0000';
                        if (buf == null) {
                            buf = new StringBuffer();
                        } else {
                            buf.setLength(0);
                        }
                        boolean ok2 = false;
                        while (!chars.atEnd()) {
                            c2 = chars.next(opts);
                            literal = chars.isEscaped();
                            if (c2 == 125 && !literal) {
                                ok2 = true;
                                break;
                            }
                            UTF16.append(buf, c2);
                        }
                        if (buf.length() < 1 || !ok2) {
                            UnicodeSet.syntaxError(chars, "Invalid multicharacter string");
                        }
                        this.add(buf.toString());
                        patBuf.append('{');
                        UnicodeSet._appendToPat(patBuf, buf.toString(), false);
                        patBuf.append('}');
                        continue block25;
                    }
                    case 36: {
                        boolean anchor;
                        backup = chars.getPos(backup);
                        c2 = chars.next(opts);
                        literal = chars.isEscaped();
                        boolean bl2 = anchor = c2 == 93 && !literal;
                        if (symbols == null && !anchor) {
                            c2 = 36;
                            chars.setPos(backup);
                            break;
                        }
                        if (anchor && op2 == '\u0000') {
                            if (lastItem == '\u0001') {
                                this.add_unchecked(lastChar, lastChar);
                                UnicodeSet._appendToPat(patBuf, lastChar, false);
                            }
                            this.add_unchecked(65535);
                            usePat = true;
                            patBuf.append('$').append(']');
                            mode = 2;
                            continue block25;
                        }
                        UnicodeSet.syntaxError(chars, "Unquoted '$'");
                        break;
                    }
                }
            }
            switch (lastItem) {
                case '\u0000': {
                    lastItem = '\u0001';
                    lastChar = c2;
                    break;
                }
                case '\u0001': {
                    if (op2 == '-') {
                        if (lastChar >= c2) {
                            UnicodeSet.syntaxError(chars, "Invalid range");
                        }
                        this.add_unchecked(lastChar, c2);
                        UnicodeSet._appendToPat(patBuf, lastChar, false);
                        patBuf.append(op2);
                        UnicodeSet._appendToPat(patBuf, c2, false);
                        op2 = '\u0000';
                        lastItem = '\u0000';
                        break;
                    }
                    this.add_unchecked(lastChar, lastChar);
                    UnicodeSet._appendToPat(patBuf, lastChar, false);
                    lastChar = c2;
                    break;
                }
                case '\u0002': {
                    if (op2 != '\u0000') {
                        UnicodeSet.syntaxError(chars, "Set expected after operator");
                    }
                    lastChar = c2;
                    lastItem = '\u0001';
                    continue block25;
                }
            }
        }
        if (mode != 2) {
            UnicodeSet.syntaxError(chars, "Missing ']'");
        }
        chars.skipIgnored(opts);
        if ((options & 2) != 0) {
            this.closeOver(2);
        }
        if (invert) {
            this.complement();
        }
        if (usePat) {
            rebuiltPat.append(patBuf.toString());
            return;
        }
        this._generatePattern(rebuiltPat, false, true);
    }

    private static void syntaxError(RuleCharacterIterator chars, String msg) {
        throw new IllegalArgumentException("Error: " + msg + " at \"" + Utility.escape(chars.toString()) + '\"');
    }

    public <T extends Collection<String>> T addAllTo(T target) {
        return UnicodeSet.addAllTo(this, target);
    }

    public String[] addAllTo(String[] target) {
        return UnicodeSet.addAllTo(this, target);
    }

    public static String[] toArray(UnicodeSet set) {
        return UnicodeSet.addAllTo(set, new String[set.size()]);
    }

    public UnicodeSet add(Collection<?> source) {
        return this.addAll(source);
    }

    public UnicodeSet addAll(Collection<?> source) {
        this.checkFrozen();
        for (Object o2 : source) {
            this.add(o2.toString());
        }
        return this;
    }

    private void ensureCapacity(int newLen) {
        if (newLen <= this.list.length) {
            return;
        }
        int[] temp = new int[newLen + 16];
        System.arraycopy(this.list, 0, temp, 0, this.len);
        this.list = temp;
    }

    private void ensureBufferCapacity(int newLen) {
        if (this.buffer != null && newLen <= this.buffer.length) {
            return;
        }
        this.buffer = new int[newLen + 16];
    }

    private int[] range(int start, int end) {
        if (this.rangeList == null) {
            this.rangeList = new int[]{start, end + 1, 0x110000};
        } else {
            this.rangeList[0] = start;
            this.rangeList[1] = end + 1;
        }
        return this.rangeList;
    }

    private UnicodeSet xor(int[] other, int otherLen, int polarity) {
        int b2;
        this.ensureBufferCapacity(this.len + otherLen);
        int i2 = 0;
        int j2 = 0;
        int k2 = 0;
        int a2 = this.list[i2++];
        if (polarity == 1 || polarity == 2) {
            b2 = 0;
            if (other[j2] == 0) {
                b2 = other[++j2];
            }
        } else {
            b2 = other[j2++];
        }
        while (true) {
            if (a2 < b2) {
                this.buffer[k2++] = a2;
                a2 = this.list[i2++];
                continue;
            }
            if (b2 < a2) {
                this.buffer[k2++] = b2;
                b2 = other[j2++];
                continue;
            }
            if (a2 == 0x110000) break;
            a2 = this.list[i2++];
            b2 = other[j2++];
        }
        this.buffer[k2++] = 0x110000;
        this.len = k2;
        int[] temp = this.list;
        this.list = this.buffer;
        this.buffer = temp;
        this.pat = null;
        return this;
    }

    /*
     * Enabled aggressive block sorting
     */
    private UnicodeSet add(int[] other, int otherLen, int polarity) {
        this.ensureBufferCapacity(this.len + otherLen);
        int i2 = 0;
        int j2 = 0;
        int k2 = 0;
        int a2 = this.list[i2++];
        int b2 = other[j2++];
        block6: while (true) {
            switch (polarity) {
                case 0: {
                    if (a2 < b2) {
                        if (k2 > 0 && a2 <= this.buffer[k2 - 1]) {
                            a2 = UnicodeSet.max(this.list[i2], this.buffer[--k2]);
                        } else {
                            this.buffer[k2++] = a2;
                            a2 = this.list[i2];
                        }
                        ++i2;
                        polarity ^= 1;
                        break;
                    }
                    if (b2 < a2) {
                        if (k2 > 0 && b2 <= this.buffer[k2 - 1]) {
                            b2 = UnicodeSet.max(other[j2], this.buffer[--k2]);
                        } else {
                            this.buffer[k2++] = b2;
                            b2 = other[j2];
                        }
                        ++j2;
                        polarity ^= 2;
                        break;
                    }
                    if (a2 == 0x110000) break block6;
                    if (k2 > 0 && a2 <= this.buffer[k2 - 1]) {
                        a2 = UnicodeSet.max(this.list[i2], this.buffer[--k2]);
                    } else {
                        this.buffer[k2++] = a2;
                        a2 = this.list[i2];
                    }
                    ++i2;
                    polarity ^= 1;
                    b2 = other[j2++];
                    polarity ^= 2;
                    break;
                }
                case 3: {
                    if (b2 <= a2) {
                        if (a2 == 0x110000) break block6;
                        this.buffer[k2++] = a2;
                    } else {
                        if (b2 == 0x110000) break block6;
                        this.buffer[k2++] = b2;
                    }
                    a2 = this.list[i2++];
                    polarity ^= 1;
                    b2 = other[j2++];
                    polarity ^= 2;
                    break;
                }
                case 1: {
                    if (a2 < b2) {
                        this.buffer[k2++] = a2;
                        a2 = this.list[i2++];
                        polarity ^= 1;
                        break;
                    }
                    if (b2 < a2) {
                        b2 = other[j2++];
                        polarity ^= 2;
                        break;
                    }
                    if (a2 == 0x110000) break block6;
                    a2 = this.list[i2++];
                    polarity ^= 1;
                    b2 = other[j2++];
                    polarity ^= 2;
                    break;
                }
                case 2: {
                    if (b2 < a2) {
                        this.buffer[k2++] = b2;
                        b2 = other[j2++];
                        polarity ^= 2;
                        break;
                    }
                    if (a2 < b2) {
                        a2 = this.list[i2++];
                        polarity ^= 1;
                        break;
                    }
                    if (a2 == 0x110000) break block6;
                    a2 = this.list[i2++];
                    polarity ^= 1;
                    b2 = other[j2++];
                    polarity ^= 2;
                }
            }
        }
        this.buffer[k2++] = 0x110000;
        this.len = k2;
        int[] temp = this.list;
        this.list = this.buffer;
        this.buffer = temp;
        this.pat = null;
        return this;
    }

    /*
     * Enabled aggressive block sorting
     */
    private UnicodeSet retain(int[] other, int otherLen, int polarity) {
        this.ensureBufferCapacity(this.len + otherLen);
        int i2 = 0;
        int j2 = 0;
        int k2 = 0;
        int a2 = this.list[i2++];
        int b2 = other[j2++];
        block6: while (true) {
            switch (polarity) {
                case 0: {
                    if (a2 < b2) {
                        a2 = this.list[i2++];
                        polarity ^= 1;
                        break;
                    }
                    if (b2 < a2) {
                        b2 = other[j2++];
                        polarity ^= 2;
                        break;
                    }
                    if (a2 == 0x110000) break block6;
                    this.buffer[k2++] = a2;
                    a2 = this.list[i2++];
                    polarity ^= 1;
                    b2 = other[j2++];
                    polarity ^= 2;
                    break;
                }
                case 3: {
                    if (a2 < b2) {
                        this.buffer[k2++] = a2;
                        a2 = this.list[i2++];
                        polarity ^= 1;
                        break;
                    }
                    if (b2 < a2) {
                        this.buffer[k2++] = b2;
                        b2 = other[j2++];
                        polarity ^= 2;
                        break;
                    }
                    if (a2 == 0x110000) break block6;
                    this.buffer[k2++] = a2;
                    a2 = this.list[i2++];
                    polarity ^= 1;
                    b2 = other[j2++];
                    polarity ^= 2;
                    break;
                }
                case 1: {
                    if (a2 < b2) {
                        a2 = this.list[i2++];
                        polarity ^= 1;
                        break;
                    }
                    if (b2 < a2) {
                        this.buffer[k2++] = b2;
                        b2 = other[j2++];
                        polarity ^= 2;
                        break;
                    }
                    if (a2 == 0x110000) break block6;
                    a2 = this.list[i2++];
                    polarity ^= 1;
                    b2 = other[j2++];
                    polarity ^= 2;
                    break;
                }
                case 2: {
                    if (b2 < a2) {
                        b2 = other[j2++];
                        polarity ^= 2;
                        break;
                    }
                    if (a2 < b2) {
                        this.buffer[k2++] = a2;
                        a2 = this.list[i2++];
                        polarity ^= 1;
                        break;
                    }
                    if (a2 == 0x110000) break block6;
                    a2 = this.list[i2++];
                    polarity ^= 1;
                    b2 = other[j2++];
                    polarity ^= 2;
                }
            }
        }
        this.buffer[k2++] = 0x110000;
        this.len = k2;
        int[] temp = this.list;
        this.list = this.buffer;
        this.buffer = temp;
        this.pat = null;
        return this;
    }

    private static final int max(int a2, int b2) {
        return a2 > b2 ? a2 : b2;
    }

    private static synchronized UnicodeSet getInclusions(int src) {
        if (INCLUSIONS == null) {
            INCLUSIONS = new UnicodeSet[12];
        }
        if (INCLUSIONS[src] == null) {
            UnicodeSet incl = new UnicodeSet();
            switch (src) {
                case 1: {
                    UCharacterProperty.INSTANCE.addPropertyStarts(incl);
                    break;
                }
                case 2: {
                    UCharacterProperty.INSTANCE.upropsvec_addPropertyStarts(incl);
                    break;
                }
                case 6: {
                    UCharacterProperty.INSTANCE.addPropertyStarts(incl);
                    UCharacterProperty.INSTANCE.upropsvec_addPropertyStarts(incl);
                    break;
                }
                case 7: {
                    Norm2AllModes.getNFCInstance().impl.addPropertyStarts(incl);
                    UCaseProps.INSTANCE.addPropertyStarts(incl);
                    break;
                }
                case 8: {
                    Norm2AllModes.getNFCInstance().impl.addPropertyStarts(incl);
                    break;
                }
                case 9: {
                    Norm2AllModes.getNFKCInstance().impl.addPropertyStarts(incl);
                    break;
                }
                case 10: {
                    Norm2AllModes.getNFKC_CFInstance().impl.addPropertyStarts(incl);
                    break;
                }
                case 11: {
                    Norm2AllModes.getNFCInstance().impl.addCanonIterPropertyStarts(incl);
                    break;
                }
                case 4: {
                    UCaseProps.INSTANCE.addPropertyStarts(incl);
                    break;
                }
                case 5: {
                    UBiDiProps.INSTANCE.addPropertyStarts(incl);
                    break;
                }
                default: {
                    throw new IllegalStateException("UnicodeSet.getInclusions(unknown src " + src + ")");
                }
            }
            UnicodeSet.INCLUSIONS[src] = incl;
        }
        return INCLUSIONS[src];
    }

    private UnicodeSet applyFilter(Filter filter, int src) {
        this.clear();
        int startHasProperty = -1;
        UnicodeSet inclusions = UnicodeSet.getInclusions(src);
        int limitRange = inclusions.getRangeCount();
        for (int j2 = 0; j2 < limitRange; ++j2) {
            int start = inclusions.getRangeStart(j2);
            int end = inclusions.getRangeEnd(j2);
            for (int ch = start; ch <= end; ++ch) {
                if (filter.contains(ch)) {
                    if (startHasProperty >= 0) continue;
                    startHasProperty = ch;
                    continue;
                }
                if (startHasProperty < 0) continue;
                this.add_unchecked(startHasProperty, ch - 1);
                startHasProperty = -1;
            }
        }
        if (startHasProperty >= 0) {
            this.add_unchecked(startHasProperty, 0x10FFFF);
        }
        return this;
    }

    private static String mungeCharName(String source) {
        source = PatternProps.trimWhiteSpace(source);
        StringBuilder buf = null;
        for (int i2 = 0; i2 < source.length(); ++i2) {
            int ch = source.charAt(i2);
            if (PatternProps.isWhiteSpace(ch)) {
                if (buf == null) {
                    buf = new StringBuilder().append(source, 0, i2);
                } else if (buf.charAt(buf.length() - 1) == ' ') continue;
                ch = 32;
            }
            if (buf == null) continue;
            buf.append((char)ch);
        }
        return buf == null ? source : buf.toString();
    }

    public UnicodeSet applyIntPropertyValue(int prop, int value) {
        this.checkFrozen();
        if (prop == 8192) {
            this.applyFilter(new GeneralCategoryMaskFilter(value), 1);
        } else if (prop == 28672) {
            this.applyFilter(new ScriptExtensionsFilter(value), 2);
        } else {
            this.applyFilter(new IntPropertyFilter(prop, value), UCharacterProperty.INSTANCE.getSource(prop));
        }
        return this;
    }

    public UnicodeSet applyPropertyAlias(String propertyAlias, String valueAlias) {
        return this.applyPropertyAlias(propertyAlias, valueAlias, null);
    }

    /*
     * Unable to fully structure code
     */
    public UnicodeSet applyPropertyAlias(String propertyAlias, String valueAlias, SymbolTable symbols) {
        block23: {
            block24: {
                block21: {
                    block22: {
                        this.checkFrozen();
                        mustNotBeEmpty = false;
                        invert = false;
                        if (symbols != null && symbols instanceof XSymbolTable && ((XSymbolTable)symbols).applyPropertyAlias(propertyAlias, valueAlias, this)) {
                            return this;
                        }
                        if (UnicodeSet.XSYMBOL_TABLE != null && UnicodeSet.XSYMBOL_TABLE.applyPropertyAlias(propertyAlias, valueAlias, this)) {
                            return this;
                        }
                        if (valueAlias.length() <= 0) break block21;
                        p = UCharacter.getPropertyEnum(propertyAlias);
                        if (p == 4101) {
                            p = 8192;
                        }
                        if (!(p >= 0 && p < 57 || p >= 4096 && p < 4117) && (p < 8192 || p >= 8193)) break block22;
                        try {
                            v = UCharacter.getPropertyValueEnum(p, valueAlias);
                        }
                        catch (IllegalArgumentException e) {
                            if (p == 4098 || p == 4112 || p == 4113) {
                                v = Integer.parseInt(PatternProps.trimWhiteSpace(valueAlias));
                                if (v >= 0 && v <= 255) ** GOTO lbl79
                                throw e;
                            }
                            throw e;
                        }
                    }
                    switch (p) {
                        case 12288: {
                            value = Double.parseDouble(PatternProps.trimWhiteSpace(valueAlias));
                            this.applyFilter(new NumericValueFilter(value), 1);
                            return this;
                        }
                        case 16389: {
                            buf = UnicodeSet.mungeCharName(valueAlias);
                            ch = UCharacter.getCharFromExtendedName(buf);
                            if (ch == -1) {
                                throw new IllegalArgumentException("Invalid character name");
                            }
                            this.clear();
                            this.add_unchecked(ch);
                            return this;
                        }
                        case 16395: {
                            throw new IllegalArgumentException("Unicode_1_Name (na1) not supported");
                        }
                        case 16384: {
                            version = VersionInfo.getInstance(UnicodeSet.mungeCharName(valueAlias));
                            this.applyFilter(new VersionFilter(version), 2);
                            return this;
                        }
                        case 28672: {
                            v = UCharacter.getPropertyValueEnum(4106, valueAlias);
                            break block23;
                        }
                        default: {
                            throw new IllegalArgumentException("Unsupported property");
                        }
                    }
                }
                pnames = UPropertyAliases.INSTANCE;
                p = 8192;
                v = pnames.getPropertyValueEnum(p, propertyAlias);
                if (v != -1 || (v = pnames.getPropertyValueEnum(p = 4106, propertyAlias)) != -1) break block23;
                p = pnames.getPropertyEnum(propertyAlias);
                if (p == -1) {
                    p = -1;
                }
                if (p < 0 || p >= 57) break block24;
                v = 1;
                break block23;
            }
            if (p != -1) ** GOTO lbl78
            if (0 == UPropertyAliases.compare("ANY", propertyAlias)) {
                this.set(0, 0x10FFFF);
                return this;
            }
            if (0 == UPropertyAliases.compare("ASCII", propertyAlias)) {
                this.set(0, 127);
                return this;
            }
            if (0 == UPropertyAliases.compare("Assigned", propertyAlias)) {
                p = 8192;
                v = 1;
                invert = true;
            } else {
                throw new IllegalArgumentException("Invalid property alias: " + propertyAlias + "=" + valueAlias);
lbl78:
                // 1 sources

                throw new IllegalArgumentException("Missing property value");
            }
        }
        this.applyIntPropertyValue(p, v);
        if (invert) {
            this.complement();
        }
        if (mustNotBeEmpty && this.isEmpty()) {
            throw new IllegalArgumentException("Invalid property value");
        }
        return this;
    }

    private static boolean resemblesPropertyPattern(String pattern, int pos) {
        if (pos + 5 > pattern.length()) {
            return false;
        }
        return pattern.regionMatches(pos, "[:", 0, 2) || pattern.regionMatches(true, pos, "\\p", 0, 2) || pattern.regionMatches(pos, "\\N", 0, 2);
    }

    private static boolean resemblesPropertyPattern(RuleCharacterIterator chars, int iterOpts) {
        boolean result = false;
        Object pos = chars.getPos(null);
        int c2 = chars.next(iterOpts &= 0xFFFFFFFD);
        if (c2 == 91 || c2 == 92) {
            int d2 = chars.next(iterOpts & 0xFFFFFFFB);
            result = c2 == 91 ? d2 == 58 : d2 == 78 || d2 == 112 || d2 == 80;
        }
        chars.setPos(pos);
        return result;
    }

    private UnicodeSet applyPropertyPattern(String pattern, ParsePosition ppos, SymbolTable symbols) {
        String valueName;
        String propName;
        int close;
        int pos = ppos.getIndex();
        if (pos + 5 > pattern.length()) {
            return null;
        }
        boolean posix = false;
        boolean isName = false;
        boolean invert = false;
        if (pattern.regionMatches(pos, "[:", 0, 2)) {
            posix = true;
            if ((pos = PatternProps.skipWhiteSpace(pattern, pos + 2)) < pattern.length() && pattern.charAt(pos) == '^') {
                ++pos;
                invert = true;
            }
        } else if (pattern.regionMatches(true, pos, "\\p", 0, 2) || pattern.regionMatches(pos, "\\N", 0, 2)) {
            char c2 = pattern.charAt(pos + 1);
            invert = c2 == 'P';
            isName = c2 == 'N';
            pos = PatternProps.skipWhiteSpace(pattern, pos + 2);
            if (pos == pattern.length() || pattern.charAt(pos++) != '{') {
                return null;
            }
        } else {
            return null;
        }
        if ((close = pattern.indexOf(posix ? ":]" : "}", pos)) < 0) {
            return null;
        }
        int equals = pattern.indexOf(61, pos);
        if (equals >= 0 && equals < close && !isName) {
            propName = pattern.substring(pos, equals);
            valueName = pattern.substring(equals + 1, close);
        } else {
            propName = pattern.substring(pos, close);
            valueName = "";
            if (isName) {
                valueName = propName;
                propName = "na";
            }
        }
        this.applyPropertyAlias(propName, valueName, symbols);
        if (invert) {
            this.complement();
        }
        ppos.setIndex(close + (posix ? 2 : 1));
        return this;
    }

    private void applyPropertyPattern(RuleCharacterIterator chars, StringBuffer rebuiltPat, SymbolTable symbols) {
        String patStr = chars.lookahead();
        ParsePosition pos = new ParsePosition(0);
        this.applyPropertyPattern(patStr, pos, symbols);
        if (pos.getIndex() == 0) {
            UnicodeSet.syntaxError(chars, "Invalid property pattern");
        }
        chars.jumpahead(pos.getIndex());
        rebuiltPat.append(patStr.substring(0, pos.getIndex()));
    }

    private static final void addCaseMapping(UnicodeSet set, int result, StringBuilder full) {
        if (result >= 0) {
            if (result > 31) {
                set.add(result);
            } else {
                set.add(full.toString());
                full.setLength(0);
            }
        }
    }

    public UnicodeSet closeOver(int attribute) {
        this.checkFrozen();
        if ((attribute & 6) != 0) {
            UCaseProps csp = UCaseProps.INSTANCE;
            UnicodeSet foldSet = new UnicodeSet(this);
            ULocale root = ULocale.ROOT;
            if ((attribute & 2) != 0) {
                foldSet.strings.clear();
            }
            int n2 = this.getRangeCount();
            StringBuilder full = new StringBuilder();
            int[] locCache = new int[1];
            for (int i2 = 0; i2 < n2; ++i2) {
                int cp2;
                int start = this.getRangeStart(i2);
                int end = this.getRangeEnd(i2);
                if ((attribute & 2) != 0) {
                    for (cp2 = start; cp2 <= end; ++cp2) {
                        csp.addCaseClosure(cp2, foldSet);
                    }
                    continue;
                }
                for (cp2 = start; cp2 <= end; ++cp2) {
                    int result = csp.toFullLower(cp2, null, full, root, locCache);
                    UnicodeSet.addCaseMapping(foldSet, result, full);
                    result = csp.toFullTitle(cp2, null, full, root, locCache);
                    UnicodeSet.addCaseMapping(foldSet, result, full);
                    result = csp.toFullUpper(cp2, null, full, root, locCache);
                    UnicodeSet.addCaseMapping(foldSet, result, full);
                    result = csp.toFullFolding(cp2, full, 0);
                    UnicodeSet.addCaseMapping(foldSet, result, full);
                }
            }
            if (!this.strings.isEmpty()) {
                if ((attribute & 2) != 0) {
                    for (String s2 : this.strings) {
                        String str = UCharacter.foldCase(s2, 0);
                        if (csp.addStringCaseClosure(str, foldSet)) continue;
                        foldSet.add(str);
                    }
                } else {
                    BreakIterator bi2 = BreakIterator.getWordInstance(root);
                    for (String str : this.strings) {
                        foldSet.add(UCharacter.toLowerCase(root, str));
                        foldSet.add(UCharacter.toTitleCase(root, str, bi2));
                        foldSet.add(UCharacter.toUpperCase(root, str));
                        foldSet.add(UCharacter.foldCase(str, 0));
                    }
                }
            }
            this.set(foldSet);
        }
        return this;
    }

    @Override
    public boolean isFrozen() {
        return this.bmpSet != null || this.stringSpan != null;
    }

    @Override
    public UnicodeSet freeze() {
        if (!this.isFrozen()) {
            this.buffer = null;
            if (this.list.length > this.len + 16) {
                int capacity = this.len == 0 ? 1 : this.len;
                int[] oldList = this.list;
                this.list = new int[capacity];
                int i2 = capacity;
                while (i2-- > 0) {
                    this.list[i2] = oldList[i2];
                }
            }
            if (!this.strings.isEmpty()) {
                this.stringSpan = new UnicodeSetStringSpan(this, new ArrayList<String>(this.strings), 63);
                if (!this.stringSpan.needsStringSpanUTF16()) {
                    this.stringSpan = null;
                }
            }
            if (this.stringSpan == null) {
                this.bmpSet = new BMPSet(this.list, this.len);
            }
        }
        return this;
    }

    public int span(CharSequence s2, SpanCondition spanCondition) {
        return this.span(s2, 0, spanCondition);
    }

    public int span(CharSequence s2, int start, SpanCondition spanCondition) {
        int c2;
        int which;
        UnicodeSetStringSpan strSpan;
        int end = s2.length();
        if (start < 0) {
            start = 0;
        } else if (start >= end) {
            return end;
        }
        if (this.bmpSet != null) {
            return start + this.bmpSet.span(s2, start, end, spanCondition);
        }
        int len = end - start;
        if (this.stringSpan != null) {
            return start + this.stringSpan.span(s2, start, len, spanCondition);
        }
        if (!this.strings.isEmpty() && (strSpan = new UnicodeSetStringSpan(this, new ArrayList<String>(this.strings), which = spanCondition == SpanCondition.NOT_CONTAINED ? 41 : 42)).needsStringSpanUTF16()) {
            return start + strSpan.span(s2, start, len, spanCondition);
        }
        boolean spanContained = spanCondition != SpanCondition.NOT_CONTAINED;
        int next = start;
        while (spanContained == this.contains(c2 = Character.codePointAt(s2, next)) && (next = Character.offsetByCodePoints(s2, next, 1)) < end) {
        }
        return next;
    }

    public int spanBack(CharSequence s2, SpanCondition spanCondition) {
        return this.spanBack(s2, s2.length(), spanCondition);
    }

    public int spanBack(CharSequence s2, int fromIndex, SpanCondition spanCondition) {
        int c2;
        int which;
        UnicodeSetStringSpan strSpan;
        if (fromIndex <= 0) {
            return 0;
        }
        if (fromIndex > s2.length()) {
            fromIndex = s2.length();
        }
        if (this.bmpSet != null) {
            return this.bmpSet.spanBack(s2, fromIndex, spanCondition);
        }
        if (this.stringSpan != null) {
            return this.stringSpan.spanBack(s2, fromIndex, spanCondition);
        }
        if (!this.strings.isEmpty() && (strSpan = new UnicodeSetStringSpan(this, new ArrayList<String>(this.strings), which = spanCondition == SpanCondition.NOT_CONTAINED ? 25 : 26)).needsStringSpanUTF16()) {
            return strSpan.spanBack(s2, fromIndex, spanCondition);
        }
        boolean spanContained = spanCondition != SpanCondition.NOT_CONTAINED;
        int prev = fromIndex;
        while (spanContained == this.contains(c2 = Character.codePointBefore(s2, prev)) && (prev = Character.offsetByCodePoints(s2, prev, -1)) > 0) {
        }
        return prev;
    }

    @Override
    public UnicodeSet cloneAsThawed() {
        UnicodeSet result = (UnicodeSet)this.clone();
        result.bmpSet = null;
        result.stringSpan = null;
        return result;
    }

    private void checkFrozen() {
        if (this.isFrozen()) {
            throw new UnsupportedOperationException("Attempt to modify frozen object");
        }
    }

    @Override
    public Iterator<String> iterator() {
        return new UnicodeSetIterator2(this);
    }

    public boolean containsAll(Collection<String> collection) {
        for (String o2 : collection) {
            if (this.contains(o2)) continue;
            return false;
        }
        return true;
    }

    public boolean containsNone(Collection<String> collection) {
        for (String o2 : collection) {
            if (!this.contains(o2)) continue;
            return false;
        }
        return true;
    }

    public final boolean containsSome(Collection<String> collection) {
        return !this.containsNone(collection);
    }

    public UnicodeSet addAll(String ... collection) {
        this.checkFrozen();
        for (String str : collection) {
            this.add(str);
        }
        return this;
    }

    public UnicodeSet removeAll(Collection<String> collection) {
        this.checkFrozen();
        for (String o2 : collection) {
            this.remove(o2);
        }
        return this;
    }

    public UnicodeSet retainAll(Collection<String> collection) {
        this.checkFrozen();
        UnicodeSet toRetain = new UnicodeSet();
        toRetain.addAll(collection);
        this.retainAll(toRetain);
        return this;
    }

    @Override
    public int compareTo(UnicodeSet o2) {
        return this.compareTo(o2, ComparisonStyle.SHORTER_FIRST);
    }

    public int compareTo(UnicodeSet o2, ComparisonStyle style) {
        int diff;
        if (style != ComparisonStyle.LEXICOGRAPHIC && (diff = this.size() - o2.size()) != 0) {
            return diff < 0 == (style == ComparisonStyle.SHORTER_FIRST) ? -1 : 1;
        }
        int i2 = 0;
        while (true) {
            int result;
            if (0 != (result = this.list[i2] - o2.list[i2])) {
                if (this.list[i2] == 0x110000) {
                    if (this.strings.isEmpty()) {
                        return 1;
                    }
                    String item = this.strings.first();
                    return UnicodeSet.compare(item, o2.list[i2]);
                }
                if (o2.list[i2] == 0x110000) {
                    if (o2.strings.isEmpty()) {
                        return -1;
                    }
                    String item = o2.strings.first();
                    return -UnicodeSet.compare(item, this.list[i2]);
                }
                return (i2 & 1) == 0 ? result : -result;
            }
            if (this.list[i2] == 0x110000) break;
            ++i2;
        }
        return UnicodeSet.compare(this.strings, o2.strings);
    }

    @Override
    public int compareTo(Iterable<String> other) {
        return UnicodeSet.compare(this, other);
    }

    public static int compare(String string, int codePoint) {
        return CharSequences.compare((CharSequence)string, codePoint);
    }

    public static int compare(int codePoint, String string) {
        return -CharSequences.compare((CharSequence)string, codePoint);
    }

    public static <T extends Comparable<T>> int compare(Iterable<T> collection1, Iterable<T> collection2) {
        return UnicodeSet.compare(collection1.iterator(), collection2.iterator());
    }

    public static <T extends Comparable<T>> int compare(Iterator<T> first, Iterator<T> other) {
        Comparable item2;
        Comparable item1;
        int result;
        do {
            if (!first.hasNext()) {
                return other.hasNext() ? -1 : 0;
            }
            if (other.hasNext()) continue;
            return 1;
        } while ((result = (item1 = (Comparable)first.next()).compareTo(item2 = (Comparable)other.next())) == 0);
        return result;
    }

    public static <T extends Comparable<T>> int compare(Collection<T> collection1, Collection<T> collection2, ComparisonStyle style) {
        int diff;
        if (style != ComparisonStyle.LEXICOGRAPHIC && (diff = collection1.size() - collection2.size()) != 0) {
            return diff < 0 == (style == ComparisonStyle.SHORTER_FIRST) ? -1 : 1;
        }
        return UnicodeSet.compare(collection1, collection2);
    }

    public static <T, U extends Collection<T>> U addAllTo(Iterable<T> source, U target) {
        for (T item : source) {
            target.add(item);
        }
        return target;
    }

    public static <T> T[] addAllTo(Iterable<T> source, T[] target) {
        int i2 = 0;
        for (T item : source) {
            target[i2++] = item;
        }
        return target;
    }

    public Iterable<String> strings() {
        return Collections.unmodifiableSortedSet(this.strings);
    }

    public static int getSingleCodePoint(CharSequence s2) {
        return CharSequences.getSingleCodePoint(s2);
    }

    public UnicodeSet addBridges(UnicodeSet dontCare) {
        UnicodeSet notInInput = new UnicodeSet(this).complement();
        UnicodeSetIterator it2 = new UnicodeSetIterator(notInInput);
        while (it2.nextRange()) {
            if (it2.codepoint == 0 || it2.codepoint == UnicodeSetIterator.IS_STRING || it2.codepointEnd == 0x10FFFF || !dontCare.contains(it2.codepoint, it2.codepointEnd)) continue;
            this.add(it2.codepoint, it2.codepointEnd);
        }
        return this;
    }

    public int findIn(CharSequence value, int fromIndex, boolean findNot) {
        int cp2;
        while (fromIndex < value.length() && this.contains(cp2 = UTF16.charAt(value, fromIndex)) == findNot) {
            fromIndex += UTF16.getCharCount(cp2);
        }
        return fromIndex;
    }

    public int findLastIn(CharSequence value, int fromIndex, boolean findNot) {
        int cp2;
        --fromIndex;
        while (fromIndex >= 0 && this.contains(cp2 = UTF16.charAt(value, fromIndex)) == findNot) {
            fromIndex -= UTF16.getCharCount(cp2);
        }
        return fromIndex < 0 ? -1 : fromIndex;
    }

    public String stripFrom(CharSequence source, boolean matches) {
        StringBuilder result = new StringBuilder();
        int pos = 0;
        while (pos < source.length()) {
            int inside = this.findIn(source, pos, !matches);
            result.append(source.subSequence(pos, inside));
            pos = this.findIn(source, inside, matches);
        }
        return result.toString();
    }

    public static XSymbolTable getDefaultXSymbolTable() {
        return XSYMBOL_TABLE;
    }

    public static void setDefaultXSymbolTable(XSymbolTable xSymbolTable) {
        XSYMBOL_TABLE = xSymbolTable;
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum SpanCondition {
        NOT_CONTAINED,
        CONTAINED,
        SIMPLE,
        CONDITION_COUNT;

    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum ComparisonStyle {
        SHORTER_FIRST,
        LEXICOGRAPHIC,
        LONGER_FIRST;

    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class UnicodeSetIterator2
    implements Iterator<String> {
        private int[] sourceList;
        private int len;
        private int item;
        private int current;
        private int limit;
        private TreeSet<String> sourceStrings;
        private Iterator<String> stringIterator;
        private char[] buffer;

        UnicodeSetIterator2(UnicodeSet source) {
            this.len = source.len - 1;
            if (this.item >= this.len) {
                this.stringIterator = source.strings.iterator();
                this.sourceList = null;
            } else {
                this.sourceStrings = source.strings;
                this.sourceList = source.list;
                this.current = this.sourceList[this.item++];
                this.limit = this.sourceList[this.item++];
            }
        }

        @Override
        public boolean hasNext() {
            return this.sourceList != null || this.stringIterator.hasNext();
        }

        @Override
        public String next() {
            if (this.sourceList == null) {
                return this.stringIterator.next();
            }
            int codepoint = this.current++;
            if (this.current >= this.limit) {
                if (this.item >= this.len) {
                    this.stringIterator = this.sourceStrings.iterator();
                    this.sourceList = null;
                } else {
                    this.current = this.sourceList[this.item++];
                    this.limit = this.sourceList[this.item++];
                }
            }
            if (codepoint <= 65535) {
                return String.valueOf((char)codepoint);
            }
            if (this.buffer == null) {
                this.buffer = new char[2];
            }
            int offset = codepoint - 65536;
            this.buffer[0] = (char)((offset >>> 10) + 55296);
            this.buffer[1] = (char)((offset & 0x3FF) + 56320);
            return String.valueOf(this.buffer);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    public static abstract class XSymbolTable
    implements SymbolTable {
        public UnicodeMatcher lookupMatcher(int i2) {
            return null;
        }

        public boolean applyPropertyAlias(String propertyName, String propertyValue, UnicodeSet result) {
            return false;
        }

        public char[] lookup(String s2) {
            return null;
        }

        public String parseReference(String text, ParsePosition pos, int limit) {
            return null;
        }
    }

    private static class VersionFilter
    implements Filter {
        VersionInfo version;

        VersionFilter(VersionInfo version) {
            this.version = version;
        }

        public boolean contains(int ch) {
            VersionInfo v2 = UCharacter.getAge(ch);
            return v2 != NO_VERSION && v2.compareTo(this.version) <= 0;
        }
    }

    private static class ScriptExtensionsFilter
    implements Filter {
        int script;

        ScriptExtensionsFilter(int script) {
            this.script = script;
        }

        public boolean contains(int c2) {
            return UScript.hasScript(c2, this.script);
        }
    }

    private static class IntPropertyFilter
    implements Filter {
        int prop;
        int value;

        IntPropertyFilter(int prop, int value) {
            this.prop = prop;
            this.value = value;
        }

        public boolean contains(int ch) {
            return UCharacter.getIntPropertyValue(ch, this.prop) == this.value;
        }
    }

    private static class GeneralCategoryMaskFilter
    implements Filter {
        int mask;

        GeneralCategoryMaskFilter(int mask) {
            this.mask = mask;
        }

        public boolean contains(int ch) {
            return (1 << UCharacter.getType(ch) & this.mask) != 0;
        }
    }

    private static class NumericValueFilter
    implements Filter {
        double value;

        NumericValueFilter(double value) {
            this.value = value;
        }

        public boolean contains(int ch) {
            return UCharacter.getUnicodeNumericValue(ch) == this.value;
        }
    }

    private static interface Filter {
        public boolean contains(int var1);
    }
}

