// 
// Decompiled by Procyon v0.6.0
// 

package org.apache.logging.log4j.core.appender.rolling;

import org.apache.logging.log4j.core.appender.ConfigurationFactoryData;
import org.apache.logging.log4j.core.util.NullOutputStream;
import org.apache.logging.log4j.core.util.FileUtils;
import java.io.File;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.appender.AppenderLoggingException;
import org.apache.logging.log4j.core.appender.ManagerFactory;
import org.apache.logging.log4j.core.appender.OutputStreamManager;
import org.apache.logging.log4j.core.config.Configuration;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.io.Serializable;
import org.apache.logging.log4j.core.Layout;
import java.io.OutputStream;
import org.apache.logging.log4j.core.LoggerContext;
import java.io.RandomAccessFile;

public class RollingRandomAccessFileManager extends RollingFileManager
{
    public static final int DEFAULT_BUFFER_SIZE = 262144;
    private static final RollingRandomAccessFileManagerFactory FACTORY;
    private RandomAccessFile randomAccessFile;
    private final ThreadLocal<Boolean> isEndOfBatch;
    
    public RollingRandomAccessFileManager(final LoggerContext loggerContext, final RandomAccessFile raf, final String fileName, final String pattern, final OutputStream os, final boolean append, final boolean immediateFlush, final int bufferSize, final long size, final long time, final TriggeringPolicy policy, final RolloverStrategy strategy, final String advertiseURI, final Layout<? extends Serializable> layout, final boolean writeHeader) {
        super(loggerContext, fileName, pattern, os, append, false, size, time, policy, strategy, advertiseURI, layout, writeHeader, ByteBuffer.wrap(new byte[bufferSize]));
        this.isEndOfBatch = new ThreadLocal<Boolean>();
        this.randomAccessFile = raf;
        this.isEndOfBatch.set(Boolean.FALSE);
        this.writeHeader();
    }
    
    private void writeHeader() {
        if (this.layout == null) {
            return;
        }
        final byte[] header = this.layout.getHeader();
        if (header == null) {
            return;
        }
        try {
            if (this.randomAccessFile.length() == 0L) {
                this.randomAccessFile.write(header, 0, header.length);
            }
        }
        catch (final IOException e) {
            this.logError("Unable to write header", e);
        }
    }
    
    public static RollingRandomAccessFileManager getRollingRandomAccessFileManager(final String fileName, final String filePattern, final boolean isAppend, final boolean immediateFlush, final int bufferSize, final TriggeringPolicy policy, final RolloverStrategy strategy, final String advertiseURI, final Layout<? extends Serializable> layout, final Configuration configuration) {
        return (RollingRandomAccessFileManager)OutputStreamManager.getManager(fileName, new FactoryData(filePattern, isAppend, immediateFlush, bufferSize, policy, strategy, advertiseURI, layout, configuration), RollingRandomAccessFileManager.FACTORY);
    }
    
    public Boolean isEndOfBatch() {
        return this.isEndOfBatch.get();
    }
    
    public void setEndOfBatch(final boolean endOfBatch) {
        this.isEndOfBatch.set(endOfBatch);
    }
    
    @Override
    protected synchronized void write(final byte[] bytes, final int offset, final int length, final boolean immediateFlush) {
        super.write(bytes, offset, length, immediateFlush);
    }
    
    @Override
    protected synchronized void writeToDestination(final byte[] bytes, final int offset, final int length) {
        try {
            this.randomAccessFile.write(bytes, offset, length);
            this.size += length;
        }
        catch (final IOException ex) {
            final String msg = "Error writing to RandomAccessFile " + this.getName();
            throw new AppenderLoggingException(msg, ex);
        }
    }
    
    @Override
    protected void createFileAfterRollover() throws IOException {
        this.randomAccessFile = new RandomAccessFile(this.getFileName(), "rw");
        if (this.isAppend()) {
            this.randomAccessFile.seek(this.randomAccessFile.length());
        }
        this.writeHeader();
    }
    
    @Override
    public synchronized void flush() {
        this.flushBuffer(this.byteBuffer);
    }
    
    public synchronized boolean closeOutputStream() {
        this.flush();
        try {
            this.randomAccessFile.close();
            return true;
        }
        catch (final IOException e) {
            this.logError("Unable to close RandomAccessFile", e);
            return false;
        }
    }
    
    @Override
    public int getBufferSize() {
        return this.byteBuffer.capacity();
    }
    
    @Override
    public void updateData(final Object data) {
        final FactoryData factoryData = (FactoryData)data;
        this.setRolloverStrategy(factoryData.getRolloverStrategy());
        this.setTriggeringPolicy(factoryData.getTriggeringPolicy());
    }
    
    static {
        FACTORY = new RollingRandomAccessFileManagerFactory();
    }
    
    private static class RollingRandomAccessFileManagerFactory implements ManagerFactory<RollingRandomAccessFileManager, FactoryData>
    {
        @Override
        public RollingRandomAccessFileManager createManager(final String name, final FactoryData data) {
            final File file = new File(name);
            if (!data.append) {
                file.delete();
            }
            final long size = data.append ? file.length() : 0L;
            final long time = file.exists() ? file.lastModified() : System.currentTimeMillis();
            final boolean writeHeader = !data.append || !file.exists();
            RandomAccessFile raf = null;
            try {
                FileUtils.makeParentDirs(file);
                raf = new RandomAccessFile(name, "rw");
                if (data.append) {
                    final long length = raf.length();
                    RollingRandomAccessFileManager.LOGGER.trace("RandomAccessFile {} seek to {}", name, length);
                    raf.seek(length);
                }
                else {
                    RollingRandomAccessFileManager.LOGGER.trace("RandomAccessFile {} set length to 0", name);
                    raf.setLength(0L);
                }
                return new RollingRandomAccessFileManager(data.getLoggerContext(), raf, name, data.pattern, NullOutputStream.getInstance(), data.append, data.immediateFlush, data.bufferSize, size, time, data.policy, data.strategy, data.advertiseURI, data.layout, writeHeader);
            }
            catch (final IOException ex) {
                RollingRandomAccessFileManager.LOGGER.error("Cannot access RandomAccessFile " + ex, ex);
                if (raf != null) {
                    try {
                        raf.close();
                    }
                    catch (final IOException e) {
                        RollingRandomAccessFileManager.LOGGER.error("Cannot close RandomAccessFile {}", name, e);
                    }
                }
                return null;
            }
        }
    }
    
    private static class FactoryData extends ConfigurationFactoryData
    {
        private final String pattern;
        private final boolean append;
        private final boolean immediateFlush;
        private final int bufferSize;
        private final TriggeringPolicy policy;
        private final RolloverStrategy strategy;
        private final String advertiseURI;
        private final Layout<? extends Serializable> layout;
        
        public FactoryData(final String pattern, final boolean append, final boolean immediateFlush, final int bufferSize, final TriggeringPolicy policy, final RolloverStrategy strategy, final String advertiseURI, final Layout<? extends Serializable> layout, final Configuration configuration) {
            super(configuration);
            this.pattern = pattern;
            this.append = append;
            this.immediateFlush = immediateFlush;
            this.bufferSize = bufferSize;
            this.policy = policy;
            this.strategy = strategy;
            this.advertiseURI = advertiseURI;
            this.layout = layout;
        }
        
        public TriggeringPolicy getTriggeringPolicy() {
            return this.policy;
        }
        
        public RolloverStrategy getRolloverStrategy() {
            return this.strategy;
        }
    }
}
