// 
// Decompiled by Procyon v0.6.0
// 

package org.apache.logging.log4j.core.appender;

import java.util.Iterator;
import org.apache.logging.log4j.core.util.Log4jThread;
import org.apache.logging.log4j.core.config.plugins.PluginConfiguration;
import org.apache.logging.log4j.core.config.plugins.PluginAliases;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderAttribute;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.Required;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.util.Builder;
import org.apache.logging.log4j.core.AbstractLogEvent;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;
import org.apache.logging.log4j.core.async.ArrayBlockingQueueFactory;
import java.util.concurrent.TransferQueue;
import java.lang.annotation.Annotation;
import org.apache.logging.log4j.message.AsynchronouslyFormattable;
import org.apache.logging.log4j.core.util.Constants;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.core.async.EventRoute;
import org.apache.logging.log4j.core.impl.Log4jLogEvent;
import org.apache.logging.log4j.core.async.DiscardingAsyncQueueFullPolicy;
import java.util.concurrent.TimeUnit;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.core.async.AsyncQueueFullPolicyFactory;
import org.apache.logging.log4j.core.config.ConfigurationException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Appender;
import java.util.ArrayList;
import java.io.Serializable;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.async.BlockingQueueFactory;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.async.AsyncQueueFullPolicy;
import org.apache.logging.log4j.core.config.AppenderControl;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.Configuration;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;

@Plugin(name = "Async", category = "Core", elementType = "appender", printObject = true)
public final class AsyncAppender extends AbstractAppender
{
    private static final int DEFAULT_QUEUE_SIZE = 128;
    private static final LogEvent SHUTDOWN_LOG_EVENT;
    private static final AtomicLong THREAD_SEQUENCE;
    private final BlockingQueue<LogEvent> queue;
    private final int queueSize;
    private final boolean blocking;
    private final long shutdownTimeout;
    private final Configuration config;
    private final AppenderRef[] appenderRefs;
    private final String errorRef;
    private final boolean includeLocation;
    private AppenderControl errorAppender;
    private AsyncThread thread;
    private AsyncQueueFullPolicy asyncQueueFullPolicy;
    
    private AsyncAppender(final String name, final Filter filter, final AppenderRef[] appenderRefs, final String errorRef, final int queueSize, final boolean blocking, final boolean ignoreExceptions, final long shutdownTimeout, final Configuration config, final boolean includeLocation, final BlockingQueueFactory<LogEvent> blockingQueueFactory) {
        super(name, filter, null, ignoreExceptions);
        this.queue = blockingQueueFactory.create(queueSize);
        this.queueSize = queueSize;
        this.blocking = blocking;
        this.shutdownTimeout = shutdownTimeout;
        this.config = config;
        this.appenderRefs = appenderRefs;
        this.errorRef = errorRef;
        this.includeLocation = includeLocation;
    }
    
    @Override
    public void start() {
        final Map<String, Appender> map = this.config.getAppenders();
        final List<AppenderControl> appenders = new ArrayList<AppenderControl>();
        for (final AppenderRef appenderRef : this.appenderRefs) {
            final Appender appender = map.get(appenderRef.getRef());
            if (appender != null) {
                appenders.add(new AppenderControl(appender, appenderRef.getLevel(), appenderRef.getFilter()));
            }
            else {
                AsyncAppender.LOGGER.error("No appender named {} was configured", appenderRef);
            }
        }
        if (this.errorRef != null) {
            final Appender appender2 = map.get(this.errorRef);
            if (appender2 != null) {
                this.errorAppender = new AppenderControl(appender2, null, null);
            }
            else {
                AsyncAppender.LOGGER.error("Unable to set up error Appender. No appender named {} was configured", this.errorRef);
            }
        }
        if (appenders.size() > 0) {
            (this.thread = new AsyncThread(appenders, this.queue)).setName("AsyncAppender-" + this.getName());
        }
        else if (this.errorRef == null) {
            throw new ConfigurationException("No appenders are available for AsyncAppender " + this.getName());
        }
        this.asyncQueueFullPolicy = AsyncQueueFullPolicyFactory.create();
        this.thread.start();
        super.start();
    }
    
    @Override
    public boolean stop(final long timeout, final TimeUnit timeUnit) {
        this.setStopping();
        super.stop(timeout, timeUnit, false);
        AsyncAppender.LOGGER.trace("AsyncAppender stopping. Queue still has {} events.", (Object)this.queue.size());
        this.thread.shutdown();
        try {
            this.thread.join(this.shutdownTimeout);
        }
        catch (final InterruptedException ex) {
            AsyncAppender.LOGGER.warn("Interrupted while stopping AsyncAppender {}", this.getName());
        }
        AsyncAppender.LOGGER.trace("AsyncAppender stopped. Queue has {} events.", (Object)this.queue.size());
        if (DiscardingAsyncQueueFullPolicy.getDiscardCount(this.asyncQueueFullPolicy) > 0L) {
            AsyncAppender.LOGGER.trace("AsyncAppender: {} discarded {} events.", this.asyncQueueFullPolicy, DiscardingAsyncQueueFullPolicy.getDiscardCount(this.asyncQueueFullPolicy));
        }
        this.setStopped();
        return true;
    }
    
