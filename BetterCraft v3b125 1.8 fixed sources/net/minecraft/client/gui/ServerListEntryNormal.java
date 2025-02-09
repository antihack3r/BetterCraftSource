/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.gui;

import com.google.common.base.Charsets;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.base64.Base64;
import java.awt.image.BufferedImage;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import me.nzxtercode.bettercraft.client.utils.ProtocolVersionUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.ServerAddress;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerListEntryNormal
implements GuiListExtended.IGuiListEntry {
    private static final Logger logger = LogManager.getLogger();
    private static final ThreadPoolExecutor field_148302_b = new ScheduledThreadPoolExecutor(5, new ThreadFactoryBuilder().setNameFormat("Server Pinger #%d").setDaemon(true).build());
    private static final ResourceLocation UNKNOWN_SERVER = new ResourceLocation("textures/misc/unknown_server.png");
    private static final ResourceLocation SERVER_SELECTION_BUTTONS = new ResourceLocation("textures/gui/server_selection.png");
    private final GuiMultiplayer owner;
    private final Minecraft mc;
    private ServerData server;
    private ResourceLocation serverIcon;
    private String field_148299_g;
    private DynamicTexture field_148305_h;
    private long field_148298_f;
    private final ServerAddress serveradress;
    private final String id;

    public ServerListEntryNormal(GuiMultiplayer owner, ServerData serverIn) {
        this.owner = owner;
        this.server = serverIn;
        this.mc = Minecraft.getMinecraft();
        if (!this.mc.isIntegratedServerRunning()) {
            this.serverIcon = new ResourceLocation("servers/" + this.server.serverIP + "/icon");
            this.field_148305_h = (DynamicTexture)this.mc.getTextureManager().getTexture(this.serverIcon);
        }
        this.serveradress = ServerAddress.resolveAddress(this.server.serverIP);
        this.id = ProtocolVersionUtils.getKnownAs(this.server.version);
    }

    @Override
    public void drawEntry(int slotIndex, int x2, int y2, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected) {
        String s1;
        int l2;
        if (!this.server.field_78841_f) {
            this.server.field_78841_f = true;
            this.server.pingToServer = -2L;
            this.server.serverMOTD = "";
            this.server.populationInfo = "";
            field_148302_b.submit(new Runnable(){

                @Override
                public void run() {
                    try {
                        ServerListEntryNormal.this.owner.getOldServerPinger().ping(ServerListEntryNormal.this.server);
                    }
                    catch (UnknownHostException var2) {
                        ((ServerListEntryNormal)ServerListEntryNormal.this).server.pingToServer = -1L;
                        ((ServerListEntryNormal)ServerListEntryNormal.this).server.serverMOTD = (Object)((Object)EnumChatFormatting.DARK_RED) + "Can't resolve hostname";
                    }
                    catch (Exception var3) {
                        ((ServerListEntryNormal)ServerListEntryNormal.this).server.pingToServer = -1L;
                        ((ServerListEntryNormal)ServerListEntryNormal.this).server.serverMOTD = (Object)((Object)EnumChatFormatting.DARK_RED) + "Can't connect to server.";
                    }
                }
            });
        }
        String s2 = null;
        try {
            if (this.server.pingToServer == -1L) {
                s2 = "\u00a77IP\u00a78: \u00a7cOffline \u00a77Port\u00a78: \u00a7cOffline \u00a77Ping\u00a78: \u00a7cOffline";
                this.mc.fontRendererObj.drawString(s2, x2 + listWidth + 10, y2 + 1, -1);
                s2 = "\u00a77Brand\u00a78: \u00a7cOffline \u00a77Version\u00a78: \u00a7cOffline \u00a77ID\u00a78: \u00a7cOffline \u00a77Online\u00a78: \u00a7cOffline";
                this.mc.fontRendererObj.drawString(s2, x2 + listWidth + 10, y2 + 10, -1);
            } else if (this.server.pingToServer == -2L) {
                s2 = "\u00a77IP\u00a78: \u00a7aLoading... \u00a77Port\u00a78: \u00a7aLoading... \u00a77Ping\u00a78: \u00a7aLoading...";
                this.mc.fontRendererObj.drawString(s2, x2 + listWidth + 10, y2 + 1, -1);
                s2 = "\u00a77Brand\u00a78: \u00a7aLoading... \u00a77Version\u00a78: \u00a7aLoading... \u00a77ID\u00a78: \u00a7aLoading... \u00a77Online\u00a78: \u00a7aLoading...";
                this.mc.fontRendererObj.drawString(s2, x2 + listWidth + 10, y2 + 10, -1);
            } else {
                s2 = "\u00a77IP\u00a78: \u00a76" + InetAddress.getByName(this.serveradress.getIP()).getHostAddress() + " \u00a77Port\u00a78: \u00a76" + this.serveradress.getPort() + " \u00a77Ping\u00a78: \u00a76" + this.server.pingToServer;
                this.mc.fontRendererObj.drawString(s2, x2 + listWidth + 10, y2 + 1, -1);
                s2 = "\u00a77Brand\u00a78: \u00a7d" + (this.server.gameVersion.split(" ").length > 1 ? (this.server.gameVersion.split(" ")[1].startsWith("1.") ? this.server.gameVersion.split(" ")[0] : "\u00a7dModded") : "\u00a7dUnknown") + " \u00a77Version\u00a78: \u00a7d" + (this.server.gameVersion.split(" ").length > 1 ? (this.server.gameVersion.split(" ")[1].startsWith("1.") ? this.server.gameVersion.split(" ")[1] : this.id) : this.id) + " \u00a77ID\u00a78: \u00a7d" + this.server.version + " \u00a77Online\u00a78: \u00a7d" + EnumChatFormatting.getTextWithoutFormattingCodes(this.server.populationInfo.split("/")[0]).toString();
                this.mc.fontRendererObj.drawString(s2, x2 + listWidth + 10, y2 + 10, -1);
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        boolean flag = this.server.version > 47;
        boolean flag1 = this.server.version < 47;
        boolean flag2 = flag || flag1;
        this.mc.fontRendererObj.drawString(this.server.serverName, x2 + 32 + 3, y2 + 1, 0xFFFFFF);
        List<String> list = this.mc.fontRendererObj.listFormattedStringToWidth(this.server.serverMOTD, listWidth - 32 - 2);
        int i2 = 0;
        while (i2 < Math.min(list.size(), 2)) {
            this.mc.fontRendererObj.drawString(list.get(i2), x2 + 32 + 3, y2 + 12 + this.mc.fontRendererObj.FONT_HEIGHT * i2, 0x808080);
            ++i2;
        }
        String s22 = flag2 ? (Object)((Object)EnumChatFormatting.DARK_RED) + this.server.gameVersion.replace("1.8.x, 1.9.x, 1.10.x, 1.11.x, 1.12.x, 1.13.x, 1.14.x, 1.15.x, 1.16.x, 1.17.x,", "") : this.server.populationInfo;
        int j2 = this.mc.fontRendererObj.getStringWidth(s22);
        this.mc.fontRendererObj.drawString(s22, x2 + listWidth - j2 - 15 - 2, y2 + 1, 0x808080);
        int k2 = 0;
        if (flag2) {
            l2 = 5;
            s1 = flag ? "Client out of date!" : "Server out of date!";
            s2 = this.server.playerList;
        } else if (this.server.field_78841_f && this.server.pingToServer != -2L) {
            l2 = this.server.pingToServer < 0L ? 5 : (this.server.pingToServer < 150L ? 0 : (this.server.pingToServer < 300L ? 1 : (this.server.pingToServer < 600L ? 2 : (this.server.pingToServer < 1000L ? 3 : 4))));
            if (this.server.pingToServer < 0L) {
                s1 = "(no connection)";
            } else {
                s1 = String.valueOf(this.server.pingToServer) + "ms";
                s2 = this.server.playerList;
            }
        } else {
            k2 = 1;
            l2 = (int)(Minecraft.getSystemTime() / 100L + (long)(slotIndex * 2) & 7L);
            if (l2 > 4) {
                l2 = 8 - l2;
            }
            s1 = "Pinging...";
        }
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        this.mc.getTextureManager().bindTexture(Gui.icons);
        Gui.drawModalRectWithCustomSizedTexture(x2 + listWidth - 15, y2, k2 * 10, 176 + l2 * 8, 10, 8, 256.0f, 256.0f);
        if (this.server.getBase64EncodedIconData() != null && !this.server.getBase64EncodedIconData().equals(this.field_148299_g)) {
            this.field_148299_g = this.server.getBase64EncodedIconData();
            this.prepareServerIcon();
            this.owner.getServerList().saveServerList();
        }
        if (this.field_148305_h != null) {
            this.drawTextureAt(x2, y2, this.serverIcon);
        } else {
            this.drawTextureAt(x2, y2, UNKNOWN_SERVER);
        }
        int i1 = mouseX - x2;
        int j1 = mouseY - y2;
        if (i1 >= listWidth - 15 && i1 <= listWidth - 5 && j1 >= 0 && j1 <= 8) {
            this.owner.setHoveringText(s1);
        } else if (i1 >= listWidth - j2 - 15 - 2 && i1 <= listWidth - 15 - 2 && j1 >= 0 && j1 <= 8) {
            this.owner.setHoveringText(s2);
        }
        if (this.mc.gameSettings.touchscreen || isSelected) {
            this.mc.getTextureManager().bindTexture(SERVER_SELECTION_BUTTONS);
            Gui.drawRect(x2, y2, x2 + 32, y2 + 32, -1601138544);
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            int k1 = mouseX - x2;
            int l1 = mouseY - y2;
            if (this.func_178013_b()) {
                if (k1 < 32 && k1 > 16) {
                    Gui.drawModalRectWithCustomSizedTexture(x2, y2, 0.0f, 32.0f, 32, 32, 256.0f, 256.0f);
                } else {
                    Gui.drawModalRectWithCustomSizedTexture(x2, y2, 0.0f, 0.0f, 32, 32, 256.0f, 256.0f);
                }
            }
            if (this.owner.func_175392_a(this, slotIndex)) {
                if (k1 < 16 && l1 < 16) {
                    Gui.drawModalRectWithCustomSizedTexture(x2, y2, 96.0f, 32.0f, 32, 32, 256.0f, 256.0f);
                } else {
                    Gui.drawModalRectWithCustomSizedTexture(x2, y2, 96.0f, 0.0f, 32, 32, 256.0f, 256.0f);
                }
            }
            if (this.owner.func_175394_b(this, slotIndex)) {
                if (k1 < 16 && l1 > 16) {
                    Gui.drawModalRectWithCustomSizedTexture(x2, y2, 64.0f, 32.0f, 32, 32, 256.0f, 256.0f);
                } else {
                    Gui.drawModalRectWithCustomSizedTexture(x2, y2, 64.0f, 0.0f, 32, 32, 256.0f, 256.0f);
                }
            }
        }
    }

    protected void drawTextureAt(int p_178012_1_, int p_178012_2_, ResourceLocation p_178012_3_) {
        this.mc.getTextureManager().bindTexture(p_178012_3_);
        GlStateManager.enableBlend();
        Gui.drawModalRectWithCustomSizedTexture(p_178012_1_, p_178012_2_, 0.0f, 0.0f, 32, 32, 32.0f, 32.0f);
        GlStateManager.disableBlend();
    }

    private boolean func_178013_b() {
        return true;
    }

    private void prepareServerIcon() {
        if (this.server.getBase64EncodedIconData() == null) {
            this.mc.getTextureManager().deleteTexture(this.serverIcon);
            this.field_148305_h = null;
        } else {
            BufferedImage bufferedimage;
            block8: {
                ByteBuf bytebuf = Unpooled.copiedBuffer(this.server.getBase64EncodedIconData(), Charsets.UTF_8);
                ByteBuf bytebuf1 = Base64.decode(bytebuf);
                try {
                    bufferedimage = TextureUtil.readBufferedImage(new ByteBufInputStream(bytebuf1));
                    Validate.validState(bufferedimage.getWidth() == 64, "Must be 64 pixels wide", new Object[0]);
                    Validate.validState(bufferedimage.getHeight() == 64, "Must be 64 pixels high", new Object[0]);
                    break block8;
                }
                catch (Throwable throwable) {
                    logger.error("Invalid icon for server " + this.server.serverName + " (" + this.server.serverIP + ")", throwable);
                    this.server.setBase64EncodedIconData(null);
                }
                finally {
                    bytebuf.release();
                    bytebuf1.release();
                }
                return;
            }
            if (this.field_148305_h == null) {
                this.field_148305_h = new DynamicTexture(bufferedimage.getWidth(), bufferedimage.getHeight());
                this.mc.getTextureManager().loadTexture(this.serverIcon, this.field_148305_h);
            }
            bufferedimage.getRGB(0, 0, bufferedimage.getWidth(), bufferedimage.getHeight(), this.field_148305_h.getTextureData(), 0, bufferedimage.getWidth());
            this.field_148305_h.updateDynamicTexture();
        }
    }

    @Override
    public boolean mousePressed(int slotIndex, int p_148278_2_, int p_148278_3_, int p_148278_4_, int p_148278_5_, int p_148278_6_) {
        if (p_148278_5_ <= 32) {
            if (p_148278_5_ < 32 && p_148278_5_ > 16 && this.func_178013_b()) {
                this.owner.selectServer(slotIndex);
                this.owner.connectToSelected();
                return true;
            }
            if (p_148278_5_ < 16 && p_148278_6_ < 16 && this.owner.func_175392_a(this, slotIndex)) {
                this.owner.func_175391_a(this, slotIndex, GuiScreen.isShiftKeyDown());
                return true;
            }
            if (p_148278_5_ < 16 && p_148278_6_ > 16 && this.owner.func_175394_b(this, slotIndex)) {
                this.owner.func_175393_b(this, slotIndex, GuiScreen.isShiftKeyDown());
                return true;
            }
        }
        this.owner.selectServer(slotIndex);
        if (Minecraft.getSystemTime() - this.field_148298_f < 250L) {
            this.owner.connectToSelected();
        }
        this.field_148298_f = Minecraft.getSystemTime();
        return false;
    }

    @Override
    public void setSelected(int p_178011_1_, int p_178011_2_, int p_178011_3_) {
    }

    @Override
    public void mouseReleased(int slotIndex, int x2, int y2, int mouseEvent, int relativeX, int relativeY) {
    }

    public ServerData getServerData() {
        return this.server;
    }
}

