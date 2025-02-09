/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.libs.gson;

import com.viaversion.viaversion.libs.gson.JsonParseException;
import com.viaversion.viaversion.libs.gson.ToNumberStrategy;
import com.viaversion.viaversion.libs.gson.internal.LazilyParsedNumber;
import com.viaversion.viaversion.libs.gson.stream.JsonReader;
import com.viaversion.viaversion.libs.gson.stream.MalformedJsonException;
import java.io.IOException;
import java.math.BigDecimal;

public enum ToNumberPolicy implements ToNumberStrategy
{
    DOUBLE{

        @Override
        public Double readNumber(JsonReader in2) throws IOException {
            return in2.nextDouble();
        }
    }
    ,
    LAZILY_PARSED_NUMBER{

        @Override
        public Number readNumber(JsonReader in2) throws IOException {
            return new LazilyParsedNumber(in2.nextString());
        }
    }
    ,
    LONG_OR_DOUBLE{

        @Override
        public Number readNumber(JsonReader in2) throws IOException, JsonParseException {
            String value = in2.nextString();
            try {
                return Long.parseLong(value);
            }
            catch (NumberFormatException longE) {
                try {
                    Double d2 = Double.valueOf(value);
                    if ((d2.isInfinite() || d2.isNaN()) && !in2.isLenient()) {
                        throw new MalformedJsonException("JSON forbids NaN and infinities: " + d2 + "; at path " + in2.getPreviousPath());
                    }
                    return d2;
                }
                catch (NumberFormatException doubleE) {
                    throw new JsonParseException("Cannot parse " + value + "; at path " + in2.getPreviousPath(), doubleE);
                }
            }
        }
    }
    ,
    BIG_DECIMAL{

        @Override
        public BigDecimal readNumber(JsonReader in2) throws IOException {
            String value = in2.nextString();
            try {
                return new BigDecimal(value);
            }
            catch (NumberFormatException e2) {
                throw new JsonParseException("Cannot parse " + value + "; at path " + in2.getPreviousPath(), e2);
            }
        }
    };

}

