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

public class ChatRendererSecond
extends ChatRenderer {
    private GameSettings gameSettings;

    public ChatRendererSecond(IngameChatManager manager) {
        super(manager, false, true);
        this.gameSettings = Minecraft.getMinecraft().gameSettings;
    }

    @Override
    public String getLogPrefix() {
        return "SECOND CHAT";
    }

    @Override
    public float getChatWidth() {
        return LabyMod.getSettings().secondChatWidth;
    }

    @Override
    public int getChatHeight() {
        return this.isChatOpen() ? LabyMod.getSettings().secondChatHeight : LabyMod.getSettings().secondChatHeight / 2;
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
        if (percent > 99.0f) {
            return (float)(screenWidth - 2.0);
        }
        double pos = screenWidth - screenWidth / 100.0 * (double)(100.0f - percent);
        if (pos < (double)this.getChatWidth()) {
            pos = this.getChatWidth();
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
        return LabyMod.getSettings().secondChatPercentX;
    }

    @Override
    public float getChatPercentY() {
        return LabyMod.getSettings().secondChatPercentY;
    }

    @Override
    public void updateChatSetting(ChatRenderer.ChatSettingType type, float value) {
        switch (type) {
            case WIDTH: {
                int min = 40;
                int max = 320;
                LabyMod.getSettings().secondChatWidth = (int)(value * 280.0f + 40.0f);
                break;
            }
            case HEIGHT: {
                int min = 20;
                int max = 180;
                LabyMod.getSettings().secondChatHeight = (int)(value * 160.0f + 20.0f);
                break;
            }
            case X: {
                LabyMod.getSettings().secondChatPercentX = value;
                break;
            }
            case Y: {
                LabyMod.getSettings().secondChatPercentY = value;
            }
        }
    }

    @Override
    public void save() {
        LabyMod.getMainConfig().save();
    }
}

