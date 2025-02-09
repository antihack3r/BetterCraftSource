// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.block.properties;

import com.google.common.base.Optional;
import java.util.Collection;
import com.google.common.collect.ImmutableSet;

public class PropertyBool extends PropertyHelper<Boolean>
{
    private final ImmutableSet<Boolean> allowedValues;
    
    protected PropertyBool(final String name) {
        super(name, Boolean.class);
        this.allowedValues = ImmutableSet.of(true, false);
    }
    
    @Override
    public Collection<Boolean> getAllowedValues() {
        return this.allowedValues;
    }
    
    public static PropertyBool create(final String name) {
        return new PropertyBool(name);
    }
    
    @Override
    public Optional<Boolean> parseValue(final String value) {
        return (!"true".equals(value) && !"false".equals(value)) ? Optional.absent() : Optional.of(Boolean.valueOf(value));
    }
    
    @Override
    public String getName(final Boolean value) {
        return value.toString();
    }
    
    @Override
    public boolean equals(final Object p_equals_1_) {
        if (this == p_equals_1_) {
            return true;
        }
        if (p_equals_1_ instanceof PropertyBool && super.equals(p_equals_1_)) {
            final PropertyBool propertybool = (PropertyBool)p_equals_1_;
            return this.allowedValues.equals(propertybool.allowedValues);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return 31 * super.hashCode() + this.allowedValues.hashCode();
    }
}
