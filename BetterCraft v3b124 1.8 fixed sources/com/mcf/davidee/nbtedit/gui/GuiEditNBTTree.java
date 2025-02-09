/*
 * Decompiled with CFR 0.152.
 */
package com.mcf.davidee.nbtedit.gui;

import com.mcf.davidee.nbtedit.gui.GuiEditNBT;
import com.mcf.davidee.nbtedit.gui.GuiNBTTree;
import com.mcf.davidee.nbtedit.nbt.NBTTree;
import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class GuiEditNBTTree
extends GuiScreen {
    public final int entityOrX;
    public final int y;
    public final int z;
    private boolean entity;
    protected String screenTitle;
    private GuiNBTTree guiTree;

    public GuiEditNBTTree(int entity, NBTTagCompound tag) {
        this.entity = true;
        this.entityOrX = entity;
        this.y = 0;
        this.z = 0;
        this.screenTitle = "NBTEdit -- EntityId #" + this.entityOrX;
        this.guiTree = new GuiNBTTree(new NBTTree(tag));
    }

    public GuiEditNBTTree(BlockPos pos, NBTTagCompound tag) {
        this.entity = false;
        this.entityOrX = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
        this.screenTitle = "NBTEdit -- TileEntity at " + pos.getX() + "," + pos.getY() + "," + pos.getZ();
        this.guiTree = new GuiNBTTree(new NBTTree(tag));
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        this.buttonList.clear();
        this.guiTree.initGUI(width, height, height - 35);
        this.buttonList.add(new GuiButton(1, width / 4 - 100, height - 27, 200, 20, "Save"));
        this.buttonList.add(new GuiButton(0, width * 3 / 4 - 100, height - 27, 200, 20, "Quit"));
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    protected void keyTyped(char par1, int key) {
        GuiEditNBT window = this.guiTree.getWindow();
        if (window != null) {
            window.keyTyped(par1, key);
        } else if (key == 1) {
            if (this.guiTree.isEditingSlot()) {
                this.guiTree.stopEditingSlot();
            } else {
                this.quitWithoutSaving();
            }
        } else if (key == 211) {
            this.guiTree.deleteSelected();
        } else if (key == 28) {
            this.guiTree.editSelected();
        } else if (key == 200) {
            this.guiTree.arrowKeyPressed(true);
        } else if (key == 208) {
            this.guiTree.arrowKeyPressed(false);
        } else {
            this.guiTree.keyTyped(par1, key);
        }
    }

    @Override
    protected void mouseClicked(int x2, int y2, int t2) throws IOException {
        if (this.guiTree.getWindow() == null) {
            super.mouseClicked(x2, y2, t2);
        }
        if (t2 == 0) {
            this.guiTree.mouseClicked(x2, y2);
        }
        if (t2 == 1) {
            this.guiTree.rightClick(x2, y2);
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int ofs = Mouse.getEventDWheel();
        if (ofs != 0) {
            this.guiTree.shift(ofs >= 1 ? 6 : -6);
        }
    }

    @Override
    protected void actionPerformed(GuiButton b2) {
        if (b2.enabled) {
            switch (b2.id) {
                case 1: {
                    this.quitWithSave();
                    break;
                }
                default: {
                    this.quitWithoutSaving();
                }
            }
        }
    }

    @Override
    public void updateScreen() {
        if (!this.mc.thePlayer.isEntityAlive()) {
            this.quitWithoutSaving();
        } else {
            this.guiTree.updateScreen();
        }
    }

    private void quitWithSave() {
        Minecraft.getMinecraft().thePlayer.getHeldItem().setTagCompound(this.guiTree.getNBTTree().toNBTTagCompound());
        new Thread(() -> {
            try {
                this.mc.displayGuiScreen(new GuiInventory(this.mc.thePlayer));
                Thread.sleep(20L);
                this.mc.displayGuiScreen(null);
            }
            catch (Exception exception) {
                // empty catch block
            }
        }).start();
        try {
            this.mc.setIngameFocus();
        }
        catch (NullPointerException nullPointerException) {
            // empty catch block
        }
    }

    private void quitWithoutSaving() {
        this.quitWithSave();
    }

    @Override
    public void drawScreen(int x2, int y2, float par3) {
        this.drawDefaultBackground();
        this.guiTree.draw(x2, y2);
        GuiEditNBTTree.drawCenteredString(this.mc.fontRendererObj, this.screenTitle, width / 2, 5, 0xFFFFFF);
        if (this.guiTree.getWindow() == null) {
            super.drawScreen(x2, y2, par3);
        } else {
            super.drawScreen(-1, -1, par3);
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return true;
    }

    public Entity getEntity() {
        return this.entity ? this.mc.theWorld.getEntityByID(this.entityOrX) : null;
    }

    public boolean isTileEntity() {
        return !this.entity;
    }

    public int getBlockX() {
        return this.entity ? 0 : this.entityOrX;
    }
}

