/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.ingamechat.tools.shortcuts;

import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.List;

public class Shortcuts {
    private List<Shortcut> shortcuts = new ArrayList<Shortcut>();

    public List<Shortcut> getShortcuts() {
        return this.shortcuts;
    }

    public static class Shortcut {
        private String shortcut;
        private String replacement;

        public String getShortcut() {
            return this.shortcut;
        }

        public String getReplacement() {
            return this.replacement;
        }

        public void setShortcut(String shortcut) {
            this.shortcut = shortcut;
        }

        public void setReplacement(String replacement) {
            this.replacement = replacement;
        }

        @ConstructorProperties(value={"shortcut", "replacement"})
        public Shortcut(String shortcut, String replacement) {
            this.shortcut = shortcut;
            this.replacement = replacement;
        }
    }
}

