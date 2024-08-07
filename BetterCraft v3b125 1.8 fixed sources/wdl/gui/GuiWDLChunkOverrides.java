/*
 * Decompiled with CFR 0.152.
 */
package wdl.gui;

import com.google.common.collect.Multimap;
import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import wdl.WDL;
import wdl.WDLPluginChannels;
import wdl.gui.GuiWDLPermissionRequest;
import wdl.gui.GuiWDLPermissions;
import wdl.gui.Utils;

public class GuiWDLChunkOverrides
extends GuiScreen {
    private static final int TOP_MARGIN = 61;
    private static final int BOTTOM_MARGIN = 32;
    private static final ResourceLocation WIDGET_TEXTURES = new ResourceLocation("wdl:textures/permission_widgets.png");
    private final GuiScreen parent;
    private GuiButton startDownloadButton;
    private float scrollX;
    private float scrollZ;
    private static final int SCALE = 8;
    private Mode mode = Mode.PANNING;
    private boolean partiallyRequested;
    private int requestStartX;
    private int requestStartZ;
    private int requestEndX;
    private int requestEndZ;
    private boolean dragging;
    private int lastTickX;
    private int lastTickY;
    private static final int RNG_SEED = 769532;

    public GuiWDLChunkOverrides(GuiScreen parent) {
        this.parent = parent;
        if (WDL.thePlayer != null) {
            this.scrollX = WDL.thePlayer.chunkCoordX;
            this.scrollZ = WDL.thePlayer.chunkCoordZ;
        }
    }

    @Override
    public void initGui() {
        this.buttonList.add(new RequestModeButton(0, width / 2 - 155, 18, Mode.PANNING));
        this.buttonList.add(new RequestModeButton(1, width / 2 - 130, 18, Mode.REQUESTING));
        this.buttonList.add(new RequestModeButton(this, 2, width / 2 - 105, 18, Mode.ERASING){
            {
                super($anonymous0, $anonymous1, $anonymous2, $anonymous3);
                this.enabled = false;
            }
        });
        this.buttonList.add(new RequestModeButton(this, 3, width / 2 - 80, 18, Mode.MOVING){
            {
                super($anonymous0, $anonymous1, $anonymous2, $anonymous3);
                this.enabled = false;
            }
        });
        this.buttonList.add(new GuiButton(4, width / 2 - 80, 18, 80, 20, "Send request"));
        this.startDownloadButton = new GuiButton(6, width / 2 + 5, 18, 150, 20, "Start download in these ranges");
        this.startDownloadButton.enabled = WDLPluginChannels.canDownloadAtAll();
        this.buttonList.add(this.startDownloadButton);
        this.buttonList.add(new GuiButton(100, width / 2 - 100, height - 29, I18n.format("gui.done", new Object[0])));
        this.buttonList.add(new GuiButton(200, width / 2 - 155, 39, 100, 20, I18n.format("wdl.gui.permissions.current", new Object[0])));
        this.buttonList.add(new GuiButton(201, width / 2 - 50, 39, 100, 20, I18n.format("wdl.gui.permissions.request", new Object[0])));
        this.buttonList.add(new GuiButton(202, width / 2 + 55, 39, 100, 20, I18n.format("wdl.gui.permissions.overrides", new Object[0])));
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 0) {
            this.mode = Mode.PANNING;
        }
        if (button.id == 1) {
            this.mode = Mode.REQUESTING;
            this.partiallyRequested = false;
        }
        if (button.id == 4) {
            WDLPluginChannels.sendRequests();
        }
        if (button.id == 6) {
            if (!WDLPluginChannels.canDownloadAtAll()) {
                button.enabled = false;
                return;
            }
            WDL.startDownload();
        }
        if (button.id == 100) {
            this.mc.displayGuiScreen(this.parent);
        }
        if (button.id == 200) {
            this.mc.displayGuiScreen(new GuiWDLPermissions(this.parent));
        }
        if (button.id == 201) {
            this.mc.displayGuiScreen(new GuiWDLPermissionRequest(this.parent));
        }
        int cfr_ignored_0 = button.id;
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (mouseY > 61 && mouseY < height - 32 && mouseButton == 0) {
            switch (this.mode) {
                case PANNING: {
                    this.dragging = true;
                    this.lastTickX = mouseX;
                    this.lastTickY = mouseY;
                    break;
                }
                case REQUESTING: {
                    if (this.partiallyRequested) {
                        this.requestEndX = this.displayXToChunkX(mouseX);
                        this.requestEndZ = this.displayZToChunkZ(mouseY);
                        WDLPluginChannels.ChunkRange requestRange = new WDLPluginChannels.ChunkRange("", this.requestStartX, this.requestStartZ, this.requestEndX, this.requestEndZ);
                        WDLPluginChannels.addChunkOverrideRequest(requestRange);
                        this.partiallyRequested = false;
                    } else {
                        this.requestStartX = this.displayXToChunkX(mouseX);
                        this.requestStartZ = this.displayZToChunkZ(mouseY);
                        this.partiallyRequested = true;
                    }
                    this.mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0f));
                }
            }
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        if (state == 0) {
            this.dragging = false;
        }
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        if (this.dragging) {
            int deltaX = this.lastTickX - mouseX;
            int deltaY = this.lastTickY - mouseY;
            this.lastTickX = mouseX;
            this.lastTickY = mouseY;
            this.scrollX += (float)deltaX / 8.0f;
            this.scrollZ += (float)deltaY / 8.0f;
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        Utils.drawDarkBackground(0, 0, height, width);
        if (this.mode == Mode.REQUESTING) {
            int x1 = this.partiallyRequested ? this.requestStartX : this.displayXToChunkX(mouseX);
            int z1 = this.partiallyRequested ? this.requestStartZ : this.displayZToChunkZ(mouseY);
            int x2 = this.displayXToChunkX(mouseX);
            int z2 = this.displayZToChunkZ(mouseY);
            WDLPluginChannels.ChunkRange requestRange = new WDLPluginChannels.ChunkRange("", x1, z1, x2, z2);
            int alpha = 127 + (int)(Math.sin((double)Minecraft.getSystemTime() * Math.PI / 5000.0) * 64.0);
            this.drawRange(requestRange, 0xFFFFFF, alpha);
        }
        for (Multimap<String, WDLPluginChannels.ChunkRange> group : WDLPluginChannels.getChunkOverrides().values()) {
            for (WDLPluginChannels.ChunkRange range : group.values()) {
                this.drawRange(range, 769532, 255);
            }
        }
        for (WDLPluginChannels.ChunkRange range : WDLPluginChannels.getChunkOverrideRequests()) {
            int alpha = 127 + (int)(Math.sin((double)Minecraft.getSystemTime() * Math.PI / 5000.0) * 64.0);
            this.drawRange(range, 0x808080, alpha);
        }
        int playerPosX = (int)((WDL.thePlayer.posX / 16.0 - (double)this.scrollX) * 8.0 + (double)(width / 2));
        int playerPosZ = (int)((WDL.thePlayer.posZ / 16.0 - (double)this.scrollZ) * 8.0 + (double)(height / 2));
        this.drawHorizontalLine(playerPosX - 3, playerPosX + 3, playerPosZ, -1);
        this.drawVerticalLine(playerPosX, playerPosZ - 4, playerPosZ + 4, -1);
        Utils.drawBorder(61, 32, 0, 0, height, width);
        GuiWDLChunkOverrides.drawCenteredString(this.fontRendererObj, "Chunk overrides", width / 2, 8, 0xFFFFFF);
        super.drawScreen(mouseX, mouseY, partialTicks);
        GuiWDLChunkOverrides.drawCenteredString(this.fontRendererObj, "\u00a7c\u00a7lThis is a work in progress.", width / 2, height / 2, 0xFFFFFF);
    }

    private void drawRange(WDLPluginChannels.ChunkRange range, int seed, int alpha) {
        int color = (range.tag.hashCode() ^ seed) & 0xFFFFFF;
        int x1 = this.chunkXToDisplayX(range.x1);
        int z1 = this.chunkZToDisplayZ(range.z1);
        int x2 = this.chunkXToDisplayX(range.x2) + 8 - 1;
        int z2 = this.chunkZToDisplayZ(range.z2) + 8 - 1;
        GuiWDLChunkOverrides.drawRect(x1, z1, x2, z2, color + (alpha << 24));
        int colorDark = this.darken(color);
        this.drawVerticalLine(x1, z1, z2, colorDark + (alpha << 24));
        this.drawVerticalLine(x2, z1, z2, colorDark + (alpha << 24));
        this.drawHorizontalLine(x1, x2, z1, colorDark + (alpha << 24));
        this.drawHorizontalLine(x1, x2, z2, colorDark + (alpha << 24));
    }

    private int chunkXToDisplayX(int chunkX) {
        return (int)(((float)chunkX - this.scrollX) * 8.0f + (float)(width / 2));
    }

    private int chunkZToDisplayZ(int chunkZ) {
        return (int)(((float)chunkZ - this.scrollZ) * 8.0f + (float)(height / 2));
    }

    private int displayXToChunkX(int displayX) {
        return MathHelper.floor_float(((float)displayX - (float)(width / 2)) / 8.0f + this.scrollX);
    }

    private int displayZToChunkZ(int displayZ) {
        return MathHelper.floor_float(((float)displayZ - (float)(height / 2)) / 8.0f + this.scrollZ);
    }

    private int darken(int color) {
        int r2 = color >> 16 & 0xFF;
        int g2 = color >> 8 & 0xFF;
        int b2 = color & 0xFF;
        return ((r2 /= 2) << 16) + ((g2 /= 2) << 8) + (b2 /= 2);
    }

    private static enum Mode {
        PANNING(0, 128),
        REQUESTING(16, 128),
        ERASING(32, 128),
        MOVING(48, 128);

        public final int overlayU;
        public final int overlayV;

        private Mode(int overlayU, int overlayV) {
            this.overlayU = overlayU;
            this.overlayV = overlayV;
        }
    }

    private class RequestModeButton
    extends GuiButton {
        public final Mode mode;

        public RequestModeButton(int buttonId, int x2, int y2, Mode mode) {
            super(buttonId, x2, y2, 20, 20, "");
            this.mode = mode;
        }

        @Override
        public void drawButton(Minecraft mc2, int mouseX, int mouseY) {
            if (GuiWDLChunkOverrides.this.mode == this.mode) {
                RequestModeButton.drawRect(this.xPosition - 2, this.yPosition - 2, this.xPosition + this.width + 2, this.yPosition + this.height + 2, -16744704);
            }
            super.drawButton(mc2, mouseX, mouseY);
            GlStateManager.color(1.0f, 1.0f, 1.0f);
            mc2.getTextureManager().bindTexture(WIDGET_TEXTURES);
            this.drawTexturedModalRect(this.xPosition + 2, this.yPosition + 2, this.mode.overlayU, this.mode.overlayV, 16, 16);
        }
    }
}

