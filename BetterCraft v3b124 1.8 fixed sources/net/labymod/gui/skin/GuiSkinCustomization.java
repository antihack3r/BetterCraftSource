/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.gui.skin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.labymod.core.LabyModCore;
import net.labymod.gui.skin.SkinCustomizationSettingElement;
import net.labymod.gui.skin.SkinHandSettingElement;
import net.labymod.gui.skin.SkinLayerSettingElement;
import net.labymod.main.LabyMod;
import net.labymod.main.Source;
import net.labymod.main.lang.LanguageManager;
import net.labymod.splash.SplashLoader;
import net.labymod.support.util.Debug;
import net.labymod.user.emote.EmoteLoader;
import net.labymod.user.sticker.StickerLoader;
import net.labymod.utils.Consumer;
import net.labymod.utils.DrawUtils;
import net.labymod.utils.ModColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EnumPlayerModelParts;
import org.lwjgl.input.Mouse;

public class GuiSkinCustomization
extends GuiScreen {
    private GuiScreen lastScreen;
    private long screenCalled = 0L;
    private boolean enabledPreviewDragging = false;
    private boolean currentDragging = false;
    private boolean currentMoving = false;
    private boolean mouseOverPreview = false;
    private double mouseClickedX;
    private double mouseClickedY;
    private double dragPreviewX = 0.0;
    private double dragPreviewY = 0.0;
    private double clickedYaw = 0.0;
    private double xRotationGoal = 0.0;
    private double yRotationGoal = 0.0;
    private long dragStartCalled = 0L;
    private long lastGoalTracking = 0L;
    private double startMoveClickX = 0.0;
    private double startMoveClickY = 0.0;
    private double moveX = 0.0;
    private double moveY = 0.0;
    private double zoomValue = 50.0;
    private List<SkinCustomizationSettingElement> settingElements = new ArrayList<SkinCustomizationSettingElement>();

    public GuiSkinCustomization(GuiScreen lastScreen) {
        this.lastScreen = lastScreen;
        this.screenCalled = System.currentTimeMillis();
    }

    @Override
    public void initGui() {
        int listElementWidth;
        super.initGui();
        this.settingElements.clear();
        this.settingElements.add(new SkinLayerSettingElement(this, null, "hat", EnumPlayerModelParts.HAT));
        SkinLayerSettingElement jacketElement = new SkinLayerSettingElement(this, LanguageManager.translate("skinpart_jacket"), "jacket", EnumPlayerModelParts.JACKET, EnumPlayerModelParts.RIGHT_SLEEVE, EnumPlayerModelParts.LEFT_SLEEVE);
        jacketElement.addSubSetting(new SkinLayerSettingElement(this, null, "right_sleeve", EnumPlayerModelParts.RIGHT_SLEEVE));
        jacketElement.addSubSetting(new SkinLayerSettingElement(this, null, "jacket_base", EnumPlayerModelParts.JACKET));
        jacketElement.addSubSetting(new SkinLayerSettingElement(this, null, "left_sleeve", EnumPlayerModelParts.LEFT_SLEEVE));
        this.settingElements.add(jacketElement);
        SkinLayerSettingElement pantsElement = new SkinLayerSettingElement(this, LanguageManager.translate("skinpart_pants"), "pants", EnumPlayerModelParts.RIGHT_PANTS_LEG, EnumPlayerModelParts.LEFT_PANTS_LEG);
        pantsElement.addSubSetting(new SkinLayerSettingElement(this, null, "right_pants", EnumPlayerModelParts.RIGHT_PANTS_LEG));
        pantsElement.addSubSetting(new SkinLayerSettingElement(this, null, "left_pants", EnumPlayerModelParts.LEFT_PANTS_LEG));
        this.settingElements.add(pantsElement);
        this.settingElements.add(new SkinLayerSettingElement(this, null, "cape", EnumPlayerModelParts.CAPE));
        if (!Source.ABOUT_MC_VERSION.startsWith("1.8")) {
            this.settingElements.add(new SkinHandSettingElement(LanguageManager.translate("skinpart_hand"), "hand"));
        }
        if ((listElementWidth = width / 3) < 130) {
            listElementWidth = 130;
        }
        if (listElementWidth > 200) {
            listElementWidth = 200;
        }
        this.buttonList.add(new GuiButton(5, (LabyMod.getInstance().isInGame() ? listElementWidth / 2 : width / 2 - 10) - listElementWidth / 2 + 10, height - 30, listElementWidth, 20, I18n.format("gui.done", new Object[0])));
        this.buttonList.add(new GuiButton(6, width - 100, 4, 97, 20, LanguageManager.translate("button_refresh_labymod")));
        if (this.getOptifineCapeScreen() != null) {
            this.buttonList.add(new GuiButton(7, width - 100, 26, 97, 20, LanguageManager.translate("optifine_cape")));
        }
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        Minecraft.getMinecraft().gameSettings.saveOptions();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        DrawUtils draw = LabyMod.getInstance().getDrawUtils();
        double elementWidth = width / 3;
        double elementHeight = (double)height / 70.0 * (double)this.settingElements.size();
        if (elementWidth < 130.0) {
            elementWidth = 130.0;
        }
        if (elementHeight < 17.0) {
            elementHeight = 17.0;
        }
        if (elementWidth > 200.0) {
            elementWidth = 200.0;
        }
        if (LabyMod.getInstance().isInGame()) {
            draw.drawOverlayBackground(0, 0, (int)elementWidth + 20, height, 32);
            draw.drawGradientShadowRight(elementWidth + 20.0, 0.0, height);
        }
        int totalHeight = 0;
        for (SkinCustomizationSettingElement element : this.settingElements) {
            totalHeight += (int)elementHeight;
            for (SkinLayerSettingElement subElement : element.getSubSettingElements()) {
                totalHeight += (int)elementHeight;
            }
            totalHeight += 5;
        }
        int posX = (int)(LabyMod.getInstance().isInGame() ? 10.0 : (double)(width / 2) - elementWidth / 2.0);
        int posY = (height - totalHeight) / 2 - 10;
        for (SkinCustomizationSettingElement element2 : this.settingElements) {
            element2.draw(false, posX, posY, elementWidth, elementHeight, mouseX, mouseY);
            posY += (int)elementHeight;
            posY += 2;
            int subIndex = 1;
            for (SkinLayerSettingElement subElement2 : element2.getSubSettingElements()) {
                int lineColor = ModColor.toRGB(150, 150, 150, 155);
                DrawUtils.drawRect((double)(posX + 20 - 10), (double)posY, (double)(posX + 20 - 8), (double)posY + elementHeight / 2.0, lineColor);
                DrawUtils.drawRect((double)(posX + 20 - 10), (double)posY + elementHeight / 2.0, (double)(posX + 20), (double)posY + elementHeight / 2.0 + 2.0, lineColor);
                if (element2.getSubSettingElements().size() != subIndex) {
                    DrawUtils.drawRect((double)(posX + 20 - 10), (double)posY + elementHeight / 2.0 + 2.0, (double)(posX + 20 - 8), (double)posY + elementHeight + 2.0, lineColor);
                }
                subElement2.draw(true, posX + 20, posY, elementWidth - 50.0, elementHeight, mouseX, mouseY);
                posY += (int)(elementHeight + 2.0);
                ++subIndex;
            }
            posY += 2;
        }
        draw.drawCenteredString(I18n.format("options.skinCustomisation.title", new Object[0]), LabyMod.getInstance().isInGame() ? 10.0 + elementWidth / 2.0 : (double)(width / 2), posY - totalHeight - 17);
        double rightSideMiddle = elementWidth + 20.0 + ((double)width - elementWidth + 20.0) / 2.0;
        if (LabyModCore.getMinecraft().getPlayer() != null) {
            int entityPosX = (int)rightSideMiddle - 20;
            int entityPosY = height / 2 + 80;
            double rotationIntroAnimation = this.screenCalled + 200L - System.currentTimeMillis();
            rotationIntroAnimation = rotationIntroAnimation <= 0.0 ? 0.0 : rotationIntroAnimation / 50.0 * rotationIntroAnimation / 50.0;
            double rotationIntroAnimationPointer = (double)(this.screenCalled + 500L - System.currentTimeMillis() + 200L) / 200.0;
            if (this.enabledPreviewDragging) {
                rotationIntroAnimationPointer = (double)(System.currentTimeMillis() - this.dragStartCalled) / 200.0;
            }
            if (rotationIntroAnimationPointer <= 1.0) {
                rotationIntroAnimationPointer = 1.0;
            }
            rotationIntroAnimationPointer *= rotationIntroAnimationPointer;
            double mousePointX = -mouseX + entityPosX;
            double mousePointY = -mouseY + entityPosY - 130;
            if (rotationIntroAnimationPointer > 0.0) {
                mousePointX /= rotationIntroAnimationPointer;
            }
            if (rotationIntroAnimationPointer > 0.0) {
                mousePointY /= rotationIntroAnimationPointer;
            }
            double dragRotationX = 0.0;
            double dragRotationY = 0.0;
            if (this.enabledPreviewDragging) {
                dragRotationX = -this.dragPreviewX;
                dragRotationY = -this.dragPreviewY;
            }
            if (dragRotationY == 0.0) {
                dragRotationY += 1.0;
            }
            DrawUtils.drawEntityOnScreen((int)((double)entityPosX + this.moveX), (int)((double)(entityPosY -= 80) + this.moveY), (int)this.zoomValue, (int)mousePointX, (int)mousePointY, (int)(dragRotationX + rotationIntroAnimation), (int)dragRotationY, 0, LabyModCore.getMinecraft().getPlayer());
        }
        this.mouseOverPreview = (double)mouseX > rightSideMiddle + this.moveX - 60.0 && (double)mouseX < rightSideMiddle + this.moveX + 60.0;
        super.drawScreen(mouseX, mouseY, partialTicks);
        if (this.currentMoving || this.currentDragging) {
            this.mouseClickMove(mouseX, mouseY, 0, 0L);
        }
        if ((this.xRotationGoal != 0.0 || this.yRotationGoal != 0.0) && this.lastGoalTracking < System.currentTimeMillis()) {
            this.lastGoalTracking = System.currentTimeMillis() + 15L;
            int f2 = 10;
            if (this.xRotationGoal > this.dragPreviewX + 10.0) {
                this.dragPreviewX += 10.0;
            }
            if (this.xRotationGoal < this.dragPreviewX - 10.0) {
                this.dragPreviewX -= 10.0;
            }
            if (this.yRotationGoal > this.dragPreviewY + 10.0) {
                this.dragPreviewY += 10.0;
            }
            if (this.yRotationGoal < this.dragPreviewY - 10.0) {
                this.dragPreviewY -= 10.0;
            }
        }
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
    }

    @Override
    protected void actionPerformed(final GuiButton button) throws IOException {
        super.actionPerformed(button);
        switch (button.id) {
            case 5: {
                Minecraft.getMinecraft().displayGuiScreen(this.lastScreen);
                break;
            }
            case 6: {
                button.enabled = false;
                LabyMod.getInstance().getUserManager().getFamiliarManager().clear();
                LabyMod.getInstance().getUserManager().getFamiliarManager().refresh();
                LabyMod.getInstance().getUserManager().clearCache();
                LabyMod.getInstance().getUserManager().init(LabyMod.getInstance().getPlayerUUID(), new Consumer<Boolean>(){

                    @Override
                    public void accept(Boolean success) {
                        LabyMod.getInstance().getUserManager().getCosmeticImageManager().loadPlayersInView();
                        button.enabled = true;
                    }
                });
                LabyMod.getInstance().getUserManager().getGroupManager().load();
                new EmoteLoader(LabyMod.getInstance().getEmoteRegistry()).start();
                new StickerLoader(LabyMod.getInstance().getStickerRegistry()).start();
                SplashLoader.getLoader().load();
                break;
            }
            case 7: {
                GuiScreen screen = this.getOptifineCapeScreen();
                if (screen == null) break;
                Minecraft.getMinecraft().displayGuiScreen(screen);
            }
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (!this.enabledPreviewDragging && this.mouseOverPreview && mouseButton == 0) {
            this.enabledPreviewDragging = true;
            this.dragStartCalled = System.currentTimeMillis();
        }
        if (this.enabledPreviewDragging && this.mouseOverPreview && mouseButton == 0) {
            this.currentDragging = true;
            this.mouseClickedX = (double)mouseX + this.dragPreviewX;
            this.mouseClickedY = (double)(this.clickedYaw > 180.0 ? -mouseY : mouseY) + this.dragPreviewY;
            this.clickedYaw = (this.dragPreviewX + 90.0) % 360.0;
            this.xRotationGoal = 0.0;
            this.yRotationGoal = 0.0;
        }
        if (mouseButton == 2) {
            this.startMoveClickX = -this.moveX + (double)mouseX;
            this.startMoveClickY = -this.moveY + (double)mouseY;
            this.currentMoving = true;
        }
        for (SkinCustomizationSettingElement element : this.settingElements) {
            element.getCheckBox().mouseClicked(mouseX, mouseY, mouseButton);
            for (SkinLayerSettingElement subElement : element.getSubSettingElements()) {
                subElement.getCheckBox().mouseClicked(mouseX, mouseY, mouseButton);
            }
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        if (state == 0) {
            this.currentDragging = false;
            this.clickedYaw = (this.dragPreviewX + 90.0) % 360.0;
        }
        if (state == 2) {
            this.currentMoving = false;
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int mouseScroll = Mouse.getDWheel();
        if (mouseScroll > 0) {
            this.zoomValue += 10.0;
        }
        if (mouseScroll < 0) {
            this.zoomValue -= 10.0;
        }
        if (!Debug.isActive()) {
            if (this.zoomValue < 50.0) {
                this.zoomValue = 50.0;
            }
            int maxZoom = 150;
            if (this.zoomValue > 150.0) {
                this.zoomValue = 150.0;
            }
        }
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
        if (this.currentDragging) {
            this.dragPreviewX = ((double)(-mouseX) + this.mouseClickedX) % 360.0;
            this.dragPreviewY = (double)(this.clickedYaw > 180.0 ? mouseY : -mouseY) + this.mouseClickedY;
            if (!Debug.isActive()) {
                if (this.dragPreviewY > 45.0) {
                    this.dragPreviewY = 45.0;
                }
                if (this.dragPreviewY < -45.0) {
                    this.dragPreviewY = -45.0;
                }
            }
        }
        if (this.currentMoving) {
            this.moveX = -this.startMoveClickX + (double)mouseX;
            this.moveY = -this.startMoveClickY + (double)mouseY;
            if (!Debug.isActive()) {
                if (this.moveX < -150.0) {
                    this.moveX = -150.0;
                }
                if (this.moveX > 150.0) {
                    this.moveX = 150.0;
                }
                if (this.moveY < -150.0) {
                    this.moveY = -150.0;
                }
                if (this.moveY > 150.0) {
                    this.moveY = 150.0;
                }
            }
        }
    }

    public void updatePart(EnumPlayerModelParts part, boolean value) {
        Minecraft.getMinecraft().gameSettings.setModelPartEnabled(part, value);
        if (this.enabledPreviewDragging) {
            if (part == EnumPlayerModelParts.CAPE) {
                this.xRotationGoal = 180.0;
                this.yRotationGoal = 0.0;
            } else if (part == EnumPlayerModelParts.HAT) {
                this.xRotationGoal = 20.0;
                this.yRotationGoal = -20.0;
            } else if (part == EnumPlayerModelParts.RIGHT_PANTS_LEG || part == EnumPlayerModelParts.LEFT_PANTS_LEG) {
                this.xRotationGoal = 20.0;
                this.yRotationGoal = 10.0;
            } else {
                this.xRotationGoal = 20.0;
                this.yRotationGoal = -5.0;
            }
        }
    }

    private GuiScreen getOptifineCapeScreen() {
        try {
            Class<?> optifineClass = Class.forName("net.optifine.gui.GuiScreenCapeOF");
            if (optifineClass != null) {
                return (GuiScreen)optifineClass.getConstructor(GuiScreen.class).newInstance(this);
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        return null;
    }
}

