/*
 * Decompiled with CFR 0.152.
 */
package me.nzxtercode.bettercraft.client.gui.serverlists.partner;

import com.google.common.collect.Lists;
import java.util.List;
import me.nzxtercode.bettercraft.client.gui.serverlists.partner.GuiPartnerMultiplayer;
import me.nzxtercode.bettercraft.client.gui.serverlists.partner.PartnerServerList;
import me.nzxtercode.bettercraft.client.gui.serverlists.partner.PartnerServerListEntryNormal;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.gui.ServerListEntryLanScan;

public class PartnerServerSelectionList
extends GuiListExtended {
    private final GuiPartnerMultiplayer owner;
    private final List<PartnerServerListEntryNormal> normalServers = Lists.newArrayList();
    private final GuiListExtended.IGuiListEntry lanScanEntry = new ServerListEntryLanScan();
    private int selectedSlotIndex = -1;

    public PartnerServerSelectionList(GuiPartnerMultiplayer ownerIn, Minecraft mcIn, int widthIn, int heightIn, int topIn, int bottomIn, int slotHeightIn) {
        super(mcIn, widthIn, heightIn, topIn, bottomIn, slotHeightIn);
        this.owner = ownerIn;
    }

    @Override
    public GuiListExtended.IGuiListEntry getListEntry(int index) {
        if (index < this.normalServers.size()) {
            return this.normalServers.get(index);
        }
        if ((index -= this.normalServers.size()) == 0) {
            return this.lanScanEntry;
        }
        --index;
        return null;
    }

    @Override
    public int getSize() {
        return this.normalServers.size() + 1;
    }

    public void setSelectedSlotIndex(int selectedSlotIndexIn) {
        this.selectedSlotIndex = selectedSlotIndexIn;
    }

    @Override
    protected boolean isSelected(int slotIndex) {
        return slotIndex == this.selectedSlotIndex;
    }

    public int func_148193_k() {
        return this.selectedSlotIndex;
    }

    public void func_148195_a(PartnerServerList savedServerList) {
        this.normalServers.clear();
        int i2 = 0;
        while (i2 < savedServerList.countServers()) {
            this.normalServers.add(new PartnerServerListEntryNormal(this.owner, savedServerList.getServerData(i2)));
            ++i2;
        }
    }

    @Override
    protected int getScrollBarX() {
        return super.getScrollBarX() + 30;
    }

    @Override
    public int getListWidth() {
        return super.getListWidth() + 85;
    }

    @Override
    public void drawScreen(int mouseXIn, int mouseYIn, float p_148128_3_) {
        super.drawScreen(mouseXIn, mouseYIn, p_148128_3_);
    }

    public void clear() {
        this.normalServers.clear();
    }
}

