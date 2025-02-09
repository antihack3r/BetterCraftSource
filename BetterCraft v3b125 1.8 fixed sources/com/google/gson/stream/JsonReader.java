/*
 * Decompiled with CFR 0.152.
 */
package com.google.gson.stream;

import com.google.gson.internal.JsonReaderInternalAccess;
import com.google.gson.internal.bind.JsonTreeReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.MalformedJsonException;
import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.io.Reader;

public class JsonReader
implements Closeable {
    private static final char[] NON_EXECUTE_PREFIX = ")]}'\n".toCharArray();
    private static final long MIN_INCOMPLETE_INTEGER = -922337203685477580L;
    private static final int PEEKED_NONE = 0;
    private static final int PEEKED_BEGIN_OBJECT = 1;
    private static final int PEEKED_END_OBJECT = 2;
    private static final int PEEKED_BEGIN_ARRAY = 3;
    private static final int PEEKED_END_ARRAY = 4;
    private static final int PEEKED_TRUE = 5;
    private static final int PEEKED_FALSE = 6;
    private static final int PEEKED_NULL = 7;
    private static final int PEEKED_SINGLE_QUOTED = 8;
    private static final int PEEKED_DOUBLE_QUOTED = 9;
    private static final int PEEKED_UNQUOTED = 10;
    private static final int PEEKED_BUFFERED = 11;
    private static final int PEEKED_SINGLE_QUOTED_NAME = 12;
    private static final int PEEKED_DOUBLE_QUOTED_NAME = 13;
    private static final int PEEKED_UNQUOTED_NAME = 14;
    private static final int PEEKED_LONG = 15;
    private static final int PEEKED_NUMBER = 16;
    private static final int PEEKED_EOF = 17;
    private static final int NUMBER_CHAR_NONE = 0;
    private static final int NUMBER_CHAR_SIGN = 1;
    private static final int NUMBER_CHAR_DIGIT = 2;
    private static final int NUMBER_CHAR_DECIMAL = 3;
    private static final int NUMBER_CHAR_FRACTION_DIGIT = 4;
    private static final int NUMBER_CHAR_EXP_E = 5;
    private static final int NUMBER_CHAR_EXP_SIGN = 6;
    private static final int NUMBER_CHAR_EXP_DIGIT = 7;
    private final Reader in;
    private boolean lenient = false;
    private final char[] buffer = new char[1024];
    private int pos = 0;
    private int limit = 0;
    private int lineNumber = 0;
    private int lineStart = 0;
    private int peeked = 0;
    private long peekedLong;
    private int peekedNumberLength;
    private String peekedString;
    private int[] stack = new int[32];
    private int stackSize = 0;

    public JsonReader(Reader in2) {
        this.stack[this.stackSize++] = 6;
        if (in2 == null) {
            throw new NullPointerException("in == null");
        }
        this.in = in2;
    }

    public final void setLenient(boolean lenient) {
        this.lenient = lenient;
    }

    public final boolean isLenient() {
        return this.lenient;
    }

    public void beginArray() throws IOException {
        int p2 = this.peeked;
        if (p2 == 0) {
            p2 = this.doPeek();
        }
        if (p2 != 3) {
            throw new IllegalStateException("Expected BEGIN_ARRAY but was " + (Object)((Object)this.peek()) + " at line " + this.getLineNumber() + " column " + this.getColumnNumber());
        }
        this.push(1);
        this.peeked = 0;
    }

    public void endArray() throws IOException {
        int p2 = this.peeked;
        if (p2 == 0) {
            p2 = this.doPeek();
        }
        if (p2 == 4) {
            --this.stackSize;
        } else {
            throw new IllegalStateException("Expected END_ARRAY but was " + (Object)((Object)this.peek()) + " at line " + this.getLineNumber() + " column " + this.getColumnNumber());
        }
        this.peeked = 0;
    }

    public void beginObject() throws IOException {
        int p2 = this.peeked;
        if (p2 == 0) {
            p2 = this.doPeek();
        }
        if (p2 != 1) {
            throw new IllegalStateException("Expected BEGIN_OBJECT but was " + (Object)((Object)this.peek()) + " at line " + this.getLineNumber() + " column " + this.getColumnNumber());
        }
        this.push(3);
        this.peeked = 0;
    }

    public void endObject() throws IOException {
        int p2 = this.peeked;
        if (p2 == 0) {
            p2 = this.doPeek();
        }
        if (p2 == 2) {
            --this.stackSize;
        } else {
            throw new IllegalStateException("Expected END_OBJECT but was " + (Object)((Object)this.peek()) + " at line " + this.getLineNumber() + " column " + this.getColumnNumber());
        }
        this.peeked = 0;
    }

    public boolean hasNext() throws IOException {
        int p2 = this.peeked;
        if (p2 == 0) {
            p2 = this.doPeek();
        }
        return p2 != 2 && p2 != 4;
    }

    public JsonToken peek() throws IOException {
        int p2 = this.peeked;
        if (p2 == 0) {
            p2 = this.doPeek();
        }
        switch (p2) {
            case 1: {
                return JsonToken.BEGIN_OBJECT;
            }
            case 2: {
                return JsonToken.END_OBJECT;
            }
            case 3: {
                return JsonToken.BEGIN_ARRAY;
            }
            case 4: {
                return JsonToken.END_ARRAY;
            }
            case 12: 
            case 13: 
            case 14: {
                return JsonToken.NAME;
            }
            case 5: 
            case 6: {
                return JsonToken.BOOLEAN;
            }
            case 7: {
                return JsonToken.NULL;
            }
            case 8: 
            case 9: 
            case 10: 
            case 11: {
                return JsonToken.STRING;
            }
            case 15: 
            case 16: {
                return JsonToken.NUMBER;
            }
            case 17: {
                return JsonToken.END_DOCUMENT;
            }
        }
        throw new AssertionError();
    }

    private int doPeek() throws IOException {
        int result;
        int c2;
        int peekStack;
        block47: {
            block49: {
                block48: {
                    block46: {
                        peekStack = this.stack[this.stackSize - 1];
                        if (peekStack != 1) break block46;
                        this.stack[this.stackSize - 1] = 2;
                        break block47;
                    }
                    if (peekStack != 2) break block48;
                    c2 = this.nextNonWhitespace(true);
                    switch (c2) {
                        case 93: {
                            this.peeked = 4;
                            return 4;
                        }
                        case 59: {
                            this.checkLenient();
                        }
                        case 44: {
                            break;
                        }
                        default: {
                            throw this.syntaxError("Unterminated array");
                        }
                    }
                    break block47;
                }
                if (peekStack == 3 || peekStack == 5) {
                    int c3;
                    this.stack[this.stackSize - 1] = 4;
                    if (peekStack == 5) {
                        c3 = this.nextNonWhitespace(true);
                        switch (c3) {
                            case 125: {
                                this.peeked = 2;
                                return 2;
                            }
                            case 59: {
                                this.checkLenient();
                            }
                            case 44: {
                                break;
                            }
                            default: {
                                throw this.syntaxError("Unterminated object");
                            }
                        }
                    }
                    c3 = this.nextNonWhitespace(true);
                    switch (c3) {
                        case 34: {
                            this.peeked = 13;
                            return 13;
                        }
                        case 39: {
                            this.checkLenient();
                            this.peeked = 12;
                            return 12;
                        }
                        case 125: {
                            if (peekStack != 5) {
                                this.peeked = 2;
                                return 2;
                            }
                            throw this.syntaxError("Expected name");
                        }
                    }
                    this.checkLenient();
                    --this.pos;
                    if (this.isLiteral((char)c3)) {
                        this.peeked = 14;
                        return 14;
                    }
                    throw this.syntaxError("Expected name");
                }
                if (peekStack != 4) break block49;
                this.stack[this.stackSize - 1] = 5;
                c2 = this.nextNonWhitespace(true);
                switch (c2) {
                    case 58: {
                        break;
                    }
                    case 61: {
                        this.checkLenient();
                        if ((this.pos < this.limit || this.fillBuffer(1)) && this.buffer[this.pos] == '>') {
                            ++this.pos;
                            break;
                        }
                        break block47;
                    }
                    default: {
                        throw this.syntaxError("Expected ':'");
                    }
                }
                break block47;
            }
            if (peekStack == 6) {
                if (this.lenient) {
                    this.consumeNonExecutePrefix();
                }
                this.stack[this.stackSize - 1] = 7;
            } else if (peekStack == 7) {
                c2 = this.nextNonWhitespace(false);
                if (c2 == -1) {
                    this.peeked = 17;
                    return 17;
                }
                this.checkLenient();
                --this.pos;
            } else if (peekStack == 8) {
                throw new IllegalStateException("JsonReader is closed");
            }
        }
        c2 = this.nextNonWhitespace(true);
        switch (c2) {
            case 93: {
                if (peekStack == 1) {
                    this.peeked = 4;
                    return 4;
                }
            }
            case 44: 
            case 59: {
                if (peekStack == 1 || peekStack == 2) {
                    this.checkLenient();
                    --this.pos;
                    this.peeked = 7;
                    return 7;
                }
                throw this.syntaxError("Unexpected value");
            }
            case 39: {
                this.checkLenient();
                this.peeked = 8;
                return 8;
            }
            case 34: {
                if (this.stackSize == 1) {
                    this.checkLenient();
                }
                this.peeked = 9;
                return 9;
            }
            case 91: {
                this.peeked = 3;
                return 3;
            }
            case 123: {
                this.peeked = 1;
                return 1;
            }
        }
        --this.pos;
        if (this.stackSize == 1) {
            this.checkLenient();
        }
        if ((result = this.peekKeyword()) != 0) {
            return result;
        }
        result = this.peekNumber();
        if (result != 0) {
            return result;
        }
        if (!this.isLiteral(this.buffer[this.pos])) {
            throw this.syntaxError("Expected value");
        }
        this.checkLenient();
        this.peeked = 10;
        return 10;
    }

    private int peekKeyword() throws IOException {
        int peeking;
        String keywordUpper;
        String keyword;
        char c2 = this.buffer[this.pos];
        if (c2 == 't' || c2 == 'T') {
            keyword = "true";
            keywordUpper = "TRUE";
            peeking = 5;
        } else if (c2 == 'f' || c2 == 'F') {
            keyword = "false";
            keywordUpper = "FALSE";
            peeking = 6;
        } else if (c2 == 'n' || c2 == 'N') {
            keyword = "null";
            keywordUpper = "NULL";
            peeking = 7;
        } else {
            return 0;
        }
        int length = keyword.length();
        for (int i2 = 1; i2 < length; ++i2) {
            if (this.pos + i2 >= this.limit && !this.fillBuffer(i2 + 1)) {
                return 0;
            }
            c2 = this.buffer[this.pos + i2];
            if (c2 == keyword.charAt(i2) || c2 == keywordUpper.charAt(i2)) continue;
            return 0;
        }
        if ((this.pos + length < this.limit || this.fillBuffer(length + 1)) && this.isLiteral(this.buffer[this.pos + length])) {
            return 0;
        }
        this.pos += length;
        this.peeked = peeking;
        return this.peeked;
    }

    private int peekNumber() throws IOException {
        char[] buffer = this.buffer;
        int p2 = this.pos;
        int l2 = this.limit;
        long value = 0L;
        boolean negative = false;
        boolean fitsInLong = true;
        int last = 0;
        int i2 = 0;
        block6: while (true) {
            if (p2 + i2 == l2) {
                if (i2 == buffer.length) {
                    return 0;
                }
                if (!this.fillBuffer(i2 + 1)) break;
                p2 = this.pos;
                l2 = this.limit;
            }
            char c2 = buffer[p2 + i2];
            switch (c2) {
                case '-': {
                    if (last == 0) {
                        negative = true;
                        last = 1;
                        break;
                    }
                    if (last == 5) {
                        last = 6;
                        break;
                    }
                    return 0;
                }
                case '+': {
                    if (last == 5) {
                        last = 6;
                        break;
                    }
                    return 0;
                }
                case 'E': 
                case 'e': {
                    if (last == 2 || last == 4) {
                        last = 5;
                        break;
                    }
                    return 0;
                }
                case '.': {
                    if (last == 2) {
                        last = 3;
                        break;
                    }
                    return 0;
                }
                default: {
                    if (c2 < '0' || c2 > '9') {
                        if (!this.isLiteral(c2)) break block6;
                        return 0;
                    }
                    if (last == 1 || last == 0) {
                        value = -(c2 - 48);
                        last = 2;
                        break;
                    }
                    if (last == 2) {
                        if (value == 0L) {
                            return 0;
                        }
                        long newValue = value * 10L - (long)(c2 - 48);
                        fitsInLong &= value > -922337203685477580L || value == -922337203685477580L && newValue < value;
                        value = newValue;
                        break;
                    }
                    if (last == 3) {
                        last = 4;
                        break;
                    }
                    if (last != 5 && last != 6) break;
                    last = 7;
                }
            }
            ++i2;
        }
        if (last == 2 && fitsInLong && (value != Long.MIN_VALUE || negative)) {
            this.peekedLong = negative ? value : -value;
            this.pos += i2;
            this.peeked = 15;
            return 15;
        }
        if (last == 2 || last == 4 || last == 7) {
            this.peekedNumberLength = i2;
            this.peeked = 16;
            return 16;
        }
        return 0;
    }

    private boolean isLiteral(char c2) throws IOException {
        switch (c2) {
            case '#': 
            case '/': 
            case ';': 
            case '=': 
            case '\\': {
                this.checkLenient();
            }
            case '\t': 
            case '\n': 
            case '\f': 
            case '\r': 
            case ' ': 
            case ',': 
            case ':': 
            case '[': 
            case ']': 
            case '{': 
            case '}': {
                return false;
            }
        }
        return true;
    }

    public String nextName() throws IOException {
        String result;
        int p2 = this.peeked;
        if (p2 == 0) {
            p2 = this.doPeek();
        }
        if (p2 == 14) {
            result = this.nextUnquotedValue();
        } else if (p2 == 12) {
            result = this.nextQuotedValue('\'');
        } else if (p2 == 13) {
            result = this.nextQuotedValue('\"');
        } else {
            throw new IllegalStateException("Expected a name but was " + (Object)((Object)this.peek()) + " at line " + this.getLineNumber() + " column " + this.getColumnNumber());
        }
        this.peeked = 0;
        return result;
    }

    public String nextString() throws IOException {
        String result;
        int p2 = this.peeked;
        if (p2 == 0) {
            p2 = this.doPeek();
        }
        if (p2 == 10) {
            result = this.nextUnquotedValue();
        } else if (p2 == 8) {
            result = this.nextQuotedValue('\'');
        } else if (p2 == 9) {
            result = this.nextQuotedValue('\"');
        } else if (p2 == 11) {
            result = this.peekedString;
            this.peekedString = null;
        } else if (p2 == 15) {
            result = Long.toString(this.peekedLong);
        } else if (p2 == 16) {
            result = new String(this.buffer, this.pos, this.peekedNumberLength);
            this.pos += this.peekedNumberLength;
        } else {
            throw new IllegalStateException("Expected a string but was " + (Object)((Object)this.peek()) + " at line " + this.getLineNumber() + " column " + this.getColumnNumber());
        }
        this.peeked = 0;
        return result;
    }

    public boolean nextBoolean() throws IOException {
        int p2 = this.peeked;
        if (p2 == 0) {
            p2 = this.doPeek();
        }
        if (p2 == 5) {
            this.peeked = 0;
            return true;
        }
        if (p2 == 6) {
            this.peeked = 0;
            return false;
        }
        throw new IllegalStateException("Expected a boolean but was " + (Object)((Object)this.peek()) + " at line " + this.getLineNumber() + " column " + this.getColumnNumber());
    }

    public void nextNull() throws IOException {
        int p2 = this.peeked;
        if (p2 == 0) {
            p2 = this.doPeek();
        }
        if (p2 != 7) {
            throw new IllegalStateException("Expected null but was " + (Object)((Object)this.peek()) + " at line " + this.getLineNumber() + " column " + this.getColumnNumber());
        }
        this.peeked = 0;
    }

    public double nextDouble() throws IOException {
        int p2 = this.peeked;
        if (p2 == 0) {
            p2 = this.doPeek();
        }
        if (p2 == 15) {
            this.peeked = 0;
            return this.peekedLong;
        }
        if (p2 == 16) {
            this.peekedString = new String(this.buffer, this.pos, this.peekedNumberLength);
            this.pos += this.peekedNumberLength;
        } else if (p2 == 8 || p2 == 9) {
            this.peekedString = this.nextQuotedValue(p2 == 8 ? (char)'\'' : '\"');
        } else if (p2 == 10) {
            this.peekedString = this.nextUnquotedValue();
        } else if (p2 != 11) {
            throw new IllegalStateException("Expected a double but was " + (Object)((Object)this.peek()) + " at line " + this.getLineNumber() + " column " + this.getColumnNumber());
        }
        this.peeked = 11;
        double result = Double.parseDouble(this.peekedString);
        if (!this.lenient && (Double.isNaN(result) || Double.isInfinite(result))) {
            throw new MalformedJsonException("JSON forbids NaN and infinities: " + result + " at line " + this.getLineNumber() + " column " + this.getColumnNumber());
        }
        this.peekedString = null;
        this.peeked = 0;
        return result;
    }

    public long nextLong() throws IOException {
        int p2 = this.peeked;
        if (p2 == 0) {
            p2 = this.doPeek();
        }
        if (p2 == 15) {
            this.peeked = 0;
            return this.peekedLong;
        }
        if (p2 == 16) {
            this.peekedString = new String(this.buffer, this.pos, this.peekedNumberLength);
            this.pos += this.peekedNumberLength;
        } else if (p2 == 8 || p2 == 9) {
            this.peekedString = this.nextQuotedValue(p2 == 8 ? (char)'\'' : '\"');
            try {
                long result = Long.parseLong(this.peekedString);
                this.peeked = 0;
                return result;
            }
            catch (NumberFormatException ignored) {}
        } else {
            throw new IllegalStateException("Expected a long but was " + (Object)((Object)this.peek()) + " at line " + this.getLineNumber() + " column " + this.getColumnNumber());
        }
        this.peeked = 11;
        double asDouble = Double.parseDouble(this.peekedString);
        long result = (long)asDouble;
        if ((double)result != asDouble) {
            throw new NumberFormatException("Expected a long but was " + this.peekedString + " at line " + this.getLineNumber() + " column " + this.getColumnNumber());
        }
        this.peekedString = null;
        this.peeked = 0;
        return result;
    }

    private String nextQuotedValue(char quote) throws IOException {
        char[] buffer = this.buffer;
        StringBuilder builder = new StringBuilder();
        do {
            int p2 = this.pos;
            int l2 = this.limit;
            int start = p2;
            while (p2 < l2) {
                char c2;
                if ((c2 = buffer[p2++]) == quote) {
                    this.pos = p2;
                    builder.append(buffer, start, p2 - start - 1);
                    return builder.toString();
                }
                if (c2 == '\\') {
                    this.pos = p2;
                    builder.append(buffer, start, p2 - start - 1);
                    builder.append(this.readEscapeCharacter());
                    p2 = this.pos;
                    l2 = this.limit;
                    start = p2;
                    continue;
                }
                if (c2 != '\n') continue;
                ++this.lineNumber;
                this.lineStart = p2;
            }
            builder.append(buffer, start, p2 - start);
            this.pos = p2;
        } while (this.fillBuffer(1));
        throw this.syntaxError("Unterminated string");
    }

    private String nextUnquotedValue() throws IOException {
        String result;
        StringBuilder builder = null;
        int i2 = 0;
        block4: while (true) {
            if (this.pos + i2 < this.limit) {
                switch (this.buffer[this.pos + i2]) {
                    case '#': 
                    case '/': 
                    case ';': 
                    case '=': 
                    case '\\': {
                        this.checkLenient();
                    }
                    case '\t': 
                    case '\n': 
                    case '\f': 
                    case '\r': 
                    case ' ': 
                    case ',': 
                    case ':': 
                    case '[': 
                    case ']': 
                    case '{': 
                    case '}': {
                        break block4;
                    }
                    default: {
                        ++i2;
                        break;
                    }
                }
                continue;
            }
            if (i2 < this.buffer.length) {
                if (!this.fillBuffer(i2 + 1)) break;
                continue;
            }
            if (builder == null) {
                builder = new StringBuilder();
            }
            builder.append(this.buffer, this.pos, i2);
            this.pos += i2;
            i2 = 0;
            if (!this.fillBuffer(1)) break;
        }
        if (builder == null) {
            result = new String(this.buffer, this.pos, i2);
        } else {
            builder.append(this.buffer, this.pos, i2);
            result = builder.toString();
        }
        this.pos += i2;
        return result;
    }

    private void skipQuotedValue(char quote) throws IOException {
        char[] buffer = this.buffer;
        do {
            int p2 = this.pos;
            int l2 = this.limit;
            while (p2 < l2) {
                char c2;
                if ((c2 = buffer[p2++]) == quote) {
                    this.pos = p2;
                    return;
                }
                if (c2 == '\\') {
                    this.pos = p2;
                    this.readEscapeCharacter();
                    p2 = this.pos;
                    l2 = this.limit;
                    continue;
                }
                if (c2 != '\n') continue;
                ++this.lineNumber;
                this.lineStart = p2;
            }
            this.pos = p2;
        } while (this.fillBuffer(1));
        throw this.syntaxError("Unterminated string");
    }

    private void skipUnquotedValue() throws IOException {
        do {
            int i2 = 0;
            while (this.pos + i2 < this.limit) {
                switch (this.buffer[this.pos + i2]) {
                    case '#': 
                    case '/': 
                    case ';': 
                    case '=': 
                    case '\\': {
                        this.checkLenient();
                    }
                    case '\t': 
                    case '\n': 
                    case '\f': 
                    case '\r': 
                    case ' ': 
                    case ',': 
                    case ':': 
                    case '[': 
                    case ']': 
                    case '{': 
                    case '}': {
                        this.pos += i2;
                        return;
                    }
                }
                ++i2;
            }
            this.pos += i2;
        } while (this.fillBuffer(1));
    }

    public int nextInt() throws IOException {
        int p2 = this.peeked;
        if (p2 == 0) {
            p2 = this.doPeek();
        }
        if (p2 == 15) {
            int result = (int)this.peekedLong;
            if (this.peekedLong != (long)result) {
                throw new NumberFormatException("Expected an int but was " + this.peekedLong + " at line " + this.getLineNumber() + " column " + this.getColumnNumber());
            }
            this.peeked = 0;
            return result;
        }
        if (p2 == 16) {
            this.peekedString = new String(this.buffer, this.pos, this.peekedNumberLength);
            this.pos += this.peekedNumberLength;
        } else if (p2 == 8 || p2 == 9) {
            this.peekedString = this.nextQuotedValue(p2 == 8 ? (char)'\'' : '\"');
            try {
                int result = Integer.parseInt(this.peekedString);
                this.peeked = 0;
                return result;
            }
            catch (NumberFormatException ignored) {}
        } else {
            throw new IllegalStateException("Expected an int but was " + (Object)((Object)this.peek()) + " at line " + this.getLineNumber() + " column " + this.getColumnNumber());
        }
        this.peeked = 11;
        double asDouble = Double.parseDouble(this.peekedString);
        int result = (int)asDouble;
        if ((double)result != asDouble) {
            throw new NumberFormatException("Expected an int but was " + this.peekedString + " at line " + this.getLineNumber() + " column " + this.getColumnNumber());
        }
        this.peekedString = null;
        this.peeked = 0;
        return result;
    }

    public void close() throws IOException {
        this.peeked = 0;
        this.stack[0] = 8;
        this.stackSize = 1;
        this.in.close();
    }

    public void skipValue() throws IOException {
        int count = 0;
        do {
            int p2;
            if ((p2 = this.peeked) == 0) {
                p2 = this.doPeek();
            }
            if (p2 == 3) {
                this.push(1);
                ++count;
            } else if (p2 == 1) {
                this.push(3);
                ++count;
            } else if (p2 == 4) {
                --this.stackSize;
                --count;
            } else if (p2 == 2) {
                --this.stackSize;
                --count;
            } else if (p2 == 14 || p2 == 10) {
                this.skipUnquotedValue();
            } else if (p2 == 8 || p2 == 12) {
                this.skipQuotedValue('\'');
            } else if (p2 == 9 || p2 == 13) {
                this.skipQuotedValue('\"');
            } else if (p2 == 16) {
                this.pos += this.peekedNumberLength;
            }
            this.peeked = 0;
        } while (count != 0);
    }

    private void push(int newTop) {
        if (this.stackSize == this.stack.length) {
            int[] newStack = new int[this.stackSize * 2];
            System.arraycopy(this.stack, 0, newStack, 0, this.stackSize);
            this.stack = newStack;
        }
        this.stack[this.stackSize++] = newTop;
    }

    private boolean fillBuffer(int minimum) throws IOException {
        int total;
        char[] buffer = this.buffer;
        this.lineStart -= this.pos;
        if (this.limit != this.pos) {
            this.limit -= this.pos;
            System.arraycopy(buffer, this.pos, buffer, 0, this.limit);
        } else {
            this.limit = 0;
        }
        this.pos = 0;
        while ((total = this.in.read(buffer, this.limit, buffer.length - this.limit)) != -1) {
            this.limit += total;
            if (this.lineNumber == 0 && this.lineStart == 0 && this.limit > 0 && buffer[0] == '\ufeff') {
                ++this.pos;
                ++this.lineStart;
                ++minimum;
            }
            if (this.limit < minimum) continue;
            return true;
        }
        return false;
    }

    private int getLineNumber() {
        return this.lineNumber + 1;
    }

    private int getColumnNumber() {
        return this.pos - this.lineStart + 1;
    }

    private int nextNonWhitespace(boolean throwOnEof) throws IOException {
        block12: {
            char c2;
            char[] buffer = this.buffer;
            int p2 = this.pos;
            int l2 = this.limit;
            block4: while (true) {
                if (p2 == l2) {
                    this.pos = p2;
                    if (!this.fillBuffer(1)) break block12;
                    p2 = this.pos;
                    l2 = this.limit;
                }
                if ((c2 = buffer[p2++]) == '\n') {
                    ++this.lineNumber;
                    this.lineStart = p2;
                    continue;
                }
                if (c2 == ' ' || c2 == '\r' || c2 == '\t') continue;
                if (c2 == '/') {
                    this.pos = p2;
                    if (p2 == l2) {
                        --this.pos;
                        boolean charsLoaded = this.fillBuffer(2);
                        ++this.pos;
                        if (!charsLoaded) {
                            return c2;
                        }
                    }
                    this.checkLenient();
                    char peek = buffer[this.pos];
                    switch (peek) {
                        case '*': {
                            ++this.pos;
                            if (!this.skipTo("*/")) {
                                throw this.syntaxError("Unterminated comment");
                            }
                            p2 = this.pos + 2;
                            l2 = this.limit;
                            continue block4;
                        }
                        case '/': {
                            ++this.pos;
                            this.skipToEndOfLine();
                            p2 = this.pos;
                            l2 = this.limit;
                            continue block4;
                        }
                    }
                    return c2;
                }
                if (c2 != '#') break;
                this.pos = p2;
                this.checkLenient();
                this.skipToEndOfLine();
                p2 = this.pos;
                l2 = this.limit;
            }
            this.pos = p2;
            return c2;
        }
        if (throwOnEof) {
            throw new EOFException("End of input at line " + this.getLineNumber() + " column " + this.getColumnNumber());
        }
        return -1;
    }

    private void checkLenient() throws IOException {
        if (!this.lenient) {
            throw this.syntaxError("Use JsonReader.setLenient(true) to accept malformed JSON");
        }
    }

    private void skipToEndOfLine() throws IOException {
        while (this.pos < this.limit || this.fillBuffer(1)) {
            char c2;
            if ((c2 = this.buffer[this.pos++]) == '\n') {
                ++this.lineNumber;
                this.lineStart = this.pos;
                break;
            }
            if (c2 != '\r') continue;
            break;
        }
    }

    private boolean skipTo(String toFind) throws IOException {
        while (this.pos + toFind.length() <= this.limit || this.fillBuffer(toFind.length())) {
            block5: {
                if (this.buffer[this.pos] == '\n') {
                    ++this.lineNumber;
                    this.lineStart = this.pos + 1;
                } else {
                    for (int c2 = 0; c2 < toFind.length(); ++c2) {
                        if (this.buffer[this.pos + c2] == toFind.charAt(c2)) {
                            continue;
                        }
                        break block5;
                    }
                    return true;
                }
            }
            ++this.pos;
        }
        return false;
    }

    public String toString() {
        return this.getClass().getSimpleName() + " at line " + this.getLineNumber() + " column " + this.getColumnNumber();
    }

    private char readEscapeCharacter() throws IOException {
        if (this.pos == this.limit && !this.fillBuffer(1)) {
            throw this.syntaxError("Unterminated escape sequence");
        }
        char escaped = this.buffer[this.pos++];
        switch (escaped) {
            case 'u': {
                int i2;
                if (this.pos + 4 > this.limit && !this.fillBuffer(4)) {
                    throw this.syntaxError("Unterminated escape sequence");
                }
                char result = '\u0000';
                int end = i2 + 4;
                for (i2 = this.pos; i2 < end; ++i2) {
                    char c2 = this.buffer[i2];
                    result = (char)(result << 4);
                    if (c2 >= '0' && c2 <= '9') {
                        result = (char)(result + (c2 - 48));
                        continue;
                    }
                    if (c2 >= 'a' && c2 <= 'f') {
                        result = (char)(result + (c2 - 97 + 10));
                        continue;
                    }
                    if (c2 >= 'A' && c2 <= 'F') {
                        result = (char)(result + (c2 - 65 + 10));
                        continue;
                    }
                    throw new NumberFormatException("\\u" + new String(this.buffer, this.pos, 4));
                }
                this.pos += 4;
                return result;
            }
            case 't': {
                return '\t';
            }
            case 'b': {
                return '\b';
            }
            case 'n': {
                return '\n';
            }
            case 'r': {
                return '\r';
            }
            case 'f': {
                return '\f';
            }
            case '\n': {
                ++this.lineNumber;
                this.lineStart = this.pos;
            }
        }
        return escaped;
    }

    private IOException syntaxError(String message) throws IOException {
        throw new MalformedJsonException(message + " at line " + this.getLineNumber() + " column " + this.getColumnNumber());
    }

    private void consumeNonExecutePrefix() throws IOException {
        this.nextNonWhitespace(true);
        --this.pos;
        if (this.pos + NON_EXECUTE_PREFIX.length > this.limit && !this.fillBuffer(NON_EXECUTE_PREFIX.length)) {
            return;
        }
        for (int i2 = 0; i2 < NON_EXECUTE_PREFIX.length; ++i2) {
            if (this.buffer[this.pos + i2] == NON_EXECUTE_PREFIX[i2]) continue;
            return;
        }
        this.pos += NON_EXECUTE_PREFIX.length;
    }

    static {
        JsonReaderInternalAccess.INSTANCE = new JsonReaderInternalAccess(){

            public void promoteNameToValue(JsonReader reader) throws IOException {
                if (reader instanceof JsonTreeReader) {
                    ((JsonTreeReader)reader).promoteNameToValue();
                    return;
                }
                int p2 = reader.peeked;
                if (p2 == 0) {
                    p2 = reader.doPeek();
                }
                if (p2 == 13) {
                    reader.peeked = 9;
                } else if (p2 == 12) {
                    reader.peeked = 8;
                } else if (p2 == 14) {
                    reader.peeked = 10;
                } else {
                    throw new IllegalStateException("Expected a name but was " + (Object)((Object)reader.peek()) + " " + " at line " + reader.getLineNumber() + " column " + reader.getColumnNumber());
                }
            }
        };
    }
}

