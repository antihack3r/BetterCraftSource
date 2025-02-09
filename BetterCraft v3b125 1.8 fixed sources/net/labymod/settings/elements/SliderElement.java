/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.settings.elements;

import net.labymod.api.LabyModAddon;
import net.labymod.main.LabyMod;
import net.labymod.main.ModSettings;
import net.labymod.settings.elements.ControlElement;
import net.labymod.utils.Consumer;
import net.labymod.utils.DrawUtils;
import net.labymod.utils.ModColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class SliderElement
extends ControlElement {
    public static final ResourceLocation buttonTextures = new ResourceLocation("textures/gui/widgets.png");
    private Integer currentValue;
    private Consumer<Integer> changeListener;
    private Consumer<Integer> callback;
    private int minValue = 0;
    private int maxValue = 10;
    private boolean dragging;
    private boolean hover;
    private int dragValue;
    private int steps = 1;

    public SliderElement(String displayName, final String configEntryName, ControlElement.IconData iconData) {
        super(displayName, configEntryName, iconData);
        if (!configEntryName.isEmpty()) {
            try {
                this.currentValue = (Integer)ModSettings.class.getDeclaredField(configEntryName).get(LabyMod.getSettings());
            }
            catch (IllegalAccessException e2) {
                e2.printStackTrace();
            }
            catch (NoSuchFieldException e2) {
                e2.printStackTrace();
            }
        }
        if (this.currentValue == null) {
            this.currentValue = this.minValue;
        }
        this.changeListener = new Consumer<Integer>(){

            @Override
            public void accept(Integer accepted) {
                try {
                    ModSettings.class.getDeclaredField(configEntryName).set(LabyMod.getSettings(), accepted);
                }
                catch (Exception e2) {
                    e2.printStackTrace();
                }
                if (SliderElement.this.callback != null) {
                    SliderElement.this.callback.accept(accepted);
                }
            }
        };
    }

    public SliderElement(String displayName, ControlElement.IconData iconData, int currentValue) {
        super(displayName, null, iconData);
        this.currentValue = currentValue;
        this.changeListener = new Consumer<Integer>(){

            @Override
            public void accept(Integer accepted) {
                if (SliderElement.this.callback != null) {
                    SliderElement.this.callback.accept(accepted);
                }
            }
        };
    }

    public SliderElement(String displayName, final LabyModAddon addon, ControlElement.IconData iconData, final String attribute, int currentValue) {
        super(displayName, iconData);
        this.currentValue = currentValue;
        this.changeListener = new Consumer<Integer>(){

            @Override
            public void accept(Integer accepted) {
                addon.getConfig().addProperty(attribute, accepted);
                addon.loadConfig();
                if (SliderElement.this.callback != null) {
                    SliderElement.this.callback.accept(accepted);
                }
            }
        };
    }

    public SliderElement(String configEntryName, ControlElement.IconData iconData) {
        this(configEntryName, configEntryName, iconData);
    }

    public SliderElement setMinValue(int minValue) {
        this.minValue = minValue;
        if (this.currentValue < this.minValue) {
            this.currentValue = this.minValue;
        }
        return this;
    }

    public SliderElement setMaxValue(int maxValue) {
        this.maxValue = maxValue;
        if (this.currentValue > this.maxValue) {
            this.currentValue = this.maxValue;
        }
        return this;
    }

    public SliderElement setRange(int min, int max) {
        this.setMinValue(min);
        this.setMaxValue(max);
        return this;
    }

    public SliderElement setSteps(int steps) {
        this.steps = steps;
        return this;
    }

    @Override
    public void draw(int x2, int y2, int maxX, int maxY, int mouseX, int mouseY) {
        super.draw(x2, y2, maxX, maxY, mouseX, mouseY);
        DrawUtils draw = LabyMod.getInstance().getDrawUtils();
        if (this.displayName != null) {
            draw.drawRectangle(x2 - 1, y2, x2, maxY, ModColor.toRGB(120, 120, 120, 120));
        }
        Minecraft.getMinecraft().getTextureManager().bindTexture(buttonTextures);
        GlStateManager.color(1.0f, 1.0f, 1.0f);
        double maxSliderPos = maxX;
        double totalValueDiff = this.maxValue - this.minValue;
        double currentValue = this.currentValue.intValue();
        boolean bl2 = this.hover = mouseX > x2 && mouseX < maxX && mouseY > y2 + 1 && mouseY < maxY;
        if (!this.isMouseOver()) {
            this.mouseRelease(mouseX, mouseY, 0);
        } else if (this.dragging) {
            this.mouseClickMove(mouseX, mouseY, 0);
        }
    }

    @Override
    public void unfocus(int mouseX, int mouseY, int mouseButton) {
        super.unfocus(mouseX, mouseY, mouseButton);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (this.hover) {
            this.dragging = true;
        }
    }

    @Override
    public void mouseRelease(int mouseX, int mouseY, int mouseButton) {
        super.mouseRelease(mouseX, mouseY, mouseButton);
        if (this.dragging) {
            this.dragging = false;
            this.currentValue = (int)((double)this.dragValue / (double)this.steps) * this.steps;
            if (this.currentValue > this.maxValue) {
                this.currentValue = this.maxValue;
            }
            if (this.currentValue < this.minValue) {
                this.currentValue = this.minValue;
            }
            this.changeListener.accept(this.currentValue);
        }
    }

    @Override
    public void mouseClickMove(int mouseX, int mouseY, int mouseButton) {
        super.mouseClickMove(mouseX, mouseY, mouseButton);
        if (this.dragging) {
            this.currentValue = (int)Math.round((double)this.dragValue / (double)this.steps * (double)this.steps);
            if (this.currentValue > this.maxValue) {
                this.currentValue = this.maxValue;
            }
            if (this.currentValue < this.minValue) {
                this.currentValue = this.minValue;
            }
            this.changeListener.accept(this.currentValue);
        }
    }

    public SliderElement addCallback(Consumer<Integer> callback) {
        this.callback = callback;
        return this;
    }

    public void setCurrentValue(Integer currentValue) {
        this.currentValue = currentValue;
    }
}

