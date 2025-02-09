/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.gui;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import net.minecraft.client.AnvilConverterException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiCreateWorld;
import net.minecraft.client.gui.GuiErrorScreen;
import net.minecraft.client.gui.GuiRenameWorld;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.SaveFormatComparator;
import net.minecraft.world.storage.WorldInfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GuiSelectWorld
extends GuiScreen
implements GuiYesNoCallback {
    private static final Logger logger = LogManager.getLogger();
    private final DateFormat field_146633_h = new SimpleDateFormat();
    protected GuiScreen parentScreen;
    protected String screenTitle = "Select world";
    private boolean field_146634_i;
    private int selectedIndex;
    private java.util.List<SaveFormatComparator> field_146639_s;
    private List availableWorlds;
    private String field_146637_u;
    private String field_146636_v;
    private String[] field_146635_w = new String[4];
    private boolean confirmingDelete;
    private GuiButton deleteButton;
    private GuiButton selectButton;
    private GuiButton renameButton;
    private GuiButton recreateButton;

    public GuiSelectWorld(GuiScreen parentScreenIn) {
        this.parentScreen = parentScreenIn;
    }

    @Override
    public void initGui() {
        this.screenTitle = I18n.format("selectWorld.title", new Object[0]);
        try {
            this.loadLevelList();
        }
        catch (AnvilConverterException anvilconverterexception) {
            logger.error("Couldn't load level list", (Throwable)anvilconverterexception);
            this.mc.displayGuiScreen(new GuiErrorScreen("Unable to load worlds", anvilconverterexception.getMessage()));
            return;
        }
        this.field_146637_u = I18n.format("selectWorld.world", new Object[0]);
        this.field_146636_v = I18n.format("selectWorld.conversion", new Object[0]);
        this.field_146635_w[WorldSettings.GameType.SURVIVAL.getID()] = I18n.format("gameMode.survival", new Object[0]);
        this.field_146635_w[WorldSettings.GameType.CREATIVE.getID()] = I18n.format("gameMode.creative", new Object[0]);
        this.field_146635_w[WorldSettings.GameType.ADVENTURE.getID()] = I18n.format("gameMode.adventure", new Object[0]);
        this.field_146635_w[WorldSettings.GameType.SPECTATOR.getID()] = I18n.format("gameMode.spectator", new Object[0]);
        this.availableWorlds = new List(this.mc);
        this.availableWorlds.registerScrollButtons(4, 5);
        this.addWorldSelectionButtons();
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        this.availableWorlds.handleMouseInput();
    }

    private void loadLevelList() throws AnvilConverterException {
        ISaveFormat isaveformat = this.mc.getSaveLoader();
        this.field_146639_s = isaveformat.getSaveList();
        Collections.sort(this.field_146639_s);
        this.selectedIndex = -1;
    }

    protected String func_146621_a(int p_146621_1_) {
        return this.field_146639_s.get(p_146621_1_).getFileName();
    }

    protected String func_146614_d(int p_146614_1_) {
        String s2 = this.field_146639_s.get(p_146614_1_).getDisplayName();
        if (StringUtils.isEmpty(s2)) {
            s2 = String.valueOf(I18n.format("selectWorld.world", new Object[0])) + " " + (p_146614_1_ + 1);
        }
        return s2;
    }

    public void addWorldSelectionButtons() {
        this.selectButton = new GuiButton(1, width / 2 - 154, height - 52, 150, 20, I18n.format("selectWorld.select", new Object[0]));
        this.buttonList.add(this.selectButton);
        this.buttonList.add(new GuiButton(3, width / 2 + 4, height - 52, 150, 20, I18n.format("selectWorld.create", new Object[0])));
        this.renameButton = new GuiButton(6, width / 2 - 154, height - 28, 72, 20, I18n.format("selectWorld.rename", new Object[0]));
        this.buttonList.add(this.renameButton);
        this.deleteButton = new GuiButton(2, width / 2 - 76, height - 28, 72, 20, I18n.format("selectWorld.delete", new Object[0]));
        this.buttonList.add(this.deleteButton);
        this.recreateButton = new GuiButton(7, width / 2 + 4, height - 28, 72, 20, I18n.format("selectWorld.recreate", new Object[0]));
        this.buttonList.add(this.recreateButton);
        this.buttonList.add(new GuiButton(0, width / 2 + 82, height - 28, 72, 20, I18n.format("gui.cancel", new Object[0])));
        this.selectButton.enabled = false;
        this.deleteButton.enabled = false;
        this.renameButton.enabled = false;
        this.recreateButton.enabled = false;
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.enabled) {
            if (button.id == 2) {
                String s2 = this.func_146614_d(this.selectedIndex);
                if (s2 != null) {
                    this.confirmingDelete = true;
                    GuiYesNo guiyesno = GuiSelectWorld.makeDeleteWorldYesNo(this, s2, this.selectedIndex);
                    this.mc.displayGuiScreen(guiyesno);
                }
            } else if (button.id == 1) {
                this.func_146615_e(this.selectedIndex);
            } else if (button.id == 3) {
                this.mc.displayGuiScreen(new GuiCreateWorld(this));
            } else if (button.id == 6) {
                this.mc.displayGuiScreen(new GuiRenameWorld(this, this.func_146621_a(this.selectedIndex)));
            } else if (button.id == 0) {
                this.mc.displayGuiScreen(this.parentScreen);
            } else if (button.id == 7) {
                GuiCreateWorld guicreateworld = new GuiCreateWorld(this);
                ISaveHandler isavehandler = this.mc.getSaveLoader().getSaveLoader(this.func_146621_a(this.selectedIndex), false);
                WorldInfo worldinfo = isavehandler.loadWorldInfo();
                isavehandler.flush();
                guicreateworld.recreateFromExistingWorld(worldinfo);
                this.mc.displayGuiScreen(guicreateworld);
            } else {
                this.availableWorlds.actionPerformed(button);
            }
        }
    }

    public void func_146615_e(int p_146615_1_) {
        this.mc.displayGuiScreen(null);
        if (!this.field_146634_i) {
            String s1;
            this.field_146634_i = true;
            String s2 = this.func_146621_a(p_146615_1_);
            if (s2 == null) {
                s2 = "World" + p_146615_1_;
            }
            if ((s1 = this.func_146614_d(p_146615_1_)) == null) {
                s1 = "World" + p_146615_1_;
            }
            if (this.mc.getSaveLoader().canLoadWorld(s2)) {
                this.mc.launchIntegratedServer(s2, s1, null);
            }
        }
    }

    @Override
    public void confirmClicked(boolean result, int id2) {
        if (this.confirmingDelete) {
            this.confirmingDelete = false;
            if (result) {
                ISaveFormat isaveformat = this.mc.getSaveLoader();
                isaveformat.flushCache();
                isaveformat.deleteWorldDirectory(this.func_146621_a(id2));
                try {
                    this.loadLevelList();
                }
                catch (AnvilConverterException anvilconverterexception) {
                    logger.error("Couldn't load level list", (Throwable)anvilconverterexception);
                }
            }
            this.mc.displayGuiScreen(this);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.availableWorlds.drawScreen(mouseX, mouseY, partialTicks);
        GuiSelectWorld.drawCenteredString(this.fontRendererObj, this.screenTitle, width / 2, 20, 0xFFFFFF);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    public static GuiYesNo makeDeleteWorldYesNo(GuiYesNoCallback selectWorld, String name, int id2) {
        String s2 = I18n.format("selectWorld.deleteQuestion", new Object[0]);
        String s1 = "'" + name + "' " + I18n.format("selectWorld.deleteWarning", new Object[0]);
        String s22 = I18n.format("selectWorld.deleteButton", new Object[0]);
        String s3 = I18n.format("gui.cancel", new Object[0]);
        GuiYesNo guiyesno = new GuiYesNo(selectWorld, s2, s1, s22, s3, id2);
        return guiyesno;
    }

    class List
    extends GuiSlot {
        public List(Minecraft mcIn) {
            super(mcIn, width, height, 32, height - 64, 36);
        }

        @Override
        protected int getSize() {
            return GuiSelectWorld.this.field_146639_s.size();
        }

        @Override
        protected void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY) {
            boolean flag;
            GuiSelectWorld.this.selectedIndex = slotIndex;
            ((GuiSelectWorld)GuiSelectWorld.this).selectButton.enabled = flag = GuiSelectWorld.this.selectedIndex >= 0 && GuiSelectWorld.this.selectedIndex < this.getSize();
            ((GuiSelectWorld)GuiSelectWorld.this).deleteButton.enabled = flag;
            ((GuiSelectWorld)GuiSelectWorld.this).renameButton.enabled = flag;
            ((GuiSelectWorld)GuiSelectWorld.this).recreateButton.enabled = flag;
            if (isDoubleClick && flag) {
                GuiSelectWorld.this.func_146615_e(slotIndex);
            }
        }

        @Override
        protected boolean isSelected(int slotIndex) {
            return slotIndex == GuiSelectWorld.this.selectedIndex;
        }

        @Override
        protected int getContentHeight() {
            return GuiSelectWorld.this.field_146639_s.size() * 36;
        }

        @Override
        protected void drawBackground() {
            GuiSelectWorld.this.drawDefaultBackground();
        }

        @Override
        protected void drawSlot(int entryID, int p_180791_2_, int p_180791_3_, int p_180791_4_, int mouseXIn, int mouseYIn) {
            SaveFormatComparator saveformatcomparator = (SaveFormatComparator)GuiSelectWorld.this.field_146639_s.get(entryID);
            String s2 = saveformatcomparator.getDisplayName();
            if (StringUtils.isEmpty(s2)) {
                s2 = String.valueOf(GuiSelectWorld.this.field_146637_u) + " " + (entryID + 1);
            }
            String s1 = saveformatcomparator.getFileName();
            s1 = String.valueOf(s1) + " (" + GuiSelectWorld.this.field_146633_h.format(new Date(saveformatcomparator.getLastTimePlayed()));
            s1 = String.valueOf(s1) + ")";
            String s22 = "";
            if (saveformatcomparator.requiresConversion()) {
                s22 = String.valueOf(GuiSelectWorld.this.field_146636_v) + " " + s22;
            } else {
                s22 = GuiSelectWorld.this.field_146635_w[saveformatcomparator.getEnumGameType().getID()];
                if (saveformatcomparator.isHardcoreModeEnabled()) {
                    s22 = (Object)((Object)EnumChatFormatting.DARK_RED) + I18n.format("gameMode.hardcore", new Object[0]) + (Object)((Object)EnumChatFormatting.RESET);
                }
                if (saveformatcomparator.getCheatsEnabled()) {
                    s22 = String.valueOf(s22) + ", " + I18n.format("selectWorld.cheats", new Object[0]);
                }
            }
            GuiSelectWorld.this.drawString(GuiSelectWorld.this.fontRendererObj, s2, p_180791_2_ + 2, p_180791_3_ + 1, 0xFFFFFF);
            GuiSelectWorld.this.drawString(GuiSelectWorld.this.fontRendererObj, s1, p_180791_2_ + 2, p_180791_3_ + 12, 0x808080);
            GuiSelectWorld.this.drawString(GuiSelectWorld.this.fontRendererObj, s22, p_180791_2_ + 2, p_180791_3_ + 12 + 10, 0x808080);
        }
    }
}

