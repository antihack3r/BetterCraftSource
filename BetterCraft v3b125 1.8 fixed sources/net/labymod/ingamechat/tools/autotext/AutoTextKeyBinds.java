/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.ingamechat.tools.autotext;

import java.util.ArrayList;
import java.util.List;
import net.labymod.utils.ModUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import org.lwjgl.input.Keyboard;

public class AutoTextKeyBinds {
    private List<AutoText> autoTextKeyBinds = new ArrayList<AutoText>();

    public List<AutoText> getAutoTextKeyBinds() {
        return this.autoTextKeyBinds;
    }

    public static class AutoText {
        private String message;
        private boolean keyShift;
        private boolean keyCtrl;
        private boolean keyAlt;
        private int keyCode;
        private boolean sendNotInstantly;
        private boolean serverBound;
        private String serverAddress;
        private boolean available = true;

        public AutoText(String message, boolean keyShift, boolean keyCtrl, boolean keyAlt, int keyCode, boolean instantSend, boolean serverBound, String serverAddress) {
            this.message = message;
            this.keyShift = keyShift;
            this.keyCtrl = keyCtrl;
            this.keyAlt = keyAlt;
            this.keyCode = keyCode;
            this.sendNotInstantly = instantSend;
            this.serverBound = serverBound;
            this.serverAddress = serverAddress == null ? "" : serverAddress;
        }

        public AutoText(AutoText component) {
            this(component.getMessage(), component.isKeyShift(), component.isKeyCtrl(), component.isKeyAlt(), component.getKeyCode(), component.isSendNotInstantly(), component.isServerBound(), component.getServerAddress());
        }

        public boolean isPressed() {
            boolean pressed;
            boolean bl2 = pressed = Keyboard.isKeyDown(this.keyCode) && (!this.keyCtrl || this.keyCtrl && Keyboard.isKeyDown(29)) && (!this.keyAlt || this.keyAlt && Keyboard.isKeyDown(56)) && (!this.keyShift || this.keyShift && Keyboard.isKeyDown(42));
            if (!this.serverBound) {
                return pressed;
            }
            if (!pressed) {
                return false;
            }
            ServerData serverData = Minecraft.getMinecraft().getCurrentServerData();
            if (serverData == null) {
                return Minecraft.getMinecraft().isSingleplayer() && this.serverAddress.equals("singleplayer");
            }
            return this.serverAddress == null || this.serverAddress.isEmpty() || ModUtils.getProfileNameByIp(serverData.serverIP).equalsIgnoreCase(this.serverAddress);
        }

        public String getMessage() {
            return this.message;
        }

        public boolean isKeyShift() {
            return this.keyShift;
        }

        public boolean isKeyCtrl() {
            return this.keyCtrl;
        }

        public boolean isKeyAlt() {
            return this.keyAlt;
        }

        public int getKeyCode() {
            return this.keyCode;
        }

        public boolean isSendNotInstantly() {
            return this.sendNotInstantly;
        }

        public boolean isServerBound() {
            return this.serverBound;
        }

        public String getServerAddress() {
            return this.serverAddress;
        }

        public boolean isAvailable() {
            return this.available;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public void setKeyShift(boolean keyShift) {
            this.keyShift = keyShift;
        }

        public void setKeyCtrl(boolean keyCtrl) {
            this.keyCtrl = keyCtrl;
        }

        public void setKeyAlt(boolean keyAlt) {
            this.keyAlt = keyAlt;
        }

        public void setKeyCode(int keyCode) {
            this.keyCode = keyCode;
        }

        public void setSendNotInstantly(boolean sendNotInstantly) {
            this.sendNotInstantly = sendNotInstantly;
        }

        public void setServerBound(boolean serverBound) {
            this.serverBound = serverBound;
        }

        public void setServerAddress(String serverAddress) {
            this.serverAddress = serverAddress;
        }

        public void setAvailable(boolean available) {
            this.available = available;
        }
    }
}

