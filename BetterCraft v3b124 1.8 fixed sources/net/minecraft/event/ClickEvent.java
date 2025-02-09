/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.event;

import com.google.common.collect.Maps;
import java.util.Map;

public class ClickEvent {
    private final Action action;
    private final String value;

    public ClickEvent(Action theAction, String theValue) {
        this.action = theAction;
        this.value = theValue;
    }

    public Action getAction() {
        return this.action;
    }

    public String getValue() {
        return this.value;
    }

    public boolean equals(Object p_equals_1_) {
        if (this == p_equals_1_) {
            return true;
        }
        if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass()) {
            ClickEvent clickevent = (ClickEvent)p_equals_1_;
            if (this.action != clickevent.action) {
                return false;
            }
            return !(this.value != null ? !this.value.equals(clickevent.value) : clickevent.value != null);
        }
        return false;
    }

    public String toString() {
        return "ClickEvent{action=" + (Object)((Object)this.action) + ", value='" + this.value + '\'' + '}';
    }

    public int hashCode() {
        int i2 = this.action.hashCode();
        i2 = 31 * i2 + (this.value != null ? this.value.hashCode() : 0);
        return i2;
    }

    public static enum Action {
        OPEN_URL("open_url", true),
        OPEN_FILE("open_file", false),
        RUN_COMMAND("run_command", true),
        TWITCH_USER_INFO("twitch_user_info", false),
        SUGGEST_COMMAND("suggest_command", true),
        CHANGE_PAGE("change_page", true);

        private static final Map<String, Action> nameMapping;
        private final boolean allowedInChat;
        private final String canonicalName;

        static {
            nameMapping = Maps.newHashMap();
            Action[] actionArray = Action.values();
            int n2 = actionArray.length;
            int n3 = 0;
            while (n3 < n2) {
                Action clickevent$action = actionArray[n3];
                nameMapping.put(clickevent$action.getCanonicalName(), clickevent$action);
                ++n3;
            }
        }

        private Action(String canonicalNameIn, boolean allowedInChatIn) {
            this.canonicalName = canonicalNameIn;
            this.allowedInChat = allowedInChatIn;
        }

        public boolean shouldAllowInChat() {
            return this.allowedInChat;
        }

        public String getCanonicalName() {
            return this.canonicalName;
        }

        public static Action getValueByCanonicalName(String canonicalNameIn) {
            return nameMapping.get(canonicalNameIn);
        }
    }
}

