// 
// Decompiled by Procyon v0.6.0
// 

package de.gerrygames.viarewind.api;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.net.URL;
import java.io.File;
import com.viaversion.viaversion.util.Config;

public class ViaRewindConfigImpl extends Config implements ViaRewindConfig
{
    public ViaRewindConfigImpl(final File configFile) {
        super(configFile);
        this.reloadConfig();
    }
    
    @Override
    public CooldownIndicator getCooldownIndicator() {
        return CooldownIndicator.valueOf(this.getString("cooldown-indicator", "TITLE").toUpperCase());
    }
    
    @Override
    public boolean isReplaceAdventureMode() {
        return this.getBoolean("replace-adventure", false);
    }
    
    @Override
    public boolean isReplaceParticles() {
        return this.getBoolean("replace-particles", false);
    }
    
    @Override
    public URL getDefaultConfigURL() {
        return this.getClass().getClassLoader().getResource("assets/viarewind/config.yml");
    }
    
    @Override
    protected void handleConfig(final Map<String, Object> map) {
    }
    
    @Override
    public List<String> getUnsupportedOptions() {
        return Collections.emptyList();
    }
}
