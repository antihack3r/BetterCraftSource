/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.labyconnect.gui.elements;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.labymod.gui.elements.Scrollbar;
import net.labymod.gui.layout.WindowElement;
import net.labymod.labyconnect.gui.GuiFriendsLayout;
import net.labymod.labyconnect.log.MessageChatComponent;
import net.labymod.labyconnect.log.SingleChat;
import net.labymod.labyconnect.user.ChatUser;
import net.labymod.main.LabyMod;
import net.labymod.utils.DrawUtils;
import net.labymod.utils.ModColor;
import net.minecraft.client.gui.GuiButton;

public class WinChatlog
extends WindowElement<GuiFriendsLayout> {
    private static final Pattern URL_PATTERNS = Pattern.compile("(?:^|[\\W])((ht|f)tp(s?):\\/\\/|www\\.)(([\\w\\-]+\\.){1,}?([\\w\\-.~]+\\/?)*[\\p{Alnum}.,%_=?&#\\-+()\\[\\]\\*$~@!:/{};']*)", 42);
    private Scrollbar scrollbar;
    private ChatUser clientUser;
    private long prevMessageTime = 0L;
    private String lastRenderHoveringMessage = null;
    private DateFormat timeDateFormat = new SimpleDateFormat("HH:mm");

    public WinChatlog(GuiFriendsLayout chatLayout, ChatUser clientUser) {
        super(chatLayout);
        this.clientUser = clientUser;
    }

    @Override
    protected void init(List<GuiButton> buttonlist, int left, int top, int right, int bottom) {
        this.scrollbar = new Scrollbar(0);
        this.scrollbar.init();
        this.scrollbar.setSpeed(15);
        this.scrollbar.setPosition(this.right - 4, this.top + 4, this.right, this.bottom - 4);
        this.updateGuiElements();
    }

    private void updateGuiElements() {
        long sentTime;
        if (GuiFriendsLayout.selectedUser == null) {
            return;
        }
        double totalEntryHeight = 0.0;
        SingleChat singleChat = LabyMod.getInstance().getLabyConnect().getChatlogManager().getChat(GuiFriendsLayout.selectedUser);
        ArrayList<MessageChatComponent> messages = new ArrayList<MessageChatComponent>();
        messages.addAll(singleChat.getMessages());
        if (messages.size() != 0) {
            for (MessageChatComponent messageChatComponent : messages) {
                totalEntryHeight += (double)this.drawMessageEntry(messageChatComponent, totalEntryHeight, false, 0, 0);
            }
            totalEntryHeight = totalEntryHeight / (double)messages.size() + 2.0;
        }
        long l2 = sentTime = singleChat.getMessages().size() == 0 ? 0L : singleChat.getMessages().get(singleChat.getMessages().size() - 1).getSentTime();
        if (this.prevMessageTime != sentTime) {
            this.prevMessageTime = sentTime;
            this.scrollbar.requestBottom();
        }
        this.scrollbar.setEntryHeight(totalEntryHeight);
        this.scrollbar.update(messages.size());
    }

    @Override
    public void draw(int mouseX, int mouseY) {
        super.draw(mouseX, mouseY);
        if (GuiFriendsLayout.selectedUser == null) {
            return;
        }
        this.updateGuiElements();
        this.lastRenderHoveringMessage = null;
        SingleChat singleChat = LabyMod.getInstance().getLabyConnect().getChatlogManager().getChat(GuiFriendsLayout.selectedUser);
        List<MessageChatComponent> messages = singleChat.getMessages();
        try {
            double entryY = (double)this.top + this.scrollbar.getScrollY() + 4.0;
            for (MessageChatComponent messageChatComponent : messages) {
                entryY += (double)this.drawMessageEntry(messageChatComponent, entryY, true, mouseX, mouseY);
                entryY += 2.0;
            }
        }
        catch (Exception error) {
            error.printStackTrace();
        }
        this.scrollbar.draw();
    }

    @Override
    public void actionPerformed(GuiButton button) {
    }

    private int drawMessageEntry(MessageChatComponent messageChatComponent, double entryY, boolean shouldDraw, int mouseX, int mouseY) {
        DrawUtils drawUtils = LabyMod.getInstance().getDrawUtils();
        String meName = LabyMod.getInstance().getPlayerName();
        boolean isMe = messageChatComponent.getSender().equals(meName);
        String message = messageChatComponent.getMessage();
        int margineWidth = 20;
        int margineBorder = 30;
        int paddingWidth = 11;
        int paddingHeight = 2;
        int spaceBetweenText = 10;
        int headSize = 12;
        double maxWidth = (double)(this.right - this.left) - (double)(this.right - this.left) / 5.0 - 20.0 - 5.5 - 20.0;
        List<String> messageList = drawUtils.listFormattedStringToWidth(message, (int)maxWidth);
        int entryHeight = messageList.size() * 10 + 4;
        int firstMessageWidth = drawUtils.getStringWidth(message);
        if (!shouldDraw) {
            return entryHeight;
        }
        if (isMe) {
            double entryLeft = (double)(this.right - 30) - maxWidth - 20.0 - 5.5;
            double entryRight = this.right - 30 - 20;
            if (messageList.size() == 1) {
                entryLeft = this.right - 30 - firstMessageWidth - 20 - 11;
            }
            double entryTop = entryY;
            double entryBottom = entryY + (double)entryHeight;
            DrawUtils.drawRect(entryLeft, entryTop, entryRight, entryBottom, ModColor.toRGB(80, 80, 80, 60));
            double lineYAdd = entryTop + 2.0 + 1.0;
            for (String messageLine : messageList) {
                if ((double)mouseX > entryLeft && (double)mouseX < entryRight && (double)mouseY > lineYAdd && (double)mouseY < lineYAdd + 10.0) {
                    this.lastRenderHoveringMessage = messageLine;
                    if (this.containsUrl(messageLine)) {
                        messageLine = String.valueOf(ModColor.cl('9')) + messageLine + ModColor.cl('f');
                    }
                }
                drawUtils.drawString(messageLine, entryLeft + 5.5, lineYAdd);
                lineYAdd += 10.0;
            }
            if (GuiFriendsLayout.selectedUser.isParty()) {
                LabyMod.getInstance().getDrawUtils().drawPlayerHead(messageChatComponent.getSender(), (int)(entryRight + 10.0 - 6.0), (int)(entryTop + (entryBottom - entryTop) / 2.0) - 6, 12);
            } else {
                drawUtils.drawPlayerHead(this.clientUser.getGameProfile(), (int)(entryRight + 10.0 - 6.0), (int)(entryTop + (entryBottom - entryTop) / 2.0) - 6, 12);
            }
            String displayTime = this.timeDateFormat.format(messageChatComponent.getSentTime());
            drawUtils.drawRightString(displayTime, entryLeft - 2.0, entryTop + 2.0, 0.5);
        } else {
            double entryLeft = this.left + 30 + 20;
            double entryRight = (double)(this.left + 30) + maxWidth + 20.0 + 5.5;
            if (messageList.size() == 1) {
                entryRight = this.left + 30 + firstMessageWidth + 20 + 11;
            }
            double entryTop = entryY;
            double entryBottom = entryY + (double)entryHeight;
            DrawUtils.drawRect(entryLeft, entryTop, entryRight, entryBottom, ModColor.toRGB(80, 80, 80, 60));
            double lineYAdd = entryTop + 2.0 + 1.0;
            for (String messageLine : messageList) {
                if ((double)mouseX > entryLeft && (double)mouseX < entryRight && (double)mouseY > lineYAdd && (double)mouseY < lineYAdd + 10.0) {
                    this.lastRenderHoveringMessage = messageLine;
                    if (this.containsUrl(messageLine)) {
                        messageLine = String.valueOf(ModColor.cl('9')) + messageLine + ModColor.cl('f');
                    }
                }
                drawUtils.drawString(messageLine, entryLeft + 5.0, lineYAdd);
                lineYAdd += 10.0;
            }
            if (GuiFriendsLayout.selectedUser.isParty()) {
                LabyMod.getInstance().getDrawUtils().drawPlayerHead(messageChatComponent.getSender(), (int)(entryLeft - 10.0 - 6.0), (int)(entryTop + (entryBottom - entryTop) / 2.0 - 6.0), 12);
            } else {
                drawUtils.drawPlayerHead(GuiFriendsLayout.selectedUser.getGameProfile(), (int)(entryLeft - 10.0 - 6.0), (int)(entryTop + (entryBottom - entryTop) / 2.0 - 6.0), 12);
            }
            String displayTime = this.timeDateFormat.format(messageChatComponent.getSentTime());
            drawUtils.drawString(displayTime, entryRight + 2.0, entryTop + 2.0, 0.5);
        }
        return entryHeight;
    }

    private boolean containsUrl(String message) {
        return message.contains("http://") || message.contains("https://");
    }

    public void handleMouseInput() {
        if (this.isMouseOver()) {
            this.scrollbar.mouseInput();
        }
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        Matcher matcher;
        this.scrollbar.mouseAction(mouseX, mouseY, Scrollbar.EnumMouseAction.CLICKED);
        if (this.lastRenderHoveringMessage != null && (matcher = URL_PATTERNS.matcher(this.lastRenderHoveringMessage)).find()) {
            int matchStart = matcher.start(1);
            int matchEnd = matcher.end();
            String website = this.lastRenderHoveringMessage.substring(matchStart, matchEnd);
            LabyMod.getInstance().openWebpage(website, true);
        }
        return false;
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
    }

    @Override
    public void mouseClickMove(int mouseX, int mouseY) {
        this.scrollbar.mouseAction(mouseX, mouseY, Scrollbar.EnumMouseAction.DRAGGING);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
    }

    @Override
    public void mouseInput() {
    }

    public boolean isScrolledToTop() {
        return this.scrollbar.getScrollY() == 0.0;
    }

    @Override
    public void updateScreen() {
    }
}

