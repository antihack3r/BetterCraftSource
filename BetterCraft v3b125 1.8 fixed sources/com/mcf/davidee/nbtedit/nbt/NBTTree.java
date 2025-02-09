/*
 * Decompiled with CFR 0.152.
 */
package com.mcf.davidee.nbtedit.nbt;

import com.mcf.davidee.nbtedit.NBTEdit;
import com.mcf.davidee.nbtedit.NBTHelper;
import com.mcf.davidee.nbtedit.NBTStringHelper;
import com.mcf.davidee.nbtedit.nbt.NamedNBT;
import com.mcf.davidee.nbtedit.nbt.Node;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class NBTTree {
    private NBTTagCompound baseTag;
    private Node<NamedNBT> root;

    public NBTTree(NBTTagCompound tag) {
        this.baseTag = tag;
        this.construct();
    }

    public Node<NamedNBT> getRoot() {
        return this.root;
    }

    public boolean canDelete(Node<NamedNBT> node) {
        return node != this.root;
    }

    public boolean delete(Node<NamedNBT> node) {
        return node != null && node != this.root && this.deleteNode(node, this.root);
    }

    private boolean deleteNode(Node<NamedNBT> toDelete, Node<NamedNBT> cur) {
        Iterator<Node<NamedNBT>> it2 = cur.getChildren().iterator();
        while (it2.hasNext()) {
            Node<NamedNBT> child = it2.next();
            if (child == toDelete) {
                it2.remove();
                return true;
            }
            boolean flag = this.deleteNode(toDelete, child);
            if (!flag) continue;
            return true;
        }
        return false;
    }

    private void construct() {
        this.root = new Node<NamedNBT>(new NamedNBT("ROOT", this.baseTag.copy()));
        this.root.setDrawChildren(true);
        this.addChildrenToTree(this.root);
        this.sort(this.root);
    }

    public void sort(Node<NamedNBT> node) {
        Collections.sort(node.getChildren(), NBTEdit.SORTER);
        for (Node<NamedNBT> c2 : node.getChildren()) {
            this.sort(c2);
        }
    }

    public void addChildrenToTree(Node<NamedNBT> parent) {
        block3: {
            NBTBase tag;
            block2: {
                tag = parent.getObject().getNBT();
                if (!(tag instanceof NBTTagCompound)) break block2;
                Map<String, NBTBase> map = NBTHelper.getMap((NBTTagCompound)tag);
                for (Map.Entry<String, NBTBase> entry : map.entrySet()) {
                    NBTBase base = entry.getValue();
                    Node<NamedNBT> child = new Node<NamedNBT>(parent, new NamedNBT(entry.getKey(), base));
                    parent.addChild(child);
                    this.addChildrenToTree(child);
                }
                break block3;
            }
            if (!(tag instanceof NBTTagList)) break block3;
            NBTTagList list = (NBTTagList)tag;
            int i2 = 0;
            while (i2 < list.tagCount()) {
                NBTBase base = NBTHelper.getTagAt(list, i2);
                Node<NamedNBT> child = new Node<NamedNBT>(parent, new NamedNBT(base));
                parent.addChild(child);
                this.addChildrenToTree(child);
                ++i2;
            }
        }
    }

    public NBTTagCompound toNBTTagCompound() {
        NBTTagCompound tag = new NBTTagCompound();
        this.addChildrenToTag(this.root, tag);
        return tag;
    }

    public void addChildrenToTag(Node<NamedNBT> parent, NBTTagCompound tag) {
        for (Node<NamedNBT> child : parent.getChildren()) {
            NBTBase base = child.getObject().getNBT();
            String name = child.getObject().getName();
            if (base instanceof NBTTagCompound) {
                NBTTagCompound newTag = new NBTTagCompound();
                this.addChildrenToTag(child, newTag);
                tag.setTag(name, newTag);
                continue;
            }
            if (base instanceof NBTTagList) {
                NBTTagList list = new NBTTagList();
                this.addChildrenToList(child, list);
                tag.setTag(name, list);
                continue;
            }
            tag.setTag(name, base.copy());
        }
    }

    public void addChildrenToList(Node<NamedNBT> parent, NBTTagList list) {
        for (Node<NamedNBT> child : parent.getChildren()) {
            NBTBase base = child.getObject().getNBT();
            if (base instanceof NBTTagCompound) {
                NBTTagCompound newTag = new NBTTagCompound();
                this.addChildrenToTag(child, newTag);
                list.appendTag(newTag);
                continue;
            }
            if (base instanceof NBTTagList) {
                NBTTagList newList = new NBTTagList();
                this.addChildrenToList(child, newList);
                list.appendTag(newList);
                continue;
            }
            list.appendTag(base.copy());
        }
    }

    public void print() {
        this.print(this.root, 0);
    }

    private void print(Node<NamedNBT> n2, int i2) {
        System.out.println(String.valueOf(NBTTree.repeat("\t", i2)) + NBTStringHelper.getNBTName(n2.getObject()));
        for (Node<NamedNBT> child : n2.getChildren()) {
            this.print(child, i2 + 1);
        }
    }

    public List<String> toStrings() {
        ArrayList<String> s2 = new ArrayList<String>();
        this.toStrings(s2, this.root, 0);
        return s2;
    }

    private void toStrings(List<String> s2, Node<NamedNBT> n2, int i2) {
        s2.add(String.valueOf(NBTTree.repeat("   ", i2)) + NBTStringHelper.getNBTName(n2.getObject()));
        for (Node<NamedNBT> child : n2.getChildren()) {
            this.toStrings(s2, child, i2 + 1);
        }
    }

    public static String repeat(String c2, int i2) {
        StringBuilder b2 = new StringBuilder(i2 + 1);
        int j2 = 0;
        while (j2 < i2) {
            b2.append(c2);
            ++j2;
        }
        return b2.toString();
    }
}

