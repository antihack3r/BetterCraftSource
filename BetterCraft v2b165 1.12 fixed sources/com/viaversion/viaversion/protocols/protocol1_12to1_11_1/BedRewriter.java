// 
// Decompiled by Procyon v0.6.0
// 

package com.viaversion.viaversion.protocols.protocol1_12to1_11_1;

import com.viaversion.viaversion.api.minecraft.item.Item;

public class BedRewriter
{
    public static void toClientItem(final Item item) {
        if (item == null) {
            return;
        }
        if (item.identifier() == 355 && item.data() == 0) {
            item.setData((short)14);
        }
    }
    
    public static void toServerItem(final Item item) {
        if (item == null) {
            return;
        }
        if (item.identifier() == 355 && item.data() == 14) {
            item.setData((short)0);
        }
    }
}
