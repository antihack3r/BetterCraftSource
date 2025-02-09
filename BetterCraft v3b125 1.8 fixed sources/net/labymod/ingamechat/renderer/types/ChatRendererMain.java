/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.ingamechat.renderer.types;

import net.labymod.core.LabyModCore;
import net.labymod.ingamechat.IngameChatManager;
import net.labymod.ingamechat.renderer.ChatRenderer;
import net.labymod.main.LabyMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;

public class ChatRendererMain
extends ChatRenderer {
    private GameSettings gameSettings;

    public ChatRendererMain(IngameChatManager manager) {
        super(manager, true, false);
        this.gameSettings = Minecraft.getMinecraft().gameSettings;
    }

    @Override
    public String getLogPrefix() {
        return "CHAT";
    }

    @Override
    public float getChatWidth() {
        float max = 320.0f;
        float min = 40.0f;
        return LabyModCore.getMath().floor_float(this.gameSettings.chatWidth * 280.0f + 40.0f);
    }

    @Override
    public int getChatHeight() {
        float height = this.isChatOpen() ? this.gameSettings.chatHeightFocused : this.gameSettings.chatHeightUnfocused;
        float max = 180.0f;
        float min = 20.0f;
        return LabyModCore.getMath().floor_float(height * 160.0f + 20.0f);
    }

    @Override
    public float getChatScale() {
        return this.gameSettings.chatScale;
    }

    @Override
    public float getChatOpacity() {
        return Minecraft.getMinecraft().gameSettings.chatOpacity;
    }

    @Override
    public float getChatPositionX() {
        double screenWidth = LabyMod.getInstance().getDrawUtils().getWidth();
        float percent = this.getChatPercentX();
        double total = screenWidth / 2.0 + (double)this.getChatWidth();
        if (percent < 1.0f) {
            return 2.0f;
        }
        double pos = total / 100.0 * (double)percent;
        if (pos > screenWidth - (double)this.getChatWidth()) {
            pos = screenWidth - (double)this.getChatWidth();
        }
        return (float)pos;
    }

    @Override
    public float getChatPositionY() {
        float height = (float)(this.lastRenderedLinesCount * LabyModCore.getMinecraft().getFontRenderer().FONT_HEIGHT) * this.getChatScale();
        double screenHeight = LabyMod.getInstance().getDrawUtils().getHeight() - 28;
        float percent = this.getChatPercentY();
        if (percent > 99.0f) {
            return (float)screenHeight;
        }
        if (percent < 50.0f) {
            return (float)((double)(height + 2.0f) + screenHeight / 100.0 * (double)percent);
        }
        return (float)(screenHeight / 100.0 * (double)percent);
    }

    @Override
    public float getChatPercentX() {
        return LabyMod.getSettings().mainChatPercentX;
    }

    @Override
    public float getChatPercentY() {
        return LabyMod.getSettings().mainChatPercentY;
    }

    @Override
    public void updateChatSetting(ChatRenderer.ChatSettingType type, float value) {
        switch (type) {
            case WIDTH: {
                this.gameSettings.chatWidth = value;
                break;
            }
            case HEIGHT: {
                this.gameSettings.chatHeightFocused = value;
                this.gameSettings.chatHeightUnfocused = value / 2.0f;
                break;
            }
            case X: {
                LabyMod.getSettings().mainChatPercentX = value;
                break;
            }
            case Y: {
                LabyMod.getSettings().mainChatPercentY = value;
            }
        }
    }

    @Override
    public void save() {
        Minecraft.getMinecraft().gameSettings.saveOptions();
    }
}

