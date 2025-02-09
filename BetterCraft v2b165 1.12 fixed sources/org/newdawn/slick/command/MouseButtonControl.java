// 
// Decompiled by Procyon v0.6.0
// 

package org.newdawn.slick.command;

public class MouseButtonControl implements Control
{
    private int button;
    
    public MouseButtonControl(final int button) {
        this.button = button;
    }
    
    @Override
    public boolean equals(final Object o) {
        return o instanceof MouseButtonControl && ((MouseButtonControl)o).button == this.button;
    }
    
    @Override
    public int hashCode() {
        return this.button;
    }
}
