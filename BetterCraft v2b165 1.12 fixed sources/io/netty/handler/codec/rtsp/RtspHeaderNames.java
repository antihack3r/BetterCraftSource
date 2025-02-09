// 
// Decompiled by Procyon v0.6.0
// 

package io.netty.handler.codec.rtsp;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.util.AsciiString;

public final class RtspHeaderNames
{
    public static final AsciiString ACCEPT;
    public static final AsciiString ACCEPT_ENCODING;
    public static final AsciiString ACCEPT_LANGUAGE;
    public static final AsciiString ALLOW;
    public static final AsciiString AUTHORIZATION;
    public static final AsciiString BANDWIDTH;
    public static final AsciiString BLOCKSIZE;
    public static final AsciiString CACHE_CONTROL;
    public static final AsciiString CONFERENCE;
    public static final AsciiString CONNECTION;
    public static final AsciiString CONTENT_BASE;
    public static final AsciiString CONTENT_ENCODING;
    public static final AsciiString CONTENT_LANGUAGE;
    public static final AsciiString CONTENT_LENGTH;
    public static final AsciiString CONTENT_LOCATION;
    public static final AsciiString CONTENT_TYPE;
    public static final AsciiString CSEQ;
    public static final AsciiString DATE;
    public static final AsciiString EXPIRES;
    public static final AsciiString FROM;
    public static final AsciiString HOST;
    public static final AsciiString IF_MATCH;
    public static final AsciiString IF_MODIFIED_SINCE;
    public static final AsciiString KEYMGMT;
    public static final AsciiString LAST_MODIFIED;
    public static final AsciiString PROXY_AUTHENTICATE;
    public static final AsciiString PROXY_REQUIRE;
    public static final AsciiString PUBLIC;
    public static final AsciiString RANGE;
    public static final AsciiString REFERER;
    public static final AsciiString REQUIRE;
    public static final AsciiString RETRT_AFTER;
    public static final AsciiString RTP_INFO;
    public static final AsciiString SCALE;
    public static final AsciiString SESSION;
    public static final AsciiString SERVER;
    public static final AsciiString SPEED;
    public static final AsciiString TIMESTAMP;
    public static final AsciiString TRANSPORT;
    public static final AsciiString UNSUPPORTED;
    public static final AsciiString USER_AGENT;
    public static final AsciiString VARY;
    public static final AsciiString VIA;
    public static final AsciiString WWW_AUTHENTICATE;
    
    private RtspHeaderNames() {
    }
    
    static {
        ACCEPT = HttpHeaderNames.ACCEPT;
        ACCEPT_ENCODING = HttpHeaderNames.ACCEPT_ENCODING;
        ACCEPT_LANGUAGE = HttpHeaderNames.ACCEPT_LANGUAGE;
        ALLOW = new AsciiString("allow");
        AUTHORIZATION = HttpHeaderNames.AUTHORIZATION;
        BANDWIDTH = new AsciiString("bandwidth");
        BLOCKSIZE = new AsciiString("blocksize");
        CACHE_CONTROL = HttpHeaderNames.CACHE_CONTROL;
        CONFERENCE = new AsciiString("conference");
        CONNECTION = HttpHeaderNames.CONNECTION;
        CONTENT_BASE = HttpHeaderNames.CONTENT_BASE;
        CONTENT_ENCODING = HttpHeaderNames.CONTENT_ENCODING;
        CONTENT_LANGUAGE = HttpHeaderNames.CONTENT_LANGUAGE;
        CONTENT_LENGTH = HttpHeaderNames.CONTENT_LENGTH;
        CONTENT_LOCATION = HttpHeaderNames.CONTENT_LOCATION;
        CONTENT_TYPE = HttpHeaderNames.CONTENT_TYPE;
        CSEQ = new AsciiString("cseq");
        DATE = HttpHeaderNames.DATE;
        EXPIRES = HttpHeaderNames.EXPIRES;
        FROM = HttpHeaderNames.FROM;
        HOST = HttpHeaderNames.HOST;
        IF_MATCH = HttpHeaderNames.IF_MATCH;
        IF_MODIFIED_SINCE = HttpHeaderNames.IF_MODIFIED_SINCE;
        KEYMGMT = new AsciiString("keymgmt");
        LAST_MODIFIED = HttpHeaderNames.LAST_MODIFIED;
        PROXY_AUTHENTICATE = HttpHeaderNames.PROXY_AUTHENTICATE;
        PROXY_REQUIRE = new AsciiString("proxy-require");
        PUBLIC = new AsciiString("public");
        RANGE = HttpHeaderNames.RANGE;
        REFERER = HttpHeaderNames.REFERER;
        REQUIRE = new AsciiString("require");
        RETRT_AFTER = HttpHeaderNames.RETRY_AFTER;
        RTP_INFO = new AsciiString("rtp-info");
        SCALE = new AsciiString("scale");
        SESSION = new AsciiString("session");
        SERVER = HttpHeaderNames.SERVER;
        SPEED = new AsciiString("speed");
        TIMESTAMP = new AsciiString("timestamp");
        TRANSPORT = new AsciiString("transport");
        UNSUPPORTED = new AsciiString("unsupported");
        USER_AGENT = HttpHeaderNames.USER_AGENT;
        VARY = HttpHeaderNames.VARY;
        VIA = HttpHeaderNames.VIA;
        WWW_AUTHENTICATE = HttpHeaderNames.WWW_AUTHENTICATE;
    }
}
