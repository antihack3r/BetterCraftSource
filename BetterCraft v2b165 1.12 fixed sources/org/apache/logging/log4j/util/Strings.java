// 
// Decompiled by Procyon v0.6.0
// 

package org.apache.logging.log4j.util;

import java.util.Objects;
import java.util.Iterator;
import java.util.Locale;

public final class Strings
{
    public static final String EMPTY = "";
    public static final String LINE_SEPARATOR;
    
    private Strings() {
    }
    
    public static String dquote(final String str) {
        return '\"' + str + '\"';
    }
    
    public static boolean isBlank(final String s) {
        return s == null || s.trim().isEmpty();
    }
    
    public static boolean isEmpty(final CharSequence cs) {
        return cs == null || cs.length() == 0;
    }
    
    public static boolean isNotBlank(final String s) {
        return !isBlank(s);
    }
    
    public static boolean isNotEmpty(final CharSequence cs) {
        return !isEmpty(cs);
    }
    
    public static String quote(final String str) {
        return '\'' + str + '\'';
    }
    
    public String toRootUpperCase(final String str) {
        return str.toUpperCase(Locale.ROOT);
    }
    
    public static String trimToNull(final String str) {
        final String ts = (str == null) ? null : str.trim();
        return isEmpty(ts) ? null : ts;
    }
    
    public static String join(final Iterable<?> iterable, final char separator) {
        if (iterable == null) {
            return null;
        }
        return join(iterable.iterator(), separator);
    }
    
    public static String join(final Iterator<?> iterator, final char separator) {
        if (iterator == null) {
            return null;
        }
        if (!iterator.hasNext()) {
            return "";
        }
        final Object first = iterator.next();
        if (!iterator.hasNext()) {
            return Objects.toString(first);
        }
        final StringBuilder buf = new StringBuilder(256);
        if (first != null) {
            buf.append(first);
        }
        while (iterator.hasNext()) {
            buf.append(separator);
            final Object obj = iterator.next();
            if (obj != null) {
                buf.append(obj);
            }
        }
        return buf.toString();
    }
    
    static {
        LINE_SEPARATOR = PropertiesUtil.getProperties().getStringProperty("line.separator", "\n");
    }
}
