/*
 * Decompiled with CFR 0.152.
 */
package com.google.common.escape;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.escape.ArrayBasedEscaperMap;
import com.google.common.escape.UnicodeEscaper;
import java.util.Map;
import javax.annotation.Nullable;

@Beta
@GwtCompatible
public abstract class ArrayBasedUnicodeEscaper
extends UnicodeEscaper {
    private final char[][] replacements;
    private final int replacementsLength;
    private final int safeMin;
    private final int safeMax;
    private final char safeMinChar;
    private final char safeMaxChar;

    protected ArrayBasedUnicodeEscaper(Map<Character, String> replacementMap, int safeMin, int safeMax, @Nullable String unsafeReplacement) {
        this(ArrayBasedEscaperMap.create(replacementMap), safeMin, safeMax, unsafeReplacement);
    }

    protected ArrayBasedUnicodeEscaper(ArrayBasedEscaperMap escaperMap, int safeMin, int safeMax, @Nullable String unsafeReplacement) {
        Preconditions.checkNotNull(escaperMap);
        this.replacements = escaperMap.getReplacementArray();
        this.replacementsLength = this.replacements.length;
        if (safeMax < safeMin) {
            safeMax = -1;
            safeMin = Integer.MAX_VALUE;
        }
        this.safeMin = safeMin;
        this.safeMax = safeMax;
        if (safeMin >= 55296) {
            this.safeMinChar = (char)65535;
            this.safeMaxChar = '\u0000';
        } else {
            this.safeMinChar = (char)safeMin;
            this.safeMaxChar = (char)Math.min(safeMax, 55295);
        }
    }

    @Override
    public final String escape(String s2) {
        Preconditions.checkNotNull(s2);
        for (int i2 = 0; i2 < s2.length(); ++i2) {
            char c2 = s2.charAt(i2);
            if ((c2 >= this.replacementsLength || this.replacements[c2] == null) && c2 <= this.safeMaxChar && c2 >= this.safeMinChar) continue;
            return this.escapeSlow(s2, i2);
        }
        return s2;
    }

    @Override
    protected final int nextEscapeIndex(CharSequence csq, int index, int end) {
        char c2;
        while (index < end && ((c2 = csq.charAt(index)) >= this.replacementsLength || this.replacements[c2] == null) && c2 <= this.safeMaxChar && c2 >= this.safeMinChar) {
            ++index;
        }
        return index;
    }

    @Override
    protected final char[] escape(int cp2) {
        char[] chars;
        if (cp2 < this.replacementsLength && (chars = this.replacements[cp2]) != null) {
            return chars;
        }
        if (cp2 >= this.safeMin && cp2 <= this.safeMax) {
            return null;
        }
        return this.escapeUnsafe(cp2);
    }

    protected abstract char[] escapeUnsafe(int var1);
}

