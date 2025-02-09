// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.gui;

import java.awt.image.BufferedImage;
import net.minecraft.client.renderer.texture.ITextureObject;
import org.apache.commons.lang3.Validate;
import javax.imageio.ImageIO;
import net.minecraft.world.WorldSettings;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;
import net.minecraft.world.storage.WorldInfo;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.client.resources.I18n;
import org.apache.commons.lang3.StringUtils;
import java.util.Date;
import net.minecraft.world.storage.ISaveFormat;
import java.text.SimpleDateFormat;
import org.apache.logging.log4j.LogManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import java.io.File;
import net.minecraft.world.storage.WorldSummary;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import java.text.DateFormat;
import org.apache.logging.log4j.Logger;

public class GuiListWorldSelectionEntry implements GuiListExtended.IGuiListEntry
{
    private static final Logger LOGGER;
    private static final DateFormat DATE_FORMAT;
    private static final ResourceLocation ICON_MISSING;
    private static final ResourceLocation ICON_OVERLAY_LOCATION;
    private final Minecraft client;
    private final GuiWorldSelection worldSelScreen;
    private final WorldSummary worldSummary;
    private final ResourceLocation iconLocation;
    private final GuiListWorldSelection containingListSel;
    private File iconFile;
    private DynamicTexture icon;
    private long lastClickTime;
    
    static {
        LOGGER = LogManager.getLogger();
        DATE_FORMAT = new SimpleDateFormat();
        ICON_MISSING = new ResourceLocation("textures/misc/unknown_server.png");
        ICON_OVERLAY_LOCATION = new ResourceLocation("textures/gui/world_selection.png");
    }
    
    public GuiListWorldSelectionEntry(final GuiListWorldSelection listWorldSelIn, final WorldSummary p_i46591_2_, final ISaveFormat p_i46591_3_) {
        this.containingListSel = listWorldSelIn;
        this.worldSelScreen = listWorldSelIn.getGuiWorldSelection();
        this.worldSummary = p_i46591_2_;
        this.client = Minecraft.getMinecraft();
        this.iconLocation = new ResourceLocation("worlds/" + p_i46591_2_.getFileName() + "/icon");
        this.iconFile = p_i46591_3_.getFile(p_i46591_2_.getFileName(), "icon.png");
        if (!this.iconFile.isFile()) {
            this.iconFile = null;
        }
        this.loadServerIcon();
    }
    
