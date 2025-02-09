/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;
import me.nzxtercode.bettercraft.client.gui.section.GuiUISettings;
import me.nzxtercode.bettercraft.client.utils.TextAnimationUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiUtilRenderComponents;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Mouse;

public class GuiNewChat
extends Gui {
    private static final Logger logger = LogManager.getLogger();
    private final Minecraft mc;
    private final List<String> sentMessages = Lists.newArrayList();
    private final List<ChatLine> chatLines = Lists.newArrayList();
    private final List<ChatLine> drawnChatLines = Lists.newArrayList();
    private int scrollPos;
    private boolean isScrolled;

    public GuiNewChat(Minecraft mcIn) {
        this.mc = mcIn;
    }

    public void drawChat(int updateCounter) {
        if (this.mc.gameSettings.chatVisibility != EntityPlayer.EnumChatVisibility.HIDDEN) {
            int i2 = this.getLineCount();
            boolean flag = false;
            int j2 = 0;
            int k2 = this.drawnChatLines.size();
            float f2 = this.mc.gameSettings.chatOpacity * 0.9f + 0.1f;
            if (k2 > 0) {
                if (this.getChatOpen()) {
                    flag = true;
                }
                float f1 = this.getChatScale();
                int l2 = MathHelper.ceiling_float_int((float)this.getChatWidth() / f1);
                GlStateManager.pushMatrix();
                GlStateManager.translate(2.0f, 20.0f, 0.0f);
                GlStateManager.scale(f1, f1, 1.0f);
                int i1 = 0;
                while (i1 + this.scrollPos < this.drawnChatLines.size() && i1 < i2) {
                    int j1;
                    ChatLine chatline = this.drawnChatLines.get(i1 + this.scrollPos);
                    if (chatline != null && ((j1 = updateCounter - chatline.getUpdatedCounter()) < 200 || flag)) {
                        double d0 = (double)j1 / 200.0;
                        d0 = 1.0 - d0;
                        d0 *= 10.0;
                        d0 = MathHelper.clamp_double(d0, 0.0, 1.0);
                        d0 *= d0;
                        int l1 = (int)(255.0 * d0);
                        if (flag) {
                            l1 = 255;
                        }
                        l1 = (int)((float)l1 * f2);
                        ++j2;
                        if (l1 > 3) {
                            int i22 = 0;
                            int j22 = -i1 * 9;
                            if (!GuiUISettings.enabledBackgrounds[2]) {
                                GuiNewChat.drawRect(i22, j22 - 1, i22 + l2 + 4, j22 - 10, l1 / 2 << 24);
                            }
                            String s2 = chatline.getChatComponent().getFormattedText();
                            GlStateManager.enableBlend();
                            this.mc.fontRendererObj.drawStringWithShadow(s2, i22, j22 - 10, 0xFFFFFF + (l1 << 24));
                            GlStateManager.disableAlpha();
                            GlStateManager.disableBlend();
                        }
                    }
                    ++i1;
                }
                if (flag) {
                    int k22 = this.mc.fontRendererObj.FONT_HEIGHT;
                    GlStateManager.translate(-3.0f, 0.0f, 0.0f);
                    int l22 = k2 * k22 + k2;
                    int i3 = j2 * k22 + j2;
                    int j3 = this.scrollPos * i3 / k2;
                    int k1 = i3 * i3 / l22;
                    if (l22 != i3) {
                        int k3 = j3 > 0 ? 170 : 96;
                        int l3 = this.isScrolled ? 0xCC3333 : 0x3333AA;
                        GuiNewChat.drawRect(0, -j3, 2, -j3 - k1, l3 + (k3 << 24));
                        GuiNewChat.drawRect(2, -j3, 1, -j3 - k1, 0xCCCCCC + (k3 << 24));
                    }
                }
                GlStateManager.popMatrix();
            }
        }
    }

    public void clearChatMessages() {
        this.drawnChatLines.clear();
        this.chatLines.clear();
        this.sentMessages.clear();
    }

    public void printChatMessage(IChatComponent chatComponent) {
        if (EnumChatFormatting.getTextWithoutFormattingCodes(chatComponent.getUnformattedText()).replaceAll("\n", "").replaceAll("\u137e", "").trim().isEmpty()) {
            return;
        }
        this.printChatMessageWithOptionalDeletion(new ChatComponentText(String.format("%s%s%s", new Object[]{EnumChatFormatting.GRAY, new SimpleDateFormat("[HH:mm:ss] ").format(System.currentTimeMillis()), EnumChatFormatting.RESET})).appendSibling(chatComponent), 0);
    }

    public void printChatMessageWithOptionalDeletion(IChatComponent chatComponent, int chatLineId) {
        this.setChatLine(chatComponent, chatLineId, this.mc.ingameGUI.getUpdateCounter(), false);
        logger.info("[CHAT] " + chatComponent.getUnformattedText());
    }

    private void setChatLine(IChatComponent chatComponent, int chatLineId, int updateCounter, boolean displayOnly) {
        if (chatLineId != 0) {
            this.deleteChatLine(chatLineId);
        }
        int i2 = MathHelper.floor_float((float)this.getChatWidth() / this.getChatScale());
        List<IChatComponent> list = GuiUtilRenderComponents.splitText(chatComponent, i2, this.mc.fontRendererObj, false, false);
        boolean flag = this.getChatOpen();
        for (IChatComponent ichatcomponent : list) {
            if (flag && this.scrollPos > 0) {
                this.isScrolled = true;
                this.scroll(1);
            }
            if (GuiUISettings.enabledAnimations[1]) {
                ChatLine line = new ChatLine(updateCounter, new ChatComponentText(""), chatLineId);
                TextAnimationUtils.writeTextAnimation(ichatcomponent.getUnformattedText(), 20L, value -> line.setChatComponent(new ChatComponentText((String)value)), value -> line.setChatComponent(ichatcomponent));
                this.drawnChatLines.add(0, line);
                continue;
            }
            this.drawnChatLines.add(0, new ChatLine(updateCounter, ichatcomponent, chatLineId));
        }
        while (this.drawnChatLines.size() > 100) {
            this.drawnChatLines.remove(this.drawnChatLines.size() - 1);
        }
        if (!displayOnly) {
            this.chatLines.add(0, new ChatLine(updateCounter, chatComponent, chatLineId));
            while (this.chatLines.size() > 100) {
                this.chatLines.remove(this.chatLines.size() - 1);
            }
        }
    }

    public void refreshChat() {
        this.drawnChatLines.clear();
        this.resetScroll();
        int i2 = this.chatLines.size() - 1;
        while (i2 >= 0) {
            ChatLine chatline = this.chatLines.get(i2);
            this.setChatLine(chatline.getChatComponent(), chatline.getChatLineID(), chatline.getUpdatedCounter(), true);
            --i2;
        }
    }

    public List<String> getSentMessages() {
        return this.sentMessages;
    }

    public void addToSentMessages(String message) {
        if (this.sentMessages.isEmpty() || !this.sentMessages.get(this.sentMessages.size() - 1).equals(message)) {
            this.sentMessages.add(message);
        }
    }

    public void resetScroll() {
        this.scrollPos = 0;
        this.isScrolled = false;
    }

    public void scroll(int amount) {
        this.scrollPos += amount;
        int i2 = this.drawnChatLines.size();
        if (this.scrollPos > i2 - this.getLineCount()) {
            this.scrollPos = i2 - this.getLineCount();
        }
        if (this.scrollPos <= 0) {
            this.scrollPos = 0;
            this.isScrolled = false;
        }
    }

    public IChatComponent getChatComponent(int mouseX, int mouseY) {
        if (!this.getChatOpen()) {
            return null;
        }
        ScaledResolution scaledresolution = new ScaledResolution(this.mc);
        int i2 = scaledresolution.getScaleFactor();
        float f2 = this.getChatScale();
        int j2 = mouseX / i2 - 3;
        int k2 = mouseY / i2 - 27;
        j2 = MathHelper.floor_float((float)j2 / f2);
        k2 = MathHelper.floor_float((float)k2 / f2);
        if (j2 >= 0 && k2 >= 0) {
            int l2 = Math.min(this.getLineCount(), this.drawnChatLines.size());
            if (j2 <= MathHelper.floor_float((float)this.getChatWidth() / this.getChatScale()) && k2 < this.mc.fontRendererObj.FONT_HEIGHT * l2 + l2) {
                int i1 = k2 / this.mc.fontRendererObj.FONT_HEIGHT + this.scrollPos;
                if (i1 >= 0 && i1 < this.drawnChatLines.size()) {
                    ChatLine chatline = this.drawnChatLines.get(i1);
                    if (Mouse.isButtonDown(1)) {
                        return chatline.getChatComponent();
                    }
                    int j1 = 0;
                    for (IChatComponent ichatcomponent : chatline.getChatComponent()) {
                        if (!(ichatcomponent instanceof ChatComponentText) || (j1 += this.mc.fontRendererObj.getStringWidth(GuiUtilRenderComponents.func_178909_a(((ChatComponentText)ichatcomponent).getChatComponentText_TextValue(), false))) <= j2) continue;
                        return ichatcomponent;
                    }
                }
                return null;
            }
            return null;
        }
        return null;
    }

    public boolean getChatOpen() {
        return this.mc.currentScreen instanceof GuiChat;
    }

    public void deleteChatLine(int id2) {
        Iterator<ChatLine> iterator = this.drawnChatLines.iterator();
        while (iterator.hasNext()) {
            ChatLine chatline = iterator.next();
            if (chatline.getChatLineID() != id2) continue;
            iterator.remove();
        }
        iterator = this.chatLines.iterator();
        while (iterator.hasNext()) {
            ChatLine chatline1 = iterator.next();
            if (chatline1.getChatLineID() != id2) continue;
            iterator.remove();
            break;
        }
    }

    public int getChatWidth() {
        return GuiNewChat.calculateChatboxWidth(this.mc.gameSettings.chatWidth);
    }

    public int getChatHeight() {
        return GuiNewChat.calculateChatboxHeight(this.getChatOpen() ? this.mc.gameSettings.chatHeightFocused : this.mc.gameSettings.chatHeightUnfocused);
    }

    public float getChatScale() {
        return this.mc.gameSettings.chatScale;
    }

    public static int calculateChatboxWidth(float scale) {
        int i2 = 320;
        int j2 = 40;
        return MathHelper.floor_float(scale * (float)(i2 - j2) + (float)j2);
    }

    public static int calculateChatboxHeight(float scale) {
        int i2 = 180;
        int j2 = 20;
        return MathHelper.floor_float(scale * (float)(i2 - j2) + (float)j2);
    }

    public int getLineCount() {
        return this.getChatHeight() / 9;
    }
}

