/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.lang3.text;

import java.text.Format;
import java.text.MessageFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.text.FormatFactory;
import org.apache.commons.lang3.text.StrMatcher;

public class ExtendedMessageFormat
extends MessageFormat {
    private static final long serialVersionUID = -2362048321261811743L;
    private static final int HASH_SEED = 31;
    private static final String DUMMY_PATTERN = "";
    private static final String ESCAPED_QUOTE = "''";
    private static final char START_FMT = ',';
    private static final char END_FE = '}';
    private static final char START_FE = '{';
    private static final char QUOTE = '\'';
    private String toPattern;
    private final Map<String, ? extends FormatFactory> registry;

    public ExtendedMessageFormat(String pattern) {
        this(pattern, Locale.getDefault());
    }

    public ExtendedMessageFormat(String pattern, Locale locale) {
        this(pattern, locale, null);
    }

    public ExtendedMessageFormat(String pattern, Map<String, ? extends FormatFactory> registry) {
        this(pattern, Locale.getDefault(), registry);
    }

    public ExtendedMessageFormat(String pattern, Locale locale, Map<String, ? extends FormatFactory> registry) {
        super(DUMMY_PATTERN);
        this.setLocale(locale);
        this.registry = registry;
        this.applyPattern(pattern);
    }

    @Override
    public String toPattern() {
        return this.toPattern;
    }

    @Override
    public final void applyPattern(String pattern) {
        if (this.registry == null) {
            super.applyPattern(pattern);
            this.toPattern = super.toPattern();
            return;
        }
        ArrayList<Format> foundFormats = new ArrayList<Format>();
        ArrayList<String> foundDescriptions = new ArrayList<String>();
        StringBuilder stripCustom = new StringBuilder(pattern.length());
        ParsePosition pos = new ParsePosition(0);
        char[] c2 = pattern.toCharArray();
        int fmtCount = 0;
        block4: while (pos.getIndex() < pattern.length()) {
            switch (c2[pos.getIndex()]) {
                case '\'': {
                    this.appendQuotedString(pattern, pos, stripCustom, true);
                    continue block4;
                }
                case '{': {
                    ++fmtCount;
                    this.seekNonWs(pattern, pos);
                    int start = pos.getIndex();
                    int index = this.readArgumentIndex(pattern, this.next(pos));
                    stripCustom.append('{').append(index);
                    this.seekNonWs(pattern, pos);
                    Format format = null;
                    String formatDescription = null;
                    if (c2[pos.getIndex()] == ',' && (format = this.getFormat(formatDescription = this.parseFormatDescription(pattern, this.next(pos)))) == null) {
                        stripCustom.append(',').append(formatDescription);
                    }
                    foundFormats.add(format);
                    foundDescriptions.add(format == null ? null : formatDescription);
                    Validate.isTrue(foundFormats.size() == fmtCount);
                    Validate.isTrue(foundDescriptions.size() == fmtCount);
                    if (c2[pos.getIndex()] == '}') break;
                    throw new IllegalArgumentException("Unreadable format element at position " + start);
                }
            }
            stripCustom.append(c2[pos.getIndex()]);
            this.next(pos);
        }
        super.applyPattern(stripCustom.toString());
        this.toPattern = this.insertFormats(super.toPattern(), foundDescriptions);
        if (this.containsElements(foundFormats)) {
            Format[] origFormats = this.getFormats();
            int i2 = 0;
            for (Format f2 : foundFormats) {
                if (f2 != null) {
                    origFormats[i2] = f2;
                }
                ++i2;
            }
            super.setFormats(origFormats);
        }
    }

    @Override
    public void setFormat(int formatElementIndex, Format newFormat) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setFormatByArgumentIndex(int argumentIndex, Format newFormat) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setFormats(Format[] newFormats) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setFormatsByArgumentIndex(Format[] newFormats) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (ObjectUtils.notEqual(this.getClass(), obj.getClass())) {
            return false;
        }
        ExtendedMessageFormat rhs = (ExtendedMessageFormat)obj;
        if (ObjectUtils.notEqual(this.toPattern, rhs.toPattern)) {
            return false;
        }
        return !ObjectUtils.notEqual(this.registry, rhs.registry);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + ObjectUtils.hashCode(this.registry);
        result = 31 * result + ObjectUtils.hashCode(this.toPattern);
        return result;
    }

    private Format getFormat(String desc) {
        if (this.registry != null) {
            FormatFactory factory;
            String name = desc;
            String args = null;
            int i2 = desc.indexOf(44);
            if (i2 > 0) {
                name = desc.substring(0, i2).trim();
                args = desc.substring(i2 + 1).trim();
            }
            if ((factory = this.registry.get(name)) != null) {
                return factory.getFormat(name, args, this.getLocale());
            }
        }
        return null;
    }

    /*
     * Unable to fully structure code
     */
    private int readArgumentIndex(String pattern, ParsePosition pos) {
        start = pos.getIndex();
        this.seekNonWs(pattern, pos);
        result = new StringBuilder();
        error = false;
        while (!error && pos.getIndex() < pattern.length()) {
            c = pattern.charAt(pos.getIndex());
            if (!Character.isWhitespace(c)) ** GOTO lbl-1000
            this.seekNonWs(pattern, pos);
            c = pattern.charAt(pos.getIndex());
            if (c != ',' && c != '}') {
                error = true;
            } else lbl-1000:
            // 2 sources

            {
                if ((c == ',' || c == '}') && result.length() > 0) {
                    try {
                        return Integer.parseInt(result.toString());
                    }
                    catch (NumberFormatException e) {
                        // empty catch block
                    }
                }
                error = Character.isDigit(c) == false;
                result.append(c);
            }
            this.next(pos);
        }
        if (error) {
            throw new IllegalArgumentException("Invalid format argument index at position " + start + ": " + pattern.substring(start, pos.getIndex()));
        }
        throw new IllegalArgumentException("Unterminated format element at position " + start);
    }

    private String parseFormatDescription(String pattern, ParsePosition pos) {
        int start = pos.getIndex();
        this.seekNonWs(pattern, pos);
        int text = pos.getIndex();
        int depth = 1;
        while (pos.getIndex() < pattern.length()) {
            switch (pattern.charAt(pos.getIndex())) {
                case '{': {
                    ++depth;
                    break;
                }
                case '}': {
                    if (--depth != 0) break;
                    return pattern.substring(text, pos.getIndex());
                }
                case '\'': {
                    this.getQuotedString(pattern, pos, false);
                    break;
                }
            }
            this.next(pos);
        }
        throw new IllegalArgumentException("Unterminated format element at position " + start);
    }

    private String insertFormats(String pattern, ArrayList<String> customPatterns) {
        if (!this.containsElements(customPatterns)) {
            return pattern;
        }
        StringBuilder sb2 = new StringBuilder(pattern.length() * 2);
        ParsePosition pos = new ParsePosition(0);
        int fe = -1;
        int depth = 0;
        block5: while (pos.getIndex() < pattern.length()) {
            char c2 = pattern.charAt(pos.getIndex());
            switch (c2) {
                case '\'': {
                    this.appendQuotedString(pattern, pos, sb2, false);
                    break;
                }
                case '{': {
                    String customPattern;
                    sb2.append('{').append(this.readArgumentIndex(pattern, this.next(pos)));
                    if (++depth != 1 || (customPattern = customPatterns.get(++fe)) == null) continue block5;
                    sb2.append(',').append(customPattern);
                    break;
                }
                case '}': {
                    --depth;
                }
                default: {
                    sb2.append(c2);
                    this.next(pos);
                }
            }
        }
        return sb2.toString();
    }

    private void seekNonWs(String pattern, ParsePosition pos) {
        int len = 0;
        char[] buffer = pattern.toCharArray();
        do {
            len = StrMatcher.splitMatcher().isMatch(buffer, pos.getIndex());
            pos.setIndex(pos.getIndex() + len);
        } while (len > 0 && pos.getIndex() < pattern.length());
    }

    private ParsePosition next(ParsePosition pos) {
        pos.setIndex(pos.getIndex() + 1);
        return pos;
    }

    private StringBuilder appendQuotedString(String pattern, ParsePosition pos, StringBuilder appendTo, boolean escapingOn) {
        int start = pos.getIndex();
        char[] c2 = pattern.toCharArray();
        if (escapingOn && c2[start] == '\'') {
            this.next(pos);
            return appendTo == null ? null : appendTo.append('\'');
        }
        int lastHold = start;
        for (int i2 = pos.getIndex(); i2 < pattern.length(); ++i2) {
            if (escapingOn && pattern.substring(i2).startsWith(ESCAPED_QUOTE)) {
                appendTo.append(c2, lastHold, pos.getIndex() - lastHold).append('\'');
                pos.setIndex(i2 + ESCAPED_QUOTE.length());
                lastHold = pos.getIndex();
                continue;
            }
            switch (c2[pos.getIndex()]) {
                case '\'': {
                    this.next(pos);
                    return appendTo == null ? null : appendTo.append(c2, lastHold, pos.getIndex() - lastHold);
                }
            }
            this.next(pos);
        }
        throw new IllegalArgumentException("Unterminated quoted string at position " + start);
    }

    private void getQuotedString(String pattern, ParsePosition pos, boolean escapingOn) {
        this.appendQuotedString(pattern, pos, null, escapingOn);
    }

    private boolean containsElements(Collection<?> coll) {
        if (coll == null || coll.isEmpty()) {
            return false;
        }
        for (Object name : coll) {
            if (name == null) continue;
            return true;
        }
        return false;
    }
}

