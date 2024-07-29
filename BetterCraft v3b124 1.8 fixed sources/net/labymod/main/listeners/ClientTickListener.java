/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.main.listeners;

import net.labymod.main.LabyMod;
import net.labymod.settings.LabyModAddonsGui;
import net.labymod.settings.LabyModModuleEditorGui;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;

public class ClientTickListener {
    private boolean cancelSwingAnimation = false;
    private boolean quitted = true;
    private int lastPressedKey = -1;

    private void checkPressedKeys() {
        if (this.isPressed(LabyMod.getSettings().keyModuleEditor)) {
            if (this.canFireKey(LabyMod.getSettings().keyModuleEditor)) {
                Minecraft.getMinecraft().addScheduledTask(new Runnable(){

                    @Override
                    public void run() {
                        Minecraft.getMinecraft().displayGuiScreen(new LabyModModuleEditorGui(Minecraft.getMinecraft().currentScreen));
                    }
                });
            }
        } else if (this.isPressed(LabyMod.getSettings().keyAddons)) {
            if (this.canFireKey(LabyMod.getSettings().keyAddons)) {
                Minecraft.getMinecraft().addScheduledTask(new Runnable(){

                    @Override
                    public void run() {
                        Minecraft.getMinecraft().displayGuiScreen(new LabyModAddonsGui(Minecraft.getMinecraft().currentScreen));
                    }
                });
            }
        } else if (this.isPressed(LabyMod.getSettings().keyToggleHitbox)) {
            if (this.canFireKey(LabyMod.getSettings().keyToggleHitbox)) {
                Minecraft.getMinecraft().getRenderManager().setDebugBoundingBox(!Minecraft.getMinecraft().getRenderManager().isDebugBoundingBox());
            }
        } else if (this.isPressed(LabyMod.getSettings().keyEmote)) {
            if (this.canFireKey(LabyMod.getSettings().keyEmote) && LabyMod.getSettings().emotes) {
                LabyMod.getInstance().getEmoteRegistry().getEmoteSelectorGui().open();
            }
        } else if (this.isPressed(LabyMod.getSettings().keyStickerMenu)) {
            if (this.canFireKey(LabyMod.getSettings().keyStickerMenu) && LabyMod.getSettings().stickers) {
                LabyMod.getInstance().getStickerRegistry().getStickerSelectorGui().open();
            }
        } else {
            if (this.canReleaseKey(LabyMod.getSettings().keyEmote)) {
                LabyMod.getInstance().getEmoteRegistry().getEmoteSelectorGui().close();
            }
            if (this.canReleaseKey(LabyMod.getSettings().keyStickerMenu)) {
                LabyMod.getInstance().getStickerRegistry().getStickerSelectorGui().close();
            }
            this.lastPressedKey = -1;
        }
    }

    private boolean canFireKey(int key) {
        if (this.lastPressedKey != -1 || key == -1) {
            return false;
        }
        this.lastPressedKey = key;
        return true;
    }

    private boolean canReleaseKey(int key) {
        return this.lastPressedKey != -1 && key != -1 && this.lastPressedKey == key;
    }

    private boolean isPressed(int key) {
        return key != -1 && Keyboard.isKeyDown(key);
    }

    public boolean isCancelSwingAnimation() {
        return this.cancelSwingAnimation;
    }

    public void setCancelSwingAnimation(boolean cancelSwingAnimation) {
        this.cancelSwingAnimation = cancelSwingAnimation;
    }
}

