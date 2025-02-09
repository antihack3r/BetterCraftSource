/*
 * Decompiled with CFR 0.152.
 */
package me.nzxtercode.bettercraft.client.gui.serverlists.serverfinder;

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
import me.nzxtercode.bettercraft.client.gui.serverlists.serverfinder.GuiServerFinderMultiplayer;
import me.nzxtercode.bettercraft.client.utils.ProtocolVersionUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiListExtended;
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

public class ServerFinderServerListEntryNormal
implements GuiListExtended.IGuiListEntry {
    private static final Logger logger = LogManager.getLogger();
    private static final ThreadPoolExecutor field_148302_b = new ScheduledThreadPoolExecutor(5, new ThreadFactoryBuilder().setNameFormat("Server Pinger #%d").setDaemon(true).build());
    private static final ResourceLocation UNKNOWN_SERVER = new ResourceLocation("textures/misc/unknown_server.png");
    private static final ResourceLocation SERVER_SELECTION_BUTTONS = new ResourceLocation("textures/gui/server_selection.png");
    private final GuiServerFinderMultiplayer owner;
    private final Minecraft mc;
    private ServerData serverData;
    private ResourceLocation iconLocation;
    private String field_148299_g;
    private DynamicTexture field_148305_h;
    private long field_148298_f;
    private final ServerAddress serveradress;
    private final String id;

    public ServerFinderServerListEntryNormal(GuiServerFinderMultiplayer owner, ServerData data) {
        this.owner = owner;
        this.serverData = data;
        this.mc = Minecraft.getMinecraft();
        if (!this.mc.isIntegratedServerRunning()) {
            this.iconLocation = new ResourceLocation("servers/" + data.serverIP + "/icon");
            this.field_148305_h = (DynamicTexture)this.mc.getTextureManager().getTexture(this.iconLocation);
        }
        this.serveradress = ServerAddress.resolveAddress(this.serverData.serverIP);
        this.id = ProtocolVersionUtils.getKnownAs(this.serverData.version);
    }

    public void setServerData(ServerData data) {
        this.serverData = data;
    }

    public void ping() {
        this.serverData.field_78841_f = true;
        this.serverData.pingToServer = -2L;
        this.serverData.serverMOTD = "";
        this.serverData.populationInfo = "";
        field_148302_b.submit(() -> {
            try {
                this.owner.getOldServerPinger().ping(this.serverData);
            }
            catch (UnknownHostException var2) {
                this.serverData.pingToServer = -1L;
                this.serverData.serverMOTD = (Object)((Object)EnumChatFormatting.DARK_RED) + "Server doesn't exist";
            }
            catch (Exception var3) {
                this.serverData.pingToServer = -1L;
                this.serverData.serverMOTD = (Object)((Object)EnumChatFormatting.DARK_RED) + "Server is offline";
            }
        });
    }

    @Override
    public void drawEntry(int p_192634_1_, int p_192634_2_, int p_192634_3_, int p_192634_4_, int p_192634_5_, int p_192634_6_, int p_192634_7_, boolean p_192634_8_) {
        String s1;
        int l2;
        if (!this.serverData.field_78841_f) {
            this.serverData.field_78841_f = true;
            this.serverData.pingToServer = -2L;
            this.serverData.serverMOTD = "";
            this.serverData.populationInfo = "";
            field_148302_b.submit(() -> {
                try {
                    this.owner.getOldServerPinger().ping(this.serverData);
                }
                catch (UnknownHostException var2) {
                    this.serverData.pingToServer = -1L;
                    this.serverData.serverMOTD = (Object)((Object)EnumChatFormatting.DARK_RED) + "Server doesn't exist";
                }
                catch (Exception var3) {
                    this.serverData.pingToServer = -1L;
                    this.serverData.serverMOTD = (Object)((Object)EnumChatFormatting.DARK_RED) + "Server is offline";
                }
            });
        }
        String s2 = null;
        try {
            if (this.serverData.pingToServer == -1L) {
                s2 = "\u00a77IP\u00a78: \u00a7cOffline \u00a77Port\u00a78: \u00a7cOffline \u00a77Ping\u00a78: \u00a7cOffline";
                this.mc.fontRendererObj.drawString(s2, p_192634_2_ + p_192634_4_ + 10, p_192634_3_ + 1, -1);
                s2 = "\u00a77Brand\u00a78: \u00a7cOffline \u00a77Version\u00a78: \u00a7cOffline \u00a77ID\u00a78: \u00a7cOffline \u00a77Online\u00a78: \u00a7cOffline";
                this.mc.fontRendererObj.drawString(s2, p_192634_2_ + p_192634_4_ + 10, p_192634_3_ + 10, -1);
            } else if (this.serverData.pingToServer == -2L) {
                s2 = "\u00a77IP\u00a78: \u00a7aLoading... \u00a77Port\u00a78: \u00a7aLoading... \u00a77Ping\u00a78: \u00a7aLoading...";
                this.mc.fontRendererObj.drawString(s2, p_192634_2_ + p_192634_4_ + 10, p_192634_3_ + 1, -1);
                s2 = "\u00a77Brand\u00a78: \u00a7aLoading... \u00a77Version\u00a78: \u00a7aLoading... \u00a77ID\u00a78: \u00a7aLoading... \u00a77Online\u00a78: \u00a7aLoading...";
                this.mc.fontRendererObj.drawString(s2, p_192634_2_ + p_192634_4_ + 10, p_192634_3_ + 10, -1);
            } else {
                s2 = "\u00a77IP\u00a78: \u00a76" + InetAddress.getByName(this.serveradress.getIP()).getHostAddress() + " \u00a77Port\u00a78: \u00a76" + this.serveradress.getPort() + " \u00a77Ping\u00a78: \u00a76" + this.serverData.pingToServer;
                this.mc.fontRendererObj.drawString(s2, p_192634_2_ + p_192634_4_ + 10, p_192634_3_ + 1, -1);
                s2 = "\u00a77Brand\u00a78: \u00a7d" + (this.serverData.gameVersion.split(" ").length > 1 ? (this.serverData.gameVersion.split(" ")[1].startsWith("1.") ? this.serverData.gameVersion.split(" ")[0] : "\u00a7dModded") : "\u00a7dUnknown") + " \u00a77Version\u00a78: \u00a7d" + (this.serverData.gameVersion.split(" ").length > 1 ? (this.serverData.gameVersion.split(" ")[1].startsWith("1.") ? this.serverData.gameVersion.split(" ")[1] : this.id) : this.id) + " \u00a77ID\u00a78: \u00a7d" + this.serverData.version + " \u00a77Online\u00a78: \u00a7d" + EnumChatFormatting.getTextWithoutFormattingCodes(this.serverData.populationInfo.split("/")[0]).toString();
                this.mc.fontRendererObj.drawString(s2, p_192634_2_ + p_192634_4_ + 10, p_192634_3_ + 10, -1);
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        boolean flag = this.serverData.version > 47;
        boolean flag1 = this.serverData.version < 47;
        boolean notJoinable = flag || flag1;
        this.mc.fontRendererObj.drawString(this.serverData.serverName, p_192634_2_ + 32 + 3, p_192634_3_ + 1, 0xFFFFFF);
        List<String> list = this.mc.fontRendererObj.listFormattedStringToWidth(this.serverData.serverMOTD, p_192634_4_ - 32 - 2);
        int i2 = 0;
        while (i2 < Math.min(list.size(), 2)) {
            this.mc.fontRendererObj.drawString(list.get(i2), p_192634_2_ + 32 + 3, p_192634_3_ + 12 + this.mc.fontRendererObj.FONT_HEIGHT * i2, 0x808080);
            ++i2;
        }
        String s22 = notJoinable ? (Object)((Object)EnumChatFormatting.DARK_RED) + this.serverData.gameVersion.replace("1.8.x, 1.9.x, 1.10.x, 1.11.x, 1.12.x, 1.13.x, 1.14.x, 1.15.x, 1.16.x, 1.17.x,", "") : this.serverData.populationInfo;
        int j2 = this.mc.fontRendererObj.getStringWidth(s22);
        this.mc.fontRendererObj.drawString(s22, p_192634_2_ + p_192634_4_ - j2 - 15 - 2, p_192634_3_ + 1, 0x808080);
        int k2 = 0;
        String s3 = null;
        if (notJoinable) {
            l2 = 5;
            s1 = flag ? "Client out of date!" : "Server out of date!";
            s3 = this.serverData.playerList;
        } else if (this.serverData.field_78841_f && this.serverData.pingToServer != -2L) {
            int n2 = this.serverData.pingToServer < 0L ? 5 : (this.serverData.pingToServer < 150L ? 0 : (this.serverData.pingToServer < 300L ? 1 : (this.serverData.pingToServer < 600L ? 2 : (l2 = this.serverData.pingToServer < 1000L ? 3 : 4))));
            if (this.serverData.pingToServer < 0L) {
                s1 = "(no connection)";
            } else {
                s1 = String.valueOf(this.serverData.pingToServer) + "ms";
                s3 = this.serverData.playerList;
            }
        } else {
            k2 = 1;
            l2 = (int)(Minecraft.getSystemTime() / 100L + (long)(p_192634_1_ * 2) & 7L);
            if (l2 > 4) {
                l2 = 8 - l2;
            }
            s1 = "Pinging...";
        }
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        this.mc.getTextureManager().bindTexture(Gui.icons);
        Gui.drawModalRectWithCustomSizedTexture(p_192634_2_ + p_192634_4_ - 15, p_192634_3_, k2 * 10, 176 + l2 * 8, 10, 8, 256.0f, 256.0f);
        if (this.serverData.getBase64EncodedIconData() != null && !this.serverData.getBase64EncodedIconData().equals(this.field_148299_g)) {
            this.field_148299_g = this.serverData.getBase64EncodedIconData();
            this.prepareServerIcon();
            try {
                this.owner.getServerList().loadServerList();
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
        if (this.field_148305_h != null) {
            this.func_178012_a(p_192634_2_, p_192634_3_, this.iconLocation);
        } else {
            this.func_178012_a(p_192634_2_, p_192634_3_, UNKNOWN_SERVER);
        }
        int i1 = p_192634_6_ - p_192634_2_;
        int j1 = p_192634_7_ - p_192634_3_;
        if (i1 >= p_192634_4_ - 15 && i1 <= p_192634_4_ - 5 && j1 >= 0 && j1 <= 8) {
            this.owner.setHoveringText(s1);
        } else if (i1 >= p_192634_4_ - j2 - 15 - 2 && i1 <= p_192634_4_ - 15 - 2 && j1 >= 0 && j1 <= 8) {
            this.owner.setHoveringText(s3);
        }
        if (this.mc.gameSettings.touchscreen || p_192634_8_) {
            this.mc.getTextureManager().bindTexture(SERVER_SELECTION_BUTTONS);
            Gui.drawRect(p_192634_2_, p_192634_3_, p_192634_2_ + 32, p_192634_3_ + 32, -1601138544);
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            int k1 = p_192634_6_ - p_192634_2_;
            int l1 = p_192634_7_ - p_192634_3_;
            if (this.func_178013_b()) {
                if (k1 < 32 && k1 > 16) {
                    Gui.drawModalRectWithCustomSizedTexture(p_192634_2_, p_192634_3_, 0.0f, 32.0f, 32, 32, 256.0f, 256.0f);
                } else {
                    Gui.drawModalRectWithCustomSizedTexture(p_192634_2_, p_192634_3_, 0.0f, 0.0f, 32, 32, 256.0f, 256.0f);
                }
            }
            if (this.owner.func_175392_a(this, p_192634_1_)) {
                if (k1 < 16 && l1 < 16) {
                    Gui.drawModalRectWithCustomSizedTexture(p_192634_2_, p_192634_3_, 96.0f, 32.0f, 32, 32, 256.0f, 256.0f);
                } else {
                    Gui.drawModalRectWithCustomSizedTexture(p_192634_2_, p_192634_3_, 96.0f, 0.0f, 32, 32, 256.0f, 256.0f);
                }
            }
            if (this.owner.func_175394_b(this, p_192634_1_)) {
                if (k1 < 16 && l1 > 16) {
                    Gui.drawModalRectWithCustomSizedTexture(p_192634_2_, p_192634_3_, 64.0f, 32.0f, 32, 32, 256.0f, 256.0f);
                } else {
                    Gui.drawModalRectWithCustomSizedTexture(p_192634_2_, p_192634_3_, 64.0f, 0.0f, 32, 32, 256.0f, 256.0f);
                }
            }
        }
    }

    protected void func_178012_a(int p_178012_1_, int p_178012_2_, ResourceLocation p_178012_3_) {
        this.mc.getTextureManager().bindTexture(p_178012_3_);
        GlStateManager.enableBlend();
        Gui.drawModalRectWithCustomSizedTexture(p_178012_1_, p_178012_2_, 0.0f, 0.0f, 32, 32, 32.0f, 32.0f);
        GlStateManager.disableBlend();
    }

    private boolean func_178013_b() {
        return true;
    }

    private void prepareServerIcon() {
        if (this.serverData.getBase64EncodedIconData() == null) {
            this.mc.getTextureManager().deleteTexture(this.iconLocation);
            this.field_148305_h = null;
        } else {
            BufferedImage bufferedimage;
            block8: {
                ByteBuf bytebuf = Unpooled.copiedBuffer(this.serverData.getBase64EncodedIconData(), Charsets.UTF_8);
                ByteBuf bytebuf1 = Base64.decode(bytebuf);
                try {
                    bufferedimage = TextureUtil.readBufferedImage(new ByteBufInputStream(bytebuf1));
                    Validate.validState(bufferedimage.getWidth() == 64, "Must be 64 pixels wide", new Object[0]);
                    Validate.validState(bufferedimage.getHeight() == 64, "Must be 64 pixels high", new Object[0]);
                    break block8;
                }
                catch (Throwable throwable) {
                    logger.error("Invalid icon for server " + this.serverData.serverName + " (" + this.serverData.serverIP + ")", throwable);
                    this.serverData.setBase64EncodedIconData(null);
                }
                finally {
                    bytebuf.release();
                    bytebuf1.release();
                }
                return;
            }
            if (this.field_148305_h == null) {
                this.field_148305_h = new DynamicTexture(bufferedimage.getWidth(), bufferedimage.getHeight());
                this.mc.getTextureManager().loadTexture(this.iconLocation, this.field_148305_h);
            }
            bufferedimage.getRGB(0, 0, bufferedimage.getWidth(), bufferedimage.getHeight(), this.field_148305_h.getTextureData(), 0, bufferedimage.getWidth());
            this.field_148305_h.updateDynamicTexture();
        }
    }

    @Override
    public boolean mousePressed(int p_192634_1_, int p_148278_2_, int p_148278_3_, int p_148278_4_, int p_148278_5_, int p_148278_6_) {
        if (p_148278_5_ <= 32) {
            if (p_148278_5_ < 32 && p_148278_5_ > 16 && this.func_178013_b()) {
                this.owner.selectServer(p_192634_1_);
                this.owner.connectToSelected();
                return true;
            }
            if (p_148278_5_ < 16 && p_148278_6_ < 16 && this.owner.func_175392_a(this, p_192634_1_)) {
                this.owner.func_175391_a(null, p_192634_1_, GuiScreen.isShiftKeyDown());
                return true;
            }
            if (p_148278_5_ < 16 && p_148278_6_ > 16 && this.owner.func_175394_b(this, p_192634_1_)) {
                this.owner.func_175393_b(this, p_192634_1_, GuiScreen.isShiftKeyDown());
                return true;
            }
        }
        this.owner.selectServer(p_192634_1_);
        if (Minecraft.getSystemTime() - this.field_148298_f < 250L) {
            this.owner.connectToSelected();
        }
        this.field_148298_f = Minecraft.getSystemTime();
        return false;
    }

    @Override
    public void setSelected(int p_192633_1_, int p_192633_2_, int p_192633_3_) {
    }

    @Override
    public void mouseReleased(int p_192634_1_, int x2, int y2, int mouseEvent, int relativeX, int relativeY) {
    }

    public ServerData getServerData() {
        return this.serverData;
    }
}

