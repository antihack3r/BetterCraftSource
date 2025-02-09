// 
// Decompiled by Procyon v0.6.0
// 

package org.apache.logging.log4j.core.config.builder.impl;

import org.apache.logging.log4j.core.config.builder.api.ComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.FilterComponentBuilder;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.builder.api.CompositeFilterComponentBuilder;

class DefaultCompositeFilterComponentBuilder extends DefaultComponentAndConfigurationBuilder<CompositeFilterComponentBuilder> implements CompositeFilterComponentBuilder
{
    public DefaultCompositeFilterComponentBuilder(final DefaultConfigurationBuilder<? extends Configuration> builder, final String onMatch, final String onMisMatch) {
        super(builder, "Filters");
        this.addAttribute("onMatch", onMatch);
        this.addAttribute("onMisMatch", onMisMatch);
    }
    
    @Override
    public CompositeFilterComponentBuilder add(final FilterComponentBuilder builder) {
        return ((DefaultComponentBuilder<CompositeFilterComponentBuilder, CB>)this).addComponent(builder);
    }
}
