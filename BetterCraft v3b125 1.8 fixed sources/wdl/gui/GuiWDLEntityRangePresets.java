/*
 * Decompiled with CFR 0.152.
 */
package wdl.gui;

import java.io.IOException;
import java.util.Set;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.resources.I18n;
import wdl.EntityUtils;
import wdl.WDL;
import wdl.WDLPluginChannels;
import wdl.gui.Utils;

public class GuiWDLEntityRangePresets
extends GuiScreen
implements GuiYesNoCallback {
    private final GuiScreen parent;
    private GuiButton vanillaButton;
    private GuiButton spigotButton;
    private GuiButton serverButton;
    private GuiButton cancelButton;

    public GuiWDLEntityRangePresets(GuiScreen parent) {
        this.parent = parent;
    }

    @Override
    public void initGui() {
        int y2 = height / 4;
        this.vanillaButton = new GuiButton(0, width / 2 - 100, y2, I18n.format("wdl.gui.rangePresets.vanilla", new Object[0]));
        this.spigotButton = new GuiButton(1, width / 2 - 100, y2 += 22, I18n.format("wdl.gui.rangePresets.spigot", new Object[0]));
        this.serverButton = new GuiButton(2, width / 2 - 100, y2 += 22, I18n.format("wdl.gui.rangePresets.server", new Object[0]));
        this.serverButton.enabled = WDLPluginChannels.hasServerEntityRange();
        this.buttonList.add(this.vanillaButton);
        this.buttonList.add(this.spigotButton);
        this.buttonList.add(this.serverButton);
        y2 += 28;
        this.cancelButton = new GuiButton(100, width / 2 - 100, height - 29, I18n.format("gui.cancel", new Object[0]));
        this.buttonList.add(this.cancelButton);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (!button.enabled) {
            return;
        }
        if (button.id < 3) {
            String lower;
            String upper = I18n.format("wdl.gui.rangePresets.upperWarning", new Object[0]);
            if (button.id == 0) {
                lower = I18n.format("wdl.gui.rangePresets.vanilla.warning", new Object[0]);
            } else if (button.id == 1) {
                lower = I18n.format("wdl.gui.rangePresets.spigot.warning", new Object[0]);
            } else if (button.id == 2) {
                lower = I18n.format("wdl.gui.rangePresets.server.warning", new Object[0]);
            } else {
                throw new Error("Button.id should never be negative.");
            }
            this.mc.displayGuiScreen(new GuiYesNo(this, upper, lower, button.id));
        }
        if (button.id == 100) {
            this.mc.displayGuiScreen(this.parent);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        Utils.drawListBackground(23, 32, 0, 0, height, width);
        GuiWDLEntityRangePresets.drawCenteredString(this.fontRendererObj, I18n.format("wdl.gui.rangePresets.title", new Object[0]), width / 2, 8, 0xFFFFFF);
        String infoText = null;
        if (this.vanillaButton.isMouseOver()) {
            infoText = I18n.format("wdl.gui.rangePresets.vanilla.description", new Object[0]);
        } else if (this.spigotButton.isMouseOver()) {
            infoText = I18n.format("wdl.gui.rangePresets.spigot.description", new Object[0]);
        } else if (this.serverButton.isMouseOver()) {
            infoText = String.valueOf(I18n.format("wdl.gui.rangePresets.server.description", new Object[0])) + "\n\n";
            infoText = this.serverButton.enabled ? String.valueOf(infoText) + I18n.format("wdl.gui.rangePresets.server.installed", new Object[0]) : String.valueOf(infoText) + I18n.format("wdl.gui.rangePresets.server.notInstalled", new Object[0]);
        } else if (this.cancelButton.isMouseOver()) {
            infoText = I18n.format("wdl.gui.rangePresets.cancel.description", new Object[0]);
        }
        if (infoText != null) {
            Utils.drawGuiInfoBox(infoText, width, height, 48);
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void confirmClicked(boolean result, int id2) {
        if (result) {
            Set<String> entities = EntityUtils.getEntityTypes();
            if (id2 == 0) {
                for (String entity : entities) {
                    WDL.worldProps.setProperty("Entity." + entity + ".TrackDistance", Integer.toString(EntityUtils.getDefaultEntityRange(entity)));
                }
            } else if (id2 == 1) {
                for (String entity : entities) {
                    Class<?> c2 = EntityUtils.stringToClassMapping.get(entity);
                    if (c2 == null) continue;
                    WDL.worldProps.setProperty("Entity." + entity + ".TrackDistance", Integer.toString(EntityUtils.getDefaultSpigotEntityRange(c2)));
                }
            } else if (id2 == 2) {
                for (String entity : entities) {
                    WDL.worldProps.setProperty("Entity." + entity + ".TrackDistance", Integer.toString(WDLPluginChannels.getEntityRange(entity)));
                }
            }
        }
        this.mc.displayGuiScreen(this.parent);
    }

    @Override
    public void onGuiClosed() {
        WDL.saveProps();
    }
}

