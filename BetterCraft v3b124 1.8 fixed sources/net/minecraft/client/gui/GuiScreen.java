/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.gui;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonObject;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import javax.imageio.ImageIO;
import me.nzxtercode.bettercraft.client.Config;
import me.nzxtercode.bettercraft.client.gui.section.GuiUISettings;
import me.nzxtercode.bettercraft.client.misc.background.ShaderBackgroundLoader;
import me.nzxtercode.bettercraft.client.utils.ColorUtils;
import me.nzxtercode.bettercraft.client.utils.ParticleUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiConfirmOpenLink;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.stream.GuiTwitchUserMode;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.EntityList;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import tv.twitch.chat.ChatUserInfo;

public abstract class GuiScreen
extends Gui
implements GuiYesNoCallback {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Set<String> PROTOCOLS = Sets.newHashSet("http", "https");
    private static final Splitter NEWLINE_SPLITTER = Splitter.on('\n');
    protected Minecraft mc;
    protected RenderItem itemRender;
    public static int width;
    public static int height;
    protected List<GuiButton> buttonList = Lists.newArrayList();
    protected List<GuiLabel> labelList = Lists.newArrayList();
    public boolean allowUserInput;
    protected FontRenderer fontRendererObj;
    private GuiButton selectedButton;
    private int eventButton;
    private long lastMouseEvent;
    private int touchValue;
    private URI clickedLinkURI;
    private ParticleUtils particleUtils = new ParticleUtils(50, 2.0f);

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (GuiUISettings.enabledBackgrounds[3]) {
            this.particleUtils.render(mouseX, mouseY);
        }
        int i2 = 0;
        while (i2 < this.buttonList.size()) {
            this.buttonList.get(i2).drawButton(this.mc, mouseX, mouseY);
            ++i2;
        }
        int j2 = 0;
        while (j2 < this.labelList.size()) {
            this.labelList.get(j2).drawLabel(this.mc, mouseX, mouseY);
            ++j2;
        }
    }

    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == 1) {
            this.mc.displayGuiScreen(null);
            if (this.mc.currentScreen == null) {
                this.mc.setIngameFocus();
            }
        }
    }

    public static String getClipboardString() {
        try {
            Transferable transferable = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
            if (transferable != null && transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                return (String)transferable.getTransferData(DataFlavor.stringFlavor);
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        return "";
    }

    public static void setClipboardString(String copyText) {
        if (!StringUtils.isEmpty(copyText)) {
            try {
                StringSelection stringselection = new StringSelection(copyText);
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringselection, null);
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }

    protected void renderToolTip(ItemStack stack, int x2, int y2) {
        List<String> list = stack.getTooltip(this.mc.thePlayer, this.mc.gameSettings.advancedItemTooltips);
        int i2 = 0;
        while (i2 < list.size()) {
            if (i2 == 0) {
                list.set(i2, (Object)((Object)stack.getRarity().rarityColor) + list.get(i2));
            } else {
                list.set(i2, (Object)((Object)EnumChatFormatting.GRAY) + list.get(i2));
            }
            ++i2;
        }
        this.drawHoveringText(list, x2, y2);
    }

    protected void drawCreativeTabHoveringText(String tabName, int mouseX, int mouseY) {
        this.drawHoveringText(Arrays.asList(tabName), mouseX, mouseY);
    }

    protected void drawHoveringText(List<String> textLines, int x2, int y2) {
        if (!textLines.isEmpty()) {
            GlStateManager.disableRescaleNormal();
            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            int i2 = 0;
            for (String s2 : textLines) {
                int j2 = this.fontRendererObj.getStringWidth(s2);
                if (j2 <= i2) continue;
                i2 = j2;
            }
            int l1 = x2 + 12;
            int i22 = y2 - 12;
            int k2 = 8;
            if (textLines.size() > 1) {
                k2 += 2 + (textLines.size() - 1) * 10;
            }
            if (l1 + i2 > width) {
                l1 -= 28 + i2;
            }
            if (i22 + k2 + 6 > height) {
                i22 = height - k2 - 6;
            }
            this.zLevel = 300.0f;
            this.itemRender.zLevel = 300.0f;
            int l2 = -267386864;
            this.drawGradientRect(l1 - 3, i22 - 4, l1 + i2 + 3, i22 - 3, l2, -1);
            this.drawGradientRect(l1 - 3, i22 + k2 + 3, l1 + i2 + 3, i22 + k2 + 4, l2, -1);
            this.drawGradientRect(l1 - 3, i22 - 3, l1 + i2 + 3, i22 + k2 + 3, l2, ColorUtils.rainbowEffect());
            this.drawGradientRect(l1 - 4, i22 - 3, l1 - 3, i22 + k2 + 3, l2, -1);
            this.drawGradientRect(l1 + i2 + 3, i22 - 3, l1 + i2 + 4, i22 + k2 + 3, l2, -1);
            int i1 = 0x505000FF;
            int j1 = (i1 & 0xFEFEFE) >> 1 | i1 & 0xFF000000;
            this.drawGradientRect(l1 - 3, i22 - 3 + 1, l1 - 3 + 1, i22 + k2 + 3 - 1, i1, j1);
            this.drawGradientRect(l1 + i2 + 2, i22 - 3 + 1, l1 + i2 + 3, i22 + k2 + 3 - 1, i1, j1);
            this.drawGradientRect(l1 - 3, i22 - 3, l1 + i2 + 3, i22 - 3 + 1, i1, i1);
            this.drawGradientRect(l1 - 3, i22 + k2 + 2, l1 + i2 + 3, i22 + k2 + 3, j1, j1);
            int k1 = 0;
            while (k1 < textLines.size()) {
                String s1 = textLines.get(k1);
                this.fontRendererObj.drawStringWithShadow(s1, l1, i22, -1);
                if (k1 == 0) {
                    i22 += 2;
                }
                i22 += 10;
                ++k1;
            }
            this.zLevel = 0.0f;
            this.itemRender.zLevel = 0.0f;
            GlStateManager.enableLighting();
            GlStateManager.enableDepth();
            RenderHelper.enableStandardItemLighting();
            GlStateManager.enableRescaleNormal();
        }
    }

    protected void handleComponentHover(IChatComponent component, int x2, int y2) {
        if (component != null && component.getChatStyle().getChatHoverEvent() != null) {
            block21: {
                HoverEvent hoverevent = component.getChatStyle().getChatHoverEvent();
                if (hoverevent.getAction() == HoverEvent.Action.SHOW_ITEM) {
                    ItemStack itemstack = null;
                    try {
                        NBTTagCompound nbtbase = JsonToNBT.getTagFromJson(hoverevent.getValue().getUnformattedText());
                        if (nbtbase instanceof NBTTagCompound) {
                            itemstack = ItemStack.loadItemStackFromNBT(nbtbase);
                        }
                    }
                    catch (NBTException nbtbase) {
                        // empty catch block
                    }
                    if (itemstack != null) {
                        this.renderToolTip(itemstack, x2, y2);
                    } else {
                        this.drawCreativeTabHoveringText((Object)((Object)EnumChatFormatting.RED) + "Invalid Item!", x2, y2);
                    }
                } else if (hoverevent.getAction() == HoverEvent.Action.SHOW_ENTITY) {
                    if (this.mc.gameSettings.advancedItemTooltips) {
                        try {
                            NBTTagCompound nbtbase1 = JsonToNBT.getTagFromJson(hoverevent.getValue().getUnformattedText());
                            if (nbtbase1 instanceof NBTTagCompound) {
                                ArrayList<String> list1 = Lists.newArrayList();
                                NBTTagCompound nbttagcompound = nbtbase1;
                                list1.add(nbttagcompound.getString("name"));
                                if (nbttagcompound.hasKey("type", 8)) {
                                    String s2 = nbttagcompound.getString("type");
                                    list1.add("Type: " + s2 + " (" + EntityList.getIDFromString(s2) + ")");
                                }
                                list1.add(nbttagcompound.getString("id"));
                                this.drawHoveringText(list1, x2, y2);
                                break block21;
                            }
                            this.drawCreativeTabHoveringText((Object)((Object)EnumChatFormatting.RED) + "Invalid Entity!", x2, y2);
                        }
                        catch (NBTException var10) {
                            this.drawCreativeTabHoveringText((Object)((Object)EnumChatFormatting.RED) + "Invalid Entity!", x2, y2);
                        }
                    }
                } else if (hoverevent.getAction() == HoverEvent.Action.SHOW_TEXT) {
                    this.drawHoveringText(NEWLINE_SPLITTER.splitToList(hoverevent.getValue().getFormattedText()), x2, y2);
                } else if (hoverevent.getAction() == HoverEvent.Action.SHOW_ACHIEVEMENT) {
                    StatBase statbase = StatList.getOneShotStat(hoverevent.getValue().getUnformattedText());
                    if (statbase != null) {
                        IChatComponent ichatcomponent = statbase.getStatName();
                        ChatComponentTranslation ichatcomponent1 = new ChatComponentTranslation("stats.tooltip.type." + (statbase.isAchievement() ? "achievement" : "statistic"), new Object[0]);
                        ichatcomponent1.getChatStyle().setItalic(true);
                        String s1 = statbase instanceof Achievement ? ((Achievement)statbase).getDescription() : null;
                        ArrayList<String> list = Lists.newArrayList(ichatcomponent.getFormattedText(), ichatcomponent1.getFormattedText());
                        if (s1 != null) {
                            list.addAll(this.fontRendererObj.listFormattedStringToWidth(s1, 150));
                        }
                        this.drawHoveringText(list, x2, y2);
                    } else {
                        this.drawCreativeTabHoveringText((Object)((Object)EnumChatFormatting.RED) + "Invalid statistic/achievement!", x2, y2);
                    }
                }
            }
            GlStateManager.disableLighting();
        }
    }

    protected void setText(String newChatText, boolean shouldOverwrite) {
    }

    protected boolean handleComponentClick(IChatComponent component) {
        if (component == null) {
            return false;
        }
        ClickEvent clickevent = component.getChatStyle().getChatClickEvent();
        if (GuiScreen.isShiftKeyDown()) {
            if (component.getChatStyle().getInsertion() != null) {
                this.setText(component.getChatStyle().getInsertion(), false);
            }
        } else if (clickevent != null) {
            block23: {
                if (clickevent.getAction() == ClickEvent.Action.OPEN_URL) {
                    if (!this.mc.gameSettings.chatLinks) {
                        return false;
                    }
                    try {
                        URI uri = new URI(clickevent.getValue());
                        String s2 = uri.getScheme();
                        if (s2 == null) {
                            throw new URISyntaxException(clickevent.getValue(), "Missing protocol");
                        }
                        if (!PROTOCOLS.contains(s2.toLowerCase())) {
                            throw new URISyntaxException(clickevent.getValue(), "Unsupported protocol: " + s2.toLowerCase());
                        }
                        if (this.mc.gameSettings.chatLinksPrompt) {
                            this.clickedLinkURI = uri;
                            this.mc.displayGuiScreen(new GuiConfirmOpenLink((GuiYesNoCallback)this, clickevent.getValue(), 31102009, false));
                            break block23;
                        }
                        this.openWebLink(uri);
                    }
                    catch (URISyntaxException urisyntaxexception) {
                        LOGGER.error("Can't open url for " + clickevent, (Throwable)urisyntaxexception);
                    }
                } else if (clickevent.getAction() == ClickEvent.Action.OPEN_FILE) {
                    URI uri1 = new File(clickevent.getValue()).toURI();
                    this.openWebLink(uri1);
                } else if (clickevent.getAction() == ClickEvent.Action.SUGGEST_COMMAND) {
                    this.setText(clickevent.getValue(), true);
                } else if (clickevent.getAction() == ClickEvent.Action.RUN_COMMAND) {
                    this.sendChatMessage(clickevent.getValue(), false);
                } else if (clickevent.getAction() == ClickEvent.Action.TWITCH_USER_INFO) {
                    ChatUserInfo chatuserinfo = this.mc.getTwitchStream().func_152926_a(clickevent.getValue());
                    if (chatuserinfo != null) {
                        this.mc.displayGuiScreen(new GuiTwitchUserMode(this.mc.getTwitchStream(), chatuserinfo));
                    } else {
                        LOGGER.error("Tried to handle twitch user but couldn't find them!");
                    }
                } else {
                    LOGGER.error("Don't know how to handle " + clickevent);
                }
            }
            return true;
        }
        return false;
    }

    public void sendChatMessage(String msg) {
        this.sendChatMessage(msg, true);
    }

    public void sendChatMessage(String msg, boolean addToChat) {
        if (addToChat) {
            this.mc.ingameGUI.getChatGUI().addToSentMessages(msg);
        }
        this.mc.thePlayer.sendChatMessage(msg);
    }

    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (mouseButton == 0) {
            int i2 = 0;
            while (i2 < this.buttonList.size()) {
                GuiButton guibutton = this.buttonList.get(i2);
                if (guibutton.mousePressed(this.mc, mouseX, mouseY)) {
                    this.selectedButton = guibutton;
                    guibutton.playPressSound(this.mc.getSoundHandler());
                    this.actionPerformed(guibutton);
                }
                ++i2;
            }
        }
    }

    protected void mouseReleased(int mouseX, int mouseY, int state) {
        if (this.selectedButton != null && state == 0) {
            this.selectedButton.mouseReleased(mouseX, mouseY);
            this.selectedButton = null;
        }
    }

    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
    }

    protected void actionPerformed(GuiButton button) throws IOException {
    }

    public void setWorldAndResolution(Minecraft mc2, int width, int height) {
        this.mc = mc2;
        this.itemRender = mc2.getRenderItem();
        this.fontRendererObj = mc2.fontRendererObj;
        GuiScreen.width = width;
        GuiScreen.height = height;
        this.buttonList.clear();
        this.initGui();
        this.particleUtils.setup();
    }

    public void setGuiSize(int w2, int h2) {
        width = w2;
        height = h2;
    }

    public void initGui() {
    }

    public void handleInput() throws IOException {
        if (Mouse.isCreated()) {
            while (Mouse.next()) {
                this.handleMouseInput();
            }
        }
        if (Keyboard.isCreated()) {
            while (Keyboard.next()) {
                this.handleKeyboardInput();
            }
        }
    }

    public void handleMouseInput() throws IOException {
        int i2 = Mouse.getEventX() * width / this.mc.displayWidth;
        int j2 = height - Mouse.getEventY() * height / this.mc.displayHeight - 1;
        int k2 = Mouse.getEventButton();
        if (Mouse.getEventButtonState()) {
            if (this.mc.gameSettings.touchscreen && this.touchValue++ > 0) {
                return;
            }
            this.eventButton = k2;
            this.lastMouseEvent = Minecraft.getSystemTime();
            this.mouseClicked(i2, j2, this.eventButton);
        } else if (k2 != -1) {
            if (this.mc.gameSettings.touchscreen && --this.touchValue > 0) {
                return;
            }
            this.eventButton = -1;
            this.mouseReleased(i2, j2, k2);
        } else if (this.eventButton != -1 && this.lastMouseEvent > 0L) {
            long l2 = Minecraft.getSystemTime() - this.lastMouseEvent;
            this.mouseClickMove(i2, j2, this.eventButton, l2);
        }
    }

    public void handleKeyboardInput() throws IOException {
        if (Keyboard.getEventKeyState()) {
            this.keyTyped(Keyboard.getEventCharacter(), Keyboard.getEventKey());
        }
        this.mc.dispatchKeypresses();
    }

    public void updateScreen() {
    }

    public void onGuiClosed() {
    }

    public void drawDefaultBackground() {
        this.drawWorldBackground(0);
    }

    public void drawWorldBackground(int tint) {
        if (this.mc.theWorld != null) {
            this.drawGradientRect(0, 0, width, height, -1072689136, -804253680);
        } else {
            this.drawBackground(tint);
        }
    }

    public void drawBackground(int tint) {
        ScaledResolution sr1 = new ScaledResolution(this.mc);
        JsonObject background = Config.getInstance().getBackground("Background");
        if (!(ShaderBackgroundLoader.getLoader().isPEnabledBackground() || ShaderBackgroundLoader.getLoader().isEnabledShader() || ShaderBackgroundLoader.getLoader().isCEnabledBackground())) {
            this.drawGradientRect(0, -100, width, height, background.get("color").getAsInt(), background.get("color").getAsInt());
            return;
        }
        if (ShaderBackgroundLoader.getLoader().isEnabledShader()) {
            ShaderBackgroundLoader.getLoader().renderShader();
            return;
        }
        if (ShaderBackgroundLoader.getLoader().isCEnabledBackground()) {
            String file = background.get("custom").getAsString();
            if (new File(file).exists()) {
                if (!ShaderBackgroundLoader.getLoader().getImageCache().containsKey(file)) {
                    try {
                        ShaderBackgroundLoader.getLoader().getImageCache().put(file, new DynamicTexture(ImageIO.read(new File(file))));
                    }
                    catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }
                DynamicTexture dynamicTexture = ShaderBackgroundLoader.getLoader().getImageCache().get(file);
                GlStateManager.bindTexture(dynamicTexture.getGlTextureId());
                Gui.drawModalRectWithCustomSizedTexture(0, 0, 0.0f, 0.0f, sr1.getScaledWidth(), sr1.getScaledHeight(), sr1.getScaledWidth(), sr1.getScaledHeight());
            } else {
                ShaderBackgroundLoader.getLoader().setCEnabledBackground(!ShaderBackgroundLoader.getLoader().isCEnabledBackground());
            }
        } else {
            TextureManager textureManager = this.mc.getTextureManager();
            Object[] objectArray = new Object[1];
            ShaderBackgroundLoader.getLoader();
            objectArray[0] = ShaderBackgroundLoader.getBackgrounds().get(background.get("id").getAsInt());
            textureManager.bindTexture(new ResourceLocation(String.format("client/backgrounds/%s.png", objectArray)));
            Gui.drawModalRectWithCustomSizedTexture(0, 0, 0.0f, 0.0f, sr1.getScaledWidth(), sr1.getScaledHeight(), sr1.getScaledWidth(), sr1.getScaledHeight());
        }
    }

    public boolean doesGuiPauseGame() {
        return true;
    }

    @Override
    public void confirmClicked(boolean result, int id2) {
        if (id2 == 31102009) {
            if (result) {
                this.openWebLink(this.clickedLinkURI);
            }
            this.clickedLinkURI = null;
            this.mc.displayGuiScreen(this);
        }
    }

    private void openWebLink(URI url) {
        try {
            Class<?> oclass = Class.forName("java.awt.Desktop");
            Object object = oclass.getMethod("getDesktop", new Class[0]).invoke(null, new Object[0]);
            oclass.getMethod("browse", URI.class).invoke(object, url);
        }
        catch (Throwable throwable) {
            LOGGER.error("Couldn't open link", throwable);
        }
    }

    public static boolean isCtrlKeyDown() {
        return Minecraft.isRunningOnMac ? Keyboard.isKeyDown(219) || Keyboard.isKeyDown(220) : Keyboard.isKeyDown(29) || Keyboard.isKeyDown(157);
    }

    public static boolean isShiftKeyDown() {
        return Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54);
    }

    public static boolean isAltKeyDown() {
        return Keyboard.isKeyDown(56) || Keyboard.isKeyDown(184);
    }

    public static boolean isKeyComboCtrlX(int keyID) {
        return keyID == 45 && GuiScreen.isCtrlKeyDown() && !GuiScreen.isShiftKeyDown() && !GuiScreen.isAltKeyDown();
    }

    public static boolean isKeyComboCtrlV(int keyID) {
        return keyID == 47 && GuiScreen.isCtrlKeyDown() && !GuiScreen.isShiftKeyDown() && !GuiScreen.isAltKeyDown();
    }

    public static boolean isKeyComboCtrlC(int keyID) {
        return keyID == 46 && GuiScreen.isCtrlKeyDown() && !GuiScreen.isShiftKeyDown() && !GuiScreen.isAltKeyDown();
    }

    public static boolean isKeyComboCtrlA(int keyID) {
        return keyID == 30 && GuiScreen.isCtrlKeyDown() && !GuiScreen.isShiftKeyDown() && !GuiScreen.isAltKeyDown();
    }

    public void onResize(Minecraft mcIn, int w2, int h2) {
        this.setWorldAndResolution(mcIn, w2, h2);
    }
}

