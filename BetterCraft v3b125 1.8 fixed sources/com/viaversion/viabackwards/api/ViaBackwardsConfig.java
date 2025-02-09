/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viabackwards.api;

import com.viaversion.viaversion.api.configuration.Config;

public interface ViaBackwardsConfig
extends Config {
    public boolean addCustomEnchantsToLore();

    public boolean addTeamColorTo1_13Prefix();

    public boolean isFix1_13FacePlayer();

    public boolean fix1_13FormattedInventoryTitle();

    public boolean alwaysShowOriginalMobName();

    public boolean handlePingsAsInvAcknowledgements();
}

