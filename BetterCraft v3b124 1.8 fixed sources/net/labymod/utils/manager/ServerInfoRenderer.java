/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.utils.manager;

import com.google.common.base.Charsets;
import com.google.common.base.Objects;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.base64.Base64;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import me.nzxtercode.bettercraft.client.utils.ProtocolVersionUtils;
import net.labymod.core.LabyModCore;
import net.labymod.core.ServerPingerData;
import net.labymod.main.LabyMod;
import net.labymod.main.Source;
import net.labymod.utils.ServerData;
import net.labymod.utils.manager.TooltipHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.ServerAddress;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

public class ServerInfoRenderer {
    private static final ResourceLocation UNKNOWN_SERVER = new ResourceLocation("textures/misc/unknown_server.png");
    private static final ResourceLocation SERVER_SELECTION_BUTTONS = new ResourceLocation("textures/gui/server_selection.png");
    private final Minecraft mc;
    private ServerPingerData serverData;
    private ResourceLocation serverIcon;
    private String base64;
    private DynamicTexture dynamicTexture;
    private boolean canReachServer = false;
    private boolean hidden = false;
    private ServerData labymodServerData;
    private int index = 0;
    private final ServerAddress serveradress;
    private final String id;
    private final AtomicReference<String> geoData = new AtomicReference();

    public ServerInfoRenderer(String rawIp, ServerPingerData serverData) {
        this(rawIp, rawIp, serverData);
    }

