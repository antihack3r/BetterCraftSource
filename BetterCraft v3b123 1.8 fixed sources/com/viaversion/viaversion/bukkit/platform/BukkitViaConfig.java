// 
// Decompiled by Procyon v0.6.0
// 

package com.viaversion.viaversion.bukkit.platform;

import java.util.Arrays;
import java.util.Map;
import java.io.File;
import com.viaversion.viaversion.api.Via;
import org.bukkit.plugin.Plugin;
import java.util.List;
import com.viaversion.viaversion.configuration.AbstractViaConfig;

public class BukkitViaConfig extends AbstractViaConfig
{
    private static final List<String> UNSUPPORTED;
    private boolean quickMoveActionFix;
    private boolean hitboxFix1_9;
    private boolean hitboxFix1_14;
    private String blockConnectionMethod;
    private boolean armorToggleFix;
    private boolean registerUserConnectionOnJoin;
    
    public BukkitViaConfig() {
        super(new File(((Plugin)Via.getPlatform()).getDataFolder(), "config.yml"));
        this.reloadConfig();
    }
    
    @Override
    protected void loadFields() {
        super.loadFields();
        this.registerUserConnectionOnJoin = this.getBoolean("register-userconnections-on-join", true);
        this.quickMoveActionFix = this.getBoolean("quick-move-action-fix", false);
        this.hitboxFix1_9 = this.getBoolean("change-1_9-hitbox", false);
        this.hitboxFix1_14 = this.getBoolean("change-1_14-hitbox", false);
        this.blockConnectionMethod = this.getString("blockconnection-method", "packet");
        this.armorToggleFix = this.getBoolean("armor-toggle-fix", true);
    }
    
    @Override
    protected void handleConfig(final Map<String, Object> config) {
    }
    
    @Override
    public boolean shouldRegisterUserConnectionOnJoin() {
        return this.registerUserConnectionOnJoin;
    }
    
    @Override
    public boolean is1_12QuickMoveActionFix() {
        return this.quickMoveActionFix;
    }
    
    @Override
    public boolean is1_9HitboxFix() {
        return this.hitboxFix1_9;
    }
    
    @Override
    public boolean is1_14HitboxFix() {
        return this.hitboxFix1_14;
    }
    
    @Override
    public String getBlockConnectionMethod() {
        return this.blockConnectionMethod;
    }
    
    @Override
    public boolean isArmorToggleFix() {
        return this.armorToggleFix;
    }
    
    @Override
    public List<String> getUnsupportedOptions() {
        return BukkitViaConfig.UNSUPPORTED;
    }
    
    static {
        UNSUPPORTED = Arrays.asList("bungee-ping-interval", "bungee-ping-save", "bungee-servers", "velocity-ping-interval", "velocity-ping-save", "velocity-servers");
    }
}
