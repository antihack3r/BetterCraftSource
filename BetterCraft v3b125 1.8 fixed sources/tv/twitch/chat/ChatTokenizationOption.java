/*
 * Decompiled with CFR 0.152.
 */
package tv.twitch.chat;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public enum ChatTokenizationOption {
    TTV_CHAT_TOKENIZATION_OPTION_NONE(0),
    TTV_CHAT_TOKENIZATION_OPTION_EMOTICON_URLS(1),
    TTV_CHAT_TOKENIZATION_OPTION_EMOTICON_TEXTURES(2);

    private static Map<Integer, ChatTokenizationOption> s_Map;
    private int m_Value;

    public static ChatTokenizationOption lookupValue(int n2) {
        ChatTokenizationOption chatTokenizationOption = s_Map.get(n2);
        return chatTokenizationOption;
    }

    public static int getNativeValue(HashSet<ChatTokenizationOption> hashSet) {
        if (hashSet == null) {
            return TTV_CHAT_TOKENIZATION_OPTION_NONE.getValue();
        }
        int n2 = TTV_CHAT_TOKENIZATION_OPTION_NONE.getValue();
        for (ChatTokenizationOption chatTokenizationOption : hashSet) {
            if (chatTokenizationOption == null) continue;
            n2 |= chatTokenizationOption.getValue();
        }
        return n2;
    }

    private ChatTokenizationOption(int n3) {
        this.m_Value = n3;
    }

    public int getValue() {
        return this.m_Value;
    }

    static {
        s_Map = new HashMap<Integer, ChatTokenizationOption>();
        EnumSet<ChatTokenizationOption> enumSet = EnumSet.allOf(ChatTokenizationOption.class);
        for (ChatTokenizationOption chatTokenizationOption : enumSet) {
            s_Map.put(chatTokenizationOption.getValue(), chatTokenizationOption);
        }
    }
}

