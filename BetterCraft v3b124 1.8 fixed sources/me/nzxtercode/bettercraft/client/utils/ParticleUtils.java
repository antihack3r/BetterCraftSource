/*
 * Decompiled with CFR 0.152.
 */
package me.nzxtercode.bettercraft.client.utils;

import java.util.ArrayList;
import me.nzxtercode.bettercraft.client.Config;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Mouse;

public class ParticleUtils {
    private final ArrayList<Particle> particles = new ArrayList();
    private final int count;
    private final float size;

    public ParticleUtils(int count, float size) {
        this.count = count;
        this.size = size;
    }

    public void setup() {
        this.particles.clear();
        int count = 0;
        while (count <= this.count) {
            this.particles.add(new Particle(this.size));
            ++count;
        }
    }

    public void render(int mouseX, int mouseY) {
        this.particles.forEach(particle -> particle.render(mouseX, mouseY));
    }

    public float genRandom(float min, float max) {
        return (float)((double)min + Math.random() * (double)(max - min + 1.0f));
    }

    public class Particle {
        private float x;
        private float y;
        private boolean xDir;
        private boolean yDir;
        private float speed;
        private float sSpeed;
        private float size;
        private boolean hold;

        public Particle(float size) {
            this.x = ParticleUtils.this.genRandom(0.0f, GuiScreen.width);
            this.y = ParticleUtils.this.genRandom(0.0f, GuiScreen.height);
            this.xDir = (int)ParticleUtils.this.genRandom(0.0f, 1.0f) == 1;
            this.yDir = (int)ParticleUtils.this.genRandom(0.0f, 1.0f) == 1;
            this.size = size;
            this.sSpeed = this.speed = ParticleUtils.this.genRandom(0.2f, 1.0f);
        }

        public void render(int mouseX, int mouseY) {
            if (Mouse.isButtonDown(1)) {
                if ((int)this.x != mouseX) {
                    this.x = this.x < (float)mouseX ? (this.x += this.speed) : (this.x -= this.speed);
                }
                if ((int)this.y != mouseY) {
                    this.y = this.y < (float)mouseY && this.y != (float)mouseY ? (this.y += this.speed) : (this.y -= this.speed);
                }
            } else {
                this.x = this.xDir ? (this.x += this.speed) : (this.x -= this.speed);
                this.y = this.yDir ? (this.y += this.speed) : (this.y -= this.speed);
                if (this.x + this.size >= (float)GuiScreen.width || this.x - this.size <= 0.0f) {
                    boolean bl2 = this.xDir = !this.xDir;
                    if (this.speed < 5.0f) {
                        this.speed += 2.0f;
                    }
                }
                if (this.y + this.size >= (float)GuiScreen.height || this.y - this.size <= 0.0f) {
                    boolean bl3 = this.yDir = !this.yDir;
                    if (this.speed < 5.0f) {
                        this.speed += 2.0f;
                    }
                }
                if (this.speed > this.sSpeed) {
                    this.speed -= 0.05f;
                }
            }
            GuiScreen.drawRect((int)this.x, (int)this.y, (int)this.x + (int)this.size, (int)this.y + (int)this.size, Config.getInstance().getColor("Particel").get("color").getAsInt());
        }
    }
}

