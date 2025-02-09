// 
// Decompiled by Procyon v0.6.0
// 

package shadersmod.client;

import optifine.Lang;

public class PropertyDefaultTrueFalse extends Property
{
    public static final String[] PROPERTY_VALUES;
    public static final String[] USER_VALUES;
    
    static {
        PROPERTY_VALUES = new String[] { "default", "true", "false" };
        USER_VALUES = new String[] { "Default", "ON", "OFF" };
    }
    
    public PropertyDefaultTrueFalse(final String propertyName, final String userName, final int defaultValue) {
        super(propertyName, PropertyDefaultTrueFalse.PROPERTY_VALUES, userName, PropertyDefaultTrueFalse.USER_VALUES, defaultValue);
    }
    
    @Override
    public String getUserValue() {
        if (this.isDefault()) {
            return Lang.getDefault();
        }
        if (this.isTrue()) {
            return Lang.getOn();
        }
        return this.isFalse() ? Lang.getOff() : super.getUserValue();
    }
    
    public boolean isDefault() {
        return this.getValue() == 0;
    }
    
    public boolean isTrue() {
        return this.getValue() == 1;
    }
    
    public boolean isFalse() {
        return this.getValue() == 2;
    }
}
