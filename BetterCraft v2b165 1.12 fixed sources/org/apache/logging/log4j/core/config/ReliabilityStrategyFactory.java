// 
// Decompiled by Procyon v0.6.0
// 

package org.apache.logging.log4j.core.config;

import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.LoaderUtil;
import org.apache.logging.log4j.util.PropertiesUtil;

public final class ReliabilityStrategyFactory
{
    private ReliabilityStrategyFactory() {
    }
    
    public static ReliabilityStrategy getReliabilityStrategy(final LoggerConfig loggerConfig) {
        final String strategy = PropertiesUtil.getProperties().getStringProperty("log4j.ReliabilityStrategy", "AwaitCompletion");
        if ("AwaitCompletion".equals(strategy)) {
            return new AwaitCompletionReliabilityStrategy(loggerConfig);
        }
        if ("AwaitUnconditionally".equals(strategy)) {
            return new AwaitUnconditionallyReliabilityStrategy(loggerConfig);
        }
        if ("Locking".equals(strategy)) {
            return new LockingReliabilityStrategy(loggerConfig);
        }
        try {
            final Class<? extends ReliabilityStrategy> cls = LoaderUtil.loadClass(strategy).asSubclass(ReliabilityStrategy.class);
            return (ReliabilityStrategy)cls.getConstructor(LoggerConfig.class).newInstance(loggerConfig);
        }
        catch (final Exception dynamicFailed) {
            StatusLogger.getLogger().warn("Could not create ReliabilityStrategy for '{}', using default AwaitCompletionReliabilityStrategy: {}", strategy, dynamicFailed);
            return new AwaitCompletionReliabilityStrategy(loggerConfig);
        }
    }
}
