/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.util;

import java.util.Arrays;
import java.util.HashSet;
import net.minecraft.client.settings.KeyBinding;

public class KeyUtils {
    public static void fixKeyConflicts(KeyBinding[] keys, KeyBinding[] keysPrio) {
        HashSet<Integer> set = new HashSet<Integer>();
        int i2 = 0;
        while (i2 < keysPrio.length) {
            KeyBinding keybinding = keysPrio[i2];
            set.add(keybinding.getKeyCode());
            ++i2;
        }
        HashSet<KeyBinding> set1 = new HashSet<KeyBinding>(Arrays.asList(keys));
        set1.removeAll(Arrays.asList(keysPrio));
        for (KeyBinding keybinding1 : set1) {
            Integer integer = keybinding1.getKeyCode();
            if (!set.contains(integer)) continue;
            keybinding1.setKeyCode(0);
        }
    }
}

