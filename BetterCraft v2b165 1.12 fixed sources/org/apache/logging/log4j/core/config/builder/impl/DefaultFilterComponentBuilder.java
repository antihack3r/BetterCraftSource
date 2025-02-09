// 
// Decompiled by Procyon v0.6.0
// 

package org.apache.logging.log4j.core.config.builder.impl;

import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.builder.api.FilterComponentBuilder;

class DefaultFilterComponentBuilder extends DefaultComponentAndConfigurationBuilder<FilterComponentBuilder> implements FilterComponentBuilder
{
    public DefaultFilterComponentBuilder(final DefaultConfigurationBuilder<? extends Configuration> builder, final String type, final String onMatch, final String onMisMatch) {
        super(builder, type);
        this.addAttribute("onMatch", onMatch);
        this.addAttribute("onMisMatch", onMisMatch);
    }
}
