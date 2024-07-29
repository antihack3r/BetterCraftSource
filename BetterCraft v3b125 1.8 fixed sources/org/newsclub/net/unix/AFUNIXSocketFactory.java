/*
 * Decompiled with CFR 0.152.
 */
package org.newsclub.net.unix;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URLDecoder;
import java.net.UnknownHostException;
import java.util.Objects;
import javax.net.SocketFactory;
import org.newsclub.net.unix.AFUNIXSocket;
import org.newsclub.net.unix.AFUNIXSocketAddress;

public abstract class AFUNIXSocketFactory
extends SocketFactory {
    protected abstract AFUNIXSocketAddress addressFromHost(String var1, int var2) throws IOException;

    protected boolean isHostnameSupported(String host) {
        return host != null;
    }

    protected boolean isInetAddressSupported(InetAddress address) {
        return address != null && this.isHostnameSupported(address.getHostName());
    }

    @Override
    public Socket createSocket() throws IOException {
        return AFUNIXSocket.newInstance(this);
    }

    @Override
    public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
        if (!this.isHostnameSupported(host)) {
            throw new UnknownHostException();
        }
        if (port < 0) {
            throw new IllegalArgumentException("Illegal port");
        }
        AFUNIXSocketAddress socketAddress = this.addressFromHost(host, port);
        return AFUNIXSocket.connectTo(socketAddress);
    }

    @Override
    public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException, UnknownHostException {
        if (!this.isHostnameSupported(host)) {
            throw new UnknownHostException();
        }
        if (localPort < 0) {
            throw new IllegalArgumentException("Illegal local port");
        }
        return this.createSocket(host, port);
    }

    @Override
    public Socket createSocket(InetAddress address, int port) throws IOException {
        if (!this.isInetAddressSupported(address)) {
            throw new UnknownHostException();
        }
        String hostname = address.getHostName();
        if (!this.isHostnameSupported(hostname)) {
            throw new UnknownHostException();
        }
        return this.createSocket(hostname, port);
    }

    @Override
    public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
        if (!this.isInetAddressSupported(address)) {
            throw new UnknownHostException();
        }
        Objects.requireNonNull(localAddress, "Local address was null");
        if (localPort < 0) {
            throw new IllegalArgumentException("Illegal local port");
        }
        return this.createSocket(address, port);
    }

    public static final class URIScheme
    extends AFUNIXSocketFactory {
        private static final String FILE_SCHEME_PREFIX = "file://";
        private static final String FILE_SCHEME_PREFIX_ENCODED = "file%";
        private static final String FILE_SCHEME_LOCALHOST = "localhost";

        private static String stripBrackets(String host) {
            if (host.startsWith("[")) {
                host = host.endsWith("]") ? host.substring(1, host.length() - 1) : host.substring(1);
            }
            return host;
        }

        @Override
        protected boolean isHostnameSupported(String host) {
            return (host = URIScheme.stripBrackets(host)).startsWith(FILE_SCHEME_PREFIX) || host.startsWith(FILE_SCHEME_PREFIX_ENCODED);
        }

        @Override
        protected AFUNIXSocketAddress addressFromHost(String host, int port) throws IOException {
            if ((host = URIScheme.stripBrackets(host)).startsWith(FILE_SCHEME_PREFIX_ENCODED)) {
                try {
                    host = URLDecoder.decode(host, "UTF-8");
                }
                catch (Exception e2) {
                    throw (UnknownHostException)new UnknownHostException().initCause(e2);
                }
            }
            if (!host.startsWith(FILE_SCHEME_PREFIX)) {
                throw new UnknownHostException();
            }
            String path = host.substring(FILE_SCHEME_PREFIX.length());
            if (path.isEmpty()) {
                throw new UnknownHostException();
            }
            if (path.startsWith(FILE_SCHEME_LOCALHOST)) {
                path = path.substring(FILE_SCHEME_LOCALHOST.length());
            }
            if (!path.startsWith("/")) {
                throw new UnknownHostException();
            }
            File socketFile = new File(path);
            return new AFUNIXSocketAddress(socketFile, port);
        }
    }

    public static final class SystemProperty
    extends DefaultSocketHostnameSocketFactory {
        private static final String PROP_SOCKET_DEFAULT = "org.newsclub.net.unix.socket.default";

        @Override
        protected AFUNIXSocketAddress addressFromHost(String host, int port) throws IOException {
            String path = System.getProperty(PROP_SOCKET_DEFAULT);
            if (path == null || path.isEmpty()) {
                throw new IllegalStateException("Property not configured: org.newsclub.net.unix.socket.default");
            }
            File socketFile = new File(path);
            return new AFUNIXSocketAddress(socketFile, port);
        }
    }

    public static final class FactoryArg
    extends DefaultSocketHostnameSocketFactory {
        private final File socketFile;

        public FactoryArg(String socketPath) {
            Objects.requireNonNull(socketPath, "Socket path was null");
            this.socketFile = new File(socketPath);
        }

        public FactoryArg(File file) {
            Objects.requireNonNull(file, "File was null");
            this.socketFile = file;
        }

        @Override
        protected AFUNIXSocketAddress addressFromHost(String host, int port) throws IOException {
            return new AFUNIXSocketAddress(this.socketFile, port);
        }
    }

    private static abstract class DefaultSocketHostnameSocketFactory
    extends AFUNIXSocketFactory {
        private static final String PROP_SOCKET_HOSTNAME = "org.newsclub.net.unix.socket.hostname";

        private DefaultSocketHostnameSocketFactory() {
        }

        @Override
        protected final boolean isHostnameSupported(String host) {
            return DefaultSocketHostnameSocketFactory.getDefaultSocketHostname().equals(host);
        }

        private static String getDefaultSocketHostname() {
            return System.getProperty(PROP_SOCKET_HOSTNAME, "localhost");
        }
    }
}

