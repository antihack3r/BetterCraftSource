/*
 * Decompiled with CFR 0.152.
 */
package wdl.gui;

import java.io.IOException;
import java.util.Map;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import wdl.WDLPluginChannels;
import wdl.gui.GuiWDLChunkOverrides;
import wdl.gui.GuiWDLPermissions;
import wdl.gui.TextList;

public class GuiWDLPermissionRequest
extends GuiScreen {
    private static final int TOP_MARGIN = 61;
    private static final int BOTTOM_MARGIN = 32;
    private TextList list;
    private final GuiScreen parent;
    private GuiTextField requestField;
    private GuiButton submitButton;

    public GuiWDLPermissionRequest(GuiScreen parent) {
        this.parent = parent;
    }

    @Override
    public void initGui() {
        this.list = new TextList(this.mc, width, height, 61, 32);
        this.list.addLine("\u00a7c\u00a7lThis is a work in progress.");
        this.list.addLine("You can request permissions in this GUI, although it currently requires manually specifying the names.");
        this.list.addBlankLine();
        this.list.addLine("Boolean fields: " + WDLPluginChannels.BOOLEAN_REQUEST_FIELDS);
        this.list.addLine("Integer fields: " + WDLPluginChannels.INTEGER_REQUEST_FIELDS);
        this.list.addBlankLine();
        for (Map.Entry<String, String> request : WDLPluginChannels.getRequests().entrySet()) {
            this.list.addLine("Requesting '" + request.getKey() + "' to be '" + request.getValue() + "'.");
        }
        this.requestField = new GuiTextField(0, this.fontRendererObj, width / 2 - 155, 18, 150, 20);
        this.submitButton = new GuiButton(1, width / 2 + 5, 18, 150, 20, "Submit request");
        this.submitButton.enabled = !WDLPluginChannels.getRequests().isEmpty();
        this.buttonList.add(this.submitButton);
        this.buttonList.add(new GuiButton(100, width / 2 - 100, height - 29, I18n.format("gui.done", new Object[0])));
        this.buttonList.add(new GuiButton(200, width / 2 - 155, 39, 100, 20, I18n.format("wdl.gui.permissions.current", new Object[0])));
        this.buttonList.add(new GuiButton(201, width / 2 - 50, 39, 100, 20, I18n.format("wdl.gui.permissions.request", new Object[0])));
        this.buttonList.add(new GuiButton(202, width / 2 + 55, 39, 100, 20, I18n.format("wdl.gui.permissions.overrides", new Object[0])));
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 1) {
            WDLPluginChannels.sendRequests();
            button.displayString = "Submitted!";
        }
        if (button.id == 100) {
            this.mc.displayGuiScreen(this.parent);
        }
        if (button.id == 200) {
            this.mc.displayGuiScreen(new GuiWDLPermissions(this.parent));
        }
        int cfr_ignored_0 = button.id;
        if (button.id == 202) {
            this.mc.displayGuiScreen(new GuiWDLChunkOverrides(this.parent));
        }
    }

    @Override
    public void updateScreen() {
        this.requestField.updateCursorCounter();
        super.updateScreen();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        this.requestField.mouseClicked(mouseX, mouseY, mouseButton);
        this.list.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        this.requestField.textboxKeyTyped(typedChar, keyCode);
        if (this.requestField.isFocused()) {
            String value;
            String key;
            String[] requestData;
            String request = this.requestField.getText();
            boolean isValid = false;
            if (request.contains("=") && (requestData = request.split("=", 2)).length == 2 && (isValid = WDLPluginChannels.isValidRequest(key = requestData[0], value = requestData[1])) && keyCode == 28) {
                this.requestField.setText("");
                isValid = false;
                WDLPluginChannels.addRequest(key, value);
                this.list.addLine("Requesting '" + key + "' to be '" + value + "'.");
                this.submitButton.enabled = true;
            }
            this.requestField.setTextColor(isValid ? 0x40E040 : 0xE04040);
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        this.list.handleMouseInput();
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        if (this.list.mouseReleased(mouseX, mouseY, state)) {
            return;
        }
        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (this.list == null) {
            return;
        }
        this.list.drawScreen(mouseX, mouseY, partialTicks);
        this.requestField.drawTextBox();
        GuiWDLPermissionRequest.drawCenteredString(this.fontRendererObj, "Permission request", width / 2, 8, 0xFFFFFF);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}

