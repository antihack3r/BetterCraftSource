/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.settings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import net.labymod.settings.elements.SettingsElement;

public class Settings {
    private List<SettingsElement> elements = new ArrayList<SettingsElement>();

    public Settings() {
    }

    public Settings(SettingsElement ... element) {
        this.elements.addAll(Arrays.asList(element));
    }

    public List<SettingsElement> getElements() {
        return this.elements;
    }

    public Settings add(SettingsElement settingsElement) {
        this.elements.add(settingsElement);
        return this;
    }

    public Settings addAll(ArrayList<SettingsElement> settingsElements) {
        this.elements.addAll(settingsElements);
        this.sort();
        return this;
    }

    private void sort() {
        this.elements.sort(Comparator.comparingInt(SettingsElement::getSortingId));
    }
}

