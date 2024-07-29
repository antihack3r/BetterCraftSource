/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.user.cosmetic.util;

import net.labymod.core.LabyModCore;
import net.labymod.main.LabyMod;

public class SneakingAnimationThread
extends Thread {
    private float ySize = 0.0f;

    @Override
    public void run() {
        while (this.isInUse()) {
            try {
                Thread.sleep(5L);
            }
            catch (Exception error) {
                error.printStackTrace();
            }
            if (LabyModCore.getMinecraft().getPlayer() == null || LabyModCore.getMinecraft().getPlayer().movementInput == null) continue;
            boolean isSneaking = LabyModCore.getMinecraft().getPlayer().movementInput.sneak;
            if (isSneaking) {
                this.ySize += 0.01f;
                if (this.ySize <= 0.08f) continue;
                this.ySize = 0.08f;
                continue;
            }
            this.ySize *= 0.91f;
        }
    }

    public boolean isInUse() {
        return LabyMod.getInstance() != null && LabyMod.getMainConfig() != null && LabyMod.getSettings().oldSneaking;
    }

    public float getYSize() {
        return this.ySize;
    }
}

