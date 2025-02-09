// 
// Decompiled by Procyon v0.6.0
// 

package io.netty.channel.epoll;

import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.socket.ChannelInputShutdownReadComplete;
import java.nio.channels.NotYetConnectedException;
import io.netty.channel.socket.ChannelInputShutdownEvent;
import io.netty.channel.unix.FileDescriptor;
import java.nio.ByteBuffer;
import java.nio.channels.UnresolvedAddressException;
import java.net.InetSocketAddress;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.util.ReferenceCountUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelConfig;
import io.netty.channel.EventLoop;
import java.io.IOException;
import io.netty.channel.ChannelException;
import io.netty.util.internal.ObjectUtil;
import io.netty.channel.Channel;
import io.netty.channel.unix.Socket;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.unix.UnixChannel;
import io.netty.channel.AbstractChannel;

abstract class AbstractEpollChannel extends AbstractChannel implements UnixChannel
{
    private static final ChannelMetadata METADATA;
    private final int readFlag;
    private final Socket fileDescriptor;
    protected int flags;
    boolean inputClosedSeenErrorOnRead;
    boolean epollInReadyRunnablePending;
    protected volatile boolean active;
    
    AbstractEpollChannel(final Socket fd, final int flag) {
        this(null, fd, flag, false);
    }
    
    AbstractEpollChannel(final Channel parent, final Socket fd, final int flag, final boolean active) {
        super(parent);
        this.flags = Native.EPOLLET;
        this.fileDescriptor = ObjectUtil.checkNotNull(fd, "fd");
        this.readFlag = flag;
        this.flags |= flag;
        this.active = active;
    }
    
    static boolean isSoErrorZero(final Socket fd) {
        try {
            return fd.getSoError() == 0;
        }
        catch (final IOException e) {
            throw new ChannelException(e);
        }
    }
    
    void setFlag(final int flag) throws IOException {
        if (!this.isFlagSet(flag)) {
            this.flags |= flag;
            this.modifyEvents();
        }
    }
    
    void clearFlag(final int flag) throws IOException {
        if (this.isFlagSet(flag)) {
            this.flags &= ~flag;
            this.modifyEvents();
        }
    }
    
    boolean isFlagSet(final int flag) {
        return (this.flags & flag) != 0x0;
    }
    
    @Override
    public final Socket fd() {
        return this.fileDescriptor;
    }
    
    @Override
    public abstract EpollChannelConfig config();
    
    @Override
    public boolean isActive() {
        return this.active;
    }
    
    @Override
    public ChannelMetadata metadata() {
        return AbstractEpollChannel.METADATA;
    }
    
    @Override
    protected void doClose() throws Exception {
        this.active = false;
        this.inputClosedSeenErrorOnRead = true;
        try {
            this.doDeregister();
        }
        finally {
            this.fileDescriptor.close();
        }
    }
    
    @Override
    protected void doDisconnect() throws Exception {
        this.doClose();
    }
    
    @Override
    protected boolean isCompatible(final EventLoop loop) {
        return loop instanceof EpollEventLoop;
    }
    
    @Override
    public boolean isOpen() {
        return this.fileDescriptor.isOpen();
    }
    
    @Override
    protected void doDeregister() throws Exception {
        ((EpollEventLoop)this.eventLoop()).remove(this);
    }
    
    @Override
    protected final void doBeginRead() throws Exception {
        final AbstractEpollUnsafe unsafe = (AbstractEpollUnsafe)this.unsafe();
        unsafe.readPending = true;
        this.setFlag(this.readFlag);
        if (unsafe.maybeMoreDataToRead) {
            unsafe.executeEpollInReadyRunnable(this.config());
        }
    }
    
    final boolean shouldBreakEpollInReady(final ChannelConfig config) {
        return this.fileDescriptor.isInputShutdown() && (this.inputClosedSeenErrorOnRead || !this.isAllowHalfClosure(config));
    }
    
    final boolean isAllowHalfClosure(final ChannelConfig config) {
        return config instanceof EpollSocketChannelConfig && ((EpollSocketChannelConfig)config).isAllowHalfClosure();
    }
    
    final void clearEpollIn() {
        if (this.isRegistered()) {
            final EventLoop loop = this.eventLoop();
            final AbstractEpollUnsafe unsafe = (AbstractEpollUnsafe)this.unsafe();
            if (loop.inEventLoop()) {
                unsafe.clearEpollIn0();
            }
            else {
                loop.execute(new Runnable() {
                    @Override
                    public void run() {
                        if (!unsafe.readPending && !AbstractEpollChannel.this.config().isAutoRead()) {
                            unsafe.clearEpollIn0();
                        }
                    }
                });
            }
        }
        else {
            this.flags &= ~this.readFlag;
        }
    }
    
