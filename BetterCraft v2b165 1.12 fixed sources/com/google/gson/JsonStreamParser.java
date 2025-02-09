// 
// Decompiled by Procyon v0.6.0
// 

package com.google.gson;

import java.io.IOException;
import com.google.gson.stream.MalformedJsonException;
import com.google.gson.stream.JsonToken;
import java.io.EOFException;
import com.google.gson.internal.Streams;
import java.util.NoSuchElementException;
import java.io.Reader;
import java.io.StringReader;
import com.google.gson.stream.JsonReader;
import java.util.Iterator;

public final class JsonStreamParser implements Iterator<JsonElement>
{
    private final JsonReader parser;
    private final Object lock;
    
    public JsonStreamParser(final String json) {
        this(new StringReader(json));
    }
    
    public JsonStreamParser(final Reader reader) {
        (this.parser = new JsonReader(reader)).setLenient(true);
        this.lock = new Object();
    }
    
    @Override
    public JsonElement next() throws JsonParseException {
        if (!this.hasNext()) {
            throw new NoSuchElementException();
        }
        try {
            return Streams.parse(this.parser);
        }
        catch (final StackOverflowError e) {
            throw new JsonParseException("Failed parsing JSON source to Json", e);
        }
        catch (final OutOfMemoryError e2) {
            throw new JsonParseException("Failed parsing JSON source to Json", e2);
        }
        catch (final JsonParseException e3) {
            throw (e3.getCause() instanceof EOFException) ? new NoSuchElementException() : e3;
        }
    }
    
    @Override
    public boolean hasNext() {
        synchronized (this.lock) {
            try {
                return this.parser.peek() != JsonToken.END_DOCUMENT;
            }
            catch (final MalformedJsonException e) {
                throw new JsonSyntaxException(e);
            }
            catch (final IOException e2) {
                throw new JsonIOException(e2);
            }
        }
    }
    
    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
