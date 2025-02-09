/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.libs.gson.internal.sql;

import com.viaversion.viaversion.libs.gson.Gson;
import com.viaversion.viaversion.libs.gson.JsonSyntaxException;
import com.viaversion.viaversion.libs.gson.TypeAdapter;
import com.viaversion.viaversion.libs.gson.TypeAdapterFactory;
import com.viaversion.viaversion.libs.gson.reflect.TypeToken;
import com.viaversion.viaversion.libs.gson.stream.JsonReader;
import com.viaversion.viaversion.libs.gson.stream.JsonToken;
import com.viaversion.viaversion.libs.gson.stream.JsonWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

final class SqlDateTypeAdapter
extends TypeAdapter<java.sql.Date> {
    static final TypeAdapterFactory FACTORY = new TypeAdapterFactory(){

        @Override
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
            return typeToken.getRawType() == java.sql.Date.class ? new SqlDateTypeAdapter() : null;
        }
    };
    private final DateFormat format = new SimpleDateFormat("MMM d, yyyy");

    private SqlDateTypeAdapter() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public java.sql.Date read(JsonReader in2) throws IOException {
        if (in2.peek() == JsonToken.NULL) {
            in2.nextNull();
            return null;
        }
        String s2 = in2.nextString();
        try {
            Date utilDate;
            SqlDateTypeAdapter sqlDateTypeAdapter = this;
            synchronized (sqlDateTypeAdapter) {
                utilDate = this.format.parse(s2);
            }
            return new java.sql.Date(utilDate.getTime());
        }
        catch (ParseException e2) {
            throw new JsonSyntaxException("Failed parsing '" + s2 + "' as SQL Date; at path " + in2.getPreviousPath(), e2);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void write(JsonWriter out, java.sql.Date value) throws IOException {
        String dateString;
        if (value == null) {
            out.nullValue();
            return;
        }
        SqlDateTypeAdapter sqlDateTypeAdapter = this;
        synchronized (sqlDateTypeAdapter) {
            dateString = this.format.format(value);
        }
        out.value(dateString);
    }
}

