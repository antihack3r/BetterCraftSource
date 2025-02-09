/*
 * Decompiled with CFR 0.152.
 */
package me.nzxtercode.bettercraft.client.mods;

import me.nzxtercode.bettercraft.client.BetterCraft;
import me.nzxtercode.bettercraft.client.mods.ModType;
import net.lenni0451.eventapi.manager.EventManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

public abstract class Mod {
    protected final Minecraft mc = Minecraft.getMinecraft();
    protected final FontRenderer font;
    protected final BetterCraft client;
    private boolean isEnabled = true;
    private String name;
    private ModType type;

    public Mod() {
        this.font = this.mc.fontRendererObj;
        this.client = BetterCraft.getInstance();
        this.setEnabled(this.isEnabled);
    }

    public void setEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
        if (isEnabled) {
            EventManager.register(this);
        } else {
            EventManager.unregister(this);
        }
    }

    public void setEnabledIngame(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    public boolean isEnabled() {
        return this.isEnabled;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ModType getType() {
        return this.type;
    }

    public void setType(ModType type) {
        this.type = type;
    }
}

