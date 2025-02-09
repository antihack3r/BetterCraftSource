/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.protocols.protocol1_20_2to1_20.util;

import com.viaversion.viaversion.libs.fastutil.objects.Object2IntMap;
import com.viaversion.viaversion.libs.fastutil.objects.Object2IntOpenHashMap;
import com.viaversion.viaversion.util.Key;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class PotionEffects {
    private static final Object2IntMap<String> KEY_TO_ID = new Object2IntOpenHashMap<String>();
    private static final String[] POTION_EFFECTS = new String[]{"", "speed", "slowness", "haste", "mining_fatigue", "strength", "instant_health", "instant_damage", "jump_boost", "nausea", "regeneration", "resistance", "fire_resistance", "water_breathing", "invisibility", "blindness", "night_vision", "hunger", "weakness", "poison", "wither", "health_boost", "absorption", "saturation", "glowing", "levitation", "luck", "unluck", "slow_falling", "conduit_power", "dolphins_grace", "bad_omen", "hero_of_the_village", "darkness"};

    public static @Nullable String idToKey(int id2) {
        return id2 >= 1 && id2 < POTION_EFFECTS.length ? Key.namespaced(POTION_EFFECTS[id2]) : null;
    }

    public static String idToKeyOrLuck(int id2) {
        return id2 >= 1 && id2 < POTION_EFFECTS.length ? Key.namespaced(POTION_EFFECTS[id2]) : "minecraft:luck";
    }

    public static int keyToId(String key) {
        return KEY_TO_ID.getInt(Key.stripMinecraftNamespace(key));
    }

    static {
        for (int i2 = 1; i2 < POTION_EFFECTS.length; ++i2) {
            String effect = POTION_EFFECTS[i2];
            KEY_TO_ID.put(effect, i2);
        }
    }
}

