/*
 * Decompiled with CFR 0.152.
 */
package me.nzxtercode.bettercraft.client.events;

import me.nzxtercode.bettercraft.client.events.types.TypePrePost;
import net.lenni0451.eventapi.events.IEvent;
import net.minecraft.client.gui.ScaledResolution;

public class RenderEvents {

    public static class GameOverlay
    implements IEvent {
        private final TypePrePost type;
        private final ScaledResolution scaledResolution;
        private final float partialTicks;

        public GameOverlay(TypePrePost type, ScaledResolution scaledResolution, float partialTicks) {
            this.type = type;
            this.scaledResolution = scaledResolution;
            this.partialTicks = partialTicks;
        }

        public float getPartialTicks() {
            return this.partialTicks;
        }

        public TypePrePost getType() {
            return this.type;
        }

        public ScaledResolution getScaledResolution() {
            return this.scaledResolution;
        }
    }

    public static class ToolTip
    implements IEvent {
        private final TypePrePost type;
        private final ScaledResolution scaledResolution;
        private final float partialTicks;

        public ToolTip(TypePrePost type, ScaledResolution scaledResolution, float partialTicks) {
            this.type = type;
            this.scaledResolution = scaledResolution;
            this.partialTicks = partialTicks;
        }

        public float getPartialTicks() {
            return this.partialTicks;
        }

        public TypePrePost getType() {
            return this.type;
        }

        public ScaledResolution getScaledResolution() {
            return this.scaledResolution;
        }
    }
}

