/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.ingamechat.renderer;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import net.labymod.core.LabyModCore;
import net.labymod.ingamechat.GuiChatCustom;
import net.labymod.ingamechat.IngameChatManager;
import net.labymod.ingamechat.renderer.ChatLine;
import net.labymod.main.LabyMod;
import net.labymod.main.lang.LanguageManager;
import net.labymod.utils.DrawUtils;
import net.labymod.utils.ModColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;

public abstract class ChatRenderer {
    private List<ChatLine> backendComponents = new LinkedList<ChatLine>();
    private List<ChatLine> chatLines = new ArrayList<ChatLine>();
    private int scrollPos;
    public int lastMouseX;
    public int lastMouseY;
    protected int lastRenderedLinesCount = 0;
    private final IngameChatManager manager;
    private final boolean rightBound;
    private final boolean tabMenu;
    public int resizeDragging = 0;
    public boolean moving = false;
    public double movingClickedX;
    public double movingClickedY;
    private String hoveringRoom = null;
    private long animationShift = 0L;

    public ChatRenderer(IngameChatManager manager, boolean tabMenu, boolean rightBound) {
        this.manager = manager;
        this.tabMenu = tabMenu;
        this.rightBound = rightBound;
    }

    public boolean isVisible() {
        GuiScreen currentScreen = Minecraft.getMinecraft().currentScreen;
        return !this.rightBound || currentScreen == null || GuiChatCustom.activeTab == -1;
    }

