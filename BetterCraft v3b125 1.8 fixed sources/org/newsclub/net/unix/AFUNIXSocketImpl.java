/*
 * Decompiled with CFR 0.152.
 */
package org.newsclub.net.unix;

import java.io.Closeable;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketImpl;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.newsclub.net.unix.AFUNIXSocketAddress;
import org.newsclub.net.unix.AFUNIXSocketCredentials;
import org.newsclub.net.unix.NativeUnixSocket;

class AFUNIXSocketImpl
extends SocketImpl {
    private static final int SHUT_RD = 0;
    private static final int SHUT_WR = 1;
    private static final int SHUT_RD_WR = 2;
    private AFUNIXSocketAddress socketAddress;
    private long inode = -1L;
    private volatile boolean closed = false;
    private volatile boolean bound = false;
    private boolean connected = false;
    private volatile boolean closedInputStream = false;
    private volatile boolean closedOutputStream = false;
    private final AFUNIXInputStream in = this.newInputStream();
    private final AFUNIXOutputStream out = this.newOutputStream();
    private final AtomicInteger pendingAccepts = new AtomicInteger(0);
    private boolean reuseAddr = true;
    private ByteBuffer ancillaryReceiveBuffer = ByteBuffer.allocateDirect(0);
    private final List<FileDescriptor[]> receivedFileDescriptors = Collections.synchronizedList(new LinkedList());
    private int[] pendingFileDescriptors = null;
    private final Map<FileDescriptor, Integer> closeableFileDescriptors = Collections.synchronizedMap(new HashMap());
    private int timeout = 0;

    protected AFUNIXSocketImpl() {
        this.fd = new FileDescriptor();
        this.address = InetAddress.getLoopbackAddress();
    }

    protected AFUNIXInputStream newInputStream() {
        return new AFUNIXInputStream();
    }

    protected AFUNIXOutputStream newOutputStream() {
        return new AFUNIXOutputStream();
    }

    FileDescriptor getFD() {
        return this.fd;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected final void finalize() {
        try {
            this.close();
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        try {
            Map<FileDescriptor, Integer> map = this.closeableFileDescriptors;
            synchronized (map) {
                for (FileDescriptor fd : this.closeableFileDescriptors.keySet()) {
                    NativeUnixSocket.close(fd);
                }
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void accept(SocketImpl socket) throws IOException {
        block9: {
            FileDescriptor fdesc = this.validFdOrException();
            AFUNIXSocketImpl si2 = (AFUNIXSocketImpl)socket;
            try {
                if (this.pendingAccepts.incrementAndGet() >= Integer.MAX_VALUE) {
                    throw new SocketException("Too many pending accepts");
                }
                if (!this.bound || this.closed) {
                    throw new SocketException("Socket is closed");
                }
                NativeUnixSocket.accept(this.socketAddress.getBytes(), fdesc, si2.fd, this.inode, this.timeout);
                if (this.bound && !this.closed) break block9;
                try {
                    NativeUnixSocket.shutdown(si2.fd, 2);
                }
                catch (Exception exception) {
                    // empty catch block
                }
                try {
                    NativeUnixSocket.close(si2.fd);
                }
                catch (Exception exception) {
                    // empty catch block
                }
                throw new SocketException("Socket is closed");
            }
            finally {
                this.pendingAccepts.decrementAndGet();
            }
        }
        si2.socketAddress = this.socketAddress;
        si2.connected = true;
        si2.port = this.socketAddress.getPort();
        si2.address = this.socketAddress.getAddress();
    }

    @Override
    protected int available() throws IOException {
        FileDescriptor fdesc = this.validFdOrException();
        return NativeUnixSocket.available(fdesc);
    }

    protected void bind(SocketAddress addr) throws IOException {
        this.bind(addr, -1);
    }

    protected void bind(SocketAddress addr, int options) throws IOException {
        if (!(addr instanceof AFUNIXSocketAddress)) {
            throw new SocketException("Cannot bind to this type of address: " + addr.getClass());
        }
        this.socketAddress = (AFUNIXSocketAddress)addr;
        this.address = this.socketAddress.getAddress();
        this.inode = NativeUnixSocket.bind(this.socketAddress.getBytes(), this.fd, options);
        this.validFdOrException();
        this.bound = true;
        this.localport = this.socketAddress.getPort();
    }

    @Override
    protected void bind(InetAddress host, int port) throws IOException {
        throw new SocketException("Cannot bind to this type of address: " + InetAddress.class);
    }

    private void checkClose() throws IOException {
        if (this.closedInputStream && this.closedOutputStream) {
            this.close();
        }
    }

    private void unblockAccepts() {
        while (this.pendingAccepts.get() > 0) {
            try {
                FileDescriptor tmpFd = new FileDescriptor();
                try {
                    NativeUnixSocket.connect(this.socketAddress.getBytes(), tmpFd, this.inode);
                }
                catch (IOException e2) {
                    return;
                }
                try {
                    NativeUnixSocket.shutdown(tmpFd, 2);
                }
                catch (Exception exception) {
                    // empty catch block
                }
                try {
                    NativeUnixSocket.close(tmpFd);
                }
                catch (Exception exception) {
                }
            }
            catch (Exception exception) {}
        }
    }

    @Override
    protected final synchronized void close() throws IOException {
        boolean wasBound = this.bound;
        this.bound = false;
        FileDescriptor fdesc = this.validFd();
        if (fdesc != null) {
            NativeUnixSocket.shutdown(fdesc, 2);
            this.closed = true;
            if (wasBound && this.socketAddress != null && this.socketAddress.getBytes() != null && this.inode >= 0L) {
                this.unblockAccepts();
            }
            NativeUnixSocket.close(fdesc);
        }
        this.closed = true;
    }

    @Override
    protected void connect(String host, int port) throws IOException {
        throw new SocketException("Cannot bind to this type of address: " + InetAddress.class);
    }

    @Override
    protected void connect(InetAddress address, int port) throws IOException {
        throw new SocketException("Cannot bind to this type of address: " + InetAddress.class);
    }

    @Override
    protected void connect(SocketAddress addr, int connectTimeout) throws IOException {
        if (!(addr instanceof AFUNIXSocketAddress)) {
            throw new SocketException("Cannot bind to this type of address: " + addr.getClass());
        }
        this.socketAddress = (AFUNIXSocketAddress)addr;
        NativeUnixSocket.connect(this.socketAddress.getBytes(), this.fd, -1L);
        this.validFdOrException();
        this.address = this.socketAddress.getAddress();
        this.port = this.socketAddress.getPort();
        this.localport = 0;
        this.connected = true;
    }

    @Override
    protected void create(boolean stream) throws IOException {
    }

    @Override
    protected InputStream getInputStream() throws IOException {
        if (!this.connected && !this.bound) {
            throw new IOException("Not connected/not bound");
        }
        this.validFdOrException();
        return this.in;
    }

    @Override
    protected OutputStream getOutputStream() throws IOException {
        if (!this.connected && !this.bound) {
            throw new IOException("Not connected/not bound");
        }
        this.validFdOrException();
        return this.out;
    }

    @Override
    protected void listen(int backlog) throws IOException {
        FileDescriptor fdesc = this.validFdOrException();
        if (backlog <= 0) {
            backlog = 50;
        }
        NativeUnixSocket.listen(fdesc, backlog);
    }

    @Override
    protected void sendUrgentData(int data) throws IOException {
        FileDescriptor fdesc = this.validFdOrException();
        NativeUnixSocket.write(this, fdesc, new byte[]{(byte)(data & 0xFF)}, 0, 1, this.pendingFileDescriptors);
    }

    private FileDescriptor validFdOrException() throws SocketException {
        FileDescriptor fdesc = this.validFd();
        if (fdesc == null) {
            throw new SocketException("Not open");
        }
        return fdesc;
    }

    private synchronized FileDescriptor validFd() {
        if (this.closed) {
            return null;
        }
        FileDescriptor descriptor = this.fd;
        if (descriptor != null && descriptor.valid()) {
            return descriptor;
        }
        return null;
    }

    @Override
    public String toString() {
        return super.toString() + "[fd=" + this.fd + "; addr=" + this.socketAddress + "; connected=" + this.connected + "; bound=" + this.bound + "]";
    }

    private static int expectInteger(Object value) throws SocketException {
        try {
            return (Integer)value;
        }
        catch (ClassCastException e2) {
            throw (SocketException)new SocketException("Unsupported value: " + value).initCause(e2);
        }
        catch (NullPointerException e3) {
            throw (SocketException)new SocketException("Value must not be null").initCause(e3);
        }
    }

    private static int expectBoolean(Object value) throws SocketException {
        try {
            return (Boolean)value != false ? 1 : 0;
        }
        catch (ClassCastException e2) {
            throw (SocketException)new SocketException("Unsupported value: " + value).initCause(e2);
        }
        catch (NullPointerException e3) {
            throw (SocketException)new SocketException("Value must not be null").initCause(e3);
        }
    }

    @Override
    public Object getOption(int optID) throws SocketException {
        if (optID == 4) {
            return this.reuseAddr;
        }
        FileDescriptor fdesc = this.validFdOrException();
        try {
            switch (optID) {
                case 1: 
                case 8: {
                    return NativeUnixSocket.getSocketOptionInt(fdesc, optID) != 0;
                }
                case 4102: {
                    return Math.max(this.timeout, Math.max(NativeUnixSocket.getSocketOptionInt(fdesc, 4101), NativeUnixSocket.getSocketOptionInt(fdesc, 4102)));
                }
                case 128: 
                case 4097: 
                case 4098: {
                    return NativeUnixSocket.getSocketOptionInt(fdesc, optID);
                }
            }
            throw new SocketException("Unsupported option: " + optID);
        }
        catch (SocketException e2) {
            throw e2;
        }
        catch (Exception e3) {
            throw (SocketException)new SocketException("Error while getting option").initCause(e3);
        }
    }

    @Override
    public void setOption(int optID, Object value) throws SocketException {
        if (optID == 4) {
            this.reuseAddr = AFUNIXSocketImpl.expectBoolean(value) != 0;
            return;
        }
        FileDescriptor fdesc = this.validFdOrException();
        try {
            switch (optID) {
                case 128: {
                    if (value instanceof Boolean) {
                        boolean b2 = (Boolean)value;
                        if (b2) {
                            throw new SocketException("Only accepting Boolean.FALSE here");
                        }
                        NativeUnixSocket.setSocketOptionInt(fdesc, optID, -1);
                        return;
                    }
                    NativeUnixSocket.setSocketOptionInt(fdesc, optID, AFUNIXSocketImpl.expectInteger(value));
                    return;
                }
                case 4102: {
                    this.timeout = AFUNIXSocketImpl.expectInteger(value);
                    NativeUnixSocket.setSocketOptionInt(fdesc, 4101, this.timeout);
                    NativeUnixSocket.setSocketOptionInt(fdesc, 4102, this.timeout);
                    return;
                }
                case 4097: 
                case 4098: {
                    NativeUnixSocket.setSocketOptionInt(fdesc, optID, AFUNIXSocketImpl.expectInteger(value));
                    return;
                }
                case 1: 
                case 8: {
                    NativeUnixSocket.setSocketOptionInt(fdesc, optID, AFUNIXSocketImpl.expectBoolean(value));
                    return;
                }
            }
            throw new SocketException("Unsupported option: " + optID);
        }
        catch (SocketException e2) {
            throw e2;
        }
        catch (Exception e3) {
            throw (SocketException)new SocketException("Error while setting option").initCause(e3);
        }
    }

    @Override
    protected void shutdownInput() throws IOException {
        FileDescriptor fdesc = this.validFd();
        if (fdesc != null) {
            NativeUnixSocket.shutdown(fdesc, 0);
        }
    }

    @Override
    protected void shutdownOutput() throws IOException {
        FileDescriptor fdesc = this.validFd();
        if (fdesc != null) {
            NativeUnixSocket.shutdown(fdesc, 1);
        }
    }

    AFUNIXSocketCredentials getPeerCredentials() throws IOException {
        return NativeUnixSocket.peerCredentials(this.fd, new AFUNIXSocketCredentials());
    }

    int getAncillaryReceiveBufferSize() {
        return this.ancillaryReceiveBuffer.capacity();
    }

    void setAncillaryReceiveBufferSize(int size) {
        this.ancillaryReceiveBuffer = ByteBuffer.allocateDirect(size);
    }

    public final void ensureAncillaryReceiveBufferSize(int minSize) {
        if (minSize <= 0) {
            return;
        }
        if (this.ancillaryReceiveBuffer.capacity() < minSize) {
            this.setAncillaryReceiveBufferSize(minSize);
        }
    }

    public final FileDescriptor[] getReceivedFileDescriptors() {
        if (this.receivedFileDescriptors.isEmpty()) {
            return null;
        }
        ArrayList<FileDescriptor[]> copy = new ArrayList<FileDescriptor[]>(this.receivedFileDescriptors);
        if (copy.isEmpty()) {
            return null;
        }
        this.receivedFileDescriptors.removeAll(copy);
        int count = 0;
        for (FileDescriptor[] fds : copy) {
            count += fds.length;
        }
        if (count == 0) {
            return null;
        }
        FileDescriptor[] oneArray = new FileDescriptor[count];
        int offset = 0;
        for (FileDescriptor[] fds : copy) {
            System.arraycopy(fds, 0, oneArray, offset, fds.length);
            offset += fds.length;
        }
        return oneArray;
    }

    public final void clearReceivedFileDescriptors() {
        this.receivedFileDescriptors.clear();
    }

    final void receiveFileDescriptors(int[] fds) throws IOException {
        if (fds == null || fds.length == 0) {
            return;
        }
        int fdsLength = fds.length;
        FileDescriptor[] descriptors = new FileDescriptor[fdsLength];
        for (int i2 = 0; i2 < fdsLength; ++i2) {
            FileDescriptor fdesc = new FileDescriptor();
            NativeUnixSocket.initFD(fdesc, fds[i2]);
            descriptors[i2] = fdesc;
            this.closeableFileDescriptors.put(fdesc, fds[i2]);
            Closeable cleanup = new Closeable(){

                @Override
                public void close() throws IOException {
                    AFUNIXSocketImpl.this.closeableFileDescriptors.remove(fdesc);
                }
            };
            NativeUnixSocket.attachCloseable(fdesc, cleanup);
        }
        this.receivedFileDescriptors.add(descriptors);
    }

    final void setOutboundFileDescriptors(int ... fds) {
        this.pendingFileDescriptors = fds == null || fds.length == 0 ? null : fds;
    }

    public final void setOutboundFileDescriptors(FileDescriptor ... fdescs) throws IOException {
        if (fdescs == null || fdescs.length == 0) {
            this.setOutboundFileDescriptors((int[])null);
        } else {
            int numFdescs = fdescs.length;
            int[] fds = new int[numFdescs];
            for (int i2 = 0; i2 < numFdescs; ++i2) {
                FileDescriptor fdesc = fdescs[i2];
                fds[i2] = NativeUnixSocket.getFD(fdesc);
            }
            this.setOutboundFileDescriptors(fds);
        }
    }

    static final class Lenient
    extends AFUNIXSocketImpl {
        Lenient() {
        }

        @Override
        public void setOption(int optID, Object value) throws SocketException {
            try {
                super.setOption(optID, value);
            }
            catch (SocketException e2) {
                switch (optID) {
                    case 1: {
                        return;
                    }
                }
                throw e2;
            }
        }

        @Override
        public Object getOption(int optID) throws SocketException {
            try {
                return super.getOption(optID);
            }
            catch (SocketException e2) {
                switch (optID) {
                    case 1: 
                    case 8: {
                        return false;
                    }
                }
                throw e2;
            }
        }
    }

    private class AFUNIXOutputStream
    extends OutputStream {
        private volatile boolean streamClosed = false;

        private AFUNIXOutputStream() {
        }

        @Override
        public void write(int oneByte) throws IOException {
            byte[] buf1 = new byte[]{(byte)oneByte};
            this.write(buf1, 0, 1);
        }

        @Override
        public void write(byte[] buf, int off, int len) throws IOException {
            if (this.streamClosed) {
                throw new SocketException("This OutputStream has already been closed.");
            }
            if (len < 0 || off < 0 || len > buf.length - off) {
                throw new IndexOutOfBoundsException();
            }
            FileDescriptor fdesc = AFUNIXSocketImpl.this.validFdOrException();
            int writtenTotal = 0;
            while (len > 0) {
                if (Thread.interrupted()) {
                    InterruptedIOException ex2 = new InterruptedIOException("Thread interrupted during write");
                    ex2.bytesTransferred = writtenTotal;
                    Thread.currentThread().interrupt();
                    throw ex2;
                }
                int written = NativeUnixSocket.write(AFUNIXSocketImpl.this, fdesc, buf, off, len, AFUNIXSocketImpl.this.pendingFileDescriptors);
                if (written < 0) {
                    throw new IOException("Unspecific error while writing");
                }
                len -= written;
                off += written;
                writtenTotal += written;
            }
        }

        @Override
        public synchronized void close() throws IOException {
            if (this.streamClosed) {
                return;
            }
            this.streamClosed = true;
            FileDescriptor fdesc = AFUNIXSocketImpl.this.validFd();
            if (fdesc != null) {
                NativeUnixSocket.shutdown(fdesc, 1);
            }
            AFUNIXSocketImpl.this.closedOutputStream = true;
            AFUNIXSocketImpl.this.checkClose();
        }
    }

    private class AFUNIXInputStream
    extends InputStream {
        private volatile boolean streamClosed = false;
        private boolean eofReached = false;

        private AFUNIXInputStream() {
        }

        @Override
        public int read(byte[] buf, int off, int len) throws IOException {
            if (this.streamClosed) {
                throw new IOException("This InputStream has already been closed.");
            }
            FileDescriptor fdesc = AFUNIXSocketImpl.this.validFdOrException();
            if (len == 0) {
                return 0;
            }
            if (off < 0 || len < 0 || len > buf.length - off) {
                throw new IndexOutOfBoundsException();
            }
            return NativeUnixSocket.read(AFUNIXSocketImpl.this, fdesc, buf, off, len, AFUNIXSocketImpl.this.ancillaryReceiveBuffer);
        }

        @Override
        public int read() throws IOException {
            if (this.eofReached) {
                return -1;
            }
            byte[] buf1 = new byte[1];
            int numRead = this.read(buf1, 0, 1);
            if (numRead <= 0) {
                this.eofReached = true;
                return -1;
            }
            return buf1[0] & 0xFF;
        }

        @Override
        public synchronized void close() throws IOException {
            this.streamClosed = true;
            FileDescriptor fdesc = AFUNIXSocketImpl.this.validFd();
            if (fdesc != null) {
                NativeUnixSocket.shutdown(fdesc, 0);
            }
            AFUNIXSocketImpl.this.closedInputStream = true;
            AFUNIXSocketImpl.this.checkClose();
        }

        @Override
        public int available() throws IOException {
            if (this.streamClosed) {
                throw new IOException("This InputStream has already been closed.");
            }
            FileDescriptor fdesc = AFUNIXSocketImpl.this.validFdOrException();
            return NativeUnixSocket.available(fdesc);
        }
    }
}