    @Override
    public void func_192634_a(final int p_192634_1_, final int p_192634_2_, final int p_192634_3_, final int p_192634_4_, final int p_192634_5_, final int p_192634_6_, final int p_192634_7_, final boolean p_192634_8_, final float p_192634_9_) {
        String s = this.worldSummary.getDisplayName();
        final String s2 = String.valueOf(this.worldSummary.getFileName()) + " (" + GuiListWorldSelectionEntry.DATE_FORMAT.format(new Date(this.worldSummary.getLastTimePlayed())) + ")";
        String s3 = "";
        if (StringUtils.isEmpty(s)) {
            s = String.valueOf(I18n.format("selectWorld.world", new Object[0])) + " " + (p_192634_1_ + 1);
        }
        if (this.worldSummary.requiresConversion()) {
            s3 = String.valueOf(I18n.format("selectWorld.conversion", new Object[0])) + " " + s3;
        }
        else {
            s3 = I18n.format("gameMode." + this.worldSummary.getEnumGameType().getName(), new Object[0]);
            if (this.worldSummary.isHardcoreModeEnabled()) {
                s3 = TextFormatting.DARK_RED + I18n.format("gameMode.hardcore", new Object[0]) + TextFormatting.RESET;
            }
            if (this.worldSummary.getCheatsEnabled()) {
                s3 = String.valueOf(s3) + ", " + I18n.format("selectWorld.cheats", new Object[0]);
            }
            final String s4 = this.worldSummary.getVersionName();
            if (this.worldSummary.markVersionInList()) {
                if (this.worldSummary.askToOpenWorld()) {
                    s3 = String.valueOf(s3) + ", " + I18n.format("selectWorld.version", new Object[0]) + " " + TextFormatting.RED + s4 + TextFormatting.RESET;
                }
                else {
                    s3 = String.valueOf(s3) + ", " + I18n.format("selectWorld.version", new Object[0]) + " " + TextFormatting.ITALIC + s4 + TextFormatting.RESET;
                }
            }
            else {
                s3 = String.valueOf(s3) + ", " + I18n.format("selectWorld.version", new Object[0]) + " " + s4;
            }
        }
        this.client.fontRendererObj.drawString(s, p_192634_2_ + 32 + 3, p_192634_3_ + 1, 16777215);
        this.client.fontRendererObj.drawString(s2, p_192634_2_ + 32 + 3, p_192634_3_ + this.client.fontRendererObj.FONT_HEIGHT + 3, 8421504);
        this.client.fontRendererObj.drawString(s3, p_192634_2_ + 32 + 3, p_192634_3_ + this.client.fontRendererObj.FONT_HEIGHT + this.client.fontRendererObj.FONT_HEIGHT + 3, 8421504);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        this.client.getTextureManager().bindTexture((this.icon != null) ? this.iconLocation : GuiListWorldSelectionEntry.ICON_MISSING);
        GlStateManager.enableBlend();
        Gui.drawModalRectWithCustomSizedTexture(p_192634_2_, p_192634_3_, 0.0f, 0.0f, 32, 32, 32.0f, 32.0f);
        GlStateManager.disableBlend();
        if (this.client.gameSettings.touchscreen || p_192634_8_) {
            this.client.getTextureManager().bindTexture(GuiListWorldSelectionEntry.ICON_OVERLAY_LOCATION);
            Gui.drawRect(p_192634_2_, p_192634_3_, p_192634_2_ + 32, p_192634_3_ + 32, -1601138544);
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            final int j = p_192634_6_ - p_192634_2_;
            final int i = (j < 32) ? 32 : 0;
            if (this.worldSummary.markVersionInList()) {
                Gui.drawModalRectWithCustomSizedTexture(p_192634_2_, p_192634_3_, 32.0f, (float)i, 32, 32, 256.0f, 256.0f);
                if (this.worldSummary.askToOpenWorld()) {
                    Gui.drawModalRectWithCustomSizedTexture(p_192634_2_, p_192634_3_, 96.0f, (float)i, 32, 32, 256.0f, 256.0f);
                    if (j < 32) {
                        this.worldSelScreen.setVersionTooltip(TextFormatting.RED + I18n.format("selectWorld.tooltip.fromNewerVersion1", new Object[0]) + "\n" + TextFormatting.RED + I18n.format("selectWorld.tooltip.fromNewerVersion2", new Object[0]));
                    }
                }
                else {
                    Gui.drawModalRectWithCustomSizedTexture(p_192634_2_, p_192634_3_, 64.0f, (float)i, 32, 32, 256.0f, 256.0f);
                    if (j < 32) {
                        this.worldSelScreen.setVersionTooltip(TextFormatting.GOLD + I18n.format("selectWorld.tooltip.snapshot1", new Object[0]) + "\n" + TextFormatting.GOLD + I18n.format("selectWorld.tooltip.snapshot2", new Object[0]));
                    }
                }
            }
            else {
                Gui.drawModalRectWithCustomSizedTexture(p_192634_2_, p_192634_3_, 0.0f, (float)i, 32, 32, 256.0f, 256.0f);
            }
        }
    }
    
    @Override
    public boolean mousePressed(final int slotIndex, final int mouseX, final int mouseY, final int mouseEvent, final int relativeX, final int relativeY) {
        this.containingListSel.selectWorld(slotIndex);
        if (relativeX <= 32 && relativeX < 32) {
            this.joinWorld();
            return true;
        }
        if (Minecraft.getSystemTime() - this.lastClickTime < 250L) {
            this.joinWorld();
            return true;
        }
        this.lastClickTime = Minecraft.getSystemTime();
        return false;
    }
    