    public void renderChat(int updateCounter) {
        if (this.chatLines.size() == 0 || !this.isVisible()) {
            return;
        }
        GlStateManager.pushMatrix();
        DrawUtils draw = LabyMod.getInstance().getDrawUtils();
        int fontHeight = LabyModCore.getMinecraft().getFontRenderer().FONT_HEIGHT;
        float scale = this.getChatScale();
        int chatLineCount = this.getLineCount();
        boolean chatOpen = this.isChatOpen();
        float opacity = this.getChatOpacity() * 0.9f + 0.1f;
        int width = this.getVisualWidth() + 1;
        int visibleMessages = 0;
        double totalMessages = 0.0;
        double animationSpeed = 20.0;
        float lineHeight = 10.0f * scale;
        double shift = 0.0;
        if (LabyMod.getSettings().chatAnimation && (shift = ((double)System.currentTimeMillis() - (double)lineHeight * 20.0 - (double)this.animationShift) / 20.0) > 0.0) {
            shift = 0.0;
        }
        double posX = this.getChatPositionX() - (float)(this.rightBound ? width : 0) * scale;
        double posY = (double)this.getChatPositionY() - shift;
        GlStateManager.translate(posX, posY, 0.0);
        GlStateManager.scale(scale, scale, 1.0f);
        if (!this.isChatOpen()) {
            this.scrollPos = 0;
        }
        int i2 = -this.scrollPos;
        for (ChatLine chatline : this.chatLines) {
            double fadeIn;
            int updateCounterDifference;
            if (chatline == null || !chatline.getRoom().equals(this.manager.getSelectedRoom())) continue;
            boolean firstLine = i2 == -this.scrollPos;
            boolean lastLine = i2 == chatLineCount;
            totalMessages += 1.0;
            if ((!lastLine || shift == 0.0) && (++i2 > chatLineCount || i2 <= 0) || (updateCounterDifference = Minecraft.getMinecraft().ingameGUI.getUpdateCounter() - chatline.getUpdateCounter()) >= 200 && !chatOpen) continue;
            ++visibleMessages;
            int alpha = 255;
            if (!chatOpen) {
                double percent = (double)updateCounterDifference / 200.0;
                percent = 1.0 - percent;
                percent *= 10.0;
                percent = LabyModCore.getMath().clamp_double(percent, 0.0, 1.0);
                percent *= percent;
                alpha = (int)(255.0 * percent);
            }
            if (shift != 0.0 && firstLine) {
                fadeIn = 25.5 * -shift;
                alpha = (int)(255.0 - fadeIn);
            }
            if (shift != 0.0 && lastLine) {
                fadeIn = 25.5 * -shift;
                alpha = (int)fadeIn;
            }
            if ((alpha *= (int)opacity) <= 3) continue;
            boolean x2 = false;
            int y2 = (i2 - 1) * -9;
            if (!LabyMod.getSettings().fastChat || chatline.getHighlightColor() != null) {
                DrawUtils.drawRect(0, y2 - fontHeight, 0 + width, y2, chatline.getHighlightColor() != null ? chatline.getHighlightColor() : alpha / 2 << 24);
            }
            GlStateManager.enableBlend();
            draw.drawStringWithShadow(chatline.getMessage(), 1.0, y2 - 8, 0xFFFFFF + (alpha << 24));
            GlStateManager.disableAlpha();
            GlStateManager.disableBlend();
            this.lastRenderedLinesCount = visibleMessages;
        }
        if (chatOpen) {
            double xEnd;
            double totalHeight = totalMessages * (double)fontHeight;
            double visibleHeight = visibleMessages * fontHeight;
            double yStart = (double)this.scrollPos * visibleHeight / totalMessages;
            double yEnd = visibleHeight * visibleHeight / totalHeight;
            double xStart = this.rightBound ? (double)width : -1.0;
            double d2 = xEnd = this.rightBound ? (double)(width + 1) : 0.0;
            if (totalHeight != visibleHeight) {
                DrawUtils.drawRect(xStart, -yStart, xEnd, -yStart - yEnd, -1);
            }
            if (this.moving) {
                double midY = yStart - visibleHeight / 2.0;
                double x2 = -this.getChatPositionX() / scale;
                double y2 = -this.getChatPositionY() / scale;
                float percentX = this.getChatPercentX();
                float percentY = this.getChatPercentY();
                if (this.isRightBound()) {
                    if (percentX < 98.0f) {
                        DrawUtils.drawRect((double)this.getVisualWidth(), midY, x2 + (double)((float)draw.getWidth() / scale) + (double)width, midY + 1.0, Color.YELLOW.getRGB());
                        draw.drawRightString(String.valueOf(ModColor.cl('e')) + (int)(100.0f - percentX) + "%", x2 + (double)((float)(draw.getWidth() - 1) / scale) + (double)width, midY - 10.0);
                    }
                } else if (percentX > 2.0f) {
                    DrawUtils.drawRect(x2, midY, 0.0, midY + 1.0, Color.YELLOW.getRGB());
                    draw.drawString(String.valueOf(ModColor.cl('e')) + (int)percentX + "%", x2 + 1.0, midY - 10.0);
                }
                if (percentY > 50.0f) {
                    if (percentY < 98.0f) {
                        DrawUtils.drawRect((double)width / 2.0, 0.0, (double)width / 2.0 + 1.0, y2 + (double)((float)(draw.getHeight() - 28) / scale), Color.YELLOW.getRGB());
                        draw.drawString(String.valueOf(ModColor.cl('e')) + (int)(100.0f - percentY) + "%", (double)width / 2.0 + 4.0, y2 + (double)((float)(draw.getHeight() - 28) / scale) - 7.0);
                    }
                } else if (percentY > 2.0f) {
                    DrawUtils.drawRect((double)width / 2.0, y2, (double)width / 2.0 + 1.0, -visibleHeight, Color.YELLOW.getRGB());
                    draw.drawString(String.valueOf(ModColor.cl('e')) + (int)percentY + "%", (double)width / 2.0 + 4.0, y2 + 2.0);
                }
            }
        }
        GlStateManager.popMatrix();
        if (LabyMod.getSettings().chatFilter && this.tabMenu && LabyMod.getInstance().getIngameChatManager().getVisibleRooms().size() > 1) {
            this.hoveringRoom = null;
            double roomX = chatOpen ? 2.0 : 1.0;
            double roomY = chatOpen ? (double)(draw.getHeight() - 27) : (double)(draw.getHeight() - 9);
            for (String roomName : LabyMod.getInstance().getIngameChatManager().getVisibleRooms()) {
                boolean hover;
                if (roomName == null) continue;
                Integer unread = LabyMod.getInstance().getIngameChatManager().getRoomsUnread().get(roomName);
                if (unread == null) {
                    unread = 0;
                }
                if (!chatOpen && unread <= 0) continue;
                int notificationColor = Integer.MIN_VALUE;
                boolean selected = roomName.equals(this.manager.getSelectedRoom());
                if (roomName.equals("Global")) {
                    roomName = LanguageManager.translate("ingame_chat_room_global");
                }
                String string = unread > 0 ? String.valueOf(roomName) + ModColor.cl("a") + " [" + unread + "]" : roomName;
                double roomWidth = (double)draw.getStringWidth(string) * 0.7 + 2.0;
                double roomHeight = 8.0;
                boolean bl2 = hover = (double)(this.lastMouseX - 2) >= roomX && (double)(this.lastMouseX - 2) < roomX + roomWidth && posY - (double)this.lastMouseY < 0.0 && posY - (double)this.lastMouseY > -8.0;
                if (!LabyMod.getSettings().fastChat) {
                    DrawUtils.drawRect(roomX, roomY - (double)(selected ? 1 : -1), roomX + roomWidth, roomY + 8.0, Integer.MIN_VALUE);
                }
                draw.drawString(String.valueOf(ModColor.cl(hover ? "e" : (selected ? "f" : "7"))) + string, roomX + 1.0, roomY + 2.0, 0.7);
                roomX += roomWidth + 2.0;
                if (!hover) continue;
                this.hoveringRoom = roomName;
            }
        }
    }

