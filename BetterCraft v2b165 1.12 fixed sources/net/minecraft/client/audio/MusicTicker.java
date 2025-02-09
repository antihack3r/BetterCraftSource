// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.audio;

import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.Minecraft;
import java.util.Random;
import net.minecraft.util.ITickable;

public class MusicTicker implements ITickable
{
    private final Random rand;
    private final Minecraft mc;
    private ISound currentMusic;
    private int timeUntilNextMusic;
    
    public MusicTicker(final Minecraft mcIn) {
        this.rand = new Random();
        this.timeUntilNextMusic = 100;
        this.mc = mcIn;
    }
    
    @Override
    public void update() {
        final MusicType musicticker$musictype = this.mc.getAmbientMusicType();
        if (this.currentMusic != null) {
            if (!musicticker$musictype.getMusicLocation().getSoundName().equals(this.currentMusic.getSoundLocation())) {
                this.mc.getSoundHandler().stopSound(this.currentMusic);
                this.timeUntilNextMusic = MathHelper.getInt(this.rand, 0, musicticker$musictype.getMinDelay() / 2);
            }
            if (!this.mc.getSoundHandler().isSoundPlaying(this.currentMusic)) {
                this.currentMusic = null;
                this.timeUntilNextMusic = Math.min(MathHelper.getInt(this.rand, musicticker$musictype.getMinDelay(), musicticker$musictype.getMaxDelay()), this.timeUntilNextMusic);
            }
        }
        this.timeUntilNextMusic = Math.min(this.timeUntilNextMusic, musicticker$musictype.getMaxDelay());
        if (this.currentMusic == null && this.timeUntilNextMusic-- <= 0) {
            this.playMusic(musicticker$musictype);
        }
    }
    
    public void playMusic(final MusicType requestedMusicType) {
        this.currentMusic = PositionedSoundRecord.getMusicRecord(requestedMusicType.getMusicLocation());
        this.mc.getSoundHandler().playSound(this.currentMusic);
        this.timeUntilNextMusic = Integer.MAX_VALUE;
    }
    
    public enum MusicType
    {
        MENU("MENU", 0, SoundEvents.MUSIC_MENU, 20, 600), 
        GAME("GAME", 1, SoundEvents.MUSIC_GAME, 12000, 24000), 
        CREATIVE("CREATIVE", 2, SoundEvents.MUSIC_CREATIVE, 1200, 3600), 
        CREDITS("CREDITS", 3, SoundEvents.MUSIC_CREDITS, 0, 0), 
        NETHER("NETHER", 4, SoundEvents.MUSIC_NETHER, 1200, 3600), 
        END_BOSS("END_BOSS", 5, SoundEvents.MUSIC_DRAGON, 0, 0), 
        END("END", 6, SoundEvents.MUSIC_END, 6000, 24000);
        
        private final SoundEvent musicLocation;
        private final int minDelay;
        private final int maxDelay;
        
        private MusicType(final String s, final int n, final SoundEvent musicLocationIn, final int minDelayIn, final int maxDelayIn) {
            this.musicLocation = musicLocationIn;
            this.minDelay = minDelayIn;
            this.maxDelay = maxDelayIn;
        }
        
        public SoundEvent getMusicLocation() {
            return this.musicLocation;
        }
        
        public int getMinDelay() {
            return this.minDelay;
        }
        
        public int getMaxDelay() {
            return this.maxDelay;
        }
    }
}
