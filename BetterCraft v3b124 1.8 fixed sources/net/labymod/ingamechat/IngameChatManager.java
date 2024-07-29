/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.ingamechat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import net.labymod.core.ChatComponent;
import net.labymod.core.LabyModCore;
import net.labymod.ingamechat.renderer.ChatRenderer;
import net.labymod.ingamechat.renderer.EnumMouseAction;
import net.labymod.ingamechat.renderer.MessageData;
import net.labymod.ingamechat.renderer.types.ChatRendererMain;
import net.labymod.ingamechat.renderer.types.ChatRendererSecond;
import net.labymod.ingamechat.tools.filter.FilterChatManager;
import net.labymod.ingamechat.tools.filter.Filters;
import net.labymod.main.LabyMod;
import net.labymod.servermanager.ChatDisplayAction;
import net.labymod.utils.DrawUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.ResourceLocation;

public class IngameChatManager {
    public static final String GLOBAL = "Global";
    private ChatRendererMain main = new ChatRendererMain(this);
    private ChatRendererSecond second = new ChatRendererSecond(this);
    private ChatRenderer[] chatRenderers = new ChatRenderer[]{this.main, this.second};
    private GameSettings gameSettings;
    private List<String> sentMessages;
    private List<String> backendRooms;
    private List<String> visibleRooms;
    private Map<String, Integer> roomsUnread;
    private String selectedRoom;

    public IngameChatManager() {
        this.gameSettings = Minecraft.getMinecraft().gameSettings;
        this.sentMessages = new LinkedList<String>();
        this.backendRooms = new ArrayList<String>();
        this.visibleRooms = new ArrayList<String>();
        this.roomsUnread = new HashMap<String, Integer>();
        this.selectedRoom = GLOBAL;
    }

    public MessageData handleSwap(ChatDisplayAction chatDisplayAction, ChatComponent chatComponent) {
        boolean displayInSecondChat;
        Filters.Filter filter = FilterChatManager.getFilterComponent(chatComponent);
        if (chatDisplayAction == ChatDisplayAction.NORMAL && filter != null && filter.isDisplayInSecondChat()) {
            chatDisplayAction = ChatDisplayAction.SWAP;
        }
        if (filter != null && filter.isPlaySound()) {
            LabyModCore.getMinecraft().playSound(new ResourceLocation(filter.getSoundPath()), 1.0f);
        }
        if (chatDisplayAction == ChatDisplayAction.HIDE || filter != null && filter.isHideMessage()) {
            return null;
        }
        boolean bl2 = displayInSecondChat = chatDisplayAction == ChatDisplayAction.SWAP;
        if (LabyMod.getSettings().chatPositionRight) {
            displayInSecondChat = !displayInSecondChat;
        }
        return new MessageData(displayInSecondChat, filter);
    }

    public void addToSentMessages(String message) {
        if (this.sentMessages.isEmpty() || !this.sentMessages.get(this.sentMessages.size() - 1).equals(message)) {
            this.sentMessages.add(message);
        }
    }