    public void deleteChatLine(int id2) {
        Iterator<ChatLine> iterator = this.chatLines.iterator();
        while (iterator.hasNext()) {
            ChatLine chatline = iterator.next();
            if (chatline.getChatLineId() != id2) continue;
            iterator.remove();
        }
        iterator = this.chatLines.iterator();
        while (iterator.hasNext()) {
            ChatLine chatline2 = iterator.next();
            if (chatline2.getChatLineId() != id2) continue;
            iterator.remove();
            break;
        }
    }

    public void addChatLine(String message, boolean secondChat, String room, Object component, int updateCounter, int chatLineId, Integer highlightColor, boolean refresh) {
        this.chatLines.add(0, new ChatLine(message, secondChat, room, component, updateCounter, chatLineId, highlightColor));
        if (!refresh) {
            this.animationShift = System.currentTimeMillis();
        }
    }

    public void checkLimit() {
        this.reduce(this.chatLines);
    }

    public void handleBackendLines(String message, boolean secondChat, String room, Object component, int chatLineId, int updateCounter, Integer highlightColor) {
        this.backendComponents.add(0, new ChatLine(message, secondChat, room, component, updateCounter, chatLineId, highlightColor));
        this.reduce(this.backendComponents);
    }

    private void reduce(List<ChatLine> list) {
        int lineLimit = LabyMod.getSettings().chatLineLimit;
        if (list.size() > lineLimit) {
            for (String roomName : LabyMod.getInstance().getIngameChatManager().getVisibleRooms()) {
                Iterator<ChatLine> it2 = list.iterator();
                int count = 0;
                while (it2.hasNext()) {
                    ChatLine chatLine = it2.next();
                    if (chatLine.getRoom() != null && chatLine.getRoom().equals(roomName)) {
                        ++count;
                    }
                    if (count <= lineLimit) continue;
                    it2.remove();
                }
            }
        }
    }

    public void clearChatMessages(boolean clearBackend) {
        this.chatLines.clear();
        this.scrollPos = 0;
        if (clearBackend) {
            this.backendComponents.clear();
        }
        if (clearBackend) {
            this.manager.getRoomsUnread().clear();
            this.manager.getVisibleRooms().clear();
            this.manager.getVisibleRooms().add("Global");
            this.manager.setSelectedRoom("Global");
        }
    }

    public void scroll(int amount) {
        this.scrollPos += amount;
        int totalMessages = 0;
        for (ChatLine chatline : this.chatLines) {
            if (!chatline.getRoom().equals(this.manager.getSelectedRoom())) continue;
            ++totalMessages;
        }
        if (this.scrollPos > totalMessages - this.getLineCount()) {
            this.scrollPos = totalMessages - this.getLineCount();
        }
        if (this.scrollPos <= 0) {
            this.scrollPos = 0;
        }
    }

    public boolean isChatOpen() {
        return Minecraft.getMinecraft().currentScreen instanceof GuiChat;
    }

    public int getVisualWidth() {
        int width = LabyModCore.getMath().ceiling_float_int(this.getChatWidth() / this.getChatScale());
        if (width <= 0) {
            width = 100;
        }
        return width + 4;
    }

    public int getVisualHeight() {
        return LabyModCore.getMath().ceiling_float_int((float)this.getChatHeight() / this.getChatScale());
    }

    public int getLineCount() {
        return this.getChatHeight() / 9;
    }

    public boolean isMouseOver() {
        boolean hoverY;
        float x2 = this.getChatPositionX();
        float y2 = this.getChatPositionY();
        float scale = this.getChatScale();
        float width = (float)this.getVisualWidth() * scale;
        boolean bl2 = hoverY = (float)this.lastMouseY < y2 && (float)this.lastMouseY > y2 - (float)this.getChatHeight() * scale;
        if (this.rightBound) {
            return (float)this.lastMouseX < x2 && (float)this.lastMouseX > x2 - width && hoverY;
        }
        return (float)this.lastMouseX > x2 && (float)this.lastMouseX < x2 + width && hoverY;
    }

