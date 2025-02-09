// 
// Decompiled by Procyon v0.6.0
// 

package optifine;

import java.util.Comparator;

public class CustomItemsComparator implements Comparator
{
    @Override
    public int compare(final Object p_compare_1_, final Object p_compare_2_) {
        final CustomItemProperties customitemproperties = (CustomItemProperties)p_compare_1_;
        final CustomItemProperties customitemproperties2 = (CustomItemProperties)p_compare_2_;
        if (customitemproperties.weight != customitemproperties2.weight) {
            return customitemproperties2.weight - customitemproperties.weight;
        }
        return Config.equals(customitemproperties.basePath, customitemproperties2.basePath) ? customitemproperties.name.compareTo(customitemproperties2.name) : customitemproperties.basePath.compareTo(customitemproperties2.basePath);
    }
}
