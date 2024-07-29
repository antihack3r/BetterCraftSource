/*
 * Decompiled with CFR 0.152.
 */
package com.mcf.davidee.nbtedit.gui;

import com.mcf.davidee.nbtedit.NBTStringHelper;
import com.mcf.davidee.nbtedit.gui.GuiCharacterButton;
import com.mcf.davidee.nbtedit.gui.GuiNBTTree;
import com.mcf.davidee.nbtedit.gui.GuiTextField;
import com.mcf.davidee.nbtedit.nbt.NamedNBT;
import com.mcf.davidee.nbtedit.nbt.Node;
import com.mcf.davidee.nbtedit.nbt.ParseHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiEditNBT
extends Gui {
    public static final ResourceLocation WINDOW_TEXTURE = new ResourceLocation("nbtedit", "textures/gui/window.png");
    public static final int WIDTH = 178;
    public static final int HEIGHT = 93;
    private Minecraft mc = Minecraft.getMinecraft();
    private Node<NamedNBT> node;
    private NBTBase nbt;
    private boolean canEditText;
    private boolean canEditValue;
    private GuiNBTTree parent;
    private int x;
    private int y;
    private GuiTextField key;
    private GuiTextField value;
    private GuiButton save;
    private GuiButton cancel;
    private String kError;
    private String vError;
    private GuiCharacterButton newLine;
    private GuiCharacterButton section;

    public GuiEditNBT(GuiNBTTree parent, Node<NamedNBT> node, boolean editText, boolean editValue) {
        this.parent = parent;
        this.node = node;
        this.nbt = node.getObject().getNBT();
        this.canEditText = editText;
        this.canEditValue = editValue;
    }

    public void initGUI(int x2, int y2) {
        this.x = x2;
        this.y = y2;
        this.section = new GuiCharacterButton(0, x2 + 178 - 1, y2 + 34);
        this.newLine = new GuiCharacterButton(1, x2 + 178 - 1, y2 + 50);
        String sKey = this.key == null ? this.node.getObject().getName() : this.key.getText();
        String sValue = this.value == null ? GuiEditNBT.getValue(this.nbt) : this.value.getText();
        this.key = new GuiTextField(this.mc.fontRendererObj, x2 + 46, y2 + 18, 116, 15, false);
        this.value = new GuiTextField(this.mc.fontRendererObj, x2 + 46, y2 + 44, 116, 15, true);
        this.key.setText(sKey);
        this.key.setEnableBackgroundDrawing(false);
        this.key.func_82265_c(this.canEditText);
        this.value.setMaxStringLength(256);
        this.value.setText(sValue);
        this.value.setEnableBackgroundDrawing(false);
        this.value.func_82265_c(this.canEditValue);
        this.save = new GuiButton(1, x2 + 9, y2 + 62, 75, 20, "Save");
        if (!this.key.isFocused() && !this.value.isFocused()) {
            if (this.canEditText) {
                this.key.setFocused(true);
            } else if (this.canEditValue) {
                this.value.setFocused(true);
            }
        }
        this.section.setEnabled(this.value.isFocused());
        this.newLine.setEnabled(this.value.isFocused());
        this.cancel = new GuiButton(0, x2 + 93, y2 + 62, 75, 20, "Cancel");
    }

    public void click(int mx2, int my2) {
        if (this.newLine.inBounds(mx2, my2) && this.value.isFocused()) {
            this.value.writeText("\n");
            this.checkValidInput();
        } else if (this.section.inBounds(mx2, my2) && this.value.isFocused()) {
            this.value.writeText("\u00a7");
            this.checkValidInput();
        } else {
            this.key.mouseClicked(mx2, my2, 0);
            this.value.mouseClicked(mx2, my2, 0);
            if (this.save.mousePressed(this.mc, mx2, my2)) {
                this.saveAndQuit();
            }
            if (this.cancel.mousePressed(this.mc, mx2, my2)) {
                this.parent.closeWindow();
            }
            this.section.setEnabled(this.value.isFocused());
            this.newLine.setEnabled(this.value.isFocused());
        }
    }

    private void saveAndQuit() {
        this.mc.thePlayer.getHeldItem().setTagCompound(this.parent.getNBTTree().toNBTTagCompound());
        if (this.canEditText) {
            this.node.getObject().setName(this.key.getText());
        }
        GuiEditNBT.setValidValue(this.node, this.value.getText());
        this.parent.nodeEdited(this.node);
        this.parent.closeWindow();
    }

    public void draw(int mx2, int my2) {
        this.mc.renderEngine.bindTexture(WINDOW_TEXTURE);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.drawTexturedModalRect(this.x, this.y, 0, 0, 178, 93);
        if (!this.canEditText) {
            GuiEditNBT.drawRect(this.x + 42, this.y + 15, this.x + 169, this.y + 31, Integer.MIN_VALUE);
        }
        if (!this.canEditValue) {
            GuiEditNBT.drawRect(this.x + 42, this.y + 41, this.x + 169, this.y + 57, Integer.MIN_VALUE);
        }
        this.key.drawTextBox();
        this.value.drawTextBox();
        this.save.drawButton(this.mc, mx2, my2);
        this.cancel.drawButton(this.mc, mx2, my2);
        if (this.kError != null) {
            this.drawCenteredStringg(this.mc.fontRendererObj, this.kError, this.x + 89, this.y + 4, 0xFF0000);
        }
        if (this.vError != null) {
            this.drawCenteredStringg(this.mc.fontRendererObj, this.vError, this.x + 89, this.y + 32, 0xFF0000);
        }
        this.newLine.draw(mx2, my2);
        this.section.draw(mx2, my2);
    }

    public void drawCenteredStringg(FontRenderer par1FontRenderer, String par2Str, int par3, int par4, int par5) {
        par1FontRenderer.drawString(par2Str, par3 - par1FontRenderer.getStringWidth(par2Str) / 2, par4, par5);
    }

    public void update() {
        this.value.updateCursorCounter();
        this.key.updateCursorCounter();
    }

    public void keyTyped(char c2, int i2) {
        if (i2 == 1) {
            this.parent.closeWindow();
        } else if (i2 == 15) {
            if (this.key.isFocused() && this.canEditValue) {
                this.key.setFocused(false);
                this.value.setFocused(true);
            } else if (this.value.isFocused() && this.canEditText) {
                this.key.setFocused(true);
                this.value.setFocused(false);
            }
            this.section.setEnabled(this.value.isFocused());
            this.newLine.setEnabled(this.value.isFocused());
        } else if (i2 == 28) {
            this.checkValidInput();
            if (this.save.enabled) {
                this.saveAndQuit();
            }
        } else {
            this.key.textboxKeyTyped(c2, i2);
            this.value.textboxKeyTyped(c2, i2);
            this.checkValidInput();
        }
    }

    private void checkValidInput() {
        boolean valid = true;
        this.kError = null;
        this.vError = null;
        if (this.canEditText && !this.validName()) {
            valid = false;
            this.kError = "Duplicate Tag Name";
        }
        try {
            GuiEditNBT.validValue(this.value.getText(), this.nbt.getId());
            valid &= true;
        }
        catch (NumberFormatException e2) {
            this.vError = e2.getMessage();
            valid = false;
        }
        this.save.enabled = valid;
    }

    private boolean validName() {
        for (Node<NamedNBT> node : this.node.getParent().getChildren()) {
            NBTBase base = node.getObject().getNBT();
            if (base == this.nbt || !node.getObject().getName().equals(this.key.getText())) continue;
            return false;
        }
        return true;
    }

    private static void setValidValue(Node<NamedNBT> node, String value) {
        NamedNBT named = node.getObject();
        NBTBase base = named.getNBT();
        if (base instanceof NBTTagByte) {
            named.setNBT(new NBTTagByte(ParseHelper.parseByte(value)));
        }
        if (base instanceof NBTTagShort) {
            named.setNBT(new NBTTagShort(ParseHelper.parseShort(value)));
        }
        if (base instanceof NBTTagInt) {
            named.setNBT(new NBTTagInt(ParseHelper.parseInt(value)));
        }
        if (base instanceof NBTTagLong) {
            named.setNBT(new NBTTagLong(ParseHelper.parseLong(value)));
        }
        if (base instanceof NBTTagFloat) {
            named.setNBT(new NBTTagFloat(ParseHelper.parseFloat(value)));
        }
        if (base instanceof NBTTagDouble) {
            named.setNBT(new NBTTagDouble(ParseHelper.parseDouble(value)));
        }
        if (base instanceof NBTTagByteArray) {
            named.setNBT(new NBTTagByteArray(ParseHelper.parseByteArray(value)));
        }
        if (base instanceof NBTTagIntArray) {
            named.setNBT(new NBTTagIntArray(ParseHelper.parseIntArray(value)));
        }
        if (base instanceof NBTTagString) {
            named.setNBT(new NBTTagString(value));
        }
    }

    private static void validValue(String value, byte type) throws NumberFormatException {
        switch (type) {
            case 1: {
                ParseHelper.parseByte(value);
                break;
            }
            case 2: {
                ParseHelper.parseShort(value);
                break;
            }
            case 3: {
                ParseHelper.parseInt(value);
                break;
            }
            case 4: {
                ParseHelper.parseLong(value);
                break;
            }
            case 5: {
                ParseHelper.parseFloat(value);
                break;
            }
            case 6: {
                ParseHelper.parseDouble(value);
                break;
            }
            case 7: {
                ParseHelper.parseByteArray(value);
                break;
            }
            case 11: {
                ParseHelper.parseIntArray(value);
            }
        }
    }

    private static String getValue(NBTBase base) {
        switch (base.getId()) {
            case 7: {
                String s2 = "";
                byte[] byArray = ((NBTTagByteArray)base).getByteArray();
                int n2 = byArray.length;
                int n3 = 0;
                while (n3 < n2) {
                    byte b2 = byArray[n3];
                    s2 = String.valueOf(s2) + b2 + " ";
                    ++n3;
                }
                return s2;
            }
            case 9: {
                return "TagList";
            }
            case 10: {
                return "TagCompound";
            }
            case 11: {
                String i2 = "";
                int[] nArray = ((NBTTagIntArray)base).getIntArray();
                int n4 = nArray.length;
                int n5 = 0;
                while (n5 < n4) {
                    int a2 = nArray[n5];
                    i2 = String.valueOf(i2) + a2 + " ";
                    ++n5;
                }
                return i2;
            }
        }
        return NBTStringHelper.toString(base);
    }
}