    @Override
    public void append(final LogEvent logEvent) {
        if (!this.isStarted()) {
            throw new IllegalStateException("AsyncAppender " + this.getName() + " is not active");
        }
        if (!this.canFormatMessageInBackground(logEvent.getMessage())) {
            logEvent.getMessage().getFormattedMessage();
        }
        final Log4jLogEvent memento = Log4jLogEvent.createMemento(logEvent, this.includeLocation);
        if (!this.transfer(memento)) {
            if (this.blocking) {
                final EventRoute route = this.asyncQueueFullPolicy.getRoute(this.thread.getId(), memento.getLevel());
                route.logMessage(this, memento);
            }
            else {
                this.error("Appender " + this.getName() + " is unable to write primary appenders. queue is full");
                this.logToErrorAppenderIfNecessary(false, memento);
            }
        }
    }
    
    private boolean canFormatMessageInBackground(final Message message) {
        return Constants.FORMAT_MESSAGES_IN_BACKGROUND || message.getClass().isAnnotationPresent(AsynchronouslyFormattable.class);
    }
    
    private boolean transfer(final LogEvent memento) {
        return (this.queue instanceof TransferQueue) ? ((TransferQueue)this.queue).tryTransfer(memento) : this.queue.offer(memento);
    }
    
    public void logMessageInCurrentThread(final LogEvent logEvent) {
        logEvent.setEndOfBatch(this.queue.isEmpty());
        final boolean appendSuccessful = this.thread.callAppenders(logEvent);
        this.logToErrorAppenderIfNecessary(appendSuccessful, logEvent);
    }
    
    public void logMessageInBackgroundThread(final LogEvent logEvent) {
        try {
            this.queue.put(logEvent);
        }
        catch (final InterruptedException e) {
            final boolean appendSuccessful = this.handleInterruptedException(logEvent);
            this.logToErrorAppenderIfNecessary(appendSuccessful, logEvent);
        }
    }
    
    private boolean handleInterruptedException(final LogEvent memento) {
        final boolean appendSuccessful = this.queue.offer(memento);
        if (!appendSuccessful) {
            AsyncAppender.LOGGER.warn("Interrupted while waiting for a free slot in the AsyncAppender LogEvent-queue {}", this.getName());
        }
        Thread.currentThread().interrupt();
        return appendSuccessful;
    }
    
    private void logToErrorAppenderIfNecessary(final boolean appendSuccessful, final LogEvent logEvent) {
        if (!appendSuccessful && this.errorAppender != null) {
            this.errorAppender.callAppender(logEvent);
        }
    }
    
    @Deprecated
    public static AsyncAppender createAppender(final AppenderRef[] appenderRefs, final String errorRef, final boolean blocking, final long shutdownTimeout, final int size, final String name, final boolean includeLocation, final Filter filter, final Configuration config, final boolean ignoreExceptions) {
        if (name == null) {
            AsyncAppender.LOGGER.error("No name provided for AsyncAppender");
            return null;
        }
        if (appenderRefs == null) {
            AsyncAppender.LOGGER.error("No appender references provided to AsyncAppender {}", name);
        }
        return new AsyncAppender(name, filter, appenderRefs, errorRef, size, blocking, ignoreExceptions, shutdownTimeout, config, includeLocation, new ArrayBlockingQueueFactory<LogEvent>());
    }
    
    @PluginBuilderFactory
    public static Builder newBuilder() {
        return new Builder();
    }
    
    public String[] getAppenderRefStrings() {
        final String[] result = new String[this.appenderRefs.length];
        for (int i = 0; i < result.length; ++i) {
            result[i] = this.appenderRefs[i].getRef();
        }
        return result;
    }
    
    public boolean isIncludeLocation() {
        return this.includeLocation;
    }
    
    public boolean isBlocking() {
        return this.blocking;
    }
    
    public String getErrorRef() {
        return this.errorRef;
    }
    
    public int getQueueCapacity() {
        return this.queueSize;
    }
    
    public int getQueueRemainingCapacity() {
        return this.queue.remainingCapacity();
    }
    
    static {
        SHUTDOWN_LOG_EVENT = new AbstractLogEvent() {};
        THREAD_SEQUENCE = new AtomicLong(1L);
    }
    
    public static class Builder implements org.apache.logging.log4j.core.util.Builder<AsyncAppender>
    {
        @PluginElement("AppenderRef")
        @Required(message = "No appender references provided to AsyncAppender")
        private AppenderRef[] appenderRefs;
        @PluginBuilderAttribute
        @PluginAliases({ "error-ref" })
        private String errorRef;
        @PluginBuilderAttribute
        private boolean blocking;
        @PluginBuilderAttribute
        private long shutdownTimeout;
        @PluginBuilderAttribute
        private int bufferSize;
        @PluginBuilderAttribute
        @Required(message = "No name provided for AsyncAppender")
        private String name;
        @PluginBuilderAttribute
        private boolean includeLocation;
        @PluginElement("Filter")
        private Filter filter;
        @PluginConfiguration
        private Configuration configuration;
        @PluginBuilderAttribute
        private boolean ignoreExceptions;
        @PluginElement("BlockingQueueFactory")
        private BlockingQueueFactory<LogEvent> blockingQueueFactory;
        
