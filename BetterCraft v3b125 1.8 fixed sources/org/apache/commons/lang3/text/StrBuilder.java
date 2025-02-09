/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.lang3.text;

import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.builder.Builder;
import org.apache.commons.lang3.text.StrMatcher;
import org.apache.commons.lang3.text.StrTokenizer;

public class StrBuilder
implements CharSequence,
Appendable,
Serializable,
Builder<String> {
    static final int CAPACITY = 32;
    private static final long serialVersionUID = 7628716375283629643L;
    protected char[] buffer;
    protected int size;
    private String newLine;
    private String nullText;

    public StrBuilder() {
        this(32);
    }

    public StrBuilder(int initialCapacity) {
        if (initialCapacity <= 0) {
            initialCapacity = 32;
        }
        this.buffer = new char[initialCapacity];
    }

    public StrBuilder(String str) {
        if (str == null) {
            this.buffer = new char[32];
        } else {
            this.buffer = new char[str.length() + 32];
            this.append(str);
        }
    }

    public String getNewLineText() {
        return this.newLine;
    }

    public StrBuilder setNewLineText(String newLine) {
        this.newLine = newLine;
        return this;
    }

    public String getNullText() {
        return this.nullText;
    }

    public StrBuilder setNullText(String nullText) {
        if (nullText != null && nullText.isEmpty()) {
            nullText = null;
        }
        this.nullText = nullText;
        return this;
    }

    @Override
    public int length() {
        return this.size;
    }

    public StrBuilder setLength(int length) {
        if (length < 0) {
            throw new StringIndexOutOfBoundsException(length);
        }
        if (length < this.size) {
            this.size = length;
        } else if (length > this.size) {
            this.ensureCapacity(length);
            int oldEnd = this.size;
            int newEnd = length;
            this.size = length;
            for (int i2 = oldEnd; i2 < newEnd; ++i2) {
                this.buffer[i2] = '\u0000';
            }
        }
        return this;
    }

    public int capacity() {
        return this.buffer.length;
    }

    public StrBuilder ensureCapacity(int capacity) {
        if (capacity > this.buffer.length) {
            char[] old = this.buffer;
            this.buffer = new char[capacity * 2];
            System.arraycopy(old, 0, this.buffer, 0, this.size);
        }
        return this;
    }

    public StrBuilder minimizeCapacity() {
        if (this.buffer.length > this.length()) {
            char[] old = this.buffer;
            this.buffer = new char[this.length()];
            System.arraycopy(old, 0, this.buffer, 0, this.size);
        }
        return this;
    }

    public int size() {
        return this.size;
    }

    @Override
    public boolean isEmpty() {
        return this.size == 0;
    }

    public StrBuilder clear() {
        this.size = 0;
        return this;
    }

    @Override
    public char charAt(int index) {
        if (index < 0 || index >= this.length()) {
            throw new StringIndexOutOfBoundsException(index);
        }
        return this.buffer[index];
    }

    public StrBuilder setCharAt(int index, char ch) {
        if (index < 0 || index >= this.length()) {
            throw new StringIndexOutOfBoundsException(index);
        }
        this.buffer[index] = ch;
        return this;
    }

    public StrBuilder deleteCharAt(int index) {
        if (index < 0 || index >= this.size) {
            throw new StringIndexOutOfBoundsException(index);
        }
        this.deleteImpl(index, index + 1, 1);
        return this;
    }

    public char[] toCharArray() {
        if (this.size == 0) {
            return ArrayUtils.EMPTY_CHAR_ARRAY;
        }
        char[] chars = new char[this.size];
        System.arraycopy(this.buffer, 0, chars, 0, this.size);
        return chars;
    }

    public char[] toCharArray(int startIndex, int endIndex) {
        int len = (endIndex = this.validateRange(startIndex, endIndex)) - startIndex;
        if (len == 0) {
            return ArrayUtils.EMPTY_CHAR_ARRAY;
        }
        char[] chars = new char[len];
        System.arraycopy(this.buffer, startIndex, chars, 0, len);
        return chars;
    }

    public char[] getChars(char[] destination) {
        int len = this.length();
        if (destination == null || destination.length < len) {
            destination = new char[len];
        }
        System.arraycopy(this.buffer, 0, destination, 0, len);
        return destination;
    }

    public void getChars(int startIndex, int endIndex, char[] destination, int destinationIndex) {
        if (startIndex < 0) {
            throw new StringIndexOutOfBoundsException(startIndex);
        }
        if (endIndex < 0 || endIndex > this.length()) {
            throw new StringIndexOutOfBoundsException(endIndex);
        }
        if (startIndex > endIndex) {
            throw new StringIndexOutOfBoundsException("end < start");
        }
        System.arraycopy(this.buffer, startIndex, destination, destinationIndex, endIndex - startIndex);
    }

    public StrBuilder appendNewLine() {
        if (this.newLine == null) {
            this.append(SystemUtils.LINE_SEPARATOR);
            return this;
        }
        return this.append(this.newLine);
    }

    public StrBuilder appendNull() {
        if (this.nullText == null) {
            return this;
        }
        return this.append(this.nullText);
    }

    public StrBuilder append(Object obj) {
        if (obj == null) {
            return this.appendNull();
        }
        return this.append(obj.toString());
    }

    @Override
    public StrBuilder append(CharSequence seq) {
        if (seq == null) {
            return this.appendNull();
        }
        return this.append(seq.toString());
    }

    @Override
    public StrBuilder append(CharSequence seq, int startIndex, int length) {
        if (seq == null) {
            return this.appendNull();
        }
        return this.append(seq.toString(), startIndex, length);
    }

    public StrBuilder append(String str) {
        if (str == null) {
            return this.appendNull();
        }
        int strLen = str.length();
        if (strLen > 0) {
            int len = this.length();
            this.ensureCapacity(len + strLen);
            str.getChars(0, strLen, this.buffer, len);
            this.size += strLen;
        }
        return this;
    }

    public StrBuilder append(String str, int startIndex, int length) {
        if (str == null) {
            return this.appendNull();
        }
        if (startIndex < 0 || startIndex > str.length()) {
            throw new StringIndexOutOfBoundsException("startIndex must be valid");
        }
        if (length < 0 || startIndex + length > str.length()) {
            throw new StringIndexOutOfBoundsException("length must be valid");
        }
        if (length > 0) {
            int len = this.length();
            this.ensureCapacity(len + length);
            str.getChars(startIndex, startIndex + length, this.buffer, len);
            this.size += length;
        }
        return this;
    }

    public StrBuilder append(String format, Object ... objs) {
        return this.append(String.format(format, objs));
    }

    public StrBuilder append(StringBuffer str) {
        if (str == null) {
            return this.appendNull();
        }
        int strLen = str.length();
        if (strLen > 0) {
            int len = this.length();
            this.ensureCapacity(len + strLen);
            str.getChars(0, strLen, this.buffer, len);
            this.size += strLen;
        }
        return this;
    }

    public StrBuilder append(StringBuffer str, int startIndex, int length) {
        if (str == null) {
            return this.appendNull();
        }
        if (startIndex < 0 || startIndex > str.length()) {
            throw new StringIndexOutOfBoundsException("startIndex must be valid");
        }
        if (length < 0 || startIndex + length > str.length()) {
            throw new StringIndexOutOfBoundsException("length must be valid");
        }
        if (length > 0) {
            int len = this.length();
            this.ensureCapacity(len + length);
            str.getChars(startIndex, startIndex + length, this.buffer, len);
            this.size += length;
        }
        return this;
    }

    public StrBuilder append(StringBuilder str) {
        if (str == null) {
            return this.appendNull();
        }
        int strLen = str.length();
        if (strLen > 0) {
            int len = this.length();
            this.ensureCapacity(len + strLen);
            str.getChars(0, strLen, this.buffer, len);
            this.size += strLen;
        }
        return this;
    }

    public StrBuilder append(StringBuilder str, int startIndex, int length) {
        if (str == null) {
            return this.appendNull();
        }
        if (startIndex < 0 || startIndex > str.length()) {
            throw new StringIndexOutOfBoundsException("startIndex must be valid");
        }
        if (length < 0 || startIndex + length > str.length()) {
            throw new StringIndexOutOfBoundsException("length must be valid");
        }
        if (length > 0) {
            int len = this.length();
            this.ensureCapacity(len + length);
            str.getChars(startIndex, startIndex + length, this.buffer, len);
            this.size += length;
        }
        return this;
    }

    public StrBuilder append(StrBuilder str) {
        if (str == null) {
            return this.appendNull();
        }
        int strLen = str.length();
        if (strLen > 0) {
            int len = this.length();
            this.ensureCapacity(len + strLen);
            System.arraycopy(str.buffer, 0, this.buffer, len, strLen);
            this.size += strLen;
        }
        return this;
    }

    public StrBuilder append(StrBuilder str, int startIndex, int length) {
        if (str == null) {
            return this.appendNull();
        }
        if (startIndex < 0 || startIndex > str.length()) {
            throw new StringIndexOutOfBoundsException("startIndex must be valid");
        }
        if (length < 0 || startIndex + length > str.length()) {
            throw new StringIndexOutOfBoundsException("length must be valid");
        }
        if (length > 0) {
            int len = this.length();
            this.ensureCapacity(len + length);
            str.getChars(startIndex, startIndex + length, this.buffer, len);
            this.size += length;
        }
        return this;
    }

    public StrBuilder append(char[] chars) {
        if (chars == null) {
            return this.appendNull();
        }
        int strLen = chars.length;
        if (strLen > 0) {
            int len = this.length();
            this.ensureCapacity(len + strLen);
            System.arraycopy(chars, 0, this.buffer, len, strLen);
            this.size += strLen;
        }
        return this;
    }

    public StrBuilder append(char[] chars, int startIndex, int length) {
        if (chars == null) {
            return this.appendNull();
        }
        if (startIndex < 0 || startIndex > chars.length) {
            throw new StringIndexOutOfBoundsException("Invalid startIndex: " + length);
        }
        if (length < 0 || startIndex + length > chars.length) {
            throw new StringIndexOutOfBoundsException("Invalid length: " + length);
        }
        if (length > 0) {
            int len = this.length();
            this.ensureCapacity(len + length);
            System.arraycopy(chars, startIndex, this.buffer, len, length);
            this.size += length;
        }
        return this;
    }

    public StrBuilder append(boolean value) {
        if (value) {
            this.ensureCapacity(this.size + 4);
            this.buffer[this.size++] = 116;
            this.buffer[this.size++] = 114;
            this.buffer[this.size++] = 117;
            this.buffer[this.size++] = 101;
        } else {
            this.ensureCapacity(this.size + 5);
            this.buffer[this.size++] = 102;
            this.buffer[this.size++] = 97;
            this.buffer[this.size++] = 108;
            this.buffer[this.size++] = 115;
            this.buffer[this.size++] = 101;
        }
        return this;
    }

    @Override
    public StrBuilder append(char ch) {
        int len = this.length();
        this.ensureCapacity(len + 1);
        this.buffer[this.size++] = ch;
        return this;
    }

    public StrBuilder append(int value) {
        return this.append(String.valueOf(value));
    }

    public StrBuilder append(long value) {
        return this.append(String.valueOf(value));
    }

    public StrBuilder append(float value) {
        return this.append(String.valueOf(value));
    }

    public StrBuilder append(double value) {
        return this.append(String.valueOf(value));
    }

    public StrBuilder appendln(Object obj) {
        return this.append(obj).appendNewLine();
    }

    public StrBuilder appendln(String str) {
        return this.append(str).appendNewLine();
    }

    public StrBuilder appendln(String str, int startIndex, int length) {
        return this.append(str, startIndex, length).appendNewLine();
    }

    public StrBuilder appendln(String format, Object ... objs) {
        return this.append(format, objs).appendNewLine();
    }

    public StrBuilder appendln(StringBuffer str) {
        return this.append(str).appendNewLine();
    }

    public StrBuilder appendln(StringBuilder str) {
        return this.append(str).appendNewLine();
    }

    public StrBuilder appendln(StringBuilder str, int startIndex, int length) {
        return this.append(str, startIndex, length).appendNewLine();
    }

    public StrBuilder appendln(StringBuffer str, int startIndex, int length) {
        return this.append(str, startIndex, length).appendNewLine();
    }

    public StrBuilder appendln(StrBuilder str) {
        return this.append(str).appendNewLine();
    }

    public StrBuilder appendln(StrBuilder str, int startIndex, int length) {
        return this.append(str, startIndex, length).appendNewLine();
    }

    public StrBuilder appendln(char[] chars) {
        return this.append(chars).appendNewLine();
    }

    public StrBuilder appendln(char[] chars, int startIndex, int length) {
        return this.append(chars, startIndex, length).appendNewLine();
    }

    public StrBuilder appendln(boolean value) {
        return this.append(value).appendNewLine();
    }

    public StrBuilder appendln(char ch) {
        return this.append(ch).appendNewLine();
    }

    public StrBuilder appendln(int value) {
        return this.append(value).appendNewLine();
    }

    public StrBuilder appendln(long value) {
        return this.append(value).appendNewLine();
    }

    public StrBuilder appendln(float value) {
        return this.append(value).appendNewLine();
    }

    public StrBuilder appendln(double value) {
        return this.append(value).appendNewLine();
    }

    public <T> StrBuilder appendAll(T ... array) {
        if (array != null && array.length > 0) {
            for (T element : array) {
                this.append(element);
            }
        }
        return this;
    }

    public StrBuilder appendAll(Iterable<?> iterable) {
        if (iterable != null) {
            for (Object o2 : iterable) {
                this.append(o2);
            }
        }
        return this;
    }

    public StrBuilder appendAll(Iterator<?> it2) {
        if (it2 != null) {
            while (it2.hasNext()) {
                this.append(it2.next());
            }
        }
        return this;
    }

    public StrBuilder appendWithSeparators(Object[] array, String separator) {
        if (array != null && array.length > 0) {
            String sep = ObjectUtils.toString(separator);
            this.append(array[0]);
            for (int i2 = 1; i2 < array.length; ++i2) {
                this.append(sep);
                this.append(array[i2]);
            }
        }
        return this;
    }

    public StrBuilder appendWithSeparators(Iterable<?> iterable, String separator) {
        if (iterable != null) {
            String sep = ObjectUtils.toString(separator);
            Iterator<?> it2 = iterable.iterator();
            while (it2.hasNext()) {
                this.append(it2.next());
                if (!it2.hasNext()) continue;
                this.append(sep);
            }
        }
        return this;
    }

    public StrBuilder appendWithSeparators(Iterator<?> it2, String separator) {
        if (it2 != null) {
            String sep = ObjectUtils.toString(separator);
            while (it2.hasNext()) {
                this.append(it2.next());
                if (!it2.hasNext()) continue;
                this.append(sep);
            }
        }
        return this;
    }

    public StrBuilder appendSeparator(String separator) {
        return this.appendSeparator(separator, null);
    }

    public StrBuilder appendSeparator(String standard, String defaultIfEmpty) {
        String str;
        String string = str = this.isEmpty() ? defaultIfEmpty : standard;
        if (str != null) {
            this.append(str);
        }
        return this;
    }

    public StrBuilder appendSeparator(char separator) {
        if (this.size() > 0) {
            this.append(separator);
        }
        return this;
    }

    public StrBuilder appendSeparator(char standard, char defaultIfEmpty) {
        if (this.size() > 0) {
            this.append(standard);
        } else {
            this.append(defaultIfEmpty);
        }
        return this;
    }

    public StrBuilder appendSeparator(String separator, int loopIndex) {
        if (separator != null && loopIndex > 0) {
            this.append(separator);
        }
        return this;
    }

    public StrBuilder appendSeparator(char separator, int loopIndex) {
        if (loopIndex > 0) {
            this.append(separator);
        }
        return this;
    }

    public StrBuilder appendPadding(int length, char padChar) {
        if (length >= 0) {
            this.ensureCapacity(this.size + length);
            for (int i2 = 0; i2 < length; ++i2) {
                this.buffer[this.size++] = padChar;
            }
        }
        return this;
    }

    public StrBuilder appendFixedWidthPadLeft(Object obj, int width, char padChar) {
        if (width > 0) {
            int strLen;
            String str;
            this.ensureCapacity(this.size + width);
            String string = str = obj == null ? this.getNullText() : obj.toString();
            if (str == null) {
                str = "";
            }
            if ((strLen = str.length()) >= width) {
                str.getChars(strLen - width, strLen, this.buffer, this.size);
            } else {
                int padLen = width - strLen;
                for (int i2 = 0; i2 < padLen; ++i2) {
                    this.buffer[this.size + i2] = padChar;
                }
                str.getChars(0, strLen, this.buffer, this.size + padLen);
            }
            this.size += width;
        }
        return this;
    }

    public StrBuilder appendFixedWidthPadLeft(int value, int width, char padChar) {
        return this.appendFixedWidthPadLeft(String.valueOf(value), width, padChar);
    }

    public StrBuilder appendFixedWidthPadRight(Object obj, int width, char padChar) {
        if (width > 0) {
            int strLen;
            String str;
            this.ensureCapacity(this.size + width);
            String string = str = obj == null ? this.getNullText() : obj.toString();
            if (str == null) {
                str = "";
            }
            if ((strLen = str.length()) >= width) {
                str.getChars(0, width, this.buffer, this.size);
            } else {
                int padLen = width - strLen;
                str.getChars(0, strLen, this.buffer, this.size);
                for (int i2 = 0; i2 < padLen; ++i2) {
                    this.buffer[this.size + strLen + i2] = padChar;
                }
            }
            this.size += width;
        }
        return this;
    }

    public StrBuilder appendFixedWidthPadRight(int value, int width, char padChar) {
        return this.appendFixedWidthPadRight(String.valueOf(value), width, padChar);
    }

    public StrBuilder insert(int index, Object obj) {
        if (obj == null) {
            return this.insert(index, this.nullText);
        }
        return this.insert(index, obj.toString());
    }

    public StrBuilder insert(int index, String str) {
        int strLen;
        this.validateIndex(index);
        if (str == null) {
            str = this.nullText;
        }
        if (str != null && (strLen = str.length()) > 0) {
            int newSize = this.size + strLen;
            this.ensureCapacity(newSize);
            System.arraycopy(this.buffer, index, this.buffer, index + strLen, this.size - index);
            this.size = newSize;
            str.getChars(0, strLen, this.buffer, index);
        }
        return this;
    }

    public StrBuilder insert(int index, char[] chars) {
        this.validateIndex(index);
        if (chars == null) {
            return this.insert(index, this.nullText);
        }
        int len = chars.length;
        if (len > 0) {
            this.ensureCapacity(this.size + len);
            System.arraycopy(this.buffer, index, this.buffer, index + len, this.size - index);
            System.arraycopy(chars, 0, this.buffer, index, len);
            this.size += len;
        }
        return this;
    }

    public StrBuilder insert(int index, char[] chars, int offset, int length) {
        this.validateIndex(index);
        if (chars == null) {
            return this.insert(index, this.nullText);
        }
        if (offset < 0 || offset > chars.length) {
            throw new StringIndexOutOfBoundsException("Invalid offset: " + offset);
        }
        if (length < 0 || offset + length > chars.length) {
            throw new StringIndexOutOfBoundsException("Invalid length: " + length);
        }
        if (length > 0) {
            this.ensureCapacity(this.size + length);
            System.arraycopy(this.buffer, index, this.buffer, index + length, this.size - index);
            System.arraycopy(chars, offset, this.buffer, index, length);
            this.size += length;
        }
        return this;
    }

    public StrBuilder insert(int index, boolean value) {
        this.validateIndex(index);
        if (value) {
            this.ensureCapacity(this.size + 4);
            System.arraycopy(this.buffer, index, this.buffer, index + 4, this.size - index);
            this.buffer[index++] = 116;
            this.buffer[index++] = 114;
            this.buffer[index++] = 117;
            this.buffer[index] = 101;
            this.size += 4;
        } else {
            this.ensureCapacity(this.size + 5);
            System.arraycopy(this.buffer, index, this.buffer, index + 5, this.size - index);
            this.buffer[index++] = 102;
            this.buffer[index++] = 97;
            this.buffer[index++] = 108;
            this.buffer[index++] = 115;
            this.buffer[index] = 101;
            this.size += 5;
        }
        return this;
    }

    public StrBuilder insert(int index, char value) {
        this.validateIndex(index);
        this.ensureCapacity(this.size + 1);
        System.arraycopy(this.buffer, index, this.buffer, index + 1, this.size - index);
        this.buffer[index] = value;
        ++this.size;
        return this;
    }

    public StrBuilder insert(int index, int value) {
        return this.insert(index, String.valueOf(value));
    }

    public StrBuilder insert(int index, long value) {
        return this.insert(index, String.valueOf(value));
    }

    public StrBuilder insert(int index, float value) {
        return this.insert(index, String.valueOf(value));
    }

    public StrBuilder insert(int index, double value) {
        return this.insert(index, String.valueOf(value));
    }

    private void deleteImpl(int startIndex, int endIndex, int len) {
        System.arraycopy(this.buffer, endIndex, this.buffer, startIndex, this.size - endIndex);
        this.size -= len;
    }

    public StrBuilder delete(int startIndex, int endIndex) {
        int len = (endIndex = this.validateRange(startIndex, endIndex)) - startIndex;
        if (len > 0) {
            this.deleteImpl(startIndex, endIndex, len);
        }
        return this;
    }

    public StrBuilder deleteAll(char ch) {
        for (int i2 = 0; i2 < this.size; ++i2) {
            if (this.buffer[i2] != ch) continue;
            int start = i2;
            while (++i2 < this.size && this.buffer[i2] == ch) {
            }
            int len = i2 - start;
            this.deleteImpl(start, i2, len);
            i2 -= len;
        }
        return this;
    }

    public StrBuilder deleteFirst(char ch) {
        for (int i2 = 0; i2 < this.size; ++i2) {
            if (this.buffer[i2] != ch) continue;
            this.deleteImpl(i2, i2 + 1, 1);
            break;
        }
        return this;
    }

    public StrBuilder deleteAll(String str) {
        int len;
        int n2 = len = str == null ? 0 : str.length();
        if (len > 0) {
            int index = this.indexOf(str, 0);
            while (index >= 0) {
                this.deleteImpl(index, index + len, len);
                index = this.indexOf(str, index);
            }
        }
        return this;
    }

    public StrBuilder deleteFirst(String str) {
        int index;
        int len;
        int n2 = len = str == null ? 0 : str.length();
        if (len > 0 && (index = this.indexOf(str, 0)) >= 0) {
            this.deleteImpl(index, index + len, len);
        }
        return this;
    }

    public StrBuilder deleteAll(StrMatcher matcher) {
        return this.replace(matcher, null, 0, this.size, -1);
    }

    public StrBuilder deleteFirst(StrMatcher matcher) {
        return this.replace(matcher, null, 0, this.size, 1);
    }

    private void replaceImpl(int startIndex, int endIndex, int removeLen, String insertStr, int insertLen) {
        int newSize = this.size - removeLen + insertLen;
        if (insertLen != removeLen) {
            this.ensureCapacity(newSize);
            System.arraycopy(this.buffer, endIndex, this.buffer, startIndex + insertLen, this.size - endIndex);
            this.size = newSize;
        }
        if (insertLen > 0) {
            insertStr.getChars(0, insertLen, this.buffer, startIndex);
        }
    }

    public StrBuilder replace(int startIndex, int endIndex, String replaceStr) {
        endIndex = this.validateRange(startIndex, endIndex);
        int insertLen = replaceStr == null ? 0 : replaceStr.length();
        this.replaceImpl(startIndex, endIndex, endIndex - startIndex, replaceStr, insertLen);
        return this;
    }

    public StrBuilder replaceAll(char search, char replace) {
        if (search != replace) {
            for (int i2 = 0; i2 < this.size; ++i2) {
                if (this.buffer[i2] != search) continue;
                this.buffer[i2] = replace;
            }
        }
        return this;
    }

    public StrBuilder replaceFirst(char search, char replace) {
        if (search != replace) {
            for (int i2 = 0; i2 < this.size; ++i2) {
                if (this.buffer[i2] != search) continue;
                this.buffer[i2] = replace;
                break;
            }
        }
        return this;
    }

    public StrBuilder replaceAll(String searchStr, String replaceStr) {
        int searchLen;
        int n2 = searchLen = searchStr == null ? 0 : searchStr.length();
        if (searchLen > 0) {
            int replaceLen = replaceStr == null ? 0 : replaceStr.length();
            int index = this.indexOf(searchStr, 0);
            while (index >= 0) {
                this.replaceImpl(index, index + searchLen, searchLen, replaceStr, replaceLen);
                index = this.indexOf(searchStr, index + replaceLen);
            }
        }
        return this;
    }

    public StrBuilder replaceFirst(String searchStr, String replaceStr) {
        int index;
        int searchLen;
        int n2 = searchLen = searchStr == null ? 0 : searchStr.length();
        if (searchLen > 0 && (index = this.indexOf(searchStr, 0)) >= 0) {
            int replaceLen = replaceStr == null ? 0 : replaceStr.length();
            this.replaceImpl(index, index + searchLen, searchLen, replaceStr, replaceLen);
        }
        return this;
    }

    public StrBuilder replaceAll(StrMatcher matcher, String replaceStr) {
        return this.replace(matcher, replaceStr, 0, this.size, -1);
    }

    public StrBuilder replaceFirst(StrMatcher matcher, String replaceStr) {
        return this.replace(matcher, replaceStr, 0, this.size, 1);
    }

    public StrBuilder replace(StrMatcher matcher, String replaceStr, int startIndex, int endIndex, int replaceCount) {
        endIndex = this.validateRange(startIndex, endIndex);
        return this.replaceImpl(matcher, replaceStr, startIndex, endIndex, replaceCount);
    }

    private StrBuilder replaceImpl(StrMatcher matcher, String replaceStr, int from, int to2, int replaceCount) {
        if (matcher == null || this.size == 0) {
            return this;
        }
        int replaceLen = replaceStr == null ? 0 : replaceStr.length();
        char[] buf = this.buffer;
        for (int i2 = from; i2 < to2 && replaceCount != 0; ++i2) {
            int removeLen = matcher.isMatch(buf, i2, from, to2);
            if (removeLen <= 0) continue;
            this.replaceImpl(i2, i2 + removeLen, removeLen, replaceStr, replaceLen);
            to2 = to2 - removeLen + replaceLen;
            i2 = i2 + replaceLen - 1;
            if (replaceCount <= 0) continue;
            --replaceCount;
        }
        return this;
    }

    public StrBuilder reverse() {
        if (this.size == 0) {
            return this;
        }
        int half = this.size / 2;
        char[] buf = this.buffer;
        int leftIdx = 0;
        int rightIdx = this.size - 1;
        while (leftIdx < half) {
            char swap = buf[leftIdx];
            buf[leftIdx] = buf[rightIdx];
            buf[rightIdx] = swap;
            ++leftIdx;
            --rightIdx;
        }
        return this;
    }

    public StrBuilder trim() {
        int pos;
        if (this.size == 0) {
            return this;
        }
        int len = this.size;
        char[] buf = this.buffer;
        for (pos = 0; pos < len && buf[pos] <= ' '; ++pos) {
        }
        while (pos < len && buf[len - 1] <= ' ') {
            --len;
        }
        if (len < this.size) {
            this.delete(len, this.size);
        }
        if (pos > 0) {
            this.delete(0, pos);
        }
        return this;
    }

    public boolean startsWith(String str) {
        if (str == null) {
            return false;
        }
        int len = str.length();
        if (len == 0) {
            return true;
        }
        if (len > this.size) {
            return false;
        }
        for (int i2 = 0; i2 < len; ++i2) {
            if (this.buffer[i2] == str.charAt(i2)) continue;
            return false;
        }
        return true;
    }

    public boolean endsWith(String str) {
        if (str == null) {
            return false;
        }
        int len = str.length();
        if (len == 0) {
            return true;
        }
        if (len > this.size) {
            return false;
        }
        int pos = this.size - len;
        int i2 = 0;
        while (i2 < len) {
            if (this.buffer[pos] != str.charAt(i2)) {
                return false;
            }
            ++i2;
            ++pos;
        }
        return true;
    }

    @Override
    public CharSequence subSequence(int startIndex, int endIndex) {
        if (startIndex < 0) {
            throw new StringIndexOutOfBoundsException(startIndex);
        }
        if (endIndex > this.size) {
            throw new StringIndexOutOfBoundsException(endIndex);
        }
        if (startIndex > endIndex) {
            throw new StringIndexOutOfBoundsException(endIndex - startIndex);
        }
        return this.substring(startIndex, endIndex);
    }

    public String substring(int start) {
        return this.substring(start, this.size);
    }

    public String substring(int startIndex, int endIndex) {
        endIndex = this.validateRange(startIndex, endIndex);
        return new String(this.buffer, startIndex, endIndex - startIndex);
    }

    public String leftString(int length) {
        if (length <= 0) {
            return "";
        }
        if (length >= this.size) {
            return new String(this.buffer, 0, this.size);
        }
        return new String(this.buffer, 0, length);
    }

    public String rightString(int length) {
        if (length <= 0) {
            return "";
        }
        if (length >= this.size) {
            return new String(this.buffer, 0, this.size);
        }
        return new String(this.buffer, this.size - length, length);
    }

    public String midString(int index, int length) {
        if (index < 0) {
            index = 0;
        }
        if (length <= 0 || index >= this.size) {
            return "";
        }
        if (this.size <= index + length) {
            return new String(this.buffer, index, this.size - index);
        }
        return new String(this.buffer, index, length);
    }

    public boolean contains(char ch) {
        char[] thisBuf = this.buffer;
        for (int i2 = 0; i2 < this.size; ++i2) {
            if (thisBuf[i2] != ch) continue;
            return true;
        }
        return false;
    }

    public boolean contains(String str) {
        return this.indexOf(str, 0) >= 0;
    }

    public boolean contains(StrMatcher matcher) {
        return this.indexOf(matcher, 0) >= 0;
    }

    public int indexOf(char ch) {
        return this.indexOf(ch, 0);
    }

    public int indexOf(char ch, int startIndex) {
        int n2 = startIndex = startIndex < 0 ? 0 : startIndex;
        if (startIndex >= this.size) {
            return -1;
        }
        char[] thisBuf = this.buffer;
        for (int i2 = startIndex; i2 < this.size; ++i2) {
            if (thisBuf[i2] != ch) continue;
            return i2;
        }
        return -1;
    }

    public int indexOf(String str) {
        return this.indexOf(str, 0);
    }

    public int indexOf(String str, int startIndex) {
        int n2 = startIndex = startIndex < 0 ? 0 : startIndex;
        if (str == null || startIndex >= this.size) {
            return -1;
        }
        int strLen = str.length();
        if (strLen == 1) {
            return this.indexOf(str.charAt(0), startIndex);
        }
        if (strLen == 0) {
            return startIndex;
        }
        if (strLen > this.size) {
            return -1;
        }
        char[] thisBuf = this.buffer;
        int len = this.size - strLen + 1;
        block0: for (int i2 = startIndex; i2 < len; ++i2) {
            for (int j2 = 0; j2 < strLen; ++j2) {
                if (str.charAt(j2) != thisBuf[i2 + j2]) continue block0;
            }
            return i2;
        }
        return -1;
    }

    public int indexOf(StrMatcher matcher) {
        return this.indexOf(matcher, 0);
    }

    public int indexOf(StrMatcher matcher, int startIndex) {
        int n2 = startIndex = startIndex < 0 ? 0 : startIndex;
        if (matcher == null || startIndex >= this.size) {
            return -1;
        }
        int len = this.size;
        char[] buf = this.buffer;
        for (int i2 = startIndex; i2 < len; ++i2) {
            if (matcher.isMatch(buf, i2, startIndex, len) <= 0) continue;
            return i2;
        }
        return -1;
    }

    public int lastIndexOf(char ch) {
        return this.lastIndexOf(ch, this.size - 1);
    }

    public int lastIndexOf(char ch, int startIndex) {
        int n2 = startIndex = startIndex >= this.size ? this.size - 1 : startIndex;
        if (startIndex < 0) {
            return -1;
        }
        for (int i2 = startIndex; i2 >= 0; --i2) {
            if (this.buffer[i2] != ch) continue;
            return i2;
        }
        return -1;
    }

    public int lastIndexOf(String str) {
        return this.lastIndexOf(str, this.size - 1);
    }

    public int lastIndexOf(String str, int startIndex) {
        int n2 = startIndex = startIndex >= this.size ? this.size - 1 : startIndex;
        if (str == null || startIndex < 0) {
            return -1;
        }
        int strLen = str.length();
        if (strLen > 0 && strLen <= this.size) {
            if (strLen == 1) {
                return this.lastIndexOf(str.charAt(0), startIndex);
            }
            block0: for (int i2 = startIndex - strLen + 1; i2 >= 0; --i2) {
                for (int j2 = 0; j2 < strLen; ++j2) {
                    if (str.charAt(j2) != this.buffer[i2 + j2]) continue block0;
                }
                return i2;
            }
        } else if (strLen == 0) {
            return startIndex;
        }
        return -1;
    }

    public int lastIndexOf(StrMatcher matcher) {
        return this.lastIndexOf(matcher, this.size);
    }

    public int lastIndexOf(StrMatcher matcher, int startIndex) {
        int n2 = startIndex = startIndex >= this.size ? this.size - 1 : startIndex;
        if (matcher == null || startIndex < 0) {
            return -1;
        }
        char[] buf = this.buffer;
        int endIndex = startIndex + 1;
        for (int i2 = startIndex; i2 >= 0; --i2) {
            if (matcher.isMatch(buf, i2, 0, endIndex) <= 0) continue;
            return i2;
        }
        return -1;
    }

    public StrTokenizer asTokenizer() {
        return new StrBuilderTokenizer();
    }

    public Reader asReader() {
        return new StrBuilderReader();
    }

    public Writer asWriter() {
        return new StrBuilderWriter();
    }

    public boolean equalsIgnoreCase(StrBuilder other) {
        if (this == other) {
            return true;
        }
        if (this.size != other.size) {
            return false;
        }
        char[] thisBuf = this.buffer;
        char[] otherBuf = other.buffer;
        for (int i2 = this.size - 1; i2 >= 0; --i2) {
            char c1 = thisBuf[i2];
            char c2 = otherBuf[i2];
            if (c1 == c2 || Character.toUpperCase(c1) == Character.toUpperCase(c2)) continue;
            return false;
        }
        return true;
    }

    public boolean equals(StrBuilder other) {
        if (this == other) {
            return true;
        }
        if (this.size != other.size) {
            return false;
        }
        char[] thisBuf = this.buffer;
        char[] otherBuf = other.buffer;
        for (int i2 = this.size - 1; i2 >= 0; --i2) {
            if (thisBuf[i2] == otherBuf[i2]) continue;
            return false;
        }
        return true;
    }

    public boolean equals(Object obj) {
        if (obj instanceof StrBuilder) {
            return this.equals((StrBuilder)obj);
        }
        return false;
    }

    public int hashCode() {
        char[] buf = this.buffer;
        int hash = 0;
        for (int i2 = this.size - 1; i2 >= 0; --i2) {
            hash = 31 * hash + buf[i2];
        }
        return hash;
    }

    @Override
    public String toString() {
        return new String(this.buffer, 0, this.size);
    }

    public StringBuffer toStringBuffer() {
        return new StringBuffer(this.size).append(this.buffer, 0, this.size);
    }

    public StringBuilder toStringBuilder() {
        return new StringBuilder(this.size).append(this.buffer, 0, this.size);
    }

    @Override
    public String build() {
        return this.toString();
    }

    protected int validateRange(int startIndex, int endIndex) {
        if (startIndex < 0) {
            throw new StringIndexOutOfBoundsException(startIndex);
        }
        if (endIndex > this.size) {
            endIndex = this.size;
        }
        if (startIndex > endIndex) {
            throw new StringIndexOutOfBoundsException("end < start");
        }
        return endIndex;
    }

    protected void validateIndex(int index) {
        if (index < 0 || index > this.size) {
            throw new StringIndexOutOfBoundsException(index);
        }
    }

    class StrBuilderWriter
    extends Writer {
        StrBuilderWriter() {
        }

        @Override
        public void close() {
        }

        @Override
        public void flush() {
        }

        @Override
        public void write(int c2) {
            StrBuilder.this.append((char)c2);
        }

        @Override
        public void write(char[] cbuf) {
            StrBuilder.this.append(cbuf);
        }

        @Override
        public void write(char[] cbuf, int off, int len) {
            StrBuilder.this.append(cbuf, off, len);
        }

        @Override
        public void write(String str) {
            StrBuilder.this.append(str);
        }

        @Override
        public void write(String str, int off, int len) {
            StrBuilder.this.append(str, off, len);
        }
    }

    class StrBuilderReader
    extends Reader {
        private int pos;
        private int mark;

        StrBuilderReader() {
        }

        @Override
        public void close() {
        }

        @Override
        public int read() {
            if (!this.ready()) {
                return -1;
            }
            return StrBuilder.this.charAt(this.pos++);
        }

        @Override
        public int read(char[] b2, int off, int len) {
            if (off < 0 || len < 0 || off > b2.length || off + len > b2.length || off + len < 0) {
                throw new IndexOutOfBoundsException();
            }
            if (len == 0) {
                return 0;
            }
            if (this.pos >= StrBuilder.this.size()) {
                return -1;
            }
            if (this.pos + len > StrBuilder.this.size()) {
                len = StrBuilder.this.size() - this.pos;
            }
            StrBuilder.this.getChars(this.pos, this.pos + len, b2, off);
            this.pos += len;
            return len;
        }

        @Override
        public long skip(long n2) {
            if ((long)this.pos + n2 > (long)StrBuilder.this.size()) {
                n2 = StrBuilder.this.size() - this.pos;
            }
            if (n2 < 0L) {
                return 0L;
            }
            this.pos = (int)((long)this.pos + n2);
            return n2;
        }

        @Override
        public boolean ready() {
            return this.pos < StrBuilder.this.size();
        }

        @Override
        public boolean markSupported() {
            return true;
        }

        @Override
        public void mark(int readAheadLimit) {
            this.mark = this.pos;
        }

        @Override
        public void reset() {
            this.pos = this.mark;
        }
    }

    class StrBuilderTokenizer
    extends StrTokenizer {
        StrBuilderTokenizer() {
        }

        @Override
        protected List<String> tokenize(char[] chars, int offset, int count) {
            if (chars == null) {
                return super.tokenize(StrBuilder.this.buffer, 0, StrBuilder.this.size());
            }
            return super.tokenize(chars, offset, count);
        }

        @Override
        public String getContent() {
            String str = super.getContent();
            if (str == null) {
                return StrBuilder.this.toString();
            }
            return str;
        }
    }
}

