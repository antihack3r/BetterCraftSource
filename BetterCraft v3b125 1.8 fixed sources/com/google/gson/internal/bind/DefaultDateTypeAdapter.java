/*
 * Decompiled with CFR 0.152.
 */
package com.google.gson.internal.bind;

import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.$Gson$Preconditions;
import com.google.gson.internal.JavaVersion;
import com.google.gson.internal.PreJava9DateFormatProvider;
import com.google.gson.internal.bind.TypeAdapters;
import com.google.gson.internal.bind.util.ISO8601Utils;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public final class DefaultDateTypeAdapter<T extends Date>
extends TypeAdapter<T> {
    private static final String SIMPLE_NAME = "DefaultDateTypeAdapter";
    private final DateType<T> dateType;
    private final List<DateFormat> dateFormats = new ArrayList<DateFormat>();

    private DefaultDateTypeAdapter(DateType<T> dateType, String datePattern) {
        this.dateType = $Gson$Preconditions.checkNotNull(dateType);
        this.dateFormats.add(new SimpleDateFormat(datePattern, Locale.US));
        if (!Locale.getDefault().equals(Locale.US)) {
            this.dateFormats.add(new SimpleDateFormat(datePattern));
        }
    }

    private DefaultDateTypeAdapter(DateType<T> dateType, int style) {
        this.dateType = $Gson$Preconditions.checkNotNull(dateType);
        this.dateFormats.add(DateFormat.getDateInstance(style, Locale.US));
        if (!Locale.getDefault().equals(Locale.US)) {
            this.dateFormats.add(DateFormat.getDateInstance(style));
        }
        if (JavaVersion.isJava9OrLater()) {
            this.dateFormats.add(PreJava9DateFormatProvider.getUSDateFormat(style));
        }
    }

    private DefaultDateTypeAdapter(DateType<T> dateType, int dateStyle, int timeStyle) {
        this.dateType = $Gson$Preconditions.checkNotNull(dateType);
        this.dateFormats.add(DateFormat.getDateTimeInstance(dateStyle, timeStyle, Locale.US));
        if (!Locale.getDefault().equals(Locale.US)) {
            this.dateFormats.add(DateFormat.getDateTimeInstance(dateStyle, timeStyle));
        }
        if (JavaVersion.isJava9OrLater()) {
            this.dateFormats.add(PreJava9DateFormatProvider.getUSDateTimeFormat(dateStyle, timeStyle));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void write(JsonWriter out, Date value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }
        List<DateFormat> list = this.dateFormats;
        synchronized (list) {
            String dateFormatAsString = this.dateFormats.get(0).format(value);
            out.value(dateFormatAsString);
        }
    }

    @Override
    public T read(JsonReader in2) throws IOException {
        if (in2.peek() == JsonToken.NULL) {
            in2.nextNull();
            return null;
        }
        Date date = this.deserializeToDate(in2.nextString());
        return this.dateType.deserialize(date);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Date deserializeToDate(String s2) {
        List<DateFormat> list = this.dateFormats;
        synchronized (list) {
            for (DateFormat dateFormat : this.dateFormats) {
                try {
                    return dateFormat.parse(s2);
                }
                catch (ParseException parseException) {
                }
            }
        }
        try {
            return ISO8601Utils.parse(s2, new ParsePosition(0));
        }
        catch (ParseException e2) {
            throw new JsonSyntaxException(s2, e2);
        }
    }

    public String toString() {
        DateFormat defaultFormat = this.dateFormats.get(0);
        if (defaultFormat instanceof SimpleDateFormat) {
            return "DefaultDateTypeAdapter(" + ((SimpleDateFormat)defaultFormat).toPattern() + ')';
        }
        return "DefaultDateTypeAdapter(" + defaultFormat.getClass().getSimpleName() + ')';
    }

    public static abstract class DateType<T extends Date> {
        public static final DateType<Date> DATE = new DateType<Date>(Date.class){

            @Override
            protected Date deserialize(Date date) {
                return date;
            }
        };
        private final Class<T> dateClass;

        protected DateType(Class<T> dateClass) {
            this.dateClass = dateClass;
        }

        protected abstract T deserialize(Date var1);

        private final TypeAdapterFactory createFactory(DefaultDateTypeAdapter<T> adapter) {
            return TypeAdapters.newFactory(this.dateClass, adapter);
        }

        public final TypeAdapterFactory createAdapterFactory(String datePattern) {
            return this.createFactory(new DefaultDateTypeAdapter(this, datePattern));
        }

        public final TypeAdapterFactory createAdapterFactory(int style) {
            return this.createFactory(new DefaultDateTypeAdapter(this, style));
        }

        public final TypeAdapterFactory createAdapterFactory(int dateStyle, int timeStyle) {
            return this.createFactory(new DefaultDateTypeAdapter(this, dateStyle, timeStyle));
        }

        public final TypeAdapterFactory createDefaultsAdapterFactory() {
            return this.createFactory(new DefaultDateTypeAdapter(this, 2, 2));
        }
    }
}

