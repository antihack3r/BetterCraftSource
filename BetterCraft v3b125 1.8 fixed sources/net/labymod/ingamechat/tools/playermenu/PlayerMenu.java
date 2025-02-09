/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.ingamechat.tools.playermenu;

import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.labymod.main.lang.LanguageManager;

public class PlayerMenu {
    private List<PlayerMenuEntry> playerMenuEntries = new ArrayList<PlayerMenuEntry>(Arrays.asList(new PlayerMenuEntry(LanguageManager.translate("playermenu_entry_partyinvite"), "/party invite {name}", true)));

    public List<PlayerMenuEntry> getPlayerMenuEntries() {
        return this.playerMenuEntries;
    }

    public static class PlayerMenuEntry {
        private String displayName;
        private String command;
        private boolean sendInstantly;

        public String getDisplayName() {
            return this.displayName;
        }

        public String getCommand() {
            return this.command;
        }

        public boolean isSendInstantly() {
            return this.sendInstantly;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        public void setCommand(String command) {
            this.command = command;
        }

        public void setSendInstantly(boolean sendInstantly) {
            this.sendInstantly = sendInstantly;
        }

        @ConstructorProperties(value={"displayName", "command", "sendInstantly"})
        public PlayerMenuEntry(String displayName, String command, boolean sendInstantly) {
            this.displayName = displayName;
            this.command = command;
            this.sendInstantly = sendInstantly;
        }
    }
}

