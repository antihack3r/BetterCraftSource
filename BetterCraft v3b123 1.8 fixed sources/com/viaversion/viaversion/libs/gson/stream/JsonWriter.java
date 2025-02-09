// 
// Decompiled by Procyon v0.6.0
// 

package com.viaversion.viaversion.libs.gson.stream;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicInteger;
import java.math.BigInteger;
import java.math.BigDecimal;
import java.util.Arrays;
import java.io.IOException;
import java.util.Objects;
import java.io.Writer;
import java.util.regex.Pattern;
import java.io.Flushable;
import java.io.Closeable;

public class JsonWriter implements Closeable, Flushable
{
    private static final Pattern VALID_JSON_NUMBER_PATTERN;
    private static final String[] REPLACEMENT_CHARS;
    private static final String[] HTML_SAFE_REPLACEMENT_CHARS;
    private final Writer out;
    private int[] stack;
    private int stackSize;
    private String indent;
    private String separator;
    private boolean lenient;
    private boolean htmlSafe;
    private String deferredName;
    private boolean serializeNulls;
    
    public JsonWriter(final Writer out) {
        this.stack = new int[32];
        this.stackSize = 0;
        this.push(6);
        this.separator = ":";
        this.serializeNulls = true;
        this.out = Objects.requireNonNull(out, "out == null");
    }
    
    public final void setIndent(final String indent) {
        if (indent.length() == 0) {
            this.indent = null;
            this.separator = ":";
        }
        else {
            this.indent = indent;
            this.separator = ": ";
        }
    }
    
    public final void setLenient(final boolean lenient) {
        this.lenient = lenient;
    }
    
    public boolean isLenient() {
        return this.lenient;
    }
    
    public final void setHtmlSafe(final boolean htmlSafe) {
        this.htmlSafe = htmlSafe;
    }
    
    public final boolean isHtmlSafe() {
        return this.htmlSafe;
    }
    
    public final void setSerializeNulls(final boolean serializeNulls) {
        this.serializeNulls = serializeNulls;
    }
    
    public final boolean getSerializeNulls() {
        return this.serializeNulls;
    }
    
    public JsonWriter beginArray() throws IOException {
        this.writeDeferredName();
        return this.open(1, '[');
    }
    
    public JsonWriter endArray() throws IOException {
        return this.close(1, 2, ']');
    }
    
    public JsonWriter beginObject() throws IOException {
        this.writeDeferredName();
        return this.open(3, '{');
    }
    
    public JsonWriter endObject() throws IOException {
        return this.close(3, 5, '}');
    }
    
    private JsonWriter open(final int empty, final char openBracket) throws IOException {
        this.beforeValue();
        this.push(empty);
        this.out.write(openBracket);
        return this;
    }
    
    private JsonWriter close(final int empty, final int nonempty, final char closeBracket) throws IOException {
        final int context = this.peek();
        if (context != nonempty && context != empty) {
            throw new IllegalStateException("Nesting problem.");
        }
        if (this.deferredName != null) {
            throw new IllegalStateException("Dangling name: " + this.deferredName);
        }
        --this.stackSize;
        if (context == nonempty) {
            this.newline();
        }
        this.out.write(closeBracket);
        return this;
    }
    
    private void push(final int newTop) {
        if (this.stackSize == this.stack.length) {
            this.stack = Arrays.copyOf(this.stack, this.stackSize * 2);
        }
        this.stack[this.stackSize++] = newTop;
    }
    
    private int peek() {
        if (this.stackSize == 0) {
            throw new IllegalStateException("JsonWriter is closed.");
        }
        return this.stack[this.stackSize - 1];
    }
    
    private void replaceTop(final int topOfStack) {
        this.stack[this.stackSize - 1] = topOfStack;
    }
    
    public JsonWriter name(final String name) throws IOException {
        Objects.requireNonNull(name, "name == null");
        if (this.deferredName != null) {
            throw new IllegalStateException();
        }
        if (this.stackSize == 0) {
            throw new IllegalStateException("JsonWriter is closed.");
        }
        this.deferredName = name;
        return this;
    }
    
    private void writeDeferredName() throws IOException {
        if (this.deferredName != null) {
            this.beforeName();
            this.string(this.deferredName);
            this.deferredName = null;
        }
    }
    
    public JsonWriter value(final String value) throws IOException {
        if (value == null) {
            return this.nullValue();
        }
        this.writeDeferredName();
        this.beforeValue();
        this.string(value);
        return this;
    }
    
    public JsonWriter jsonValue(final String value) throws IOException {
        if (value == null) {
            return this.nullValue();
        }
        this.writeDeferredName();
        this.beforeValue();
        this.out.append((CharSequence)value);
        return this;
    }
    
    public JsonWriter nullValue() throws IOException {
        if (this.deferredName != null) {
            if (!this.serializeNulls) {
                this.deferredName = null;
                return this;
            }
            this.writeDeferredName();
        }
        this.beforeValue();
        this.out.write("null");
        return this;
    }
    
    public JsonWriter value(final boolean value) throws IOException {
        this.writeDeferredName();
        this.beforeValue();
        this.out.write(value ? "true" : "false");
        return this;
    }
    
    public JsonWriter value(final Boolean value) throws IOException {
        if (value == null) {
            return this.nullValue();
        }
        this.writeDeferredName();
        this.beforeValue();
        this.out.write(((boolean)value) ? "true" : "false");
        return this;
    }
    
    public JsonWriter value(final float value) throws IOException {
        this.writeDeferredName();
        if (!this.lenient && (Float.isNaN(value) || Float.isInfinite(value))) {
            throw new IllegalArgumentException("Numeric values must be finite, but was " + value);
        }
        this.beforeValue();
        this.out.append((CharSequence)Float.toString(value));
        return this;
    }
    
