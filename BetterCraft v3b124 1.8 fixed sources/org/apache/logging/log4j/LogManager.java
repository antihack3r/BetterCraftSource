/*
 * Decompiled with CFR 0.152.
 */
package org.apache.logging.log4j;

import java.net.URI;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.MessageFactory;
import org.apache.logging.log4j.message.StringFormatterMessageFactory;
import org.apache.logging.log4j.simple.SimpleLoggerContextFactory;
import org.apache.logging.log4j.spi.LoggerContext;
import org.apache.logging.log4j.spi.LoggerContextFactory;
import org.apache.logging.log4j.spi.Provider;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.PropertiesUtil;
import org.apache.logging.log4j.util.ProviderUtil;

public class LogManager {
    private static LoggerContextFactory factory;
    private static final String FACTORY_PROPERTY_NAME = "log4j2.loggerContextFactory";
    private static final Logger LOGGER;
    public static final String ROOT_LOGGER_NAME = "";

    private static String getClassName(int depth) {
        return new Throwable().getStackTrace()[depth].getClassName();
    }

    public static LoggerContext getContext() {
        return factory.getContext(LogManager.class.getName(), null, true);
    }

    public static LoggerContext getContext(boolean currentContext) {
        return factory.getContext(LogManager.class.getName(), null, currentContext);
    }

    public static LoggerContext getContext(ClassLoader loader, boolean currentContext) {
        return factory.getContext(LogManager.class.getName(), loader, currentContext);
    }

    public static LoggerContext getContext(ClassLoader loader, boolean currentContext, URI configLocation) {
        return factory.getContext(LogManager.class.getName(), loader, currentContext, configLocation);
    }

    protected static LoggerContext getContext(String fqcn, boolean currentContext) {
        return factory.getContext(fqcn, null, currentContext);
    }

    protected static LoggerContext getContext(String fqcn, ClassLoader loader, boolean currentContext) {
        return factory.getContext(fqcn, loader, currentContext);
    }

    public static LoggerContextFactory getFactory() {
        return factory;
    }

    public static Logger getFormatterLogger(Class<?> clazz) {
        return LogManager.getLogger(clazz != null ? clazz.getName() : LogManager.getClassName(2), (MessageFactory)StringFormatterMessageFactory.INSTANCE);
    }

    public static Logger getFormatterLogger(Object value) {
        return LogManager.getLogger(value != null ? value.getClass().getName() : LogManager.getClassName(2), (MessageFactory)StringFormatterMessageFactory.INSTANCE);
    }

    public static Logger getFormatterLogger(String name) {
        return LogManager.getLogger(name != null ? name : LogManager.getClassName(2), (MessageFactory)StringFormatterMessageFactory.INSTANCE);
    }

    public static Logger getLogger() {
        return LogManager.getLogger(LogManager.getClassName(2));
    }

    public static Logger getLogger(Class<?> clazz) {
        return LogManager.getLogger(clazz != null ? clazz.getName() : LogManager.getClassName(2));
    }

    public static Logger getLogger(Class<?> clazz, MessageFactory messageFactory) {
        return LogManager.getLogger(clazz != null ? clazz.getName() : LogManager.getClassName(2), messageFactory);
    }

    public static Logger getLogger(MessageFactory messageFactory) {
        return LogManager.getLogger(LogManager.getClassName(2), messageFactory);
    }

    public static Logger getLogger(Object value) {
        return LogManager.getLogger(value != null ? value.getClass().getName() : LogManager.getClassName(2));
    }

    public static Logger getLogger(Object value, MessageFactory messageFactory) {
        return LogManager.getLogger(value != null ? value.getClass().getName() : LogManager.getClassName(2), messageFactory);
    }

    public static Logger getLogger(String name) {
        String actualName = name != null ? name : LogManager.getClassName(2);
        return factory.getContext(LogManager.class.getName(), null, false).getLogger(actualName);
    }

    public static Logger getLogger(String name, MessageFactory messageFactory) {
        String actualName = name != null ? name : LogManager.getClassName(2);
        return factory.getContext(LogManager.class.getName(), null, false).getLogger(actualName, messageFactory);
    }

    protected static Logger getLogger(String fqcn, String name) {
        return factory.getContext(fqcn, null, false).getLogger(name);
    }

    public static Logger getRootLogger() {
        return LogManager.getLogger(ROOT_LOGGER_NAME);
    }

    protected LogManager() {
    }

    static {
        LOGGER = StatusLogger.getLogger();
        PropertiesUtil managerProps = PropertiesUtil.getProperties();
        String factoryClass = managerProps.getStringProperty(FACTORY_PROPERTY_NAME);
        ClassLoader cl2 = ProviderUtil.findClassLoader();
        if (factoryClass != null) {
            try {
                Class<?> clazz = cl2.loadClass(factoryClass);
                if (LoggerContextFactory.class.isAssignableFrom(clazz)) {
                    factory = (LoggerContextFactory)clazz.newInstance();
                }
            }
            catch (ClassNotFoundException cnfe) {
                LOGGER.error("Unable to locate configured LoggerContextFactory {}", factoryClass);
            }
            catch (Exception ex2) {
                LOGGER.error("Unable to create configured LoggerContextFactory {}", factoryClass, ex2);
            }
        }
        if (factory == null) {
            TreeMap<Integer, LoggerContextFactory> factories = new TreeMap<Integer, LoggerContextFactory>();
            if (ProviderUtil.hasProviders()) {
                Iterator<Provider> providers = ProviderUtil.getProviders();
                while (providers.hasNext()) {
                    Provider provider = providers.next();
                    String className = provider.getClassName();
                    if (className == null) continue;
                    try {
                        Class<?> clazz = cl2.loadClass(className);
                        if (LoggerContextFactory.class.isAssignableFrom(clazz)) {
                            factories.put(provider.getPriority(), (LoggerContextFactory)clazz.newInstance());
                            continue;
                        }
                        LOGGER.error(className + " does not implement " + LoggerContextFactory.class.getName());
                    }
                    catch (ClassNotFoundException cnfe) {
                        LOGGER.error("Unable to locate class " + className + " specified in " + provider.getURL().toString(), (Throwable)cnfe);
                    }
                    catch (IllegalAccessException iae) {
                        LOGGER.error("Unable to create class " + className + " specified in " + provider.getURL().toString(), (Throwable)iae);
                    }
                    catch (Exception e2) {
                        LOGGER.error("Unable to create class " + className + " specified in " + provider.getURL().toString(), (Throwable)e2);
                        e2.printStackTrace();
                    }
                }
                if (factories.size() == 0) {
                    LOGGER.error("Unable to locate a logging implementation, using SimpleLogger");
                    factory = new SimpleLoggerContextFactory();
                } else {
                    StringBuilder sb2 = new StringBuilder("Multiple logging implementations found: \n");
                    for (Map.Entry entry : factories.entrySet()) {
                        sb2.append("Factory: ").append(((LoggerContextFactory)entry.getValue()).getClass().getName());
                        sb2.append(", Weighting: ").append(entry.getKey()).append("\n");
                    }
                    factory = (LoggerContextFactory)factories.get(factories.lastKey());
                    sb2.append("Using factory: ").append(factory.getClass().getName());
                    LOGGER.warn(sb2.toString());
                }
            } else {
                LOGGER.error("Unable to locate a logging implementation, using SimpleLogger");
                factory = new SimpleLoggerContextFactory();
            }
        }
    }
}

