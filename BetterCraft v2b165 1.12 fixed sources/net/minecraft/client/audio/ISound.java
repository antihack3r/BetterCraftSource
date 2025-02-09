// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.audio;

import net.minecraft.util.SoundCategory;
import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;

public interface ISound
{
    ResourceLocation getSoundLocation();
    
    @Nullable
    SoundEventAccessor createAccessor(final SoundHandler p0);
    
    Sound getSound();
    
    SoundCategory getCategory();
    
    boolean canRepeat();
    
    int getRepeatDelay();
    
    float getVolume();
    
    float getPitch();
    
    float getXPosF();
    
    float getYPosF();
    
    float getZPosF();
    
    AttenuationType getAttenuationType();
    
    public enum AttenuationType
    {
        NONE("NONE", 0, 0), 
        LINEAR("LINEAR", 1, 2);
        
        private final int type;
        
        private AttenuationType(final String s, final int n, final int typeIn) {
            this.type = typeIn;
        }
        
        public int getTypeInt() {
            return this.type;
        }
    }
}
