/*
 * Decompiled with CFR 0.152.
 */
package wdl.gui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import wdl.WDL;
import wdl.gui.GuiTurningCameraBase;
import wdl.gui.Utils;

public class GuiWDLMultiworldSelect
extends GuiTurningCameraBase {
    private final WorldSelectionCallback callback;
    private final String title;
    private GuiButton cancelBtn;
    private GuiButton acceptBtn;
    private GuiTextField newNameField;
    private GuiTextField searchField;
    private GuiButton newWorldButton;
    private boolean showNewWorldTextBox;
    private List<MultiworldInfo> linkedWorlds;
    private List<MultiworldInfo> linkedWorldsFiltered;
    private MultiworldInfo selectedMultiWorld;
    private int index = 0;
    private GuiButton nextButton;
    private GuiButton prevButton;
    private int numWorldButtons;
    private String searchText = "";

    public GuiWDLMultiworldSelect(String title, WorldSelectionCallback callback) {
        this.title = title;
        this.callback = callback;
        String[] worldNames = WDL.baseProps.getProperty("LinkedWorlds").split("\\|");
        this.linkedWorlds = new ArrayList<MultiworldInfo>();
        String[] stringArray = worldNames;
        int n2 = worldNames.length;
        int n3 = 0;
        while (n3 < n2) {
            Properties props;
            String worldName = stringArray[n3];
            if (worldName != null && !worldName.isEmpty() && (props = WDL.loadWorldProps(worldName)).containsKey("WorldName")) {
                String displayName = props.getProperty("WorldName", worldName);
                this.linkedWorlds.add(new MultiworldInfo(worldName, displayName));
            }
            ++n3;
        }
        this.linkedWorldsFiltered = new ArrayList<MultiworldInfo>();
        this.linkedWorldsFiltered.addAll(this.linkedWorlds);
    }

    @Override
    public void initGui() {
        super.initGui();
        this.numWorldButtons = (width - 50) / 155;
        if (this.numWorldButtons < 1) {
            this.numWorldButtons = 1;
        }
        int offset = (this.numWorldButtons * 155 + 45) / 2;
        int y2 = height - 49;
        this.cancelBtn = new GuiButton(-1, width / 2 - 155, height - 25, 150, 20, I18n.format("gui.cancel", new Object[0]));
        this.buttonList.add(this.cancelBtn);
        this.acceptBtn = new GuiButton(-2, width / 2 + 5, height - 25, 150, 20, I18n.format("wdl.gui.multiworldSelect.done", new Object[0]));
        this.acceptBtn.enabled = this.selectedMultiWorld != null;
        this.buttonList.add(this.acceptBtn);
        this.prevButton = new GuiButton(-4, width / 2 - offset, y2, 20, 20, "<");
        this.buttonList.add(this.prevButton);
        int i2 = 0;
        while (i2 < this.numWorldButtons) {
            this.buttonList.add(new WorldGuiButton(i2, width / 2 - offset + i2 * 155 + 25, y2, 150, 20));
            ++i2;
        }
        this.nextButton = new GuiButton(-5, width / 2 - offset + 25 + this.numWorldButtons * 155, y2, 20, 20, ">");
        this.buttonList.add(this.nextButton);
        this.newWorldButton = new GuiButton(-3, width / 2 - 155, 29, 150, 20, I18n.format("wdl.gui.multiworldSelect.newName", new Object[0]));
        this.buttonList.add(this.newWorldButton);
        this.newNameField = new GuiTextField(40, this.fontRendererObj, width / 2 - 155, 29, 150, 20);
        this.searchField = new GuiTextField(41, this.fontRendererObj, width / 2 + 5, 29, 150, 20);
        this.searchField.setText(this.searchText);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.enabled) {
            if (button instanceof WorldGuiButton) {
                this.selectedMultiWorld = ((WorldGuiButton)button).getWorldInfo();
                this.acceptBtn.enabled = this.selectedMultiWorld != null;
            } else if (button.id == -1) {
                this.callback.onCancel();
            } else if (button.id == -2) {
                this.callback.onWorldSelected(this.selectedMultiWorld.folderName);
            } else if (button.id == -3) {
                this.showNewWorldTextBox = true;
            } else if (button.id == -4) {
                --this.index;
            } else if (button.id == -5) {
                ++this.index;
            }
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (this.showNewWorldTextBox) {
            this.newNameField.mouseClicked(mouseX, mouseY, mouseButton);
        }
        this.searchField.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == 1) {
            this.callback.onCancel();
        }
        super.keyTyped(typedChar, keyCode);
        if (this.showNewWorldTextBox) {
            String newName;
            this.newNameField.textboxKeyTyped(typedChar, keyCode);
            if (keyCode == 28 && (newName = this.newNameField.getText()) != null && !newName.isEmpty()) {
                this.addMultiworld(newName);
                this.newNameField.setText("");
                this.showNewWorldTextBox = false;
            }
        }
        if (this.searchField.textboxKeyTyped(typedChar, keyCode)) {
            this.searchText = this.searchField.getText();
            this.rebuildFilteredWorlds();
        }
    }

    @Override
    public void updateScreen() {
        this.newNameField.updateCursorCounter();
        this.searchField.updateCursorCounter();
        super.updateScreen();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (this.index >= this.linkedWorlds.size() - this.numWorldButtons) {
            this.index = this.linkedWorlds.size() - this.numWorldButtons;
            this.nextButton.enabled = false;
        } else {
            this.nextButton.enabled = true;
        }
        if (this.index <= 0) {
            this.index = 0;
            this.prevButton.enabled = false;
        } else {
            this.prevButton.enabled = true;
        }
        Utils.drawBorder(53, 53, 0, 0, height, width);
        GuiWDLMultiworldSelect.drawCenteredString(this.fontRendererObj, this.title, width / 2, 8, 0xFFFFFF);
        GuiWDLMultiworldSelect.drawCenteredString(this.fontRendererObj, I18n.format("wdl.gui.multiworldSelect.subtitle", new Object[0]), width / 2, 18, 0xFF0000);
        if (this.showNewWorldTextBox) {
            this.newNameField.drawTextBox();
        }
        this.searchField.drawTextBox();
        if (this.searchField.getText().isEmpty() && !this.searchField.isFocused()) {
            this.drawString(this.fontRendererObj, I18n.format("wdl.gui.multiworldSelect.search", new Object[0]), this.searchField.xPosition + 4, this.searchField.yPosition + 6, 0x909090);
        }
        this.newWorldButton.visible = !this.showNewWorldTextBox;
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.drawMultiworldDescription();
    }

    private void addMultiworld(String worldName) {
        char[] unsafeChars;
        String folderName = worldName;
        char[] cArray = unsafeChars = "\\/:*?\"<>|.".toCharArray();
        int n2 = unsafeChars.length;
        int n3 = 0;
        while (n3 < n2) {
            char unsafeChar = cArray[n3];
            folderName = folderName.replace(unsafeChar, '_');
            ++n3;
        }
        Properties worldProps = new Properties(WDL.baseProps);
        worldProps.setProperty("WorldName", worldName);
        String linkedWorldsProp = WDL.baseProps.getProperty("LinkedWorlds");
        linkedWorldsProp = String.valueOf(linkedWorldsProp) + "|" + folderName;
        WDL.baseProps.setProperty("LinkedWorlds", linkedWorldsProp);
        WDL.saveProps(folderName, worldProps);
        this.linkedWorlds.add(new MultiworldInfo(folderName, worldName));
        this.rebuildFilteredWorlds();
    }

    private void rebuildFilteredWorlds() {
        String searchFilter = this.searchText.toLowerCase();
        this.linkedWorldsFiltered.clear();
        for (MultiworldInfo info : this.linkedWorlds) {
            if (!info.displayName.toLowerCase().contains(searchFilter)) continue;
            this.linkedWorldsFiltered.add(info);
        }
    }

    private void drawMultiworldDescription() {
        if (this.selectedMultiWorld == null) {
            return;
        }
        String title = "Info about " + this.selectedMultiWorld.displayName;
        List<String> description = this.selectedMultiWorld.getDescription();
        int maxWidth = this.fontRendererObj.getStringWidth(title);
        for (String line : description) {
            int width = this.fontRendererObj.getStringWidth(line);
            if (width <= maxWidth) continue;
            maxWidth = width;
        }
        GuiWDLMultiworldSelect.drawRect(2, 61, 5 + maxWidth + 3, height - 61, Integer.MIN_VALUE);
        this.drawString(this.fontRendererObj, title, 5, 64, 0xFFFFFF);
        int y2 = 64 + this.fontRendererObj.FONT_HEIGHT;
        for (String s2 : description) {
            this.drawString(this.fontRendererObj, s2, 5, y2, 0xFFFFFF);
            y2 += this.fontRendererObj.FONT_HEIGHT;
        }
    }

    private static class MultiworldInfo {
        public final String folderName;
        public final String displayName;
        private List<String> description;

        public MultiworldInfo(String folderName, String displayName) {
            this.folderName = folderName;
            this.displayName = displayName;
        }

        public List<String> getDescription() {
            if (this.description == null) {
                this.description = new ArrayList<String>();
                this.description.add("Defined dimensions:");
                File savesFolder = new File(WDL.minecraft.mcDataDir, "saves");
                File world = new File(savesFolder, WDL.getWorldFolderName(this.folderName));
                File[] subfolders = world.listFiles();
                if (subfolders != null) {
                    File[] fileArray = subfolders;
                    int n2 = subfolders.length;
                    int n3 = 0;
                    while (n3 < n2) {
                        File subfolder = fileArray[n3];
                        if (subfolder.listFiles() != null && subfolder.listFiles().length != 0) {
                            if (subfolder.getName().equals("region")) {
                                this.description.add(" * Overworld (#0)");
                            } else if (subfolder.getName().startsWith("DIM")) {
                                String dimension = subfolder.getName().substring(3);
                                if (dimension.equals("-1")) {
                                    this.description.add(" * Nether (#-1)");
                                } else if (dimension.equals("1")) {
                                    this.description.add(" * The End (#1)");
                                } else {
                                    this.description.add(" * #" + dimension);
                                }
                            }
                        }
                        ++n3;
                    }
                }
            }
            return this.description;
        }
    }

    private class WorldGuiButton
    extends GuiButton {
        public WorldGuiButton(int offset, int x2, int y2, int width, int height) {
            super(offset, x2, y2, width, height, "");
        }

        public WorldGuiButton(int offset, int x2, int y2, String worldName, String displayName) {
            super(offset, x2, y2, "");
        }

        @Override
        public void drawButton(Minecraft mc2, int mouseX, int mouseY) {
            MultiworldInfo info = this.getWorldInfo();
            if (info == null) {
                this.displayString = "";
                this.enabled = false;
            } else {
                this.displayString = info.displayName;
                this.enabled = true;
            }
            if (info != null && info == GuiWDLMultiworldSelect.this.selectedMultiWorld) {
                WorldGuiButton.drawRect(this.xPosition - 2, this.yPosition - 2, this.xPosition + this.width + 2, this.yPosition + this.height + 2, -16744704);
            }
            super.drawButton(mc2, mouseX, mouseY);
        }

        public MultiworldInfo getWorldInfo() {
            int location = GuiWDLMultiworldSelect.this.index + this.id;
            if (location < 0) {
                return null;
            }
            if (location >= GuiWDLMultiworldSelect.this.linkedWorldsFiltered.size()) {
                return null;
            }
            return (MultiworldInfo)GuiWDLMultiworldSelect.this.linkedWorldsFiltered.get(location);
        }
    }

    public static interface WorldSelectionCallback {
        public void onCancel();

        public void onWorldSelected(String var1);
    }
}

