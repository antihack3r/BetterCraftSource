/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.ingamechat.tools.filter;

import java.awt.Color;
import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.List;

public class Filters {
    private List<Filter> filters = new ArrayList<Filter>();

    public List<Filter> getFilters() {
        return this.filters;
    }

    public static class Filter {
        private String filterName;
        private String[] wordsContains;
        private String[] wordsContainsNot;
        private boolean playSound;
        private String soundPath;
        private boolean highlightMessage;
        private short highlightColorR;
        private short highlightColorG;
        private short highlightColorB;
        private boolean hideMessage;
        private boolean displayInSecondChat;
        private boolean filterTooltips;
        private String room;

        public Filter(Filter component) {
            this(component.getFilterName(), component.getWordsContains(), component.getWordsContainsNot(), component.isPlaySound(), component.getSoundPath(), component.isHighlightMessage(), component.getHighlightColorR(), component.getHighlightColorG(), component.getHighlightColorB(), component.isHideMessage(), component.isDisplayInSecondChat(), component.isFilterTooltips(), component.getRoom());
        }

        public Filter clone() {
            return new Filter(this.filterName, this.wordsContains, this.wordsContainsNot, this.playSound, this.soundPath, this.highlightMessage, this.highlightColorR, this.highlightColorG, this.highlightColorB, this.hideMessage, this.displayInSecondChat, this.filterTooltips, this.room);
        }

        public Color getHighlightColor() {
            return new Color(this.getHighlightColorR(), this.getHighlightColorG(), this.getHighlightColorB());
        }

        public String getFilterName() {
            return this.filterName;
        }

        public String[] getWordsContains() {
            return this.wordsContains;
        }

        public String[] getWordsContainsNot() {
            return this.wordsContainsNot;
        }

        public boolean isPlaySound() {
            return this.playSound;
        }

        public String getSoundPath() {
            return this.soundPath;
        }

        public boolean isHighlightMessage() {
            return this.highlightMessage;
        }

        public short getHighlightColorR() {
            return this.highlightColorR;
        }

        public short getHighlightColorG() {
            return this.highlightColorG;
        }

        public short getHighlightColorB() {
            return this.highlightColorB;
        }

        public boolean isHideMessage() {
            return this.hideMessage;
        }

        public boolean isDisplayInSecondChat() {
            return this.displayInSecondChat;
        }

        public boolean isFilterTooltips() {
            return this.filterTooltips;
        }

        public String getRoom() {
            return this.room;
        }

        public void setFilterName(String filterName) {
            this.filterName = filterName;
        }

        public void setWordsContains(String[] wordsContains) {
            this.wordsContains = wordsContains;
        }

        public void setWordsContainsNot(String[] wordsContainsNot) {
            this.wordsContainsNot = wordsContainsNot;
        }

        public void setPlaySound(boolean playSound) {
            this.playSound = playSound;
        }

        public void setSoundPath(String soundPath) {
            this.soundPath = soundPath;
        }

        public void setHighlightMessage(boolean highlightMessage) {
            this.highlightMessage = highlightMessage;
        }

        public void setHighlightColorR(short highlightColorR) {
            this.highlightColorR = highlightColorR;
        }

        public void setHighlightColorG(short highlightColorG) {
            this.highlightColorG = highlightColorG;
        }

        public void setHighlightColorB(short highlightColorB) {
            this.highlightColorB = highlightColorB;
        }

        public void setHideMessage(boolean hideMessage) {
            this.hideMessage = hideMessage;
        }

        public void setDisplayInSecondChat(boolean displayInSecondChat) {
            this.displayInSecondChat = displayInSecondChat;
        }

        public void setFilterTooltips(boolean filterTooltips) {
            this.filterTooltips = filterTooltips;
        }

        public void setRoom(String room) {
            this.room = room;
        }

        @ConstructorProperties(value={"filterName", "wordsContains", "wordsContainsNot", "playSound", "soundPath", "highlightMessage", "highlightColorR", "highlightColorG", "highlightColorB", "hideMessage", "displayInSecondChat", "filterTooltips", "room"})
        public Filter(String filterName, String[] wordsContains, String[] wordsContainsNot, boolean playSound, String soundPath, boolean highlightMessage, short highlightColorR, short highlightColorG, short highlightColorB, boolean hideMessage, boolean displayInSecondChat, boolean filterTooltips, String room) {
            this.filterName = filterName;
            this.wordsContains = wordsContains;
            this.wordsContainsNot = wordsContainsNot;
            this.playSound = playSound;
            this.soundPath = soundPath;
            this.highlightMessage = highlightMessage;
            this.highlightColorR = highlightColorR;
            this.highlightColorG = highlightColorG;
            this.highlightColorB = highlightColorB;
            this.hideMessage = hideMessage;
            this.displayInSecondChat = displayInSecondChat;
            this.filterTooltips = filterTooltips;
            this.room = room;
        }
    }
}

