/*
 * Decompiled with CFR 0.152.
 */
package com.mcf.davidee.nbtedit.gui;

import com.mcf.davidee.nbtedit.NBTEdit;
import com.mcf.davidee.nbtedit.NBTHelper;
import com.mcf.davidee.nbtedit.NBTStringHelper;
import com.mcf.davidee.nbtedit.gui.GuiEditNBT;
import com.mcf.davidee.nbtedit.gui.GuiNBTButton;
import com.mcf.davidee.nbtedit.gui.GuiNBTNode;
import com.mcf.davidee.nbtedit.gui.GuiSaveSlotButton;
import com.mcf.davidee.nbtedit.nbt.NBTTree;
import com.mcf.davidee.nbtedit.nbt.NamedNBT;
import com.mcf.davidee.nbtedit.nbt.Node;
import com.mcf.davidee.nbtedit.nbt.SaveStates;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiControls;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import org.apache.logging.log4j.Level;
import org.lwjgl.input.Mouse;

public class GuiNBTTree
extends Gui {
    private final Minecraft mc = Minecraft.getMinecraft();
    private NBTTree tree;
    private final List<GuiNBTNode> nodes;
    private final GuiSaveSlotButton[] saves;
    private final GuiNBTButton[] buttons;
    private final int X_GAP = 10;
    private final int START_X = 10;
    private final int START_Y = 30;
    private final int Y_GAP;
    private int y;
    private int yClick;
    private int bottom;
    private int width;
    private int height;
    private int heightDiff;
    private int offset;
    private Node<NamedNBT> focused;
    private int focusedSlotIndex;
    private GuiEditNBT window;

    public Node<NamedNBT> getFocused() {
        return this.focused;
    }

    public GuiSaveSlotButton getFocusedSaveSlot() {
        return this.focusedSlotIndex != -1 ? this.saves[this.focusedSlotIndex] : null;
    }

    public NBTTree getNBTTree() {
        return this.tree;
    }

    public GuiNBTTree(NBTTree tree) {
        this.Y_GAP = Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT + 2;
        this.tree = tree;
        this.yClick = -1;
        this.focusedSlotIndex = -1;
        this.nodes = new ArrayList<GuiNBTNode>();
        this.buttons = new GuiNBTButton[16];
        this.saves = new GuiSaveSlotButton[7];
    }

    private int getHeightDifference() {
        return this.getContentHeight() - (this.bottom - 30 + 2);
    }

    private int getContentHeight() {
        return this.Y_GAP * this.nodes.size();
    }

    public GuiEditNBT getWindow() {
        return this.window;
    }

    public void initGUI(int width, int height, int bottom) {
        this.width = width;
        this.height = height;
        this.bottom = bottom;
        this.yClick = -1;
        this.initGUI(false);
        if (this.window != null) {
            this.window.initGUI((width - 178) / 2, (height - 93) / 2);
        }
    }

    public void updateScreen() {
        if (this.window != null) {
            this.window.update();
        }
        if (this.focusedSlotIndex != -1) {
            this.saves[this.focusedSlotIndex].update();
        }
    }

    private void setFocused(Node<NamedNBT> toFocus) {
        if (toFocus == null) {
            GuiNBTButton[] guiNBTButtonArray = this.buttons;
            int n2 = this.buttons.length;
            int n3 = 0;
            while (n3 < n2) {
                GuiNBTButton b2 = guiNBTButtonArray[n3];
                b2.setEnabled(false);
                ++n3;
            }
        } else if (toFocus.getObject().getNBT() instanceof NBTTagCompound) {
            GuiNBTButton[] guiNBTButtonArray = this.buttons;
            int n4 = this.buttons.length;
            int n5 = 0;
            while (n5 < n4) {
                GuiNBTButton b3 = guiNBTButtonArray[n5];
                b3.setEnabled(true);
                ++n5;
            }
            this.buttons[12].setEnabled(toFocus != this.tree.getRoot());
            this.buttons[11].setEnabled(toFocus.hasParent() && !(toFocus.getParent().getObject().getNBT() instanceof NBTTagList));
            this.buttons[13].setEnabled(true);
            this.buttons[14].setEnabled(toFocus != this.tree.getRoot());
            this.buttons[15].setEnabled(NBTEdit.clipboard != null);
        } else if (toFocus.getObject().getNBT() instanceof NBTTagList) {
            if (toFocus.hasChildren()) {
                byte type = toFocus.getChildren().get(0).getObject().getNBT().getId();
                GuiNBTButton[] guiNBTButtonArray = this.buttons;
                int n6 = this.buttons.length;
                int n7 = 0;
                while (n7 < n6) {
                    GuiNBTButton b4 = guiNBTButtonArray[n7];
                    b4.setEnabled(false);
                    ++n7;
                }
                this.buttons[type - 1].setEnabled(true);
                this.buttons[12].setEnabled(true);
                this.buttons[11].setEnabled(!(toFocus.getParent().getObject().getNBT() instanceof NBTTagList));
                this.buttons[13].setEnabled(true);
                this.buttons[14].setEnabled(true);
                this.buttons[15].setEnabled(NBTEdit.clipboard != null && NBTEdit.clipboard.getNBT().getId() == type);
            } else {
                GuiNBTButton[] guiNBTButtonArray = this.buttons;
                int n8 = this.buttons.length;
                int n9 = 0;
                while (n9 < n8) {
                    GuiNBTButton b5 = guiNBTButtonArray[n9];
                    b5.setEnabled(true);
                    ++n9;
                }
            }
            this.buttons[11].setEnabled(!(toFocus.getParent().getObject().getNBT() instanceof NBTTagList));
            this.buttons[13].setEnabled(true);
            this.buttons[14].setEnabled(true);
            this.buttons[15].setEnabled(NBTEdit.clipboard != null);
        } else {
            GuiNBTButton[] guiNBTButtonArray = this.buttons;
            int n10 = this.buttons.length;
            int n11 = 0;
            while (n11 < n10) {
                GuiNBTButton b6 = guiNBTButtonArray[n11];
                b6.setEnabled(false);
                ++n11;
            }
            this.buttons[12].setEnabled(true);
            this.buttons[11].setEnabled(true);
            this.buttons[13].setEnabled(true);
            this.buttons[14].setEnabled(true);
            this.buttons[15].setEnabled(false);
        }
        this.focused = toFocus;
        if (this.focused != null && this.focusedSlotIndex != -1) {
            this.stopEditingSlot();
        }
    }

    public void initGUI() {
        this.initGUI(false);
    }

    public void initGUI(boolean shiftToFocused) {
        this.y = 30;
        this.nodes.clear();
        this.addNodes(this.tree.getRoot(), 10);
        this.addButtons();
        if (this.focused != null && !this.checkValidFocus(this.focused)) {
            this.setFocused(null);
        }
        if (this.focusedSlotIndex != -1) {
            this.saves[this.focusedSlotIndex].startEditing();
        }
        this.heightDiff = this.getHeightDifference();
        if (this.heightDiff <= 0) {
            this.offset = 0;
        } else {
            if (this.offset < -this.heightDiff) {
                this.offset = -this.heightDiff;
            }
            if (this.offset > 0) {
                this.offset = 0;
            }
            for (GuiNBTNode node : this.nodes) {
                node.shift(this.offset);
            }
            if (shiftToFocused && this.focused != null) {
                this.shiftTo(this.focused);
            }
        }
    }

    private void addSaveSlotButtons() {
        SaveStates saveStates = NBTEdit.getSaveStates();
        int i2 = 0;
        while (i2 < 7) {
            this.saves[i2] = new GuiSaveSlotButton(saveStates.getSaveState(i2), this.width - 24, 31 + i2 * 25);
            ++i2;
        }
    }

    private void addButtons() {
        int x2 = 18;
        int y2 = 4;
        byte i2 = 14;
        while (i2 < 17) {
            this.buttons[i2 - 1] = new GuiNBTButton(i2, x2, y2);
            x2 += 15;
            i2 = (byte)(i2 + 1);
        }
        x2 += 30;
        i2 = 12;
        while (i2 < 14) {
            this.buttons[i2 - 1] = new GuiNBTButton(i2, x2, y2);
            x2 += 15;
            i2 = (byte)(i2 + 1);
        }
        x2 = 18;
        y2 = 17;
        i2 = 1;
        while (i2 < 12) {
            this.buttons[i2 - 1] = new GuiNBTButton(i2, x2, y2);
            x2 += 9;
            i2 = (byte)(i2 + 1);
        }
    }

    private boolean checkValidFocus(Node<NamedNBT> fc2) {
        for (GuiNBTNode node : this.nodes) {
            if (node.getNode() != fc2) continue;
            this.setFocused(fc2);
            return true;
        }
        return fc2.hasParent() && this.checkValidFocus(fc2.getParent());
    }

    private void addNodes(Node<NamedNBT> node, int x2) {
        this.nodes.add(new GuiNBTNode(this, node, x2, this.y));
        x2 += 10;
        this.y += this.Y_GAP;
        if (node.shouldDrawChildren()) {
            for (Node<NamedNBT> child : node.getChildren()) {
                this.addNodes(child, x2);
            }
        }
    }

    public void draw(int mx2, int my2) {
        int cmx = mx2;
        int cmy = my2;
        if (this.window != null) {
            cmx = -1;
            cmy = -1;
        }
        for (GuiNBTNode node : this.nodes) {
            if (!node.shouldDraw(29, this.bottom)) continue;
            node.draw(cmx, cmy);
        }
        this.overlayBackground(0, 29, 255, 255);
        this.overlayBackground(this.bottom, this.height, 255, 255);
        GuiNBTButton[] guiNBTButtonArray = this.buttons;
        int n2 = this.buttons.length;
        int n3 = 0;
        while (n3 < n2) {
            GuiNBTButton but = guiNBTButtonArray[n3];
            but.draw(cmx, cmy);
            ++n3;
        }
        this.drawScrollBar(cmx, cmy);
        if (this.window != null) {
            this.window.draw(mx2, my2);
        }
    }

    private void drawScrollBar(int mx2, int my2) {
        if (this.heightDiff > 0) {
            int y2;
            if (Mouse.isButtonDown(0)) {
                if (this.yClick == -1) {
                    if (mx2 >= this.width - 20 && mx2 < this.width && my2 >= 29 && my2 < this.bottom) {
                        this.yClick = my2;
                    }
                } else {
                    int length;
                    float scrollMultiplier = 1.0f;
                    int height = this.getHeightDifference();
                    if (height < 1) {
                        height = 1;
                    }
                    if ((length = (this.bottom - 29) * (this.bottom - 29) / this.getContentHeight()) < 32) {
                        length = 32;
                    }
                    if (length > this.bottom - 29 - 8) {
                        length = this.bottom - 29 - 8;
                    }
                    this.shift((int)((float)(this.yClick - my2) * (scrollMultiplier /= (float)(this.bottom - 29 - length) / (float)height)));
                    this.yClick = my2;
                }
            } else {
                this.yClick = -1;
            }
            GuiNBTTree.drawRect(this.width - 20, 29, this.width, this.bottom, Integer.MIN_VALUE);
            int length = (this.bottom - 29) * (this.bottom - 29) / this.getContentHeight();
            if (length < 32) {
                length = 32;
            }
            if (length > this.bottom - 29 - 8) {
                length = this.bottom - 29 - 8;
            }
            if ((y2 = -this.offset * (this.bottom - 29 - length) / this.heightDiff + 29) < 29) {
                y2 = 29;
            }
            this.drawGradientRect(this.width - 20, y2, this.width, y2 + length, -2130706433, -2144128205);
        }
    }

    protected void overlayBackground(int startY, int endY, int startAlpha, int endAlpha) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        this.mc.getTextureManager().bindTexture(Gui.optionsBackground);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        float f2 = 32.0f;
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        worldrenderer.pos(0.0, endY, 0.0).tex(0.0, (float)endY / 32.0f).color(64, 64, 64, endAlpha).endVertex();
        worldrenderer.pos(0 + this.width, endY, 0.0).tex((float)this.width / 32.0f, (float)endY / 32.0f).color(64, 64, 64, endAlpha).endVertex();
        worldrenderer.pos(0 + this.width, startY, 0.0).tex((float)this.width / 32.0f, (float)startY / 32.0f).color(64, 64, 64, startAlpha).endVertex();
        worldrenderer.pos(0.0, startY, 0.0).tex(0.0, (float)startY / 32.0f).color(64, 64, 64, startAlpha).endVertex();
        tessellator.draw();
    }

    public void mouseClicked(int mx2, int my2) {
        if (this.window == null) {
            boolean reInit = false;
            for (GuiNBTNode node : this.nodes) {
                if (!node.hideShowClicked(mx2, my2)) continue;
                reInit = true;
                if (!node.shouldDrawChildren()) break;
                this.offset = 31 - node.y + this.offset;
                break;
            }
            if (!reInit) {
                GuiNBTButton[] guiNBTButtonArray = this.buttons;
                int n2 = this.buttons.length;
                int n3 = 0;
                while (n3 < n2) {
                    GuiNBTButton button = guiNBTButtonArray[n3];
                    if (button.inBounds(mx2, my2)) {
                        this.buttonClicked(button);
                        return;
                    }
                    ++n3;
                }
                if (my2 >= 30 && mx2 <= this.width - 175) {
                    Node<NamedNBT> newFocus = null;
                    for (GuiNBTNode node : this.nodes) {
                        if (!node.clicked(mx2, my2)) continue;
                        newFocus = node.getNode();
                        break;
                    }
                    if (this.focusedSlotIndex != -1) {
                        this.stopEditingSlot();
                    }
                    this.setFocused(newFocus);
                }
            } else {
                this.initGUI();
            }
        } else {
            this.window.click(mx2, my2);
        }
    }

    private void saveButtonClicked(GuiSaveSlotButton button) {
        if (button.save.tag.hasNoTags()) {
            Node<NamedNBT> obj = this.focused == null ? this.tree.getRoot() : this.focused;
            NBTBase base = obj.getObject().getNBT();
            String name = obj.getObject().getName();
            if (base instanceof NBTTagList) {
                NBTTagList list = new NBTTagList();
                this.tree.addChildrenToList(obj, list);
                button.save.tag.setTag(name, list);
            } else if (base instanceof NBTTagCompound) {
                NBTTagCompound compound = new NBTTagCompound();
                this.tree.addChildrenToTag(obj, compound);
                button.save.tag.setTag(name, compound);
            } else {
                button.save.tag.setTag(name, base.copy());
            }
            button.saved();
            NBTEdit.getSaveStates().save();
        } else {
            Map<String, NBTBase> nbtMap = NBTHelper.getMap(button.save.tag);
            if (nbtMap.isEmpty()) {
                NBTEdit.log(Level.WARN, "Unable to copy from save \"" + button.save.name + "\".");
                NBTEdit.log(Level.WARN, "The save is invalid - a valid save must only contain 1 core NBTBase");
            } else {
                if (this.focused == null) {
                    this.setFocused(this.tree.getRoot());
                }
                Map.Entry<String, NBTBase> firstEntry = nbtMap.entrySet().iterator().next();
                assert (firstEntry != null);
                String name = firstEntry.getKey();
                NBTBase nbt = firstEntry.getValue().copy();
                if (this.focused == this.tree.getRoot() && nbt instanceof NBTTagCompound && name.equals("ROOT")) {
                    this.setFocused(null);
                    this.tree = new NBTTree((NBTTagCompound)nbt);
                    this.initGUI();
                } else if (this.canAddToParent(this.focused.getObject().getNBT(), nbt)) {
                    this.focused.setDrawChildren(true);
                    Iterator<Node<NamedNBT>> it2 = this.focused.getChildren().iterator();
                    while (it2.hasNext()) {
                        if (!it2.next().getObject().getName().equals(name)) continue;
                        it2.remove();
                        break;
                    }
                    Node<NamedNBT> node = this.insert(new NamedNBT(name, nbt));
                    this.tree.addChildrenToTree(node);
                    this.tree.sort(node);
                    this.setFocused(node);
                    this.initGUI(true);
                }
            }
        }
    }

    private void buttonClicked(GuiNBTButton button) {
        if (button.getId() == 16) {
            this.paste();
        } else if (button.getId() == 15) {
            this.cut();
        } else if (button.getId() == 14) {
            this.copy();
        } else if (button.getId() == 13) {
            this.deleteSelected();
        } else if (button.getId() == 12) {
            this.edit();
        } else if (this.focused != null) {
            this.focused.setDrawChildren(true);
            List<Node<NamedNBT>> children = this.focused.getChildren();
            String type = NBTStringHelper.getButtonName(button.getId());
            if (this.focused.getObject().getNBT() instanceof NBTTagList) {
                NBTBase nbt = NBTStringHelper.newTag(button.getId());
                if (nbt != null) {
                    Node<NamedNBT> newNode = new Node<NamedNBT>(this.focused, new NamedNBT("", nbt));
                    children.add(newNode);
                    this.setFocused(newNode);
                }
            } else if (children.size() == 0) {
                this.setFocused(this.insert(String.valueOf(type) + "1", button.getId()));
            } else {
                int i2 = 1;
                while (i2 <= children.size() + 1) {
                    String name = String.valueOf(type) + i2;
                    if (this.validName(name, children)) {
                        this.setFocused(this.insert(name, button.getId()));
                        break;
                    }
                    ++i2;
                }
            }
            this.initGUI(true);
        }
    }

    private boolean validName(String name, List<Node<NamedNBT>> list) {
        for (Node<NamedNBT> node : list) {
            if (!node.getObject().getName().equals(name)) continue;
            return false;
        }
        return true;
    }

    private Node<NamedNBT> insert(NamedNBT nbt) {
        Node<NamedNBT> newNode = new Node<NamedNBT>(this.focused, nbt);
        if (this.focused.hasChildren()) {
            List<Node<NamedNBT>> children = this.focused.getChildren();
            boolean added = false;
            int i2 = 0;
            while (i2 < children.size()) {
                if (NBTEdit.SORTER.compare(newNode, children.get(i2)) < 0) {
                    children.add(i2, newNode);
                    added = true;
                    break;
                }
                ++i2;
            }
            if (!added) {
                children.add(newNode);
            }
        } else {
            this.focused.addChild(newNode);
        }
        return newNode;
    }

    private Node<NamedNBT> insert(String name, byte type) {
        NBTBase nbt = NBTStringHelper.newTag(type);
        if (nbt != null) {
            return this.insert(new NamedNBT(name, nbt));
        }
        return null;
    }

    public void deleteSelected() {
        if (this.focused != null && this.tree.delete(this.focused)) {
            Node<NamedNBT> oldFocused = this.focused;
            this.shiftFocus(true);
            if (this.focused == oldFocused) {
                this.setFocused(null);
            }
            this.initGUI();
        }
    }

    public void editSelected() {
        if (this.focused != null) {
            NBTBase base = this.focused.getObject().getNBT();
            if (this.focused.hasChildren() && (base instanceof NBTTagCompound || base instanceof NBTTagList)) {
                int index;
                this.focused.setDrawChildren(!this.focused.shouldDrawChildren());
                if (this.focused.shouldDrawChildren() && (index = this.indexOf(this.focused)) != -1) {
                    this.offset = 31 - this.nodes.get((int)index).y + this.offset;
                }
                this.initGUI();
            } else if (this.buttons[11].isEnabled()) {
                this.edit();
            }
        } else if (this.focusedSlotIndex != -1) {
            this.stopEditingSlot();
        }
    }

    private boolean canAddToParent(NBTBase parent, NBTBase child) {
        if (parent instanceof NBTTagCompound) {
            return true;
        }
        if (parent instanceof NBTTagList) {
            NBTTagList list = (NBTTagList)parent;
            return list.tagCount() == 0 || list.getTagType() == child.getId();
        }
        return false;
    }

    private boolean canPaste() {
        return NBTEdit.clipboard != null && this.focused != null && this.canAddToParent(this.focused.getObject().getNBT(), NBTEdit.clipboard.getNBT());
    }

    private void paste() {
        if (NBTEdit.clipboard != null) {
            this.focused.setDrawChildren(true);
            NamedNBT namedNBT = NBTEdit.clipboard.copy();
            if (this.focused.getObject().getNBT() instanceof NBTTagList) {
                namedNBT.setName("");
                Node<NamedNBT> node = new Node<NamedNBT>(this.focused, namedNBT);
                this.focused.addChild(node);
                this.tree.addChildrenToTree(node);
                this.tree.sort(node);
                this.setFocused(node);
            } else {
                List<Node<NamedNBT>> children;
                String name = namedNBT.getName();
                if (!this.validName(name, children = this.focused.getChildren())) {
                    int i2 = 1;
                    while (i2 <= children.size() + 1) {
                        String n2 = String.valueOf(name) + "(" + i2 + ")";
                        if (this.validName(n2, children)) {
                            namedNBT.setName(n2);
                            break;
                        }
                        ++i2;
                    }
                }
                Node<NamedNBT> node = this.insert(namedNBT);
                this.tree.addChildrenToTree(node);
                this.tree.sort(node);
                this.setFocused(node);
            }
            this.initGUI(true);
        }
    }

    private void copy() {
        if (this.focused != null) {
            NamedNBT namedNBT = this.focused.getObject();
            if (namedNBT.getNBT() instanceof NBTTagList) {
                NBTTagList list = new NBTTagList();
                this.tree.addChildrenToList(this.focused, list);
                NBTEdit.clipboard = new NamedNBT(namedNBT.getName(), list);
            } else if (namedNBT.getNBT() instanceof NBTTagCompound) {
                NBTTagCompound compound = new NBTTagCompound();
                this.tree.addChildrenToTag(this.focused, compound);
                NBTEdit.clipboard = new NamedNBT(namedNBT.getName(), compound);
            } else {
                NBTEdit.clipboard = this.focused.getObject().copy();
            }
            this.setFocused(this.focused);
        }
    }

    private void cut() {
        this.copy();
        this.deleteSelected();
    }

    private void edit() {
        NBTBase base = this.focused.getObject().getNBT();
        NBTBase parent = this.focused.getParent().getObject().getNBT();
        this.window = new GuiEditNBT(this, this.focused, !(parent instanceof NBTTagList), !(base instanceof NBTTagCompound) && !(base instanceof NBTTagList));
        this.window.initGUI((this.width - 178) / 2, (this.height - 93) / 2);
    }

    public void nodeEdited(Node<NamedNBT> node) {
        Node<NamedNBT> parent = node.getParent();
        Collections.sort(parent.getChildren(), NBTEdit.SORTER);
        this.initGUI(true);
    }

    public void arrowKeyPressed(boolean up2) {
        if (this.focused == null) {
            this.shift(up2 ? this.Y_GAP : -this.Y_GAP);
        } else {
            this.shiftFocus(up2);
        }
    }

    private int indexOf(Node<NamedNBT> node) {
        int i2 = 0;
        while (i2 < this.nodes.size()) {
            if (this.nodes.get(i2).getNode() == node) {
                return i2;
            }
            ++i2;
        }
        return -1;
    }

    private void shiftFocus(boolean up2) {
        int index = this.indexOf(this.focused);
        if (index != -1 && (index += up2 ? -1 : 1) >= 0 && index < this.nodes.size()) {
            this.setFocused(this.nodes.get(index).getNode());
            this.shift(up2 ? this.Y_GAP : -this.Y_GAP);
        }
    }

    private void shiftTo(Node<NamedNBT> node) {
        int index = this.indexOf(node);
        if (index != -1) {
            GuiNBTNode gui = this.nodes.get(index);
            this.shift((this.bottom + 30 + 1) / 2 - (gui.y + gui.height));
        }
    }

    public void shift(int i2) {
        if (this.heightDiff <= 0 || this.window != null) {
            return;
        }
        int dif = this.offset + i2;
        if (dif > 0) {
            dif = 0;
        }
        if (dif < -this.heightDiff) {
            dif = -this.heightDiff;
        }
        for (GuiNBTNode node : this.nodes) {
            node.shift(dif - this.offset);
        }
        this.offset = dif;
    }

    public void closeWindow() {
        this.window = null;
    }

    public boolean isEditingSlot() {
        return this.focusedSlotIndex != -1;
    }

    public void stopEditingSlot() {
        this.saves[this.focusedSlotIndex].stopEditing();
        NBTEdit.getSaveStates().save();
        this.focusedSlotIndex = -1;
    }

    public void keyTyped(char ch, int key) {
        if (this.focusedSlotIndex != -1) {
            this.saves[this.focusedSlotIndex].keyTyped(ch, key);
        } else {
            if (key == 46 && GuiControls.isCtrlKeyDown()) {
                this.copy();
            }
            if (key == 47 && GuiControls.isCtrlKeyDown() && this.canPaste()) {
                this.paste();
            }
            if (key == 45 && GuiControls.isCtrlKeyDown()) {
                this.cut();
            }
        }
    }

    public void rightClick(int mx2, int my2) {
        int i2 = 0;
        while (i2 < 7) {
            if (this.saves[i2].inBounds(mx2, my2)) {
                this.setFocused(null);
                if (this.focusedSlotIndex != -1) {
                    if (this.focusedSlotIndex != i2) {
                        this.saves[this.focusedSlotIndex].stopEditing();
                        NBTEdit.getSaveStates().save();
                    } else {
                        return;
                    }
                }
                this.saves[i2].startEditing();
                this.focusedSlotIndex = i2;
                break;
            }
            ++i2;
        }
    }
}

