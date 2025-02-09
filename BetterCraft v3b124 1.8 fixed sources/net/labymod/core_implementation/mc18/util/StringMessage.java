/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.core_implementation.mc18.util;

import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

public class StringMessage {
    private static final Map<Character, EnumChatFormatting> formatMap;
    private static final Pattern INCREMENTAL_PATTERN;
    private final List<IChatComponent> list = new ArrayList<IChatComponent>();
    private ChatComponentText currentChatComponent = new ChatComponentText("");
    private ChatStyle modifier = new ChatStyle();
    private final IChatComponent[] output;
    private int currentIndex;
    private final String message;

    static {
        INCREMENTAL_PATTERN = Pattern.compile("(" + String.valueOf('\u00a7') + "[0-9a-fk-or])|(\\n)|((?:(?:https?):\\/\\/)?(?:[-\\w_\\.]{2,}\\.[a-z]{2,4}.*?(?=[\\.\\?!,;:]?(?:[" + String.valueOf('\u00a7') + " \\n]|$))))", 2);
        ImmutableMap.Builder<Character, EnumChatFormatting> builder = ImmutableMap.builder();
        EnumChatFormatting[] enumChatFormattingArray = EnumChatFormatting.values();
        int n2 = enumChatFormattingArray.length;
        int n3 = 0;
        while (n3 < n2) {
            EnumChatFormatting format = enumChatFormattingArray[n3];
            builder.put(Character.valueOf(Character.toLowerCase(format.toString().charAt(1))), format);
            ++n3;
        }
        formatMap = builder.build();
    }

    public StringMessage(String message, boolean keepNewlines) {
        this.message = message;
        if (message == null) {
            this.output = new IChatComponent[]{this.currentChatComponent};
            return;
        }
        this.list.add(this.currentChatComponent);
        Matcher matcher = INCREMENTAL_PATTERN.matcher(message);
        String match = null;
        while (matcher.find()) {
            int groupId = 0;
            while ((match = matcher.group(++groupId)) == null) {
            }
            this.appendNewComponent(matcher.start(groupId));
            block0 : switch (groupId) {
                case 1: {
                    EnumChatFormatting format = formatMap.get(Character.valueOf(match.toLowerCase(Locale.ENGLISH).charAt(1)));
                    if (format == EnumChatFormatting.RESET) {
                        this.modifier = new ChatStyle();
                        break;
                    }
                    if (!format.isFancyStyling()) {
                        this.modifier = new ChatStyle().setColor(format);
                        break;
                    }
                    switch (format) {
                        case BOLD: {
                            this.modifier.setBold(Boolean.TRUE);
                            break block0;
                        }
                        case ITALIC: {
                            this.modifier.setItalic(Boolean.TRUE);
                            break block0;
                        }
                        case STRIKETHROUGH: {
                            this.modifier.setStrikethrough(Boolean.TRUE);
                            break block0;
                        }
                        case UNDERLINE: {
                            this.modifier.setUnderlined(Boolean.TRUE);
                            break block0;
                        }
                        case OBFUSCATED: {
                            this.modifier.setObfuscated(Boolean.TRUE);
                            break block0;
                        }
                    }
                    throw new AssertionError((Object)"Unexpected message format");
                }
                case 2: {
                    if (keepNewlines) {
                        this.currentChatComponent.appendSibling(new ChatComponentText("\n"));
                        break;
                    }
                    this.currentChatComponent = null;
                    break;
                }
                case 3: {
                    if (!match.startsWith("http://") && !match.startsWith("https://")) {
                        match = "http://" + match;
                    }
                    this.modifier.setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, match));
                    this.appendNewComponent(matcher.end(groupId));
                    this.modifier.setChatClickEvent(null);
                }
            }
            this.currentIndex = matcher.end(groupId);
        }
        if (this.currentIndex < message.length()) {
            this.appendNewComponent(message.length());
        }
        this.output = this.list.toArray(new IChatComponent[this.list.size()]);
    }

    private void appendNewComponent(int index) {
        if (index <= this.currentIndex) {
            return;
        }
        IChatComponent addition = new ChatComponentText(this.message.substring(this.currentIndex, index)).setChatStyle(this.modifier);
        this.currentIndex = index;
        this.modifier = this.modifier.createShallowCopy();
        if (this.currentChatComponent == null) {
            this.currentChatComponent = new ChatComponentText("");
            this.list.add(this.currentChatComponent);
        }
        this.currentChatComponent.appendSibling(addition);
    }

    public IChatComponent[] getOutput() {
        return this.output;
    }
}

