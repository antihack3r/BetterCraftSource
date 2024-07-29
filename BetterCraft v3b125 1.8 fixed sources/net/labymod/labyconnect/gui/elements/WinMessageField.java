/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.labyconnect.gui.elements;

import java.util.List;
import net.labymod.core.LabyModCore;
import net.labymod.gui.elements.ModTextField;
import net.labymod.gui.layout.WindowElement;
import net.labymod.labyconnect.gui.GuiFriendsLayout;
import net.labymod.labyconnect.log.MessageChatComponent;
import net.labymod.labyconnect.log.SingleChat;
import net.labymod.labyconnect.packets.PacketPlayTyping;
import net.labymod.main.LabyMod;
import net.labymod.main.lang.LanguageManager;
import net.minecraft.client.gui.GuiButton;

public class WinMessageField
extends WindowElement<GuiFriendsLayout> {
    private static String storedMessage = "";
    private static long cooldown = 0L;
    private ModTextField fieldMessage;
    private GuiButton buttonSendMessage;
    private long typingCooldown = 0L;

    public WinMessageField(GuiFriendsLayout chatLayout) {
        super(chatLayout);
    }

    @Override
    protected void init(List<GuiButton> buttonlist, int left, int top, int right, int bottom) {
        int paddingHeight = 3;
        boolean paddingWidth = false;
        int dragBarWidth = 2;
        int spaceBetween = 5;
        int buttonWidth = 70;
        this.fieldMessage = new ModTextField(0, LabyModCore.getMinecraft().getFontRenderer(), left + 0 + 2, top + 3, right - left - 70 - 0 - 5, bottom - top - 6);
        this.fieldMessage.setBlackBox(false);
        this.fieldMessage.setMaxStringLength(120);
        this.fieldMessage.setText(storedMessage);
        this.buttonSendMessage = new GuiButton(1, right - 70, top + (bottom - top - 20) / 2, 70, 20, LanguageManager.translate("button_send"));
        buttonlist.add(this.buttonSendMessage);
    }

    @Override
    public void draw(int mouseX, int mouseY) {
        super.draw(mouseX, mouseY);
        this.fieldMessage.drawTextBox();
        this.buttonSendMessage.enabled = GuiFriendsLayout.selectedUser != null && !this.fieldMessage.getText().replaceAll(" ", "").isEmpty() && cooldown < System.currentTimeMillis();
        this.fieldMessage.setEnabled(GuiFriendsLayout.selectedUser != null);
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        this.fieldMessage.mouseClicked(mouseX, mouseY, mouseButton);
        return false;
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (this.fieldMessage.textboxKeyTyped(typedChar, keyCode)) {
            storedMessage = this.fieldMessage.getText();
            if (GuiFriendsLayout.selectedUser != null && this.typingCooldown + 1000L < System.currentTimeMillis()) {
                this.typingCooldown = System.currentTimeMillis();
                LabyMod.getInstance().getLabyConnect().getClientConnection().sendPacket(new PacketPlayTyping(LabyMod.getInstance().getLabyConnect().getClientProfile().buildClientUser(), GuiFriendsLayout.selectedUser, true));
            }
        }
        if (keyCode == 28 && this.fieldMessage.isFocused()) {
            this.actionPerformed(this.buttonSendMessage);
        }
    }

    @Override
    public void actionPerformed(GuiButton button) {
        if (button.enabled && button.id == 1 && GuiFriendsLayout.selectedUser != null && !this.fieldMessage.getText().replaceAll(" ", "").isEmpty()) {
            SingleChat singleChat = LabyMod.getInstance().getLabyConnect().getChatlogManager().getChat(GuiFriendsLayout.selectedUser);
            MessageChatComponent messageChatComponent = new MessageChatComponent(LabyMod.getInstance().getPlayerName(), System.currentTimeMillis(), this.fieldMessage.getText());
            singleChat.addMessage(messageChatComponent);
            this.fieldMessage.setText("");
            storedMessage = "";
            cooldown = System.currentTimeMillis() + 1000L;
        }
    }

    @Override
    public void mouseClickMove(int mouseX, int mouseY) {
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
    }

    @Override
    public void mouseInput() {
    }

    @Override
    public void updateScreen() {
        this.fieldMessage.updateCursorCounter();
    }

    public ModTextField getFieldMessage() {
        return this.fieldMessage;
    }
}

