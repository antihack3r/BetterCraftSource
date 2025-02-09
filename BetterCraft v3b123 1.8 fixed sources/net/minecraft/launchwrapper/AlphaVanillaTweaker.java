// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.launchwrapper;

import java.io.File;
import java.util.List;

public class AlphaVanillaTweaker implements ITweaker
{
    private List<String> args;
    
    @Override
    public void acceptOptions(final List<String> args, final File gameDir, final File assetsDir, final String profile) {
        this.args = args;
    }
    
    @Override
    public void injectIntoClassLoader(final LaunchClassLoader classLoader) {
        classLoader.registerTransformer("net.minecraft.launchwrapper.injector.AlphaVanillaTweakInjector");
    }
    
    @Override
    public String getLaunchTarget() {
        return "net.minecraft.launchwrapper.injector.AlphaVanillaTweakInjector";
    }
    
    @Override
    public String[] getLaunchArguments() {
        return this.args.toArray(new String[this.args.size()]);
    }
}
