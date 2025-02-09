/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel.socket.nio;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPromise;
import io.netty.channel.FileRegion;
import io.netty.channel.nio.AbstractNioByteChannel;
import io.netty.channel.nio.NioEventLoop;
import io.netty.channel.socket.DefaultSocketChannelConfig;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.SocketChannelConfig;
import io.netty.util.internal.OneTimeTask;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.spi.SelectorProvider;

public class NioSocketChannel
extends AbstractNioByteChannel
implements SocketChannel {
    private static final ChannelMetadata METADATA = new ChannelMetadata(false);
    private static final SelectorProvider DEFAULT_SELECTOR_PROVIDER = SelectorProvider.provider();
    private final SocketChannelConfig config;

    private static java.nio.channels.SocketChannel newSocket(SelectorProvider provider) {
        try {
            return provider.openSocketChannel();
        }
        catch (IOException e2) {
            throw new ChannelException("Failed to open a socket.", e2);
        }
    }

    public NioSocketChannel() {
        this(NioSocketChannel.newSocket(DEFAULT_SELECTOR_PROVIDER));
    }

    public NioSocketChannel(SelectorProvider provider) {
        this(NioSocketChannel.newSocket(provider));
    }

    public NioSocketChannel(java.nio.channels.SocketChannel socket) {
        this(null, socket);
    }

    public NioSocketChannel(Channel parent, java.nio.channels.SocketChannel socket) {
        super(parent, socket);
        this.config = new NioSocketChannelConfig(this, socket.socket());
    }

    @Override
    public ServerSocketChannel parent() {
        return (ServerSocketChannel)super.parent();
    }

    @Override
    public ChannelMetadata metadata() {
        return METADATA;
    }

    @Override
    public SocketChannelConfig config() {
        return this.config;
    }

    @Override
    protected java.nio.channels.SocketChannel javaChannel() {
        return (java.nio.channels.SocketChannel)super.javaChannel();
    }

    @Override
    public boolean isActive() {
        java.nio.channels.SocketChannel ch = this.javaChannel();
        return ch.isOpen() && ch.isConnected();
    }

    @Override
    public boolean isInputShutdown() {
        return super.isInputShutdown();
    }

    @Override
    public InetSocketAddress localAddress() {
        return (InetSocketAddress)super.localAddress();
    }

    @Override
    public InetSocketAddress remoteAddress() {
        return (InetSocketAddress)super.remoteAddress();
    }

    @Override
    public boolean isOutputShutdown() {
        return this.javaChannel().socket().isOutputShutdown() || !this.isActive();
    }

    @Override
    public ChannelFuture shutdownOutput() {
        return this.shutdownOutput(this.newPromise());
    }

    @Override
    public ChannelFuture shutdownOutput(final ChannelPromise promise) {
        NioEventLoop loop = this.eventLoop();
        if (loop.inEventLoop()) {
            try {
                this.javaChannel().socket().shutdownOutput();
                promise.setSuccess();
            }
            catch (Throwable t2) {
                promise.setFailure(t2);
            }
        } else {
            loop.execute(new OneTimeTask(){

                @Override
                public void run() {
                    NioSocketChannel.this.shutdownOutput(promise);
                }
            });
        }
        return promise;
    }

    @Override
    protected SocketAddress localAddress0() {
        return this.javaChannel().socket().getLocalSocketAddress();
    }

    @Override
    protected SocketAddress remoteAddress0() {
        return this.javaChannel().socket().getRemoteSocketAddress();
    }

    @Override
    protected void doBind(SocketAddress localAddress) throws Exception {
        this.javaChannel().socket().bind(localAddress);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected boolean doConnect(SocketAddress remoteAddress, SocketAddress localAddress) throws Exception {
        if (localAddress != null) {
            this.javaChannel().socket().bind(localAddress);
        }
        boolean success = false;
        try {
            boolean connected = this.javaChannel().connect(remoteAddress);
            if (!connected) {
                this.selectionKey().interestOps(8);
            }
            success = true;
            boolean bl2 = connected;
            return bl2;
        }
        finally {
            if (!success) {
                this.doClose();
            }
        }
    }

    @Override
    protected void doFinishConnect() throws Exception {
        if (!this.javaChannel().finishConnect()) {
            throw new Error();
        }
    }

    @Override
    protected void doDisconnect() throws Exception {
        this.doClose();
    }

    @Override
    protected void doClose() throws Exception {
        this.javaChannel().close();
    }

    @Override
    protected int doReadBytes(ByteBuf byteBuf) throws Exception {
        return byteBuf.writeBytes(this.javaChannel(), byteBuf.writableBytes());
    }

    @Override
    protected int doWriteBytes(ByteBuf buf) throws Exception {
        int expectedWrittenBytes = buf.readableBytes();
        return buf.readBytes(this.javaChannel(), expectedWrittenBytes);
    }

    @Override
    protected long doWriteFileRegion(FileRegion region) throws Exception {
        long position = region.transfered();
        return region.transferTo(this.javaChannel(), position);
    }

    @Override
    protected void doWrite(ChannelOutboundBuffer in2) throws Exception {
        block10: {
            boolean setOpWrite;
            boolean done;
            do {
                int size;
                if ((size = in2.size()) == 0) {
                    this.clearOpWrite();
                    break block10;
                }
                long writtenBytes = 0L;
                done = false;
                setOpWrite = false;
                ByteBuffer[] nioBuffers = in2.nioBuffers();
                int nioBufferCnt = in2.nioBufferCount();
                long expectedWrittenBytes = in2.nioBufferSize();
                java.nio.channels.SocketChannel ch = this.javaChannel();
                block0 : switch (nioBufferCnt) {
                    case 0: {
                        super.doWrite(in2);
                        return;
                    }
                    case 1: {
                        int i2;
                        ByteBuffer nioBuffer = nioBuffers[0];
                        for (i2 = this.config().getWriteSpinCount() - 1; i2 >= 0; --i2) {
                            int localWrittenBytes = ch.write(nioBuffer);
                            if (localWrittenBytes == 0) {
                                setOpWrite = true;
                                break block0;
                            }
                            writtenBytes += (long)localWrittenBytes;
                            if ((expectedWrittenBytes -= (long)localWrittenBytes) != 0L) continue;
                            done = true;
                            break block0;
                        }
                        break;
                    }
                    default: {
                        int i2;
                        for (i2 = this.config().getWriteSpinCount() - 1; i2 >= 0; --i2) {
                            long localWrittenBytes = ch.write(nioBuffers, 0, nioBufferCnt);
                            if (localWrittenBytes == 0L) {
                                setOpWrite = true;
                                break block0;
                            }
                            writtenBytes += localWrittenBytes;
                            if ((expectedWrittenBytes -= localWrittenBytes) != 0L) continue;
                            done = true;
                            break block0;
                        }
                    }
                }
                in2.removeBytes(writtenBytes);
            } while (done);
            this.incompleteWrite(setOpWrite);
        }
    }

    private final class NioSocketChannelConfig
    extends DefaultSocketChannelConfig {
        private NioSocketChannelConfig(NioSocketChannel channel, Socket javaSocket) {
            super(channel, javaSocket);
        }

        @Override
        protected void autoReadCleared() {
            NioSocketChannel.this.setReadPending(false);
        }
    }
}

