/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.gui;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Objects;
import net.labymod.core.LabyModCore;
import net.labymod.gui.elements.CheckBox;
import net.labymod.gui.elements.Scrollbar;
import net.labymod.main.LabyMod;
import net.labymod.main.ModTextures;
import net.labymod.main.lang.LanguageManager;
import net.labymod.utils.Consumer;
import net.labymod.utils.DrawUtils;
import net.labymod.utils.ModColor;
import net.labymod.utils.ReflectionHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class GuiShaderSelection
extends GuiScreen {
    private static int shaderIndex;
    private final GuiScreen lastScreen;
    private final Scrollbar scrollbar = new Scrollbar(12);
    private final CheckBox checkBox;
    private int hoveredShader = -1;
    private ResourceLocation[] shaderResourceLocations;
    private boolean enabled = false;

    public GuiShaderSelection(GuiScreen lastScreen) {
        this.lastScreen = lastScreen;
        if (LabyMod.getInstance().isInGame()) {
            try {
                Field field = ReflectionHelper.findField(EntityRenderer.class, LabyModCore.getMappingAdapter().getShaderResourceLocationsMappings());
                field.setAccessible(true);
                this.shaderResourceLocations = (ResourceLocation[])field.get(Minecraft.getMinecraft().entityRenderer);
                if (!Minecraft.getMinecraft().entityRenderer.isShaderActive()) {
                    shaderIndex = -1;
                }
                field = ReflectionHelper.findField(EntityRenderer.class, LabyModCore.getMappingAdapter().getUseShaderMappings());
                field.setAccessible(true);
                this.enabled = (Boolean)field.get(Minecraft.getMinecraft().entityRenderer);
            }
            catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        this.checkBox = new CheckBox("Enabled", this.enabled ? CheckBox.EnumCheckBoxValue.ENABLED : CheckBox.EnumCheckBoxValue.DISABLED, null, 0, 0, 20, 20);
        this.checkBox.setUpdateListener(new Consumer<CheckBox.EnumCheckBoxValue>(){

            @Override
            public void accept(CheckBox.EnumCheckBoxValue accepted) {
                Minecraft.getMinecraft().entityRenderer.switchUseShader();
                if (accepted == CheckBox.EnumCheckBoxValue.DISABLED) {
                    LabyMod.getSettings().loadedShader = null;
                    LabyMod.getMainConfig().save();
                }
            }
        });
    }

    public static void loadShader(ResourceLocation resourceLocation) throws Exception {
        String[] methodNames = LabyModCore.getMappingAdapter().getLoadShaderMappings();
        Throwable exception = null;
        String[] array = methodNames;
        int length = array.length;
        int i2 = 0;
        while (i2 < length) {
            String methodName = array[i2];
            try {
                Method method = EntityRenderer.class.getDeclaredMethod(methodName, ResourceLocation.class);
                method.setAccessible(true);
                method.invoke((Object)Minecraft.getMinecraft().entityRenderer, resourceLocation);
                break;
            }
            catch (Exception e2) {
                exception = e2;
                ++i2;
            }
        }
        if (exception != null) {
            exception.printStackTrace();
        }
    }

    private void loadShader(int index) {
        if (index == -1) {
            shaderIndex = -1;
            this.mc.entityRenderer.stopUseShader();
            Objects.requireNonNull(LabyMod.getSettings()).loadedShader = null;
            LabyMod.getMainConfig().save();
            return;
        }
        try {
            ResourceLocation[] shaderResourceLocations = this.shaderResourceLocations;
            shaderIndex = index;
            ResourceLocation resourceLocation = shaderResourceLocations[index];
            Objects.requireNonNull(LabyMod.getSettings()).loadedShader = resourceLocation.getResourcePath();
            LabyMod.getMainConfig().save();
            GuiShaderSelection.loadShader(resourceLocation);
            this.checkBox.setCurrentValue(CheckBox.EnumCheckBoxValue.ENABLED);
        }
        catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    @Override
    public void initGui() {
        super.initGui();
        this.buttonList.add(new GuiButton(3, width / 2 - 100 + 30, height - 26, 170, 20, LanguageManager.translate("button_done")));
        this.scrollbar.setPosition(width / 2 + 100 - 5, 48, width / 2 + 100, height - 45);
        this.checkBox.setX(width / 2 - 100);
        this.checkBox.setY(height - 26);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        DrawUtils draw = LabyMod.getInstance().getDrawUtils();
        draw.drawAutoDimmedBackground(this.scrollbar.getScrollY());
        this.hoveredShader = -2;
        if (LabyMod.getInstance().isInGame() && this.shaderResourceLocations != null) {
            double y2 = 48.0 + this.scrollbar.getScrollY();
            int index = -1;
            this.drawEntry("None " + ModColor.cl('e') + "(Default)", index, y2, mouseX, mouseY);
            y2 += 12.0;
            ++index;
            ResourceLocation[] resourceLocationArray = this.shaderResourceLocations;
            int n2 = this.shaderResourceLocations.length;
            int n3 = 0;
            while (n3 < n2) {
                ResourceLocation resourceLocation = resourceLocationArray[n3];
                String name = resourceLocation.getResourcePath();
                if (name.contains("/")) {
                    String[] path = name.split("/");
                    name = path[path.length - 1];
                    name = name.split("\\.")[0];
                    name = String.valueOf(Character.toUpperCase(name.charAt(0))) + name.substring(1);
                }
                this.drawEntry(name, index, y2, mouseX, mouseY);
                y2 += 12.0;
                ++index;
                ++n3;
            }
            this.scrollbar.update(this.shaderResourceLocations.length + 1);
            this.scrollbar.draw();
        } else {
            draw.drawCenteredString(String.valueOf(ModColor.cl('c')) + LanguageManager.translate("shader_selection_not_ingame"), width / 2, height / 2);
        }
        draw.drawOverlayBackground(0, 41);
        draw.drawOverlayBackground(height - 38, height);
        draw.drawGradientShadowTop(41.0, 0.0, width);
        draw.drawGradientShadowBottom(height - 38, 0.0, width);
        draw.drawCenteredString(LanguageManager.translate("title_shaders"), width / 2, 29.0);
        this.checkBox.drawCheckbox(mouseX, mouseY);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void drawEntry(String name, int index, double y2, int mouseX, int mouseY) {
        DrawUtils draw = LabyMod.getInstance().getDrawUtils();
        if (shaderIndex == index) {
            DrawUtils.drawRect((double)(width / 2 - 102), y2 - 2.0, (double)(width / 2 + 100 - 6), y2 + 10.0, Integer.MIN_VALUE);
            draw.drawRectBorder(width / 2 - 102, y2 - 2.0, width / 2 + 100 - 6, y2 + 10.0, Integer.MAX_VALUE, 1.0);
            if (index == -1) {
                GlStateManager.color(1.0f, 0.5f, 0.5f, 1.0f);
            } else {
                GlStateManager.color(0.5f, 1.0f, 0.5f, 1.0f);
            }
        } else {
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        }
        Minecraft.getMinecraft().getTextureManager().bindTexture(ModTextures.MISC_MENU_POINT);
        draw.drawTexture(width / 2 - 100, y2, 255.0, 255.0, 8.0, 8.0, 1.1f);
        draw.drawString(name, width / 2 - 100 + 10, y2);
        if (mouseX > width / 2 - 100 && mouseX < width / 2 + 100 && (double)mouseY > y2 && (double)mouseY < y2 + 12.0 && mouseY > 41 && mouseY < height - 38) {
            this.hoveredShader = index;
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 3) {
            Minecraft.getMinecraft().displayGuiScreen(this.lastScreen);
        }
        super.actionPerformed(button);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == 1) {
            Minecraft.getMinecraft().displayGuiScreen(this.lastScreen);
            return;
        }
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (this.hoveredShader != -2 && this.hoveredShader != shaderIndex) {
            this.loadShader(this.hoveredShader);
        }
        this.checkBox.mouseClicked(mouseX, mouseY, mouseButton);
        this.scrollbar.mouseAction(mouseX, mouseY, Scrollbar.EnumMouseAction.CLICKED);
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
        this.scrollbar.mouseAction(mouseX, mouseY, Scrollbar.EnumMouseAction.DRAGGING);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        this.scrollbar.mouseAction(mouseX, mouseY, Scrollbar.EnumMouseAction.RELEASED);
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        this.scrollbar.mouseInput();
    }
}

