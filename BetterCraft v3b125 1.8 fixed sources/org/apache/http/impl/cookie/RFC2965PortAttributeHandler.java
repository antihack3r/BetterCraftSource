/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.impl.cookie;

import java.util.StringTokenizer;
import org.apache.http.annotation.Immutable;
import org.apache.http.cookie.ClientCookie;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieAttributeHandler;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.CookieRestrictionViolationException;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.cookie.SetCookie;
import org.apache.http.cookie.SetCookie2;
import org.apache.http.util.Args;

@Immutable
public class RFC2965PortAttributeHandler
implements CookieAttributeHandler {
    private static int[] parsePortAttribute(String portValue) throws MalformedCookieException {
        StringTokenizer st2 = new StringTokenizer(portValue, ",");
        int[] ports = new int[st2.countTokens()];
        try {
            int i2 = 0;
            while (st2.hasMoreTokens()) {
                ports[i2] = Integer.parseInt(st2.nextToken().trim());
                if (ports[i2] < 0) {
                    throw new MalformedCookieException("Invalid Port attribute.");
                }
                ++i2;
            }
        }
        catch (NumberFormatException e2) {
            throw new MalformedCookieException("Invalid Port attribute: " + e2.getMessage());
        }
        return ports;
    }

    private static boolean portMatch(int port, int[] ports) {
        boolean portInList = false;
        for (int port2 : ports) {
            if (port != port2) continue;
            portInList = true;
            break;
        }
        return portInList;
    }

    public void parse(SetCookie cookie, String portValue) throws MalformedCookieException {
        Args.notNull(cookie, "Cookie");
        if (cookie instanceof SetCookie2) {
            SetCookie2 cookie2 = (SetCookie2)cookie;
            if (portValue != null && portValue.trim().length() > 0) {
                int[] ports = RFC2965PortAttributeHandler.parsePortAttribute(portValue);
                cookie2.setPorts(ports);
            }
        }
    }

    public void validate(Cookie cookie, CookieOrigin origin) throws MalformedCookieException {
        Args.notNull(cookie, "Cookie");
        Args.notNull(origin, "Cookie origin");
        int port = origin.getPort();
        if (cookie instanceof ClientCookie && ((ClientCookie)cookie).containsAttribute("port") && !RFC2965PortAttributeHandler.portMatch(port, cookie.getPorts())) {
            throw new CookieRestrictionViolationException("Port attribute violates RFC 2965: Request port not found in cookie's port list.");
        }
    }

    public boolean match(Cookie cookie, CookieOrigin origin) {
        Args.notNull(cookie, "Cookie");
        Args.notNull(origin, "Cookie origin");
        int port = origin.getPort();
        if (cookie instanceof ClientCookie && ((ClientCookie)cookie).containsAttribute("port")) {
            if (cookie.getPorts() == null) {
                return false;
            }
            if (!RFC2965PortAttributeHandler.portMatch(port, cookie.getPorts())) {
                return false;
            }
        }
        return true;
    }
}
