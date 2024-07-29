/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.ingamechat.tools.filter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import net.labymod.core.ChatComponent;
import net.labymod.core.LabyModCore;
import net.labymod.ingamechat.renderer.ChatLine;
import net.labymod.ingamechat.tools.filter.Filters;
import net.labymod.main.LabyMod;
import net.labymod.utils.ModColor;

public class FilterChatManager {
    private static Map<ChatLine, Filters.Filter> filterResults = new HashMap<ChatLine, Filters.Filter>();

    public static Map<ChatLine, Filters.Filter> getFilterResults() {
        return filterResults;
    }

    public static Filters.Filter getFilterComponent(ChatLine line) {
        if (!LabyMod.getSettings().chatFilter) {
            return null;
        }
        if (filterResults.containsKey(line)) {
            return filterResults.get(line);
        }
        Filters.Filter foundComponent = FilterChatManager.getFilterComponent(LabyModCore.getMinecraft().getChatComponent(line.getComponent()));
        filterResults.put(line, foundComponent);
        return foundComponent;
    }

    public static Filters.Filter getFilterComponent(ChatComponent chatComponent) {
        if (!LabyMod.getSettings().chatFilter) {
            return null;
        }
        String message = ModColor.removeColor(chatComponent.getUnformattedText()).toLowerCase();
        String messageJson = ModColor.removeColor(chatComponent.getJson()).toLowerCase();
        Filters.Filter foundComponent = null;
        for (Filters.Filter component : LabyMod.getInstance().getChatToolManager().getFilters()) {
            boolean contains = false;
            String[] stringArray = component.getWordsContains();
            int n2 = stringArray.length;
            int n3 = 0;
            while (n3 < n2) {
                String containsMsg = stringArray[n3];
                if (message.contains(containsMsg.toLowerCase()) || component.isFilterTooltips() && messageJson.contains(containsMsg.toLowerCase())) {
                    contains = true;
                    break;
                }
                ++n3;
            }
            if (contains) {
                stringArray = component.getWordsContainsNot();
                n2 = stringArray.length;
                n3 = 0;
                while (n3 < n2) {
                    String containsNot = stringArray[n3];
                    if (!containsNot.isEmpty()) {
                        if (message.contains(containsNot.toLowerCase())) {
                            contains = false;
                            break;
                        }
                        if (component.isFilterTooltips() && messageJson.contains(containsNot.toLowerCase())) {
                            contains = false;
                            break;
                        }
                    }
                    ++n3;
                }
            }
            if (!contains) continue;
            if (foundComponent == null) {
                foundComponent = component.clone();
            }
            if (!foundComponent.isDisplayInSecondChat() && component.isDisplayInSecondChat()) {
                foundComponent.setDisplayInSecondChat(true);
            }
            if (!foundComponent.isHideMessage() && component.isHideMessage()) {
                foundComponent.setHideMessage(true);
            }
            if (!foundComponent.isPlaySound() && component.isPlaySound()) {
                foundComponent.setPlaySound(true);
                foundComponent.setSoundPath(component.getSoundPath());
            }
            if (!foundComponent.isHighlightMessage() && component.isHighlightMessage()) {
                foundComponent.setHighlightMessage(true);
                foundComponent.setHighlightColorR(component.getHighlightColorR());
                foundComponent.setHighlightColorG(component.getHighlightColorG());
                foundComponent.setHighlightColorB(component.getHighlightColorB());
            }
            foundComponent.setRoom(component.getRoom());
        }
        return foundComponent;
    }

    public static void unloadMessage(ChatLine line) {
        filterResults.remove(line);
    }

    public static void removeFilterComponent(Filters.Filter component) {
        HashSet<ChatLine> removeLines = new HashSet<ChatLine>();
        for (Map.Entry<ChatLine, Filters.Filter> filterResult : filterResults.entrySet()) {
            if (filterResult.getValue() != component) continue;
            removeLines.add(filterResult.getKey());
        }
        for (ChatLine remove : removeLines) {
            filterResults.remove(remove);
        }
    }
}

