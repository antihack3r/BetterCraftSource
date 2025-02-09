/*
 * Decompiled with CFR 0.152.
 */
package wdl;

import net.minecraft.client.resources.I18n;

public abstract class MessageTypeCategory {
    public final String internalName;
    static final MessageTypeCategory CORE_RECOMMENDED = new I18nableMessageTypeCategory("CORE_RECOMMENDED", "wdl.messages.category.core_recommended");
    static final MessageTypeCategory CORE_DEBUG = new I18nableMessageTypeCategory("CORE_DEBUG", "wdl.messages.category.core_debug");

    public MessageTypeCategory(String internalName) {
        this.internalName = internalName;
    }

    public abstract String getDisplayName();

    public String toString() {
        return "MessageTypeCategory [internalName=" + this.internalName + ", displayName=" + this.getDisplayName() + "]";
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + (this.internalName == null ? 0 : this.internalName.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        MessageTypeCategory other = (MessageTypeCategory)obj;
        return !(this.internalName == null ? other.internalName != null : !this.internalName.equals(other.internalName));
    }

    public static class I18nableMessageTypeCategory
    extends MessageTypeCategory {
        public final String i18nKey;

        public I18nableMessageTypeCategory(String internalName, String i18nKey) {
            super(internalName);
            this.i18nKey = i18nKey;
        }

        @Override
        public String getDisplayName() {
            return I18n.format(this.i18nKey, new Object[0]);
        }
    }
}

