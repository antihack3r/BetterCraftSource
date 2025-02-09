// 
// Decompiled by Procyon v0.6.0
// 

package org.apache.logging.log4j.core.net.ssl;

import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import java.security.UnrecoverableKeyException;
import java.security.KeyStoreException;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.KeyManager;
import java.security.KeyManagementException;
import java.security.SecureRandom;
import java.security.NoSuchAlgorithmException;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.SSLContext;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.core.config.plugins.Plugin;

@Plugin(name = "Ssl", category = "Core", printObject = true)
public class SslConfiguration
{
    private static final StatusLogger LOGGER;
    private final KeyStoreConfiguration keyStoreConfig;
    private final TrustStoreConfiguration trustStoreConfig;
    private final SSLContext sslContext;
    private final String protocol;
    
    private SslConfiguration(final String protocol, final KeyStoreConfiguration keyStoreConfig, final TrustStoreConfiguration trustStoreConfig) {
        this.keyStoreConfig = keyStoreConfig;
        this.trustStoreConfig = trustStoreConfig;
        this.protocol = ((protocol == null) ? "SSL" : protocol);
        this.sslContext = this.createSslContext();
    }
    
    public SSLSocketFactory getSslSocketFactory() {
        return this.sslContext.getSocketFactory();
    }
    
    public SSLServerSocketFactory getSslServerSocketFactory() {
        return this.sslContext.getServerSocketFactory();
    }
    
    private SSLContext createSslContext() {
        SSLContext context = null;
        try {
            context = this.createSslContextBasedOnConfiguration();
            SslConfiguration.LOGGER.debug("Creating SSLContext with the given parameters");
        }
        catch (final TrustStoreConfigurationException e) {
            context = this.createSslContextWithTrustStoreFailure();
        }
        catch (final KeyStoreConfigurationException e2) {
            context = this.createSslContextWithKeyStoreFailure();
        }
        return context;
    }
    
    private SSLContext createSslContextWithTrustStoreFailure() {
        SSLContext context;
        try {
            context = this.createSslContextWithDefaultTrustManagerFactory();
            SslConfiguration.LOGGER.debug("Creating SSLContext with default truststore");
        }
        catch (final KeyStoreConfigurationException e) {
            context = this.createDefaultSslContext();
            SslConfiguration.LOGGER.debug("Creating SSLContext with default configuration");
        }
        return context;
    }
    
    private SSLContext createSslContextWithKeyStoreFailure() {
        SSLContext context;
        try {
            context = this.createSslContextWithDefaultKeyManagerFactory();
            SslConfiguration.LOGGER.debug("Creating SSLContext with default keystore");
        }
        catch (final TrustStoreConfigurationException e) {
            context = this.createDefaultSslContext();
            SslConfiguration.LOGGER.debug("Creating SSLContext with default configuration");
        }
        return context;
    }
    
    private SSLContext createSslContextBasedOnConfiguration() throws KeyStoreConfigurationException, TrustStoreConfigurationException {
        return this.createSslContext(false, false);
    }
    
    private SSLContext createSslContextWithDefaultKeyManagerFactory() throws TrustStoreConfigurationException {
        try {
            return this.createSslContext(true, false);
        }
        catch (final KeyStoreConfigurationException dummy) {
            SslConfiguration.LOGGER.debug("Exception occured while using default keystore. This should be a BUG");
            return null;
        }
    }
    
    private SSLContext createSslContextWithDefaultTrustManagerFactory() throws KeyStoreConfigurationException {
        try {
            return this.createSslContext(false, true);
        }
        catch (final TrustStoreConfigurationException dummy) {
            SslConfiguration.LOGGER.debug("Exception occured while using default truststore. This should be a BUG");
            return null;
        }
    }
    
    private SSLContext createDefaultSslContext() {
        try {
            return SSLContext.getDefault();
        }
        catch (final NoSuchAlgorithmException e) {
            SslConfiguration.LOGGER.error("Failed to create an SSLContext with default configuration", e);
            return null;
        }
    }
    