    public JsonWriter value(final double value) throws IOException {
        this.writeDeferredName();
        if (!this.lenient && (Double.isNaN(value) || Double.isInfinite(value))) {
            throw new IllegalArgumentException("Numeric values must be finite, but was " + value);
        }
        this.beforeValue();
        this.out.append((CharSequence)Double.toString(value));
        return this;
    }
    
    public JsonWriter value(final long value) throws IOException {
        this.writeDeferredName();
        this.beforeValue();
        this.out.write(Long.toString(value));
        return this;
    }
    
    private static boolean isTrustedNumberType(final Class<? extends Number> c) {
        return c == Integer.class || c == Long.class || c == Double.class || c == Float.class || c == Byte.class || c == Short.class || c == BigDecimal.class || c == BigInteger.class || c == AtomicInteger.class || c == AtomicLong.class;
    }
    
    public JsonWriter value(final Number value) throws IOException {
        if (value == null) {
            return this.nullValue();
        }
        this.writeDeferredName();
        final String string = value.toString();
        if (string.equals("-Infinity") || string.equals("Infinity") || string.equals("NaN")) {
            if (!this.lenient) {
                throw new IllegalArgumentException("Numeric values must be finite, but was " + string);
            }
        }
        else {
            final Class<? extends Number> numberClass = value.getClass();
            if (!isTrustedNumberType(numberClass) && !JsonWriter.VALID_JSON_NUMBER_PATTERN.matcher(string).matches()) {
                throw new IllegalArgumentException("String created by " + numberClass + " is not a valid JSON number: " + string);
            }
        }
        this.beforeValue();
        this.out.append((CharSequence)string);
        return this;
    }
    
    @Override
    public void flush() throws IOException {
        if (this.stackSize == 0) {
            throw new IllegalStateException("JsonWriter is closed.");
        }
        this.out.flush();
    }
    
    @Override
    public void close() throws IOException {
        this.out.close();
        final int size = this.stackSize;
        if (size > 1 || (size == 1 && this.stack[size - 1] != 7)) {
            throw new IOException("Incomplete document");
        }
        this.stackSize = 0;
    }
    
    private void string(final String value) throws IOException {
        final String[] replacements = this.htmlSafe ? JsonWriter.HTML_SAFE_REPLACEMENT_CHARS : JsonWriter.REPLACEMENT_CHARS;
        this.out.write(34);
        int last = 0;
        final int length = value.length();
        for (int i = 0; i < length; ++i) {
            final char c = value.charAt(i);
            String replacement;
            if (c < '\u0080') {
                replacement = replacements[c];
                if (replacement == null) {
                    continue;
                }
            }
            else if (c == '\u2028') {
                replacement = "\\u2028";
            }
            else {
                if (c != '\u2029') {
                    continue;
                }
                replacement = "\\u2029";
            }
            if (last < i) {
                this.out.write(value, last, i - last);
            }
            this.out.write(replacement);
            last = i + 1;
        }
        if (last < length) {
            this.out.write(value, last, length - last);
        }
        this.out.write(34);
    }
    
    private void newline() throws IOException {
        if (this.indent == null) {
            return;
        }
        this.out.write(10);
        for (int i = 1, size = this.stackSize; i < size; ++i) {
            this.out.write(this.indent);
        }
    }
    
    private void beforeName() throws IOException {
        final int context = this.peek();
        if (context == 5) {
            this.out.write(44);
        }
        else if (context != 3) {
            throw new IllegalStateException("Nesting problem.");
        }
        this.newline();
        this.replaceTop(4);
    }
    
    private void beforeValue() throws IOException {
        switch (this.peek()) {
            case 7: {
                if (!this.lenient) {
                    throw new IllegalStateException("JSON must have only one top-level value.");
                }
            }
            case 6: {
                this.replaceTop(7);
                break;
            }
            case 1: {
                this.replaceTop(2);
                this.newline();
                break;
            }
            case 2: {
                this.out.append(',');
                this.newline();
                break;
            }
            case 4: {
                this.out.append((CharSequence)this.separator);
                this.replaceTop(5);
                break;
            }
            default: {
                throw new IllegalStateException("Nesting problem.");
            }
        }
    }
    
    static {
        VALID_JSON_NUMBER_PATTERN = Pattern.compile("-?(?:0|[1-9][0-9]*)(?:\\.[0-9]+)?(?:[eE][-+]?[0-9]+)?");
        REPLACEMENT_CHARS = new String[128];
        for (int i = 0; i <= 31; ++i) {
            JsonWriter.REPLACEMENT_CHARS[i] = String.format("\\u%04x", i);
        }
        JsonWriter.REPLACEMENT_CHARS[34] = "\\\"";
        JsonWriter.REPLACEMENT_CHARS[92] = "\\\\";
        JsonWriter.REPLACEMENT_CHARS[9] = "\\t";
        JsonWriter.REPLACEMENT_CHARS[8] = "\\b";
        JsonWriter.REPLACEMENT_CHARS[10] = "\\n";
        JsonWriter.REPLACEMENT_CHARS[13] = "\\r";
        JsonWriter.REPLACEMENT_CHARS[12] = "\\f";
        (HTML_SAFE_REPLACEMENT_CHARS = JsonWriter.REPLACEMENT_CHARS.clone())[60] = "\\u003c";
        JsonWriter.HTML_SAFE_REPLACEMENT_CHARS[62] = "\\u003e";
        JsonWriter.HTML_SAFE_REPLACEMENT_CHARS[38] = "\\u0026";
        JsonWriter.HTML_SAFE_REPLACEMENT_CHARS[61] = "\\u003d";
        JsonWriter.HTML_SAFE_REPLACEMENT_CHARS[39] = "\\u0027";
    }
}
