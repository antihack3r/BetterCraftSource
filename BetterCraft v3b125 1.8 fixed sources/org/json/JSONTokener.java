/*
 * Decompiled with CFR 0.152.
 */
package org.json;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONTokener {
    private long character;
    private boolean eof;
    private long index;
    private long line;
    private char previous;
    private final Reader reader;
    private boolean usePrevious;
    private long characterPreviousLine;

    public JSONTokener(Reader reader) {
        this.reader = reader.markSupported() ? reader : new BufferedReader(reader);
        this.eof = false;
        this.usePrevious = false;
        this.previous = '\u0000';
        this.index = 0L;
        this.character = 1L;
        this.characterPreviousLine = 0L;
        this.line = 1L;
    }

    public JSONTokener(InputStream inputStream) {
        this(new InputStreamReader(inputStream));
    }

    public JSONTokener(String s2) {
        this(new StringReader(s2));
    }

    public void back() throws JSONException {
        if (this.usePrevious || this.index <= 0L) {
            throw new JSONException("Stepping back two steps is not supported");
        }
        this.decrementIndexes();
        this.usePrevious = true;
        this.eof = false;
    }

    private void decrementIndexes() {
        --this.index;
        if (this.previous == '\r' || this.previous == '\n') {
            --this.line;
            this.character = this.characterPreviousLine;
        } else if (this.character > 0L) {
            --this.character;
        }
    }

    public static int dehexchar(char c2) {
        if (c2 >= '0' && c2 <= '9') {
            return c2 - 48;
        }
        if (c2 >= 'A' && c2 <= 'F') {
            return c2 - 55;
        }
        if (c2 >= 'a' && c2 <= 'f') {
            return c2 - 87;
        }
        return -1;
    }

    public boolean end() {
        return this.eof && !this.usePrevious;
    }

    public boolean more() throws JSONException {
        if (this.usePrevious) {
            return true;
        }
        try {
            this.reader.mark(1);
        }
        catch (IOException e2) {
            throw new JSONException("Unable to preserve stream position", e2);
        }
        try {
            if (this.reader.read() <= 0) {
                this.eof = true;
                return false;
            }
            this.reader.reset();
        }
        catch (IOException e3) {
            throw new JSONException("Unable to read the next character from the stream", e3);
        }
        return true;
    }

    public char next() throws JSONException {
        int c2;
        if (this.usePrevious) {
            this.usePrevious = false;
            c2 = this.previous;
        } else {
            try {
                c2 = this.reader.read();
            }
            catch (IOException exception) {
                throw new JSONException(exception);
            }
        }
        if (c2 <= 0) {
            this.eof = true;
            return '\u0000';
        }
        this.incrementIndexes(c2);
        this.previous = (char)c2;
        return this.previous;
    }

    private void incrementIndexes(int c2) {
        if (c2 > 0) {
            ++this.index;
            if (c2 == 13) {
                ++this.line;
                this.characterPreviousLine = this.character;
                this.character = 0L;
            } else if (c2 == 10) {
                if (this.previous != '\r') {
                    ++this.line;
                    this.characterPreviousLine = this.character;
                }
                this.character = 0L;
            } else {
                ++this.character;
            }
        }
    }

    public char next(char c2) throws JSONException {
        char n2 = this.next();
        if (n2 != c2) {
            if (n2 > '\u0000') {
                throw this.syntaxError("Expected '" + c2 + "' and instead saw '" + n2 + "'");
            }
            throw this.syntaxError("Expected '" + c2 + "' and instead saw ''");
        }
        return n2;
    }

    public String next(int n2) throws JSONException {
        if (n2 == 0) {
            return "";
        }
        char[] chars = new char[n2];
        for (int pos = 0; pos < n2; ++pos) {
            chars[pos] = this.next();
            if (!this.end()) continue;
            throw this.syntaxError("Substring bounds error");
        }
        return new String(chars);
    }

    public char nextClean() throws JSONException {
        char c2;
        while ((c2 = this.next()) != '\u0000' && c2 <= ' ') {
        }
        return c2;
    }

    public String nextString(char quote) throws JSONException {
        StringBuilder sb2 = new StringBuilder();
        block15: while (true) {
            char c2 = this.next();
            switch (c2) {
                case '\u0000': 
                case '\n': 
                case '\r': {
                    throw this.syntaxError("Unterminated string");
                }
                case '\\': {
                    c2 = this.next();
                    switch (c2) {
                        case 'b': {
                            sb2.append('\b');
                            continue block15;
                        }
                        case 't': {
                            sb2.append('\t');
                            continue block15;
                        }
                        case 'n': {
                            sb2.append('\n');
                            continue block15;
                        }
                        case 'f': {
                            sb2.append('\f');
                            continue block15;
                        }
                        case 'r': {
                            sb2.append('\r');
                            continue block15;
                        }
                        case 'u': {
                            try {
                                sb2.append((char)Integer.parseInt(this.next(4), 16));
                                continue block15;
                            }
                            catch (NumberFormatException e2) {
                                throw this.syntaxError("Illegal escape.", e2);
                            }
                        }
                        case '\"': 
                        case '\'': 
                        case '/': 
                        case '\\': {
                            sb2.append(c2);
                            continue block15;
                        }
                    }
                    throw this.syntaxError("Illegal escape.");
                }
            }
            if (c2 == quote) {
                return sb2.toString();
            }
            sb2.append(c2);
        }
    }

    public String nextTo(char delimiter) throws JSONException {
        StringBuilder sb2 = new StringBuilder();
        while (true) {
            char c2;
            if ((c2 = this.next()) == delimiter || c2 == '\u0000' || c2 == '\n' || c2 == '\r') {
                if (c2 != '\u0000') {
                    this.back();
                }
                return sb2.toString().trim();
            }
            sb2.append(c2);
        }
    }

    public String nextTo(String delimiters) throws JSONException {
        StringBuilder sb2 = new StringBuilder();
        while (true) {
            char c2;
            if (delimiters.indexOf(c2 = this.next()) >= 0 || c2 == '\u0000' || c2 == '\n' || c2 == '\r') {
                if (c2 != '\u0000') {
                    this.back();
                }
                return sb2.toString().trim();
            }
            sb2.append(c2);
        }
    }

    public Object nextValue() throws JSONException {
        char c2 = this.nextClean();
        switch (c2) {
            case '\"': 
            case '\'': {
                return this.nextString(c2);
            }
            case '{': {
                this.back();
                return new JSONObject(this);
            }
            case '[': {
                this.back();
                return new JSONArray(this);
            }
        }
        StringBuilder sb2 = new StringBuilder();
        while (c2 >= ' ' && ",:]}/\\\"[{;=#".indexOf(c2) < 0) {
            sb2.append(c2);
            c2 = this.next();
        }
        this.back();
        String string = sb2.toString().trim();
        if ("".equals(string)) {
            throw this.syntaxError("Missing value");
        }
        return JSONObject.stringToValue(string);
    }

    public char skipTo(char to2) throws JSONException {
        char c2;
        try {
            long startIndex = this.index;
            long startCharacter = this.character;
            long startLine = this.line;
            this.reader.mark(1000000);
            do {
                if ((c2 = this.next()) != '\u0000') continue;
                this.reader.reset();
                this.index = startIndex;
                this.character = startCharacter;
                this.line = startLine;
                return '\u0000';
            } while (c2 != to2);
            this.reader.mark(1);
        }
        catch (IOException exception) {
            throw new JSONException(exception);
        }
        this.back();
        return c2;
    }

    public JSONException syntaxError(String message) {
        return new JSONException(message + this.toString());
    }

    public JSONException syntaxError(String message, Throwable causedBy) {
        return new JSONException(message + this.toString(), causedBy);
    }

    public String toString() {
        return " at " + this.index + " [character " + this.character + " line " + this.line + "]";
    }
}