    public void joinWorld() {
        if (this.worldSummary.askToOpenWorld()) {
            this.client.displayGuiScreen(new GuiYesNo(new GuiYesNoCallback() {
                @Override
                public void confirmClicked(final boolean result, final int id) {
                    if (result) {
                        GuiListWorldSelectionEntry.this.loadWorld();
                    }
                    else {
                        GuiListWorldSelectionEntry.this.client.displayGuiScreen(GuiListWorldSelectionEntry.this.worldSelScreen);
                    }
                }
            }, I18n.format("selectWorld.versionQuestion", new Object[0]), I18n.format("selectWorld.versionWarning", this.worldSummary.getVersionName()), I18n.format("selectWorld.versionJoinButton", new Object[0]), I18n.format("gui.cancel", new Object[0]), 0));
        }
        else {
            this.loadWorld();
        }
    }
    
    public void deleteWorld() {
        this.client.displayGuiScreen(new GuiYesNo(new GuiYesNoCallback() {
            @Override
            public void confirmClicked(final boolean result, final int id) {
                if (result) {
                    GuiListWorldSelectionEntry.this.client.displayGuiScreen(new GuiScreenWorking());
                    final ISaveFormat isaveformat = GuiListWorldSelectionEntry.this.client.getSaveLoader();
                    isaveformat.flushCache();
                    isaveformat.deleteWorldDirectory(GuiListWorldSelectionEntry.this.worldSummary.getFileName());
                    GuiListWorldSelectionEntry.this.containingListSel.refreshList();
                }
                GuiListWorldSelectionEntry.this.client.displayGuiScreen(GuiListWorldSelectionEntry.this.worldSelScreen);
            }
        }, I18n.format("selectWorld.deleteQuestion", new Object[0]), "'" + this.worldSummary.getDisplayName() + "' " + I18n.format("selectWorld.deleteWarning", new Object[0]), I18n.format("selectWorld.deleteButton", new Object[0]), I18n.format("gui.cancel", new Object[0]), 0));
    }
    
    public void editWorld() {
        this.client.displayGuiScreen(new GuiWorldEdit(this.worldSelScreen, this.worldSummary.getFileName()));
    }
    
    public void recreateWorld() {
        this.client.displayGuiScreen(new GuiScreenWorking());
        final GuiCreateWorld guicreateworld = new GuiCreateWorld(this.worldSelScreen);
        final ISaveHandler isavehandler = this.client.getSaveLoader().getSaveLoader(this.worldSummary.getFileName(), false);
        final WorldInfo worldinfo = isavehandler.loadWorldInfo();
        isavehandler.flush();
        if (worldinfo != null) {
            guicreateworld.recreateFromExistingWorld(worldinfo);
            this.client.displayGuiScreen(guicreateworld);
        }
    }
    
    private void loadWorld() {
        this.client.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f));
        if (this.client.getSaveLoader().canLoadWorld(this.worldSummary.getFileName())) {
            this.client.launchIntegratedServer(this.worldSummary.getFileName(), this.worldSummary.getDisplayName(), null);
        }
    }
    
    private void loadServerIcon() {
        final boolean flag = this.iconFile != null && this.iconFile.isFile();
        if (flag) {
            BufferedImage bufferedimage;
            try {
                bufferedimage = ImageIO.read(this.iconFile);
                Validate.validState(bufferedimage.getWidth() == 64, "Must be 64 pixels wide", new Object[0]);
                Validate.validState(bufferedimage.getHeight() == 64, "Must be 64 pixels high", new Object[0]);
            }
            catch (final Throwable throwable) {
                GuiListWorldSelectionEntry.LOGGER.error("Invalid icon for world {}", this.worldSummary.getFileName(), throwable);
                this.iconFile = null;
                return;
            }
            if (this.icon == null) {
                this.icon = new DynamicTexture(bufferedimage.getWidth(), bufferedimage.getHeight());
                this.client.getTextureManager().loadTexture(this.iconLocation, this.icon);
            }
            bufferedimage.getRGB(0, 0, bufferedimage.getWidth(), bufferedimage.getHeight(), this.icon.getTextureData(), 0, bufferedimage.getWidth());
            this.icon.updateDynamicTexture();
        }
        else if (!flag) {
            this.client.getTextureManager().deleteTexture(this.iconLocation);
            this.icon = null;
        }
    }
    
    @Override
    public void mouseReleased(final int slotIndex, final int x, final int y, final int mouseEvent, final int relativeX, final int relativeY) {
    }
    
    @Override
    public void func_192633_a(final int p_192633_1_, final int p_192633_2_, final int p_192633_3_, final float p_192633_4_) {
    }
}
