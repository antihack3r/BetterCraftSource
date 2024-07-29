/*
 * Decompiled with CFR 0.152.
 */
package me.nzxtercode.bettercraft.client.mods.impl;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import me.nzxtercode.bettercraft.client.hud.ScreenPosition;
import me.nzxtercode.bettercraft.client.mods.ModRender;
import me.nzxtercode.bettercraft.client.utils.ColorUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class ModKeystrokes
extends ModRender {
    private KeystrokesMode mode = KeystrokesMode.WASD_MOUSE;
    private static List<Long> clicksLMB = new ArrayList<Long>();
    private static List<Long> clicksRMB = new ArrayList<Long>();
    private boolean wasPressedLMB;
    private long lastPressedLMB;
    private boolean wasPressedRMB;
    private long lastPressedRMB;

    public void setMode(KeystrokesMode mode) {
        this.mode = mode;
    }

    @Override
    public int getWidth() {
        return this.mode.getWidth();
    }

    @Override
    public int getHeight() {
        return this.mode.getHeight();
    }

    @Override
    public void render(ScreenPosition pos) {
        boolean pressedRMB;
        boolean pressedLMB = Mouse.isButtonDown(0);
        if (pressedLMB != this.wasPressedLMB) {
            this.lastPressedLMB = System.currentTimeMillis();
            this.wasPressedLMB = pressedLMB;
            if (pressedLMB) {
                clicksLMB.add(this.lastPressedLMB);
            }
        }
        if ((pressedRMB = Mouse.isButtonDown(1)) != this.wasPressedRMB) {
            this.lastPressedRMB = System.currentTimeMillis();
            this.wasPressedRMB = pressedRMB;
            if (pressedRMB) {
                clicksRMB.add(this.lastPressedRMB);
            }
        }
        GL11.glPushMatrix();
        Key[] keyArray = this.mode.getKeys();
        int n2 = keyArray.length;
        int n3 = 0;
        while (n3 < n2) {
            Key key = keyArray[n3];
            int textWidth = this.font.getStringWidth(key.getName());
            Color c2 = new Color(255, 255, 255, 50);
            Gui.drawRect(pos.getAbsoluteX() + key.getX(), pos.getAbsoluteY() + key.getY(), pos.getAbsoluteX() + key.getX() + key.getWidth(), pos.getAbsoluteY() + key.getY() + key.getHeight(), key.isDown() ? new Color(255, 255, 255, 100).getRGB() : Integer.MIN_VALUE);
            this.font.drawString(String.format("%s", key.getName() == "LMB" ? String.valueOf(key.getName()) + " " + ModKeystrokes.getLMB() : String.format("%s", key.getName() == "RMB" ? String.valueOf(key.getName()) + " " + ModKeystrokes.getRMB() : key.getName())), pos.getAbsoluteX() + key.getX() + key.getWidth() / 2 - textWidth / 2 - 3, pos.getAbsoluteY() + key.getY() + key.getHeight() / 2 - 4, key.isDown() ? Color.WHITE.getRGB() : ColorUtils.rainbowEffect());
            ++n3;
        }
        GL11.glPopMatrix();
    }

    public static int getLMB() {
        long time = System.currentTimeMillis();
        clicksLMB.removeIf(aLong -> aLong + 1000L < time);
        return clicksLMB.size();
    }

    public static int getRMB() {
        long time = System.currentTimeMillis();
        clicksRMB.removeIf(aLong -> aLong + 1000L < time);
        return clicksRMB.size();
    }

    private static class Key {
        private static final Key W = new Key("W", Minecraft.getMinecraft().gameSettings.keyBindForward, 21, 1, 18, 18);
        private static final Key A = new Key("A", Minecraft.getMinecraft().gameSettings.keyBindLeft, 1, 21, 18, 18);
        private static final Key S = new Key("S", Minecraft.getMinecraft().gameSettings.keyBindBack, 21, 21, 18, 18);
        private static final Key D = new Key("D", Minecraft.getMinecraft().gameSettings.keyBindRight, 41, 21, 18, 18);
        private static final Key LMB = new Key("LMB", Minecraft.getMinecraft().gameSettings.keyBindAttack, 1, 41, 28, 18);
        private static final Key RMB = new Key("RMB", Minecraft.getMinecraft().gameSettings.keyBindUseItem, 31, 41, 28, 18);
        private final String name;
        private final KeyBinding keyBind;
        private final int x;
        private final int y;
        private final int width;
        private final int height;

        public Key(String name, KeyBinding keyBind, int x2, int y2, int width, int height) {
            this.name = name;
            this.keyBind = keyBind;
            this.x = x2;
            this.y = y2;
            this.width = width;
            this.height = height;
        }

        public boolean isDown() {
            return this.keyBind.isKeyDown();
        }

        public int getHeight() {
            return this.height;
        }

        public String getName() {
            return this.name;
        }

        public int getWidth() {
            return this.width;
        }

        public int getX() {
            return this.x;
        }

        public int getY() {
            return this.y;
        }

        static /* synthetic */ Key access$0() {
            return W;
        }

        static /* synthetic */ Key access$1() {
            return A;
        }

        static /* synthetic */ Key access$2() {
            return S;
        }

        static /* synthetic */ Key access$3() {
            return D;
        }

        static /* synthetic */ Key access$4() {
            return LMB;
        }

        static /* synthetic */ Key access$5() {
            return RMB;
        }
    }

    public static enum KeystrokesMode {
        WASD(Key.access$0(), Key.access$1(), Key.access$2(), Key.access$3()),
        WASD_MOUSE(Key.access$0(), Key.access$1(), Key.access$2(), Key.access$3(), Key.access$4(), Key.access$5()),
        WASD_SPRINT(Key.access$0(), Key.access$1(), Key.access$2(), Key.access$3(), new Key("Sprint", Minecraft.getMinecraft().gameSettings.keyBindSprint, 1, 41, 58, 18)),
        WASD_SPRINT_MOUSE(Key.access$0(), Key.access$1(), Key.access$2(), Key.access$3(), Key.access$4(), Key.access$5(), new Key("Sprint", Minecraft.getMinecraft().gameSettings.keyBindSprint, 1, 61, 58, 18));

        private final Key[] keys;
        private int width = 0;
        private int height = 0;

        private KeystrokesMode(Key ... keysIn) {
            Key[] keyArray = this.keys = keysIn;
            int n3 = this.keys.length;
            int n4 = 0;
            while (n4 < n3) {
                Key key = keyArray[n4];
                this.width = Math.max(this.width, key.getX() + key.getWidth());
                this.height = Math.max(this.height, key.getY() + key.getHeight());
                ++n4;
            }
        }

        public int getHeight() {
            return this.height;
        }

        public int getWidth() {
            return this.width;
        }

        public Key[] getKeys() {
            return this.keys;
        }
    }
}

