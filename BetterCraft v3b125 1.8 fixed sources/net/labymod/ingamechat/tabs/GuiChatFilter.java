/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.ingamechat.tabs;

import java.awt.Color;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.labymod.core.LabyModCore;
import net.labymod.gui.elements.Scrollbar;
import net.labymod.ingamechat.GuiChatCustom;
import net.labymod.ingamechat.tools.filter.FilterChatManager;
import net.labymod.ingamechat.tools.filter.Filters;
import net.labymod.main.LabyMod;
import net.labymod.main.lang.LanguageManager;
import net.labymod.utils.ModColor;
import net.labymod.utils.ReflectionHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.audio.SoundRegistry;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;

public class GuiChatFilter
extends GuiChatCustom {
    private Scrollbar scrollbar = new Scrollbar(15);
    private Filters.Filter selectedFilter;
    private GuiTextField textFieldFilterName;
    private GuiTextField textFieldFilterContains;
    private GuiTextField textFieldFilterContainsNot;
    private GuiTextField textFieldFilterSoundfile;
    private GuiTextField textFieldFilterRoom;
    private int sliderDrag = -1;
    private boolean markFilterNameRed = false;
    private boolean markContainsRed = false;
    private boolean markSoundNameRed = false;
    private String editStartName = "";
    private boolean canScroll;
    private static List<String> soundNames = new ArrayList<String>();

    static {
        try {
            Field soundRegistryInSoundHandlerField = ReflectionHelper.findField(SoundHandler.class, LabyModCore.getMappingAdapter().getSoundRegistryInSoundHandlerMappings());
            Field soundRegistryInSoundRegistryField = ReflectionHelper.findField(SoundRegistry.class, LabyModCore.getMappingAdapter().getSoundRegistryInSoundRegistryMappings());
            SoundRegistry soundRegistry = (SoundRegistry)soundRegistryInSoundHandlerField.get(Minecraft.getMinecraft().getSoundHandler());
            Map sounds = (Map)soundRegistryInSoundRegistryField.get(soundRegistry);
            for (Object resourceObject : sounds.keySet()) {
                soundNames.add(((ResourceLocation)resourceObject).getResourcePath());
            }
        }
        catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    public GuiChatFilter(String defaultText) {
        super(defaultText);
    }

    @Override
    public void initGui() {
        super.initGui();
        this.scrollbar.setPosition(width - 6, height - 196, width - 5, height - 20);
        this.scrollbar.update(LabyMod.getInstance().getChatToolManager().getFilters().size());
        this.scrollbar.setSpeed(10);
        this.scrollbar.setEntryHeight(10.0);
        this.textFieldFilterName = new GuiTextField(0, LabyModCore.getMinecraft().getFontRenderer(), 0, 0, 110, 10);
        this.textFieldFilterContains = new GuiTextField(0, LabyModCore.getMinecraft().getFontRenderer(), 0, 0, 110, 10);
        this.textFieldFilterContainsNot = new GuiTextField(0, LabyModCore.getMinecraft().getFontRenderer(), 0, 0, 110, 10);
        this.textFieldFilterSoundfile = new GuiTextField(0, LabyModCore.getMinecraft().getFontRenderer(), 0, 0, 110, 10);
        this.textFieldFilterRoom = new GuiTextField(0, LabyModCore.getMinecraft().getFontRenderer(), 0, 0, 110, 10);
        this.markContainsRed = false;
        this.markFilterNameRed = false;
        this.markSoundNameRed = false;
        this.selectedFilter = null;
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        if (this.canScroll) {
            this.scrollbar.mouseInput();
            int i2 = Mouse.getEventDWheel();
            if (i2 != 0) {
                this.mc.ingameGUI.getChatGUI().resetScroll();
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.scrollbar.calc();
        GuiChatFilter.drawRect(width - 150, height - 220, width - 2, height - 16, Integer.MIN_VALUE);
        this.canScroll = mouseX > width - 150 && mouseX < width - 2 && mouseY > height - 150 && mouseY < height - 16;
        int row = 0;
        for (Filters.Filter component : LabyMod.getInstance().getChatToolManager().getFilters()) {
            boolean hover;
            double posY = (double)(height - 215 + row * 10) + this.scrollbar.getScrollY();
            ++row;
            if (!(posY >= (double)(height - 220)) || posY > (double)(height - 25)) continue;
            boolean bl2 = hover = this.selectedFilter == null && mouseX > width - 150 + 1 && mouseX < width - 2 - 1 && (double)mouseY > posY - 1.0 && (double)mouseY < posY + 9.0;
            if (hover || this.selectedFilter != null && (this.selectedFilter.getFilterName().equalsIgnoreCase(component.getFilterName()) || component.getFilterName().equalsIgnoreCase(this.editStartName))) {
                GuiChatFilter.drawRect(width - 150 + 1, (int)posY - 1, width - 2 - 1, (int)posY + 9, hover ? ModColor.toRGB(100, 200, 200, 100) : Integer.MAX_VALUE);
            }
            this.drawString(LabyModCore.getMinecraft().getFontRenderer(), LabyMod.getInstance().getDrawUtils().trimStringToWidth(component.getFilterName(), 130), width - 145, (int)posY, Integer.MAX_VALUE);
            if (this.selectedFilter != null) continue;
            boolean hoverX = mouseX > width - 12 - 1 && mouseX < width - 12 + 7 && (double)mouseY > posY && (double)mouseY < posY + 8.0;
            this.drawString(LabyModCore.getMinecraft().getFontRenderer(), String.valueOf(ModColor.cl(hoverX ? "c" : "4")) + "\u2718", width - 12, (int)posY, Integer.MAX_VALUE);
        }
        if (!this.scrollbar.isHidden()) {
            GuiChatFilter.drawRect(width - 6, height - 145, width - 5, height - 20, Integer.MIN_VALUE);
            GuiChatFilter.drawRect(width - 7, (int)this.scrollbar.getTop(), width - 4, (int)(this.scrollbar.getTop() + this.scrollbar.getBarLength()), Integer.MAX_VALUE);
        }
        if (this.selectedFilter == null) {
            boolean hover2 = mouseX > width - 165 && mouseX < width - 152 && mouseY > height - 220 && mouseY < height - 220 + 13;
            GuiChatFilter.drawRect(width - 165, height - 220, width - 152, height - 220 + 13, hover2 ? Integer.MAX_VALUE : Integer.MIN_VALUE);
            GuiChatFilter.drawCenteredString(LabyModCore.getMinecraft().getFontRenderer(), "+", width - 158, height - 217, hover2 ? ModColor.toRGB(50, 220, 120, 210) : Integer.MAX_VALUE);
        } else {
            String hint;
            int count;
            int relYAtSoundHint = 0;
            int relYAtRoomHint = 0;
            GuiChatFilter.drawRect(width - 270, height - 220, width - 152, height - 16, Integer.MIN_VALUE);
            int relX = width - 270;
            int relY = height - 220;
            this.drawElementTextField("name", this.textFieldFilterName, relX, relY, mouseX, mouseY);
            this.drawElementTextField("contains", this.textFieldFilterContains, relX, relY + 23, mouseX, mouseY);
            this.drawElementTextField("contains_not", this.textFieldFilterContainsNot, relX, relY + 46, mouseX, mouseY);
            this.drawElementTextField("room", this.textFieldFilterRoom, relX, relY + 69, mouseX, mouseY);
            relYAtRoomHint = relY + 69;
            this.drawElementCheckBox("play_sound", this.selectedFilter.isPlaySound(), relX, (relY += 23) + 69, mouseX, mouseY);
            if (this.selectedFilter.isPlaySound()) {
                this.drawElementTextField("", this.textFieldFilterSoundfile, relX, relY + 69, mouseX, mouseY);
                relYAtSoundHint = relY + 69;
            } else {
                relY -= 10;
            }
            this.drawElementCheckBox("highlight", this.selectedFilter.isHighlightMessage(), relX, relY + 92, mouseX, mouseY);
            if (this.selectedFilter.isHighlightMessage() && this.selectedFilter.getHighlightColor() != null) {
                this.drawElementSlider(this.selectedFilter.getHighlightColor().getRed(), relX, relY + 92 + 15, 0, mouseX, mouseY);
                this.drawElementSlider(this.selectedFilter.getHighlightColor().getGreen(), relX, relY + 92 + 15 + 9, 1, mouseX, mouseY);
                this.drawElementSlider(this.selectedFilter.getHighlightColor().getBlue(), relX, relY + 92 + 15 + 18, 2, mouseX, mouseY);
                GuiChatFilter.drawRect(relX + 85, relY + 92 + 1, relX + 85 + 9, relY + 92 + 1 + 9, this.selectedFilter.getHighlightColor().getRGB());
            } else {
                relY -= 26;
            }
            this.drawElementCheckBox("hide", this.selectedFilter.isHideMessage(), relX, relY + 115 + 15, mouseX, mouseY);
            this.drawElementCheckBox("second_chat", this.selectedFilter.isDisplayInSecondChat(), relX, relY + 115 + 15 + 12, mouseX, mouseY);
            this.drawElementCheckBox("tooltip", this.selectedFilter.isFilterTooltips(), relX, relY + 115 + 15 + 24, mouseX, mouseY);
            boolean hoverCancel = mouseX > width - 268 && mouseX < width - 213 && mouseY > height - 30 && mouseY < height - 18;
            boolean hoverSave = mouseX > width - 210 && mouseX < width - 154 && mouseY > height - 30 && mouseY < height - 18;
            GuiChatFilter.drawRect(width - 268, height - 30, width - 213, height - 18, hoverCancel ? ModColor.toRGB(200, 100, 100, 200) : Integer.MAX_VALUE);
            GuiChatFilter.drawRect(width - 210, height - 30, width - 154, height - 18, hoverSave ? ModColor.toRGB(100, 200, 100, 200) : Integer.MAX_VALUE);
            GuiChatFilter.drawCenteredString(LabyModCore.getMinecraft().getFontRenderer(), LanguageManager.translate("button_cancel"), width - 262 + 22, height - 30 + 2, Integer.MAX_VALUE);
            GuiChatFilter.drawCenteredString(LabyModCore.getMinecraft().getFontRenderer(), LanguageManager.translate("button_save"), width - 205 + 23, height - 30 + 2, Integer.MAX_VALUE);
            this.textFieldFilterName.drawTextBox();
            this.textFieldFilterContains.drawTextBox();
            this.textFieldFilterContainsNot.drawTextBox();
            this.textFieldFilterRoom.drawTextBox();
            if (this.selectedFilter.isPlaySound()) {
                this.textFieldFilterSoundfile.drawTextBox();
            }
            if (this.textFieldFilterSoundfile.isFocused() && this.selectedFilter.isPlaySound() && this.textFieldFilterSoundfile != null && !this.textFieldFilterSoundfile.getText().isEmpty()) {
                String lowerCase;
                count = 0;
                hint = "";
                for (String path : soundNames) {
                    if (!path.startsWith(lowerCase = this.textFieldFilterSoundfile.getText().toLowerCase()) || path.equals(lowerCase)) continue;
                    hint = String.valueOf(hint) + path + "\n";
                    if (++count > 5) break;
                }
                if (count == 0) {
                    for (String path : soundNames) {
                        if (!path.contains(lowerCase = this.textFieldFilterSoundfile.getText().toLowerCase()) || path.equals(lowerCase)) continue;
                        hint = String.valueOf(hint) + path + "\n";
                        if (++count > 5) break;
                    }
                }
                if (count != 0) {
                    LabyMod.getInstance().getDrawUtils().drawHoveringText(relX, relYAtSoundHint + 40, hint.split("\n"));
                }
            }
            if (this.textFieldFilterRoom.isFocused()) {
                count = 0;
                hint = "";
                if (this.textFieldFilterRoom.getText().isEmpty() || "Global".contains(this.textFieldFilterRoom.getText().toUpperCase())) {
                    ++count;
                    hint = String.valueOf(hint) + "Global";
                }
                for (Filters.Filter filterComponent : LabyMod.getInstance().getChatToolManager().getFilters()) {
                    if (filterComponent.getRoom() == null || !filterComponent.getRoom().toLowerCase().contains(this.textFieldFilterRoom.getText().toLowerCase()) || hint.contains(filterComponent.getRoom())) continue;
                    hint = String.valueOf(hint) + filterComponent.getRoom() + "\n";
                    ++count;
                }
                if (count != 0) {
                    LabyMod.getInstance().getDrawUtils().drawHoveringText(relX, relYAtRoomHint + 40, hint.split("\n"));
                }
            }
        }
        this.drawString(LabyModCore.getMinecraft().getFontRenderer(), LanguageManager.translate("ingame_chat_tab_filter"), width - 150, height - 230, -1);
        if (this.sliderDrag != -1) {
            this.mouseClickMove(mouseX, mouseY, 0, 0L);
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.scrollbar.mouseAction(mouseX, mouseY, Scrollbar.EnumMouseAction.CLICKED);
        if (this.selectedFilter == null && mouseX > width - 165 && mouseX < width - 152 && mouseY > height - 220 && mouseY < height - 220 + 13) {
            this.loadFilter(new Filters.Filter("", new String[0], new String[0], false, "note.harp", true, 200, 200, 50, false, false, false, "Global"));
        }
        if (this.selectedFilter == null) {
            int row = 0;
            Filters.Filter todoDelete = null;
            for (Filters.Filter component : LabyMod.getInstance().getChatToolManager().getFilters()) {
                double posY = (double)(height - 215 + row * 10) + this.scrollbar.getScrollY();
                ++row;
                if (!(posY >= (double)(height - 220)) || posY > (double)(height - 25)) continue;
                if (mouseX > width - 12 - 1 && mouseX < width - 12 + 7 && (double)mouseY > posY && (double)mouseY < posY + 8.0) {
                    todoDelete = component;
                    continue;
                }
                if (mouseX <= width - 150 + 1 || mouseX >= width - 2 - 1 || (double)mouseY <= posY - 1.0 || (double)mouseY >= posY + 9.0) continue;
                this.loadFilter(new Filters.Filter(component));
                this.editStartName = component.getFilterName();
            }
            if (todoDelete != null) {
                LabyMod.getInstance().getChatToolManager().getFilters().remove(todoDelete);
                FilterChatManager.removeFilterComponent(todoDelete);
                LabyMod.getInstance().getChatToolManager().saveTools();
            }
        } else {
            boolean hoverSave;
            if (this.textFieldFilterRoom.getText().contains(" ") || this.selectedFilter.getRoom() == null || this.selectedFilter.getRoom().contains(" ")) {
                this.textFieldFilterRoom.setText(this.textFieldFilterRoom.getText().replaceAll(" ", ""));
                this.selectedFilter.setRoom(this.textFieldFilterRoom.getText());
            }
            if (this.textFieldFilterRoom.getText().isEmpty() || this.selectedFilter.getRoom() == null || this.selectedFilter.getRoom().isEmpty()) {
                this.textFieldFilterRoom.setText("Global");
                this.selectedFilter.setRoom(this.textFieldFilterRoom.getText());
            }
            int relX = width - 270;
            int relY = height - 220;
            if (this.isHoverElementCheckbox("play_sound", this.selectedFilter.isPlaySound(), relX, (relY += 23) + 69, mouseX, mouseY)) {
                this.selectedFilter.setPlaySound(!this.selectedFilter.isPlaySound());
            }
            if (!this.selectedFilter.isPlaySound()) {
                relY -= 10;
            }
            if (this.isHoverElementCheckbox("highlight", this.selectedFilter.isHighlightMessage(), relX, relY + 92, mouseX, mouseY)) {
                this.selectedFilter.setHighlightMessage(!this.selectedFilter.isHighlightMessage());
            }
            if (this.selectedFilter.isHighlightMessage() && this.selectedFilter.getHighlightColor() != null) {
                this.dragElementSlider(relX, relY + 92 + 15, 0, mouseX, mouseY);
                this.dragElementSlider(relX, relY + 92 + 15 + 9, 1, mouseX, mouseY);
                this.dragElementSlider(relX, relY + 92 + 15 + 18, 2, mouseX, mouseY);
            } else {
                relY -= 26;
            }
            if (this.isHoverElementCheckbox("hide", this.selectedFilter.isHideMessage(), relX, relY + 115 + 15, mouseX, mouseY)) {
                this.selectedFilter.setHideMessage(!this.selectedFilter.isHideMessage());
            }
            if (this.isHoverElementCheckbox("second_chat", this.selectedFilter.isDisplayInSecondChat(), relX, relY + 115 + 15 + 12, mouseX, mouseY)) {
                this.selectedFilter.setDisplayInSecondChat(!this.selectedFilter.isDisplayInSecondChat());
            }
            if (this.isHoverElementCheckbox("tooltip", this.selectedFilter.isFilterTooltips(), relX, relY + 115 + 15 + 24, mouseX, mouseY)) {
                this.selectedFilter.setFilterTooltips(!this.selectedFilter.isFilterTooltips());
            }
            boolean hoverCancel = mouseX > width - 268 && mouseX < width - 213 && mouseY > height - 30 && mouseY < height - 18;
            boolean bl2 = hoverSave = mouseX > width - 210 && mouseX < width - 154 && mouseY > height - 30 && mouseY < height - 18;
            if (hoverCancel) {
                this.selectedFilter = null;
            }
            if (hoverSave) {
                if (this.selectedFilter.getFilterName().replaceAll(" ", "").isEmpty()) {
                    this.markFilterNameRed = true;
                }
                this.markContainsRed = this.selectedFilter.getWordsContains().length == 0;
                this.markSoundNameRed = this.selectedFilter.isPlaySound() && !soundNames.contains(this.textFieldFilterSoundfile.getText().toLowerCase());
                if (!(this.markFilterNameRed || this.markSoundNameRed || this.markContainsRed)) {
                    Iterator<Filters.Filter> it2 = LabyMod.getInstance().getChatToolManager().getFilters().iterator();
                    while (it2.hasNext()) {
                        Filters.Filter next = it2.next();
                        if (this.editStartName == null) {
                            if (!next.getFilterName().equalsIgnoreCase(this.selectedFilter.getFilterName())) continue;
                            it2.remove();
                            continue;
                        }
                        if (!next.getFilterName().equalsIgnoreCase(this.editStartName)) continue;
                        it2.remove();
                    }
                    if (!LabyMod.getInstance().getChatToolManager().getFilters().contains(this.selectedFilter)) {
                        LabyMod.getInstance().getChatToolManager().getFilters().add(this.selectedFilter);
                    }
                    LabyMod.getInstance().getChatToolManager().saveTools();
                    FilterChatManager.getFilterResults().clear();
                    Minecraft.getMinecraft().ingameGUI.getChatGUI().refreshChat();
                    this.selectedFilter = null;
                    this.initGui();
                }
            }
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (this.selectedFilter != null && (this.textFieldFilterName.isFocused() || this.textFieldFilterContains.isFocused() || this.textFieldFilterContainsNot.isFocused() || this.textFieldFilterRoom.isFocused() || this.textFieldFilterSoundfile.isFocused())) {
            if (keyCode == 15) {
                if (this.textFieldFilterName.isFocused()) {
                    this.textFieldFilterName.setFocused(false);
                    this.textFieldFilterContains.setFocused(true);
                    return;
                }
                if (this.textFieldFilterContains.isFocused()) {
                    this.textFieldFilterContains.setFocused(false);
                    this.textFieldFilterContainsNot.setFocused(true);
                    return;
                }
                if (this.textFieldFilterContainsNot.isFocused()) {
                    this.textFieldFilterContainsNot.setFocused(false);
                    this.textFieldFilterRoom.setFocused(true);
                    return;
                }
                if (this.textFieldFilterRoom.isFocused()) {
                    this.textFieldFilterRoom.setFocused(false);
                    if (this.selectedFilter.isPlaySound()) {
                        this.textFieldFilterSoundfile.setFocused(true);
                    } else {
                        this.textFieldFilterName.setFocused(true);
                    }
                    return;
                }
                if (this.textFieldFilterSoundfile.isFocused()) {
                    this.textFieldFilterSoundfile.setFocused(false);
                    this.textFieldFilterName.setFocused(true);
                    return;
                }
            }
            if (this.textFieldFilterName.textboxKeyTyped(typedChar, keyCode)) {
                String newText = this.textFieldFilterName.getText().replaceAll(" ", "");
                boolean equals = false;
                for (Filters.Filter filter : LabyMod.getInstance().getChatToolManager().getFilters()) {
                    if (!filter.getFilterName().equalsIgnoreCase(newText) || filter.getFilterName().equalsIgnoreCase(this.editStartName)) continue;
                    equals = true;
                    break;
                }
                if (equals) {
                    this.markFilterNameRed = true;
                    return;
                }
                this.selectedFilter.setFilterName(newText);
                this.markFilterNameRed = false;
            }
            if (this.textFieldFilterContains.textboxKeyTyped(typedChar, keyCode)) {
                this.selectedFilter.setWordsContains(this.splitWords(this.textFieldFilterContains.getText()));
                this.markContainsRed = false;
            }
            if (this.textFieldFilterContainsNot.textboxKeyTyped(typedChar, keyCode)) {
                this.selectedFilter.setWordsContainsNot(this.splitWords(this.textFieldFilterContainsNot.getText()));
            }
            if (this.textFieldFilterRoom.textboxKeyTyped(typedChar, keyCode)) {
                this.selectedFilter.setRoom(this.textFieldFilterRoom.getText());
            }
            if (this.textFieldFilterSoundfile.textboxKeyTyped(typedChar, keyCode)) {
                this.selectedFilter.setSoundPath(this.textFieldFilterSoundfile.getText().toLowerCase());
                this.markSoundNameRed = false;
            }
        } else {
            super.keyTyped(typedChar, keyCode);
        }
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        this.scrollbar.mouseAction(mouseX, mouseY, Scrollbar.EnumMouseAction.DRAGGING);
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
        if (this.selectedFilter != null) {
            int relX = width - 270;
            int relY = height - 200;
            if (!this.selectedFilter.isPlaySound()) {
                relY -= 10;
            }
            if (this.selectedFilter.isHighlightMessage() && this.selectedFilter.getHighlightColor() != null) {
                if (this.sliderDrag == 0) {
                    this.dragElementSlider(relX, relY + 92 + 15, 0, mouseX, mouseY);
                }
                if (this.sliderDrag == 1) {
                    this.dragElementSlider(relX, relY + 92 + 15 + 9, 1, mouseX, mouseY);
                }
                if (this.sliderDrag == 2) {
                    this.dragElementSlider(relX, relY + 92 + 15 + 18, 2, mouseX, mouseY);
                }
            }
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        super.mouseReleased(mouseX, mouseY, mouseButton);
        this.sliderDrag = -1;
        this.scrollbar.mouseAction(mouseX, mouseY, Scrollbar.EnumMouseAction.RELEASED);
        this.textFieldFilterName.mouseClicked(mouseX, mouseY, mouseButton);
        this.textFieldFilterContains.mouseClicked(mouseX, mouseY, mouseButton);
        this.textFieldFilterContainsNot.mouseClicked(mouseX, mouseY, mouseButton);
        this.textFieldFilterRoom.mouseClicked(mouseX, mouseY, mouseButton);
        this.textFieldFilterSoundfile.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        this.textFieldFilterName.updateCursorCounter();
        this.textFieldFilterContains.updateCursorCounter();
        this.textFieldFilterContainsNot.updateCursorCounter();
        this.textFieldFilterRoom.updateCursorCounter();
        this.textFieldFilterSoundfile.updateCursorCounter();
    }

    private void drawElementTextField(String prefix, GuiTextField textField, int x2, int y2, int mouseX, int mouseY) {
        if (!prefix.isEmpty()) {
            prefix = String.valueOf(LanguageManager.translate("chat_filter_" + prefix)) + ":";
        }
        this.drawString(LabyModCore.getMinecraft().getFontRenderer(), prefix, x2 + 2, y2 + 2, Integer.MAX_VALUE);
        GuiChatFilter.drawRect(x2 + 2, y2 + 12, x2 + 2 + 114, y2 + 12 + 10, this.markContainsRed && textField != null && textField.equals(this.textFieldFilterContains) || this.markFilterNameRed && textField != null && textField.equals(this.textFieldFilterName) || this.markSoundNameRed && textField != null && textField.equals(this.textFieldFilterSoundfile) ? ModColor.toRGB(200, 100, 100, 200) : Integer.MAX_VALUE);
        if (textField == null) {
            return;
        }
        LabyModCore.getMinecraft().setTextFieldXPosition(textField, x2 + 2);
        LabyModCore.getMinecraft().setTextFieldYPosition(textField, y2 + 13);
        textField.setEnableBackgroundDrawing(false);
        textField.setMaxStringLength(120);
    }

    private void drawElementCheckBox(String text, boolean check, int x2, int y2, int mouseX, int mouseY) {
        boolean hover = this.isHoverElementCheckbox(text, check, x2, y2, mouseX, mouseY);
        text = LanguageManager.translate("chat_filter_" + text);
        this.drawString(LabyModCore.getMinecraft().getFontRenderer(), text, x2 + 2, y2 + 2, Integer.MAX_VALUE);
        GuiChatFilter.drawRect((x2 += LabyModCore.getMinecraft().getFontRenderer().getStringWidth(text) + 2) + 3, y2 + 1, x2 + 12, y2 + 10, hover ? 2147483547 : Integer.MAX_VALUE);
        if (!check) {
            return;
        }
        GuiChatFilter.drawCenteredString(LabyModCore.getMinecraft().getFontRenderer(), String.valueOf(ModColor.cl("a")) + "\u2714", x2 + 8, y2 + 1, Integer.MAX_VALUE);
    }

    private void drawElementSlider(int value, int x2, int y2, int id2, int mouseX, int mouseY) {
        GuiChatFilter.drawRect(x2 + 2, y2, x2 + 2 + 94, y2 + 1, Integer.MAX_VALUE);
        double colorValue = value;
        double percent = colorValue / 255.0 * 94.0;
        int pos = (int)percent;
        GuiChatFilter.drawRect(x2 + 2 + pos, y2 - 3, x2 + 2 + pos + 2, y2 + 5, ModColor.toRGB(id2 == 0 ? value : 0, id2 == 1 ? value : 0, id2 == 2 ? value : 0, 255));
    }

    private boolean isHoverElementCheckbox(String text, boolean check, int x2, int y2, int mouseX, int mouseY) {
        text = LanguageManager.translate("chat_filter_" + text);
        return mouseX > (x2 += LabyModCore.getMinecraft().getFontRenderer().getStringWidth(text) + 2) + 3 && mouseX < x2 + 12 && mouseY > y2 + 1 && mouseY < y2 + 10;
    }

    private void dragElementSlider(int x2, int y2, int id2, int mouseX, int mouseY) {
        if (mouseX <= x2 || mouseX >= x2 + 94 || (mouseY <= y2 - 5 || mouseY >= y2 + 5) && this.sliderDrag != id2) {
            return;
        }
        if (mouseX < x2) {
            mouseX = x2;
        }
        if (mouseX > x2 + 94) {
            mouseX = x2 + 94;
        }
        double pos = mouseX - x2;
        double value = pos * 255.0 / 94.0;
        int colorValue = (int)value;
        Color highlightColor = this.selectedFilter.getHighlightColor();
        int r2 = highlightColor.getRed();
        int g2 = highlightColor.getGreen();
        int b2 = highlightColor.getBlue();
        switch (id2) {
            case 0: {
                r2 = colorValue;
                break;
            }
            case 1: {
                g2 = colorValue;
                break;
            }
            case 2: {
                b2 = colorValue;
            }
        }
        this.selectedFilter.setHighlightColorR((short)r2);
        this.selectedFilter.setHighlightColorG((short)g2);
        this.selectedFilter.setHighlightColorB((short)b2);
        this.sliderDrag = id2;
    }

    private void loadFilter(Filters.Filter filter) {
        this.editStartName = null;
        this.selectedFilter = filter;
        this.markFilterNameRed = false;
        this.textFieldFilterName.setText(filter.getFilterName());
        this.textFieldFilterContains.setText(this.wordsToString(filter.getWordsContains()));
        this.textFieldFilterContainsNot.setText(this.wordsToString(filter.getWordsContainsNot()));
        this.textFieldFilterRoom.setText(filter.getRoom() == null || filter.getRoom().isEmpty() ? "Global" : filter.getRoom());
        this.textFieldFilterSoundfile.setText(filter.getSoundPath());
    }

    private String[] splitWords(String text) {
        String[] stringArray;
        if (text.contains(",")) {
            stringArray = text.split(",");
        } else if (text.isEmpty()) {
            stringArray = new String[]{};
        } else {
            String[] stringArray2 = new String[1];
            stringArray = stringArray2;
            stringArray2[0] = text;
        }
        return stringArray;
    }

    private String wordsToString(String[] words) {
        String output = "";
        String[] stringArray = words;
        int n2 = words.length;
        int n3 = 0;
        while (n3 < n2) {
            String word = stringArray[n3];
            if (!output.isEmpty()) {
                output = String.valueOf(output) + ",";
            }
            output = String.valueOf(output) + word;
            ++n3;
        }
        return output;
    }
}

