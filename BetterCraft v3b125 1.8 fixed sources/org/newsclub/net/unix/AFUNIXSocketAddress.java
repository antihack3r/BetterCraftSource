/*
 * Decompiled with CFR 0.152.
 */
package org.newsclub.net.unix;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.util.Locale;
import org.newsclub.net.unix.NativeUnixSocket;

public final class AFUNIXSocketAddress
extends InetSocketAddress {
    private static final long serialVersionUID = 1L;
    private final byte[] bytes;

    public AFUNIXSocketAddress(File socketFile) throws IOException {
        this(socketFile, 0);
    }

    public AFUNIXSocketAddress(File socketFile, int port) throws IOException {
        this(socketFile.getCanonicalPath().getBytes(Charset.defaultCharset()), port);
    }

    public AFUNIXSocketAddress(byte[] socketAddress) throws IOException {
        this(socketAddress, 0);
    }

    public AFUNIXSocketAddress(byte[] socketAddress, int port) throws IOException {
        super(InetAddress.getLoopbackAddress(), 0);
        if (port != 0) {
            NativeUnixSocket.setPort1(this, port);
        }
        if (socketAddress.length == 0) {
            throw new SocketException("Illegal address length: " + socketAddress.length);
        }
        this.bytes = (byte[])socketAddress.clone();
    }

    public static AFUNIXSocketAddress inAbstractNamespace(String name) throws IOException {
        return AFUNIXSocketAddress.inAbstractNamespace(name, 0);
    }

    public static AFUNIXSocketAddress inAbstractNamespace(String name, int port) throws IOException {
        byte[] bytes = name.getBytes(Charset.defaultCharset());
        byte[] addr = new byte[bytes.length + 1];
        System.arraycopy(bytes, 0, addr, 1, bytes.length);
        return new AFUNIXSocketAddress(addr, port);
    }

    byte[] getBytes() {
        return this.bytes;
    }

    private static String prettyPrint(byte[] data) {
        int dataLength = data.length;
        StringBuilder sb2 = new StringBuilder(dataLength + 16);
        for (int i2 = 0; i2 < dataLength; ++i2) {
            byte c2 = data[i2];
            if (c2 >= 32 && c2 < 127) {
                sb2.append((char)c2);
                continue;
            }
            sb2.append("\\x");
            sb2.append(String.format(Locale.ENGLISH, "%02x", c2));
        }
        return sb2.toString();
    }

    @Override
    public String toString() {
        return this.getClass().getName() + "[port=" + this.getPort() + ";path=" + AFUNIXSocketAddress.prettyPrint(this.bytes) + "]";
    }

    public String getPath() {
        byte[] by = this.getPathAsBytes();
        for (int i2 = 1; i2 < by.length; ++i2) {
            byte b2 = by[i2];
            if (b2 == 0) {
                by[i2] = 64;
                continue;
            }
            if (b2 >= 32 && b2 != 127) continue;
            by[i2] = 46;
        }
        return new String(by, Charset.defaultCharset());
    }

    public byte[] getPathAsBytes() {
        return (byte[])this.bytes.clone();
    }
}

