/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.core_implementation.mc18.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufProcessor;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;
import net.labymod.labyconnect.packets.PacketBuf;

public class PacketBufOld
extends PacketBuf {
    public PacketBufOld(ByteBuf buf) {
        super(buf);
    }

    @Override
    public int refCnt() {
        return this.buf.refCnt();
    }

    @Override
    public boolean release() {
        return this.buf.release();
    }

    @Override
    public boolean release(int arg0) {
        return this.buf.release(arg0);
    }

    @Override
    public ByteBufAllocator alloc() {
        return this.buf.alloc();
    }

    @Override
    public byte[] array() {
        return this.buf.array();
    }

    @Override
    public int arrayOffset() {
        return this.buf.arrayOffset();
    }

    @Override
    public int bytesBefore(byte arg0) {
        return this.buf.bytesBefore(arg0);
    }

    @Override
    public int bytesBefore(int arg0, byte arg1) {
        return this.buf.bytesBefore(arg0, arg1);
    }

    @Override
    public int bytesBefore(int arg0, int arg1, byte arg2) {
        return this.buf.bytesBefore(arg0, arg1, arg2);
    }

    @Override
    public int capacity() {
        return this.buf.capacity();
    }

    @Override
    public ByteBuf capacity(int arg0) {
        return this.buf.capacity(arg0);
    }

    @Override
    public ByteBuf clear() {
        return this.buf.clear();
    }

    @Override
    public int compareTo(ByteBuf arg0) {
        return this.buf.compareTo(arg0);
    }

    @Override
    public ByteBuf copy() {
        return this.buf.copy();
    }

    @Override
    public ByteBuf copy(int arg0, int arg1) {
        return this.buf.copy(arg0, arg1);
    }

    @Override
    public ByteBuf discardReadBytes() {
        return this.buf.discardReadBytes();
    }

    @Override
    public ByteBuf discardSomeReadBytes() {
        return this.buf.discardSomeReadBytes();
    }

    @Override
    public ByteBuf duplicate() {
        return this.buf.duplicate();
    }

    @Override
    public ByteBuf ensureWritable(int arg0) {
        return this.buf.ensureWritable(arg0);
    }

    @Override
    public int ensureWritable(int arg0, boolean arg1) {
        return this.buf.ensureWritable(arg0, arg1);
    }

    @Override
    public boolean equals(Object arg0) {
        return this.buf.equals(arg0);
    }

    @Override
    public int forEachByte(ByteBufProcessor arg0) {
        return this.buf.forEachByte(arg0);
    }

    @Override
    public int forEachByte(int arg0, int arg1, ByteBufProcessor arg2) {
        return this.buf.forEachByte(arg0, arg1, arg2);
    }

    @Override
    public int forEachByteDesc(ByteBufProcessor arg0) {
        return this.buf.forEachByteDesc(arg0);
    }

    @Override
    public int forEachByteDesc(int arg0, int arg1, ByteBufProcessor arg2) {
        return this.buf.forEachByteDesc(arg0, arg1, arg2);
    }

    @Override
    public boolean getBoolean(int arg0) {
        return this.buf.getBoolean(arg0);
    }

    @Override
    public byte getByte(int arg0) {
        return this.buf.getByte(arg0);
    }

    @Override
    public ByteBuf getBytes(int arg0, ByteBuf arg1) {
        return this.buf.getBytes(arg0, arg1);
    }

    @Override
    public ByteBuf getBytes(int arg0, byte[] arg1) {
        return this.buf.getBytes(arg0, arg1);
    }

    @Override
    public ByteBuf getBytes(int arg0, ByteBuffer arg1) {
        return this.buf.getBytes(arg0, arg1);
    }

    @Override
    public ByteBuf getBytes(int arg0, ByteBuf arg1, int arg2) {
        return this.buf.getBytes(arg0, arg1, arg2);
    }

    @Override
    public ByteBuf getBytes(int arg0, OutputStream arg1, int arg2) throws IOException {
        return this.buf.getBytes(arg0, arg1, arg2);
    }

    @Override
    public int getBytes(int arg0, GatheringByteChannel arg1, int arg2) throws IOException {
        return this.buf.getBytes(arg0, arg1, arg2);
    }

    @Override
    public ByteBuf getBytes(int arg0, ByteBuf arg1, int arg2, int arg3) {
        return this.buf.getBytes(arg0, arg1, arg2, arg3);
    }

    @Override
    public ByteBuf getBytes(int arg0, byte[] arg1, int arg2, int arg3) {
        return this.buf.getBytes(arg0, arg1, arg2, arg3);
    }

    @Override
    public char getChar(int arg0) {
        return this.buf.getChar(arg0);
    }

    @Override
    public double getDouble(int arg0) {
        return this.buf.getDouble(arg0);
    }

    @Override
    public float getFloat(int arg0) {
        return this.buf.getFloat(arg0);
    }

    @Override
    public int getInt(int arg0) {
        return this.buf.getInt(arg0);
    }

    @Override
    public long getLong(int arg0) {
        return this.buf.getLong(arg0);
    }

    @Override
    public int getMedium(int arg0) {
        return this.buf.getMedium(arg0);
    }

    @Override
    public short getShort(int arg0) {
        return this.buf.getShort(arg0);
    }

    @Override
    public short getUnsignedByte(int arg0) {
        return this.buf.getUnsignedByte(arg0);
    }

    @Override
    public long getUnsignedInt(int arg0) {
        return this.buf.getUnsignedInt(arg0);
    }

    @Override
    public int getUnsignedMedium(int arg0) {
        return this.buf.getUnsignedMedium(arg0);
    }

    @Override
    public int getUnsignedShort(int arg0) {
        return this.buf.getUnsignedShort(arg0);
    }

    @Override
    public boolean hasArray() {
        return this.buf.hasArray();
    }

    @Override
    public boolean hasMemoryAddress() {
        return this.buf.hasMemoryAddress();
    }

    @Override
    public int hashCode() {
        return this.buf.hashCode();
    }

    @Override
    public int indexOf(int arg0, int arg1, byte arg2) {
        return this.buf.indexOf(arg0, arg1, arg2);
    }

    @Override
    public ByteBuffer internalNioBuffer(int arg0, int arg1) {
        return this.buf.internalNioBuffer(arg0, arg1);
    }

    @Override
    public boolean isDirect() {
        return this.buf.isDirect();
    }

    @Override
    public boolean isReadable() {
        return this.buf.isReadable();
    }

    @Override
    public boolean isReadable(int arg0) {
        return this.buf.isReadable(arg0);
    }

    @Override
    public boolean isWritable() {
        return this.buf.isWritable();
    }

    @Override
    public boolean isWritable(int arg0) {
        return this.buf.isWritable(arg0);
    }

    @Override
    public ByteBuf markReaderIndex() {
        return this.buf.markReaderIndex();
    }

    @Override
    public ByteBuf markWriterIndex() {
        return this.buf.markWriterIndex();
    }

    @Override
    public int maxCapacity() {
        return this.buf.maxCapacity();
    }

    @Override
    public int maxWritableBytes() {
        return this.buf.maxWritableBytes();
    }

    @Override
    public long memoryAddress() {
        return this.buf.memoryAddress();
    }

    @Override
    public ByteBuffer nioBuffer() {
        return this.buf.nioBuffer();
    }

    @Override
    public ByteBuffer nioBuffer(int arg0, int arg1) {
        return this.buf.nioBuffer(arg0, arg1);
    }

    @Override
    public int nioBufferCount() {
        return this.buf.nioBufferCount();
    }

    @Override
    public ByteBuffer[] nioBuffers() {
        return this.buf.nioBuffers();
    }

    @Override
    public ByteBuffer[] nioBuffers(int arg0, int arg1) {
        return this.buf.nioBuffers(arg0, arg1);
    }

    @Override
    public ByteOrder order() {
        return this.buf.order();
    }

    @Override
    public ByteBuf order(ByteOrder arg0) {
        return this.buf.order(arg0);
    }

    @Override
    public boolean readBoolean() {
        return this.buf.readBoolean();
    }

    @Override
    public byte readByte() {
        return this.buf.readByte();
    }

    @Override
    public ByteBuf readBytes(int arg0) {
        return this.buf.readBytes(arg0);
    }

    @Override
    public ByteBuf readBytes(ByteBuf arg0) {
        return this.buf.readBytes(arg0);
    }

    @Override
    public ByteBuf readBytes(byte[] arg0) {
        return this.buf.readBytes(arg0);
    }

    @Override
    public ByteBuf readBytes(ByteBuffer arg0) {
        return this.buf.readBytes(arg0);
    }

    @Override
    public ByteBuf readBytes(ByteBuf arg0, int arg1) {
        return this.buf.readBytes(arg0, arg1);
    }

    @Override
    public ByteBuf readBytes(OutputStream arg0, int arg1) throws IOException {
        return this.buf.readBytes(arg0, arg1);
    }

    @Override
    public int readBytes(GatheringByteChannel arg0, int arg1) throws IOException {
        return this.buf.readBytes(arg0, arg1);
    }

    @Override
    public ByteBuf readBytes(ByteBuf arg0, int arg1, int arg2) {
        return this.buf.readBytes(arg0, arg1, arg2);
    }

    @Override
    public ByteBuf readBytes(byte[] arg0, int arg1, int arg2) {
        return this.buf.readBytes(arg0, arg1, arg2);
    }

    @Override
    public char readChar() {
        return this.buf.readChar();
    }

    @Override
    public double readDouble() {
        return this.buf.readDouble();
    }

    @Override
    public float readFloat() {
        return this.buf.readFloat();
    }

    @Override
    public int readInt() {
        return this.buf.readInt();
    }

    @Override
    public long readLong() {
        return this.buf.readLong();
    }

    @Override
    public int readMedium() {
        return this.buf.readMedium();
    }

    @Override
    public short readShort() {
        return this.buf.readShort();
    }

    @Override
    public ByteBuf readSlice(int arg0) {
        return this.buf.readSlice(arg0);
    }

    @Override
    public short readUnsignedByte() {
        return this.buf.readUnsignedByte();
    }

    @Override
    public long readUnsignedInt() {
        return this.buf.readUnsignedInt();
    }

    @Override
    public int readUnsignedMedium() {
        return this.buf.readUnsignedMedium();
    }

    @Override
    public int readUnsignedShort() {
        return this.buf.readUnsignedShort();
    }

    @Override
    public int readableBytes() {
        return this.buf.readableBytes();
    }

    @Override
    public int readerIndex() {
        return this.buf.readerIndex();
    }

    @Override
    public ByteBuf readerIndex(int arg0) {
        return this.buf.readerIndex(arg0);
    }

    @Override
    public ByteBuf resetReaderIndex() {
        return this.buf.resetReaderIndex();
    }

    @Override
    public ByteBuf resetWriterIndex() {
        return this.buf.resetWriterIndex();
    }

    @Override
    public ByteBuf retain() {
        return this.buf.retain();
    }

    @Override
    public ByteBuf retain(int arg0) {
        return this.buf.retain(arg0);
    }

    @Override
    public ByteBuf setBoolean(int arg0, boolean arg1) {
        return this.buf.setBoolean(arg0, arg1);
    }

    @Override
    public ByteBuf setByte(int arg0, int arg1) {
        return this.buf.setByte(arg0, arg1);
    }

    @Override
    public ByteBuf setBytes(int arg0, ByteBuf arg1) {
        return this.buf.setBytes(arg0, arg1);
    }

    @Override
    public ByteBuf setBytes(int arg0, byte[] arg1) {
        return this.buf.setBytes(arg0, arg1);
    }

    @Override
    public ByteBuf setBytes(int arg0, ByteBuffer arg1) {
        return this.buf.setBytes(arg0, arg1);
    }

    @Override
    public ByteBuf setBytes(int arg0, ByteBuf arg1, int arg2) {
        return this.buf.setBytes(arg0, arg1, arg2);
    }

    @Override
    public int setBytes(int arg0, InputStream arg1, int arg2) throws IOException {
        return this.buf.setBytes(arg0, arg1, arg2);
    }

    @Override
    public int setBytes(int arg0, ScatteringByteChannel arg1, int arg2) throws IOException {
        return this.buf.setBytes(arg0, arg1, arg2);
    }

    @Override
    public ByteBuf setBytes(int arg0, ByteBuf arg1, int arg2, int arg3) {
        return this.buf.setBytes(arg0, arg1, arg2, arg3);
    }

    @Override
    public ByteBuf setBytes(int arg0, byte[] arg1, int arg2, int arg3) {
        return this.buf.setBytes(arg0, arg1, arg2, arg3);
    }

    @Override
    public ByteBuf setChar(int arg0, int arg1) {
        return this.buf.setChar(arg0, arg1);
    }

    @Override
    public ByteBuf setDouble(int arg0, double arg1) {
        return this.buf.setDouble(arg0, arg1);
    }

    @Override
    public ByteBuf setFloat(int arg0, float arg1) {
        return this.buf.setFloat(arg0, arg1);
    }

    @Override
    public ByteBuf setIndex(int arg0, int arg1) {
        return this.buf.setIndex(arg0, arg1);
    }

    @Override
    public ByteBuf setInt(int arg0, int arg1) {
        return this.buf.setInt(arg0, arg1);
    }

    @Override
    public ByteBuf setLong(int arg0, long arg1) {
        return this.buf.setLong(arg0, arg1);
    }

    @Override
    public ByteBuf setMedium(int arg0, int arg1) {
        return this.buf.setMedium(arg0, arg1);
    }

    @Override
    public ByteBuf setShort(int arg0, int arg1) {
        return this.buf.setShort(arg0, arg1);
    }

    @Override
    public ByteBuf setZero(int arg0, int arg1) {
        return this.buf.setZero(arg0, arg1);
    }

    @Override
    public ByteBuf skipBytes(int arg0) {
        return this.buf.skipBytes(arg0);
    }

    @Override
    public ByteBuf slice() {
        return this.buf.slice();
    }

    @Override
    public ByteBuf slice(int arg0, int arg1) {
        return this.buf.slice(arg0, arg1);
    }

    @Override
    public String toString() {
        return this.buf.toString();
    }

    @Override
    public String toString(Charset arg0) {
        return this.buf.toString(arg0);
    }

    @Override
    public String toString(int arg0, int arg1, Charset arg2) {
        return this.buf.toString(arg0, arg1, arg2);
    }

    @Override
    public ByteBuf unwrap() {
        return this.buf.unwrap();
    }

    @Override
    public int writableBytes() {
        return this.buf.writableBytes();
    }

    @Override
    public ByteBuf writeBoolean(boolean arg0) {
        return this.buf.writeBoolean(arg0);
    }

    @Override
    public ByteBuf writeByte(int arg0) {
        return this.buf.writeByte(arg0);
    }

    @Override
    public ByteBuf writeBytes(ByteBuf arg0) {
        return this.buf.writeBytes(arg0);
    }

    @Override
    public ByteBuf writeBytes(byte[] arg0) {
        return this.buf.writeBytes(arg0);
    }

    @Override
    public ByteBuf writeBytes(ByteBuffer arg0) {
        return this.buf.writeBytes(arg0);
    }

    @Override
    public ByteBuf writeBytes(ByteBuf arg0, int arg1) {
        return this.buf.writeBytes(arg0, arg1);
    }

    @Override
    public int writeBytes(InputStream arg0, int arg1) throws IOException {
        return this.buf.writeBytes(arg0, arg1);
    }

    @Override
    public int writeBytes(ScatteringByteChannel arg0, int arg1) throws IOException {
        return this.buf.writeBytes(arg0, arg1);
    }

    @Override
    public ByteBuf writeBytes(ByteBuf arg0, int arg1, int arg2) {
        return this.buf.writeBytes(arg0, arg1, arg2);
    }

    @Override
    public ByteBuf writeBytes(byte[] arg0, int arg1, int arg2) {
        return this.buf.writeBytes(arg0, arg1, arg2);
    }

    @Override
    public ByteBuf writeChar(int arg0) {
        return this.buf.writeChar(arg0);
    }

    @Override
    public ByteBuf writeDouble(double arg0) {
        return this.buf.writeDouble(arg0);
    }

    @Override
    public ByteBuf writeFloat(float arg0) {
        return this.buf.writeFloat(arg0);
    }

    @Override
    public ByteBuf writeInt(int arg0) {
        return this.buf.writeInt(arg0);
    }

    @Override
    public ByteBuf writeLong(long arg0) {
        return this.buf.writeLong(arg0);
    }

    @Override
    public ByteBuf writeMedium(int arg0) {
        return this.buf.writeMedium(arg0);
    }

    @Override
    public ByteBuf writeShort(int arg0) {
        return this.buf.writeShort(arg0);
    }

    @Override
    public ByteBuf writeZero(int arg0) {
        return this.buf.writeZero(arg0);
    }

    @Override
    public int writerIndex() {
        return this.buf.writerIndex();
    }

    @Override
    public ByteBuf writerIndex(int arg0) {
        return this.buf.writerIndex(arg0);
    }
}

