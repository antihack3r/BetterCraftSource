/*
 * Decompiled with CFR 0.152.
 */
package org.newsclub.net.unix;

import java.io.Closeable;
import java.io.FileDescriptor;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import org.newsclub.net.unix.AFUNIXSocketAddress;
import org.newsclub.net.unix.AFUNIXSocketCapability;
import org.newsclub.net.unix.AFUNIXSocketCredentials;
import org.newsclub.net.unix.AFUNIXSocketFactory;
import org.newsclub.net.unix.AFUNIXSocketImpl;
import org.newsclub.net.unix.Closeables;
import org.newsclub.net.unix.NativeLibraryLoader;
import org.newsclub.net.unix.NativeUnixSocket;

public final class AFUNIXSocket
extends Socket {
    static String loadedLibrary;
    private static Integer capabilities;
    AFUNIXSocketImpl impl;
    AFUNIXSocketAddress addr;
    private final AFUNIXSocketFactory socketFactory;
    private final Closeables closeables = new Closeables();

    private AFUNIXSocket(AFUNIXSocketImpl impl, AFUNIXSocketFactory factory) throws IOException {
        super(impl);
        this.socketFactory = factory;
        if (factory == null) {
            this.setIsCreated();
        }
    }

    private void setIsCreated() throws IOException {
        try {
            NativeUnixSocket.setCreated(this);
        }
        catch (LinkageError e2) {
            throw new IOException("Couldn't load native library", e2);
        }
    }

    public static AFUNIXSocket newInstance() throws IOException {
        return AFUNIXSocket.newInstance(null);
    }

    static AFUNIXSocket newInstance(AFUNIXSocketFactory factory) throws IOException {
        AFUNIXSocketImpl.Lenient impl = new AFUNIXSocketImpl.Lenient();
        AFUNIXSocket instance = new AFUNIXSocket(impl, factory);
        instance.impl = impl;
        return instance;
    }

    public static AFUNIXSocket newStrictInstance() throws IOException {
        AFUNIXSocketImpl impl = new AFUNIXSocketImpl();
        AFUNIXSocket instance = new AFUNIXSocket(impl, null);
        instance.impl = impl;
        return instance;
    }

    public static AFUNIXSocket connectTo(AFUNIXSocketAddress addr) throws IOException {
        AFUNIXSocket socket = AFUNIXSocket.newInstance();
        socket.connect(addr);
        return socket;
    }

    @Override
    public void bind(SocketAddress bindpoint) throws IOException {
        super.bind(bindpoint);
        this.addr = (AFUNIXSocketAddress)bindpoint;
    }

    @Override
    public void connect(SocketAddress endpoint) throws IOException {
        this.connect(endpoint, 0);
    }

    @Override
    public void connect(SocketAddress endpoint, int timeout) throws IOException {
        if (endpoint == null) {
            throw new IllegalArgumentException("connect: The address can't be null");
        }
        if (timeout < 0) {
            throw new IllegalArgumentException("connect: timeout can't be negative");
        }
        if (this.isClosed()) {
            throw new SocketException("Socket is closed");
        }
        if (!(endpoint instanceof AFUNIXSocketAddress)) {
            InetSocketAddress isa;
            String hostname;
            if (this.socketFactory != null && endpoint instanceof InetSocketAddress && this.socketFactory.isHostnameSupported(hostname = (isa = (InetSocketAddress)endpoint).getHostString())) {
                endpoint = this.socketFactory.addressFromHost(hostname, isa.getPort());
            }
            if (!(endpoint instanceof AFUNIXSocketAddress)) {
                throw new IllegalArgumentException("Can only connect to endpoints of type " + AFUNIXSocketAddress.class.getName() + ", got: " + endpoint);
            }
        }
        this.impl.connect(endpoint, timeout);
        this.addr = (AFUNIXSocketAddress)endpoint;
        NativeUnixSocket.setBound(this);
        NativeUnixSocket.setConnected(this);
    }

    @Override
    public String toString() {
        if (this.isConnected()) {
            return "AFUNIXSocket[fd=" + this.impl.getFD() + ";addr=" + this.addr.toString() + "]";
        }
        return "AFUNIXSocket[unconnected]";
    }

    public static boolean isSupported() {
        return NativeUnixSocket.isLoaded();
    }

    public static String getVersion() {
        try {
            return NativeLibraryLoader.getJunixsocketVersion();
        }
        catch (IOException e2) {
            return null;
        }
    }

    public static String getLoadedLibrary() {
        return loadedLibrary;
    }

    public AFUNIXSocketCredentials getPeerCredentials() throws IOException {
        if (this.isClosed() || !this.isConnected()) {
            throw new SocketException("Not connected");
        }
        return this.impl.getPeerCredentials();
    }

    @Override
    public boolean isClosed() {
        return super.isClosed() || this.isConnected() && !this.impl.getFD().valid();
    }

    public int getAncillaryReceiveBufferSize() {
        return this.impl.getAncillaryReceiveBufferSize();
    }

    public void setAncillaryReceiveBufferSize(int size) {
        this.impl.setAncillaryReceiveBufferSize(size);
    }

    public void ensureAncillaryReceiveBufferSize(int minSize) {
        this.impl.ensureAncillaryReceiveBufferSize(minSize);
    }

    public FileDescriptor[] getReceivedFileDescriptors() throws IOException {
        return this.impl.getReceivedFileDescriptors();
    }

    public void clearReceivedFileDescriptors() {
        this.impl.clearReceivedFileDescriptors();
    }

    public void setOutboundFileDescriptors(FileDescriptor ... fdescs) throws IOException {
        this.impl.setOutboundFileDescriptors(fdescs);
    }

    private static synchronized int getCapabilities() {
        if (capabilities == null) {
            capabilities = !AFUNIXSocket.isSupported() ? Integer.valueOf(0) : Integer.valueOf(NativeUnixSocket.capabilities());
        }
        return capabilities;
    }

    public static boolean supports(AFUNIXSocketCapability capability) {
        return (AFUNIXSocket.getCapabilities() & capability.getBitmask()) != 0;
    }

    @Override
    public synchronized void close() throws IOException {
        IOException superException = null;
        try {
            super.close();
        }
        catch (IOException e2) {
            superException = e2;
        }
        this.closeables.close(superException);
    }

    public void addCloseable(Closeable closeable) {
        this.closeables.add(closeable);
    }

    public void removeCloseable(Closeable closeable) {
        this.closeables.remove(closeable);
    }

    public static void main(String[] args) {
        System.out.print("AFUNIXSocket.isSupported(): ");
        System.out.flush();
        System.out.println(AFUNIXSocket.isSupported());
        for (AFUNIXSocketCapability cap : AFUNIXSocketCapability.values()) {
            System.out.print(cap + ": ");
            System.out.flush();
            System.out.println(AFUNIXSocket.supports(cap));
        }
    }

    static {
        capabilities = null;
    }
}

