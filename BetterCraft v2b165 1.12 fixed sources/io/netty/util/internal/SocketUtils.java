// 
// Decompiled by Procyon v0.6.0
// 

package io.netty.util.internal;

import java.net.SocketException;
import java.util.Enumeration;
import java.net.NetworkInterface;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.net.InetAddress;
import java.security.PrivilegedAction;
import java.net.ServerSocket;
import java.nio.channels.DatagramChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.security.PrivilegedActionException;
import java.security.AccessController;
import java.io.IOException;
import java.security.PrivilegedExceptionAction;
import java.net.SocketAddress;
import java.net.Socket;

public final class SocketUtils
{
    private SocketUtils() {
    }
    
    public static void connect(final Socket socket, final SocketAddress remoteAddress, final int timeout) throws IOException {
        try {
            AccessController.doPrivileged((PrivilegedExceptionAction<Object>)new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws IOException {
                    socket.connect(remoteAddress, timeout);
                    return null;
                }
            });
        }
        catch (final PrivilegedActionException e) {
            throw (IOException)e.getCause();
        }
    }
    
    public static void bind(final Socket socket, final SocketAddress bindpoint) throws IOException {
        try {
            AccessController.doPrivileged((PrivilegedExceptionAction<Object>)new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws IOException {
                    socket.bind(bindpoint);
                    return null;
                }
            });
        }
        catch (final PrivilegedActionException e) {
            throw (IOException)e.getCause();
        }
    }
    
    public static boolean connect(final SocketChannel socketChannel, final SocketAddress remoteAddress) throws IOException {
        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<Boolean>)new PrivilegedExceptionAction<Boolean>() {
                @Override
                public Boolean run() throws IOException {
                    return socketChannel.connect(remoteAddress);
                }
            });
        }
        catch (final PrivilegedActionException e) {
            throw (IOException)e.getCause();
        }
    }
    
    public static void bind(final SocketChannel socketChannel, final SocketAddress address) throws IOException {
        try {
            AccessController.doPrivileged((PrivilegedExceptionAction<Object>)new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws IOException {
                    socketChannel.bind(address);
                    return null;
                }
            });
        }
        catch (final PrivilegedActionException e) {
            throw (IOException)e.getCause();
        }
    }
    
    public static SocketChannel accept(final ServerSocketChannel serverSocketChannel) throws IOException {
        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<SocketChannel>)new PrivilegedExceptionAction<SocketChannel>() {
                @Override
                public SocketChannel run() throws IOException {
                    return serverSocketChannel.accept();
                }
            });
        }
        catch (final PrivilegedActionException e) {
            throw (IOException)e.getCause();
        }
    }
    
    public static void bind(final DatagramChannel networkChannel, final SocketAddress address) throws IOException {
        try {
            AccessController.doPrivileged((PrivilegedExceptionAction<Object>)new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws IOException {
                    networkChannel.bind(address);
                    return null;
                }
            });
        }
        catch (final PrivilegedActionException e) {
            throw (IOException)e.getCause();
        }
    }
    
    public static SocketAddress localSocketAddress(final ServerSocket socket) {
        return AccessController.doPrivileged((PrivilegedAction<SocketAddress>)new PrivilegedAction<SocketAddress>() {
            @Override
            public SocketAddress run() {
                return socket.getLocalSocketAddress();
            }
        });
    }
    
    public static InetAddress addressByName(final String hostname) throws UnknownHostException {
        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<InetAddress>)new PrivilegedExceptionAction<InetAddress>() {
                @Override
                public InetAddress run() throws UnknownHostException {
                    return InetAddress.getByName(hostname);
                }
            });
        }
        catch (final PrivilegedActionException e) {
            throw (UnknownHostException)e.getCause();
        }
    }
    
    public static InetAddress[] allAddressesByName(final String hostname) throws UnknownHostException {
        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<InetAddress[]>)new PrivilegedExceptionAction<InetAddress[]>() {
                @Override
                public InetAddress[] run() throws UnknownHostException {
                    return InetAddress.getAllByName(hostname);
                }
            });
        }
        catch (final PrivilegedActionException e) {
            throw (UnknownHostException)e.getCause();
        }
    }
    
    public static InetSocketAddress socketAddress(final String hostname, final int port) {
        return AccessController.doPrivileged((PrivilegedAction<InetSocketAddress>)new PrivilegedAction<InetSocketAddress>() {
            @Override
            public InetSocketAddress run() {
                return new InetSocketAddress(hostname, port);
            }
        });
    }
    
    public static Enumeration<InetAddress> addressesFromNetworkInterface(final NetworkInterface intf) {
        return AccessController.doPrivileged((PrivilegedAction<Enumeration<InetAddress>>)new PrivilegedAction<Enumeration<InetAddress>>() {
            @Override
            public Enumeration<InetAddress> run() {
                return intf.getInetAddresses();
            }
        });
    }
    
    public static InetAddress loopbackAddress() {
        return AccessController.doPrivileged((PrivilegedAction<InetAddress>)new PrivilegedAction<InetAddress>() {
            @Override
            public InetAddress run() {
                if (PlatformDependent.javaVersion() >= 7) {
                    return InetAddress.getLoopbackAddress();
                }
                try {
                    return InetAddress.getByName(null);
                }
                catch (final UnknownHostException e) {
                    throw new IllegalStateException(e);
                }
            }
        });
    }
    
    public static byte[] hardwareAddressFromNetworkInterface(final NetworkInterface intf) throws SocketException {
        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<byte[]>)new PrivilegedExceptionAction<byte[]>() {
                @Override
                public byte[] run() throws SocketException {
                    return intf.getHardwareAddress();
                }
            });
        }
        catch (final PrivilegedActionException e) {
            throw (SocketException)e.getCause();
        }
    }
}