    private SSLContext createSslContext(final boolean loadDefaultKeyManagerFactory, final boolean loadDefaultTrustManagerFactory) throws KeyStoreConfigurationException, TrustStoreConfigurationException {
        try {
            KeyManager[] kManagers = null;
            TrustManager[] tManagers = null;
            final SSLContext newSslContext = SSLContext.getInstance(this.protocol);
            if (!loadDefaultKeyManagerFactory) {
                final KeyManagerFactory kmFactory = this.loadKeyManagerFactory();
                kManagers = kmFactory.getKeyManagers();
            }
            if (!loadDefaultTrustManagerFactory) {
                final TrustManagerFactory tmFactory = this.loadTrustManagerFactory();
                tManagers = tmFactory.getTrustManagers();
            }
            newSslContext.init(kManagers, tManagers, null);
            return newSslContext;
        }
        catch (final NoSuchAlgorithmException e) {
            SslConfiguration.LOGGER.error("No Provider supports a TrustManagerFactorySpi implementation for the specified protocol", e);
            throw new TrustStoreConfigurationException(e);
        }
        catch (final KeyManagementException e2) {
            SslConfiguration.LOGGER.error("Failed to initialize the SSLContext", e2);
            throw new KeyStoreConfigurationException(e2);
        }
    }
    
    private TrustManagerFactory loadTrustManagerFactory() throws TrustStoreConfigurationException {
        if (this.trustStoreConfig == null) {
            throw new TrustStoreConfigurationException(new Exception("The trustStoreConfiguration is null"));
        }
        try {
            return this.trustStoreConfig.initTrustManagerFactory();
        }
        catch (final NoSuchAlgorithmException e) {
            SslConfiguration.LOGGER.error("The specified algorithm is not available from the specified provider", e);
            throw new TrustStoreConfigurationException(e);
        }
        catch (final KeyStoreException e2) {
            SslConfiguration.LOGGER.error("Failed to initialize the TrustManagerFactory", e2);
            throw new TrustStoreConfigurationException(e2);
        }
    }
    
    private KeyManagerFactory loadKeyManagerFactory() throws KeyStoreConfigurationException {
        if (this.keyStoreConfig == null) {
            throw new KeyStoreConfigurationException(new Exception("The keyStoreConfiguration is null"));
        }
        try {
            return this.keyStoreConfig.initKeyManagerFactory();
        }
        catch (final NoSuchAlgorithmException e) {
            SslConfiguration.LOGGER.error("The specified algorithm is not available from the specified provider", e);
            throw new KeyStoreConfigurationException(e);
        }
        catch (final KeyStoreException e2) {
            SslConfiguration.LOGGER.error("Failed to initialize the TrustManagerFactory", e2);
            throw new KeyStoreConfigurationException(e2);
        }
        catch (final UnrecoverableKeyException e3) {
            SslConfiguration.LOGGER.error("The key cannot be recovered (e.g. the given password is wrong)", e3);
            throw new KeyStoreConfigurationException(e3);
        }
    }
    
    @PluginFactory
    public static SslConfiguration createSSLConfiguration(@PluginAttribute("protocol") final String protocol, @PluginElement("KeyStore") final KeyStoreConfiguration keyStoreConfig, @PluginElement("TrustStore") final TrustStoreConfiguration trustStoreConfig) {
        return new SslConfiguration(protocol, keyStoreConfig, trustStoreConfig);
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + ((this.keyStoreConfig == null) ? 0 : this.keyStoreConfig.hashCode());
        result = 31 * result + ((this.protocol == null) ? 0 : this.protocol.hashCode());
        result = 31 * result + ((this.sslContext == null) ? 0 : this.sslContext.hashCode());
        result = 31 * result + ((this.trustStoreConfig == null) ? 0 : this.trustStoreConfig.hashCode());
        return result;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final SslConfiguration other = (SslConfiguration)obj;
        if (this.keyStoreConfig == null) {
            if (other.keyStoreConfig != null) {
                return false;
            }
        }
        else if (!this.keyStoreConfig.equals(other.keyStoreConfig)) {
            return false;
        }
        if (this.protocol == null) {
            if (other.protocol != null) {
                return false;
            }
        }
        else if (!this.protocol.equals(other.protocol)) {
            return false;
        }
        if (this.sslContext == null) {
            if (other.sslContext != null) {
                return false;
            }
        }
        else if (!this.sslContext.equals(other.sslContext)) {
            return false;
        }
        if (this.trustStoreConfig == null) {
            if (other.trustStoreConfig != null) {
                return false;
            }
        }
        else if (!this.trustStoreConfig.equals(other.trustStoreConfig)) {
            return false;
        }
        return true;
    }
    
    static {
        LOGGER = StatusLogger.getLogger();
    }
}
