/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.core_implementation.mc18;

import net.labymod.core.SoundAdapter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;

public class SoundImplementation
implements SoundAdapter {
    @Override
    public void playSignSearchSign(int x2, int y2, int z2) {
        Minecraft.getMinecraft().getSoundHandler().playSound(new PositionedSoundRecord(new ResourceLocation("fireworks.twinkle_far"), 10.0f, 2.0f, x2, y2, z2));
    }
}

