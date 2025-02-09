// 
// Decompiled by Procyon v0.6.0
// 

package org.apache.logging.log4j.core.config;

import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.config.plugins.PluginValue;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import java.util.Objects;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.plugins.Plugin;

@Plugin(name = "property", category = "Core", printObject = true)
public final class Property
{
    private static final Logger LOGGER;
    private final String name;
    private final String value;
    private final boolean valueNeedsLookup;
    
    private Property(final String name, final String value) {
        this.name = name;
        this.value = value;
        this.valueNeedsLookup = (value != null && value.contains("${"));
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getValue() {
        return Objects.toString(this.value, "");
    }
    
    public boolean isValueNeedsLookup() {
        return this.valueNeedsLookup;
    }
    
    @PluginFactory
    public static Property createProperty(@PluginAttribute("name") final String name, @PluginValue("value") final String value) {
        if (name == null) {
            Property.LOGGER.error("Property name cannot be null");
        }
        return new Property(name, value);
    }
    
    @Override
    public String toString() {
        return this.name + '=' + this.getValue();
    }
    
    static {
        LOGGER = StatusLogger.getLogger();
    }
}
