// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.gui.achievement;

import net.minecraft.entity.EntityList;
import java.util.Iterator;
import net.minecraft.block.Block;
import net.minecraft.stats.StatList;
import com.google.common.collect.Lists;
import java.util.Collections;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatBase;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;
import org.lwjgl.input.Mouse;
import java.util.Comparator;
import net.minecraft.stats.StatCrafting;
import java.util.List;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.Item;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketClientStatus;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.stats.StatisticsManager;
import net.minecraft.client.gui.IProgressMeter;
import net.minecraft.client.gui.GuiScreen;

public class GuiStats extends GuiScreen implements IProgressMeter
{
    protected GuiScreen parentScreen;
    protected String screenTitle;
    private StatsGeneral generalStats;
    private StatsItem itemStats;
    private StatsBlock blockStats;
    private StatsMobsList mobStats;
    private final StatisticsManager stats;
    private GuiSlot displaySlot;
    private boolean doesGuiPauseGame;
    
    public GuiStats(final GuiScreen p_i1071_1_, final StatisticsManager p_i1071_2_) {
        this.screenTitle = "Select world";
        this.doesGuiPauseGame = true;
        this.parentScreen = p_i1071_1_;
        this.stats = p_i1071_2_;
    }
    
    @Override
    public void initGui() {
        this.screenTitle = I18n.format("gui.stats", new Object[0]);
        this.doesGuiPauseGame = true;
        this.mc.getConnection().sendPacket(new CPacketClientStatus(CPacketClientStatus.State.REQUEST_STATS));
    }
    
    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        if (this.displaySlot != null) {
            this.displaySlot.handleMouseInput();
        }
    }
    
    public void func_193028_a() {
        (this.generalStats = new StatsGeneral(this.mc)).registerScrollButtons(1, 1);
        (this.itemStats = new StatsItem(this.mc)).registerScrollButtons(1, 1);
        (this.blockStats = new StatsBlock(this.mc)).registerScrollButtons(1, 1);
        (this.mobStats = new StatsMobsList(this.mc)).registerScrollButtons(1, 1);
    }
    
    public void func_193029_f() {
        this.buttonList.add(new GuiButton(0, GuiStats.width / 2 + 4, GuiStats.height - 28, 150, 20, I18n.format("gui.done", new Object[0])));
        this.buttonList.add(new GuiButton(1, GuiStats.width / 2 - 160, GuiStats.height - 52, 80, 20, I18n.format("stat.generalButton", new Object[0])));
        final GuiButton guibutton = this.addButton(new GuiButton(2, GuiStats.width / 2 - 80, GuiStats.height - 52, 80, 20, I18n.format("stat.blocksButton", new Object[0])));
        final GuiButton guibutton2 = this.addButton(new GuiButton(3, GuiStats.width / 2, GuiStats.height - 52, 80, 20, I18n.format("stat.itemsButton", new Object[0])));
        final GuiButton guibutton3 = this.addButton(new GuiButton(4, GuiStats.width / 2 + 80, GuiStats.height - 52, 80, 20, I18n.format("stat.mobsButton", new Object[0])));
        if (this.blockStats.getSize() == 0) {
            guibutton.enabled = false;
        }
        if (this.itemStats.getSize() == 0) {
            guibutton2.enabled = false;
        }
        if (this.mobStats.getSize() == 0) {
            guibutton3.enabled = false;
        }
    }
    
    @Override
    protected void actionPerformed(final GuiButton button) throws IOException {
        if (button.enabled) {
            if (button.id == 0) {
                this.mc.displayGuiScreen(this.parentScreen);
            }
            else if (button.id == 1) {
                this.displaySlot = this.generalStats;
            }
            else if (button.id == 3) {
                this.displaySlot = this.itemStats;
            }
            else if (button.id == 2) {
                this.displaySlot = this.blockStats;
            }
            else if (button.id == 4) {
                this.displaySlot = this.mobStats;
            }
            else {
                this.displaySlot.actionPerformed(button);
            }
        }
    }
    
    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        if (this.doesGuiPauseGame) {
            this.drawDefaultBackground();
            Gui.drawCenteredString(this.fontRendererObj, I18n.format("multiplayer.downloadingStats", new Object[0]), GuiStats.width / 2, GuiStats.height / 2, 16777215);
            Gui.drawCenteredString(this.fontRendererObj, GuiStats.LOADING_STRINGS[(int)(Minecraft.getSystemTime() / 150L % GuiStats.LOADING_STRINGS.length)], GuiStats.width / 2, GuiStats.height / 2 + this.fontRendererObj.FONT_HEIGHT * 2, 16777215);
        }
        else {
            this.drawDefaultBackground();
            this.displaySlot.drawScreen(mouseX, mouseY, partialTicks);
            Gui.drawCenteredString(this.fontRendererObj, this.screenTitle, GuiStats.width / 2, 20, 16777215);
            super.drawScreen(mouseX, mouseY, partialTicks);
        }
    }
    
    @Override
    public void func_193026_g() {
        if (this.doesGuiPauseGame) {
            this.func_193028_a();
            this.func_193029_f();
            this.displaySlot = this.generalStats;
            this.doesGuiPauseGame = false;
        }
    }
    
    @Override
    public boolean doesGuiPauseGame() {
        return !this.doesGuiPauseGame;
    }
    
    private void drawStatsScreen(final int p_146521_1_, final int p_146521_2_, final Item p_146521_3_) {
        this.drawButtonBackground(p_146521_1_ + 1, p_146521_2_ + 1);
        GlStateManager.enableRescaleNormal();
        RenderHelper.enableGUIStandardItemLighting();
        this.itemRender.renderItemIntoGUI(p_146521_3_.func_190903_i(), p_146521_1_ + 2, p_146521_2_ + 2);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
    }
    
    private void drawButtonBackground(final int p_146531_1_, final int p_146531_2_) {
        this.drawSprite(p_146531_1_, p_146531_2_, 0, 0);
    }
    
    private void drawSprite(final int p_146527_1_, final int p_146527_2_, final int p_146527_3_, final int p_146527_4_) {
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        this.mc.getTextureManager().bindTexture(GuiStats.STAT_ICONS);
        final float f = 0.0078125f;
        final float f2 = 0.0078125f;
        final int i = 18;
        final int j = 18;
        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(p_146527_1_ + 0, p_146527_2_ + 18, GuiStats.zLevel).tex((p_146527_3_ + 0) * 0.0078125f, (p_146527_4_ + 18) * 0.0078125f).endVertex();
        bufferbuilder.pos(p_146527_1_ + 18, p_146527_2_ + 18, GuiStats.zLevel).tex((p_146527_3_ + 18) * 0.0078125f, (p_146527_4_ + 18) * 0.0078125f).endVertex();
        bufferbuilder.pos(p_146527_1_ + 18, p_146527_2_ + 0, GuiStats.zLevel).tex((p_146527_3_ + 18) * 0.0078125f, (p_146527_4_ + 0) * 0.0078125f).endVertex();
        bufferbuilder.pos(p_146527_1_ + 0, p_146527_2_ + 0, GuiStats.zLevel).tex((p_146527_3_ + 0) * 0.0078125f, (p_146527_4_ + 0) * 0.0078125f).endVertex();
        tessellator.draw();
    }
    
    abstract class Stats extends GuiSlot
    {
        protected int headerPressed;
        protected List<StatCrafting> statsHolder;
        protected Comparator<StatCrafting> statSorter;
        protected int sortColumn;
        protected int sortOrder;
        
        protected Stats(final Minecraft p_i47550_2_) {
            super(p_i47550_2_, GuiStats.width, GuiStats.height, 32, GuiStats.height - 64, 20);
            this.headerPressed = -1;
            this.sortColumn = -1;
            this.func_193651_b(false);
            this.setHasListHeader(true, 20);
        }
        
        @Override
        protected void elementClicked(final int slotIndex, final boolean isDoubleClick, final int mouseX, final int mouseY) {
        }
        
        @Override
        protected boolean isSelected(final int slotIndex) {
            return false;
        }
        
        @Override
        public int getListWidth() {
            return 375;
        }
        
        @Override
        protected int getScrollBarX() {
            return this.width / 2 + 140;
        }
        
        @Override
        protected void drawBackground() {
            GuiStats.this.drawDefaultBackground();
        }
        
        @Override
        protected void drawListHeader(final int insideLeft, final int insideTop, final Tessellator tessellatorIn) {
            if (!Mouse.isButtonDown(0)) {
                this.headerPressed = -1;
            }
            if (this.headerPressed == 0) {
                GuiStats.this.drawSprite(insideLeft + 115 - 18, insideTop + 1, 0, 0);
            }
            else {
                GuiStats.this.drawSprite(insideLeft + 115 - 18, insideTop + 1, 0, 18);
            }
            if (this.headerPressed == 1) {
                GuiStats.this.drawSprite(insideLeft + 165 - 18, insideTop + 1, 0, 0);
            }
            else {
                GuiStats.this.drawSprite(insideLeft + 165 - 18, insideTop + 1, 0, 18);
            }
            if (this.headerPressed == 2) {
                GuiStats.this.drawSprite(insideLeft + 215 - 18, insideTop + 1, 0, 0);
            }
            else {
                GuiStats.this.drawSprite(insideLeft + 215 - 18, insideTop + 1, 0, 18);
            }
            if (this.headerPressed == 3) {
                GuiStats.this.drawSprite(insideLeft + 265 - 18, insideTop + 1, 0, 0);
            }
            else {
                GuiStats.this.drawSprite(insideLeft + 265 - 18, insideTop + 1, 0, 18);
            }
            if (this.headerPressed == 4) {
                GuiStats.this.drawSprite(insideLeft + 315 - 18, insideTop + 1, 0, 0);
            }
            else {
                GuiStats.this.drawSprite(insideLeft + 315 - 18, insideTop + 1, 0, 18);
            }
            if (this.sortColumn != -1) {
                int i = 79;
                int j = 18;
                if (this.sortColumn == 1) {
                    i = 129;
                }
                else if (this.sortColumn == 2) {
                    i = 179;
                }
                else if (this.sortColumn == 3) {
                    i = 229;
                }
                else if (this.sortColumn == 4) {
                    i = 279;
                }
                if (this.sortOrder == 1) {
                    j = 36;
                }
                GuiStats.this.drawSprite(insideLeft + i, insideTop + 1, j, 0);
            }
        }
        
        @Override
        protected void clickedHeader(final int p_148132_1_, final int p_148132_2_) {
            this.headerPressed = -1;
            if (p_148132_1_ >= 79 && p_148132_1_ < 115) {
                this.headerPressed = 0;
            }
            else if (p_148132_1_ >= 129 && p_148132_1_ < 165) {
                this.headerPressed = 1;
            }
            else if (p_148132_1_ >= 179 && p_148132_1_ < 215) {
                this.headerPressed = 2;
            }
            else if (p_148132_1_ >= 229 && p_148132_1_ < 265) {
                this.headerPressed = 3;
            }
            else if (p_148132_1_ >= 279 && p_148132_1_ < 315) {
                this.headerPressed = 4;
            }
            if (this.headerPressed >= 0) {
                this.sortByColumn(this.headerPressed);
                this.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f));
            }
        }
        
        @Override
        protected final int getSize() {
            return this.statsHolder.size();
        }
        
        protected final StatCrafting getSlotStat(final int p_148211_1_) {
            return this.statsHolder.get(p_148211_1_);
        }
        
        protected abstract String getHeaderDescriptionId(final int p0);
        
        protected void renderStat(final StatBase p_148209_1_, final int p_148209_2_, final int p_148209_3_, final boolean p_148209_4_) {
            if (p_148209_1_ != null) {
                final String s = p_148209_1_.format(GuiStats.this.stats.readStat(p_148209_1_));
                Gui.drawString(GuiStats.this.fontRendererObj, s, p_148209_2_ - GuiStats.this.fontRendererObj.getStringWidth(s), p_148209_3_ + 5, p_148209_4_ ? 16777215 : 9474192);
            }
            else {
                final String s2 = "-";
                Gui.drawString(GuiStats.this.fontRendererObj, "-", p_148209_2_ - GuiStats.this.fontRendererObj.getStringWidth("-"), p_148209_3_ + 5, p_148209_4_ ? 16777215 : 9474192);
            }
        }
        
        @Override
        protected void renderDecorations(final int mouseXIn, final int mouseYIn) {
            if (mouseYIn >= this.top && mouseYIn <= this.bottom) {
                final int i = this.getSlotIndexFromScreenCoords(mouseXIn, mouseYIn);
                final int j = (this.width - this.getListWidth()) / 2;
                if (i >= 0) {
                    if (mouseXIn < j + 40 || mouseXIn > j + 40 + 20) {
                        return;
                    }
                    final StatCrafting statcrafting = this.getSlotStat(i);
                    this.renderMouseHoverToolTip(statcrafting, mouseXIn, mouseYIn);
                }
                else {
                    String s;
                    if (mouseXIn >= j + 115 - 18 && mouseXIn <= j + 115) {
                        s = this.getHeaderDescriptionId(0);
                    }
                    else if (mouseXIn >= j + 165 - 18 && mouseXIn <= j + 165) {
                        s = this.getHeaderDescriptionId(1);
                    }
                    else if (mouseXIn >= j + 215 - 18 && mouseXIn <= j + 215) {
                        s = this.getHeaderDescriptionId(2);
                    }
                    else if (mouseXIn >= j + 265 - 18 && mouseXIn <= j + 265) {
                        s = this.getHeaderDescriptionId(3);
                    }
                    else {
                        if (mouseXIn < j + 315 - 18 || mouseXIn > j + 315) {
                            return;
                        }
                        s = this.getHeaderDescriptionId(4);
                    }
                    s = new StringBuilder().append(I18n.format(s, new Object[0])).toString().trim();
                    if (!s.isEmpty()) {
                        final int k = mouseXIn + 12;
                        final int l = mouseYIn - 12;
                        final int i2 = GuiStats.this.fontRendererObj.getStringWidth(s);
                        Gui.this.drawGradientRect(k - 3, l - 3, k + i2 + 3, l + 8 + 3, -1073741824, -1073741824);
                        GuiStats.this.fontRendererObj.drawStringWithShadow(s, (float)k, (float)l, -1);
                    }
                }
            }
        }
        
        protected void renderMouseHoverToolTip(final StatCrafting p_148213_1_, final int p_148213_2_, final int p_148213_3_) {
            if (p_148213_1_ != null) {
                final Item item = p_148213_1_.getItem();
                final ItemStack itemstack = new ItemStack(item);
                final String s = itemstack.getUnlocalizedName();
                final String s2 = new StringBuilder().append(I18n.format(String.valueOf(s) + ".name", new Object[0])).toString().trim();
                if (!s2.isEmpty()) {
                    final int i = p_148213_2_ + 12;
                    final int j = p_148213_3_ - 12;
                    final int k = GuiStats.this.fontRendererObj.getStringWidth(s2);
                    Gui.this.drawGradientRect(i - 3, j - 3, i + k + 3, j + 8 + 3, -1073741824, -1073741824);
                    GuiStats.this.fontRendererObj.drawStringWithShadow(s2, (float)i, (float)j, -1);
                }
            }
        }
        
        protected void sortByColumn(final int p_148212_1_) {
            if (p_148212_1_ != this.sortColumn) {
                this.sortColumn = p_148212_1_;
                this.sortOrder = -1;
            }
            else if (this.sortOrder == -1) {
                this.sortOrder = 1;
            }
            else {
                this.sortColumn = -1;
                this.sortOrder = 0;
            }
            Collections.sort(this.statsHolder, this.statSorter);
        }
    }
    
    class StatsBlock extends Stats
    {
        public StatsBlock(final Minecraft p_i47554_2_) {
            super(p_i47554_2_);
            this.statsHolder = (List<StatCrafting>)Lists.newArrayList();
            for (final StatCrafting statcrafting : StatList.MINE_BLOCK_STATS) {
                boolean flag = false;
                final Item item = statcrafting.getItem();
                if (GuiStats.this.stats.readStat(statcrafting) > 0) {
                    flag = true;
                }
                else if (StatList.getObjectUseStats(item) != null && GuiStats.this.stats.readStat(StatList.getObjectUseStats(item)) > 0) {
                    flag = true;
                }
                else if (StatList.getCraftStats(item) != null && GuiStats.this.stats.readStat(StatList.getCraftStats(item)) > 0) {
                    flag = true;
                }
                else if (StatList.getObjectsPickedUpStats(item) != null && GuiStats.this.stats.readStat(StatList.getObjectsPickedUpStats(item)) > 0) {
                    flag = true;
                }
                else if (StatList.getDroppedObjectStats(item) != null && GuiStats.this.stats.readStat(StatList.getDroppedObjectStats(item)) > 0) {
                    flag = true;
                }
                if (flag) {
                    this.statsHolder.add(statcrafting);
                }
            }
            this.statSorter = new Comparator<StatCrafting>() {
                @Override
                public int compare(final StatCrafting p_compare_1_, final StatCrafting p_compare_2_) {
                    final Item item1 = p_compare_1_.getItem();
                    final Item item2 = p_compare_2_.getItem();
                    StatBase statbase = null;
                    StatBase statbase2 = null;
                    if (StatsBlock.this.sortColumn == 2) {
                        statbase = StatList.getBlockStats(Block.getBlockFromItem(item1));
                        statbase2 = StatList.getBlockStats(Block.getBlockFromItem(item2));
                    }
                    else if (StatsBlock.this.sortColumn == 0) {
                        statbase = StatList.getCraftStats(item1);
                        statbase2 = StatList.getCraftStats(item2);
                    }
                    else if (StatsBlock.this.sortColumn == 1) {
                        statbase = StatList.getObjectUseStats(item1);
                        statbase2 = StatList.getObjectUseStats(item2);
                    }
                    else if (StatsBlock.this.sortColumn == 3) {
                        statbase = StatList.getObjectsPickedUpStats(item1);
                        statbase2 = StatList.getObjectsPickedUpStats(item2);
                    }
                    else if (StatsBlock.this.sortColumn == 4) {
                        statbase = StatList.getDroppedObjectStats(item1);
                        statbase2 = StatList.getDroppedObjectStats(item2);
                    }
                    if (statbase != null || statbase2 != null) {
                        if (statbase == null) {
                            return 1;
                        }
                        if (statbase2 == null) {
                            return -1;
                        }
                        final int i = GuiStats.this.stats.readStat(statbase);
                        final int j = GuiStats.this.stats.readStat(statbase2);
                        if (i != j) {
                            return (i - j) * StatsBlock.this.sortOrder;
                        }
                    }
                    return Item.getIdFromItem(item1) - Item.getIdFromItem(item2);
                }
            };
        }
        
        @Override
        protected void drawListHeader(final int insideLeft, final int insideTop, final Tessellator tessellatorIn) {
            super.drawListHeader(insideLeft, insideTop, tessellatorIn);
            if (this.headerPressed == 0) {
                GuiStats.this.drawSprite(insideLeft + 115 - 18 + 1, insideTop + 1 + 1, 18, 18);
            }
            else {
                GuiStats.this.drawSprite(insideLeft + 115 - 18, insideTop + 1, 18, 18);
            }
            if (this.headerPressed == 1) {
                GuiStats.this.drawSprite(insideLeft + 165 - 18 + 1, insideTop + 1 + 1, 36, 18);
            }
            else {
                GuiStats.this.drawSprite(insideLeft + 165 - 18, insideTop + 1, 36, 18);
            }
            if (this.headerPressed == 2) {
                GuiStats.this.drawSprite(insideLeft + 215 - 18 + 1, insideTop + 1 + 1, 54, 18);
            }
            else {
                GuiStats.this.drawSprite(insideLeft + 215 - 18, insideTop + 1, 54, 18);
            }
            if (this.headerPressed == 3) {
                GuiStats.this.drawSprite(insideLeft + 265 - 18 + 1, insideTop + 1 + 1, 90, 18);
            }
            else {
                GuiStats.this.drawSprite(insideLeft + 265 - 18, insideTop + 1, 90, 18);
            }
            if (this.headerPressed == 4) {
                GuiStats.this.drawSprite(insideLeft + 315 - 18 + 1, insideTop + 1 + 1, 108, 18);
            }
            else {
                GuiStats.this.drawSprite(insideLeft + 315 - 18, insideTop + 1, 108, 18);
            }
        }
        
        @Override
        protected void func_192637_a(final int p_192637_1_, final int p_192637_2_, final int p_192637_3_, final int p_192637_4_, final int p_192637_5_, final int p_192637_6_, final float p_192637_7_) {
            final StatCrafting statcrafting = this.getSlotStat(p_192637_1_);
            final Item item = statcrafting.getItem();
            GuiStats.this.drawStatsScreen(p_192637_2_ + 40, p_192637_3_, item);
            this.renderStat(StatList.getCraftStats(item), p_192637_2_ + 115, p_192637_3_, p_192637_1_ % 2 == 0);
            this.renderStat(StatList.getObjectUseStats(item), p_192637_2_ + 165, p_192637_3_, p_192637_1_ % 2 == 0);
            this.renderStat(statcrafting, p_192637_2_ + 215, p_192637_3_, p_192637_1_ % 2 == 0);
            this.renderStat(StatList.getObjectsPickedUpStats(item), p_192637_2_ + 265, p_192637_3_, p_192637_1_ % 2 == 0);
            this.renderStat(StatList.getDroppedObjectStats(item), p_192637_2_ + 315, p_192637_3_, p_192637_1_ % 2 == 0);
        }
        
        @Override
        protected String getHeaderDescriptionId(final int p_148210_1_) {
            if (p_148210_1_ == 0) {
                return "stat.crafted";
            }
            if (p_148210_1_ == 1) {
                return "stat.used";
            }
            if (p_148210_1_ == 3) {
                return "stat.pickup";
            }
            return (p_148210_1_ == 4) ? "stat.dropped" : "stat.mined";
        }
    }
    
    class StatsGeneral extends GuiSlot
    {
        public StatsGeneral(final Minecraft p_i47553_2_) {
            super(p_i47553_2_, GuiStats.width, GuiStats.height, 32, GuiStats.height - 64, 10);
            this.func_193651_b(false);
        }
        
        @Override
        protected int getSize() {
            return StatList.BASIC_STATS.size();
        }
        
        @Override
        protected void elementClicked(final int slotIndex, final boolean isDoubleClick, final int mouseX, final int mouseY) {
        }
        
        @Override
        protected boolean isSelected(final int slotIndex) {
            return false;
        }
        
        @Override
        protected int getContentHeight() {
            return this.getSize() * 10;
        }
        
        @Override
        protected void drawBackground() {
            GuiStats.this.drawDefaultBackground();
        }
        
        @Override
        protected void func_192637_a(final int p_192637_1_, final int p_192637_2_, final int p_192637_3_, final int p_192637_4_, final int p_192637_5_, final int p_192637_6_, final float p_192637_7_) {
            final StatBase statbase = StatList.BASIC_STATS.get(p_192637_1_);
            Gui.drawString(GuiStats.this.fontRendererObj, statbase.getStatName().getUnformattedText(), p_192637_2_ + 2, p_192637_3_ + 1, (p_192637_1_ % 2 == 0) ? 16777215 : 9474192);
            final String s = statbase.format(GuiStats.this.stats.readStat(statbase));
            Gui.drawString(GuiStats.this.fontRendererObj, s, p_192637_2_ + 2 + 213 - GuiStats.this.fontRendererObj.getStringWidth(s), p_192637_3_ + 1, (p_192637_1_ % 2 == 0) ? 16777215 : 9474192);
        }
    }
    
    class StatsItem extends Stats
    {
        public StatsItem(final Minecraft p_i47552_2_) {
            super(p_i47552_2_);
            this.statsHolder = (List<StatCrafting>)Lists.newArrayList();
            for (final StatCrafting statcrafting : StatList.USE_ITEM_STATS) {
                boolean flag = false;
                final Item item = statcrafting.getItem();
                if (GuiStats.this.stats.readStat(statcrafting) > 0) {
                    flag = true;
                }
                else if (StatList.getObjectBreakStats(item) != null && GuiStats.this.stats.readStat(StatList.getObjectBreakStats(item)) > 0) {
                    flag = true;
                }
                else if (StatList.getCraftStats(item) != null && GuiStats.this.stats.readStat(StatList.getCraftStats(item)) > 0) {
                    flag = true;
                }
                else if (StatList.getObjectsPickedUpStats(item) != null && GuiStats.this.stats.readStat(StatList.getObjectsPickedUpStats(item)) > 0) {
                    flag = true;
                }
                else if (StatList.getDroppedObjectStats(item) != null && GuiStats.this.stats.readStat(StatList.getDroppedObjectStats(item)) > 0) {
                    flag = true;
                }
                if (flag) {
                    this.statsHolder.add(statcrafting);
                }
            }
            this.statSorter = new Comparator<StatCrafting>() {
                @Override
                public int compare(final StatCrafting p_compare_1_, final StatCrafting p_compare_2_) {
                    final Item item1 = p_compare_1_.getItem();
                    final Item item2 = p_compare_2_.getItem();
                    final int i = Item.getIdFromItem(item1);
                    final int j = Item.getIdFromItem(item2);
                    StatBase statbase = null;
                    StatBase statbase2 = null;
                    if (StatsItem.this.sortColumn == 0) {
                        statbase = StatList.getObjectBreakStats(item1);
                        statbase2 = StatList.getObjectBreakStats(item2);
                    }
                    else if (StatsItem.this.sortColumn == 1) {
                        statbase = StatList.getCraftStats(item1);
                        statbase2 = StatList.getCraftStats(item2);
                    }
                    else if (StatsItem.this.sortColumn == 2) {
                        statbase = StatList.getObjectUseStats(item1);
                        statbase2 = StatList.getObjectUseStats(item2);
                    }
                    else if (StatsItem.this.sortColumn == 3) {
                        statbase = StatList.getObjectsPickedUpStats(item1);
                        statbase2 = StatList.getObjectsPickedUpStats(item2);
                    }
                    else if (StatsItem.this.sortColumn == 4) {
                        statbase = StatList.getDroppedObjectStats(item1);
                        statbase2 = StatList.getDroppedObjectStats(item2);
                    }
                    if (statbase != null || statbase2 != null) {
                        if (statbase == null) {
                            return 1;
                        }
                        if (statbase2 == null) {
                            return -1;
                        }
                        final int k = GuiStats.this.stats.readStat(statbase);
                        final int l = GuiStats.this.stats.readStat(statbase2);
                        if (k != l) {
                            return (k - l) * StatsItem.this.sortOrder;
                        }
                    }
                    return i - j;
                }
            };
        }
        
        @Override
        protected void drawListHeader(final int insideLeft, final int insideTop, final Tessellator tessellatorIn) {
            super.drawListHeader(insideLeft, insideTop, tessellatorIn);
            if (this.headerPressed == 0) {
                GuiStats.this.drawSprite(insideLeft + 115 - 18 + 1, insideTop + 1 + 1, 72, 18);
            }
            else {
                GuiStats.this.drawSprite(insideLeft + 115 - 18, insideTop + 1, 72, 18);
            }
            if (this.headerPressed == 1) {
                GuiStats.this.drawSprite(insideLeft + 165 - 18 + 1, insideTop + 1 + 1, 18, 18);
            }
            else {
                GuiStats.this.drawSprite(insideLeft + 165 - 18, insideTop + 1, 18, 18);
            }
            if (this.headerPressed == 2) {
                GuiStats.this.drawSprite(insideLeft + 215 - 18 + 1, insideTop + 1 + 1, 36, 18);
            }
            else {
                GuiStats.this.drawSprite(insideLeft + 215 - 18, insideTop + 1, 36, 18);
            }
            if (this.headerPressed == 3) {
                GuiStats.this.drawSprite(insideLeft + 265 - 18 + 1, insideTop + 1 + 1, 90, 18);
            }
            else {
                GuiStats.this.drawSprite(insideLeft + 265 - 18, insideTop + 1, 90, 18);
            }
            if (this.headerPressed == 4) {
                GuiStats.this.drawSprite(insideLeft + 315 - 18 + 1, insideTop + 1 + 1, 108, 18);
            }
            else {
                GuiStats.this.drawSprite(insideLeft + 315 - 18, insideTop + 1, 108, 18);
            }
        }
        
        @Override
        protected void func_192637_a(final int p_192637_1_, final int p_192637_2_, final int p_192637_3_, final int p_192637_4_, final int p_192637_5_, final int p_192637_6_, final float p_192637_7_) {
            final StatCrafting statcrafting = this.getSlotStat(p_192637_1_);
            final Item item = statcrafting.getItem();
            GuiStats.this.drawStatsScreen(p_192637_2_ + 40, p_192637_3_, item);
            this.renderStat(StatList.getObjectBreakStats(item), p_192637_2_ + 115, p_192637_3_, p_192637_1_ % 2 == 0);
            this.renderStat(StatList.getCraftStats(item), p_192637_2_ + 165, p_192637_3_, p_192637_1_ % 2 == 0);
            this.renderStat(statcrafting, p_192637_2_ + 215, p_192637_3_, p_192637_1_ % 2 == 0);
            this.renderStat(StatList.getObjectsPickedUpStats(item), p_192637_2_ + 265, p_192637_3_, p_192637_1_ % 2 == 0);
            this.renderStat(StatList.getDroppedObjectStats(item), p_192637_2_ + 315, p_192637_3_, p_192637_1_ % 2 == 0);
        }
        
        @Override
        protected String getHeaderDescriptionId(final int p_148210_1_) {
            if (p_148210_1_ == 1) {
                return "stat.crafted";
            }
            if (p_148210_1_ == 2) {
                return "stat.used";
            }
            if (p_148210_1_ == 3) {
                return "stat.pickup";
            }
            return (p_148210_1_ == 4) ? "stat.dropped" : "stat.depleted";
        }
    }
    
    class StatsMobsList extends GuiSlot
    {
        private final List<EntityList.EntityEggInfo> mobs;
        
        public StatsMobsList(final Minecraft p_i47551_2_) {
            super(p_i47551_2_, GuiStats.width, GuiStats.height, 32, GuiStats.height - 64, GuiStats.this.fontRendererObj.FONT_HEIGHT * 4);
            this.mobs = (List<EntityList.EntityEggInfo>)Lists.newArrayList();
            this.func_193651_b(false);
            for (final EntityList.EntityEggInfo entitylist$entityegginfo : EntityList.ENTITY_EGGS.values()) {
                if (GuiStats.this.stats.readStat(entitylist$entityegginfo.killEntityStat) > 0 || GuiStats.this.stats.readStat(entitylist$entityegginfo.entityKilledByStat) > 0) {
                    this.mobs.add(entitylist$entityegginfo);
                }
            }
        }
        
        @Override
        protected int getSize() {
            return this.mobs.size();
        }
        
        @Override
        protected void elementClicked(final int slotIndex, final boolean isDoubleClick, final int mouseX, final int mouseY) {
        }
        
        @Override
        protected boolean isSelected(final int slotIndex) {
            return false;
        }
        
        @Override
        protected int getContentHeight() {
            return this.getSize() * GuiStats.this.fontRendererObj.FONT_HEIGHT * 4;
        }
        
        @Override
        protected void drawBackground() {
            GuiStats.this.drawDefaultBackground();
        }
        
        @Override
        protected void func_192637_a(final int p_192637_1_, final int p_192637_2_, final int p_192637_3_, final int p_192637_4_, final int p_192637_5_, final int p_192637_6_, final float p_192637_7_) {
            final EntityList.EntityEggInfo entitylist$entityegginfo = this.mobs.get(p_192637_1_);
            final String s = I18n.format("entity." + EntityList.func_191302_a(entitylist$entityegginfo.spawnedID) + ".name", new Object[0]);
            final int i = GuiStats.this.stats.readStat(entitylist$entityegginfo.killEntityStat);
            final int j = GuiStats.this.stats.readStat(entitylist$entityegginfo.entityKilledByStat);
            String s2 = I18n.format("stat.entityKills", i, s);
            String s3 = I18n.format("stat.entityKilledBy", s, j);
            if (i == 0) {
                s2 = I18n.format("stat.entityKills.none", s);
            }
            if (j == 0) {
                s3 = I18n.format("stat.entityKilledBy.none", s);
            }
            Gui.drawString(GuiStats.this.fontRendererObj, s, p_192637_2_ + 2 - 10, p_192637_3_ + 1, 16777215);
            Gui.drawString(GuiStats.this.fontRendererObj, s2, p_192637_2_ + 2, p_192637_3_ + 1 + GuiStats.this.fontRendererObj.FONT_HEIGHT, (i == 0) ? 6316128 : 9474192);
            Gui.drawString(GuiStats.this.fontRendererObj, s3, p_192637_2_ + 2, p_192637_3_ + 1 + GuiStats.this.fontRendererObj.FONT_HEIGHT * 2, (j == 0) ? 6316128 : 9474192);
        }
    }
}
