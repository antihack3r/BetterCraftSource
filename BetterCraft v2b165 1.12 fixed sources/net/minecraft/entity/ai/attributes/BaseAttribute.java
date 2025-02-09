// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.entity.ai.attributes;

import javax.annotation.Nullable;

public abstract class BaseAttribute implements IAttribute
{
    private final IAttribute parent;
    private final String unlocalizedName;
    private final double defaultValue;
    private boolean shouldWatch;
    
    protected BaseAttribute(@Nullable final IAttribute parentIn, final String unlocalizedNameIn, final double defaultValueIn) {
        this.parent = parentIn;
        this.unlocalizedName = unlocalizedNameIn;
        this.defaultValue = defaultValueIn;
        if (unlocalizedNameIn == null) {
            throw new IllegalArgumentException("Name cannot be null!");
        }
    }
    
    @Override
    public String getAttributeUnlocalizedName() {
        return this.unlocalizedName;
    }
    
    @Override
    public double getDefaultValue() {
        return this.defaultValue;
    }
    
    @Override
    public boolean getShouldWatch() {
        return this.shouldWatch;
    }
    
    public BaseAttribute setShouldWatch(final boolean shouldWatchIn) {
        this.shouldWatch = shouldWatchIn;
        return this;
    }
    
    @Nullable
    @Override
    public IAttribute getParent() {
        return this.parent;
    }
    
    @Override
    public int hashCode() {
        return this.unlocalizedName.hashCode();
    }
    
    @Override
    public boolean equals(final Object p_equals_1_) {
        return p_equals_1_ instanceof IAttribute && this.unlocalizedName.equals(((IAttribute)p_equals_1_).getAttributeUnlocalizedName());
    }
}