    public ServerInfoRenderer(String serverName, String rawIp, ServerPingerData serverData) {
        this.mc = Minecraft.getMinecraft();
        this.init(serverName, rawIp, serverData);
        this.serveradress = ServerAddress.resolveAddress(this.serverData.getIpAddress());
        this.id = ProtocolVersionUtils.getKnownAs(this.serverData.version);
        CompletableFuture.runAsync(() -> {
            try {
                URL servergeodata = new URL("http://ip-api.com/json/" + this.serveradress.getIP() + "?fields=country,regionName,city,as,reverse,query");
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(servergeodata.openStream()));
                this.geoData.set(bufferedReader.readLine());
            }
            catch (Exception exception) {
                // empty catch block
            }
        });
    }

    public void init(String serverName, String rawIp, ServerPingerData serverData) {
        if (serverData == null) {
            serverData = new ServerPingerData(rawIp == null || rawIp.isEmpty() ? "localhost" : rawIp, 0L);
            serverData.setPingToServer(-1L);
            serverData.setMotd("\u00a74Can't connect to server.");
            serverData.setVersion(Source.ABOUT_MC_PROTOCOL_VERSION);
            this.canReachServer = false;
        } else {
            this.serverIcon = new ResourceLocation("servers/" + serverData.getIpAddress() + "/icon");
            this.dynamicTexture = (DynamicTexture)this.mc.getTextureManager().getTexture(this.serverIcon);
            this.canReachServer = true;
        }
        this.serverData = serverData;
        this.serverData.setServerName(serverName);
    }

    public void drawEntry(int slotIndex, int x2, int y2, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected) {
        String s4;
        int l2;
        boolean flag = this.isClientOutOfDate();
        boolean flag2 = this.isServerOutOfDate();
        boolean flag3 = flag || flag2;
        LabyModCore.getMinecraft().getFontRenderer().drawString(this.serverData.getServerName(), x2 + 32 + 3, y2 + 1, 0xFFFFFF);
        List<String> list = LabyMod.getInstance().getDrawUtils().listFormattedStringToWidth(this.serverData.getMotd(), listWidth - 32 - 2);
        int i2 = 0;
        while (i2 < Math.min(list.size(), 2)) {
            LabyModCore.getMinecraft().getFontRenderer().drawString(list.get(i2), x2 + 32 + 3, y2 + 12 + LabyModCore.getMinecraft().getFontRenderer().FONT_HEIGHT * i2, 0x808080);
            ++i2;
        }
        String populationInfo = "\u00a77" + this.serverData.getCurrentPlayers() + "\u00a78/\u00a77" + this.serverData.getMaxPlayers();
        if (!this.canReachServer) {
            populationInfo = "\u00a77???";
        }
        String s2 = flag3 ? "\u00a74" + this.serverData.getGameVersion() : populationInfo;
        int j2 = LabyModCore.getMinecraft().getFontRenderer().getStringWidth(s2);
        LabyModCore.getMinecraft().getFontRenderer().drawString(s2, x2 + listWidth - j2 - 15 - 2, y2 + 1, 0x808080);
        int k2 = 0;
        String s3 = null;
        try {
            if (this.serverData.pingToServer == -1L) {
                s3 = "\u00a77IP\u00a78: \u00a7cOffline \u00a77Port\u00a78: \u00a7cOffline \u00a77Ping\u00a78: \u00a7cOffline";
                this.mc.fontRendererObj.drawString(s3, x2 + listWidth + 10, y2 + 1, -1);
                s3 = "\u00a77Brand\u00a78: \u00a7cOffline \u00a77Version\u00a78: \u00a7cOffline \u00a77ID\u00a78: \u00a7cOffline \u00a77Online\u00a78: \u00a7cOffline";
                this.mc.fontRendererObj.drawString(s3, x2 + listWidth + 10, y2 + 10, -1);
            } else if (this.serverData.pingToServer == -2L) {
                s3 = "\u00a77IP\u00a78: \u00a7aLoading... \u00a77Port\u00a78: \u00a7aLoading... \u00a77Ping\u00a78: \u00a7aLoading...";
                this.mc.fontRendererObj.drawString(s3, x2 + listWidth + 10, y2 + 1, -1);
                s3 = "\u00a77Brand\u00a78: \u00a7aLoading... \u00a77Version\u00a78: \u00a7aLoading... \u00a77ID\u00a78: \u00a7aLoading... \u00a77Online\u00a78: \u00a7aLoading...";
                this.mc.fontRendererObj.drawString(s3, x2 + listWidth + 10, y2 + 10, -1);
            } else {
                s3 = "\u00a77IP\u00a78: \u00a76" + InetAddress.getByName(this.serveradress.getIP()).getHostAddress() + " \u00a77Port\u00a78: \u00a76" + this.serveradress.getPort() + " \u00a77Ping\u00a78: \u00a76" + this.serverData.pingToServer;
                this.mc.fontRendererObj.drawString(s3, x2 + listWidth + 10, y2 + 1, -1);
                s3 = "\u00a77Brand\u00a78: \u00a7d" + (this.serverData.gameVersion.split(" ").length > 1 ? (this.serverData.gameVersion.split(" ")[1].startsWith("1.") ? this.serverData.gameVersion.split(" ")[0] : "\u00a7dModded") : "\u00a7dUnknown") + " \u00a77Version\u00a78: \u00a7d" + (this.serverData.gameVersion.split(" ").length > 1 ? (this.serverData.gameVersion.split(" ")[1].startsWith("1.") ? this.serverData.gameVersion.split(" ")[1] : this.id) : this.id) + " \u00a77ID\u00a78: \u00a7d" + this.serverData.version + " \u00a77Online\u00a78: \u00a7d" + this.serverData.getCurrentPlayers();
                this.mc.fontRendererObj.drawString(s3, x2 + listWidth + 10, y2 + 10, -1);
                JsonObject json = new JsonParser().parse(this.geoData.get()).getAsJsonObject();
                AtomicInteger height = new AtomicInteger();
                json.entrySet().forEach(entry -> this.mc.fontRendererObj.drawString(String.format("%s%s: %s%s", new Object[]{EnumChatFormatting.GRAY, StringUtils.capitalize((String)entry.getKey()), EnumChatFormatting.RED, ((JsonElement)entry.getValue()).getAsString()}), x2 + listWidth + 10, GuiScreen.height / 4 + 85 + height.getAndAdd(this.mc.fontRendererObj.FONT_HEIGHT) - (json.entrySet().size() * this.mc.fontRendererObj.FONT_HEIGHT / 2 + this.mc.fontRendererObj.FONT_HEIGHT / 2), -1));
            }
        }
        catch (Exception json) {
            // empty catch block
        }
        if (flag3) {
            l2 = 5;
            s4 = flag ? "Client out of date!" : "Server out of date!";
            s3 = this.serverData.getPlayerList();
        } else if (this.serverData.getPingToServer() != -2L) {
            l2 = this.serverData.getPingToServer() < 0L ? 5 : (this.serverData.getPingToServer() < 150L ? 0 : (this.serverData.getPingToServer() < 300L ? 1 : (this.serverData.getPingToServer() < 600L ? 2 : (this.serverData.getPingToServer() < 1000L ? 3 : 4))));
            if (this.serverData.getPingToServer() < 0L) {
                s4 = "(no connection)";
            } else {
                s4 = String.valueOf(this.serverData.getPingToServer()) + "ms";
                s3 = this.serverData.getPlayerList();
            }
        } else {
            k2 = 1;
            l2 = (int)(Minecraft.getSystemTime() / 100L + 2L & 7L);
            if (l2 > 4) {
                l2 = 8 - l2;
            }
            s4 = "Pinging...";
        }
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        this.mc.getTextureManager().bindTexture(LabyModCore.getRenderImplementation().getIcons());
        Gui.drawModalRectWithCustomSizedTexture(x2 + listWidth - 15, y2, k2 * 10, 176 + l2 * 8, 10, 8, 256.0f, 256.0f);
        if (this.serverData != null && this.serverData.getBase64EncodedIconData() != null && !Objects.equal(this.serverData.getBase64EncodedIconData(), this.base64)) {
            this.base64 = this.serverData.getBase64EncodedIconData();
            this.prepareServerIcon();
        }
        if (this.dynamicTexture != null) {
            this.drawServerIcon(x2, y2, this.serverIcon);
        } else {
            this.drawServerIcon(x2, y2, UNKNOWN_SERVER);
        }
        int i22 = mouseX - x2;
        int j22 = mouseY - y2;
        if (s3 != null) {
            if (i22 >= listWidth - 15 && i22 <= listWidth - 5 && j22 >= 0 && j22 <= 8 && !s4.isEmpty()) {
                TooltipHelper.getHelper().pointTooltip(mouseX, mouseY, 0L, s4.split("\n"));
            } else if (i22 >= listWidth - j2 - 15 - 2 && i22 <= listWidth - 15 - 2 && j22 >= 0 && j22 <= 8 && !s3.isEmpty()) {
                TooltipHelper.getHelper().pointTooltip(mouseX, mouseY, 0L, s3.split("\n"));
            }
        }
    }

    public boolean isClientOutOfDate() {
        return this.serverData.getVersion() > Source.ABOUT_MC_PROTOCOL_VERSION;
    }

    public boolean isServerOutOfDate() {
        return this.serverData.getVersion() < Source.ABOUT_MC_PROTOCOL_VERSION;
    }

    public boolean drawJoinServerButton(int x2, int y2, int listWidth, int slotHeight, int mouseX, int mouseY) {
        if (mouseX > x2 && mouseX < x2 + listWidth && mouseY > y2 && mouseY < y2 + slotHeight) {
            this.mc.getTextureManager().bindTexture(SERVER_SELECTION_BUTTONS);
            Gui.drawRect(x2, y2, x2 + 32, y2 + 32, -1601138544);
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            int k1 = mouseX - x2;
            if (k1 < 32 && k1 > 16) {
                Gui.drawModalRectWithCustomSizedTexture(x2, y2, 0.0f, 32.0f, 32, 32, 256.0f, 256.0f);
                return true;
            }
            Gui.drawModalRectWithCustomSizedTexture(x2, y2, 0.0f, 0.0f, 32, 32, 256.0f, 256.0f);
        }
        return false;
    }

    public boolean drawSaveServerButton(int x2, int y2, int listWidth, int slotHeight, int mouseX, int mouseY) {
        if (mouseX > x2 && mouseX < x2 + listWidth && mouseY > y2 && mouseY < y2 + slotHeight) {
            this.mc.getTextureManager().bindTexture(SERVER_SELECTION_BUTTONS);
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            int k1 = mouseX - x2;
            int l1 = mouseY - y2;
            if (k1 < 16 && l1 > 16) {
                Gui.drawModalRectWithCustomSizedTexture(x2, y2, 64.0f, 32.0f, 32, 32, 256.0f, 256.0f);
                return true;
            }
            Gui.drawModalRectWithCustomSizedTexture(x2, y2, 64.0f, 0.0f, 32, 32, 256.0f, 256.0f);
        }
        return false;
    }

    protected void drawServerIcon(int posX, int posY, ResourceLocation resourceLocation) {
        this.mc.getTextureManager().bindTexture(resourceLocation);
        GlStateManager.enableBlend();
        Gui.drawModalRectWithCustomSizedTexture(posX, posY, 0.0f, 0.0f, 32, 32, 32.0f, 32.0f);
        GlStateManager.disableBlend();
    }

    private void prepareServerIcon() {
        if (this.serverData.getBase64EncodedIconData() == null) {
            this.mc.getTextureManager().deleteTexture(this.serverIcon);
            this.dynamicTexture = null;
        } else {
            BufferedImage bufferedimage;
            block8: {
                ByteBuf bytebuf = Unpooled.copiedBuffer(this.serverData.getBase64EncodedIconData(), Charsets.UTF_8);
                ByteBuf bytebuf2 = Base64.decode(bytebuf);
                bufferedimage = null;
                try {
                    bufferedimage = TextureUtil.readBufferedImage(new ByteBufInputStream(bytebuf2));
                    Validate.validState(bufferedimage.getWidth() == 64, "Must be 64 pixels wide", new Object[0]);
                    Validate.validState(bufferedimage.getHeight() == 64, "Must be 64 pixels high", new Object[0]);
                    break block8;
                }
                catch (Throwable throwable) {
                    this.serverData.setBase64EncodedIconData(null);
                }
                finally {
                    bytebuf.release();
                    bytebuf2.release();
                }
                return;
            }
            if (this.dynamicTexture == null) {
                this.dynamicTexture = new DynamicTexture(bufferedimage.getWidth(), bufferedimage.getHeight());
                this.mc.getTextureManager().loadTexture(this.serverIcon, this.dynamicTexture);
            }
            bufferedimage.getRGB(0, 0, bufferedimage.getWidth(), bufferedimage.getHeight(), this.dynamicTexture.getTextureData(), 0, bufferedimage.getWidth());
            this.dynamicTexture.updateDynamicTexture();
        }
    }

    public boolean canReachServer() {
        return this.canReachServer && this.serverData != null && !this.serverData.isPinging();
    }

    public ServerPingerData getServerData() {
        return this.serverData;
    }

    public ResourceLocation getServerIcon() {
        return this.serverIcon;
    }

    public boolean isHidden() {
        return this.hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public ServerData getLabymodServerData() {
        return this.labymodServerData;
    }

    public void setLabymodServerData(ServerData labymodServerData) {
        this.labymodServerData = labymodServerData;
    }

    public int getIndex() {
        return this.index;
    }

    public ServerInfoRenderer setIndex(int index) {
        this.index = index;
        return this;
    }
}

