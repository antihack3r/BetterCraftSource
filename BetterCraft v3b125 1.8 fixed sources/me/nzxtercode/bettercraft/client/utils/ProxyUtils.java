/*
 * Decompiled with CFR 0.152.
 */
package me.nzxtercode.bettercraft.client.utils;

import io.netty.bootstrap.ChannelFactory;
import io.netty.channel.socket.oio.OioSocketChannel;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;

public class ProxyUtils
implements ChannelFactory<OioSocketChannel> {
    public static Proxy proxy;

    public ProxyUtils(Proxy proxy) {
        ProxyUtils.setProxy(proxy);
    }

    public static void setProxy(Proxy proxy) {
        ProxyUtils.proxy = proxy;
    }

    public static Proxy getProxy() {
        return proxy;
    }

    public static Proxy getProxyFromString(String proxy) {
        return new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(proxy.split(":")[0], (int)Integer.valueOf(proxy.split(":")[1])));
    }

    @Override
    public OioSocketChannel newChannel() {
        if (proxy == null || proxy == Proxy.NO_PROXY) {
            return new OioSocketChannel(new Socket(Proxy.NO_PROXY));
        }
        Socket sock = new Socket(proxy);
        try {
            Method m2 = sock.getClass().getDeclaredMethod("getImpl", new Class[0]);
            m2.setAccessible(true);
            Object sd2 = m2.invoke((Object)sock, new Object[0]);
            m2 = sd2.getClass().getDeclaredMethod("setV4", new Class[0]);
            m2.setAccessible(true);
            m2.invoke(sd2, new Object[0]);
            return new OioSocketChannel(sock);
        }
        catch (Exception e2) {
            e2.printStackTrace();
            return null;
        }
    }
}