    private void modifyEvents() throws IOException {
        if (this.isOpen() && this.isRegistered()) {
            ((EpollEventLoop)this.eventLoop()).modify(this);
        }
    }
    
    @Override
    protected void doRegister() throws Exception {
        this.epollInReadyRunnablePending = false;
        ((EpollEventLoop)this.eventLoop()).add(this);
    }
    
    @Override
    protected abstract AbstractEpollUnsafe newUnsafe();
    
    protected final ByteBuf newDirectBuffer(final ByteBuf buf) {
        return this.newDirectBuffer(buf, buf);
    }
    
    protected final ByteBuf newDirectBuffer(final Object holder, final ByteBuf buf) {
        final int readableBytes = buf.readableBytes();
        if (readableBytes == 0) {
            ReferenceCountUtil.safeRelease(holder);
            return Unpooled.EMPTY_BUFFER;
        }
        final ByteBufAllocator alloc = this.alloc();
        if (alloc.isDirectBufferPooled()) {
            return newDirectBuffer0(holder, buf, alloc, readableBytes);
        }
        final ByteBuf directBuf = ByteBufUtil.threadLocalDirectBuffer();
        if (directBuf == null) {
            return newDirectBuffer0(holder, buf, alloc, readableBytes);
        }
        directBuf.writeBytes(buf, buf.readerIndex(), readableBytes);
        ReferenceCountUtil.safeRelease(holder);
        return directBuf;
    }
    
    private static ByteBuf newDirectBuffer0(final Object holder, final ByteBuf buf, final ByteBufAllocator alloc, final int capacity) {
        final ByteBuf directBuf = alloc.directBuffer(capacity);
        directBuf.writeBytes(buf, buf.readerIndex(), capacity);
        ReferenceCountUtil.safeRelease(holder);
        return directBuf;
    }
    
    protected static void checkResolvable(final InetSocketAddress addr) {
        if (addr.isUnresolved()) {
            throw new UnresolvedAddressException();
        }
    }
    
    protected final int doReadBytes(final ByteBuf byteBuf) throws Exception {
        final int writerIndex = byteBuf.writerIndex();
        this.unsafe().recvBufAllocHandle().attemptedBytesRead(byteBuf.writableBytes());
        int localReadAmount;
        if (byteBuf.hasMemoryAddress()) {
            localReadAmount = this.fileDescriptor.readAddress(byteBuf.memoryAddress(), writerIndex, byteBuf.capacity());
        }
        else {
            final ByteBuffer buf = byteBuf.internalNioBuffer(writerIndex, byteBuf.writableBytes());
            localReadAmount = this.fileDescriptor.read(buf, buf.position(), buf.limit());
        }
        if (localReadAmount > 0) {
            byteBuf.writerIndex(writerIndex + localReadAmount);
        }
        return localReadAmount;
    }
    
    protected final int doWriteBytes(final ByteBuf buf, final int writeSpinCount) throws Exception {
        final int readableBytes = buf.readableBytes();
        int writtenBytes = 0;
        if (buf.hasMemoryAddress()) {
            final long memoryAddress = buf.memoryAddress();
            int readerIndex = buf.readerIndex();
            final int writerIndex = buf.writerIndex();
            for (int i = writeSpinCount - 1; i >= 0; --i) {
                final int localFlushedAmount = this.fileDescriptor.writeAddress(memoryAddress, readerIndex, writerIndex);
                if (localFlushedAmount <= 0) {
                    break;
                }
                writtenBytes += localFlushedAmount;
                if (writtenBytes == readableBytes) {
                    return writtenBytes;
                }
                readerIndex += localFlushedAmount;
            }
        }
        else {
            ByteBuffer nioBuf;
            if (buf.nioBufferCount() == 1) {
                nioBuf = buf.internalNioBuffer(buf.readerIndex(), buf.readableBytes());
            }
            else {
                nioBuf = buf.nioBuffer();
            }
            for (int j = writeSpinCount - 1; j >= 0; --j) {
                final int pos = nioBuf.position();
                final int limit = nioBuf.limit();
                final int localFlushedAmount2 = this.fileDescriptor.write(nioBuf, pos, limit);
                if (localFlushedAmount2 <= 0) {
                    break;
                }
                nioBuf.position(pos + localFlushedAmount2);
                writtenBytes += localFlushedAmount2;
                if (writtenBytes == readableBytes) {
                    return writtenBytes;
                }
            }
        }
        if (writtenBytes < readableBytes) {
            this.setFlag(Native.EPOLLOUT);
        }
        return writtenBytes;
    }
    
    static {
        METADATA = new ChannelMetadata(false);
    }
    
    protected abstract class AbstractEpollUnsafe extends AbstractUnsafe
    {
        boolean readPending;
        boolean maybeMoreDataToRead;
        private EpollRecvByteAllocatorHandle allocHandle;
        private final Runnable epollInReadyRunnable;
        
