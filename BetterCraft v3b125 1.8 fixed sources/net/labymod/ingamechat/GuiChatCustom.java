/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.ingamechat;

import java.io.IOException;
import java.util.ArrayList;
import me.nzxtercode.bettercraft.client.utils.ColorUtils;
import net.labymod.api.events.MessageSendEvent;
import net.labymod.api.permissions.Permissions;
import net.labymod.core.LabyModCore;
import net.labymod.ingamechat.IngameChatManager;
import net.labymod.ingamechat.namehistory.NameHistory;
import net.labymod.ingamechat.namehistory.NameHistoryUtil;
import net.labymod.ingamechat.renderer.EnumMouseAction;
import net.labymod.ingamechat.tabs.GuiChatAutoText;
import net.labymod.ingamechat.tabs.GuiChatNameHistory;
import net.labymod.ingamechat.tabs.GuiChatPlayerMenu;
import net.labymod.ingamechat.tabs.GuiChatShortcuts;
import net.labymod.ingamechat.tabs.GuiChatSymbols;
import net.labymod.ingamechat.tools.shortcuts.Shortcuts;
import net.labymod.main.LabyMod;
import net.labymod.main.ModTextures;
import net.labymod.main.lang.LanguageManager;
import net.labymod.settings.LabyModModuleEditorGui;
import net.labymod.settings.elements.ControlElement;
import net.labymod.utils.Material;
import net.labymod.utils.ModColor;
import net.labymod.utils.ModUtils;
import net.labymod.utils.UUIDFetcher;
import net.labymod.utils.manager.TooltipHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class GuiChatCustom
extends GuiChat {
    public static int activeTab = -1;
    private final IngameChatManager ingameChatManager = LabyMod.getInstance().getIngameChatManager();
    private ChatButton[] chatButtons;
    private String defaultText;

    public GuiChatCustom(String defaultText) {
        super(defaultText);
        this.defaultText = defaultText;
    }

    public GuiChatCustom() {
    }

    @Override
    public void initGui() {
        super.initGui();
        if (this.mc.currentScreen != null && this.mc.currentScreen.getClass() == GuiChatCustom.class) {
            activeTab = -1;
        }
        boolean chatFeaturesAllowed = LabyMod.getInstance().getServerManager().isAllowed(Permissions.Permission.CHAT);
        ArrayList<ChatButton> chatButtonList = new ArrayList<ChatButton>();
        if (LabyMod.getSettings().chatSymbols) {
            chatButtonList.add(new ChatButton(0, "symbols", new ControlElement.IconData(ModTextures.CHAT_TAB_SYMBOLS), chatFeaturesAllowed));
        }
        if (LabyMod.getSettings().nameHistory) {
            chatButtonList.add(new ChatButton(5, "namehistory", new ControlElement.IconData(Material.BOOK_AND_QUILL), true));
        }
        this.chatButtons = new ChatButton[chatButtonList.size()];
        chatButtonList.toArray(this.chatButtons);
        this.inputField.setText(this.defaultText == null ? "" : this.defaultText);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
        if (button.id == 4) {
            this.mc.displayGuiScreen(new GuiChatNameHistory(this.inputField.getText()));
        }
    }

    public void drawButtons(int mouseX, int mouseY, float partialTicks) {
        int slot = 0;
        ChatButton[] chatButtonArray = this.chatButtons;
        int n2 = this.chatButtons.length;
        int n3 = 0;
        while (n3 < n2) {
            boolean hoverSymbols;
            ChatButton chatButton = chatButtonArray[n3];
            boolean enabled = chatButton.isEnabled();
            int x2 = width - 2 - 11 - slot * 14;
            int y2 = height - 14;
            if (slot == activeTab) {
                GuiChatCustom.drawRect(x2, y2 - 2, x2 + 13, y2, Integer.MIN_VALUE);
            }
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            boolean bl2 = hoverSymbols = mouseX >= x2 && mouseX < x2 + 13 && mouseY > y2 && mouseY < y2 + 12;
            if (chatButton.getIconData().hasMaterialIcon()) {
                GlStateManager.pushMatrix();
                double scale = hoverSymbols ? 0.7 : 0.6;
                GlStateManager.scale(scale, scale, 1.0);
                LabyMod.getInstance().getDrawUtils().renderItemIntoGUI(chatButton.getItem(), ((double)x2 + 5.5 - scale * 6.0) / scale, ((double)(y2 + 5) - scale * 6.0) / scale);
                GlStateManager.popMatrix();
            } else if (chatButton.getIconData().hastextureIcon()) {
                Minecraft.getMinecraft().getTextureManager().bindTexture(chatButton.getIconData().gettextureIcon());
                LabyMod.getInstance().getDrawUtils().drawTexture(x2 + 2 - (hoverSymbols ? 1 : 0), y2 + 2 - (hoverSymbols ? 1 : 0), 255.0, 255.0, hoverSymbols ? 11.0 : 9.0, hoverSymbols ? 11.0 : 9.0);
            }
            if (hoverSymbols) {
                TooltipHelper.getHelper().pointTooltip(mouseX, mouseY, 0L, enabled ? chatButton.getDisplayName() : LanguageManager.translate("ingame_chat_feature_not_allowed", chatButton.getDisplayName()));
            }
            ++slot;
            ++n3;
        }
    }

    public void onButtonClick(int mouseX, int mouseY, int mouseButton) {
        int slot = 0;
        while (slot < this.chatButtons.length) {
            boolean hoverSymbols;
            ChatButton chatButton = this.chatButtons[slot];
            boolean bl2 = hoverSymbols = mouseX > width - 2 - 13 - slot * 14 && mouseX < width - 2 - slot * 14 && mouseY > height - 12 && mouseY < height;
            if (hoverSymbols && chatButton.isEnabled()) {
                switch (chatButton.getId()) {
                    case 0: {
                        activeTab = slot;
                        this.mc.displayGuiScreen(this.mc.currentScreen instanceof GuiChatSymbols ? new GuiChatCustom(this.inputField.getText()) : new GuiChatSymbols(this.inputField.getText()));
                        break;
                    }
                    case 1: {
                        activeTab = slot;
                        this.mc.displayGuiScreen(this.mc.currentScreen instanceof GuiChatAutoText ? new GuiChatCustom(this.inputField.getText()) : new GuiChatAutoText(this.inputField.getText()));
                        break;
                    }
                    case 2: {
                        activeTab = slot;
                        this.mc.displayGuiScreen(this.mc.currentScreen instanceof GuiChatShortcuts ? new GuiChatCustom(this.inputField.getText()) : new GuiChatShortcuts(this.inputField.getText()));
                        break;
                    }
                    case 3: {
                        activeTab = slot;
                        this.mc.displayGuiScreen(this.mc.currentScreen instanceof GuiChatPlayerMenu ? new GuiChatCustom(this.inputField.getText()) : new GuiChatPlayerMenu(this.inputField.getText()));
                        break;
                    }
                    case 4: {
                        activeTab = slot;
                        break;
                    }
                    case 5: {
                        activeTab = slot;
                        this.mc.displayGuiScreen(this.mc.currentScreen instanceof GuiChatNameHistory ? new GuiChatCustom(this.inputField.getText()) : new GuiChatNameHistory(this.inputField.getText()));
                        break;
                    }
                    case 6: {
                        activeTab = slot;
                        this.mc.displayGuiScreen(new LabyModModuleEditorGui(new GuiChatCustom(this.inputField.getText())));
                    }
                }
            }
            ++slot;
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        String name;
        String value;
        this.ingameChatManager.handleMouse(mouseX, mouseY, mouseButton, EnumMouseAction.CLICKED);
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.onButtonClick(mouseX, mouseY, mouseButton);
        if (mouseButton == 1 && (value = LabyModCore.getMinecraft().getClickEventValue(Mouse.getX(), Mouse.getY())) != null && value.startsWith("/msg ") && !NameHistoryUtil.isInCache(name = value.replace("/msg ", "").replace(" ", ""))) {
            NameHistoryUtil.getNameHistory(name);
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        this.ingameChatManager.handleMouse(mouseX, mouseY, state, EnumMouseAction.RELEASED);
        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        this.ingameChatManager.handleMouse(mouseX, mouseY, clickedMouseButton, EnumMouseAction.DRAGGING);
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawModifiedSuperScreen(mouseX, mouseY, partialTicks);
        GuiChatCustom.drawRect(2, height - 14, width - 2, height - 2, Integer.MIN_VALUE);
        this.ingameChatManager.handleMouse(mouseX, mouseY, -1, EnumMouseAction.RENDER);
        this.drawButtons(mouseX, mouseY, partialTicks);
        this.inputField.drawTextBox();
        this.drawString(this.fontRendererObj, String.valueOf(this.inputField.getText().length()) + " / " + this.inputField.getMaxStringLength(), 5, height - 25, ColorUtils.rainbowEffect());
        this.inputField.setMaxStringLength(this.inputField.getText().startsWith("/") ? Integer.MAX_VALUE : 100);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        String value = LabyModCore.getMinecraft().getClickEventValue(Mouse.getX(), Mouse.getY());
        if (LabyMod.getSettings().hoverNameHistory && value != null && value.startsWith("/msg ")) {
            String name = value.replace("/msg ", "").replace(" ", "");
            if (NameHistoryUtil.isInCache(name)) {
                NameHistory history = NameHistoryUtil.getNameHistory(name);
                ArrayList<String> lines = new ArrayList<String>();
                boolean currentName = true;
                UUIDFetcher[] uUIDFetcherArray = history.getChanges();
                int n2 = uUIDFetcherArray.length;
                int n3 = 0;
                while (n3 < n2) {
                    UUIDFetcher change = uUIDFetcherArray[n3];
                    if (change.changedToAt != 0L) {
                        String date = ModUtils.getTimeDiff(change.changedToAt);
                        String c2 = "7";
                        if (currentName) {
                            c2 = "6";
                        }
                        currentName = false;
                        lines.add(String.valueOf(ModColor.cl(c2)) + change.name + ModColor.cl("8") + " - " + ModColor.cl("8") + date);
                    } else {
                        lines.add(String.valueOf(ModColor.cl("a")) + change.name);
                    }
                    ++n3;
                }
                this.drawHoveringText(lines, mouseX, mouseY);
            } else {
                ArrayList<String> lines2 = new ArrayList<String>();
                lines2.add(LanguageManager.translate("ingame_chat_rightclick_for_namechanges"));
                this.drawHoveringText(lines2, mouseX, mouseY);
            }
            GlStateManager.disableLighting();
        }
    }

    private void drawModifiedSuperScreen(int mouseX, int mouseY, float partialTicks) {
        this.inputField.drawTextBox();
        this.handleComponentHover(this.mc.ingameGUI.getChatGUI().getChatComponent(Mouse.getX(), Mouse.getY()), mouseX, mouseY);
        for (GuiButton guiButton : this.buttonList) {
            LabyModCore.getMinecraft().drawButton(guiButton, mouseX, mouseY);
        }
        for (GuiLabel guiLabel : this.labelList) {
            guiLabel.drawLabel(this.mc, mouseX, mouseY);
        }
    }

    @Override
    public void sendChatMessage(String msg, boolean addToChat) {
        boolean cancelled = false;
        for (Shortcuts.Shortcut shortcut : LabyMod.getInstance().getChatToolManager().getShortcuts()) {
            msg = msg.replace(shortcut.getShortcut(), String.format(shortcut.getReplacement(), LabyMod.getInstance().getPlayerName()));
        }
        for (MessageSendEvent messageSend : LabyMod.getInstance().getEventManager().getMessageSend()) {
            if (!messageSend.onSend(msg) || cancelled) continue;
            cancelled = true;
        }
        if (cancelled) {
            if (addToChat) {
                this.mc.ingameGUI.getChatGUI().addToSentMessages(msg);
            }
            return;
        }
        super.sendChatMessage(msg, addToChat);
    }

    private class ChatButton {
        private final int id;
        private final String displayName;
        private final boolean enabled;
        private final ItemStack item;
        private final ControlElement.IconData iconData;

        public ChatButton(int id2, String languageKey, ControlElement.IconData iconData, boolean enabled) {
            this.id = id2;
            this.displayName = LanguageManager.translate("ingame_chat_tab_" + languageKey);
            this.item = iconData.hasMaterialIcon() ? iconData.getMaterialIcon().createItemStack() : null;
            this.iconData = iconData;
            this.enabled = enabled;
        }

        public int getId() {
            return this.id;
        }

        public String getDisplayName() {
            return this.displayName;
        }

        public boolean isEnabled() {
            return this.enabled;
        }

        public ItemStack getItem() {
            return this.item;
        }

        public ControlElement.IconData getIconData() {
            return this.iconData;
        }
    }
}

