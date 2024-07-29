/*
 * Decompiled with CFR 0.152.
 */
package me.nzxtercode.bettercraft.client.misc.cosmetics;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderPlayer;

public class CosmeticModelBase
extends ModelBase {
    protected final ModelBiped playerModel;

    public CosmeticModelBase(RenderPlayer player) {
        this.playerModel = player.getMainModel();
    }
}

