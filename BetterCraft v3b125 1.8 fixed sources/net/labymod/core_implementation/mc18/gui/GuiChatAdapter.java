/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.core_implementation.mc18.gui;

import java.beans.ConstructorProperties;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.labymod.api.events.MessageReceiveEvent;
import net.labymod.core.LabyModCore;
import net.labymod.ingamechat.IngameChatManager;
import net.labymod.ingamechat.renderer.ChatLine;
import net.labymod.ingamechat.renderer.ChatRenderer;
import net.labymod.ingamechat.renderer.MessageData;
import net.labymod.ingamechat.renderer.types.ChatRendererMain;
import net.labymod.ingamechat.renderer.types.ChatRendererSecond;
import net.labymod.ingamechat.tools.filter.Filters;
import net.labymod.main.LabyMod;
import net.labymod.servermanager.ChatDisplayAction;
import net.labymod.utils.ModColor;
import net.labymod.utils.manager.TagManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.gui.GuiUtilRenderComponents;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GuiChatAdapter
extends GuiNewChat {
    private static final Logger logger = LogManager.getLogger();
    private final IngameChatManager manager = LabyMod.getInstance().getIngameChatManager();
    private final ChatRendererMain chatMain = this.manager.getMain();
    private final ChatRendererSecond chatSecond = this.manager.getSecond();
    private final ChatRenderer[] chatRenderers = this.manager.getChatRenderers();
    private final Queue<QueuedMessage> queuedMessages = new ConcurrentLinkedQueue<QueuedMessage>();

    public GuiChatAdapter(Minecraft mcIn) {
        super(mcIn);
    }

    @Override
    public void addToSentMessages(String message) {
        this.manager.addToSentMessages(message);
    }

    @Override
    public List<String> getSentMessages() {
        return this.manager.getSentMessages();
    }

    @Override
    public void drawChat(int updateCounter) {
        int offset = LabyMod.getInstance().getDrawUtils().getHeight() - 48;
        GlStateManager.translate(0.0f, -offset, 0.0f);
        if (Minecraft.getMinecraft().gameSettings.chatVisibility == EntityPlayer.EnumChatVisibility.HIDDEN) {
            return;
        }
        ChatRenderer[] chatRendererArray = this.chatRenderers;
        int n2 = this.chatRenderers.length;
        int n3 = 0;
        while (n3 < n2) {
            ChatRenderer chatRenderer = chatRendererArray[n3];
            chatRenderer.renderChat(updateCounter);
            ++n3;
        }
        if (this.queuedMessages.size() != 0) {
            QueuedMessage queuedMessage = null;
            while ((queuedMessage = this.queuedMessages.poll()) != null) {
                this.setChatLine(queuedMessage.getComponent(), queuedMessage.getChatLineId(), Minecraft.getMinecraft().ingameGUI.getUpdateCounter(), false, false, "Global", null);
            }
        }
    }

    @Override
    public void deleteChatLine(int id2) {
    }

    public void deleteChatLine(ChatRenderer chatRenderer, int id2) {
        chatRenderer.deleteChatLine(id2);
    }

    @Override
    public void clearChatMessages() {
        ChatRenderer[] chatRendererArray = this.chatRenderers;
        int n2 = this.chatRenderers.length;
        int n3 = 0;
        while (n3 < n2) {
            ChatRenderer chatRenderer = chatRendererArray[n3];
            chatRenderer.clearChatMessages(true);
            ++n3;
        }
    }

    @Override
    public void scroll(int amount) {
        if (amount != 1 && amount != -1) {
            amount = (int)((double)amount / 7.0 * (double)LabyMod.getSettings().chatScrollSpeed);
        }
        ChatRenderer[] chatRendererArray = this.chatRenderers;
        int n2 = this.chatRenderers.length;
        int n3 = 0;
        while (n3 < n2) {
            ChatRenderer chatRenderer = chatRendererArray[n3];
            if (chatRenderer.isMouseOver()) {
                chatRenderer.scroll(amount);
            }
            ++n3;
        }
    }

    @Override
    public void printChatMessageWithOptionalDeletion(IChatComponent component, int chatLineId) {
        boolean cancel = false;
        for (MessageReceiveEvent event : LabyMod.getInstance().getEventManager().getMessageReceive()) {
            if (!event.onReceive(component.getFormattedText(), component.getUnformattedText())) continue;
            cancel = true;
        }
        if (!cancel) {
            this.queuedMessages.add(new QueuedMessage(component, chatLineId));
        }
    }

    private void setChatLine(IChatComponent component, int chatLineId, int updateCounter, boolean refresh, boolean secondChat, String room, Integer highlightColor) {
        if (component == null) {
            return;
        }
        ChatRenderer target = null;
        if (!refresh) {
            ChatDisplayAction serverDisplayActionResponse = LabyMod.getInstance().getServerManager().handleChatMessage(component.getUnformattedText(), component.getFormattedText());
            MessageData messageData = this.manager.handleSwap(serverDisplayActionResponse, LabyModCore.getMinecraft().getChatComponent(component));
            if (messageData == null) {
                return;
            }
            secondChat = messageData.isDisplayInSecondChat();
            component = (IChatComponent)TagManager.tagComponent(component);
            Filters.Filter filter = messageData.getFilter();
            if (filter != null) {
                room = filter.getRoom();
                if (filter.isHighlightMessage()) {
                    highlightColor = ModColor.toRGB(filter.getHighlightColorR(), filter.getHighlightColorG(), filter.getHighlightColorB(), 120);
                }
            }
        }
        ChatRenderer chatRenderer = target = secondChat ? this.chatSecond : this.chatMain;
        if (chatLineId != 0) {
            this.deleteChatLine(target, chatLineId);
        }
        int width = target.getVisualWidth();
        List<IChatComponent> list = GuiUtilRenderComponents.splitText(component, width, LabyModCore.getMinecraft().getFontRenderer(), false, false);
        for (IChatComponent ichatcomponent : list) {
            target.addChatLine(ichatcomponent.getFormattedText(), secondChat, room, ichatcomponent, updateCounter, chatLineId, highlightColor, refresh);
        }
        target.checkLimit();
        if (!refresh) {
            target.handleBackendLines(component.getFormattedText(), secondChat, room, component, chatLineId, updateCounter, highlightColor);
            this.manager.handleUnread(room);
            logger.info("[" + target.getLogPrefix() + "] " + component.getUnformattedText());
        }
    }

    @Override
    public void refreshChat() {
        ChatRenderer[] chatRendererArray = this.chatRenderers;
        int n2 = this.chatRenderers.length;
        int n3 = 0;
        while (n3 < n2) {
            ChatRenderer chatRenderer = chatRendererArray[n3];
            chatRenderer.clearChatMessages(false);
            int i2 = chatRenderer.getBackendComponents().size() - 1;
            while (i2 >= 0) {
                ChatLine chatLine = chatRenderer.getBackendComponents().get(i2);
                this.setChatLine((IChatComponent)chatLine.getComponent(), chatLine.getChatLineId(), chatLine.getUpdateCounter(), true, chatLine.isSecondChat(), chatLine.getRoom(), chatLine.getHighlightColor());
                --i2;
            }
            ++n3;
        }
    }

    @Override
    public IChatComponent getChatComponent(int mouseX, int mouseY) {
        ChatRenderer[] chatRendererArray = this.chatRenderers;
        int n2 = this.chatRenderers.length;
        int n3 = 0;
        while (n3 < n2) {
            ChatRenderer chatRenderer = chatRendererArray[n3];
            Object component = this.getHoveringComponent(chatRenderer);
            if (component != null) {
                return (IChatComponent)component;
            }
            ++n3;
        }
        return null;
    }

    private Object getHoveringComponent(ChatRenderer chatRenderer) {
        if (!chatRenderer.isChatOpen() || !chatRenderer.isMouseOver()) {
            return null;
        }
        float mouseX = chatRenderer.isRightBound() ? (float)chatRenderer.lastMouseX - chatRenderer.getChatPositionX() + chatRenderer.getChatWidth() + 3.0f : -(chatRenderer.getChatPositionX() - (float)chatRenderer.lastMouseX);
        float mouseY = (float)(-chatRenderer.lastMouseY) + chatRenderer.getChatPositionY();
        mouseX /= this.getChatScale();
        mouseY /= this.getChatScale();
        LinkedList<ChatLine> list = new LinkedList<ChatLine>();
        for (ChatLine chatline : chatRenderer.getChatLines()) {
            if (chatline == null || !chatline.getRoom().equals(this.manager.getSelectedRoom())) continue;
            list.add(chatline);
        }
        int hoveredLine = (int)mouseY / Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT + chatRenderer.getScrollPos();
        if (hoveredLine < 0 || hoveredLine >= list.size()) {
            return null;
        }
        int x2 = 0;
        ChatLine chatline2 = (ChatLine)list.get(hoveredLine);
        for (IChatComponent ichatcomponent : (IChatComponent)chatline2.getComponent()) {
            if (!(ichatcomponent instanceof ChatComponentText) || !((float)(x2 += Minecraft.getMinecraft().fontRendererObj.getStringWidth(GuiUtilRenderComponents.func_178909_a(((ChatComponentText)ichatcomponent).getChatComponentText_TextValue(), false))) > mouseX)) continue;
            return ichatcomponent;
        }
        return null;
    }

    private class QueuedMessage {
        private final IChatComponent component;
        private final int chatLineId;

        @ConstructorProperties(value={"component", "chatLineId"})
        public QueuedMessage(IChatComponent component, int chatLineId) {
            this.component = component;
            this.chatLineId = chatLineId;
        }

        public IChatComponent getComponent() {
            return this.component;
        }

        public int getChatLineId() {
            return this.chatLineId;
        }
    }
}