        protected AbstractEpollUnsafe() {
            this.epollInReadyRunnable = new Runnable() {
                @Override
                public void run() {
                    AbstractEpollChannel.this.epollInReadyRunnablePending = false;
                    AbstractEpollUnsafe.this.epollInReady();
                }
            };
        }
        
        abstract void epollInReady();
        
        final void epollInBefore() {
            this.maybeMoreDataToRead = false;
        }
        
        final void epollInFinally(final ChannelConfig config) {
            this.maybeMoreDataToRead = (this.allocHandle.isEdgeTriggered() && this.allocHandle.maybeMoreDataToRead());
            if (!this.readPending && !config.isAutoRead()) {
                AbstractEpollChannel.this.clearEpollIn();
            }
            else if (this.readPending && this.maybeMoreDataToRead) {
                this.executeEpollInReadyRunnable(config);
            }
        }
        
        final void executeEpollInReadyRunnable(final ChannelConfig config) {
            if (AbstractEpollChannel.this.epollInReadyRunnablePending || !AbstractEpollChannel.this.isActive() || AbstractEpollChannel.this.shouldBreakEpollInReady(config)) {
                return;
            }
            AbstractEpollChannel.this.epollInReadyRunnablePending = true;
            AbstractEpollChannel.this.eventLoop().execute(this.epollInReadyRunnable);
        }
        
        final void epollRdHupReady() {
            this.recvBufAllocHandle().receivedRdHup();
            if (AbstractEpollChannel.this.isActive()) {
                this.epollInReady();
            }
            else {
                this.shutdownInput(true);
            }
            this.clearEpollRdHup();
        }
        
        private void clearEpollRdHup() {
            try {
                AbstractEpollChannel.this.clearFlag(Native.EPOLLRDHUP);
            }
            catch (final IOException e) {
                AbstractEpollChannel.this.pipeline().fireExceptionCaught((Throwable)e);
                this.close(this.voidPromise());
            }
        }
        
        void shutdownInput(final boolean rdHup) {
            if (!AbstractEpollChannel.this.fd().isInputShutdown()) {
                if (AbstractEpollChannel.this.isAllowHalfClosure(AbstractEpollChannel.this.config())) {
                    try {
                        AbstractEpollChannel.this.fd().shutdown(true, false);
                    }
                    catch (final IOException ignored) {
                        this.fireEventAndClose(ChannelInputShutdownEvent.INSTANCE);
                        return;
                    }
                    catch (final NotYetConnectedException ignore) {
                        this.fireEventAndClose(ChannelInputShutdownEvent.INSTANCE);
                        return;
                    }
                    AbstractEpollChannel.this.pipeline().fireUserEventTriggered((Object)ChannelInputShutdownEvent.INSTANCE);
                }
                else {
                    this.close(this.voidPromise());
                }
            }
            else if (!rdHup) {
                AbstractEpollChannel.this.inputClosedSeenErrorOnRead = true;
                AbstractEpollChannel.this.pipeline().fireUserEventTriggered((Object)ChannelInputShutdownReadComplete.INSTANCE);
            }
        }
        
        private void fireEventAndClose(final Object evt) {
            AbstractEpollChannel.this.pipeline().fireUserEventTriggered(evt);
            this.close(this.voidPromise());
        }
        
        @Override
        public EpollRecvByteAllocatorHandle recvBufAllocHandle() {
            if (this.allocHandle == null) {
                this.allocHandle = this.newEpollHandle((RecvByteBufAllocator.ExtendedHandle)super.recvBufAllocHandle());
            }
            return this.allocHandle;
        }
        
        EpollRecvByteAllocatorHandle newEpollHandle(final RecvByteBufAllocator.ExtendedHandle handle) {
            return new EpollRecvByteAllocatorHandle(handle);
        }
        
        @Override
        protected void flush0() {
            if (AbstractEpollChannel.this.isFlagSet(Native.EPOLLOUT)) {
                return;
            }
            super.flush0();
        }
        
        void epollOutReady() {
            if (AbstractEpollChannel.this.fd().isOutputShutdown()) {
                return;
            }
            super.flush0();
        }
        
        protected final void clearEpollIn0() {
            assert AbstractEpollChannel.this.eventLoop().inEventLoop();
            try {
                this.readPending = false;
                AbstractEpollChannel.this.clearFlag(AbstractEpollChannel.this.readFlag);
            }
            catch (final IOException e) {
                AbstractEpollChannel.this.pipeline().fireExceptionCaught((Throwable)e);
                AbstractEpollChannel.this.unsafe().close(AbstractEpollChannel.this.unsafe().voidPromise());
            }
        }
    }
}
