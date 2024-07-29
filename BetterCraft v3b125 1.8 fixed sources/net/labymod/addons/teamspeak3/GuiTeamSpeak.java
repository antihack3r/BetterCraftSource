/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.addons.teamspeak3;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import net.labymod.addons.teamspeak3.Chat;
import net.labymod.addons.teamspeak3.EnumTargetMode;
import net.labymod.addons.teamspeak3.Message;
import net.labymod.addons.teamspeak3.PopUpCallback;
import net.labymod.addons.teamspeak3.TeamSpeak;
import net.labymod.addons.teamspeak3.TeamSpeakBridge;
import net.labymod.addons.teamspeak3.TeamSpeakChannel;
import net.labymod.addons.teamspeak3.TeamSpeakChannelGroup;
import net.labymod.addons.teamspeak3.TeamSpeakController;
import net.labymod.addons.teamspeak3.TeamSpeakServerGroup;
import net.labymod.addons.teamspeak3.TeamSpeakUser;
import net.labymod.utils.DrawUtils;
import net.labymod.utils.ModColor;
import net.labymod.utils.ModUtils;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class GuiTeamSpeak
extends GuiScreen {
    private GuiScreen parent;
    private DrawUtils draw;
    boolean boxEnabled = false;
    boolean boxIsUser = false;
    int boxId = 0;
    int boxPosX = 0;
    int boxPosY = 0;
    int boxLengthX = 0;
    int boxLengthY = 0;
    boolean drag = false;
    boolean drop = false;
    int dragVisible = 0;
    boolean dropFocus = false;
    int dropX = 0;
    int dropY = 0;
    boolean dragIsUser = false;
    int dragId = 0;
    boolean changeNickName = false;
    GuiTextField nickNameField;
    GuiTextField chatInputField;
    long lastClick = 0L;
    boolean allowChatScroll;
    boolean allowChannelScroll;
    int yMouse = 0;
    int doubleClickDelay = 500;
    boolean doubleClickIsUser;
    int doubleClickTarget = 0;
    GuiButton connectButton;
    GuiButton backButton;
    int connect = 0;
    boolean init = false;
    int clickX;
    int clickY;
    boolean moveX;
    boolean moveY;

    public GuiTeamSpeak(GuiScreen parent) {
        this.parent = parent;
    }

    @Override
    public void initGui() {
        this.draw = new DrawUtils();
        TeamSpeak.setDefaultScreen();
        this.changeNickName = false;
        this.drag = false;
        this.closeBox();
        this.connect = 0;
        Keyboard.enableRepeatEvents(true);
        this.buttonList.clear();
        this.chatInputField = new GuiTextField(0, this.mc.fontRendererObj, 5, this.draw.getHeight() - 17, this.draw.getWidth() - 10, 12);
        this.chatInputField.setText(TeamSpeak.inputString);
        this.chatInputField.setMaxStringLength(200);
        int i2 = TeamSpeak.xSplit - 26;
        this.nickNameField = new GuiTextField(0, this.mc.fontRendererObj, 0, 0, i2, 9);
        this.nickNameField.setMaxStringLength(20);
        this.backButton = new GuiButton(0, 15, 2, 30, 20, "Back");
        this.connectButton = new GuiButton(1, width / 2 - 100, height / 2 + 10, "Connect");
        if (TeamSpeakController.getInstance() != null) {
            this.connectButton.visible = !TeamSpeakController.getInstance().isConnectionEstablished();
        }
        this.buttonList.add(this.connectButton);
        this.buttonList.add(this.backButton);
        while (TeamSpeak.xSplit > this.draw.getWidth() / 4 * 3) {
            --TeamSpeak.xSplit;
        }
        while (TeamSpeak.xSplit < 200) {
            ++TeamSpeak.xSplit;
        }
        while (TeamSpeak.ySplit > this.draw.getHeight() - 50) {
            --TeamSpeak.ySplit;
        }
        while (TeamSpeak.ySplit < 50) {
            ++TeamSpeak.ySplit;
        }
        this.boxEnabled = false;
        super.initGui();
    }

    @Override
    public void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 0: {
                this.mc.displayGuiScreen(this.parent);
                break;
            }
            case 1: {
                this.connect = 1;
            }
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (TeamSpeak.overlayWindows != null) {
            TeamSpeak.overlayWindows.KeyTyped(typedChar, keyCode);
            if (!TeamSpeak.overlayWindows.allow()) {
                return;
            }
        }
        if (keyCode == 28) {
            if (this.changeNickName && this.nickNameField != null && this.nickNameField.isFocused()) {
                this.changeNickname();
            } else if (!TeamSpeak.inputString.isEmpty() && this.chatInputField != null && this.chatInputField.isFocused()) {
                TeamSpeakBridge.sendTextMessage(TeamSpeak.selectedChat, TeamSpeak.inputString);
                TeamSpeak.inputString = "";
                this.chatInputField.setText("");
            }
        }
        if (this.chatInputField.textboxKeyTyped(typedChar, keyCode)) {
            TeamSpeak.inputString = this.chatInputField.getText();
        }
        if (this.changeNickName) {
            this.nickNameField.textboxKeyTyped(typedChar, keyCode);
        }
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (this.boxAction(mouseX, mouseY, mouseButton)) {
            super.mouseClicked(mouseX, mouseY, mouseButton);
        } else {
            this.closeBox();
            if (TeamSpeak.overlayWindows != null) {
                TeamSpeak.overlayWindows.mouseClicked(mouseX, mouseY, mouseButton);
                if (TeamSpeak.overlayWindows.isInScreen(mouseX, mouseY)) {
                    super.mouseClicked(mouseX, mouseY, mouseButton);
                    return;
                }
            }
            if (this.changeNickName) {
                this.changeNickname();
            }
            this.chatInputField.mouseClicked(mouseX, mouseY, mouseButton);
            if (this.changeNickName) {
                this.nickNameField.mouseClicked(mouseX, mouseY, mouseButton);
            }
            this.clickX = mouseX - TeamSpeak.xSplit;
            this.clickY = mouseY - TeamSpeak.ySplit;
            if (mouseX > TeamSpeak.xSplit - 5 && mouseX < TeamSpeak.xSplit + 5) {
                this.moveX = true;
            }
            if (mouseY > TeamSpeak.ySplit - 5 && mouseY < TeamSpeak.ySplit + 5) {
                this.moveY = true;
            }
            if (!this.moveX && !this.moveY) {
                this.switchChat(mouseX, mouseY, mouseButton);
                this.channelAction(mouseX, mouseY, mouseButton);
                this.menuAction(mouseX, mouseY, mouseButton);
                super.mouseClicked(mouseX, mouseY, mouseButton);
            }
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int i2 = Mouse.getEventDWheel();
        if (i2 != 0) {
            if (i2 > 1) {
                i2 = 1;
            }
            if (i2 < -1) {
                i2 = -1;
            }
            if (this.boxEnabled) {
                return;
            }
            if (this.changeNickName) {
                return;
            }
            if (this.yMouse > TeamSpeak.ySplit) {
                if (i2 > 0) {
                    if (this.allowChatScroll) {
                        TeamSpeak.scrollChat -= 10;
                    }
                } else if (TeamSpeak.scrollChat < 0) {
                    TeamSpeak.scrollChat += 10;
                }
            } else if (i2 > 0) {
                if (TeamSpeak.scrollChannel < 0) {
                    TeamSpeak.scrollChannel += 20;
                }
            } else if (this.allowChannelScroll) {
                TeamSpeak.scrollChannel -= 20;
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.callBackListener(mouseX, mouseY);
        boolean bl2 = this.connectButton.visible = !TeamSpeakController.getInstance().isConnectionEstablished();
        if (this.connectButton != null) {
            if (this.connect == 2) {
                this.connect = 0;
                TeamSpeakController.getInstance().connect();
            }
            if (this.connect == 1) {
                ++this.connect;
            }
        }
        if (!TeamSpeakController.getInstance().isConnectionEstablished()) {
            String s2 = String.valueOf(TeamSpeakController.getInstance().serverIP) + ":" + TeamSpeakController.getInstance().serverPort;
            if (!TeamSpeakController.getInstance().serverIP.isEmpty() && TeamSpeakController.getInstance().serverPort != 0) {
                this.draw.drawCenteredString(String.valueOf(ModColor.cl("a")) + "Connect to " + s2, width / 2, height / 2 - 15);
            } else {
                this.draw.drawCenteredString(String.valueOf(ModColor.cl("c")) + "Error please open TeamSpeak and Connect", width / 2, height / 2 - 15);
                if (this.connectButton != null) {
                    this.connectButton.visible = true;
                    this.connectButton.displayString = !this.connectButton.enabled ? "Connecting" : "Connect";
                }
            }
            if (this.init) {
                this.init = false;
                this.initGui();
            }
            super.drawScreen(mouseX, mouseY, partialTicks);
        } else if (!TeamSpeakController.getInstance().serverIP.isEmpty() && TeamSpeakController.getInstance().serverPort != 0 && TeamSpeakUser.amount() != 0 && TeamSpeakChannel.amount() != 0) {
            if (!this.init) {
                this.init = true;
                this.initGui();
            }
            this.chatInputField.drawTextBox();
            this.drawChat();
            this.drawChannel(mouseX, mouseY);
            this.drawClientInfo();
            this.drawMenu(mouseX, mouseY);
            if (this.changeNickName) {
                this.nickNameField.drawTextBox();
            }
            this.yMouse = mouseY;
            if (TeamSpeak.selectedUser != -1 && TeamSpeakController.getInstance().getUser(TeamSpeak.selectedUser) == null) {
                TeamSpeak.selectedUser = -1;
            }
            if (TeamSpeak.selectedChannel != -1 && TeamSpeakController.getInstance().getChannel(TeamSpeak.selectedChannel) == null) {
                TeamSpeak.selectedChannel = -1;
            }
            if (TeamSpeak.selectedChannel == -1 && TeamSpeak.selectedUser == -1 && TeamSpeakController.getInstance().me() != null) {
                TeamSpeak.selectedUser = TeamSpeakController.getInstance().me().getClientId();
            }
            if (TeamSpeak.overlayWindows != null) {
                TeamSpeak.overlayWindows.drawWindow(mouseX, mouseY);
                if (TeamSpeak.overlayWindows.isInScreen(mouseX, mouseY)) {
                    super.drawScreen(mouseX, mouseY, partialTicks);
                    this.drawBox(mouseX, mouseY);
                    return;
                }
            }
            this.drawBox(mouseX, mouseY);
            if (mouseX > TeamSpeak.xSplit - 5 && mouseX < TeamSpeak.xSplit + 5 && mouseY > TeamSpeak.ySplit - 5 && mouseY < TeamSpeak.ySplit + 5) {
                this.draw.drawCenteredString(String.valueOf(ModColor.cl("7")) + "+", mouseX + 1, mouseY - 2);
            } else {
                if (mouseX > TeamSpeak.xSplit - 5 && mouseX < TeamSpeak.xSplit + 5 && mouseY < TeamSpeak.ySplit) {
                    this.draw.drawCenteredString(String.valueOf(ModColor.cl("7")) + "...", mouseX + 1, mouseY - 6);
                    this.draw.drawCenteredString(String.valueOf(ModColor.cl("7")) + "...", mouseX + 1, mouseY - 3);
                }
                if (mouseY > TeamSpeak.ySplit - 5 && mouseY < TeamSpeak.ySplit + 5) {
                    this.draw.drawCenteredString(String.valueOf(ModColor.cl("7")) + "||", mouseX + 1, mouseY - 3);
                }
            }
            if (this.drag) {
                this.drawDrag(mouseX, mouseY);
            }
            super.drawScreen(mouseX, mouseY, partialTicks);
        } else {
            if (this.connectButton != null) {
                this.connectButton.visible = true;
            }
            this.draw.drawCenteredString(String.valueOf(ModColor.cl("c")) + "No server found", width / 2, height / 2 - 15);
            this.draw.drawCenteredString(String.valueOf(ModColor.cl("7")) + "Try restart", width / 2, height / 2 - 5);
            super.drawScreen(mouseX, mouseY, partialTicks);
        }
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        if (TeamSpeak.overlayWindows != null) {
            TeamSpeak.overlayWindows.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
            if (TeamSpeak.overlayWindows.isInScreen(mouseX, mouseY)) {
                super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
                return;
            }
        }
        if (this.moveX && mouseX - this.clickX < this.draw.getWidth() / 4 * 3 && mouseX - this.clickX > 200) {
            TeamSpeak.xSplit = mouseX - this.clickX;
        }
        if (this.moveY && mouseY - this.clickY < this.draw.getHeight() - 50 && mouseY - this.clickY > 50) {
            TeamSpeak.ySplit = mouseY - this.clickY;
        }
        if (this.drag) {
            ++this.dragVisible;
        }
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        if (TeamSpeak.overlayWindows != null) {
            TeamSpeak.overlayWindows.mouseReleased(mouseX, mouseY, state);
            if (TeamSpeak.overlayWindows.isInScreen(mouseX, mouseY)) {
                super.mouseReleased(mouseX, mouseY, state);
                return;
            }
        }
        if (this.moveX || this.moveY) {
            this.initGui();
        }
        this.moveX = false;
        this.moveY = false;
        if (this.drag) {
            this.setDrop(mouseX, mouseY);
        }
        super.mouseReleased(mouseX, mouseY, state);
    }

    private void drawChat() {
        DrawUtils drawutils = this.draw;
        DrawUtils.drawRect(5, TeamSpeak.ySplit, this.draw.getWidth() - 5, this.draw.getHeight() - 20, Integer.MIN_VALUE);
        Chat chat = null;
        int i2 = 0;
        for (Chat chat1 : TeamSpeak.chats) {
            drawutils = this.draw;
            DrawUtils.drawRect(5 + i2, this.draw.getHeight() - 33, 50 + i2, this.draw.getHeight() - 20, 835640000);
            String s2 = "";
            if (TeamSpeak.selectedChat == chat1.getSlotId()) {
                drawutils = this.draw;
                DrawUtils.drawRect(5 + i2 + 1, this.draw.getHeight() - 33 + 1, 50 + i2 - 1, this.draw.getHeight() - 20 - 1, Integer.MAX_VALUE);
                chat = chat1;
            }
            String s1 = StringUtils.capitalize(chat1.getTargetMode().name());
            if (chat1.getTargetMode() == EnumTargetMode.USER && (s1 = chat1.getChatOwner().getNickName()).length() > 7) {
                s1 = s1.substring(0, 7);
            }
            this.draw.drawCenteredString(String.valueOf(s2) + s1, 27 + i2, this.draw.getHeight() - 30);
            i2 += 46;
        }
        if (chat != null) {
            int j2 = 0;
            ArrayList<Message> list = new ArrayList<Message>();
            list.addAll(chat.getLog());
            Collections.reverse(list);
            for (Message message : list) {
                if (this.draw.getHeight() - 45 - j2 - TeamSpeak.scrollChat >= TeamSpeak.ySplit && this.draw.getHeight() - 45 - j2 - TeamSpeak.scrollChat <= this.draw.getHeight() - 45) {
                    if (message.getUser() == null) {
                        this.draw.drawString(message.getMessage(), 8.0, this.draw.getHeight() - 45 - j2 - TeamSpeak.scrollChat);
                    } else {
                        this.draw.drawString(String.valueOf(ModColor.cl("9")) + message.getUser().getNickName() + ModColor.cl("7") + ": " + message.getMessage(), 8.0, this.draw.getHeight() - 45 - j2 - TeamSpeak.scrollChat);
                    }
                }
                j2 += 10;
            }
            this.allowChatScroll = this.draw.getHeight() - 45 - j2 - TeamSpeak.scrollChat < TeamSpeak.ySplit - 10;
        }
    }

    public void switchChat(int mouseX, int mouseY, int mouseButton) {
        int i2 = 0;
        Chat chat = null;
        for (Chat chat1 : TeamSpeak.chats) {
            if (mouseX > 5 + i2 && mouseX < 50 + i2 && mouseY > this.draw.getHeight() - 33 && mouseY < this.draw.getHeight() - 20) {
                TeamSpeak.scrollChat = 0;
                if (mouseButton == 0) {
                    TeamSpeak.selectedChat = chat1.getSlotId();
                }
                if (mouseButton == 1 && chat1.getSlotId() >= 0) {
                    TeamSpeak.selectedChat = -1;
                    chat = chat1;
                }
            }
            i2 += 46;
        }
        if (chat != null) {
            TeamSpeak.chats.remove(chat);
        }
        Chat chat3 = null;
        for (Chat chat2 : TeamSpeak.chats) {
            if (TeamSpeak.selectedChat != chat2.getSlotId()) continue;
            chat3 = chat2;
        }
        if (chat3 != null) {
            int j2 = 0;
            ArrayList<Message> list = new ArrayList<Message>();
            list.addAll(chat3.getLog());
            Collections.reverse(list);
            for (Message message : list) {
                if (this.draw.getHeight() - 45 - j2 - TeamSpeak.scrollChat >= TeamSpeak.ySplit && this.draw.getHeight() - 45 - j2 - TeamSpeak.scrollChat <= this.draw.getHeight() - 45 && message.getUser() != null) {
                    ArrayList<String> arraylist;
                    if (mouseX > 8 && mouseX < this.draw.getStringWidth(message.getUser().getNickName()) + 8 && mouseY > this.draw.getHeight() - 45 - j2 - TeamSpeak.scrollChat && mouseY < this.draw.getHeight() - 45 - j2 - TeamSpeak.scrollChat + 10) {
                        this.openBox(true, message.getUser().getClientId(), mouseX, mouseY);
                    }
                    if (mouseX > 8 && mouseX > this.draw.getStringWidth(message.getUser().getNickName()) + 8 && mouseY > this.draw.getHeight() - 45 - j2 - TeamSpeak.scrollChat && mouseY < this.draw.getHeight() - 45 - j2 - TeamSpeak.scrollChat + 10 && !(arraylist = ModUtils.extractDomains(message.getMessage())).isEmpty()) {
                        try {
                            ModUtils.openWebpage(new URI(arraylist.get(0)), false);
                        }
                        catch (URISyntaxException e2) {
                            e2.printStackTrace();
                        }
                    }
                }
                j2 += 10;
            }
        }
    }

    private void drawChannel(int mouseX, int mouseY) {
        DrawUtils drawutils = this.draw;
        DrawUtils.drawRect(5, TeamSpeak.ySplit - 1, TeamSpeak.xSplit, 25, Integer.MIN_VALUE);
        ArrayList<TeamSpeakChannel> arraylist = new ArrayList<TeamSpeakChannel>();
        int i2 = 0;
        for (TeamSpeakChannel teamspeakchannel : TeamSpeakBridge.getChannels()) {
            if (teamspeakchannel.getChannelOrder() == 0) {
                arraylist.add(teamspeakchannel);
                continue;
            }
            TeamSpeakChannel teamspeakchannel1 = TeamSpeak.getFromOrder(i2);
            if (teamspeakchannel1 == null) break;
            i2 = teamspeakchannel1.getChannelId();
            arraylist.add(teamspeakchannel1);
        }
        boolean flag = false;
        int j2 = 0;
        int k2 = 0;
        TeamSpeak.outOfView.clear();
        for (TeamSpeakChannel teamspeakchannel2 : arraylist) {
            if (mouseX > 5 && mouseX < TeamSpeak.xSplit && mouseY > 30 + k2 + TeamSpeak.scrollChannel && mouseY < 30 + k2 + TeamSpeak.scrollChannel + 11 && 30 + k2 + TeamSpeak.scrollChannel + 10 < TeamSpeak.ySplit && 30 + k2 + TeamSpeak.scrollChannel > 20) {
                flag = true;
                j2 = teamspeakchannel2.getChannelId();
                if (this.drag) {
                    GuiTeamSpeak.drawRect(5, 30 + k2 + TeamSpeak.scrollChannel - 1, TeamSpeak.xSplit, 30 + k2 + TeamSpeak.scrollChannel + 10, 1230000000);
                }
            }
            if (30 + k2 + TeamSpeak.scrollChannel < 20) {
                TeamSpeak.outOfView.add(teamspeakchannel2.getChannelId());
            }
            if (30 + k2 + TeamSpeak.scrollChannel + 10 < TeamSpeak.ySplit && 30 + k2 + TeamSpeak.scrollChannel > 20) {
                String s2 = ModColor.cl("7");
                if (TeamSpeak.selectedChannel == teamspeakchannel2.getChannelId()) {
                    GuiTeamSpeak.drawRect(5, 30 + k2 + TeamSpeak.scrollChannel - 1, TeamSpeak.xSplit, 30 + k2 + TeamSpeak.scrollChannel + 10, 1230000000);
                }
                if (TeamSpeak.isSpacer(teamspeakchannel2.getChannelName())) {
                    this.draw.drawCenteredString(String.valueOf(s2) + TeamSpeak.toStarSpacer(teamspeakchannel2.getChannelName(), TeamSpeak.xSplit), TeamSpeak.xSplit / 2, 30 + k2 + TeamSpeak.scrollChannel);
                } else if (TeamSpeak.isStarSpacer(teamspeakchannel2.getChannelName())) {
                    this.draw.drawCenteredString(String.valueOf(s2) + TeamSpeak.toStarSpacer(teamspeakchannel2.getChannelName(), TeamSpeak.xSplit), TeamSpeak.xSplit / 2, 30 + k2 + TeamSpeak.scrollChannel);
                } else if (TeamSpeak.isCenterSpacer(teamspeakchannel2.getChannelName())) {
                    this.draw.drawCenteredString(String.valueOf(s2) + TeamSpeak.toCenterSpacer(teamspeakchannel2.getChannelName()), TeamSpeak.xSplit / 2, 30 + k2 + TeamSpeak.scrollChannel);
                } else {
                    String s1 = ModColor.cl("2");
                    if (teamspeakchannel2.getIsPassword()) {
                        s1 = ModColor.cl("e");
                    }
                    if (!teamspeakchannel2.getSubscription()) {
                        s1 = ModColor.cl("b");
                    }
                    if (teamspeakchannel2.getTotalClients() >= teamspeakchannel2.getMaxClients() && teamspeakchannel2.getMaxClients() != -1) {
                        s1 = ModColor.cl("4");
                    }
                    this.draw.drawString(String.valueOf(s1) + " \u2b1b " + s2 + teamspeakchannel2.getChannelName(), 5.0, 30 + k2 + TeamSpeak.scrollChannel);
                }
            }
            ArrayList<TeamSpeakUser> arraylist2 = new ArrayList<TeamSpeakUser>();
            arraylist2.addAll(TeamSpeakBridge.getUsers());
            Collections.sort(arraylist2, new Comparator<TeamSpeakUser>(){

                @Override
                public int compare(TeamSpeakUser o1, TeamSpeakUser o2) {
                    return o1 != null && o2 != null ? (o1.getTalkPower() < o2.getTalkPower() ? 1 : (o1.getTalkPower() > o2.getTalkPower() ? -1 : 0)) : 0;
                }
            });
            for (TeamSpeakUser teamspeakuser : arraylist2) {
                if (teamspeakuser == null || teamspeakuser.getChannelId() != teamspeakchannel2.getChannelId() || 30 + (k2 += 10) + TeamSpeak.scrollChannel + 10 >= TeamSpeak.ySplit || 30 + k2 + TeamSpeak.scrollChannel <= 20) continue;
                String s2 = "";
                if (TeamSpeak.selectedUser == teamspeakuser.getClientId()) {
                    GuiTeamSpeak.drawRect(5, 30 + k2 + TeamSpeak.scrollChannel - 1, TeamSpeak.xSplit, 30 + k2 + TeamSpeak.scrollChannel + 10, 1230000000);
                }
                if (TeamSpeakController.getInstance().me() != null && teamspeakuser.getClientId() == TeamSpeakController.getInstance().me().getClientId()) {
                    s2 = String.valueOf(s2) + ModColor.cl("c") + ModColor.cl("l") + ModColor.cl("n");
                    this.nickNameField.xPosition = 25;
                    this.nickNameField.yPosition = 30 + k2 + TeamSpeak.scrollChannel;
                }
                String s3 = "";
                if (TeamSpeak.teamSpeakGroupPrefix) {
                    ArrayList<TeamSpeakServerGroup> arraylist1 = new ArrayList<TeamSpeakServerGroup>();
                    arraylist1.addAll(TeamSpeakServerGroup.getGroups());
                    for (TeamSpeakServerGroup teamspeakservergroup : arraylist1) {
                        if (teamspeakservergroup == null || !teamspeakuser.getServerGroups().contains(teamspeakservergroup.getSgid()) || teamspeakservergroup.getType() != 1 || teamspeakservergroup.getIconId() == 0) continue;
                        s3 = String.valueOf(s3) + ModColor.cl("b") + "[" + TeamSpeak.fix(teamspeakservergroup.getGroupName()) + "] ";
                    }
                }
                this.draw.drawString(String.valueOf(TeamSpeak.getTalkColor(teamspeakuser)) + "  \u2b24 " + ModColor.cl("f") + s3 + ModColor.cl("f") + s2 + teamspeakuser.getNickName() + TeamSpeak.getAway(teamspeakuser), 5.0, 30 + k2 + TeamSpeak.scrollChannel);
            }
            k2 += 10;
        }
        boolean bl2 = this.allowChannelScroll = 30 + k2 + TeamSpeak.scrollChannel > TeamSpeak.ySplit - 10;
        if (30 + k2 + TeamSpeak.scrollChannel < TeamSpeak.ySplit - 30) {
            TeamSpeak.scrollChannel += 10;
        }
        if (TeamSpeak.scrollChannel > 20) {
            TeamSpeak.scrollChannel -= 10;
        }
        this.drop(j2, flag);
    }

    private void channelAction(int mouseX, int mouseY, int mouseButton) {
        ArrayList<TeamSpeakChannel> arraylist = new ArrayList<TeamSpeakChannel>();
        int i2 = 0;
        for (TeamSpeakChannel teamspeakchannel : TeamSpeakBridge.getChannels()) {
            TeamSpeakChannel teamspeakchannel1 = TeamSpeak.getFromOrder(i2);
            if (teamspeakchannel.getChannelOrder() == 0) {
                arraylist.add(teamspeakchannel);
                continue;
            }
            if (teamspeakchannel1 == null) break;
            i2 = teamspeakchannel1.getChannelId();
            arraylist.add(teamspeakchannel1);
        }
        int j2 = 0;
        for (TeamSpeakChannel teamspeakchannel2 : arraylist) {
            if (30 + j2 + TeamSpeak.scrollChannel + 5 < TeamSpeak.ySplit && 30 + j2 + TeamSpeak.scrollChannel > 20 && mouseX > 5 && mouseX < TeamSpeak.xSplit && mouseY > 30 + j2 + TeamSpeak.scrollChannel && mouseY < 30 + j2 + TeamSpeak.scrollChannel + 10) {
                TeamSpeak.selectedChannel = teamspeakchannel2.getChannelId();
                TeamSpeak.selectedUser = -1;
                if (mouseButton == 1) {
                    this.openBox(false, teamspeakchannel2.getChannelId(), mouseX, mouseY);
                    return;
                }
                if (this.lastClick + (long)this.doubleClickDelay > System.currentTimeMillis() && this.doubleClickTarget == teamspeakchannel2.getChannelId() && !this.doubleClickIsUser && TeamSpeakController.getInstance().me() != null && TeamSpeakController.getInstance().me().getChannelId() != teamspeakchannel2.getChannelId()) {
                    if (teamspeakchannel2.getIsPassword()) {
                        TeamSpeak.overlayWindows.openInput(teamspeakchannel2.getChannelId(), "Channel Password Title", "insert password for" + teamspeakchannel2.getChannelName(), new PopUpCallback(){

                            @Override
                            public void ok(int cid, String message) {
                                TeamSpeakBridge.moveClient(TeamSpeakController.getInstance().me().getClientId(), cid);
                            }

                            @Override
                            public void ok() {
                            }

                            @Override
                            public void cancel() {
                            }

                            @Override
                            public boolean tick(int cid) {
                                return TeamSpeakController.getInstance().me() != null && TeamSpeakController.getInstance().me().getChannelId() == cid;
                            }
                        });
                    }
                    TeamSpeakBridge.moveClient(TeamSpeakController.getInstance().me().getClientId(), teamspeakchannel2.getChannelId());
                }
                this.doubleClickTarget = teamspeakchannel2.getChannelId();
                this.doubleClickIsUser = false;
                this.lastClick = System.currentTimeMillis();
            }
            ArrayList<TeamSpeakUser> arraylist1 = new ArrayList<TeamSpeakUser>();
            arraylist1.addAll(TeamSpeakBridge.getUsers());
            Collections.sort(arraylist1, new Comparator<TeamSpeakUser>(){

                @Override
                public int compare(TeamSpeakUser o1, TeamSpeakUser o2) {
                    return o1 != null && o2 != null ? (o1.getTalkPower() < o2.getTalkPower() ? 1 : (o1.getTalkPower() > o2.getTalkPower() ? -1 : 0)) : 0;
                }
            });
            for (TeamSpeakUser teamspeakuser : arraylist1) {
                if (teamspeakuser.getChannelId() != teamspeakchannel2.getChannelId() || 30 + (j2 += 10) + TeamSpeak.scrollChannel + 5 >= TeamSpeak.ySplit || 30 + j2 + TeamSpeak.scrollChannel <= 20 || mouseX <= 5 || mouseX >= TeamSpeak.xSplit || mouseY <= 30 + j2 + TeamSpeak.scrollChannel || mouseY >= 30 + j2 + TeamSpeak.scrollChannel + 10) continue;
                TeamSpeak.selectedUser = teamspeakuser.getClientId();
                TeamSpeak.selectedChannel = -1;
                if (mouseButton == 1) {
                    this.openBox(true, teamspeakuser.getClientId(), mouseX, mouseY);
                    return;
                }
                this.drag(true, teamspeakuser.getClientId());
                if (this.lastClick + (long)this.doubleClickDelay > System.currentTimeMillis() && this.doubleClickTarget == teamspeakuser.getClientId() && this.doubleClickIsUser) {
                    if (TeamSpeakController.getInstance().me() != null && teamspeakuser.getClientId() != TeamSpeakController.getInstance().me().getClientId()) {
                        TeamSpeak.addChat(teamspeakuser, TeamSpeakController.getInstance().me(), null, EnumTargetMode.USER);
                        TeamSpeak.selectedChat = teamspeakuser.getClientId();
                    } else {
                        this.openNickNameBox();
                    }
                }
                this.doubleClickTarget = teamspeakuser.getClientId();
                this.doubleClickIsUser = true;
                this.lastClick = System.currentTimeMillis();
            }
            j2 += 10;
        }
    }

    private void drawClientInfo() {
        if (TeamSpeak.selectedUser != -1) {
            TeamSpeakUser teamspeakuser = TeamSpeakController.getInstance().getUser(TeamSpeak.selectedUser);
            if (teamspeakuser == null) {
                return;
            }
            int i2 = 30;
            int j2 = TeamSpeak.xSplit + 10;
            this.drawInfo(String.valueOf(ModColor.cl("7")) + "Nickname" + ModColor.cl("f") + ":", j2, i2);
            this.drawInfo(String.valueOf(ModColor.cl("7")) + "Country" + ModColor.cl("f") + ":", j2, i2 += 12);
            this.drawInfo(String.valueOf(ModColor.cl("7")) + "Talkpower" + ModColor.cl("f") + ":", j2, i2 += 12);
            i2 += 12;
            i2 = 30;
            this.drawInfo(teamspeakuser.getNickName(), j2 + 70, i2);
            this.drawInfo(TeamSpeak.country(teamspeakuser.getCountry()), j2 + 70, i2 += 12);
            this.drawInfo("" + teamspeakuser.getTalkPower(), j2 + 70, i2 += 12);
            i2 += 12;
            this.drawInfo(String.valueOf(ModColor.cl("7")) + "Servergroups" + ModColor.cl("f") + ":", j2, i2 += 10);
            i2 += 12;
            if (teamspeakuser.getServerGroups() == null) {
                return;
            }
            for (int k2 : teamspeakuser.getServerGroups()) {
                TeamSpeakServerGroup teamspeakservergroup = TeamSpeakController.getInstance().getServerGroup(k2);
                if (teamspeakservergroup != null) {
                    this.drawInfo(TeamSpeak.fix(teamspeakservergroup.getGroupName()), j2, i2);
                    i2 += 12;
                    continue;
                }
                this.drawInfo("" + k2, j2, i2);
                i2 += 12;
            }
            this.drawInfo(String.valueOf(ModColor.cl("7")) + "Channelgroups" + ModColor.cl("f") + ":", j2, i2 += 10);
            i2 += 12;
            TeamSpeakChannelGroup teamspeakchannelgroup = TeamSpeakController.getInstance().getChannelGroup(teamspeakuser.getChannelGroupId());
            if (teamspeakchannelgroup != null) {
                this.drawInfo(TeamSpeak.fix(teamspeakchannelgroup.getGroupName()), j2, i2);
                i2 += 12;
            } else {
                this.drawInfo("" + teamspeakuser.getChannelGroupId(), j2, i2);
                i2 += 12;
            }
            i2 += 10;
            if (!teamspeakuser.hasClientInputHardware()) {
                this.drawInfo(String.valueOf(ModColor.cl("c")) + "Mic off", j2, i2);
                i2 += 12;
            }
            if (teamspeakuser.hasClientInputMuted()) {
                this.drawInfo(String.valueOf(ModColor.cl("c")) + "Mic mute", j2, i2);
                i2 += 12;
            }
            if (!teamspeakuser.hasClientOutputHardware()) {
                this.drawInfo(String.valueOf(ModColor.cl("4")) + "Sound off", j2, i2);
                i2 += 12;
            }
            if (teamspeakuser.hasClientOutputMuted()) {
                this.drawInfo(String.valueOf(ModColor.cl("4")) + "Sound mute", j2, i2);
                i2 += 12;
            }
        } else if (TeamSpeak.selectedChannel != -1) {
            TeamSpeakChannel teamspeakchannel = TeamSpeakController.getInstance().getChannel(TeamSpeak.selectedChannel);
            if (teamspeakchannel == null) {
                return;
            }
            int l2 = 30;
            int i1 = TeamSpeak.xSplit + 10;
            this.drawInfo(String.valueOf(ModColor.cl("7")) + "Channel Name" + ModColor.cl("f") + ":", i1, l2);
            l2 += 12;
            if (teamspeakchannel != null && teamspeakchannel.getTopic() != null && !teamspeakchannel.getTopic().isEmpty()) {
                this.drawInfo(String.valueOf(ModColor.cl("7")) + "Channel Topic" + ModColor.cl("f") + ":", i1, l2);
                l2 += 12;
            }
            this.drawInfo(String.valueOf(ModColor.cl("7")) + "Codec" + ModColor.cl("f") + ":", i1, l2);
            this.drawInfo(String.valueOf(ModColor.cl("7")) + "Codec quality" + ModColor.cl("f") + ":", i1, l2 += 12);
            this.drawInfo(String.valueOf(ModColor.cl("7")) + "Type" + ModColor.cl("f") + ":", i1, l2 += 12);
            this.drawInfo(String.valueOf(ModColor.cl("7")) + "Current clients" + ModColor.cl("f") + ":", i1, l2 += 12);
            this.drawInfo(String.valueOf(ModColor.cl("7")) + "Sub status" + ModColor.cl("f") + ":", i1, l2 += 12);
            l2 += 12;
            l2 = 30;
            this.drawInfo(teamspeakchannel.getChannelName(), i1 += 110, l2);
            l2 += 12;
            if (!teamspeakchannel.getTopic().isEmpty()) {
                this.drawInfo(teamspeakchannel.getTopic(), i1, l2);
                l2 += 12;
            }
            this.drawInfo(teamspeakchannel.getChannelCodecName(), i1, l2);
            this.drawInfo("" + teamspeakchannel.getChannelCodecQuality(), i1, l2 += 12);
            this.drawInfo(TeamSpeak.status(teamspeakchannel.getIsPermanent(), "Perm ", "") + TeamSpeak.status(teamspeakchannel.getIsSemiPermanent(), "Semi perm ", "") + TeamSpeak.status(teamspeakchannel.getIsPassword(), "Password", ""), i1, l2 += 12);
            this.drawInfo(teamspeakchannel.getTotalClients() + "/" + String.valueOf(teamspeakchannel.getMaxClients()).replace("-1", "Unlimited"), i1, l2 += 12);
            this.drawInfo(TeamSpeak.status(teamspeakchannel.getSubscription(), "Subed", "Not subed"), i1, l2 += 12);
            l2 += 12;
        }
    }

    public void drawInfo(String text, int x2, int y2) {
        if (y2 < TeamSpeak.ySplit - 10) {
            this.draw.drawString(text, x2, y2);
        }
    }

    public void drawBox(int mouseX, int mouseY) {
        if (this.boxEnabled) {
            boolean flag;
            int i2 = this.boxPosX;
            int j2 = this.boxPosY;
            int k2 = this.boxPosX + this.boxLengthX;
            int l2 = this.boxPosY + this.boxLengthY;
            boolean bl2 = flag = this.boxLengthY == 0;
            if (this.boxIsUser) {
                if (this.boxId == TeamSpeakController.getInstance().me().getClientId()) {
                    int i1 = 0;
                    this.draw.drawBox(i2, j2, k2, l2);
                    this.boxSlot("Change nickname", i2, j2, k2, l2, i1, mouseX, mouseY);
                    this.boxSlot("Channel commander", i2, j2, k2, l2, i1 += 15, mouseX, mouseY);
                    this.boxLengthX = 110;
                    this.boxLengthY = i1 += 15;
                } else {
                    int j1 = 0;
                    this.draw.drawBox(i2, j2, k2, l2);
                    this.boxSlot("Open chat", i2, j2, k2, l2, j1, mouseX, mouseY);
                    this.boxSlot("Poke", i2, j2, k2, l2, j1 += 15, mouseX, mouseY);
                    this.boxSlot("Move to me", i2, j2, k2, l2, j1 += 15, mouseX, mouseY);
                    this.boxLengthX = 145;
                    this.boxLengthY = j1 += 15;
                }
            } else {
                int k1 = 0;
                this.draw.drawBox(i2, j2, k2, l2);
                this.boxSlot("Switch channel", i2, j2, k2, l2, k1, mouseX, mouseY);
                this.boxLengthX = 100;
                this.boxLengthY = k1 += 15;
            }
            if (flag) {
                int l1 = i2 + this.boxLengthX;
                int i22 = j2 + this.boxLengthY;
                this.draw.drawBox(i2, j2, l1, i22);
            }
        }
    }

    public void boxSlot(String text, int x2, int y2, int lengthX, int lengthY, int slot, int mouseX, int mouseY) {
        String s2 = ModColor.cl("7");
        if (mouseX > x2 && mouseX < x2 + lengthX && mouseY > y2 + slot && mouseY < y2 + slot + 15) {
            s2 = ModColor.cl("f");
        }
        this.draw.drawString(String.valueOf(s2) + text, x2 + 5, y2 + 4 + slot);
    }

    public void boxSplit(int x2, int y2, int lengthX, int lengthY, int slot, int mouseX, int mouseY) {
        DrawUtils drawutils = this.draw;
        DrawUtils.drawRect(x2 + 5, y2 + slot + 3, lengthX - 5, y2 + slot + 4, 1423232232);
    }

    private boolean boxClick(int x2, int y2, int lengthX, int lengthY, int slot, int mouseX, int mouseY) {
        return mouseX > x2 && mouseX < x2 + lengthX && mouseY > y2 + slot && mouseY < y2 + slot + 15;
    }

    private boolean boxAction(int mouseX, int mouseY, int mouseButton) {
        if (!this.boxEnabled) {
            return false;
        }
        if (mouseX > this.boxPosX && mouseX < this.boxPosX + this.boxLengthX && mouseY > this.boxPosY && mouseY < this.boxPosY + this.boxLengthY) {
            if (mouseButton != 0) {
                return true;
            }
            int i2 = this.boxPosX;
            int j2 = this.boxPosY;
            int k2 = this.boxPosX + this.boxLengthX;
            int l2 = this.boxPosY + this.boxLengthY;
            if (this.boxIsUser) {
                if (this.boxId == TeamSpeakController.getInstance().me().getClientId()) {
                    int i1 = 0;
                    if (this.boxClick(i2, j2, k2, l2, i1, mouseX, mouseY)) {
                        this.openNickNameBox();
                    }
                    if (this.boxClick(i2, j2, k2, l2, i1 += 15, mouseX, mouseY)) {
                        TeamSpeakBridge.setChannelCommander(!TeamSpeakController.getInstance().me().isChannelCommander());
                    }
                    i1 += 15;
                } else {
                    int j1 = 0;
                    if (this.boxClick(i2, j2, k2, l2, j1, mouseX, mouseY)) {
                        TeamSpeak.addChat(TeamSpeakController.getInstance().getUser(this.boxId), TeamSpeakController.getInstance().me(), null, EnumTargetMode.USER);
                        TeamSpeak.selectedChat = this.boxId;
                    }
                    if (this.boxClick(i2, j2, k2, l2, j1 += 15, mouseX, mouseY)) {
                        TeamSpeak.overlayWindows.openInput(TeamSpeak.selectedUser, "Poke Title", "gui_ts_window_poke_content", new PopUpCallback(){

                            @Override
                            public void ok(int id2, String message) {
                                TeamSpeakUser teamspeakuser = TeamSpeakController.getInstance().getUser(id2);
                                if (teamspeakuser == null) {
                                    TeamSpeak.error("Poke error offline");
                                } else {
                                    TeamSpeakBridge.pokeClient(teamspeakuser, message);
                                }
                            }

                            @Override
                            public void ok() {
                            }

                            @Override
                            public void cancel() {
                            }

                            @Override
                            public boolean tick(int cid) {
                                return false;
                            }
                        });
                    }
                    if (this.boxClick(i2, j2, k2, l2, j1 += 15, mouseX, mouseY)) {
                        TeamSpeakBridge.moveClient(this.boxId, TeamSpeakController.getInstance().me().getChannelId());
                    }
                    j1 += 15;
                }
            } else {
                int k1 = 0;
                if (this.boxClick(i2, j2, k2, l2, k1, mouseX, mouseY)) {
                    if (TeamSpeakController.getInstance().me() != null) {
                        TeamSpeakBridge.moveClient(TeamSpeakController.getInstance().me().getClientId(), this.boxId);
                    }
                    this.closeBox();
                }
                k1 += 15;
            }
            this.closeBox();
            return true;
        }
        return false;
    }

    private void openBox(boolean isUser, int id2, int x2, int y2) {
        this.boxEnabled = true;
        this.boxIsUser = isUser;
        this.boxId = id2;
        this.boxPosX = x2;
        this.boxPosY = y2;
        if (isUser) {
            TeamSpeak.selectedChannel = -1;
            TeamSpeak.selectedUser = id2;
        } else {
            TeamSpeak.selectedUser = -1;
            TeamSpeak.selectedChannel = id2;
        }
    }

    private void closeBox() {
        this.boxEnabled = false;
        this.boxIsUser = true;
        this.boxId = 0;
        this.boxPosX = 0;
        this.boxPosY = 0;
        this.boxLengthX = 0;
        this.boxLengthY = 0;
    }

    private void changeNickname() {
        this.changeNickName = false;
        if (!this.nickNameField.getText().equals(TeamSpeakController.getInstance().me().getNickName())) {
            TeamSpeakBridge.setNickname(this.nickNameField.getText());
        }
    }

    private void openNickNameBox() {
        this.changeNickName = true;
        this.nickNameField.setFocused(true);
        this.nickNameField.setText(TeamSpeakController.getInstance().me().getNickName());
    }

    private void drag(boolean isUser, int Id) {
        this.resetDrag();
        this.drag = true;
        this.dragIsUser = isUser;
        this.dragId = Id;
    }

    private void drop(int channelId, boolean isInRegion) {
        if (this.drag) {
            this.dropFocus = isInRegion;
            if (this.drop) {
                if (this.dragIsUser && this.dropFocus) {
                    TeamSpeakBridge.moveClient(this.dragId, channelId);
                }
                this.resetDrag();
            }
        }
    }

    private void resetDrag() {
        this.drag = false;
        this.drop = false;
        this.dragIsUser = false;
        this.dragId = 0;
        this.dropX = 0;
        this.dropY = 0;
        this.dragVisible = 0;
        this.dropFocus = false;
    }

    public void setDrop(int x2, int y2) {
        this.dropX = x2;
        this.dropY = y2;
        this.drop = true;
    }

    public void drawDrag(int mouseX, int mouseY) {
        if (this.drag && this.dragVisible >= 5) {
            String s2 = "";
            String s1 = "";
            if (this.dragIsUser) {
                TeamSpeakUser teamspeakuser = TeamSpeakController.getInstance().getUser(this.dragId);
                if (teamspeakuser == null) {
                    return;
                }
                s1 = teamspeakuser.getNickName();
            } else {
                TeamSpeakChannel teamspeakchannel = TeamSpeakController.getInstance().getChannel(this.dragId);
                if (teamspeakchannel == null) {
                    return;
                }
                s1 = teamspeakchannel.getChannelName();
            }
            if (!this.dropFocus) {
                s2 = ModColor.cl("c");
            }
            this.draw.drawString(String.valueOf(s2) + s1, mouseX, mouseY);
        }
    }

    public void drawMenu(int mouseX, int mouseY) {
        if (TeamSpeakController.getInstance().me() != null) {
            int i2 = 0;
            String s2 = String.valueOf(TeamSpeakController.getInstance().serverIP) + ":" + TeamSpeakController.getInstance().serverPort;
            DrawUtils drawutils = this.draw;
            DrawUtils.drawRect(i2 += this.draw.getWidth() - 30 - this.draw.getStringWidth(s2), 5, this.draw.getWidth() - 5, 20, Integer.MIN_VALUE);
            this.draw.drawRightString(String.valueOf(ModColor.cl("a")) + s2, this.draw.getWidth() - 20, 9.0);
            i2 -= 4;
            int j2 = Integer.MIN_VALUE;
            if (TeamSpeakController.getInstance().me().hasClientOutputMuted()) {
                j2 = 2122022291;
            }
            drawutils = this.draw;
            DrawUtils.drawRect(i2, 5, i2 - 16, 20, j2);
            this.draw.drawString(String.valueOf(ModColor.cl("f")), 0.0, 0.0);
            this.mc.getTextureManager().bindTexture(new ResourceLocation("labymod/textures/teamspeak.png"));
            this.draw.drawTexturedModalRect(i2 - 16 + 3, 7, 12, 0, 12, 12);
            i2 -= 20;
            j2 = Integer.MIN_VALUE;
            if (TeamSpeakController.getInstance().me().hasClientInputMuted()) {
                j2 = 2122022291;
            }
            drawutils = this.draw;
            DrawUtils.drawRect(i2, 5, i2 - 16, 20, j2);
            this.draw.drawString(String.valueOf(ModColor.cl("f")), 0.0, 0.0);
            this.mc.getTextureManager().bindTexture(new ResourceLocation("labymod/textures/teamspeak.png"));
            this.draw.drawTexturedModalRect(i2 - 16 + 3, 7, 0, 0, 12, 12);
            i2 -= 20;
            j2 = Integer.MIN_VALUE;
            if (TeamSpeak.teamSpeakGroupPrefix) {
                j2 = 2122022291;
            }
            drawutils = this.draw;
            DrawUtils.drawRect(i2, 5, i2 - 16, 20, j2);
            this.draw.drawCenteredString(String.valueOf(ModColor.cl("b")) + "[]", i2 - 8, 9.0);
        }
    }

    public void menuAction(int mouseX, int mouseY, int mouseButton) {
        int i2 = 0;
        String s2 = String.valueOf(TeamSpeakController.getInstance().serverIP) + ":" + TeamSpeakController.getInstance().serverPort;
        i2 += this.draw.getWidth() - 30 - this.draw.getStringWidth(s2);
        if (mouseX > (i2 -= 4) - 16 && mouseX < i2 && mouseY > 5 && mouseY < 20) {
            TeamSpeakBridge.setOutputMuted(!TeamSpeakController.getInstance().me().hasClientOutputMuted());
        }
        if (mouseX > (i2 -= 20) - 16 && mouseX < i2 && mouseY > 5 && mouseY < 20) {
            TeamSpeakBridge.setInputMuted(!TeamSpeakController.getInstance().me().hasClientInputMuted());
        }
        if (mouseX > (i2 -= 20) - 16 && mouseX < i2 && mouseY > 5 && mouseY < 20) {
            TeamSpeak.teamSpeakGroupPrefix = !TeamSpeak.teamSpeakGroupPrefix;
        }
    }

    public void callBackListener(int mouseX, int mouseY) {
        if (TeamSpeak.callBack) {
            TeamSpeak.callBack = false;
            this.openBox(true, TeamSpeak.callBackClient, mouseX, mouseY);
        }
    }
}

