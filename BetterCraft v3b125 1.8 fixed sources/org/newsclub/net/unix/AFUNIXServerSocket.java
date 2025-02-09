/*
 * Decompiled with CFR 0.152.
 */
package org.newsclub.net.unix;

import java.io.Closeable;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketAddress;
import java.net.SocketException;
import org.newsclub.net.unix.AFUNIXSocket;
import org.newsclub.net.unix.AFUNIXSocketAddress;
import org.newsclub.net.unix.AFUNIXSocketImpl;
import org.newsclub.net.unix.Closeables;
import org.newsclub.net.unix.NativeUnixSocket;

public class AFUNIXServerSocket
extends ServerSocket {
    private final AFUNIXSocketImpl implementation;
    private AFUNIXSocketAddress boundEndpoint;
    private final Closeables closeables = new Closeables();

    protected AFUNIXServerSocket() throws IOException {
        this.setReuseAddress(true);
        this.implementation = new AFUNIXSocketImpl();
        NativeUnixSocket.initServerImpl(this, this.implementation);
        NativeUnixSocket.setCreatedServer(this);
    }

    public static AFUNIXServerSocket newInstance() throws IOException {
        return new AFUNIXServerSocket();
    }

    public static AFUNIXServerSocket bindOn(AFUNIXSocketAddress addr) throws IOException {
        AFUNIXServerSocket socket = AFUNIXServerSocket.newInstance();
        socket.bind(addr);
        return socket;
    }

    public static AFUNIXServerSocket forceBindOn(AFUNIXSocketAddress forceAddr) throws IOException {
        return new AFUNIXServerSocket(){

            @Override
            public void bind(SocketAddress ignored, int backlog) throws IOException {
                super.bind(forceAddr, backlog);
            }
        };
    }

    @Override
    public void bind(SocketAddress endpoint, int backlog) throws IOException {
        if (this.isClosed()) {
            throw new SocketException("Socket is closed");
        }
        if (this.isBound()) {
            throw new SocketException("Already bound");
        }
        if (!(endpoint instanceof AFUNIXSocketAddress)) {
            throw new IOException("Can only bind to endpoints of type " + AFUNIXSocketAddress.class.getName());
        }
        this.implementation.bind(endpoint, this.getReuseAddress() ? -1 : 0);
        this.boundEndpoint = (AFUNIXSocketAddress)endpoint;
        this.implementation.listen(backlog);
    }

    @Override
    public boolean isBound() {
        return this.boundEndpoint != null;
    }

    @Override
    public boolean isClosed() {
        return super.isClosed() || this.isBound() && !this.implementation.getFD().valid();
    }

    @Override
    public AFUNIXSocket accept() throws IOException {
        if (this.isClosed()) {
            throw new SocketException("Socket is closed");
        }
        AFUNIXSocket as2 = this.newSocketInstance();
        this.implementation.accept(as2.impl);
        as2.addr = this.boundEndpoint;
        NativeUnixSocket.setConnected(as2);
        return as2;
    }

    protected AFUNIXSocket newSocketInstance() throws IOException {
        return AFUNIXSocket.newInstance();
    }

    @Override
    public String toString() {
        if (!this.isBound()) {
            return "AFUNIXServerSocket[unbound]";
        }
        return "AFUNIXServerSocket[" + this.boundEndpoint.toString() + "]";
    }

    @Override
    public synchronized void close() throws IOException {
        if (this.isClosed()) {
            return;
        }
        IOException superException = null;
        try {
            super.close();
        }
        catch (IOException e2) {
            superException = e2;
        }
        if (this.implementation != null) {
            try {
                this.implementation.close();
            }
            catch (IOException e3) {
                if (superException == null) {
                    superException = e3;
                }
                superException.addSuppressed(e3);
            }
        }
        this.closeables.close(superException);
    }

    public void addCloseable(Closeable closeable) {
        this.closeables.add(closeable);
    }

    public void removeCloseable(Closeable closeable) {
        this.closeables.remove(closeable);
    }

    public static boolean isSupported() {
        return NativeUnixSocket.isLoaded();
    }

    @Override
    public SocketAddress getLocalSocketAddress() {
        return this.boundEndpoint;
    }

    @Override
    public int getLocalPort() {
        if (this.boundEndpoint == null) {
            return -1;
        }
        return this.boundEndpoint.getPort();
    }
}

