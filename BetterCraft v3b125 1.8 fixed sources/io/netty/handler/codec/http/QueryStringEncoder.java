/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.http;

import io.netty.handler.codec.http.HttpConstants;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.List;

public class QueryStringEncoder {
    private final Charset charset;
    private final String uri;
    private final List<Param> params = new ArrayList<Param>();

    public QueryStringEncoder(String uri) {
        this(uri, HttpConstants.DEFAULT_CHARSET);
    }

    public QueryStringEncoder(String uri, Charset charset) {
        if (uri == null) {
            throw new NullPointerException("getUri");
        }
        if (charset == null) {
            throw new NullPointerException("charset");
        }
        this.uri = uri;
        this.charset = charset;
    }

    public void addParam(String name, String value) {
        if (name == null) {
            throw new NullPointerException("name");
        }
        this.params.add(new Param(name, value));
    }

    public URI toUri() throws URISyntaxException {
        return new URI(this.toString());
    }

    public String toString() {
        if (this.params.isEmpty()) {
            return this.uri;
        }
        StringBuilder sb2 = new StringBuilder(this.uri).append('?');
        for (int i2 = 0; i2 < this.params.size(); ++i2) {
            Param param = this.params.get(i2);
            sb2.append(QueryStringEncoder.encodeComponent(param.name, this.charset));
            if (param.value != null) {
                sb2.append('=');
                sb2.append(QueryStringEncoder.encodeComponent(param.value, this.charset));
            }
            if (i2 == this.params.size() - 1) continue;
            sb2.append('&');
        }
        return sb2.toString();
    }

    private static String encodeComponent(String s2, Charset charset) {
        try {
            return URLEncoder.encode(s2, charset.name()).replace("+", "%20");
        }
        catch (UnsupportedEncodingException ignored) {
            throw new UnsupportedCharsetException(charset.name());
        }
    }

    private static final class Param {
        final String name;
        final String value;

        Param(String name, String value) {
            this.value = value;
            this.name = name;
        }
    }
}

