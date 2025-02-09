/*
 * Decompiled with CFR 0.152.
 */
package com.google.common.base;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Ascii;
import com.google.common.base.CharMatcher;
import com.google.common.base.Converter;
import com.google.common.base.Preconditions;
import java.io.Serializable;
import javax.annotation.Nullable;

@GwtCompatible
public enum CaseFormat {
    LOWER_HYPHEN(CharMatcher.is('-'), "-"){

        @Override
        String normalizeWord(String word) {
            return Ascii.toLowerCase(word);
        }

        @Override
        String convert(CaseFormat format, String s2) {
            if (format == LOWER_UNDERSCORE) {
                return s2.replace('-', '_');
            }
            if (format == UPPER_UNDERSCORE) {
                return Ascii.toUpperCase(s2.replace('-', '_'));
            }
            return super.convert(format, s2);
        }
    }
    ,
    LOWER_UNDERSCORE(CharMatcher.is('_'), "_"){

        @Override
        String normalizeWord(String word) {
            return Ascii.toLowerCase(word);
        }

        @Override
        String convert(CaseFormat format, String s2) {
            if (format == LOWER_HYPHEN) {
                return s2.replace('_', '-');
            }
            if (format == UPPER_UNDERSCORE) {
                return Ascii.toUpperCase(s2);
            }
            return super.convert(format, s2);
        }
    }
    ,
    LOWER_CAMEL(CharMatcher.inRange('A', 'Z'), ""){

        @Override
        String normalizeWord(String word) {
            return CaseFormat.firstCharOnlyToUpper(word);
        }
    }
    ,
    UPPER_CAMEL(CharMatcher.inRange('A', 'Z'), ""){

        @Override
        String normalizeWord(String word) {
            return CaseFormat.firstCharOnlyToUpper(word);
        }
    }
    ,
    UPPER_UNDERSCORE(CharMatcher.is('_'), "_"){

        @Override
        String normalizeWord(String word) {
            return Ascii.toUpperCase(word);
        }

        @Override
        String convert(CaseFormat format, String s2) {
            if (format == LOWER_HYPHEN) {
                return Ascii.toLowerCase(s2.replace('_', '-'));
            }
            if (format == LOWER_UNDERSCORE) {
                return Ascii.toLowerCase(s2);
            }
            return super.convert(format, s2);
        }
    };

    private final CharMatcher wordBoundary;
    private final String wordSeparator;

    private CaseFormat(CharMatcher wordBoundary, String wordSeparator) {
        this.wordBoundary = wordBoundary;
        this.wordSeparator = wordSeparator;
    }

    public final String to(CaseFormat format, String str) {
        Preconditions.checkNotNull(format);
        Preconditions.checkNotNull(str);
        return format == this ? str : this.convert(format, str);
    }

    String convert(CaseFormat format, String s2) {
        StringBuilder out = null;
        int i2 = 0;
        int j2 = -1;
        while (true) {
            ++j2;
            if ((j2 = this.wordBoundary.indexIn(s2, j2)) == -1) break;
            if (i2 == 0) {
                out = new StringBuilder(s2.length() + 4 * this.wordSeparator.length());
                out.append(format.normalizeFirstWord(s2.substring(i2, j2)));
            } else {
                out.append(format.normalizeWord(s2.substring(i2, j2)));
            }
            out.append(format.wordSeparator);
            i2 = j2 + this.wordSeparator.length();
        }
        return i2 == 0 ? format.normalizeFirstWord(s2) : out.append(format.normalizeWord(s2.substring(i2))).toString();
    }

    @Beta
    public Converter<String, String> converterTo(CaseFormat targetFormat) {
        return new StringConverter(this, targetFormat);
    }

    abstract String normalizeWord(String var1);

    private String normalizeFirstWord(String word) {
        return this == LOWER_CAMEL ? Ascii.toLowerCase(word) : this.normalizeWord(word);
    }

    private static String firstCharOnlyToUpper(String word) {
        return word.isEmpty() ? word : new StringBuilder(word.length()).append(Ascii.toUpperCase(word.charAt(0))).append(Ascii.toLowerCase(word.substring(1))).toString();
    }

    private static final class StringConverter
    extends Converter<String, String>
    implements Serializable {
        private final CaseFormat sourceFormat;
        private final CaseFormat targetFormat;
        private static final long serialVersionUID = 0L;

        StringConverter(CaseFormat sourceFormat, CaseFormat targetFormat) {
            this.sourceFormat = Preconditions.checkNotNull(sourceFormat);
            this.targetFormat = Preconditions.checkNotNull(targetFormat);
        }

        @Override
        protected String doForward(String s2) {
            return s2 == null ? null : this.sourceFormat.to(this.targetFormat, s2);
        }

        @Override
        protected String doBackward(String s2) {
            return s2 == null ? null : this.targetFormat.to(this.sourceFormat, s2);
        }

        @Override
        public boolean equals(@Nullable Object object) {
            if (object instanceof StringConverter) {
                StringConverter that = (StringConverter)object;
                return this.sourceFormat.equals((Object)that.sourceFormat) && this.targetFormat.equals((Object)that.targetFormat);
            }
            return false;
        }

        public int hashCode() {
            return this.sourceFormat.hashCode() ^ this.targetFormat.hashCode();
        }

        public String toString() {
            return (Object)((Object)this.sourceFormat) + ".converterTo(" + (Object)((Object)this.targetFormat) + ")";
        }
    }
}

