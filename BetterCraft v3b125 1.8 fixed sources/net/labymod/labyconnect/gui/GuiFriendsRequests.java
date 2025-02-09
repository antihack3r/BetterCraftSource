/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.labyconnect.gui;

import java.io.IOException;
import java.util.ArrayList;
import net.labymod.gui.elements.Scrollbar;
import net.labymod.labyconnect.LabyConnect;
import net.labymod.labyconnect.packets.PacketPlayDenyFriendRequest;
import net.labymod.labyconnect.packets.PacketPlayRequestAddFriend;
import net.labymod.labyconnect.user.ChatRequest;
import net.labymod.main.LabyMod;
import net.labymod.main.lang.LanguageManager;
import net.labymod.utils.DrawUtils;
import net.labymod.utils.ModColor;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Keyboard;

public class GuiFriendsRequests
extends GuiScreen {
    public static String response = null;
    private GuiScreen lastScreen;
    private Scrollbar scrollbar = new Scrollbar(10);
    private ChatRequest hoverAccept = null;
    private ChatRequest hoverDeny = null;

    public GuiFriendsRequests(GuiScreen lastScreen) {
        this.lastScreen = lastScreen;
    }

    @Override
    public void initGui() {
        super.initGui();
        Keyboard.enableRepeatEvents(true);
        this.buttonList.add(new GuiButton(1, width / 2 - 101, height - 37, 98, 20, LanguageManager.translate("button_done")));
        this.scrollbar.init();
        this.scrollbar.setPosition(width / 2 + 100, 32, width / 2 + 104, height - 40 - 2);
        this.scrollbar.setSpeed(10);
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
        LabyConnect chatClient = LabyMod.getInstance().getLabyConnect();
        if (this.hoverAccept != null) {
            chatClient.getClientConnection().sendPacket(new PacketPlayRequestAddFriend(this.hoverAccept.getGameProfile().getName()));
            chatClient.getRequests().remove(this.hoverAccept);
        } else if (this.hoverDeny != null) {
            chatClient.getClientConnection().sendPacket(new PacketPlayDenyFriendRequest(this.hoverDeny));
            chatClient.getRequests().remove(this.hoverDeny);
        }
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
        DrawUtils draw = LabyMod.getInstance().getDrawUtils();
        this.drawBackground(0);
        draw.drawDimmedOverlayBackground(width / 2 - 100, 30, width / 2 + 100, height - 40);
        this.hoverAccept = null;
        this.hoverDeny = null;
        double listY = 32.0 + this.scrollbar.getScrollY() + 1.0;
        int entryHeight = 10;
        ArrayList<ChatRequest> list = new ArrayList<ChatRequest>(LabyMod.getInstance().getLabyConnect().getRequests());
        for (ChatRequest requester : list) {
            boolean hoverEntry = mouseX > width / 2 - 100 && mouseX < width / 2 + 100 && (double)mouseY > listY && (double)mouseY < listY + 10.0 + 1.0;
            int hoverColor = hoverEntry ? 200 : 100;
            DrawUtils.drawRect((double)(width / 2 - 100), listY, (double)(width / 2 + 100), listY + 10.0, ModColor.toRGB(hoverColor, hoverColor, hoverColor, 30));
            GlStateManager.color(1.0f, 1.0f, 1.0f);
            draw.drawPlayerHead(requester.getGameProfile(), width / 2 - 97, (int)listY, 10);
            draw.drawString(requester.getGameProfile().getName(), width / 2 - 85, listY + 1.0);
            if (hoverEntry) {
                boolean hoverDeny;
                boolean hoverAccept;
                int buttonWidth = 10;
                boolean bl2 = hoverAccept = mouseX > width / 2 + 60 - 10 && mouseX < width / 2 + 60 + 10;
                if (hoverAccept) {
                    DrawUtils.drawRect((double)(width / 2 + 60 - 10), listY, (double)(width / 2 + 60 + 10), listY + 10.0, ModColor.toRGB(255, 255, 255, 50));
                    this.hoverAccept = requester;
                }
                boolean bl3 = hoverDeny = mouseX > width / 2 + 80 - 10 && mouseX < width / 2 + 80 + 10;
                if (hoverDeny) {
                    DrawUtils.drawRect((double)(width / 2 + 80 - 10), listY, (double)(width / 2 + 80 + 10), listY + 10.0, ModColor.toRGB(255, 255, 255, 50));
                    this.hoverDeny = requester;
                }
                draw.drawCenteredString((Object)((Object)ModColor.GREEN) + "\u2714", width / 2 + 60, listY + 1.0);
                draw.drawCenteredString((Object)((Object)ModColor.RED) + "\u2716", width / 2 + 80, listY + 1.0);
            }
            listY += 11.0;
        }
        draw.drawGradientShadowTop(30.0, width / 2 - 100, width / 2 + 100);
        draw.drawGradientShadowBottom(height - 40, width / 2 - 100, width / 2 + 100);
        draw.drawString(String.valueOf(LanguageManager.translate("button_requests")) + ":", width / 2 - 100, 20.0);
        int size = LabyMod.getInstance().getLabyConnect().getRequests().size();
        draw.drawRightString(String.valueOf(size) + " " + LanguageManager.translate(size == 1 ? "button_request" : "button_requests"), width / 2 + 99, height - 35);
        this.scrollbar.update(list.size());
        this.scrollbar.setEntryHeight(11.0);
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