    public boolean renderHoveringResizeX(boolean forceRender) {
        if (this.chatLines.size() == 0 || !LabyMod.getSettings().scalableChat) {
            return false;
        }
        float x2 = this.getChatPositionX();
        float y2 = this.getChatPositionY();
        float scale = this.getChatScale();
        float width = (float)this.getVisualWidth() * scale;
        float height = (float)(this.lastRenderedLinesCount * LabyModCore.getMinecraft().getFontRenderer().FONT_HEIGHT) * scale;
        float thickness = 2.0f;
        boolean hoverY = (float)this.lastMouseY < y2 && (float)this.lastMouseY > y2 - height;
        boolean hover = false;
        if (this.rightBound) {
            hover = (float)this.lastMouseX < x2 - width + 2.0f && (float)this.lastMouseX > x2 - width - 2.0f && hoverY;
        } else {
            boolean bl2 = hover = (float)this.lastMouseX > x2 + width - 2.0f && (float)this.lastMouseX < x2 + width + 2.0f && hoverY;
        }
        if (hover || forceRender) {
            DrawUtils draw = LabyMod.getInstance().getDrawUtils();
            draw.drawString("|||", this.lastMouseX - 2, this.lastMouseY - 2);
            if (this.rightBound) {
                LabyMod.getInstance().getDrawUtils();
                DrawUtils.drawRect(x2 - width - 1.0f, y2, x2 - width, y2 - height, Integer.MAX_VALUE);
            } else {
                LabyMod.getInstance().getDrawUtils();
                DrawUtils.drawRect(x2 + width, y2, x2 + width + 1.0f, y2 - height, Integer.MAX_VALUE);
            }
        }
        return hover;
    }

    public boolean renderHoveringResizeY(boolean forceRender) {
        if (this.chatLines.size() == 0 || !LabyMod.getSettings().scalableChat) {
            return false;
        }
        float x2 = this.getChatPositionX();
        float y2 = this.getChatPositionY();
        float scale = this.getChatScale();
        float width = (float)this.getVisualWidth() * scale;
        float height = (float)(this.lastRenderedLinesCount * LabyModCore.getMinecraft().getFontRenderer().FONT_HEIGHT) * scale;
        float thickness = 2.0f;
        boolean hoverY = (float)this.lastMouseY > y2 - height - 2.0f && (float)this.lastMouseY < y2 - height;
        boolean hover = false;
        if (this.rightBound) {
            hover = (float)this.lastMouseX < x2 && (float)this.lastMouseX > x2 - width && hoverY;
        } else {
            boolean bl2 = hover = (float)this.lastMouseX > x2 && (float)this.lastMouseX < x2 + width && hoverY;
        }
        if (forceRender) {
            height = (float)(this.getLineCount() * LabyModCore.getMinecraft().getFontRenderer().FONT_HEIGHT) * scale;
        }
        if (hover || forceRender) {
            DrawUtils draw = LabyMod.getInstance().getDrawUtils();
            draw.drawString("==", this.lastMouseX - 5, this.lastMouseY - 3);
            if (this.rightBound) {
                LabyMod.getInstance().getDrawUtils();
                DrawUtils.drawRect(x2 - width, y2 - height - 1.0f, x2, y2 - height, Integer.MAX_VALUE);
            } else {
                LabyMod.getInstance().getDrawUtils();
                DrawUtils.drawRect(x2, y2 - height - 1.0f, x2 + width, y2 - height, Integer.MAX_VALUE);
            }
        }
        return hover;
    }

    public String selectHoveredTab() {
        if (this.hoveringRoom != null) {
            this.manager.setSelectedRoom(this.hoveringRoom);
            this.scrollPos = 0;
        }
        return this.manager.getSelectedRoom();
    }

    public void updateMouse(int mouseX, int mouseY) {
        this.lastMouseX = mouseX;
        this.lastMouseY = mouseY;
    }

    public abstract float getChatOpacity();

    public abstract float getChatScale();

    public abstract float getChatWidth();

    public abstract int getChatHeight();

    public abstract float getChatPositionX();

    public abstract float getChatPositionY();

    public abstract float getChatPercentX();

    public abstract float getChatPercentY();

    public abstract String getLogPrefix();

    public abstract void updateChatSetting(ChatSettingType var1, float var2);

    public abstract void save();

    public List<ChatLine> getBackendComponents() {
        return this.backendComponents;
    }

    public List<ChatLine> getChatLines() {
        return this.chatLines;
    }

    public int getScrollPos() {
        return this.scrollPos;
    }

    public boolean isRightBound() {
        return this.rightBound;
    }

    public static enum ChatSettingType {
        WIDTH,
        HEIGHT,
        X,
        Y;

    }
}

