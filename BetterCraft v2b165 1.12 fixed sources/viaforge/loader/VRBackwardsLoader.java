// 
// Decompiled by Procyon v0.6.0
// 

package viaforge.loader;

import viaforge.ViaForge;
import java.util.logging.Logger;
import java.io.File;
import com.viaversion.viabackwards.api.ViaBackwardsPlatform;

public class VRBackwardsLoader implements ViaBackwardsPlatform
{
    private final File file;
    
    public VRBackwardsLoader(final File file) {
        this.init(this.file = new File(file, "ViaBackwards"));
    }
    
    @Override
    public Logger getLogger() {
        return ViaForge.getInstance().getjLogger();
    }
    
    @Override
    public void disable() {
    }
    
    @Override
    public boolean isOutdated() {
        return false;
    }
    
    @Override
    public File getDataFolder() {
        return new File(this.file, "config.yml");
    }
}