    public void handleMouse(int mouseX, int mouseY, int mouseButton, EnumMouseAction action) {
        ChatRenderer[] chatRendererArray = this.chatRenderers;
        int n2 = this.chatRenderers.length;
        int n3 = 0;
        while (n3 < n2) {
            ChatRenderer chatRenderer = chatRendererArray[n3];
            if (chatRenderer.isVisible()) {
                String selected;
                chatRenderer.updateMouse(mouseX, mouseY);
                if (action == EnumMouseAction.CLICKED || action == EnumMouseAction.RENDER) {
                    boolean isDragging = chatRenderer.resizeDragging != 0;
                    if (chatRenderer.renderHoveringResizeX(chatRenderer.resizeDragging == 1) && !isDragging && action == EnumMouseAction.CLICKED) {
                        chatRenderer.resizeDragging = 1;
                    } else if (chatRenderer.renderHoveringResizeY(chatRenderer.resizeDragging == 2) && !isDragging && action == EnumMouseAction.CLICKED) {
                        chatRenderer.resizeDragging = 2;
                    }
                }
                if (action == EnumMouseAction.RELEASED && chatRenderer.resizeDragging != 0) {
                    chatRenderer.resizeDragging = 0;
                    chatRenderer.save();
                }
                if ((action == EnumMouseAction.DRAGGING || action == EnumMouseAction.CLICKED || action == EnumMouseAction.RENDER) && chatRenderer.resizeDragging != 0) {
                    float value;
                    boolean horizontal = chatRenderer.resizeDragging == 1;
                    int min = horizontal ? 40 : 20;
                    int max = horizontal ? 320 : 180;
                    float valueX = (chatRenderer.isRightBound() ? chatRenderer.getChatPositionX() - (float)mouseX : (float)mouseX - chatRenderer.getChatPositionX()) - 4.0f;
                    float f2 = value = horizontal ? valueX : chatRenderer.getChatPositionY() - (float)mouseY;
                    if (!horizontal) {
                        value /= chatRenderer.getChatScale();
                    }
                    if (value > (float)max) {
                        value = max;
                    }
                    if (value < (float)min) {
                        value = min;
                    }
                    value -= (float)min;
                    chatRenderer.updateChatSetting(horizontal ? ChatRenderer.ChatSettingType.WIDTH : ChatRenderer.ChatSettingType.HEIGHT, value /= (float)(max - min));
                    Minecraft.getMinecraft().ingameGUI.getChatGUI().refreshChat();
                }
                if (chatRenderer.isMouseOver() && action == EnumMouseAction.CLICKED && mouseButton == 2 && LabyMod.getSettings().scalableChat) {
                    chatRenderer.moving = true;
                    DrawUtils draw = LabyMod.getInstance().getDrawUtils();
                    double screenWidth = draw.getWidth();
                    double screenHeight = draw.getHeight();
                    chatRenderer.movingClickedX = -(screenWidth / 100.0 * (double)chatRenderer.getChatPercentX()) + (double)mouseX;
                    chatRenderer.movingClickedY = -(screenHeight / 100.0 * (double)chatRenderer.getChatPercentY()) + (double)mouseY;
                }
                if (chatRenderer.moving && (action == EnumMouseAction.DRAGGING || action == EnumMouseAction.RENDER)) {
                    DrawUtils draw = LabyMod.getInstance().getDrawUtils();
                    double screenWidth = draw.getWidth();
                    double percentX = 100.0 / screenWidth * (-chatRenderer.movingClickedX + (double)mouseX);
                    double screenHeight2 = draw.getHeight();
                    double percentY = 100.0 / screenHeight2 * (-chatRenderer.movingClickedY + (double)mouseY);
                    if (percentX > 100.0) {
                        percentX = 100.0;
                    }
                    if (percentX < 0.0) {
                        percentX = 0.0;
                    }
                    if (percentY > 100.0) {
                        percentY = 100.0;
                    }
                    if (percentY < 0.0) {
                        percentY = 0.0;
                    }
                    chatRenderer.updateChatSetting(ChatRenderer.ChatSettingType.X, (float)percentX);
                    chatRenderer.updateChatSetting(ChatRenderer.ChatSettingType.Y, (float)percentY);
                }
                if (action == EnumMouseAction.RELEASED) {
                    chatRenderer.moving = false;
                }
                if (action == EnumMouseAction.CLICKED && (selected = chatRenderer.selectHoveredTab()) != null) {
                    this.roomsUnread.put(selected, 0);
                }
            }
            ++n3;
        }
    }

    public void updateRooms() {
        ArrayList<String> rooms = new ArrayList<String>();
        for (Filters.Filter filterComponent : LabyMod.getInstance().getChatToolManager().getFilters()) {
            if (filterComponent.getRoom() == null || rooms.contains(filterComponent.getRoom())) continue;
            rooms.add(filterComponent.getRoom());
        }
        if (!rooms.isEmpty()) {
            rooms.add(0, GLOBAL);
        }
        this.backendRooms = rooms;
        this.visibleRooms.clear();
        this.roomsUnread.clear();
        this.visibleRooms.add(GLOBAL);
        this.selectedRoom = GLOBAL;
    }

    public void handleUnread(String room) {
        Integer unread = this.roomsUnread.get(room);
        if (unread == null) {
            unread = 0;
        }
        if (!this.selectedRoom.equals(room)) {
            unread = unread + 1;
        }
        this.roomsUnread.put(room, unread);
        if (room != null && !this.visibleRooms.contains(room)) {
            this.visibleRooms.add(room);
        }
    }

    public ChatRendererMain getMain() {
        return this.main;
    }

    public ChatRendererSecond getSecond() {
        return this.second;
    }

    public ChatRenderer[] getChatRenderers() {
        return this.chatRenderers;
    }

    public GameSettings getGameSettings() {
        return this.gameSettings;
    }

    public List<String> getSentMessages() {
        return this.sentMessages;
    }

    public List<String> getBackendRooms() {
        return this.backendRooms;
    }

    public List<String> getVisibleRooms() {
        return this.visibleRooms;
    }

    public Map<String, Integer> getRoomsUnread() {
        return this.roomsUnread;
    }

    public void setSelectedRoom(String selectedRoom) {
        this.selectedRoom = selectedRoom;
    }

    public String getSelectedRoom() {
        return this.selectedRoom;
    }
}

