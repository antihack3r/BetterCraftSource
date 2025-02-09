/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.splash.advertisement;

import java.awt.Color;
import java.beans.ConstructorProperties;

public class Advertisement {
    private String title;
    private boolean isNew;
    private boolean visible;
    private String url;
    private Color color;
    private Color colorHover;
    private String iconName;

    public String getTitle() {
        return this.title;
    }

    public boolean isNew() {
        return this.isNew;
    }

    public boolean isVisible() {
        return this.visible;
    }

    public String getUrl() {
        return this.url;
    }

    public Color getColor() {
        return this.color;
    }

    public Color getColorHover() {
        return this.colorHover;
    }

    public String getIconName() {
        return this.iconName;
    }

    @ConstructorProperties(value={"title", "isNew", "visible", "url", "color", "colorHover", "iconName"})
    public Advertisement(String title, boolean isNew, boolean visible, String url, Color color, Color colorHover, String iconName) {
        this.title = title;
        this.isNew = isNew;
        this.visible = visible;
        this.url = url;
        this.color = color;
        this.colorHover = colorHover;
        this.iconName = iconName;
    }
}

