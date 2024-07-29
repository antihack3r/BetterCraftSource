/*
 * Decompiled with CFR 0.152.
 */
package com.mcf.davidee.nbtedit.nbt;

import com.mcf.davidee.nbtedit.nbt.NamedNBT;
import com.mcf.davidee.nbtedit.nbt.Node;
import java.util.Comparator;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class NBTNodeSorter
implements Comparator<Node<NamedNBT>> {
    @Override
    public int compare(Node<NamedNBT> a2, Node<NamedNBT> b2) {
        NBTBase n1 = a2.getObject().getNBT();
        NBTBase n2 = b2.getObject().getNBT();
        String s1 = a2.getObject().getName();
        String s2 = b2.getObject().getName();
        if (n1 instanceof NBTTagCompound || n1 instanceof NBTTagList) {
            if (n2 instanceof NBTTagCompound || n2 instanceof NBTTagList) {
                int dif = n1.getId() - n2.getId();
                return dif == 0 ? s1.compareTo(s2) : dif;
            }
            return 1;
        }
        if (n2 instanceof NBTTagCompound || n2 instanceof NBTTagList) {
            return -1;
        }
        int dif = n1.getId() - n2.getId();
        return dif == 0 ? s1.compareTo(s2) : dif;
    }
}

