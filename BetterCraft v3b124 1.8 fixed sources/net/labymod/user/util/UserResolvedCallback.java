/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.user.util;

import java.util.Map;
import net.labymod.user.cosmetic.util.CosmeticData;
import net.labymod.user.group.LabyGroup;

public interface UserResolvedCallback {
    public void resolvedCosmetics(Map<Integer, CosmeticData> var1);

    public void resolvedGroup(LabyGroup var1);

    public void complete();

    public void resolvedDailyEmoteFlat(boolean var1);
}

