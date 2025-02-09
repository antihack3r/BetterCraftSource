// 
// Decompiled by Procyon v0.6.0
// 

package javax.vecmath;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

class VecMathI18N
{
    static String getString(final String key) {
        String s;
        try {
            s = ResourceBundle.getBundle("javax.vecmath.ExceptionStrings").getString(key);
        }
        catch (final MissingResourceException e) {
            System.err.println("VecMathI18N: Error looking up: " + key);
            s = key;
        }
        return s;
    }
}
