// 
// Decompiled by Procyon v0.6.0
// 

package org.apache.logging.log4j.core.layout;

import java.io.IOException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.core.JsonGenerationException;
import java.io.Writer;
import org.apache.logging.log4j.core.LogEvent;
import java.nio.charset.StandardCharsets;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.core.config.Configuration;
import java.nio.charset.Charset;
import org.apache.logging.log4j.core.config.plugins.Plugin;

@Plugin(name = "XmlLayout", category = "Core", elementType = "layout", printObject = true)
public final class XmlLayout extends AbstractJacksonLayout
{
    private static final String ROOT_TAG = "Events";
    
    protected XmlLayout(final boolean locationInfo, final boolean properties, final boolean complete, final boolean compact, final Charset charset, final boolean includeStacktrace) {
        super(null, new JacksonFactory.XML(includeStacktrace).newWriter(locationInfo, properties, compact), charset, compact, complete, false, null, null);
    }
    
    @Override
    public byte[] getHeader() {
        if (!this.complete) {
            return null;
        }
        final StringBuilder buf = new StringBuilder();
        buf.append("<?xml version=\"1.0\" encoding=\"");
        buf.append(this.getCharset().name());
        buf.append("\"?>");
        buf.append(this.eol);
        buf.append('<');
        buf.append("Events");
        buf.append(" xmlns=\"http://logging.apache.org/log4j/2.0/events\">");
        buf.append(this.eol);
        return buf.toString().getBytes(this.getCharset());
    }
    
    @Override
    public byte[] getFooter() {
        if (!this.complete) {
            return null;
        }
        return this.getBytes("</Events>" + this.eol);
    }
    
    @Override
    public Map<String, String> getContentFormat() {
        final Map<String, String> result = new HashMap<String, String>();
        result.put("xsd", "log4j-events.xsd");
        result.put("version", "2.0");
        return result;
    }
    
    @Override
    public String getContentType() {
        return "text/xml; charset=" + this.getCharset();
    }
    
    @PluginFactory
    public static XmlLayout createLayout(@PluginAttribute("locationInfo") final boolean locationInfo, @PluginAttribute("properties") final boolean properties, @PluginAttribute("complete") final boolean complete, @PluginAttribute("compact") final boolean compact, @PluginAttribute(value = "charset", defaultString = "UTF-8") final Charset charset, @PluginAttribute(value = "includeStacktrace", defaultBoolean = true) final boolean includeStacktrace) {
        return new XmlLayout(locationInfo, properties, complete, compact, charset, includeStacktrace);
    }
    
    public static XmlLayout createDefaultLayout() {
        return new XmlLayout(false, false, false, false, StandardCharsets.UTF_8, true);
    }
}
