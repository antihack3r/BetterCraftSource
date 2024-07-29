/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.user.gui;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import net.labymod.core.LabyModCore;
import net.labymod.gui.elements.ModTextField;
import net.labymod.main.LabyMod;
import net.labymod.main.ModSettings;
import net.labymod.main.ModTextures;
import net.labymod.main.Source;
import net.labymod.main.lang.LanguageManager;
import net.labymod.user.User;
import net.labymod.user.UserManager;
import net.labymod.user.cosmetic.custom.UserTextureContainer;
import net.labymod.user.cosmetic.custom.handler.StickerImageHandler;
import net.labymod.user.sticker.StickerRegistry;
import net.labymod.user.sticker.data.Sticker;
import net.labymod.user.sticker.data.StickerPack;
import net.labymod.utils.DrawUtils;
import net.labymod.utils.ModColor;
import net.labymod.utils.ReflectionHelper;
import net.labymod.utils.manager.TooltipHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class StickerSelectorGui
extends Gui {
    private static final ResourceLocation POP_SOUND = new ResourceLocation(Source.ABOUT_MC_VERSION.startsWith("1.8") ? "random.pop" : "entity.chicken.egg");
    private static final int ANIMATION_SPEED = 100;
    private static Field fieldPressTime;
    private static Field fieldLeftClickCounter;
    private boolean open = false;
    private long lastOpened;
    private int selectedItemIndex;
    private ModTextField fieldSearch;
    private boolean hoverSearchBar;
    private float lockedYaw = 0.0f;
    private float lockedPitch = 0.0f;
    private boolean prevCrosshairState;
    private short lastHoveredStickerId = (short)-1;
    private int hotkeySelectedSticker = -1;
    private short lastAcceptedHoveredStickerId = (short)-1;
    private long lastHoveredStickerChanged;
    private int hoverStickerIndex = -1;
    private double lastSelectorWidth;
    private double lastSelectorHeight;
    private List<Sticker> filteredStickers;
    private boolean searchOpened = false;
    private List<String> packTitles;
    private int searchMouseX;
    private int searchMouseY;
    private int page = 0;
    private int acceptedPage = 0;
    private int animationState = 0;
    private long pageAnimation = 0L;

    public void open() {
        if (this.open) {
            return;
        }
        EntityPlayerSP player = LabyModCore.getMinecraft().getPlayer();
        if (player == null) {
            return;
        }
        this.open = true;
        this.page = 0;
        this.selectedItemIndex = LabyModCore.getMinecraft().getPlayer().inventory.currentItem;
        this.hotkeySelectedSticker = -1;
        this.lastAcceptedHoveredStickerId = (short)-1;
        this.lockedYaw = player.rotationYaw;
        this.lockedPitch = player.rotationPitch;
        this.fieldSearch = new ModTextField(0, LabyModCore.getMinecraft().getFontRenderer(), 0, 0, 0, 20);
        this.fieldSearch.setBlackBox(false);
        this.fieldSearch.setEnableBackgroundDrawing(false);
        this.searchOpened = false;
        this.prevCrosshairState = LabyMod.getInstance().getLabyModAPI().isCrosshairHidden();
        LabyMod.getInstance().getLabyModAPI().setCrosshairHidden(true);
        if (fieldLeftClickCounter == null) {
            try {
                fieldLeftClickCounter = ReflectionHelper.findField(Minecraft.class, LabyModCore.getMappingAdapter().getLeftClickCounterMappings());
                fieldLeftClickCounter.setAccessible(true);
            }
            catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        if (fieldPressTime == null) {
            try {
                fieldPressTime = ReflectionHelper.findField(KeyBinding.class, LabyModCore.getMappingAdapter().getPressTimeMappings());
                fieldPressTime.setAccessible(true);
            }
            catch (Exception e3) {
                e3.printStackTrace();
            }
        }
        this.filter("");
        if (System.currentTimeMillis() - this.lastOpened < 300L) {
            Minecraft.getMinecraft().displayGuiScreen(new SearchGui(this, this.fieldSearch));
            this.searchOpened = true;
            this.animationState = 0;
        } else {
            this.searchOpened = false;
        }
        this.lastOpened = System.currentTimeMillis();
    }

    public void close() {
        if (!this.open) {
            return;
        }
        if (!this.searchOpened && this.hoverSearchBar) {
            this.searchOpened = true;
            Minecraft.getMinecraft().displayGuiScreen(new SearchGui(this, this.fieldSearch));
            return;
        }
        this.searchOpened = false;
        this.open = false;
        LabyMod.getInstance().getLabyModAPI().setCrosshairHidden(this.prevCrosshairState);
        this.updateScrollLock(false);
        EntityPlayerSP player = LabyModCore.getMinecraft().getPlayer();
        if (player == null) {
            return;
        }
        player.rotationYaw = this.lockedYaw;
        player.rotationPitch = this.lockedPitch;
        if (this.lastHoveredStickerId != -1) {
            LabyModCore.getMinecraft().playSound(POP_SOUND, 1.0f);
            LabyMod.getInstance().getStickerRegistry().playSticker(LabyMod.getInstance().getUserManager().getUser(player.getUniqueID()), this.lastHoveredStickerId);
            ModSettings settings = LabyMod.getSettings();
            try {
                boolean swap = false;
                int i2 = 0;
                while (i2 < settings.stickerHistory.length) {
                    if (settings.stickerHistory[i2] == this.lastHoveredStickerId) {
                        swap = true;
                    }
                    ++i2;
                }
                if (!swap) {
                    settings.stickerHistory[settings.stickerHistory.length - 1] = -1;
                    i2 = settings.stickerHistory.length - 2;
                    while (i2 >= 0) {
                        if (settings.stickerHistory[i2 + 1] == -1) {
                            settings.stickerHistory[i2 + 1] = settings.stickerHistory[i2];
                            settings.stickerHistory[i2] = -1;
                        }
                        --i2;
                    }
                    settings.stickerHistory[0] = this.lastHoveredStickerId;
                    LabyMod.getMainConfig().save();
                }
            }
            catch (Exception error) {
                error.printStackTrace();
                settings.stickerHistory = new short[]{-1, -1, -1, -1, -1};
                LabyMod.getMainConfig().save();
            }
        }
    }

    private void updateScrollLock(boolean locked) {
        GameSettings gameSettings = Minecraft.getMinecraft().gameSettings;
        if (gameSettings == null) {
            return;
        }
        KeyBinding keyBinding = gameSettings.keyBindsHotbar[this.selectedItemIndex];
        if (keyBinding == null) {
            return;
        }
        int keyCode = keyBinding.getKeyCode();
        KeyBinding.setKeyBindState(keyCode, locked);
        if (locked) {
            KeyBinding.onTick(keyCode);
        } else {
            try {
                if (fieldPressTime != null) {
                    fieldPressTime.setInt(keyBinding, 0);
                }
            }
            catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    public void filter(String searchString) {
        EntityPlayerSP player = LabyModCore.getMinecraft().getPlayer();
        User user = LabyMod.getInstance().getUserManager().getUser(player.getUniqueID());
        List<Short> stickerPacks = user.getStickerPacks();
        ArrayList<Sticker> filteredStickers = new ArrayList<Sticker>();
        StickerRegistry stickerRegistry = LabyMod.getInstance().getStickerRegistry();
        if (stickerRegistry.getStickerData() != null && stickerRegistry.getStickerData().getPacks() != null) {
            if (searchString.isEmpty()) {
                ArrayList<String> packTitles = new ArrayList<String>();
                ModSettings settings = LabyMod.getSettings();
                int i2 = 0;
                while (i2 < settings.stickerHistory.length) {
                    short id2 = settings.stickerHistory[i2];
                    if (id2 != -1 || !packTitles.isEmpty()) {
                        filteredStickers.add(id2 == -1 ? null : stickerRegistry.getSticker(id2));
                        if (packTitles.isEmpty()) {
                            packTitles.add(LanguageManager.translate("sticker_history"));
                        }
                    }
                    ++i2;
                }
                StickerPack[] stickerPackArray = stickerRegistry.getStickerData().getPacks();
                int n2 = stickerPackArray.length;
                int n3 = 0;
                while (n3 < n2) {
                    StickerPack stickerPack = stickerPackArray[n3];
                    if (stickerPacks.contains(stickerPack.getId())) {
                        Sticker[] stickerArray = stickerPack.getStickers();
                        int n4 = stickerArray.length;
                        int n5 = 0;
                        while (n5 < n4) {
                            Sticker sticker = stickerArray[n5];
                            filteredStickers.add(sticker);
                            ++n5;
                        }
                        packTitles.add(stickerPack.getName());
                    }
                    ++n3;
                }
                this.packTitles = packTitles;
                this.filteredStickers = filteredStickers;
            } else {
                searchString = searchString.toLowerCase();
                StickerPack[] stickerPackArray = stickerRegistry.getStickerData().getPacks();
                int n6 = stickerPackArray.length;
                int n7 = 0;
                while (n7 < n6) {
                    StickerPack stickerPack2 = stickerPackArray[n7];
                    if (stickerPacks.contains(stickerPack2.getId())) {
                        Sticker sticker2;
                        int n8;
                        int n9;
                        Sticker[] stickerArray;
                        if (stickerPack2.getName().toLowerCase().contains(searchString)) {
                            stickerArray = stickerPack2.getStickers();
                            n9 = stickerArray.length;
                            n8 = 0;
                            while (n8 < n9) {
                                sticker2 = stickerArray[n8];
                                filteredStickers.add(sticker2);
                                ++n8;
                            }
                        } else {
                            stickerArray = stickerPack2.getStickers();
                            n9 = stickerArray.length;
                            n8 = 0;
                            while (n8 < n9) {
                                sticker2 = stickerArray[n8];
                                if (sticker2.getName().toLowerCase().contains(searchString)) {
                                    filteredStickers.add(sticker2);
                                } else {
                                    String[] stringArray = sticker2.getTags();
                                    int n10 = stringArray.length;
                                    int n11 = 0;
                                    while (n11 < n10) {
                                        String tag = stringArray[n11];
                                        if (tag.toLowerCase().contains(searchString)) {
                                            filteredStickers.add(sticker2);
                                        }
                                        ++n11;
                                    }
                                }
                                ++n8;
                            }
                        }
                    }
                    ++n7;
                }
            }
        }
        this.filteredStickers = filteredStickers;
    }

    public void render() {
        long timePassed;
        if (!this.open) {
            return;
        }
        EntityPlayerSP player = LabyModCore.getMinecraft().getPlayer();
        UserManager userManager = LabyMod.getInstance().getUserManager();
        User user = userManager.getUser(player.getUniqueID());
        DrawUtils draw = LabyMod.getInstance().getDrawUtils();
        try {
            if (fieldLeftClickCounter != null) {
                fieldLeftClickCounter.setInt(Minecraft.getMinecraft(), 2);
            }
        }
        catch (Exception e2) {
            e2.printStackTrace();
        }
        if ((timePassed = System.currentTimeMillis() - this.lastOpened) > 2000L) {
            timePassed = 2000L;
        }
        double stickerSize = (double)draw.getHeight() / 8.0 - Math.exp((double)(-timePassed) / 100.0) * 10.0;
        double padding = 2.0;
        double midX = (double)draw.getWidth() / 2.0;
        double midY = (double)draw.getHeight() / 2.0 - (stickerSize / 2.0 + 2.0);
        double lockedX = this.lockedYaw;
        double lockedY = this.lockedPitch;
        double radiusY = this.lastSelectorHeight / 3.0;
        if (lockedY + radiusY > 90.0) {
            lockedY = (float)(90.0 - radiusY);
        }
        if (lockedY - radiusY < -90.0) {
            lockedY = (float)(-90.0 + radiusY);
        }
        double offsetX = lockedX - (double)player.rotationYaw;
        double offsetY = lockedY - (double)player.rotationPitch;
        if (Minecraft.getMinecraft().gameSettings.thirdPersonView == 0) {
            midX += offsetX;
            midY += offsetY;
        }
        int stickerPerPage = 5;
        int totalSticker = this.filteredStickers.size();
        int maxPages = (int)Math.ceil((double)totalSticker / 5.0);
        int stickerIndex = this.page * 5;
        int acceptedStickerIndex = this.acceptedPage * 5;
        double selectorWidth = 5.0 * (stickerSize + 1.0) - 1.0;
        DrawUtils.drawRect(midX - selectorWidth / 2.0 - 2.0, midY - stickerSize / 2.0 - 2.0, midX + selectorWidth / 2.0 + 2.0, midY + stickerSize / 2.0 + 2.0, Integer.MIN_VALUE);
        double cursorX = midX - offsetX * 1.5;
        double cursorY = midY - offsetY * 1.5 + stickerSize / 2.0 + 2.0;
        if (this.searchOpened) {
            cursorX = this.searchMouseX;
            cursorY = this.searchMouseY;
            offsetX = this.searchMouseX - draw.getWidth() / 2;
            offsetY = this.searchMouseY - draw.getHeight() / 2;
        }
        int searchFieldHeight = 10;
        boolean searchAreaGap = true;
        DrawUtils.drawRect(midX - selectorWidth / 2.0 - 2.0, midY + stickerSize / 2.0 + 2.0 + 1.0, midX + selectorWidth / 2.0 + 2.0, midY + stickerSize / 2.0 + 2.0 + 10.0 + 4.0 + 1.0 + 2.0, Integer.MIN_VALUE);
        double fieldX = midX - selectorWidth / 2.0 + 1.0;
        double fieldY = midY + stickerSize / 2.0 + 2.0 + 1.0 + 2.0 + 1.0;
        this.fieldSearch.width = (int)selectorWidth - 2;
        this.fieldSearch.height = 10;
        this.fieldSearch.xPosition = (int)fieldX + 1;
        this.fieldSearch.yPosition = (int)fieldY + 2;
        this.hoverSearchBar = cursorX > fieldX - 1.0 && cursorX < fieldX + selectorWidth - 1.0 && cursorY > fieldY - 1.0 && cursorY < fieldY + (double)this.fieldSearch.height + 1.0;
        DrawUtils.drawRect(fieldX - 1.0, fieldY - 1.0, fieldX + selectorWidth - 1.0, fieldY + (double)this.fieldSearch.height + 1.0, this.hoverSearchBar ? ModColor.toRGB(255, 255, 170, 100) : Integer.MAX_VALUE);
        if (!this.fieldSearch.isFocused()) {
            draw.drawString(LanguageManager.translate("search_textbox_placeholder"), fieldX + 1.0, fieldY + 1.0);
        }
        this.drawStickerRow(midX, midY, cursorX, cursorY, 5, totalSticker, stickerIndex, stickerSize, selectorWidth, user, this.filteredStickers, 0);
        this.drawStickerRow(midX, midY, cursorX, cursorY, 5, totalSticker, stickerIndex, stickerSize, selectorWidth, user, this.filteredStickers, -1);
        this.drawStickerRow(midX, midY, cursorX, cursorY, 5, totalSticker, acceptedStickerIndex, stickerSize, selectorWidth, user, this.filteredStickers, 1);
        double pageDisplayX = midX + selectorWidth / 2.0 + 2.0 + 1.0;
        double pageDisplayY = midY - stickerSize / 2.0 - 2.0;
        int i2 = 0;
        while (i2 < maxPages) {
            boolean selected = this.page == i2;
            GlStateManager.pushMatrix();
            GlStateManager.enableAlpha();
            GL11.glColor4f(0.0f, 1.0f, 0.0f, 1.0f);
            Minecraft.getMinecraft().getTextureManager().bindTexture(ModTextures.MISC_MENU_POINT);
            draw.drawTexture(pageDisplayX, pageDisplayY, 255.0, 255.0, 3.0, 3.0, selected ? 1.1f : 0.2f);
            GlStateManager.color(1.0f, 1.0f, 1.0f);
            GlStateManager.popMatrix();
            pageDisplayY += 4.0;
            if (pageDisplayY > midY + stickerSize / 2.0 + 2.0) {
                pageDisplayY = midY - stickerSize / 2.0 - 2.0;
                pageDisplayX += 4.0;
            }
            ++i2;
        }
        String title = this.packTitles == null || this.packTitles.isEmpty() ? LanguageManager.translate("sticker_title") : this.packTitles.get(this.page);
        double titleWidth = draw.getStringWidth(title);
        double titleOffsetY = -15.0;
        double titlePadding = 3.0;
        DrawUtils.drawRect(midX - titleWidth / 2.0 - 3.0, midY - stickerSize / 2.0 + -15.0 - 3.0, midX + titleWidth / 2.0 + 3.0, midY - stickerSize / 2.0 + -15.0 + 7.0 + 3.0, Integer.MIN_VALUE);
        draw.drawCenteredString(title, midX, midY - stickerSize / 2.0 + -15.0);
        if (this.filteredStickers.isEmpty() && !this.searchOpened) {
            double statusY = midY + stickerSize / 2.0 + 10.0 + 1.0 + 10.0;
            draw.drawCenteredString(LanguageManager.translate("sticker_status_no_stickers"), midX, statusY, 0.8);
        } else if (maxPages > 1 && this.page == 0) {
            double statusY = midY + stickerSize / 2.0 + 10.0 + 1.0 + 10.0;
            draw.drawCenteredString(String.valueOf(ModColor.cl('7')) + LanguageManager.translate("sticker_info_scroll"), midX, statusY, 0.8);
        }
        if (offsetX == 0.0 && offsetY == 0.0) {
            cursorX = (int)cursorX;
            cursorY = (int)cursorY;
        }
        if (!this.searchOpened) {
            DrawUtils.drawRect(cursorX, cursorY - 4.0, cursorX + 1.0, cursorY + 5.0, Integer.MAX_VALUE);
            DrawUtils.drawRect(cursorX - 4.0, cursorY, cursorX + 5.0, cursorY + 1.0, Integer.MAX_VALUE);
        }
        this.lastSelectorWidth = selectorWidth + 4.0;
        this.lastSelectorHeight = 2.0 + stickerSize + 2.0 + 1.0 + 2.0 + 10.0 + 2.0;
        this.handleMouseInput(maxPages - 1);
    }

    private void handleMouseInput(int maxPages) {
        boolean moveDown;
        double scroll = Mouse.getDWheel();
        boolean moveUp = scroll > 0.0;
        boolean bl2 = moveDown = scroll < 0.0;
        if (!(this.hoverStickerIndex != -1 || !moveUp && !moveDown || moveDown && this.acceptedPage == maxPages && this.hotkeySelectedSticker == 0 || moveUp && this.acceptedPage == 0 && this.hotkeySelectedSticker == 4)) {
            int value = this.hotkeySelectedSticker + (moveUp ? 1 : -1);
            int n2 = this.hotkeySelectedSticker = value < 0 ? 4 : value % 5;
            if (!(!moveUp && value == -1 || moveUp && value == 5)) {
                moveDown = false;
                moveUp = false;
            }
        }
        try {
            int stickerPerPage = 5;
            int i2 = 0;
            while (i2 < stickerPerPage) {
                int code = Minecraft.getMinecraft().gameSettings.keyBindsHotbar[i2].getKeyCode();
                if (code >= 0 && Keyboard.isKeyDown(code)) {
                    this.hotkeySelectedSticker = stickerPerPage - 1 - i2;
                }
                ++i2;
            }
        }
        catch (Exception error) {
            error.printStackTrace();
        }
        if (Mouse.isButtonDown(0)) {
            moveUp = true;
        }
        if (Mouse.isButtonDown(1)) {
            moveDown = true;
        }
        if (this.acceptedPage == this.page && this.animationState == 0) {
            if (moveUp && this.page > 0) {
                --this.page;
                this.animationState = -1;
                this.pageAnimation = System.currentTimeMillis();
            }
            if (moveDown && this.page < maxPages) {
                ++this.page;
                this.animationState = 1;
                this.pageAnimation = System.currentTimeMillis();
            }
        } else if (!moveUp && !moveDown && this.animationState != -1 && this.animationState != 1) {
            this.acceptedPage = this.page;
        }
        if ((this.animationState == -1 || this.animationState == 1) && this.pageAnimation + 100L < System.currentTimeMillis()) {
            this.animationState *= 2;
        }
        if ((this.animationState == -2 || this.animationState == 2) && this.pageAnimation + 100L < System.currentTimeMillis()) {
            this.animationState = 0;
        }
    }

    private void drawStickerRow(double midX, double midY, double cursorX, double cursorY, int stickerPerPage, int totalSticker, int stickerIndex, double stickerSize, double selectorWidth, User user, List<Sticker> stickers, int animationChannel) {
        DrawUtils draw = LabyMod.getInstance().getDrawUtils();
        long timePassed = System.currentTimeMillis() - this.pageAnimation;
        double percentage = 0.01 * (double)timePassed;
        double animation = this.animationState == 0 ? 1.0 : Math.min(percentage, 1.0);
        boolean increased = this.page < this.acceptedPage;
        boolean lowBound = animationChannel == (increased ? 1 : -1);
        this.lastHoveredStickerId = (short)-1;
        this.hoverStickerIndex = -1;
        if (animationChannel == 1) {
            animation = 1.0 - animation;
        }
        double lowOffset = (stickerSize - 2.0) * (1.0 - animation);
        int index = stickerPerPage - 1;
        while (index >= 0) {
            Short id2;
            double x2 = midX - selectorWidth / 2.0 + (double)(stickerPerPage - index - 1) * (stickerSize + 1.0);
            double y2 = midY - stickerSize / 2.0;
            boolean hover = cursorX > x2 && cursorX < x2 + stickerSize && cursorY > y2 && cursorY < y2 + stickerSize;
            Sticker sticker = stickerIndex >= totalSticker ? null : stickers.get(stickerIndex);
            Short s2 = id2 = sticker == null ? null : Short.valueOf(sticker.getId());
            if (hover) {
                this.hoverStickerIndex = index;
            }
            if (hover && id2 != null) {
                this.hotkeySelectedSticker = -1;
            }
            if (this.hotkeySelectedSticker != -1 && this.hotkeySelectedSticker == index) {
                hover = true;
            }
            double brightness = 0.0;
            if (hover && id2 != null) {
                brightness = 185.0 + 185.0 * -Math.exp((double)(-(System.currentTimeMillis() - this.lastHoveredStickerChanged)) / 500.0);
            }
            if (animationChannel == 0) {
                DrawUtils.drawRect(x2, y2, x2 + stickerSize, y2 + stickerSize, ModColor.toRGB((int)brightness, (int)brightness, (int)brightness, 50));
            } else {
                double currentStickerSize = stickerSize - 2.0;
                double hoverOffset = 0.0;
                if (hover && id2 != null) {
                    this.lastHoveredStickerId = id2;
                    if (this.lastAcceptedHoveredStickerId != id2) {
                        this.lastAcceptedHoveredStickerId = id2;
                        this.lastHoveredStickerChanged = System.currentTimeMillis();
                    }
                }
                if (hover) {
                    double popUp = 10.0 + 10.0 * -Math.exp((double)(-(System.currentTimeMillis() - this.lastHoveredStickerChanged)) / 100.0);
                    hoverOffset = popUp / 2.0;
                    currentStickerSize += popUp;
                }
                GlStateManager.pushMatrix();
                GlStateManager.scale(1.0, animation, 1.0);
                this.drawSticker(x2 + 1.0 - hoverOffset, (y2 + 1.0 - hoverOffset + (lowBound ? lowOffset : 0.0)) / animation, currentStickerSize, id2, user);
                GlStateManager.popMatrix();
                ++stickerIndex;
            }
            --index;
        }
    }

    private void drawSticker(double x2, double y2, double size, Short stickerId, User user) {
        UserTextureContainer container;
        UserManager userManager = LabyMod.getInstance().getUserManager();
        StickerImageHandler stickerHandler = userManager.getCosmeticImageManager().getStickerImageHandler();
        if (stickerHandler != null && stickerId != null && stickerId != -1 && (container = stickerHandler.getContainer(user, stickerId)) != null) {
            container.validateTexture(stickerHandler);
            ResourceLocation resourceLocation = stickerHandler.getResourceLocations().get(container.getFileName());
            if (resourceLocation != null) {
                double scale = size / 20.0;
                double textureSizeX = 115.90909090909092;
                double textureSizeY = 231.81818181818184;
                double textureOffsetX = 11.590909090909092;
                double textureOffsetY = 23.181818181818183;
                GlStateManager.pushMatrix();
                GlStateManager.color(1.0f, 1.1f, 1.0f, 1.0f);
                GlStateManager.scale(scale, scale, 1.0);
                Minecraft.getMinecraft().getTextureManager().bindTexture(resourceLocation);
                LabyMod.getInstance().getDrawUtils().drawTexture(x2 / scale, y2 / scale, 11.590909090909092, 23.181818181818183, 115.90909090909092, 231.81818181818184, 20.0, 20.0);
                GlStateManager.popMatrix();
            }
        }
    }

    public void pointSearchMouse(int mouseX, int mouseY) {
        Sticker sticker;
        this.searchMouseX = mouseX;
        this.searchMouseY = mouseY;
        if (this.lastHoveredStickerId != -1 && (sticker = LabyMod.getInstance().getStickerRegistry().getSticker(this.lastHoveredStickerId)) != null) {
            TooltipHelper.getHelper().pointTooltip(mouseX, mouseY, sticker.getName());
        }
    }

    public void lockMouseMovementInCircle() {
        double multiplier;
        if (!this.open) {
            return;
        }
        EntityPlayerSP player = LabyModCore.getMinecraft().getPlayer();
        if (player == null) {
            return;
        }
        double radiusX = this.lastSelectorWidth / 3.0;
        double radiusY = this.lastSelectorHeight / 3.0;
        float centerX = this.lockedYaw;
        float centerY = this.lockedPitch + 5.0f - 16.0f;
        if ((double)centerY + radiusY > 90.0) {
            centerY = (float)(90.0 - radiusY);
        }
        if ((double)centerY - radiusY < -90.0) {
            centerY = (float)(-90.0 + radiusY);
        }
        float newX = player.rotationYaw;
        float newY = player.rotationPitch;
        double distanceX = Math.abs(centerX - newX);
        double distanceY = Math.abs(centerY - newY);
        if (distanceX > radiusX) {
            double fromOriginToObjectX = newX - centerX;
            multiplier = radiusX / distanceX;
            player.rotationYaw = centerX += (float)(fromOriginToObjectX *= multiplier);
            player.prevRotationYaw = centerX;
        }
        if (distanceY > radiusY) {
            double fromOriginToObjectY = newY - centerY;
            multiplier = radiusY / distanceY;
            player.rotationPitch = centerY += (float)(fromOriginToObjectY *= multiplier);
            player.prevRotationPitch = centerY;
        }
        this.updateScrollLock(true);
    }

    public boolean isOpen() {
        return this.open;
    }

    public static class SearchGui
    extends GuiScreen {
        private StickerSelectorGui stickerSelectorGui;
        private ModTextField textField;

        public SearchGui(StickerSelectorGui stickerSelectorGui, ModTextField textField) {
            this.stickerSelectorGui = stickerSelectorGui;
            this.textField = textField;
        }

        @Override
        public void initGui() {
            super.initGui();
            this.textField.setFocused(true);
        }

        @Override
        public void onGuiClosed() {
            super.onGuiClosed();
            this.stickerSelectorGui.close();
        }

        @Override
        public void drawScreen(int mouseX, int mouseY, float partialTicks) {
            super.drawScreen(mouseX, mouseY, partialTicks);
            this.stickerSelectorGui.pointSearchMouse(mouseX, mouseY);
            this.textField.drawTextBox();
        }

        @Override
        public void updateScreen() {
            super.updateScreen();
            this.textField.updateCursorCounter();
        }

        @Override
        protected void keyTyped(char typedChar, int keyCode) throws IOException {
            super.keyTyped(typedChar, keyCode);
            if (this.textField.textboxKeyTyped(typedChar, keyCode)) {
                this.stickerSelectorGui.filter(this.textField.getText());
            }
        }

        @Override
        public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
            super.mouseClicked(mouseX, mouseY, mouseButton);
            if (this.textField.mouseClicked(mouseX, mouseY, mouseButton)) {
                return;
            }
            this.stickerSelectorGui.pointSearchMouse(mouseX, mouseY);
            Minecraft.getMinecraft().displayGuiScreen(null);
            this.stickerSelectorGui.close();
        }

        @Override
        protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
            super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
            this.stickerSelectorGui.pointSearchMouse(mouseX, mouseY);
        }
    }
}

