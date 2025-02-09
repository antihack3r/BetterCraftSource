// 
// Decompiled by Procyon v0.6.0
// 

package io.netty.handler.codec.compression;

import net.jpountz.lz4.LZ4Exception;
import java.util.List;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import java.util.zip.Checksum;
import net.jpountz.xxhash.XXHashFactory;
import net.jpountz.lz4.LZ4Factory;
import net.jpountz.lz4.LZ4FastDecompressor;
import io.netty.handler.codec.ByteToMessageDecoder;

public class Lz4FrameDecoder extends ByteToMessageDecoder
{
    private State currentState;
    private LZ4FastDecompressor decompressor;
    private ByteBufChecksum checksum;
    private int blockType;
    private int compressedLength;
    private int decompressedLength;
    private int currentChecksum;
    
    public Lz4FrameDecoder() {
        this(false);
    }
    
    public Lz4FrameDecoder(final boolean validateChecksums) {
        this(LZ4Factory.fastestInstance(), validateChecksums);
    }
    
    public Lz4FrameDecoder(final LZ4Factory factory, final boolean validateChecksums) {
        this(factory, validateChecksums ? XXHashFactory.fastestInstance().newStreamingHash32(-1756908916).asChecksum() : null);
    }
    
    public Lz4FrameDecoder(final LZ4Factory factory, final Checksum checksum) {
        this.currentState = State.INIT_BLOCK;
        if (factory == null) {
            throw new NullPointerException("factory");
        }
        this.decompressor = factory.fastDecompressor();
        this.checksum = ((checksum == null) ? null : ByteBufChecksum.wrapChecksum(checksum));
    }
    
    @Override
    protected void decode(final ChannelHandlerContext ctx, final ByteBuf in, final List<Object> out) throws Exception {
        try {
            switch (this.currentState) {
                case INIT_BLOCK: {
                    if (in.readableBytes() < 21) {
                        break;
                    }
                    final long magic = in.readLong();
                    if (magic != 5501767354678207339L) {
                        throw new DecompressionException("unexpected block identifier");
                    }
                    final int token = in.readByte();
                    final int compressionLevel = (token & 0xF) + 10;
                    final int blockType = token & 0xF0;
                    final int compressedLength = Integer.reverseBytes(in.readInt());
                    if (compressedLength < 0 || compressedLength > 33554432) {
                        throw new DecompressionException(String.format("invalid compressedLength: %d (expected: 0-%d)", compressedLength, 33554432));
                    }
                    final int decompressedLength = Integer.reverseBytes(in.readInt());
                    final int maxDecompressedLength = 1 << compressionLevel;
                    if (decompressedLength < 0 || decompressedLength > maxDecompressedLength) {
                        throw new DecompressionException(String.format("invalid decompressedLength: %d (expected: 0-%d)", decompressedLength, maxDecompressedLength));
                    }
                    if ((decompressedLength == 0 && compressedLength != 0) || (decompressedLength != 0 && compressedLength == 0) || (blockType == 16 && decompressedLength != compressedLength)) {
                        throw new DecompressionException(String.format("stream corrupted: compressedLength(%d) and decompressedLength(%d) mismatch", compressedLength, decompressedLength));
                    }
                    final int currentChecksum = Integer.reverseBytes(in.readInt());
                    if (decompressedLength != 0 || compressedLength != 0) {
                        this.blockType = blockType;
                        this.compressedLength = compressedLength;
                        this.decompressedLength = decompressedLength;
                        this.currentChecksum = currentChecksum;
                        this.currentState = State.DECOMPRESS_DATA;
                    }
                    if (currentChecksum != 0) {
                        throw new DecompressionException("stream corrupted: checksum error");
                    }
                    this.currentState = State.FINISHED;
                    this.decompressor = null;
                    this.checksum = null;
                    break;
                }
                case DECOMPRESS_DATA: {
                    final int blockType = this.blockType;
                    final int compressedLength = this.compressedLength;
                    final int decompressedLength = this.decompressedLength;
                    final int currentChecksum = this.currentChecksum;
                    if (in.readableBytes() < compressedLength) {
                        break;
                    }
                    final ByteBufChecksum checksum = this.checksum;
                    ByteBuf uncompressed = null;
                    try {
                        switch (blockType) {
                            case 16: {
                                uncompressed = in.retainedSlice(in.readerIndex(), decompressedLength);
                                break;
                            }
                            case 32: {
                                uncompressed = ctx.alloc().buffer(decompressedLength, decompressedLength);
                                this.decompressor.decompress(CompressionUtil.safeNioBuffer(in), uncompressed.internalNioBuffer(uncompressed.writerIndex(), decompressedLength));
                                uncompressed.writerIndex(uncompressed.writerIndex() + decompressedLength);
                                break;
                            }
                            default: {
                                throw new DecompressionException(String.format("unexpected blockType: %d (expected: %d or %d)", blockType, 16, 32));
                            }
                        }
                        in.skipBytes(compressedLength);
                        if (checksum != null) {
                            CompressionUtil.checkChecksum(checksum, uncompressed, currentChecksum);
                        }
                        out.add(uncompressed);
                        uncompressed = null;
                        this.currentState = State.INIT_BLOCK;
                    }
                    catch (final LZ4Exception e) {
                        throw new DecompressionException((Throwable)e);
                    }
                    finally {
                        if (uncompressed != null) {
                            uncompressed.release();
                        }
                    }
                    break;
                }
                case FINISHED:
                case CORRUPTED: {
                    in.skipBytes(in.readableBytes());
                    break;
                }
                default: {
                    throw new IllegalStateException();
                }
            }
        }
        catch (final Exception e2) {
            this.currentState = State.CORRUPTED;
            throw e2;
        }
    }
    
    public boolean isClosed() {
        return this.currentState == State.FINISHED;
    }
    
    private enum State
    {
        INIT_BLOCK, 
        DECOMPRESS_DATA, 
        FINISHED, 
        CORRUPTED;
    }
}
