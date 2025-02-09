/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viabackwards.utils;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

public class ChatUtil {
    private static final Pattern UNUSED_COLOR_PATTERN = Pattern.compile("(?>(?>\u00a7[0-fk-or])*(\u00a7r|\\Z))|(?>(?>\u00a7[0-f])*(\u00a7[0-f]))");
    private static final Pattern UNUSED_COLOR_PATTERN_PREFIX = Pattern.compile("(?>(?>\u00a7[0-fk-or])*(\u00a7r))|(?>(?>\u00a7[0-f])*(\u00a7[0-f]))");

    public static String removeUnusedColor(String legacy, char defaultColor) {
        return ChatUtil.removeUnusedColor(legacy, defaultColor, false);
    }

    public static String fromLegacy(String legacy, char defaultColor, int limit) {
        return ChatUtil.fromLegacy(legacy, defaultColor, limit, false);
    }

    public static String fromLegacyPrefix(String legacy, char defaultColor, int limit) {
        return ChatUtil.fromLegacy(legacy, defaultColor, limit, true);
    }

    public static String fromLegacy(String legacy, char defaultColor, int limit, boolean isPrefix) {
        if ((legacy = ChatUtil.removeUnusedColor(legacy, defaultColor, isPrefix)).length() > limit) {
            legacy = legacy.substring(0, limit);
        }
        if (legacy.endsWith("\u00a7")) {
            legacy = legacy.substring(0, legacy.length() - 1);
        }
        return legacy;
    }

    public static String removeUnusedColor(String legacy, char defaultColor, boolean isPrefix) {
        if (legacy == null) {
            return null;
        }
        Pattern pattern = isPrefix ? UNUSED_COLOR_PATTERN_PREFIX : UNUSED_COLOR_PATTERN;
        legacy = pattern.matcher(legacy).replaceAll("$1$2");
        StringBuilder builder = new StringBuilder();
        ChatFormattingState builderState = new ChatFormattingState(defaultColor);
        ChatFormattingState lastState = new ChatFormattingState(defaultColor);
        for (int i2 = 0; i2 < legacy.length(); ++i2) {
            char current = legacy.charAt(i2);
            if (current != '\u00a7' || i2 == legacy.length() - 1) {
                if (!lastState.equals(builderState)) {
                    lastState.appendTo(builder);
                    builderState = lastState.copy();
                }
                builder.append(current);
                continue;
            }
            current = legacy.charAt(++i2);
            lastState.processNextControlChar(current);
        }
        if (isPrefix && !lastState.equals(builderState)) {
            lastState.appendTo(builder);
        }
        return builder.toString();
    }

    private static class ChatFormattingState {
        private final Set<Character> formatting;
        private final char defaultColor;
        private char color;

        private ChatFormattingState(char defaultColor) {
            this(new HashSet<Character>(), defaultColor, defaultColor);
        }

        public ChatFormattingState(Set<Character> formatting, char defaultColor, char color) {
            this.formatting = formatting;
            this.defaultColor = defaultColor;
            this.color = color;
        }

        private void setColor(char newColor) {
            this.formatting.clear();
            this.color = newColor;
        }

        public ChatFormattingState copy() {
            return new ChatFormattingState(new HashSet<Character>(this.formatting), this.defaultColor, this.color);
        }

        public void appendTo(StringBuilder builder) {
            builder.append('\u00a7').append(this.color);
            for (Character formatCharacter : this.formatting) {
                builder.append('\u00a7').append(formatCharacter);
            }
        }

        public boolean equals(Object o2) {
            if (this == o2) {
                return true;
            }
            if (o2 == null || this.getClass() != o2.getClass()) {
                return false;
            }
            ChatFormattingState that = (ChatFormattingState)o2;
            return this.defaultColor == that.defaultColor && this.color == that.color && Objects.equals(this.formatting, that.formatting);
        }

        public int hashCode() {
            return Objects.hash(this.formatting, Character.valueOf(this.defaultColor), Character.valueOf(this.color));
        }

        public void processNextControlChar(char controlChar) {
            if (controlChar == 'r') {
                this.setColor(this.defaultColor);
                return;
            }
            if (controlChar == 'l' || controlChar == 'm' || controlChar == 'n' || controlChar == 'o') {
                this.formatting.add(Character.valueOf(controlChar));
                return;
            }
            this.setColor(controlChar);
        }
    }
}