        public Builder() {
            this.blocking = true;
            this.shutdownTimeout = 0L;
            this.bufferSize = 128;
            this.includeLocation = false;
            this.ignoreExceptions = true;
            this.blockingQueueFactory = new ArrayBlockingQueueFactory<LogEvent>();
        }
        
        public Builder setAppenderRefs(final AppenderRef[] appenderRefs) {
            this.appenderRefs = appenderRefs;
            return this;
        }
        
        public Builder setErrorRef(final String errorRef) {
            this.errorRef = errorRef;
            return this;
        }
        
        public Builder setBlocking(final boolean blocking) {
            this.blocking = blocking;
            return this;
        }
        
        public Builder setShutdownTimeout(final long shutdownTimeout) {
            this.shutdownTimeout = shutdownTimeout;
            return this;
        }
        
        public Builder setBufferSize(final int bufferSize) {
            this.bufferSize = bufferSize;
            return this;
        }
        
        public Builder setName(final String name) {
            this.name = name;
            return this;
        }
        
        public Builder setIncludeLocation(final boolean includeLocation) {
            this.includeLocation = includeLocation;
            return this;
        }
        
        public Builder setFilter(final Filter filter) {
            this.filter = filter;
            return this;
        }
        
        public Builder setConfiguration(final Configuration configuration) {
            this.configuration = configuration;
            return this;
        }
        
        public Builder setIgnoreExceptions(final boolean ignoreExceptions) {
            this.ignoreExceptions = ignoreExceptions;
            return this;
        }
        
        public Builder setBlockingQueueFactory(final BlockingQueueFactory<LogEvent> blockingQueueFactory) {
            this.blockingQueueFactory = blockingQueueFactory;
            return this;
        }
        
        @Override
        public AsyncAppender build() {
            return new AsyncAppender(this.name, this.filter, this.appenderRefs, this.errorRef, this.bufferSize, this.blocking, this.ignoreExceptions, this.shutdownTimeout, this.configuration, this.includeLocation, this.blockingQueueFactory, null);
        }
    }
    
    private class AsyncThread extends Log4jThread
    {
        private volatile boolean shutdown;
        private final List<AppenderControl> appenders;
        private final BlockingQueue<LogEvent> queue;
        
        public AsyncThread(final List<AppenderControl> appenders, final BlockingQueue<LogEvent> queue) {
            super("AsyncAppender-" + AsyncAppender.THREAD_SEQUENCE.getAndIncrement());
            this.shutdown = false;
            this.appenders = appenders;
            this.queue = queue;
            this.setDaemon(true);
        }
        
        @Override
        public void run() {
            while (!this.shutdown) {
                LogEvent event;
                try {
                    event = this.queue.take();
                    if (event == AsyncAppender.SHUTDOWN_LOG_EVENT) {
                        this.shutdown = true;
                        continue;
                    }
                }
                catch (final InterruptedException ex) {
                    break;
                }
                event.setEndOfBatch(this.queue.isEmpty());
                final boolean success = this.callAppenders(event);
                if (!success && AsyncAppender.this.errorAppender != null) {
                    try {
                        AsyncAppender.this.errorAppender.callAppender(event);
                    }
                    catch (final Exception ex2) {}
                }
            }
            AsyncAppender.LOGGER.trace("AsyncAppender.AsyncThread shutting down. Processing remaining {} queue events.", (Object)this.queue.size());
            int count = 0;
            int ignored = 0;
            while (!this.queue.isEmpty()) {
                try {
                    final LogEvent event2 = this.queue.take();
                    if (event2 instanceof Log4jLogEvent) {
                        final Log4jLogEvent logEvent = (Log4jLogEvent)event2;
                        logEvent.setEndOfBatch(this.queue.isEmpty());
                        this.callAppenders(logEvent);
                        ++count;
                    }
                    else {
                        ++ignored;
                        AsyncAppender.LOGGER.trace("Ignoring event of class {}", event2.getClass().getName());
                    }
                }
                catch (final InterruptedException ex3) {}
            }
            AsyncAppender.LOGGER.trace("AsyncAppender.AsyncThread stopped. Queue has {} events remaining. Processed {} and ignored {} events since shutdown started.", (Object)this.queue.size(), count, ignored);
        }
        
        boolean callAppenders(final LogEvent event) {
            boolean success = false;
            for (final AppenderControl control : this.appenders) {
                try {
                    control.callAppender(event);
                    success = true;
                }
                catch (final Exception ex) {}
            }
            return success;
        }
        
        public void shutdown() {
            this.shutdown = true;
            if (this.queue.isEmpty()) {
                this.queue.offer(AsyncAppender.SHUTDOWN_LOG_EVENT);
            }
            if (this.getState() == Thread.State.TIMED_WAITING || this.getState() == Thread.State.WAITING) {
                this.interrupt();
            }
        }
    }
}
