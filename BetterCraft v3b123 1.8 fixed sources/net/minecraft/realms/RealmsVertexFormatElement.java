// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.realms;

import net.minecraft.client.renderer.vertex.VertexFormatElement;

public class RealmsVertexFormatElement
{
    private VertexFormatElement v;
    
    public RealmsVertexFormatElement(final VertexFormatElement vIn) {
        this.v = vIn;
    }
    
    public VertexFormatElement getVertexFormatElement() {
        return this.v;
    }
    
    public boolean isPosition() {
        return this.v.isPositionElement();
    }
    
    public int getIndex() {
        return this.v.getIndex();
    }
    
    public int getByteSize() {
        return this.v.getSize();
    }
    
    public int getCount() {
        return this.v.getElementCount();
    }
    
    @Override
    public int hashCode() {
        return this.v.hashCode();
    }
    
    @Override
    public boolean equals(final Object p_equals_1_) {
        return this.v.equals(p_equals_1_);
    }
    
    @Override
    public String toString() {
        return this.v.toString();
    }
}
