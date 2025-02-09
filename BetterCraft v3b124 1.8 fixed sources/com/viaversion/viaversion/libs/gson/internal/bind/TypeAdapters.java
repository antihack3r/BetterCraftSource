/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.libs.gson.internal.bind;

import com.viaversion.viaversion.libs.gson.Gson;
import com.viaversion.viaversion.libs.gson.JsonArray;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.libs.gson.JsonIOException;
import com.viaversion.viaversion.libs.gson.JsonNull;
import com.viaversion.viaversion.libs.gson.JsonObject;
import com.viaversion.viaversion.libs.gson.JsonPrimitive;
import com.viaversion.viaversion.libs.gson.JsonSyntaxException;
import com.viaversion.viaversion.libs.gson.TypeAdapter;
import com.viaversion.viaversion.libs.gson.TypeAdapterFactory;
import com.viaversion.viaversion.libs.gson.annotations.SerializedName;
import com.viaversion.viaversion.libs.gson.internal.LazilyParsedNumber;
import com.viaversion.viaversion.libs.gson.internal.bind.JsonTreeReader;
import com.viaversion.viaversion.libs.gson.reflect.TypeToken;
import com.viaversion.viaversion.libs.gson.stream.JsonReader;
import com.viaversion.viaversion.libs.gson.stream.JsonToken;
import com.viaversion.viaversion.libs.gson.stream.JsonWriter;
import java.io.IOException;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Calendar;
import java.util.Currency;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;

