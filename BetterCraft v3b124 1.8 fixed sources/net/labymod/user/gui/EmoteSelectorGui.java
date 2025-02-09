/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.user.gui;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.labymod.core.LabyModCore;
import net.labymod.gui.elements.ModTextField;
import net.labymod.main.LabyMod;
import net.labymod.main.ModTextures;
import net.labymod.splash.SplashLoader;
import net.labymod.splash.dailyemotes.DailyEmote;
import net.labymod.user.User;
import net.labymod.user.UserManager;
import net.labymod.user.emote.EmoteRegistry;
import net.labymod.user.emote.EmoteRenderer;
import net.labymod.user.emote.keys.EmoteKeyFrame;
import net.labymod.user.emote.keys.EmotePose;
import net.labymod.user.emote.keys.provider.KeyFrameStorage;
import net.labymod.utils.DrawUtils;
import net.labymod.utils.ModColor;
import net.labymod.utils.ReflectionHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class EmoteSelectorGui
extends Gui {
    private static final int ANIMATION_SPEED = 100;
    private static Field fieldPressTime;
    private static Field fieldLeftClickCounter;
    private static long emoteCooldownEnd;
    private long lastOpened;
    private int selectedItemIndex;
    public boolean open = false;
    private float lockedYaw = 0.0f;
    private float lockedPitch = 0.0f;
    private boolean prevCrosshairState;
    private short lastHoveredEmoteId = (short)-1;
    private boolean emotesLocked = false;
    private boolean emotesOnCooldown = false;
    private int page = 0;
    private int acceptedPage = 0;
    private int animationState = 0;
    private long pageAnimation = 0L;
    private int scrollSelectedEmote = -1;
    private List<Short> filteredEmotes;
    private boolean searchOpened = false;
    private boolean dailyEmotes = false;
    private int searchMouseX;
    private int searchMouseY;

    static {
        emoteCooldownEnd = 0L;
    }

    public void open() {
        if (this.open || Minecraft.getMinecraft().gameSettings.hideGUI) {
            return;
        }
        EntityPlayerSP player = LabyModCore.getMinecraft().getPlayer();
        if (player == null) {
            return;
        }
        if (fieldLeftClickCounter == null) {
            try {
                fieldLeftClickCounter = ReflectionHelper.findField(Minecraft.class, LabyModCore.getMappingAdapter().getLeftClickCounterMappings());
                fieldLeftClickCounter.setAccessible(true);
            }
            catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        this.page = 0;
        this.open = true;
        this.selectedItemIndex = LabyModCore.getMinecraft().getPlayer().inventory.currentItem;
        this.scrollSelectedEmote = -1;
        this.lockedYaw = player.rotationYaw;
        this.lockedPitch = player.rotationPitch;
        this.prevCrosshairState = LabyMod.getInstance().getLabyModAPI().isCrosshairHidden();
        LabyMod.getInstance().getLabyModAPI().setCrosshairHidden(true);
        if (fieldPressTime == null) {
            try {
                fieldPressTime = ReflectionHelper.findField(KeyBinding.class, LabyModCore.getMappingAdapter().getPressTimeMappings());
                fieldPressTime.setAccessible(true);
            }
            catch (Exception e3) {
                e3.printStackTrace();
            }
        }
        this.emotesOnCooldown = LabyMod.getInstance().isServerHasEmoteSpamProtection();
        this.dailyEmotes = this.hasDailyEmotes(player);
        if (this.isEmotePlaying(player)) {
            this.emotesLocked = true;
        }
        UserManager userManager = LabyMod.getInstance().getUserManager();
        if (System.currentTimeMillis() - this.lastOpened < 300L && userManager.getGroupManager().hasPermissionOf(userManager.getUser(player.getUniqueID()), (short)10)) {
            Minecraft.getMinecraft().displayGuiScreen(new SearchGui(this));
            this.searchOpened = true;
            this.animationState = 0;
        } else {
            this.searchOpened = false;
        }
        this.lastOpened = System.currentTimeMillis();
        this.filter("");
    }

    public void close() {
        if (!this.open) {
            return;
        }
        this.open = false;
        LabyMod.getInstance().getLabyModAPI().setCrosshairHidden(this.prevCrosshairState);
        this.updateScrollLock(false);
        EntityPlayerSP player = LabyModCore.getMinecraft().getPlayer();
        if (player == null) {
            return;
        }
        player.rotationYaw = this.lockedYaw;
        player.rotationPitch = this.lockedPitch;
        if (this.lastHoveredEmoteId != -1) {
            emoteCooldownEnd = System.currentTimeMillis() + 5000L;
            LabyMod.getInstance().getEmoteRegistry().playEmote(this.lastHoveredEmoteId);
        }
    }

    public void pointSearchMouse(int mouseX, int mouseY) {
        this.searchMouseX = mouseX;
        this.searchMouseY = mouseY;
    }

    public void filter(String searchString) {
        EntityPlayerSP player = LabyModCore.getMinecraft().getPlayer();
        User user = LabyMod.getInstance().getUserManager().getUser(player.getUniqueID());
        if (searchString.isEmpty()) {
            this.filteredEmotes = user.getEmotes();
        } else {
            searchString = searchString.toLowerCase();
            Map<Short, KeyFrameStorage> sources = LabyMod.getInstance().getEmoteRegistry().getEmoteSources();
            ArrayList<Short> filteredEmotes = new ArrayList<Short>();
            for (Short id2 : user.getEmotes()) {
                KeyFrameStorage storage = sources.get(id2);
                if (storage == null || !storage.getName().toLowerCase().contains(searchString)) continue;
                filteredEmotes.add(id2);
            }
            this.filteredEmotes = filteredEmotes;
        }
    }

    private boolean hasDailyEmotes(EntityPlayerSP player) {
        UserManager userManager = LabyMod.getInstance().getUserManager();
        User user = userManager.getUser(player.getUniqueID());
        if (SplashLoader.getLoader() == null || SplashLoader.getLoader().getEntries() == null) {
            return false;
        }
        DailyEmote[] dailyEmotes = SplashLoader.getLoader().getEntries().getDailyEmotes();
        return dailyEmotes != null && dailyEmotes.length != 0 && user.isDailyEmoteFlat();
    }

    private boolean isEmotePlaying(EntityPlayerSP player) {
        EmoteRenderer renderer = LabyMod.getInstance().getEmoteRegistry().getEmoteRendererFor(player);
        return renderer != null && renderer.isVisible() && !renderer.isStream();
    }

    public void render() {
        long timePassed;
        if (!this.open) {
            return;
        }
        DrawUtils draw = LabyMod.getInstance().getDrawUtils();
        EntityPlayerSP player = LabyModCore.getMinecraft().getPlayer();
        if (player == null || player.getUniqueID() == null || player.hurtTime != 0) {
            this.close();
            return;
        }
        try {
            if (fieldLeftClickCounter != null) {
                fieldLeftClickCounter.setInt(Minecraft.getMinecraft(), 2);
            }
        }
        catch (Exception e2) {
            e2.printStackTrace();
        }
        if (this.emotesLocked && !this.isEmotePlaying(player)) {
            this.emotesLocked = false;
        }
        double radiusMouseBorder = (double)draw.getHeight() / 4.0 / 3.0;
        double midX = (double)draw.getWidth() / 2.0;
        double midY = (double)draw.getHeight() / 2.0;
        double lockedX = this.lockedYaw;
        double lockedY = this.lockedPitch;
        if (lockedY + radiusMouseBorder > 90.0) {
            lockedY = (float)(90.0 - radiusMouseBorder);
        }
        if (lockedY - radiusMouseBorder < -90.0) {
            lockedY = (float)(-90.0 + radiusMouseBorder);
        }
        if ((timePassed = System.currentTimeMillis() - this.lastOpened) > 2000L) {
            timePassed = 2000L;
        }
        double radius = (double)draw.getHeight() / 4.0 - Math.exp((double)(-timePassed) / 100.0) * 10.0;
        double offsetX = lockedX - (double)player.rotationYaw;
        double offsetY = lockedY - (double)player.rotationPitch;
        double distance = Math.sqrt(offsetX * offsetX + offsetY * offsetY);
        double cursorX = midX - offsetX * 1.5;
        double cursorY = midY - offsetY * 1.5;
        if (this.searchOpened) {
            cursorX = this.searchMouseX;
            cursorY = this.searchMouseY;
            offsetX = this.searchMouseX - draw.getWidth() / 2;
            offsetY = this.searchMouseY - draw.getHeight() / 2;
            distance = Math.sqrt(offsetX * offsetX + offsetY * offsetY);
        }
        GlStateManager.pushMatrix();
        if (Minecraft.getMinecraft().gameSettings.thirdPersonView == 0 && !this.searchOpened) {
            midX += offsetX;
            midY += offsetY;
        }
        int totalEmotes = this.filteredEmotes.size();
        int amount = 6;
        int emoteIndex = this.page * 6;
        int maxPages = (int)Math.ceil((double)totalEmotes / 6.0);
        boolean cooldown = this.emotesOnCooldown && emoteCooldownEnd > System.currentTimeMillis();
        boolean emotesEnabled = LabyMod.getSettings().emotes;
        String localeKey = emotesEnabled ? (this.emotesLocked ? "emote_status_already_playing" : (totalEmotes == 0 ? (this.searchOpened ? "emote_status_not_found" : "emote_status_no_emotes") : (cooldown ? "emote_status_cooldown" : "emote_status_select"))) : "emote_status_disabled";
        String title = String.valueOf(this.emotesLocked || !emotesEnabled ? ModColor.cl('c') : "") + LabyMod.getMessage(localeKey, new Object[0]);
        draw.drawCenteredString(title, midX, midY - radius - 5.0);
        if (this.page == -1) {
            draw.drawCenteredString(String.valueOf(ModColor.cl('b')) + ModColor.cl('o') + "labymod.net/shop", midX, midY + radius + 6.0);
            draw.drawCenteredString(String.valueOf(ModColor.cl('6')) + LabyMod.getMessage("emote_daily", new Object[0]), midX, midY + radius - 5.0, 0.7);
        } else {
            if (totalEmotes == 0) {
                draw.drawCenteredString(String.valueOf(ModColor.cl('b')) + ModColor.cl('o') + "labymod.net/shop", midX, midY + radius - 5.0);
            } else if (maxPages > 1 && !this.searchOpened) {
                String keyName = "?";
                try {
                    keyName = Keyboard.getKeyName(LabyMod.getSettings().keyEmote).toLowerCase();
                }
                catch (Exception exception) {
                    // empty catch block
                }
                draw.drawCenteredString(LabyMod.getMessage("emote_selector_page", this.page + 1, maxPages), midX, midY + radius - 5.0, 0.7);
                draw.drawCenteredString(String.valueOf(ModColor.cl('7')) + LabyMod.getMessage("emote_doubletap", keyName), midX, midY + radius + 6.0, 0.7);
            }
            if (maxPages == 1 && !this.searchOpened && this.dailyEmotes) {
                draw.drawCenteredString(LabyMod.getMessage("emote_own", new Object[0]), midX, midY + radius - 5.0, 0.7);
            }
        }
        if (!this.searchOpened) {
            double scale;
            double arrowWidth = 9.0;
            double arrowHeight = 6.0;
            double arrowAnimation = Math.abs(Math.sin((double)(System.currentTimeMillis() - this.pageAnimation) / (Math.PI * 9)) / 2.0);
            if (this.acceptedPage > (this.dailyEmotes ? -1 : 0)) {
                if (this.acceptedPage <= 0) {
                    GL11.glColor4f(1.0f, 0.7f, 0.0f, 1.0f);
                } else {
                    GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                }
                scale = this.animationState == -1 ? arrowAnimation + 1.0 : 1.0;
                Minecraft.getMinecraft().getTextureManager().bindTexture(ModTextures.MISC_ARROW);
                draw.drawTexture(midX - 9.0 * scale / 2.0 - radius / 2.0, midY + radius - 2.0 - 6.0 * scale / 2.0, 0.0, 0.0, 127.5, 255.0, 9.0 * scale, 6.0 * scale, 1.1f);
                Minecraft.getMinecraft().getTextureManager().bindTexture(ModTextures.MISC_MOUSE);
                draw.drawTexture(midX - 9.0 * scale / 2.0 - radius / 2.0 + 10.0 + 2.0, midY + radius - 4.0 - 6.0 * scale / 2.0, 127.0, 0.0, 127.0, 255.0, 7.0 * scale, 10.0 * scale, 1.1f);
            }
            if (this.acceptedPage < maxPages - 1) {
                scale = this.animationState == 1 ? arrowAnimation + 1.0 : 1.0;
                GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                Minecraft.getMinecraft().getTextureManager().bindTexture(ModTextures.MISC_ARROW);
                draw.drawTexture(midX - 9.0 * scale / 2.0 + radius / 2.0, midY + radius - 2.0 - 6.0 * scale / 2.0, 127.5, 0.0, 127.5, 255.0, 9.0 * scale, 6.0 * scale, 1.1f);
                Minecraft.getMinecraft().getTextureManager().bindTexture(ModTextures.MISC_MOUSE);
                draw.drawTexture(midX - 9.0 * scale / 2.0 + radius / 2.0 - 10.0, midY + radius - 4.0 - 6.0 * scale / 2.0, 0.0, 0.0, 127.0, 255.0, 7.0 * scale, 10.0 * scale, 1.1f);
            }
        }
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        this.lastHoveredEmoteId = (short)-1;
        if (this.animationState != 0) {
            double animation = (double)(System.currentTimeMillis() - this.pageAnimation) * 0.01 * radius;
            double speed = 1.0 * radius;
            midX = this.animationState == 1 || this.animationState == -1 ? (this.animationState == -1 ? (midX += animation) : (midX -= animation)) : (this.animationState == 2 ? (midX += speed - animation) : (midX -= speed - animation));
        }
        Map<Short, KeyFrameStorage> sources = LabyMod.getInstance().getEmoteRegistry().getEmoteSources();
        if (this.page == -1) {
            DailyEmote[] dailyEmotes = SplashLoader.getLoader().getEntries().getDailyEmotes();
            emoteIndex = 0;
            int index = 6;
            while (index >= 1) {
                DailyEmote dailyEmote = emoteIndex >= dailyEmotes.length ? null : dailyEmotes[emoteIndex];
                KeyFrameStorage emote = dailyEmote == null ? null : sources.get(dailyEmote.getId());
                this.drawUnit(midX, midY, radius, 6, index, cursorX, cursorY, distance, emote, player);
                ++emoteIndex;
                --index;
            }
        } else {
            int index2 = 5;
            while (index2 >= 0) {
                Short emoteId = emoteIndex >= totalEmotes ? null : this.filteredEmotes.get(emoteIndex);
                KeyFrameStorage emote2 = emoteId == null ? null : sources.get(emoteId);
                this.drawUnit(midX, midY, radius, 6, index2, cursorX, cursorY, distance, emote2, player);
                ++emoteIndex;
                --index2;
            }
        }
        GlStateManager.disableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
        if (offsetX == 0.0 && offsetY == 0.0) {
            cursorX = (int)cursorX;
            cursorY = (int)cursorY;
        }
        if (!this.searchOpened) {
            DrawUtils.drawRect(cursorX, cursorY - 4.0, cursorX + 1.0, cursorY + 5.0, Integer.MAX_VALUE);
            DrawUtils.drawRect(cursorX - 4.0, cursorY, cursorX + 5.0, cursorY + 1.0, Integer.MAX_VALUE);
            this.handleMouseInput(maxPages - 1);
        }
    }

    private void handleMouseInput(int maxPages) {
        boolean moveDown;
        double scroll = Mouse.getDWheel();
        boolean moveUp = scroll > 0.0;
        boolean bl2 = moveDown = scroll < 0.0;
        if (moveUp || moveDown) {
            int value = this.scrollSelectedEmote + (moveUp ? 1 : -1);
            this.scrollSelectedEmote = value < 0 ? 5 : value % 6;
        }
        try {
            int amount = 6;
            int i2 = 0;
            while (i2 < amount) {
                int code = Minecraft.getMinecraft().gameSettings.keyBindsHotbar[i2].getKeyCode();
                if (code >= 0 && Keyboard.isKeyDown(code)) {
                    this.scrollSelectedEmote = amount - 1 - i2;
                }
                ++i2;
            }
        }
        catch (Exception error) {
            error.printStackTrace();
        }
        if (this.acceptedPage == this.page && this.animationState == 0) {
            if (Mouse.isButtonDown(0) && this.page > (this.dailyEmotes ? -1 : 0)) {
                --this.page;
                this.animationState = -1;
                this.pageAnimation = System.currentTimeMillis();
            }
            if (Mouse.isButtonDown(1) && this.page < maxPages) {
                ++this.page;
                this.animationState = 1;
                this.pageAnimation = System.currentTimeMillis();
            }
        } else if (!Mouse.isButtonDown(0) && !Mouse.isButtonDown(1) && this.animationState != -1 && this.animationState != 1) {
            this.acceptedPage = this.page;
        }
        if ((this.animationState == -1 || this.animationState == 1) && this.pageAnimation + 100L < System.currentTimeMillis()) {
            this.animationState *= 2;
            this.pageAnimation = System.currentTimeMillis();
        }
        if ((this.animationState == -2 || this.animationState == 2) && this.pageAnimation + 100L < System.currentTimeMillis()) {
            this.animationState = 0;
            this.pageAnimation = System.currentTimeMillis();
        }
    }

    private void drawUnit(double midX, double midY, double radius, int amount, int index, double cursorX, double cursorY, double distance, KeyFrameStorage emote, EntityPlayerSP player) {
        double middleOutsideY2;
        double middleOutsideX2;
        boolean cooldown;
        boolean validEmote;
        double tau = Math.PI * 2;
        double unitGap = 0.02;
        double idleGap = 1.0;
        double shift = 5.235987755982988;
        double outsideX = midX + radius * Math.cos((double)index * (Math.PI * 2) / (double)amount + 0.02 + 5.235987755982988);
        double outsideY = midY + radius * Math.sin((double)index * (Math.PI * 2) / (double)amount + 0.02 + 5.235987755982988);
        double outsideXNext = midX + radius * Math.cos((double)(index + 1) * (Math.PI * 2) / (double)amount - 0.02 + 5.235987755982988);
        double outsideYNext = midY + radius * Math.sin((double)(index + 1) * (Math.PI * 2) / (double)amount - 0.02 + 5.235987755982988);
        double radiusInside = radius / 5.0;
        double insideX = midX + radiusInside * Math.cos((double)index * (Math.PI * 2) / (double)amount + 0.02 + 5.235987755982988);
        double insideY = midY + radiusInside * Math.sin((double)index * (Math.PI * 2) / (double)amount + 0.02 + 5.235987755982988);
        double insideXNext = midX + radiusInside * Math.cos((double)(index + 1) * (Math.PI * 2) / (double)amount - 0.02 + 5.235987755982988);
        double insideYNext = midY + radiusInside * Math.sin((double)(index + 1) * (Math.PI * 2) / (double)amount - 0.02 + 5.235987755982988);
        double idleRadius = radius / 5.0 - 1.0;
        double idleX = midX + idleRadius * Math.cos((double)index * (Math.PI * 2) / (double)amount + 5.235987755982988);
        double idleY = midY + idleRadius * Math.sin((double)index * (Math.PI * 2) / (double)amount + 5.235987755982988);
        double idleXNext = midX + idleRadius * Math.cos((double)(index + 1) * (Math.PI * 2) / (double)amount + 5.235987755982988);
        double idleYNext = midY + idleRadius * Math.sin((double)(index + 1) * (Math.PI * 2) / (double)amount + 5.235987755982988);
        double staticMidX = LabyMod.getInstance().getDrawUtils().getWidth() / 2;
        double switchPageBrightness = this.animationState == 0 ? 1.0 : 1.0 - 1.0 / radius * Math.abs(staticMidX - midX);
        boolean bl2 = validEmote = LabyMod.getSettings().emotes && !this.emotesLocked && emote != null;
        boolean scrollSelected = this.scrollSelectedEmote != -1 && this.scrollSelectedEmote == index - (this.page == -1 ? 1 : 0);
        boolean hoverOutside = validEmote && (this.isInside(cursorX, cursorY, outsideX, outsideY, midX, midY, outsideXNext, outsideYNext) && Math.abs(distance) > 6.0 || scrollSelected && Math.abs(distance) < 6.0);
        double outsideBrightness = (hoverOutside ? 0.5 : 0.1) * switchPageBrightness;
        double outsideAlpha = (hoverOutside ? 0.6 : 0.5) * switchPageBrightness;
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        GL11.glLineWidth(2.0f);
        Minecraft.getMinecraft().getTextureManager().bindTexture(ModTextures.VOID);
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        GL11.glBegin(7);
        GL11.glColor4d(outsideBrightness, outsideBrightness, outsideBrightness, outsideAlpha);
        GL11.glVertex3d(insideX, insideY, 0.0);
        GL11.glVertex3d(insideXNext, insideYNext, 0.0);
        GL11.glVertex3d(outsideXNext, outsideYNext, 0.0);
        GL11.glVertex3d(outsideX, outsideY, 0.0);
        GL11.glEnd();
        GL11.glBegin(2);
        GL11.glColor4d(0.0, 0.0, 0.0, 0.5 * switchPageBrightness);
        GL11.glVertex3d(insideX, insideY, 0.0);
        GL11.glVertex3d(insideXNext, insideYNext, 0.0);
        GL11.glVertex3d(outsideXNext, outsideYNext, 0.0);
        GL11.glVertex3d(outsideX, outsideY, 0.0);
        GL11.glEnd();
        GL11.glBegin(4);
        GL11.glColor4d(0.3, 0.3, 0.3, 0.5 * switchPageBrightness);
        GL11.glVertex3d(idleXNext, idleYNext, 0.0);
        GL11.glVertex3d(idleX, idleY, 0.0);
        GL11.glVertex3d(midX, midY, 0.0);
        GL11.glEnd();
        GL11.glBegin(1);
        GL11.glColor4d(0.2, 0.2, 0.2, 0.9 * switchPageBrightness);
        GL11.glVertex3d(idleXNext, idleYNext, 0.0);
        GL11.glVertex3d(idleX, idleY, 0.0);
        GL11.glEnd();
        boolean bl3 = cooldown = this.emotesOnCooldown && emoteCooldownEnd > System.currentTimeMillis();
        if (emote != null && !this.emotesLocked && !cooldown) {
            EmoteRegistry registry = LabyMod.getInstance().getEmoteRegistry();
            EmoteRenderer emoteRenderer = registry.getEmoteRendererFor(player);
            if (hoverOutside) {
                boolean moving;
                boolean bl4 = moving = player.prevPosX != player.posX || player.prevPosY != player.posY || player.prevPosZ != player.posZ;
                if (moving) {
                    EmoteKeyFrame[] emoteKeyFrameArray = emote.getKeyframes();
                    int n2 = emoteKeyFrameArray.length;
                    int n3 = 0;
                    while (n3 < n2) {
                        EmoteKeyFrame keyframe = emoteKeyFrameArray[n3];
                        if (keyframe != null) {
                            EmotePose[] emotePoseArray = keyframe.getEmotePoses();
                            int n4 = emotePoseArray.length;
                            int n5 = 0;
                            while (n5 < n4) {
                                EmotePose emotePose = emotePoseArray[n5];
                                if (emotePose != null && emotePose.isBlockMovement()) {
                                    hoverOutside = false;
                                    break;
                                }
                                ++n5;
                            }
                        }
                        ++n3;
                    }
                }
            }
            if (hoverOutside) {
                if ((emoteRenderer == null || emoteRenderer.isStream()) && (emoteRenderer = registry.handleEmote(player.getUniqueID(), emote.getId())) != null) {
                    emoteRenderer.setVisible(false);
                }
                this.lastHoveredEmoteId = emote.getId();
            } else if (emoteRenderer != null && emoteRenderer.getEmoteId() == emote.getId()) {
                registry.handleEmote(player.getUniqueID(), (short)-1);
                emoteRenderer = null;
            }
            double emoteRadius = radius / 1.7;
            double middleOutsideX = midX + emoteRadius * Math.cos(((double)index + 0.5) * (Math.PI * 2) / (double)amount + 5.235987755982988);
            double middleOutsideY = midY + emoteRadius * Math.sin(((double)index + 0.5) * (Math.PI * 2) / (double)amount + 5.235987755982988);
            if (emoteRenderer != null && emoteRenderer.getEmoteId() == emote.getId()) {
                emoteRenderer.setVisible(true);
            }
            double size = radius / 70.0;
            if (switchPageBrightness < 0.5) {
                size = 0.0;
            }
            GlStateManager.pushMatrix();
            GlStateManager.scale(size, size, size);
            EmoteSelectorGui.drawEntityOnScreen(player, middleOutsideX / size, middleOutsideY / size + 8.0, 13.0, middleOutsideX - cursorX, middleOutsideY - cursorY, hoverOutside);
            GlStateManager.popMatrix();
            if (emoteRenderer != null && emoteRenderer.getEmoteId() == emote.getId()) {
                emoteRenderer.setVisible(false);
            }
        }
        if (emote != null && (this.emotesLocked || cooldown)) {
            double emoteRadius2 = radius / 1.7;
            middleOutsideX2 = midX + emoteRadius2 * Math.cos(((double)index + 0.5) * (Math.PI * 2) / (double)amount + 5.235987755982988);
            middleOutsideY2 = midY + emoteRadius2 * Math.sin(((double)index + 0.5) * (Math.PI * 2) / (double)amount + 5.235987755982988);
            double size2 = radius / 4.0;
            GlStateManager.color(1.0f, 1.0f, 1.0f);
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            Minecraft.getMinecraft().getTextureManager().bindTexture(ModTextures.MISC_BLOCKED);
            LabyMod.getInstance().getDrawUtils().drawTexture(middleOutsideX2 - size2 / 2.0, middleOutsideY2 - size2 / 2.0, 255.0, 255.0, size2, size2);
        }
        if (emote != null) {
            double emoteRadius2 = radius / 1.7;
            middleOutsideX2 = midX + emoteRadius2 * Math.cos(((double)index + 0.5) * (Math.PI * 2) / (double)amount + 5.235987755982988);
            middleOutsideY2 = midY + emoteRadius2 * Math.sin(((double)index + 0.5) * (Math.PI * 2) / (double)amount + 5.235987755982988);
            DrawUtils draw = LabyMod.getInstance().getDrawUtils();
            double fontSize = 0.5;
            String prefix = this.page == -1 ? ModColor.cl('6') : "";
            List<String> lines = draw.listFormattedStringToWidth(String.valueOf(prefix) + emote.getName(), (int)(idleRadius * 2.0 / 0.5), 2);
            int lineY = 0;
            for (String line : lines) {
                draw.drawCenteredString(line, middleOutsideX2, middleOutsideY2 + (double)lineY + emoteRadius2 / 4.0, radius / 70.0 * 0.5 * switchPageBrightness);
                lineY += 6;
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

    public void lockMouseMovementInCircle() {
        float newY;
        double distanceY;
        float newX;
        double distanceX;
        double distance;
        if (!this.open) {
            return;
        }
        EntityPlayerSP player = LabyModCore.getMinecraft().getPlayer();
        if (player == null) {
            return;
        }
        double radius = (double)LabyMod.getInstance().getDrawUtils().getHeight() / 4.0 / 3.0;
        float centerX = this.lockedYaw;
        float centerY = this.lockedPitch;
        if ((double)centerY + radius > 90.0) {
            centerY = (float)(90.0 - radius);
        }
        if ((double)centerY - radius < -90.0) {
            centerY = (float)(-90.0 + radius);
        }
        if ((distance = Math.sqrt((distanceX = (double)(centerX - (newX = player.rotationYaw))) * distanceX + (distanceY = (double)(centerY - (newY = player.rotationPitch))) * distanceY)) > radius) {
            double fromOriginToObjectX = newX - centerX;
            double fromOriginToObjectY = newY - centerY;
            double multiplier = radius / distance;
            player.rotationYaw = centerX += (float)(fromOriginToObjectX *= multiplier);
            player.prevRotationYaw = centerX;
            player.rotationPitch = centerY += (float)(fromOriginToObjectY *= multiplier);
            player.prevRotationPitch = centerY;
        }
        this.updateScrollLock(true);
    }

    public static void drawEntityOnScreen(EntityPlayerSP entity, double x2, double y2, double size, double mouseX, double mouseY, boolean hover) {
        GlStateManager.pushMatrix();
        GlStateManager.enableColorMaterial();
        GlStateManager.translate(x2, y2, 0.0);
        GlStateManager.scale(-size, size, size);
        GlStateManager.rotate(180.0f, 0.0f, 0.0f, 1.0f);
        float var6 = entity.renderYawOffset;
        float var7 = entity.rotationYaw;
        float var8 = entity.rotationPitch;
        float var9 = entity.prevRotationYawHead;
        float var10 = entity.rotationYawHead;
        RenderHelper.enableStandardItemLighting();
        GlStateManager.enableNormalize();
        GlStateManager.disableLighting();
        if (hover) {
            GL11.glColor4d(1.0, 1.0, 1.0, 1.0);
        } else {
            GL11.glColor4d(0.5, 0.5, 0.5, 1.0);
        }
        entity.renderYawOffset = (float)Math.atan(mouseX / 40.0) * 20.0f;
        entity.rotationYaw = (float)Math.atan(mouseX / 40.0) * 40.0f;
        entity.rotationPitch = -((float)Math.atan(mouseY / 40.0)) * 20.0f;
        entity.rotationYawHead = entity.rotationYaw;
        entity.prevRotationYawHead = entity.rotationYaw;
        double lastTickPosX = entity.lastTickPosX;
        double lastTickPosY = entity.lastTickPosY;
        double lastTickPosZ = entity.lastTickPosZ;
        double posX = entity.posX;
        double posY = entity.posY;
        double posZ = entity.posZ;
        double prevPosX = entity.prevPosX;
        double prevPosY = entity.prevPosY;
        double prevPosZ = entity.prevPosZ;
        double chasingPosX = entity.chasingPosX;
        double chasingPosY = entity.chasingPosY;
        double chasingPosZ = entity.chasingPosZ;
        double prevChasingPosX = entity.prevChasingPosX;
        double prevChasingPosY = entity.prevChasingPosY;
        double prevChasingPosZ = entity.prevChasingPosZ;
        entity.lastTickPosX = 0.0;
        entity.lastTickPosY = 0.0;
        entity.lastTickPosZ = 0.0;
        entity.posX = 0.0;
        entity.posY = 0.0;
        entity.posZ = 0.0;
        entity.prevPosX = 0.0;
        entity.prevPosY = 0.0;
        entity.prevPosZ = 0.0;
        entity.chasingPosX = 0.0;
        entity.chasingPosY = 0.0;
        entity.chasingPosZ = 0.0;
        entity.prevChasingPosX = 0.0;
        entity.prevChasingPosY = 0.0;
        entity.prevChasingPosZ = 0.0;
        GlStateManager.translate(0.0f, 0.0f, 10.0f);
        RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
        renderManager.setPlayerViewY(90.0f);
        LabyModCore.getRenderImplementation().renderEntity(renderManager, entity, 0.0, 0.0, 0.0, 0.0f, 1.0f, false);
        entity.lastTickPosX = lastTickPosX;
        entity.lastTickPosY = lastTickPosY;
        entity.lastTickPosZ = lastTickPosZ;
        entity.posX = posX;
        entity.posY = posY;
        entity.posZ = posZ;
        entity.prevPosX = prevPosX;
        entity.prevPosY = prevPosY;
        entity.prevPosZ = prevPosZ;
        entity.chasingPosX = chasingPosX;
        entity.chasingPosY = chasingPosY;
        entity.chasingPosZ = chasingPosZ;
        entity.prevChasingPosX = prevChasingPosX;
        entity.prevChasingPosY = prevChasingPosY;
        entity.prevChasingPosZ = prevChasingPosZ;
        entity.renderYawOffset = var6;
        entity.rotationYaw = var7;
        entity.rotationPitch = var8;
        entity.prevRotationYawHead = var9;
        entity.rotationYawHead = var10;
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GlStateManager.popMatrix();
    }

    private double sign(double px1, double py1, double px2, double py2, double px3, double py3) {
        return (px1 - px3) * (py2 - py3) - (px2 - px3) * (py1 - py3);
    }

    private boolean isInside(double pointX, double pointY, double px1, double py1, double px2, double py2, double px3, double py3) {
        boolean b3;
        boolean b1 = this.sign(pointX, pointY, px1, py1, px2, py2) < 0.0;
        boolean b2 = this.sign(pointX, pointY, px2, py2, px3, py3) < 0.0;
        boolean bl2 = b3 = this.sign(pointX, pointY, px3, py3, px1, py1) < 0.0;
        return b1 == b2 && b2 == b3;
    }

    public boolean isOpen() {
        return this.open;
    }

    public static class SearchGui
    extends GuiScreen {
        private EmoteSelectorGui emoteSelectorGui;
        private ModTextField textField;

        public SearchGui(EmoteSelectorGui emoteSelectorGui) {
            this.emoteSelectorGui = emoteSelectorGui;
        }

        @Override
        public void initGui() {
            super.initGui();
            this.textField = new ModTextField(0, LabyModCore.getMinecraft().getFontRenderer(), width / 2 - 50, height / 4 - 30, 100, 20);
            this.textField.setFocused(true);
            this.textField.setBlackBox(false);
        }

        @Override
        public void drawScreen(int mouseX, int mouseY, float partialTicks) {
            super.drawScreen(mouseX, mouseY, partialTicks);
            this.textField.drawTextBox();
            this.emoteSelectorGui.pointSearchMouse(mouseX, mouseY);
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
                this.emoteSelectorGui.filter(this.textField.getText());
            }
        }

        @Override
        public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
            super.mouseClicked(mouseX, mouseY, mouseButton);
            if (this.textField.mouseClicked(mouseX, mouseY, mouseButton)) {
                return;
            }
            this.emoteSelectorGui.pointSearchMouse(mouseX, mouseY);
            Minecraft.getMinecraft().displayGuiScreen(null);
            this.emoteSelectorGui.close();
        }

        @Override
        protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
            super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
            this.emoteSelectorGui.pointSearchMouse(mouseX, mouseY);
        }
    }
}

