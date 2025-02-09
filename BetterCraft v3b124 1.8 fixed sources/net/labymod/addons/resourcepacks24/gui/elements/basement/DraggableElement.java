/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.addons.resourcepacks24.gui.elements.basement;

import net.labymod.addons.resourcepacks24.gui.elements.basement.DeletableElement;

public abstract class DraggableElement
extends DeletableElement {
    protected double lastX;
    protected double lastY;
    protected double lastWidth;
    protected double lastHeight;

    public DraggableElement(boolean deletable) {
        super(deletable);
    }

    @Override
    public boolean draw(double x2, double y2, double width, double height, int mouseX, int mouseY) {
        this.lastX = x2;
        this.lastY = y2;
        this.lastWidth = width;
        this.lastHeight = height;
        return super.draw(x2, y2, width, height, mouseX, mouseY);
    }

    public static class Dragging {
        private DraggableElement element;
        private double offsetX;
        private double offsetY;
        private double width;
        private double height;
        private boolean valid;

        public Dragging(DraggableElement element, int mouseX, int mouseY) {
            this.element = element;
            this.offsetX = (double)mouseX - element.lastX;
            this.offsetY = (double)mouseY - element.lastY;
            this.width = element.lastWidth;
            this.height = element.lastHeight;
        }

        public DraggableElement getElement() {
            return this.element;
        }

        public double getOffsetX() {
            return this.offsetX;
        }

        public double getOffsetY() {
            return this.offsetY;
        }

        public double getWidth() {
            return this.width;
        }

        public double getHeight() {
            return this.height;
        }

        public boolean isValid() {
            return this.valid;
        }

        public void setValid(boolean valid) {
            this.valid = valid;
        }
    }
}