public final class TypeAdapters {
    public static final TypeAdapter<Class> CLASS = new TypeAdapter<Class>(){

        @Override
        public void write(JsonWriter out, Class value) throws IOException {
            throw new UnsupportedOperationException("Attempted to serialize java.lang.Class: " + value.getName() + ". Forgot to register a type adapter?");
        }

        @Override
        public Class read(JsonReader in2) throws IOException {
            throw new UnsupportedOperationException("Attempted to deserialize a java.lang.Class. Forgot to register a type adapter?");
        }
    }.nullSafe();
    public static final TypeAdapterFactory CLASS_FACTORY = TypeAdapters.newFactory(Class.class, CLASS);
    public static final TypeAdapter<BitSet> BIT_SET = new TypeAdapter<BitSet>(){

        @Override
        public BitSet read(JsonReader in2) throws IOException {
            BitSet bitset = new BitSet();
            in2.beginArray();
            int i2 = 0;
            JsonToken tokenType = in2.peek();
            while (tokenType != JsonToken.END_ARRAY) {
                boolean set;
                switch (tokenType) {
                    case NUMBER: 
                    case STRING: {
                        int intValue = in2.nextInt();
                        if (intValue == 0) {
                            set = false;
                            break;
                        }
                        if (intValue == 1) {
                            set = true;
                            break;
                        }
                        throw new JsonSyntaxException("Invalid bitset value " + intValue + ", expected 0 or 1; at path " + in2.getPreviousPath());
                    }
                    case BOOLEAN: {
                        set = in2.nextBoolean();
                        break;
                    }
                    default: {
                        throw new JsonSyntaxException("Invalid bitset value type: " + (Object)((Object)tokenType) + "; at path " + in2.getPath());
                    }
                }
                if (set) {
                    bitset.set(i2);
                }
                ++i2;
                tokenType = in2.peek();
            }
            in2.endArray();
            return bitset;
        }

        @Override
        public void write(JsonWriter out, BitSet src) throws IOException {
            out.beginArray();
            int length = src.length();
            for (int i2 = 0; i2 < length; ++i2) {
                int value = src.get(i2) ? 1 : 0;
                out.value(value);
            }
            out.endArray();
        }
    }.nullSafe();
    public static final TypeAdapterFactory BIT_SET_FACTORY = TypeAdapters.newFactory(BitSet.class, BIT_SET);
    public static final TypeAdapter<Boolean> BOOLEAN = new TypeAdapter<Boolean>(){

        @Override
        public Boolean read(JsonReader in2) throws IOException {
            JsonToken peek = in2.peek();
            if (peek == JsonToken.NULL) {
                in2.nextNull();
                return null;
            }
            if (peek == JsonToken.STRING) {
                return Boolean.parseBoolean(in2.nextString());
            }
            return in2.nextBoolean();
        }

        @Override
        public void write(JsonWriter out, Boolean value) throws IOException {
            out.value(value);
        }
    };
    public static final TypeAdapter<Boolean> BOOLEAN_AS_STRING = new TypeAdapter<Boolean>(){

        @Override
        public Boolean read(JsonReader in2) throws IOException {
            if (in2.peek() == JsonToken.NULL) {
                in2.nextNull();
                return null;
            }
            return Boolean.valueOf(in2.nextString());
        }

        @Override
        public void write(JsonWriter out, Boolean value) throws IOException {
            out.value(value == null ? "null" : value.toString());
        }
    };
    public static final TypeAdapterFactory BOOLEAN_FACTORY = TypeAdapters.newFactory(Boolean.TYPE, Boolean.class, BOOLEAN);
    public static final TypeAdapter<Number> BYTE = new TypeAdapter<Number>(){

        @Override
        public Number read(JsonReader in2) throws IOException {
            int intValue;
            if (in2.peek() == JsonToken.NULL) {
                in2.nextNull();
                return null;
            }
            try {
                intValue = in2.nextInt();
            }
            catch (NumberFormatException e2) {
                throw new JsonSyntaxException(e2);
            }
            if (intValue > 255 || intValue < -128) {
                throw new JsonSyntaxException("Lossy conversion from " + intValue + " to byte; at path " + in2.getPreviousPath());
            }
            return (byte)intValue;
        }

        @Override
        public void write(JsonWriter out, Number value) throws IOException {
            if (value == null) {
                out.nullValue();
            } else {
                out.value(value.byteValue());
            }
        }
    };
    public static final TypeAdapterFactory BYTE_FACTORY = TypeAdapters.newFactory(Byte.TYPE, Byte.class, BYTE);
    public static final TypeAdapter<Number> SHORT = new TypeAdapter<Number>(){

        @Override
        public Number read(JsonReader in2) throws IOException {
            int intValue;
            if (in2.peek() == JsonToken.NULL) {
                in2.nextNull();
                return null;
            }
            try {
                intValue = in2.nextInt();
            }
            catch (NumberFormatException e2) {
                throw new JsonSyntaxException(e2);
            }
            if (intValue > 65535 || intValue < Short.MIN_VALUE) {
                throw new JsonSyntaxException("Lossy conversion from " + intValue + " to short; at path " + in2.getPreviousPath());
            }
            return (short)intValue;
        }

        @Override
        public void write(JsonWriter out, Number value) throws IOException {
            if (value == null) {
                out.nullValue();
            } else {
                out.value(value.shortValue());
            }
        }
    };
    public static final TypeAdapterFactory SHORT_FACTORY = TypeAdapters.newFactory(Short.TYPE, Short.class, SHORT);
    public static final TypeAdapter<Number> INTEGER = new TypeAdapter<Number>(){

        @Override
        public Number read(JsonReader in2) throws IOException {
            if (in2.peek() == JsonToken.NULL) {
                in2.nextNull();
                return null;
            }
            try {
                return in2.nextInt();
            }
            catch (NumberFormatException e2) {
                throw new JsonSyntaxException(e2);
            }
        }

        @Override
        public void write(JsonWriter out, Number value) throws IOException {
            if (value == null) {
                out.nullValue();
            } else {
                out.value(value.intValue());
            }
        }
    };
    public static final TypeAdapterFactory INTEGER_FACTORY = TypeAdapters.newFactory(Integer.TYPE, Integer.class, INTEGER);
    public static final TypeAdapter<AtomicInteger> ATOMIC_INTEGER = new TypeAdapter<AtomicInteger>(){

        @Override
        public AtomicInteger read(JsonReader in2) throws IOException {
            try {
                return new AtomicInteger(in2.nextInt());
            }
            catch (NumberFormatException e2) {
                throw new JsonSyntaxException(e2);
            }
        }

        @Override
        public void write(JsonWriter out, AtomicInteger value) throws IOException {
            out.value(value.get());
        }
    }.nullSafe();
    public static final TypeAdapterFactory ATOMIC_INTEGER_FACTORY = TypeAdapters.newFactory(AtomicInteger.class, ATOMIC_INTEGER);
    public static final TypeAdapter<AtomicBoolean> ATOMIC_BOOLEAN = new TypeAdapter<AtomicBoolean>(){

        @Override
        public AtomicBoolean read(JsonReader in2) throws IOException {
            return new AtomicBoolean(in2.nextBoolean());
        }

        @Override
        public void write(JsonWriter out, AtomicBoolean value) throws IOException {
            out.value(value.get());
        }
    }.nullSafe();
    public static final TypeAdapterFactory ATOMIC_BOOLEAN_FACTORY = TypeAdapters.newFactory(AtomicBoolean.class, ATOMIC_BOOLEAN);
    public static final TypeAdapter<AtomicIntegerArray> ATOMIC_INTEGER_ARRAY = new TypeAdapter<AtomicIntegerArray>(){

        @Override
        public AtomicIntegerArray read(JsonReader in2) throws IOException {
            ArrayList<Integer> list = new ArrayList<Integer>();
            in2.beginArray();
            while (in2.hasNext()) {
                try {
                    int integer = in2.nextInt();
                    list.add(integer);
                }
                catch (NumberFormatException e2) {
                    throw new JsonSyntaxException(e2);
                }
            }
            in2.endArray();
            int length = list.size();
            AtomicIntegerArray array = new AtomicIntegerArray(length);
            for (int i2 = 0; i2 < length; ++i2) {
                array.set(i2, (Integer)list.get(i2));
            }
            return array;
        }

        @Override
        public void write(JsonWriter out, AtomicIntegerArray value) throws IOException {
            out.beginArray();
            int length = value.length();
            for (int i2 = 0; i2 < length; ++i2) {
                out.value(value.get(i2));
            }
            out.endArray();
        }
    }.nullSafe();
    public static final TypeAdapterFactory ATOMIC_INTEGER_ARRAY_FACTORY = TypeAdapters.newFactory(AtomicIntegerArray.class, ATOMIC_INTEGER_ARRAY);
    public static final TypeAdapter<Number> LONG = new TypeAdapter<Number>(){

        @Override
        public Number read(JsonReader in2) throws IOException {
            if (in2.peek() == JsonToken.NULL) {
                in2.nextNull();
                return null;
            }
            try {
                return in2.nextLong();
            }
            catch (NumberFormatException e2) {
                throw new JsonSyntaxException(e2);
            }
        }

        @Override
        public void write(JsonWriter out, Number value) throws IOException {
            if (value == null) {
                out.nullValue();
            } else {
                out.value(value.longValue());
            }
        }
    };
    public static final TypeAdapter<Number> FLOAT = new TypeAdapter<Number>(){

        @Override
        public Number read(JsonReader in2) throws IOException {
            if (in2.peek() == JsonToken.NULL) {
                in2.nextNull();
                return null;
            }
            return Float.valueOf((float)in2.nextDouble());
        }

        @Override
        public void write(JsonWriter out, Number value) throws IOException {
            if (value == null) {
                out.nullValue();
            } else {
                Number floatNumber = value instanceof Float ? (Number)value : (Number)Float.valueOf(value.floatValue());
                out.value(floatNumber);
            }
        }
    };
    public static final TypeAdapter<Number> DOUBLE = new TypeAdapter<Number>(){

        @Override
        public Number read(JsonReader in2) throws IOException {
            if (in2.peek() == JsonToken.NULL) {
                in2.nextNull();
                return null;
            }
            return in2.nextDouble();
        }

        @Override
        public void write(JsonWriter out, Number value) throws IOException {
            if (value == null) {
                out.nullValue();
            } else {
                out.value(value.doubleValue());
            }
        }
    };
    public static final TypeAdapter<Character> CHARACTER = new TypeAdapter<Character>(){

        @Override
        public Character read(JsonReader in2) throws IOException {
            if (in2.peek() == JsonToken.NULL) {
                in2.nextNull();
                return null;
            }
            String str = in2.nextString();
            if (str.length() != 1) {
                throw new JsonSyntaxException("Expecting character, got: " + str + "; at " + in2.getPreviousPath());
            }
            return Character.valueOf(str.charAt(0));
        }

        @Override
        public void write(JsonWriter out, Character value) throws IOException {
            out.value(value == null ? null : String.valueOf(value));
        }
    };
    public static final TypeAdapterFactory CHARACTER_FACTORY = TypeAdapters.newFactory(Character.TYPE, Character.class, CHARACTER);
    public static final TypeAdapter<String> STRING = new TypeAdapter<String>(){

        @Override
        public String read(JsonReader in2) throws IOException {
            JsonToken peek = in2.peek();
            if (peek == JsonToken.NULL) {
                in2.nextNull();
                return null;
            }
            if (peek == JsonToken.BOOLEAN) {
                return Boolean.toString(in2.nextBoolean());
            }
            return in2.nextString();
        }

        @Override
        public void write(JsonWriter out, String value) throws IOException {
            out.value(value);
        }
    };
    public static final TypeAdapter<BigDecimal> BIG_DECIMAL = new TypeAdapter<BigDecimal>(){

        @Override
        public BigDecimal read(JsonReader in2) throws IOException {
            if (in2.peek() == JsonToken.NULL) {
                in2.nextNull();
                return null;
            }
            String s2 = in2.nextString();
            try {
                return new BigDecimal(s2);
            }
            catch (NumberFormatException e2) {
                throw new JsonSyntaxException("Failed parsing '" + s2 + "' as BigDecimal; at path " + in2.getPreviousPath(), e2);
            }
        }

        @Override
        public void write(JsonWriter out, BigDecimal value) throws IOException {
            out.value(value);
        }
    };
    public static final TypeAdapter<BigInteger> BIG_INTEGER = new TypeAdapter<BigInteger>(){

        @Override
        public BigInteger read(JsonReader in2) throws IOException {
            if (in2.peek() == JsonToken.NULL) {
                in2.nextNull();
                return null;
            }
            String s2 = in2.nextString();
            try {
                return new BigInteger(s2);
            }
            catch (NumberFormatException e2) {
                throw new JsonSyntaxException("Failed parsing '" + s2 + "' as BigInteger; at path " + in2.getPreviousPath(), e2);
            }
        }

        @Override
        public void write(JsonWriter out, BigInteger value) throws IOException {
            out.value(value);
        }
    };
    public static final TypeAdapter<LazilyParsedNumber> LAZILY_PARSED_NUMBER = new TypeAdapter<LazilyParsedNumber>(){

        @Override
        public LazilyParsedNumber read(JsonReader in2) throws IOException {
            if (in2.peek() == JsonToken.NULL) {
                in2.nextNull();
                return null;
            }
            return new LazilyParsedNumber(in2.nextString());
        }

        @Override
        public void write(JsonWriter out, LazilyParsedNumber value) throws IOException {
            out.value(value);
        }
    };
    public static final TypeAdapterFactory STRING_FACTORY = TypeAdapters.newFactory(String.class, STRING);
    public static final TypeAdapter<StringBuilder> STRING_BUILDER = new TypeAdapter<StringBuilder>(){

        @Override
        public StringBuilder read(JsonReader in2) throws IOException {
            if (in2.peek() == JsonToken.NULL) {
                in2.nextNull();
                return null;
            }
            return new StringBuilder(in2.nextString());
        }

        @Override
        public void write(JsonWriter out, StringBuilder value) throws IOException {
            out.value(value == null ? null : value.toString());
        }
    };
    public static final TypeAdapterFactory STRING_BUILDER_FACTORY = TypeAdapters.newFactory(StringBuilder.class, STRING_BUILDER);
    public static final TypeAdapter<StringBuffer> STRING_BUFFER = new TypeAdapter<StringBuffer>(){

        @Override
        public StringBuffer read(JsonReader in2) throws IOException {
            if (in2.peek() == JsonToken.NULL) {
                in2.nextNull();
                return null;
            }
            return new StringBuffer(in2.nextString());
        }

        @Override
        public void write(JsonWriter out, StringBuffer value) throws IOException {
            out.value(value == null ? null : value.toString());
        }
    };
    public static final TypeAdapterFactory STRING_BUFFER_FACTORY = TypeAdapters.newFactory(StringBuffer.class, STRING_BUFFER);
    public static final TypeAdapter<URL> URL = new TypeAdapter<URL>(){

        @Override
        public URL read(JsonReader in2) throws IOException {
            if (in2.peek() == JsonToken.NULL) {
                in2.nextNull();
                return null;
            }
            String nextString = in2.nextString();
            return "null".equals(nextString) ? null : new URL(nextString);
        }

        @Override
        public void write(JsonWriter out, URL value) throws IOException {
            out.value(value == null ? null : value.toExternalForm());
        }
    };
    public static final TypeAdapterFactory URL_FACTORY = TypeAdapters.newFactory(URL.class, URL);
    public static final TypeAdapter<URI> URI = new TypeAdapter<URI>(){

        @Override
        public URI read(JsonReader in2) throws IOException {
            if (in2.peek() == JsonToken.NULL) {
                in2.nextNull();
                return null;
            }
            try {
                String nextString = in2.nextString();
                return "null".equals(nextString) ? null : new URI(nextString);
            }
            catch (URISyntaxException e2) {
                throw new JsonIOException(e2);
            }
        }

        @Override
        public void write(JsonWriter out, URI value) throws IOException {
            out.value(value == null ? null : value.toASCIIString());
        }
    };
    public static final TypeAdapterFactory URI_FACTORY = TypeAdapters.newFactory(URI.class, URI);
    public static final TypeAdapter<InetAddress> INET_ADDRESS = new TypeAdapter<InetAddress>(){

        @Override
        public InetAddress read(JsonReader in2) throws IOException {
            if (in2.peek() == JsonToken.NULL) {
                in2.nextNull();
                return null;
            }
            return InetAddress.getByName(in2.nextString());
        }

        @Override
        public void write(JsonWriter out, InetAddress value) throws IOException {
            out.value(value == null ? null : value.getHostAddress());
        }
    };
    public static final TypeAdapterFactory INET_ADDRESS_FACTORY = TypeAdapters.newTypeHierarchyFactory(InetAddress.class, INET_ADDRESS);
    public static final TypeAdapter<UUID> UUID = new TypeAdapter<UUID>(){

        @Override
        public UUID read(JsonReader in2) throws IOException {
            if (in2.peek() == JsonToken.NULL) {
                in2.nextNull();
                return null;
            }
            String s2 = in2.nextString();
            try {
                return java.util.UUID.fromString(s2);
            }
            catch (IllegalArgumentException e2) {
                throw new JsonSyntaxException("Failed parsing '" + s2 + "' as UUID; at path " + in2.getPreviousPath(), e2);
            }
        }

        @Override
        public void write(JsonWriter out, UUID value) throws IOException {
            out.value(value == null ? null : value.toString());
        }
    };
    public static final TypeAdapterFactory UUID_FACTORY = TypeAdapters.newFactory(UUID.class, UUID);
    public static final TypeAdapter<Currency> CURRENCY = new TypeAdapter<Currency>(){

        @Override
        public Currency read(JsonReader in2) throws IOException {
            String s2 = in2.nextString();
            try {
                return Currency.getInstance(s2);
            }
            catch (IllegalArgumentException e2) {
                throw new JsonSyntaxException("Failed parsing '" + s2 + "' as Currency; at path " + in2.getPreviousPath(), e2);
            }
        }

        @Override
        public void write(JsonWriter out, Currency value) throws IOException {
            out.value(value.getCurrencyCode());
        }
    }.nullSafe();
    public static final TypeAdapterFactory CURRENCY_FACTORY = TypeAdapters.newFactory(Currency.class, CURRENCY);
    public static final TypeAdapter<Calendar> CALENDAR = new TypeAdapter<Calendar>(){
        private static final String YEAR = "year";
        private static final String MONTH = "month";
        private static final String DAY_OF_MONTH = "dayOfMonth";
        private static final String HOUR_OF_DAY = "hourOfDay";
        private static final String MINUTE = "minute";
        private static final String SECOND = "second";

        @Override
        public Calendar read(JsonReader in2) throws IOException {
            if (in2.peek() == JsonToken.NULL) {
                in2.nextNull();
                return null;
            }
            in2.beginObject();
            int year = 0;
            int month = 0;
            int dayOfMonth = 0;
            int hourOfDay = 0;
            int minute = 0;
            int second = 0;
            while (in2.peek() != JsonToken.END_OBJECT) {
                String name = in2.nextName();
                int value = in2.nextInt();
                if (YEAR.equals(name)) {
                    year = value;
                    continue;
                }
                if (MONTH.equals(name)) {
                    month = value;
                    continue;
                }
                if (DAY_OF_MONTH.equals(name)) {
                    dayOfMonth = value;
                    continue;
                }
                if (HOUR_OF_DAY.equals(name)) {
                    hourOfDay = value;
                    continue;
                }
                if (MINUTE.equals(name)) {
                    minute = value;
                    continue;
                }
                if (!SECOND.equals(name)) continue;
                second = value;
            }
            in2.endObject();
            return new GregorianCalendar(year, month, dayOfMonth, hourOfDay, minute, second);
        }

        @Override
        public void write(JsonWriter out, Calendar value) throws IOException {
            if (value == null) {
                out.nullValue();
                return;
            }
            out.beginObject();
            out.name(YEAR);
            out.value(value.get(1));
            out.name(MONTH);
            out.value(value.get(2));
            out.name(DAY_OF_MONTH);
            out.value(value.get(5));
            out.name(HOUR_OF_DAY);
            out.value(value.get(11));
            out.name(MINUTE);
            out.value(value.get(12));
            out.name(SECOND);
            out.value(value.get(13));
            out.endObject();
        }
    };
    public static final TypeAdapterFactory CALENDAR_FACTORY = TypeAdapters.newFactoryForMultipleTypes(Calendar.class, GregorianCalendar.class, CALENDAR);
    public static final TypeAdapter<Locale> LOCALE = new TypeAdapter<Locale>(){

        @Override
        public Locale read(JsonReader in2) throws IOException {
            if (in2.peek() == JsonToken.NULL) {
                in2.nextNull();
                return null;
            }
            String locale = in2.nextString();
            StringTokenizer tokenizer = new StringTokenizer(locale, "_");
            String language = null;
            String country = null;
            String variant = null;
            if (tokenizer.hasMoreElements()) {
                language = tokenizer.nextToken();
            }
            if (tokenizer.hasMoreElements()) {
                country = tokenizer.nextToken();
            }
            if (tokenizer.hasMoreElements()) {
                variant = tokenizer.nextToken();
            }
            if (country == null && variant == null) {
                return new Locale(language);
            }
            if (variant == null) {
                return new Locale(language, country);
            }
            return new Locale(language, country, variant);
        }

        @Override
        public void write(JsonWriter out, Locale value) throws IOException {
            out.value(value == null ? null : value.toString());
        }
    };
    public static final TypeAdapterFactory LOCALE_FACTORY = TypeAdapters.newFactory(Locale.class, LOCALE);
    public static final TypeAdapter<JsonElement> JSON_ELEMENT = new TypeAdapter<JsonElement>(){

        private JsonElement tryBeginNesting(JsonReader in2, JsonToken peeked) throws IOException {
            switch (peeked) {
                case BEGIN_ARRAY: {
                    in2.beginArray();
                    return new JsonArray();
                }
                case BEGIN_OBJECT: {
                    in2.beginObject();
                    return new JsonObject();
                }
            }
            return null;
        }

        private JsonElement readTerminal(JsonReader in2, JsonToken peeked) throws IOException {
            switch (peeked) {
                case STRING: {
                    return new JsonPrimitive(in2.nextString());
                }
                case NUMBER: {
                    String number = in2.nextString();
                    return new JsonPrimitive(new LazilyParsedNumber(number));
                }
                case BOOLEAN: {
                    return new JsonPrimitive(in2.nextBoolean());
                }
                case NULL: {
                    in2.nextNull();
                    return JsonNull.INSTANCE;
                }
            }
            throw new IllegalStateException("Unexpected token: " + (Object)((Object)peeked));
        }

        @Override
        public JsonElement read(JsonReader in2) throws IOException {
            if (in2 instanceof JsonTreeReader) {
                return ((JsonTreeReader)in2).nextJsonElement();
            }
            JsonToken peeked = in2.peek();
            JsonElement current = this.tryBeginNesting(in2, peeked);
            if (current == null) {
                return this.readTerminal(in2, peeked);
            }
            ArrayDeque<JsonElement> stack = new ArrayDeque<JsonElement>();
            while (true) {
                if (in2.hasNext()) {
                    JsonElement value;
                    boolean isNesting;
                    String name = null;
                    if (current instanceof JsonObject) {
                        name = in2.nextName();
                    }
                    boolean bl2 = isNesting = (value = this.tryBeginNesting(in2, peeked = in2.peek())) != null;
                    if (value == null) {
                        value = this.readTerminal(in2, peeked);
                    }
                    if (current instanceof JsonArray) {
                        ((JsonArray)current).add(value);
                    } else {
                        ((JsonObject)current).add(name, value);
                    }
                    if (!isNesting) continue;
                    stack.addLast(current);
                    current = value;
                    continue;
                }
                if (current instanceof JsonArray) {
                    in2.endArray();
                } else {
                    in2.endObject();
                }
                if (stack.isEmpty()) {
                    return current;
                }
                current = (JsonElement)stack.removeLast();
            }
        }

        @Override
        public void write(JsonWriter out, JsonElement value) throws IOException {
            if (value == null || value.isJsonNull()) {
                out.nullValue();
            } else if (value.isJsonPrimitive()) {
                JsonPrimitive primitive = value.getAsJsonPrimitive();
                if (primitive.isNumber()) {
                    out.value(primitive.getAsNumber());
                } else if (primitive.isBoolean()) {
                    out.value(primitive.getAsBoolean());
                } else {
                    out.value(primitive.getAsString());
                }
            } else if (value.isJsonArray()) {
                out.beginArray();
                for (JsonElement e2 : value.getAsJsonArray()) {
                    this.write(out, e2);
                }
                out.endArray();
            } else if (value.isJsonObject()) {
                out.beginObject();
                for (Map.Entry<String, JsonElement> e3 : value.getAsJsonObject().entrySet()) {
                    out.name(e3.getKey());
                    this.write(out, e3.getValue());
                }
                out.endObject();
            } else {
                throw new IllegalArgumentException("Couldn't write " + value.getClass());
            }
        }
    };
    public static final TypeAdapterFactory JSON_ELEMENT_FACTORY = TypeAdapters.newTypeHierarchyFactory(JsonElement.class, JSON_ELEMENT);
    public static final TypeAdapterFactory ENUM_FACTORY = new TypeAdapterFactory(){

        @Override
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
            Class<T> rawType = typeToken.getRawType();
            if (!Enum.class.isAssignableFrom(rawType) || rawType == Enum.class) {
                return null;
            }
            if (!rawType.isEnum()) {
                rawType = rawType.getSuperclass();
            }
            EnumTypeAdapter<T> adapter = new EnumTypeAdapter<T>(rawType);
            return adapter;
        }
    };

    private TypeAdapters() {
        throw new UnsupportedOperationException();
    }

    public static <TT> TypeAdapterFactory newFactory(final TypeToken<TT> type, final TypeAdapter<TT> typeAdapter) {
        return new TypeAdapterFactory(){

            @Override
            public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
                return typeToken.equals(type) ? typeAdapter : null;
            }
        };
    }

    public static <TT> TypeAdapterFactory newFactory(final Class<TT> type, final TypeAdapter<TT> typeAdapter) {
        return new TypeAdapterFactory(){

            @Override
            public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
                return typeToken.getRawType() == type ? typeAdapter : null;
            }

            public String toString() {
                return "Factory[type=" + type.getName() + ",adapter=" + typeAdapter + "]";
            }
        };
    }

    public static <TT> TypeAdapterFactory newFactory(final Class<TT> unboxed, final Class<TT> boxed, final TypeAdapter<? super TT> typeAdapter) {
        return new TypeAdapterFactory(){

            @Override
            public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
                Class<T> rawType = typeToken.getRawType();
                return rawType == unboxed || rawType == boxed ? typeAdapter : null;
            }

            public String toString() {
                return "Factory[type=" + boxed.getName() + "+" + unboxed.getName() + ",adapter=" + typeAdapter + "]";
            }
        };
    }

    public static <TT> TypeAdapterFactory newFactoryForMultipleTypes(final Class<TT> base, final Class<? extends TT> sub, final TypeAdapter<? super TT> typeAdapter) {
        return new TypeAdapterFactory(){

            @Override
            public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
                Class<T> rawType = typeToken.getRawType();
                return rawType == base || rawType == sub ? typeAdapter : null;
            }

            public String toString() {
                return "Factory[type=" + base.getName() + "+" + sub.getName() + ",adapter=" + typeAdapter + "]";
            }
        };
    }

    public static <T1> TypeAdapterFactory newTypeHierarchyFactory(final Class<T1> clazz, final TypeAdapter<T1> typeAdapter) {
        return new TypeAdapterFactory(){

            public <T2> TypeAdapter<T2> create(Gson gson, TypeToken<T2> typeToken) {
                final Class<T2> requestedType = typeToken.getRawType();
                if (!clazz.isAssignableFrom(requestedType)) {
                    return null;
                }
                return new TypeAdapter<T1>(){

                    @Override
                    public void write(JsonWriter out, T1 value) throws IOException {
                        typeAdapter.write(out, value);
                    }

                    @Override
                    public T1 read(JsonReader in2) throws IOException {
                        Object result = typeAdapter.read(in2);
                        if (result != null && !requestedType.isInstance(result)) {
                            throw new JsonSyntaxException("Expected a " + requestedType.getName() + " but was " + result.getClass().getName() + "; at path " + in2.getPreviousPath());
                        }
                        return result;
                    }
                };
            }

            public String toString() {
                return "Factory[typeHierarchy=" + clazz.getName() + ",adapter=" + typeAdapter + "]";
            }
        };
    }

    private static final class EnumTypeAdapter<T extends Enum<T>>
    extends TypeAdapter<T> {
        private final Map<String, T> nameToConstant = new HashMap<String, T>();
        private final Map<String, T> stringToConstant = new HashMap<String, T>();
        private final Map<T, String> constantToName = new HashMap<T, String>();

        public EnumTypeAdapter(final Class<T> classOfT) {
            try {
                Field[] constantFields;
                for (Field constantField : constantFields = AccessController.doPrivileged(new PrivilegedAction<Field[]>(){

                    @Override
                    public Field[] run() {
                        Field[] fields = classOfT.getDeclaredFields();
                        ArrayList<Field> constantFieldsList = new ArrayList<Field>(fields.length);
                        for (Field f2 : fields) {
                            if (!f2.isEnumConstant()) continue;
                            constantFieldsList.add(f2);
                        }
                        AccessibleObject[] constantFields = constantFieldsList.toArray(new Field[0]);
                        AccessibleObject.setAccessible(constantFields, true);
                        return constantFields;
                    }
                })) {
                    Enum constant = (Enum)constantField.get(null);
                    String name = constant.name();
                    String toStringVal = constant.toString();
                    SerializedName annotation = constantField.getAnnotation(SerializedName.class);
                    if (annotation != null) {
                        name = annotation.value();
                        for (String alternate : annotation.alternate()) {
                            this.nameToConstant.put(alternate, constant);
                        }
                    }
                    this.nameToConstant.put(name, constant);
                    this.stringToConstant.put(toStringVal, constant);
                    this.constantToName.put(constant, name);
                }
            }
            catch (IllegalAccessException e2) {
                throw new AssertionError((Object)e2);
            }
        }

        @Override
        public T read(JsonReader in2) throws IOException {
            if (in2.peek() == JsonToken.NULL) {
                in2.nextNull();
                return null;
            }
            String key = in2.nextString();
            Enum constant = (Enum)this.nameToConstant.get(key);
            return (T)(constant == null ? (Enum)this.stringToConstant.get(key) : constant);
        }

        @Override
        public void write(JsonWriter out, T value) throws IOException {
            out.value(value == null ? null : this.constantToName.get(value));
        }
    }
}

