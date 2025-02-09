/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.protocol;

import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;

@Deprecated
public final class DefaultedHttpContext
implements HttpContext {
    private final HttpContext local;
    private final HttpContext defaults;

    public DefaultedHttpContext(HttpContext local, HttpContext defaults) {
        this.local = Args.notNull(local, "HTTP context");
        this.defaults = defaults;
    }

    public Object getAttribute(String id2) {
        Object obj = this.local.getAttribute(id2);
        if (obj == null) {
            return this.defaults.getAttribute(id2);
        }
        return obj;
    }

    public Object removeAttribute(String id2) {
        return this.local.removeAttribute(id2);
    }

    public void setAttribute(String id2, Object obj) {
        this.local.setAttribute(id2, obj);
    }

    public HttpContext getDefaults() {
        return this.defaults;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("[local: ").append(this.local);
        buf.append("defaults: ").append(this.defaults);
        buf.append("]");
        return buf.toString();
    }
}

