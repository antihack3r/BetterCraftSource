// 
// Decompiled by Procyon v0.6.0
// 

package io.netty.handler.codec.http.multipart;

import java.nio.charset.Charset;
import java.io.InputStream;
import java.io.File;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import io.netty.buffer.ByteBufHolder;

public interface HttpData extends InterfaceHttpData, ByteBufHolder
{
    long getMaxSize();
    
    void setMaxSize(final long p0);
    
    void checkSize(final long p0) throws IOException;
    
    void setContent(final ByteBuf p0) throws IOException;
    
    void addContent(final ByteBuf p0, final boolean p1) throws IOException;
    
    void setContent(final File p0) throws IOException;
    
    void setContent(final InputStream p0) throws IOException;
    
    boolean isCompleted();
    
    long length();
    
    long definedLength();
    
    void delete();
    
    byte[] get() throws IOException;
    
    ByteBuf getByteBuf() throws IOException;
    
    ByteBuf getChunk(final int p0) throws IOException;
    
    String getString() throws IOException;
    
    String getString(final Charset p0) throws IOException;
    
    void setCharset(final Charset p0);
    
    Charset getCharset();
    
    boolean renameTo(final File p0) throws IOException;
    
    boolean isInMemory();
    
    File getFile() throws IOException;
    
    HttpData copy();
    
    HttpData duplicate();
    
    HttpData retainedDuplicate();
    
    HttpData replace(final ByteBuf p0);
    
    HttpData retain();
    
    HttpData retain(final int p0);
    
    HttpData touch();
    
    HttpData touch(final Object p0);
}
