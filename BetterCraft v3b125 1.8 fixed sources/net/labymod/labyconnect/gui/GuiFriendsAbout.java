/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.labyconnect.gui;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import net.labymod.gui.elements.Scrollbar;
import net.labymod.labyconnect.user.ChatUser;
import net.labymod.labyconnect.user.UserStatus;
import net.labymod.main.LabyMod;
import net.labymod.main.lang.LanguageManager;
import net.labymod.utils.DrawUtils;
import net.labymod.utils.ModColor;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Keyboard;

public class GuiFriendsAbout
extends GuiScreen {
    private GuiScreen lastScreen;
    private Scrollbar scrollbar = new Scrollbar(10);
    private DateFormat formatTime = new SimpleDateFormat("HH:mm");
    private DateFormat formatDate = new SimpleDateFormat("yyyy/MM/dd HH:mm");
    private Map<String, String> infoList = new HashMap<String, String>();
    private ChatUser player;

    public GuiFriendsAbout(GuiScreen lastScreen, ChatUser player) {
        this.lastScreen = lastScreen;
        this.player = player;
    }

    @Override
    public void initGui() {
        super.initGui();
        Keyboard.enableRepeatEvents(true);
        this.buttonList.add(new GuiButton(1, width / 2 - 100, height - 37, 200, 20, LanguageManager.translate("button_done")));
        this.scrollbar.init();
        this.scrollbar.setPosition(width / 2 + 130, 40, width / 2 + 134, height - 40);
        this.scrollbar.setSpeed(10);
        this.infoList.clear();
        this.formatTime.setCalendar(Calendar.getInstance());
        this.formatTime.setTimeZone(TimeZone.getTimeZone(this.player.getTimeZone()));
        this.infoList.put(LanguageManager.translate("profile_timezone"), LabyMod.getInstance().getDrawUtils().trimStringToWidth(this.formatTime.getTimeZone().getDisplayName(), 120));
        this.infoList.put(LanguageManager.translate("profile_time"), this.formatTime.format(this.formatTime.getCalendar().getTime()));
        if (this.player.getFirstJoined() != 0L) {
            this.infoList.put(LanguageManager.translate("profile_first_joined"), this.formatDate.format(this.player.getFirstJoined()));
        }
        if (this.player.getLastOnline() != 0L) {
            this.infoList.put(LanguageManager.translate("profile_last_online"), this.formatDate.format(this.player.isOnline() ? System.currentTimeMillis() : this.player.getLastOnline()));
        }
        if (this.player.getContactAmount() != 0) {
            this.infoList.put(LanguageManager.translate("profile_friends"), "" + this.player.getContactAmount());
        }
        if (!this.player.getStatusMessage().isEmpty()) {
            this.infoList.put(LanguageManager.translate("profile_status_message"), LabyMod.getInstance().getDrawUtils().trimStringToWidth(this.player.getStatusMessage(), 100));
        }
        this.infoList.put(LanguageManager.translate("profile_status"), String.valueOf(ModColor.cl(this.player.getStatus().getChatColor())) + this.player.getStatus().getName());
        if (this.player.getStatus() != UserStatus.OFFLINE) {
            this.infoList.put("Playing", this.player.getCurrentServerInfo() != null && this.player.getCurrentServerInfo().isServerAvailable() ? this.player.getCurrentServerInfo().getDisplayAddress() : "No");
        }
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == 1) {
            this.mc.displayGuiScreen(this.lastScreen);
            return;
        }
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.scrollbar.mouseAction(mouseX, mouseY, Scrollbar.EnumMouseAction.CLICKED);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
        if (button.id == 1) {
            this.mc.displayGuiScreen(this.lastScreen);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawBackground(0);
        double listY = 42.0 + this.scrollbar.getScrollY();
        int entryHeight = 10;
        for (Map.Entry<String, String> entry : this.infoList.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            boolean hoverEntry = mouseX > width / 2 - 130 && mouseX < width / 2 + 130 && (double)mouseY > listY && (double)mouseY < listY + 10.0 + 1.0;
            int hoverColor = hoverEntry ? 200 : 100;
            LabyMod.getInstance().getDrawUtils();
            DrawUtils.drawRect((double)(width / 2 - 130), listY, (double)(width / 2 + 130), listY + 10.0, ModColor.toRGB(hoverColor, hoverColor, hoverColor, 30));
            LabyMod.getInstance().getDrawUtils().drawString(String.valueOf(ModColor.cl("e")) + key + ":", width / 2 - 120, listY + 1.0);
            LabyMod.getInstance().getDrawUtils().drawRightString(value, width / 2 + 120, listY + 1.0);
            listY += 11.0;
        }
        LabyMod.getInstance().getDrawUtils().drawString(LanguageManager.translate("profile_title", String.valueOf(ModColor.cl(this.player.getStatus().getChatColor())) + this.player.getGameProfile().getName()), width / 2 - 109, 30.0, 1.0);
        GlStateManager.color(1.0f, 1.0f, 1.0f);
        LabyMod.getInstance().getDrawUtils().drawPlayerHead(this.player.getGameProfile(), width / 2 - 130, 22, 17);
        this.scrollbar.update(this.infoList.size());
        this.scrollbar.draw();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
        this.scrollbar.mouseAction(mouseX, mouseY, Scrollbar.EnumMouseAction.DRAGGING);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        this.scrollbar.mouseAction(mouseX, mouseY, Scrollbar.EnumMouseAction.RELEASED);
        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        this.scrollbar.mouseInput();
    }
}

